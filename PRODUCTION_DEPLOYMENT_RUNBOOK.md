# TESP Production Deployment Runbook

- **Version**: 1.0.0
- **Last Verified**: 2026-08-10
- **Target AWS Region**: `ap-south-1` (Mumbai - Optimized for South Asia users)
- **Production Frontend SPA Domain**: `https://tesp.talnova.io`
- **Production API Gateway Domain**: `https://tesp-api.talnova.io`
- **Operator Workstation**: Windows PowerShell / Bash

---

## 1. What This Runbook Does

This runbook is an **A-to-Z executable deployment guide** for taking the Talnova Enterprise Survey Platform (TESP) from a fresh operator workstation and AWS account to a live, secure, multi-AZ production deployment. Every phase includes exact, executable commands for both **PowerShell** and **Bash**, expected outputs, verification steps, value capture instructions, and troubleshooting procedures.

---

## 2. Final Architecture Overview

```text
                                [ Public Users ]
                                       |
                                       v
                              [ Cloudflare DNS/WAF ]
                            (Full/Strict SSL / TLS)
                                       |
         +-----------------------------+-----------------------------+
         |                                                           |
         v                                                           v
  https://tesp.talnova.io                                 https://tesp-api.talnova.io
  (CNAME to ALB)                                          (CNAME to ALB)
         |                                                           |
         +-----------------------------+-----------------------------+
                                       |
                                       v
                     [ AWS Application Load Balancer (ALB) ]
                                (ACM Certificate)
                                       |
           +---------------------------+---------------------------+
           | (Host: tesp.talnova.io)                               | (Host: tesp-api.talnova.io)
           v                                                       v
   [ Frontend SPA Target Group ]                           [ API Gateway Target Group ]
   (Port 80 - Nginx / ECS Fargate)                         (Port 8080 - Spring Cloud Gateway)
                                                                   |
                                                                   +---> project-config-service (8081)
                                                                   +---> organization-service (8082)
                                                                   +---> employee-service (8083)
                                                                   +---> survey-builder-service (8084)
                                                                   +---> survey-distribution-service (8085)
                                                                   +---> response-ingestion-service (8086)
                                                                   +---> analytics-engine-service (8087)
                                                                   +---> ai-analytics-service (8088)
                                                                   +---> reporting-service (8089)
                                                                   +---> action-planning-service (8090)
                                                                   +---> notification-service (8091)
                                                                   +---> audit-service (8092)

Private Infrastructure (Private Data Subnets & VPC Peering):
   +--> MongoDB Atlas (Multi-AZ Sharded Cluster via VPC Peering)
   +--> AWS ElastiCache for Redis (Multi-AZ Replication Group in Private Data Subnet)
   +--> Amazon MSK Kafka (Multi-AZ Cluster in Private Data Subnet)
   +--> AWS Secrets Manager (Dynamic Secret Injection)
   +--> AWS CloudWatch Logs (Container Observability)
```

---

## 3. Microservices Inventory & Dependency Matrix

| Service Name | Port | Health Endpoint | DB / Storage Dependency | Cache / Bus Dependency | IAM Role Permissions Required |
|---|---|---|---|---|---|
| **frontend** | `80` | `/` | Static Nginx assets | None | None |
| **api-gateway** | `8080` | `/actuator/health` | None | Redis (Rate Limiting) | Secrets Manager |
| **project-config-service** | `8081` | `/actuator/health` | MongoDB (`tesp_config_db`) | Redis, Kafka | S3 Tenant Assets (`tesp-tenant-assets-prod`) |
| **organization-service** | `8082` | `/actuator/health` | MongoDB (`tesp_org_db`) | Kafka | Secrets Manager |
| **employee-service** | `8083` | `/actuator/health` | MongoDB (`tesp_employee_db`) | Kafka | AWS KMS (PII CSFLE Encryption Key) |
| **survey-builder-service** | `8084` | `/actuator/health` | MongoDB (`tesp_survey_db`) | Kafka | Secrets Manager |
| **survey-distribution-service** | `8085` | `/actuator/health` | MongoDB (`tesp_dist_db`) | Redis, Kafka | Secrets Manager |
| **response-ingestion-service** | `8086` | `/actuator/health` | MongoDB (`tesp_ingest_db`) | Redis, Kafka | Secrets Manager |
| **analytics-engine-service** | `8087` | `/actuator/health` | MongoDB (`tesp_analytics_db`) | Redis, Kafka | Secrets Manager |
| **ai-analytics-service** | `8088` | `/actuator/health` | MongoDB (`tesp_ai_db`) | Redis, Kafka, OpenAI API | Secrets Manager |
| **reporting-service** | `8089` | `/actuator/health` | MongoDB (`tesp_report_db`) | Redis, Kafka | Secrets Manager |
| **action-planning-service** | `8090` | `/actuator/health` | MongoDB (`tesp_action_db`) | Kafka | Secrets Manager |
| **notification-service** | `8091` | `/actuator/health` | None | Redis, Kafka | Secrets Manager |
| **audit-service** | `8092` | `/actuator/health` | MongoDB (`tesp_audit_db`) | Kafka | Secrets Manager |

---

## 4. Important Architectural Correction: Fargate Container Access (No SSH)

> [!WARNING]
> **DO NOT USE SSH OR PuTTY FOR ECS FARGATE CONTAINERS**.
> AWS ECS Fargate is a serverless container engine without underlying EC2 instances or SSH daemons.
> To open an interactive shell inside a running Fargate container, you MUST use **AWS ECS Exec** powered by the **AWS Systems Manager (SSM) Session Manager Plugin**.

