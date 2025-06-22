# 🚀 AWS Cloud-Native Architecture
# Online Bookstore Management System

## 📋 **Executive Summary**

Transform your current Docker Compose microservices into a fully AWS cloud-native application leveraging:
- **Amazon EKS** for container orchestration
- **RDS PostgreSQL** for primary databases (3 separate instances)
- **OpenSearch** for observability, monitoring, and enhanced search
- **SNS/SQS** for event-driven architecture (replacing Kafka)
- **Lambda Functions** for serverless secondary services
- **DynamoDB** for high-performance secondary data storage

## 🏗️ **Architecture Overview**

### **Core Components Transformation**
| Current (Docker Compose) | AWS Cloud-Native | Benefits |
|--------------------------|------------------|----------|
| Docker Containers | EKS Pods | Auto-scaling, managed updates |
| PostgreSQL Container | RDS PostgreSQL (3 instances) | Managed, Multi-AZ, automated backups |
| Kafka + Zookeeper | SNS + SQS | Fully managed, infinite scale |
| Basic Logging | OpenSearch + CloudWatch | Advanced search, analytics, monitoring |
| Manual Scaling | Auto Scaling Groups | Dynamic capacity management |

## 🎯 **Detailed Component Design**

### **1. Amazon EKS Cluster Configuration**

```yaml
# eksctl-config.yaml
apiVersion: eksctl.io/v1alpha5
kind: ClusterConfig
metadata:
  name: bookstore-cluster
  region: us-east-1
  version: "1.28"

vpc:
  cidr: "10.0.0.0/16"
  subnets:
    private:
      us-east-1a: { cidr: "10.0.1.0/24" }
      us-east-1b: { cidr: "10.0.2.0/24" }
      us-east-1c: { cidr: "10.0.3.0/24" }
    public:
      us-east-1a: { cidr: "10.0.101.0/24" }
      us-east-1b: { cidr: "10.0.102.0/24" }
      us-east-1c: { cidr: "10.0.103.0/24" }

nodeGroups:
  - name: bookstore-services
    instanceType: t3.medium
    desiredCapacity: 6
    minSize: 3
    maxSize: 12
    volumeSize: 50
    amiFamily: AmazonLinux2
    labels:
      role: microservices
    tags:
      Environment: production
      Project: bookstore
    iam:
      withAddonPolicies:
        autoScaler: true
        cloudWatch: true
        ebs: true
        efs: true

addons:
  - name: vpc-cni
  - name: coredns
  - name: kube-proxy
  - name: aws-ebs-csi-driver
```

### **2. Event-Driven Architecture with SNS/SQS**

#### **SNS Topic Structure**
```bash
# Core Event Topics
- bookstore-user-events        # User registration, profile updates
- bookstore-inventory-events   # Stock updates, price changes
- bookstore-order-events       # Order lifecycle events
- bookstore-notification-events # Email, SMS notifications
```

#### **SQS Queue Configuration**
```yaml
# user-service-queue.yaml
UserServiceQueue:
  Type: AWS::SQS::Queue
  Properties:
    QueueName: user-service-events
    VisibilityTimeoutSeconds: 300
    MessageRetentionPeriod: 1209600  # 14 days
    DeadLetterQueue:
      TargetArn: !GetAtt UserServiceDLQ.Arn
      MaxReceiveCount: 3

OrderProcessingQueue:
  Type: AWS::SQS::Queue
  Properties:
    QueueName: order-processing
    VisibilityTimeoutSeconds: 900    # 15 minutes for complex processing
    MessageRetentionPeriod: 1209600
    DeadLetterQueue:
      TargetArn: !GetAtt OrderProcessingDLQ.Arn
      MaxReceiveCount: 5

NotificationQueue:
  Type: AWS::SQS::Queue
  Properties:
    QueueName: notification-events
    VisibilityTimeoutSeconds: 60
    MessageRetentionPeriod: 345600   # 4 days
```

### **3. Database Architecture**

#### **RDS PostgreSQL Configuration**
```yaml
# Primary Databases (3 separate RDS instances)
UserDatabase:
  Type: AWS::RDS::DBInstance
  Properties:
    DBInstanceIdentifier: bookstore-users-db
    Engine: postgres
    EngineVersion: "15.4"
    DBInstanceClass: db.t3.medium
    AllocatedStorage: 100
    StorageType: gp3
    MultiAZ: true
    BackupRetentionPeriod: 7
    DeletionProtection: true
    DBSubnetGroupName: !Ref DatabaseSubnetGroup
    VPCSecurityGroups:
      - !Ref DatabaseSecurityGroup

BookInventoryDatabase:
  Type: AWS::RDS::DBInstance
  Properties:
    DBInstanceIdentifier: bookstore-inventory-db
    Engine: postgres
    EngineVersion: "15.4"
    DBInstanceClass: db.r5.large     # More memory for search operations
    AllocatedStorage: 200
    StorageType: gp3
    MultiAZ: true
    BackupRetentionPeriod: 30        # Longer retention for business data
    ReadReplicaCount: 2              # Read replicas for scaling
    DeletionProtection: true

OrderDatabase:
  Type: AWS::RDS::DBInstance
  Properties:
    DBInstanceIdentifier: bookstore-orders-db
    Engine: postgres
    EngineVersion: "15.4"
    DBInstanceClass: db.t3.medium
    AllocatedStorage: 150
    StorageType: gp3
    MultiAZ: true
    BackupRetentionPeriod: 14
    DeletionProtection: true
```

#### **DynamoDB Tables for Secondary Services**
```yaml
# Session Management Table
UserSessionsTable:
  Type: AWS::DynamoDB::Table
  Properties:
    TableName: bookstore-user-sessions
    BillingMode: PAY_PER_REQUEST
    AttributeDefinitions:
      - AttributeName: sessionId
        AttributeType: S
    KeySchema:
      - AttributeName: sessionId
        KeyType: HASH
    TimeToLiveSpecification:
      AttributeName: expiresAt
      Enabled: true
    StreamSpecification:
      StreamViewType: NEW_AND_OLD_IMAGES

# Book Search Cache Table
BookSearchCacheTable:
  Type: AWS::DynamoDB::Table
  Properties:
    TableName: bookstore-search-cache
    BillingMode: PAY_PER_REQUEST
    AttributeDefinitions:
      - AttributeName: searchQuery
        AttributeType: S
    KeySchema:
      - AttributeName: searchQuery
        KeyType: HASH
    TimeToLiveSpecification:
      AttributeName: expiresAt
      Enabled: true

# Audit Logs Table
AuditLogsTable:
  Type: AWS::DynamoDB::Table
  Properties:
    TableName: bookstore-audit-logs
    BillingMode: PAY_PER_REQUEST
    AttributeDefinitions:
      - AttributeName: eventId
        AttributeType: S
      - AttributeName: timestamp
        AttributeType: S
      - AttributeName: userId
        AttributeType: S
    KeySchema:
      - AttributeName: eventId
        KeyType: HASH
      - AttributeName: timestamp
        KeyType: RANGE
    GlobalSecondaryIndexes:
      - IndexName: UserIdIndex
        KeySchema:
          - AttributeName: userId
            KeyType: HASH
          - AttributeName: timestamp
            KeyType: RANGE
        Projection:
          ProjectionType: ALL
```

### **4. Lambda Functions for Secondary Services**

#### **Notification Lambda**
```kotlin
// NotificationLambda.kt
@Component
class NotificationHandler : RequestHandler<SQSEvent, String> {
    
    @Autowired
    private lateinit var sesClient: SesV2Client
    
    @Autowired
    private lateinit var snsClient: SnsClient
    
    override fun handleRequest(event: SQSEvent, context: Context): String {
        val logger = LoggerFactory.getLogger(NotificationHandler::class.java)
        
        event.records.forEach { record ->
            try {
                val notificationEvent = objectMapper.readValue(record.body, NotificationEvent::class.java)
                
                when (notificationEvent.type) {
                    "ORDER_CONFIRMATION" -> sendOrderConfirmationEmail(notificationEvent)
                    "SHIPPING_UPDATE" -> sendShippingNotification(notificationEvent)
                    "LOW_STOCK_ALERT" -> sendLowStockAlert(notificationEvent)
                    "WELCOME_EMAIL" -> sendWelcomeEmail(notificationEvent)
                }
                
                logger.info("Processed notification: ${notificationEvent.id}")
            } catch (e: Exception) {
                logger.error("Failed to process notification", e)
                throw e  // Will send to DLQ after retry exhaustion
            }
        }
        
        return "SUCCESS"
    }
    
    private fun sendOrderConfirmationEmail(event: NotificationEvent) {
        val emailRequest = SendEmailRequest.builder()
            .destination(Destination.builder().toAddresses(event.recipient).build())
            .content(EmailContent.builder()
                .simple(Message.builder()
                    .subject(Content.builder().data("Order Confirmation #${event.orderId}").build())
                    .body(Body.builder()
                        .text(Content.builder().data(generateOrderConfirmationText(event)).build())
                        .html(Content.builder().data(generateOrderConfirmationHtml(event)).build())
                        .build())
                    .build())
                .build())
            .fromEmailAddress("orders@bookstore.com")
            .build()
            
        sesClient.sendEmail(emailRequest)
    }
}
```

#### **Analytics Lambda**
```kotlin
// AnalyticsLambda.kt
@Component
class AnalyticsHandler : RequestHandler<SQSEvent, String> {
    
    @Autowired
    private lateinit var dynamoDbClient: DynamoDbClient
    
    @Autowired
    private lateinit var openSearchClient: OpenSearchClient
    
    override fun handleRequest(event: SQSEvent, context: Context): String {
        event.records.forEach { record ->
            val analyticsEvent = objectMapper.readValue(record.body, AnalyticsEvent::class.java)
            
            // Store in DynamoDB for fast queries
            storeAnalyticsEvent(analyticsEvent)
            
            // Index in OpenSearch for complex analytics
            indexInOpenSearch(analyticsEvent)
            
            // Update real-time metrics
            updateMetrics(analyticsEvent)
        }
        
        return "SUCCESS"
    }
    
    private fun storeAnalyticsEvent(event: AnalyticsEvent) {
        val item = mapOf(
            "eventId" to AttributeValue.builder().s(event.id).build(),
            "timestamp" to AttributeValue.builder().s(event.timestamp.toString()).build(),
            "eventType" to AttributeValue.builder().s(event.type).build(),
            "userId" to AttributeValue.builder().s(event.userId).build(),
            "data" to AttributeValue.builder().s(objectMapper.writeValueAsString(event.data)).build()
        )
        
        dynamoDbClient.putItem(PutItemRequest.builder()
            .tableName("bookstore-analytics-events")
            .item(item)
            .build())
    }
}
```