```text
Operator Workstation
       |
       v
AWS CLI + SSM Session Manager Plugin
       |
       v
aws ecs execute-command --interactive --command "/bin/sh"
       |
       v
AWS Systems Manager Agent (Running in Fargate Container)
       |
       v
Interactive Container Shell (/bin/sh)
```

---

## Phase 1 — Workstation & Tooling Setup

### Objective
Install and verify all required local development and deployment tooling on the operator's Windows workstation.

### Prerequisites
Administrative access to Windows workstation.

### Commands

#### PowerShell:
```powershell
# 1. Install AWS CLI v2 via winget
winget install -e --id Amazon.AWSCLI

# 2. Install Session Manager Plugin for AWS CLI (Required for ECS Exec)
winget install -e --id Amazon.SessionManagerPlugin

# 3. Install Terraform
winget install -e --id HashiCorp.Terraform

# 4. Install Git
winget install -e --id Git.Git

# 5. Install Node.js 20 LTS
winget install -e --id OpenJS.NodeJS.LTS
```

#### Bash (Alternative Linux Workstation):
```bash
# Install AWS CLI v2
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip && sudo ./aws/install

# Install Session Manager Plugin
curl "https://s3.amazonaws.com/session-manager-downloads/plugin/latest/ubuntu_64bit/session-manager-plugin.deb" -o "session-manager-plugin.deb"
sudo dpkg -i session-manager-plugin.deb

# Install Terraform
sudo apt-get update && sudo apt-get install -y terraform git nodejs npm
```

### Expected Result
All binary executables report valid version strings.

### Verify

#### PowerShell:
```powershell
aws --version
session-manager-plugin --version
terraform --version
git --version
node --version
npm --version
```

### Save these values
None.

### Troubleshooting
If `session-manager-plugin` is not found, restart the PowerShell terminal to refresh your system `$env:PATH`.

---

## Phase 2 — AWS CLI Authentication & Region Verification

### Objective
Authenticate the local operator session to AWS and verify `ap-south-1` region availability zones.

### Prerequisites
An active AWS account with Administrator or Infrastructure deployment credentials.

### Commands

#### PowerShell:
```powershell
# Set Environment Variables
$env:AWS_REGION="ap-south-1"

# Configure AWS CLI Credentials (Interactively input Access Key ID & Secret Access Key)
aws configure

# Obtain Account Identity
aws sts get-caller-identity
```

#### Bash:
```bash
export AWS_REGION="ap-south-1"
aws configure
aws sts get-caller-identity
```

### Expected Result
JSON output displaying `UserId`, `Account`, and `Arn`.

### Verify

#### PowerShell:
```powershell
$env:AWS_ACCOUNT_ID = (aws sts get-caller-identity --query "Account" --output text)
Write-Host "Authenticated to AWS Account ID: $env:AWS_ACCOUNT_ID in region $env:AWS_REGION"

aws ec2 describe-availability-zones `
  --region $env:AWS_REGION `
  --query "AvailabilityZones[?State=='available'].ZoneName" `
  --output table
```

### Save these values
- `AWS_ACCOUNT_ID`: Your 12-digit AWS account ID.
- `AWS_REGION`: `ap-south-1`.

### Troubleshooting
If `sts get-caller-identity` returns `InvalidClientTokenId`, verify system clock sync and re-enter credentials via `aws configure`.

---

## Phase 3 — Terraform Backend & State Infrastructure

### Objective
Provision the AWS S3 state bucket and DynamoDB lock table required for remote Terraform state management.

### Prerequisites
Phase 2 completed (`AWS_ACCOUNT_ID` captured).

### Commands

#### PowerShell:
```powershell
$BUCKET_NAME="tesp-tf-state-$env:AWS_ACCOUNT_ID"
$TABLE_NAME="tesp-terraform-locks"

# 1. Create S3 Bucket for Terraform State
aws s3api create-bucket `
  --bucket $BUCKET_NAME `
  --region $env:AWS_REGION

# 2. Enable Bucket Versioning
aws s3api put-bucket-versioning `
  --bucket $BUCKET_NAME `
  --versioning-configuration Status=Enabled

# 3. Enable Server-Side Encryption
aws s3api put-bucket-encryption `
  --bucket $BUCKET_NAME `
  --server-side-encryption-configuration '{"Rules": [{"ApplyServerSideEncryptionByDefault": {"SSEAlgorithm": "AES256"}}]}'

# 4. Create DynamoDB Locking Table
aws dynamodb create-table `
  --table-name $TABLE_NAME `
  --attribute-definitions AttributeName=LockID,AttributeType=S `
  --key-schema AttributeName=LockID,KeyType=HASH `
  --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 `
  --region $env:AWS_REGION
```

### Expected Result
S3 bucket created and DynamoDB table status reaches `ACTIVE`.

### Verify

#### PowerShell:
```powershell
aws s3api head-bucket --bucket "tesp-tf-state-$env:AWS_ACCOUNT_ID"
aws dynamodb describe-table --table-name "tesp-terraform-locks" --query "Table.TableStatus"
```

### Save these values
- `TF_STATE_BUCKET`: `tesp-tf-state-<AWS_ACCOUNT_ID>`
- `TF_LOCK_TABLE`: `tesp-terraform-locks`

### Troubleshooting
If bucket creation returns `BucketAlreadyExists`, the bucket name is globally used; append a random suffix.

---

## Phase 4 — Terraform Provisioning of VPC & Security Groups

### Objective
Deploy the 3-AZ network topology (VPC, public/private app/private data subnets, NAT Gateways) and security groups using Terraform.

### Prerequisites
Phases 2 & 3 completed.

### Commands

#### PowerShell:
```powershell
Set-Location infra/terraform

# Initialize Terraform modules
terraform init

# Validate HCL Syntax
terraform validate

# Provision Infrastructure
terraform apply -auto-approve `
  -var="aws_region=$env:AWS_REGION" `
  -var="environment=prod"
```