### **5. OpenSearch Configuration**

#### **Domain Setup**
```yaml
OpenSearchDomain:
  Type: AWS::OpenSearch::Domain
  Properties:
    DomainName: bookstore-search
    EngineVersion: OpenSearch_2.3
    ClusterConfig:
      InstanceType: t3.small.search
      InstanceCount: 3
      DedicatedMasterEnabled: true
      MasterInstanceType: t3.small.search
      MasterInstanceCount: 3
    EBSOptions:
      EBSEnabled: true
      VolumeType: gp3
      VolumeSize: 20
    VPCOptions:
      SubnetIds:
        - !Ref PrivateSubnet1
        - !Ref PrivateSubnet2
        - !Ref PrivateSubnet3
      SecurityGroupIds:
        - !Ref OpenSearchSecurityGroup
    AccessPolicies:
      Version: '2012-10-17'
      Statement:
        - Effect: Allow
          Principal:
            AWS: !Sub '${AWS::AccountId}'
          Action: 'es:*'
          Resource: !Sub 'arn:aws:es:${AWS::Region}:${AWS::AccountId}:domain/bookstore-search/*'
```

#### **Index Templates**
```json
# Book Search Index Template
{
  "index_patterns": ["books-*"],
  "template": {
    "settings": {
      "number_of_shards": 2,
      "number_of_replicas": 1,
      "analysis": {
        "analyzer": {
          "book_analyzer": {
            "type": "custom",
            "tokenizer": "standard",
            "filter": ["lowercase", "stop", "snowball"]
          }
        }
      }
    },
    "mappings": {
      "properties": {
        "id": {"type": "keyword"},
        "title": {
          "type": "text",
          "analyzer": "book_analyzer",
          "fields": {
            "keyword": {"type": "keyword"}
          }
        },
        "author": {
          "type": "text",
          "analyzer": "book_analyzer"
        },
        "description": {
          "type": "text",
          "analyzer": "book_analyzer"
        },
        "category": {"type": "keyword"},
        "price": {"type": "double"},
        "stockQuantity": {"type": "integer"},
        "publishedDate": {"type": "date"},
        "createdAt": {"type": "date"},
        "updatedAt": {"type": "date"}
      }
    }
  }
}
```

## 🔧 **Implementation Roadmap**

### **Phase 1: Infrastructure Setup (Week 1-2)**
1. Create VPC and networking components
2. Deploy EKS cluster with eksctl
3. Setup RDS PostgreSQL instances (3 databases)
4. Configure SNS topics and SQS queues
5. Create DynamoDB tables

### **Phase 2: Application Migration (Week 3-4)**
1. Update Spring Boot applications for AWS services
2. Replace Kafka with SNS/SQS integration
3. Deploy microservices to EKS
4. Configure Application Load Balancer
5. Setup AWS Secrets Manager for credentials

### **Phase 3: Observability & Lambda (Week 5-6)**
1. Deploy OpenSearch domain
2. Configure Fluent Bit for log collection
3. Create and deploy Lambda functions
4. Setup CloudWatch dashboards
5. Configure X-Ray distributed tracing

### **Phase 4: Optimization & Security (Week 7-8)**
1. Implement auto-scaling policies
2. Setup AWS WAF for security
3. Configure backup and disaster recovery
4. Performance testing and optimization
5. Security hardening and compliance

## 💰 **Cost Estimation (Monthly)**

| Service | Configuration | Estimated Cost |
|---------|---------------|----------------|
| EKS Cluster | Control Plane | $73 |
| EC2 Instances | 6 × t3.medium | $180 |
| RDS PostgreSQL | 3 × db.t3.medium Multi-AZ | $450 |
| DynamoDB | Pay-per-request | $50 |
| OpenSearch | 3 × t3.small.search | $180 |
| Lambda | 1M invocations/month | $20 |
| SNS/SQS | Standard usage | $30 |
| Data Transfer | Regional | $50 |
| **Total Estimated** | | **~$1,033/month** |

## 🎯 **Next Steps**

1. **Review and approve this architecture design**
2. **Setup AWS account and IAM permissions**
3. **Begin Phase 1 infrastructure deployment**
4. **Plan application code modifications**
5. **Setup CI/CD pipeline for automated deployments**

This AWS cloud-native transformation will provide you with enterprise-grade scalability, reliability, and observability while maintaining the core functionality of your current system. 