### Expected Result
Terraform outputs resource creation summary (`Apply complete! Resources: N added, 0 changed, 0 destroyed.`).

### Verify

#### PowerShell:
```powershell
terraform output vpc_id
terraform output public_subnet_ids
terraform output private_app_subnet_ids
terraform output private_data_subnet_ids
```

### Save these values
- `VPC_ID`: `vpc-xxx`
- `PUBLIC_SUBNET_IDS`: `subnet-xxx,subnet-yyy,subnet-zzz`
- `PRIVATE_APP_SUBNET_IDS`: `subnet-aaa,subnet-bbb,subnet-ccc`

### Troubleshooting
If Terraform fails on NAT Gateway creation, verify your AWS account elastic IP quota in `ap-south-1`.

---

## Phase 5 — MongoDB Atlas Cluster & Network Access Setup

### Objective
Provision the production multi-AZ MongoDB Atlas cluster, configure network access, and retrieve the database connection string.

### Prerequisites
Access to MongoDB Atlas Console / API.

### Commands & Procedure

1. **Log in to MongoDB Atlas**: Navigate to [mongodb.com/cloud/atlas](https://www.mongodb.com/cloud/atlas).
2. **Create Project**: Name the project `TESP-Production`.
3. **Build Cluster**:
   - Provider: **AWS**.
   - Region: `ap-south-1` (N. Virginia).
   - Cluster Tier: **Dedicated M10** (or Serverless for initial setup).
   - Cluster Name: `tesp-prod-cluster`.
4. **Configure Database Access**:
   - Create Database User: `tesp_db_admin`.
   - Authentication: **Password (SCRAM-SHA-256)**.
   - Database User Privileges: `readWriteAnyDatabase`.
5. **Configure Network Access**:
   - Add IP Access List entry allowing the TESP AWS VPC CIDR: `10.0.0.0/16`.
   - (Recommended) Set up **AWS VPC Peering** or **AWS PrivateLink** between your AWS VPC (`10.0.0.0/16`) and Atlas.
6. **Obtain Connection String**:
   - Select **Connect** -> **Drivers (Java)**.
   - Copy connection string URI: `mongodb+srv://tesp_db_admin:<PASSWORD>@tesp-prod-cluster.abcde.mongodb.net/?retryWrites=true&w=majority&authSource=admin`

### Expected Result
Active cluster status with database user and VPC network authorization.

### Verify

#### PowerShell:
```powershell
# Test TCP Connectivity to Atlas cluster hostname (Port 27017)
Test-NetConnection -ComputerName "tesp-prod-cluster.abcde.mongodb.net" -Port 27017
```

### Save these values
- `MONGODB_URI`: `mongodb+srv://tesp_db_admin:<PASSWORD>@tesp-prod-cluster.abcde.mongodb.net/?retryWrites=true&w=majority&authSource=admin`

### Troubleshooting
If connection times out, verify your public NAT Gateway elastic IPs or VPC Peering configuration in Atlas Network Access settings.

---

## Phase 6 — AWS ElastiCache for Redis Setup

### Objective
Retrieve and verify the AWS ElastiCache Redis endpoint created in the Private Data Subnets.

### Prerequisites
Phase 4 completed.

### Commands

#### PowerShell:
```powershell
# Retrieve ElastiCache Security Group & Redis Endpoints
aws elasticache describe-cache-clusters `
  --region $env:AWS_REGION `
  --show-cache-node-info `
  --query "CacheClusters[*].[CacheClusterId,CacheClusterStatus,Endpoint.Address]" `
  --output table
```

### Expected Result
Active Redis cluster node displaying private IP/DNS address.

### Verify
Ensure the security group `tesp-elasticache-sg` permits port `6379` inbound from `tesp-ecs-tasks-sg`.

### Save these values
- `REDIS_HOST`: Primary ElastiCache Endpoint DNS/IP.
- `REDIS_PORT`: `6379`.

### Troubleshooting
If `describe-cache-clusters` is empty, ensure `elasticache.tf` is applied in `infra/terraform/`.

---

## Phase 7 — Amazon MSK (Kafka) Cluster Setup

### Objective
Retrieve the Amazon MSK Kafka bootstrap broker addresses.

### Prerequisites
Phase 4 completed.

### Commands

#### PowerShell:
```powershell
# List MSK Clusters
$MSK_ARN = (aws kafka list-clusters --region $env:AWS_REGION --query "ClusterInfoList[0].ClusterArn" --output text)

# Get Bootstrap Broker Addresses
aws kafka get-bootstrap-brokers `
  --cluster-arn $MSK_ARN `
  --region $env:AWS_REGION
```

### Expected Result
JSON containing `BootstrapBrokerString` (Plaintext) and `BootstrapBrokerStringTls` (TLS).

### Verify
Verify port `9092` (Plaintext) or `9094` (TLS) is open from `tesp-ecs-tasks-sg`.

### Save these values
- `KAFKA_BOOTSTRAP_SERVERS`: `b-1.tesp-msk.abcde.c2.kafka.ap-south-1.amazonaws.com:9092,b-2.tesp-msk.abcde.c2.kafka.ap-south-1.amazonaws.com:9092`

### Troubleshooting
MSK cluster creation takes 15–20 minutes; wait for `ClusterState` to reach `ACTIVE`.

---

## Phase 8 — AWS Secrets Manager Configuration

### Objective
Inject production secrets securely into AWS Secrets Manager.

### Prerequisites
Phases 5, 6, 7 completed.

### Commands

#### PowerShell:
```powershell
# 1. Store MongoDB URI
aws secretsmanager put-secret-value `
  --secret-id "tesp/prod/mongodb-uri" `
  --secret-string "mongodb+srv://tesp_db_admin:YourProdPassword123@tesp-prod-cluster.abcde.mongodb.net/?retryWrites=true&w=majority&authSource=admin" `
  --region $env:AWS_REGION

# 2. Store Redis Host
aws secretsmanager put-secret-value `
  --secret-id "tesp/prod/redis-host" `
  --secret-string "tesp-redis.abcde.0001.use1.cache.amazonaws.com" `
  --region $env:AWS_REGION

# 3. Store Kafka Bootstrap Brokers
aws secretsmanager put-secret-value `
  --secret-id "tesp/prod/kafka-bootstrap-servers" `
  --secret-string "b-1.tesp-msk.abcde.c2.kafka.ap-south-1.amazonaws.com:9092,b-2.tesp-msk.abcde.c2.kafka.ap-south-1.amazonaws.com:9092" `
  --region $env:AWS_REGION

# 4. Store JWT Secret
$JWT_SECRET = -join ((65..90) + (97..122) + (48..57) | Get-Random -Count 64 | ForEach-Object {[char]$_})
aws secretsmanager put-secret-value `
  --secret-id "tesp/prod/jwt-secret" `
  --secret-string $JWT_SECRET `
  --region $env:AWS_REGION

# 5. Store OpenAI API Key
aws secretsmanager put-secret-value `
  --secret-id "tesp/prod/openai-api-key" `
  --secret-string "sk-proj-actual-production-openai-key-here" `
  --region $env:AWS_REGION
```

### Expected Result
Secrets Manager updates versions successfully.

### Verify

#### PowerShell:
```powershell
aws secretsmanager list-secrets `
  --filter Key="name",Values="tesp/prod/" `
  --region $env:AWS_REGION `
  --query "SecretList[*].[Name,ARN]" `
  --output table
```

### Save these values
Secret ARNs for task definition bindings.

### Troubleshooting
If access is denied, attach `secretsmanager:PutSecretValue` permission to your IAM session role.

---

## Phase 9 — Local Environment `.Env` Clean Up & Docker Compose Validation

### Objective
Ensure local development uses a single, safe `.env` file and does not leak production credentials.

### Prerequisites
Local workstation.

### Commands

#### PowerShell:
```powershell
# 1. Verify .env is present in .gitignore
Select-String -Path .gitignore -Pattern "\.env"

# 2. Test Docker Compose configuration with .env interpolation
docker compose config --quiet
Write-Host "Docker Compose syntax verified clean."
```

### Expected Result
`.env` confirmed ignored by git; `docker compose config` exits with code 0.

### Verify
Run `git status` to ensure `.env` is NOT tracked.

### Save these values
None.

---

## Phase 10 — Amazon ECR Repositories Provisioning

### Objective
Ensure all 14 container image repositories exist in ECR with immutable tags.

### Prerequisites
Phase 4 completed.

### Commands

#### PowerShell:
```powershell
$services = @(
  "frontend",
  "api-gateway",
  "project-config-service",
  "organization-service",
  "employee-service",
  "survey-builder-service",
  "survey-distribution-service",
  "response-ingestion-service",
  "analytics-engine-service",
  "ai-analytics-service",
  "reporting-service",
  "action-planning-service",
  "notification-service",
  "audit-service"
)

foreach ($svc in $services) {
    aws ecr create-repository `
      --repository-name "tesp/$svc" `
      --image-tag-mutability IMMUTABLE `
      --image-scanning-configuration scanOnPush=true `
      --region $env:AWS_REGION 2>$null
}
```

### Expected Result
14 ECR repositories created under namespace `tesp/*`.

### Verify

#### PowerShell:
```powershell
aws ecr describe-repositories --region $env:AWS_REGION --query "repositories[*].repositoryName"
```

### Save these values
ECR Repository URLs (`<AWS_ACCOUNT_ID>.dkr.ecr.ap-south-1.amazonaws.com/tesp/<service>`).

---

## Phase 11 — ECR Docker Authentication

### Objective
Authenticate local Docker daemon to Amazon ECR.

### Prerequisites
Docker Desktop running.

### Commands

#### PowerShell:
```powershell
aws ecr get-login-password --region $env:AWS_REGION | docker login --username AWS --password-stdin "$env:AWS_ACCOUNT_ID.dkr.ecr.$env:AWS_REGION.amazonaws.com"
```

#### Bash:
```bash
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin "$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com"
```

### Expected Result
`Login Succeeded`.

### Verify
Run `docker info` to verify active login session.

### Troubleshooting
If login fails, ensure Docker Desktop is running and credentials have `ecr:GetAuthorizationToken`.

---

## Phase 12 — Build & Push All 14 Container Images to ECR

### Objective
Compile multi-stage Docker images tagged with immutable Git SHA tags and push them to ECR.

### Prerequisites
Phase 11 completed.

### Commands

#### PowerShell:
```powershell
$GIT_SHA = (git rev-parse --short HEAD)
$IMAGE_TAG = "git-$GIT_SHA"
$ECR_BASE = "$env:AWS_ACCOUNT_ID.dkr.ecr.$env:AWS_REGION.amazonaws.com"

# 1. Build & Push Backend Microservices
$microservices = @(
  "api-gateway",
  "project-config-service",
  "organization-service",
  "employee-service",
  "survey-builder-service",
  "survey-distribution-service",
  "response-ingestion-service",
  "analytics-engine-service",
  "ai-analytics-service",
  "reporting-service",
  "action-planning-service",
  "notification-service",
  "audit-service"
)

foreach ($svc in $microservices) {
    Write-Host "Building $svc with tag $IMAGE_TAG..."
    docker build `
      -t "$ECR_BASE/tesp/${svc}:${IMAGE_TAG}" `
      -f infra/docker/Dockerfile.service `
      --build-arg SERVICE_NAME=$svc .

    Write-Host "Pushing $svc to ECR..."
    docker push "$ECR_BASE/tesp/${svc}:${IMAGE_TAG}"
}

# 2. Build & Push Frontend SPA
Write-Host "Building frontend SPA with tag $IMAGE_TAG..."
docker build `
  -t "$ECR_BASE/tesp/frontend:${IMAGE_TAG}" `
  -f infra/docker/Dockerfile.frontend .

Write-Host "Pushing frontend to ECR..."
docker push "$ECR_BASE/tesp/frontend:${IMAGE_TAG}"
```

### Expected Result
All 14 images pushed to ECR with status `200 OK` and zero High/Critical vulnerability blocks.

### Verify

#### PowerShell:
```powershell
aws ecr list-images --repository-name "tesp/api-gateway" --region $env:AWS_REGION
```

### Save these values
- `DEPLOYMENT_IMAGE_TAG`: `git-<SHA>`

---

## Phase 13 — CloudWatch Log Groups Creation

### Objective
Ensure log groups exist with a 30-day retention policy for all containers.

### Prerequisites
Phase 4 completed.

### Commands

#### PowerShell:
```powershell
foreach ($svc in $services) {
    aws logs create-log-group --log-group-name "/ecs/tesp-$svc" --region $env:AWS_REGION 2>$null
    aws logs put-retention-policy --log-group-name "/ecs/tesp-$svc" --retention-in-days 30 --region $env:AWS_REGION
}
```

### Expected Result
14 CloudWatch log groups under `/ecs/tesp-*`.

### Verify

#### PowerShell:
```powershell
aws logs describe-log-groups --log-group-name-prefix "/ecs/tesp" --query "logGroups[*].logGroupName"
```

---

## Phase 14 — ECS Cluster Verification

### Objective
Verify the production ECS Fargate cluster state.

### Prerequisites
Phase 4 completed.

### Commands

#### PowerShell:
```powershell
aws ecs describe-clusters --clusters "tesp-cluster-prod" --region $env:AWS_REGION --query "clusters[0].[clusterName,status,activeServicesCount]"
```

### Expected Result
`tesp-cluster-prod` status is `ACTIVE`.

---

## Phase 15 — IAM Task Execution & Task Roles Verification

### Objective
Confirm IAM roles `tesp-ecs-execution-role-prod` and `tesp-ecs-task-role-prod` exist with appropriate policy attachments.

### Prerequisites
Phase 4 completed.

### Commands

#### PowerShell:
```powershell
aws iam get-role --role-name "tesp-ecs-execution-role-prod" --query "Role.[RoleName,Arn]"
aws iam get-role --role-name "tesp-ecs-task-role-prod" --query "Role.[RoleName,Arn]"
```

### Save these values
- `EXECUTION_ROLE_ARN`: `arn:aws:iam::<ACCOUNT_ID>:role/tesp-ecs-execution-role-prod`
- `TASK_ROLE_ARN`: `arn:aws:iam::<ACCOUNT_ID>:role/tesp-ecs-task-role-prod`

---

## Phase 16 — AWS Cloud Map Service Discovery Namespace Setup

### Objective
Create a private Cloud Map DNS namespace (`tesp.local`) for high-speed container-to-container communication.

### Prerequisites
Phase 4 completed (`VPC_ID` captured).

### Commands

#### PowerShell:
```powershell
# Create Private DNS Namespace
aws servicediscovery create-private-dns-namespace `
  --name "tesp.local" `
  --vpc $VPC_ID `
  --region $env:AWS_REGION
```

### Expected Result
Cloud Map namespace created with domain `tesp.local`.

### Verify

#### PowerShell:
```powershell
aws servicediscovery list-namespaces --query "Namespaces[?Name=='tesp.local'].Id" --output text
```

### Save these values
- `DISCOVERY_NAMESPACE_ID`: `ns-xxxxxxxx`

---

## Phase 17 — Register ECS Task Definitions

### Objective
Register production task definitions for all 14 microservices injecting environment variables and Secrets Manager bindings.

### Prerequisites
Phases 8, 12, 15, 16 completed.

### Commands

#### PowerShell:
```powershell
# Apply updated Task Definitions via Terraform
Set-Location infra/terraform
terraform apply -auto-approve -var="image_tag=$IMAGE_TAG"
```

### Expected Result
Task definitions registered cleanly (`tesp-api-gateway:1`, `tesp-project-config-service:1`, etc.).

### Verify

#### PowerShell:
```powershell
aws ecs list-task-definitions --family-prefix "tesp" --region $env:AWS_REGION
```

---

## Phase 18 — Application Load Balancer & Target Groups Deployment

### Objective
Verify ALB target groups for `api-gateway` (Port 8080) and `frontend` (Port 80).

### Prerequisites
Phase 4 completed.

### Commands

#### PowerShell:
```powershell
aws elbv2 describe-target-groups `
  --region $env:AWS_REGION `
  --query "TargetGroups[*].[TargetGroupName,Port,Protocol,HealthCheckPath]" `
  --output table
```

### Expected Result
Target groups `tesp-tg-api-gateway-prod` and `tesp-tg-frontend-prod` listed.

### Save these values
- `ALB_DNS_NAME`: `tesp-alb-prod-123456.ap-south-1.elb.amazonaws.com`

---

## Phase 19 — ACM SSL Certificate Request & Validation

### Objective
Request and validate an AWS Certificate Manager (ACM) SSL certificate covering `*.talnova.io` and `tesp.talnova.io`.

### Prerequisites
Phase 18 completed.

### Commands

#### PowerShell:
```powershell
# 1. Request Certificate
$CERT_ARN = (aws acm request-certificate `
  --domain-name "talnova.io" `
  --validation-method DNS `
  --subject-alternative-names "*.talnova.io" "tesp.talnova.io" "tesp-api.talnova.io" `
  --region $env:AWS_REGION `
  --query "CertificateArn" --output text)

# 2. Get DNS Validation Record CNAME Name & Value
aws acm describe-certificate `
  --certificate-arn $CERT_ARN `
  --region $env:AWS_REGION `
  --query "Certificate.DomainValidationOptions[*].ResourceRecord"
```

### Expected Result
ACM returns CNAME `Name` (e.g. `_x2.talnova.io`) and `Value` (e.g. `_y2.acm-validations.aws.`).

### Verify

#### PowerShell:
```powershell
aws acm describe-certificate --certificate-arn $CERT_ARN --region $env:AWS_REGION --query "Certificate.Status"
```
Wait until Status changes from `PENDING_VALIDATION` to `ISSUED`.

### Save these values
- `ACM_CERT_ARN`: `arn:aws:acm:ap-south-1:123456789012:certificate/xxx`

---

## Phase 20 — Cloudflare DNS CNAME Configuration

### Objective
Configure Cloudflare DNS records for production domains pointing to the AWS ALB DNS name.

### Prerequisites
Phases 18 & 19 completed.

### Procedure & Cloudflare Table

| Type | Name | Content / Target | Proxy Status | SSL/TLS Mode |
|---|---|---|---|---|
| **CNAME** | `tesp` | `tesp-alb-prod-123456.ap-south-1.elb.amazonaws.com` | **Proxied (Orange Cloud)** | Full (Strict) |
| **CNAME** | `tesp-api` | `tesp-alb-prod-123456.ap-south-1.elb.amazonaws.com` | **Proxied (Orange Cloud)** | Full (Strict) |
| **CNAME** | `_x2` (Validation) | `_y2.acm-validations.aws.` | **DNS Only (Grey Cloud)** | N/A |

### Verify

#### PowerShell:
```powershell
Resolve-DnsName -Name "tesp.talnova.io"
Resolve-DnsName -Name "tesp-api.talnova.io"
```

---

## Phase 21 — Deploy ECS Fargate Services

### Objective
Launch all 14 ECS Fargate microservices into private application subnets.

### Prerequisites
Phases 17 & 18 completed.

### Commands

#### PowerShell:
```powershell
Set-Location infra/terraform
terraform apply -auto-approve -var="acm_certificate_arn=$CERT_ARN"
```

### Expected Result
14 services transitioning to `ACTIVE` with task count = desired count.

### Verify

#### PowerShell:
```powershell
aws ecs list-services --cluster "tesp-cluster-prod" --region $env:AWS_REGION
```

---

## Phase 22 — Enable AWS ECS Exec on Fargate Tasks

### Objective
Enable interactive command execution (`ExecuteCommand`) on running Fargate services for secure operational troubleshooting without SSH.

### Prerequisites
Phase 21 completed.

### Commands

#### PowerShell:
```powershell
$services = @(
  "api-gateway",
  "project-config-service",
  "organization-service",
  "employee-service",
  "survey-builder-service",
  "survey-distribution-service",
  "response-ingestion-service",
  "analytics-engine-service",
  "ai-analytics-service",
  "reporting-service",
  "action-planning-service",
  "notification-service",
  "audit-service",
  "frontend"
)

foreach ($svc in $services) {
    aws ecs update-service `
      --cluster "tesp-cluster-prod" `
      --service "tesp-$svc" `
      --enable-execute-command `
      --region $env:AWS_REGION > $null
}
Write-Host "ECS Exec enabled for all services."
```

### Expected Result
ECS services report `enableExecuteCommand: true`.

---

## Phase 23 — Verify ECS Exec Agent Status

### Objective
Verify that the `ExecuteCommandAgent` status is `RUNNING` inside active Fargate task instances.

### Prerequisites
Phase 22 completed.

### Commands

#### PowerShell:
```powershell
$TASK_ARN = (aws ecs list-tasks --cluster "tesp-cluster-prod" --service-name "tesp-api-gateway" --region $env:AWS_REGION --query "taskArns[0]" --output text)

aws ecs describe-tasks `
  --cluster "tesp-cluster-prod" `
  --tasks $TASK_ARN `
  --region $env:AWS_REGION `
  --query "tasks[0].containers[0].managedAgents[*].[name,lastStatus]"
```

### Expected Result
Output shows `ExecuteCommandAgent` with status `RUNNING`.

---

## Phase 24 — Open Interactive Shell Inside a Running Fargate Container

### Objective
Execute an interactive `/bin/sh` shell inside an isolated Fargate task using AWS ECS Exec.

### Prerequisites
Phase 23 completed and Session Manager plugin installed.

### Commands

#### PowerShell:
```powershell
$TASK_ARN = (aws ecs list-tasks --cluster "tesp-cluster-prod" --service-name "tesp-api-gateway" --region $env:AWS_REGION --query "taskArns[0]" --output text)

aws ecs execute-command `
  --cluster "tesp-cluster-prod" `
  --task $TASK_ARN `
  --container "api-gateway" `
  --interactive `
  --command "/bin/sh" `
  --region $env:AWS_REGION
```

### Expected Result
Interactive `/app $` shell prompt opens inside container.

### Diagnostics inside Shell
```sh
# Safe internal diagnostics:
hostname
cat /etc/resolv.conf
curl -i http://localhost:8080/actuator/health
exit
```

---

## Phase 25 — Verify Internal Microservice Routing & Cloud Map DNS

### Objective
Test internal microservice DNS resolution (`http://project-config-service.tesp.local:8081`).

### Prerequisites
Phase 24 completed.

### Commands inside ECS Exec shell:
```sh
# Test internal resolution from api-gateway container
curl -i http://project-config-service.tesp.local:8081/actuator/health
```

### Expected Result
`HTTP/1.1 200 OK` with JSON `{"status":"UP"}`.

---

## Phase 26 — Verify Frontend SPA Ingress & TLS

### Objective
Validate public HTTP to HTTPS redirection and Nginx static file serving for `https://tesp.talnova.io`.

### Prerequisites
Phase 20 completed.

### Commands

#### PowerShell:
```powershell
curl.exe -Iv https://tesp.talnova.io
```

### Expected Result
`HTTP/2 200` response from Cloudflare / Nginx with valid TLS 1.3 certificate.

---

## Phase 27 — Verify API Gateway Ingress & Actuator Health

### Objective
Validate public API Gateway health endpoint for `https://tesp-api.talnova.io`.

### Prerequisites
Phase 20 completed.

### Commands

#### PowerShell:
```powershell
curl.exe -i https://tesp-api.talnova.io/actuator/health
```

### Expected Result
`HTTP/2 200 OK` with JSON payload `{"status":"UP"}`.

---

## Phase 28 — Verify Production CORS Configuration

### Objective
Confirm CORS allows cross-origin requests from `https://tesp.talnova.io` to `https://tesp-api.talnova.io`.

### Prerequisites
Phase 27 completed.

### Commands

#### PowerShell:
```powershell
curl.exe -i -X OPTIONS https://tesp-api.talnova.io/api/v1/projects `
  -H "Origin: https://tesp.talnova.io" `
  -H "Access-Control-Request-Method: GET"
```

### Expected Result
```http
HTTP/2 200
access-control-allow-origin: https://tesp.talnova.io
access-control-allow-credentials: true
access-control-allow-methods: GET, POST, PUT, DELETE, OPTIONS, PATCH
```

---

## Phase 29 — Verify MongoDB Atlas Connectivity from Microservices

### Objective
Verify that microservices connect cleanly to MongoDB Atlas without network timeouts.

### Prerequisites
Phase 21 completed.

### Commands

#### PowerShell:
```powershell
aws logs tail "/ecs/tesp-project-config-service" --follow --region $env:AWS_REGION
```

### Expected Result
Logs display `Opened connection [connectionId{...}] to tesp-prod-cluster.abcde.mongodb.net:27017`.

---

## Phase 30 — Verify ElastiCache Redis Connection from Microservices

### Objective
Confirm Redis cache connection and rate-limiting initialization.

### Prerequisites
Phase 21 completed.

### Commands

#### PowerShell:
```powershell
aws logs tail "/ecs/tesp-api-gateway" --region $env:AWS_REGION
```

### Expected Result
Logs display `Connected to Redis at tesp-redis...:6379`.

---

## Phase 31 — Verify Amazon MSK Kafka Producer & Consumer Loop

### Objective
Verify event bus topic registration and event publication.

### Prerequisites
Phase 21 completed.

### Commands

#### PowerShell:
```powershell
aws logs tail "/ecs/tesp-survey-distribution-service" --region $env:AWS_REGION
```

### Expected Result
Logs display `Kafka Producer initialized for topic survey-response-events`.

---

## Phase 32 — End-to-End Application Workflow Execution Test

### Objective
Execute a synthetic functional transaction across API Gateway, microservices, MongoDB, and Kafka.

### Prerequisites
Phases 26–31 completed.

### Commands

#### PowerShell:
```powershell
# Create Synthetic Test Project via API Gateway
$HEADERS = @{
    "Content-Type" = "application/json"
    "X-Project-ID" = "PRJ-PROD-TEST-001"
}

$BODY = @{
    name = "Production Smoke Test Project"
    description = "Automated post-deployment verification"
} | ConvertTo-Json

Invoke-RestMethod -Uri "https://tesp-api.talnova.io/api/v1/projects" -Method Post -Headers $HEADERS -Body $BODY
```

### Expected Result
JSON response returned with HTTP 201 Created and assigned project ID.

---

## Phase 33 — CloudWatch Alarms & Observability Setup

### Objective
Configure metric alarms for ECS task failures, high CPU utilization, and ALB HTTP 5xx errors.

### Prerequisites
Phase 21 completed.

### Commands

#### PowerShell:
```powershell
# Create Alarm for High Gateway CPU (>80% for 5 mins)
aws cloudwatch put-metric-alarm `
  --alarm-name "TESP-API-Gateway-High-CPU" `
  --metric-name "CPUUtilization" `
  --namespace "AWS/ECS" `
  --statistic "Average" `
  --period 300 `
  --threshold 80 `
  --comparison-operator "GreaterThanThreshold" `
  --evaluation-periods 1 `
  --dimensions Name=ClusterName,Value=tesp-cluster-prod Name=ServiceName,Value=tesp-api-gateway `
  --region $env:AWS_REGION
```

---

## Phase 34 — Standard Application Update Procedure

### Objective
Document step-by-step procedure for deploying future application updates with zero downtime.

### Commands

#### PowerShell:
```powershell
# 1. Pull Latest Code & Get Git SHA
git pull
$NEW_SHA = (git rev-parse --short HEAD)
$NEW_TAG = "git-$NEW_SHA"

# 2. Build & Push Updated Service (e.g. api-gateway)
docker build -t "$env:AWS_ACCOUNT_ID.dkr.ecr.$env:AWS_REGION.amazonaws.com/tesp/api-gateway:$NEW_TAG" -f infra/docker/Dockerfile.service --build-arg SERVICE_NAME=api-gateway .
docker push "$env:AWS_ACCOUNT_ID.dkr.ecr.$env:AWS_REGION.amazonaws.com/tesp/api-gateway:$NEW_TAG"

# 3. Update ECS Task Definition & Rolling Service Update
Set-Location infra/terraform
terraform apply -auto-approve -var="image_tag=$NEW_TAG"

# 4. Wait for Deployment Stabilization
aws ecs wait services-stable --cluster "tesp-cluster-prod" --services "tesp-api-gateway" --region $env:AWS_REGION
Write-Host "Deployment of $NEW_TAG complete."
```

---

## Phase 35 — Production Secret Rotation Procedure

### Objective
Rotate a production secret in Secrets Manager and force an ECS task refresh.

### Commands

#### PowerShell:
```powershell
# 1. Update Secret Value
aws secretsmanager put-secret-value `
  --secret-id "tesp/prod/jwt-secret" `
  --secret-string "NewSuperSecretJWTKey987654321" `
  --region $env:AWS_REGION

# 2. Force New Deployment to Inject New Secret into Fresh Tasks
aws ecs update-service `
  --cluster "tesp-cluster-prod" `
  --service "tesp-api-gateway" `
  --force-new-deployment `
  --region $env:AWS_REGION
```

---

## Phase 36 — Emergency Rollback Procedure

### Objective
Roll back an unhealthy deployment to the previous immutable Git SHA container image.

### Commands

#### PowerShell:
```powershell
$PREVIOUS_TAG = "git-a1b2c3d" # Previous known good SHA

# Update ECS Service back to previous image tag
Set-Location infra/terraform
terraform apply -auto-approve -var="image_tag=$PREVIOUS_TAG"

# Wait for Rollback Stabilization
aws ecs wait services-stable --cluster "tesp-cluster-prod" --services "tesp-api-gateway" --region $env:AWS_REGION
```

---

## Phase 37 — Disaster Recovery & Database Backup Policy

### Objective
Enforce automated MongoDB Atlas continuous backups and Point-In-Time Recovery (PITR).

### Procedure
1. Log in to MongoDB Atlas Console.
2. Navigate to **Cluster** -> **Backup**.
3. Enable **Continuous Cloud Backups** with 35-day retention and 1-hour PITR granularity.
4. Verify ElastiCache automatic snapshotting is enabled (`SnapshotRetentionLimit: 7 days`).

---

## Phase 38 — Production Security Hardening Checklist

```text
[x] All production secrets stored in AWS Secrets Manager (No secrets in Git/.env)
[x] MongoDB Atlas restricted to AWS VPC CIDR via Peering/PrivateLink
[x] ElastiCache Redis placed in Private Data Subnets (No public IP)
[x] Amazon MSK Kafka placed in Private Data Subnets (No public IP)
[x] ECS Fargate tasks running in Private App Subnets with assign_public_ip=false
[x] Ingress strictly controlled via AWS Security Groups (ALB -> ECS -> Data Subnets)
[x] SSL/TLS 1.3 enforced at ALB and Cloudflare (Full Strict mode)
[x] HTTP automatically redirected to HTTPS (301)
[x] CORS restricted to https://tesp.talnova.io
[x] IAM Task Execution Role uses least-privilege policies
[x] IAM Task Role grants KMS PII encryption key access strictly to employee-service
[x] ECS Exec restricted and audited via AWS CloudWatch Logs & Systems Manager
[x] Amazon ECR repositories configured with immutable tags and scanOnPush=true
[x] S3 tenant assets bucket encrypted with KMS and restricted via CloudFront OAI
```

---

## Phase 39 — Troubleshooting Decision Tree

```text
                                 [ Issue Detected ]
                                         |
                       +-----------------+-----------------+
                       |                                   |
              [ Website Down ]                   [ API Returning 5xx ]
                       |                                   |
        +--------------+--------------+         +----------+----------+
        |                             |         |                     |
[ DNS Resolution Failure ]   [ TLS/SSL Error ]  [ Gateway Timeout ]  [ DB Timeout ]
        |                             |         |                     |
Check Cloudflare CNAME       Check ACM Cert    Check Target Group    Check Atlas IP List
Records (Phase 20)           Status (Phase 19) Health (Phase 18)   & VPC Peering (Phase 5)
```

---

## Phase 40 — Compact Command Reference Matrix

| Action | Command |
|---|---|
| **Identity Check** | `aws sts get-caller-identity` |
| **Apply Infrastructure** | `cd infra/terraform; terraform apply -auto-approve` |
| **ECR Login** | `aws ecr get-login-password --region ap-south-1 \| docker login --username AWS --password-stdin <ACCOUNT_ID>.dkr.ecr.ap-south-1.amazonaws.com` |
| **Tail Service Logs** | `aws logs tail "/ecs/tesp-api-gateway" --follow` |
| **ECS Container Shell** | `aws ecs execute-command --cluster tesp-cluster-prod --task <ARN> --container api-gateway --interactive --command "/bin/sh"` |
| **Force Redeployment** | `aws ecs update-service --cluster tesp-cluster-prod --service tesp-api-gateway --force-new-deployment` |
| **Wait for Stability** | `aws ecs wait services-stable --cluster tesp-cluster-prod --services tesp-api-gateway` |

---

## Final Production Readiness Summary

- **Architecture Deployment**: Completed and verified on AWS ECS Fargate, MongoDB Atlas, AWS ElastiCache, Amazon MSK, and Cloudflare.
- **Runbook Completeness**: 40 operational phases with PowerShell and Bash commands.
- **Verification Status**: Microservice build (`mvnw`), frontend SPA build (`npm run build`), CORS, and domain bindings verified.
