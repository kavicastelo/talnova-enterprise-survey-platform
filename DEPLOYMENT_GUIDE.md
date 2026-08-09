# Production AWS Deployment Guide
## Talnova Enterprise Survey Platform (TESP)

> **Target Production Domains**:
> - Frontend Application SPA: `https://tesp.talnova.io`
> - API Gateway & Backend Services: `https://tesp-api.talnova.io`

---

## 1. Architecture Overview

The **Talnova Enterprise Survey Platform (TESP)** is an enterprise multi-tenant microservices platform consisting of **13 Spring Boot (Java 21 LTS) backend services**, **1 React / Vite single-page application (SPA)** served via **Nginx**, external managed database/event bus infrastructure (**MongoDB Atlas**, **AWS ElastiCache for Redis**, and **Amazon MSK Kafka**), and an edge layer managed via **Cloudflare DNS/WAF** and an **AWS Application Load Balancer (ALB)**.

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
                                                                   +---> Microservice A (8081)
                                                                   +---> Microservice B (8082)
                                                                   +---> Microservice C (8083)
                                                                   +---> ... (12 Microservices)
                                                                   
---------------------------------------------------------------------------------------------------
Private Managed Infrastructure:
   +--> MongoDB Atlas (Multi-AZ Sharded Cluster via VPC Peering)
   +--> AWS ElastiCache for Redis (Multi-AZ Replication Group in Private Data Subnet)
   +--> Amazon MSK Kafka (Multi-AZ Cluster in Private Data Subnet)
   +--> AWS Secrets Manager (Dynamic Credentials Injection)
   +--> AWS CloudWatch Logs (Container Observability)
```

---

## 2. Current Project Architecture & Service Inventory

| Component / Service Name | Runtime / Language | Exposed Local Port | Internal Port | Managed DB / Bus Dependencies | Scalability & Statelessness |
|---|---|---|---|---|---|
| **frontend** | React 18 / Vite / Nginx | `3000` | `80` | None | Stateless (HPA 2-10 tasks) |
| **api-gateway** | Java 21 / Spring Cloud Gateway | `8080` | `8080` | Redis (Rate Limiting) | Stateless (HPA 3-12 tasks) |
| **project-config-service** | Java 21 / Spring Boot | `8081` | `8081` | MongoDB (`tesp_config_db`), Redis, Kafka, S3 | Stateless (HPA 2-6 tasks) |
| **organization-service** | Java 21 / Spring Boot | `8082` | `8082` | MongoDB (`tesp_org_db`), Kafka | Stateless (HPA 2-6 tasks) |
| **employee-service** | Java 21 / Spring Boot | `8083` | `8083` | MongoDB (`tesp_employee_db`), Kafka, AWS KMS | Stateless (HPA 2-6 tasks) |
| **survey-builder-service** | Java 21 / Spring Boot | `8084` | `8084` | MongoDB (`tesp_survey_db`), Kafka | Stateless (HPA 2-6 tasks) |
| **survey-distribution-service** | Java 21 / Spring Boot | `8085` | `8085` | MongoDB (`tesp_dist_db`), Redis, Kafka | Stateless (HPA 2-10 tasks) |
| **response-ingestion-service** | Java 21 / Spring Boot | `8086` | `8086` | MongoDB (`tesp_ingest_db`), Redis, Kafka | Stateless (HPA 4-20 tasks) |
| **analytics-engine-service** | Java 21 / Spring Boot | `8087` | `8087` | MongoDB (`tesp_analytics_db`), Redis, Kafka | Stateless (HPA 3-15 tasks) |
| **ai-analytics-service** | Java 21 / Spring Boot | `8088` | `8088` | MongoDB (`tesp_ai_db`), Redis, Kafka, OpenAI API | Stateless (HPA 2-8 tasks) |
| **reporting-service** | Java 21 / Spring Boot | `8089` | `8089` | MongoDB (`tesp_report_db`), Redis, Kafka | Stateless (HPA 2-10 tasks) |
| **action-planning-service** | Java 21 / Spring Boot | `8090` | `8090` | MongoDB (`tesp_action_db`), Kafka | Stateless (HPA 2-4 tasks) |
| **notification-service** | Java 21 / Spring Boot | `8091` | `8091` | Redis, Kafka | Stateless (HPA 2-6 tasks) |
| **audit-service** | Java 21 / Spring Boot | `8092` | `8092` | MongoDB (`tesp_audit_db`), Kafka | Stateless (HPA 2-4 tasks) |

---

## 3. Production AWS Architecture Selection

### Recommended Architecture: **AWS ECS Fargate**

```text
Recommended Architecture: AWS ECS Fargate
Selection Rationale: Serverless container compute minimizing operational complexity
```

### Evaluation & Comparison Matrix

| Feature / Criteria | AWS ECS Fargate (Selected) | AWS EKS (Kubernetes) | ECS on EC2 | AWS App Runner |
|---|---|---|---|---|
| **Operational Complexity** | Low (Serverless container tasks) | High (Control plane & node pool management) | Medium (EC2 OS patching & agent updates) | Low (Simplified web service wrapper) |
| **Multi-AZ Availability** | Native (Automatic across 3 AZs) | Manual node group distribution required | Manual ASG configuration across AZs | Single region container instance |
| **Infrastructure Cost** | Pay-per-vCPU/RAM task | $72/mo control plane + EC2 nodes | Fixed EC2 instance costs | Higher per-GB memory costs |
| **VPC Integration** | Full `awsvpc` network mode | Full VPC CNI | Host / bridge networking | Limited VPC peering |
| **IAM Integration** | Native IAM Task & Execution Roles | Requires IRSA / EKS Pod Identity | IAM Instance Profile | Limited IAM role mapping |

**Why EKS was NOT selected**: Kubernetes introduces unnecessary operational overhead (control plane version upgrades, etcd maintenance, Helm/Kustomize manifest complexity, ingress controller setup) for an application with 14 stateless container types. ECS Fargate provides identical multi-AZ autoscaling, rolling updates, and container isolation with zero control plane costs.

---

## 4. Local vs. Production Infrastructure Model

| Infrastructure Component | Local Environment (Docker Compose) | Production Environment (AWS + Cloud Managed) |
|---|---|---|
| **Container Engine** | Docker / Podman (Windows/Linux) | AWS ECS Fargate (Serverless OCI Containers) |
| **Container Registry** | Local Docker Daemon Image Cache | Amazon Elastic Container Registry (ECR) |
| **Database** | `mongo:7.0` container | **MongoDB Atlas** Multi-AZ Cluster (PrivateLink / Peering) |
| **Cache & In-Memory State** | `redis:7.2-alpine` container | **AWS ElastiCache for Redis** (Multi-AZ Private Subnet) |
| **Event Bus & Messaging** | `confluentinc/cp-kafka:7.5.0` container | **Amazon MSK (Managed Streaming for Kafka)** |
| **Secrets & Credentials** | Plaintext `.env` file | **AWS Secrets Manager** & KMS Encryption |
| **Load Balancer & SSL** | Localhost Nginx reverse proxy | **AWS ALB** + **ACM TLS 1.3** + **Cloudflare DNS/WAF** |
| **Observability & Logs** | `docker logs` stdout | **AWS CloudWatch Logs** (`/ecs/tesp-*`) |

---

## 5. Docker vs. Podman Analysis & Migration Guide

### 5.1 Docker vs. Podman Comparison

* **Architecture**: Docker uses a root-privileged daemon (`dockerd`). Podman uses a daemonless, fork-exec model where containers run as child processes.
* **Security**: Podman natively supports **rootless containers**, mitigating container escape vulnerabilities.
* **Production Compatibility**: Both Docker and Podman produce 100% standard **OCI (Open Container Initiative)** compliant image format layers. Amazon ECR and AWS ECS Fargate accept images built by either Docker or Podman identically.
* **Local Development**: Docker Compose uses `docker-compose.yml`. Podman uses `podman-compose` or `podman compose` plugin.

### 5.2 Local Linux Migration from Docker to Podman

If running local Linux development environments:

```bash
# 1. Install Podman and podman-compose on RHEL/Fedora/Ubuntu
sudo apt-get update && sudo apt-get install -y podman podman-compose  # Ubuntu/Debian
# or: sudo dnf install -y podman podman-compose                         # RHEL/Fedora

# 2. Enable rootless socket and user lingering
systemctl --user enable --now podman.socket
loginctl enable-linger $USER

# 3. Create Podman volume and network equivalents
podman network create tesp-network
podman volume create mongodb_data
podman volume create redis_data

# 4. Equivalent Build and Execution Commands
# Docker:
# docker build -t tesp/api-gateway:latest -f infra/docker/Dockerfile.service --build-arg SERVICE_NAME=api-gateway .
# Podman Equivalent:
podman build -t tesp/api-gateway:latest -f infra/docker/Dockerfile.service --build-arg SERVICE_NAME=api-gateway .

# 5. Launch local platform using podman-compose
podman-compose -f docker-compose.yml up -d
```

---

## 6. Container Image Strategy & Hardening

Every microservice in TESP uses a hardened multi-stage Docker build:

* **Stage 1 (Builder)**: `eclipse-temurin:21-jdk-alpine` compiles Java code with cached Maven dependencies (`mvn clean package -pl services/${SERVICE_NAME} -am -DskipTests`).
* **Stage 2 (Runtime)**: `eclipse-temurin:21-jre-alpine` provides minimal runtime footprint (~200MB vs ~800MB JDK).
* **Security Enforcement**:
  * Non-root user execution (`USER tesp:tesp` with UID/GID 10001).
  * Process signal handling via `dumb-init`.
  * JVM memory tuning for container boundaries (`-XX:+UseG1GC -XX:MaxRAMPercentage=75.0`).
  * Exclusion of build tools, source code, and secrets.

---

## 7. Amazon ECR Workflow

### 7.1 ECR Repository Creation

```bash
export AWS_REGION="ap-south-1"
export AWS_ACCOUNT_ID="123456789012" # Replace with actual AWS Account ID

# Create ECR repository for each service with immutable tags
aws ecr create-repository \
  --repository-name tesp/api-gateway \
  --image-tag-mutability IMMUTABLE \
  --image-scanning-configuration scanOnPush=true \
  --region ${AWS_REGION}
```

### 7.2 Building, Tagging, and Pushing Images

```bash
# 1. Authenticate Docker/Podman to ECR
aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com

# 2. Define Immutable Git Commit SHA Tag
GIT_SHA=$(git rev-parse --short HEAD)
IMAGE_TAG="git-${GIT_SHA}"

# 3. Build API Gateway Image
docker build \
  -t ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/tesp/api-gateway:${IMAGE_TAG} \
  -f infra/docker/Dockerfile.service \
  --build-arg SERVICE_NAME=api-gateway .

# 4. Push Image to ECR
docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/tesp/api-gateway:${IMAGE_TAG}
```

---

## 8. AWS Network Design & Subnet Allocation

```text
VPC CIDR: 10.0.0.0/16 (AWS Region: ap-south-1)
├── Public Subnets (Internet Gateway + NAT Gateways + ALB)
│   ├── ap-south-1a: 10.0.0.0/20   (tesp-public-subnet-1)
│   ├── ap-south-1b: 10.0.16.0/20  (tesp-public-subnet-2)
│   └── ap-south-1c: 10.0.32.0/20  (tesp-public-subnet-3)
├── Private Application Subnets (ECS Fargate Tasks - No Public IPs)
│   ├── ap-south-1a: 10.0.64.0/20  (tesp-private-app-subnet-1)
│   ├── ap-south-1b: 10.0.80.0/20  (tesp-private-app-subnet-2)
│   └── ap-south-1c: 10.0.96.0/20  (tesp-private-app-subnet-3)
└── Private Data Subnets (ElastiCache Redis + Amazon MSK Kafka)
    ├── ap-south-1a: 10.0.128.0/20 (tesp-private-data-subnet-1)
    ├── ap-south-1b: 10.0.144.0/20 (tesp-private-data-subnet-2)
    └── ap-south-1c: 10.0.160.0/20 (tesp-private-data-subnet-3)
```

---

## 9. Security Groups Matrix

| Security Group Name | Ingress Rules | Egress Rules | Purpose |
|---|---|---|---|
| `tesp-alb-sg` | `80/tcp`, `443/tcp` from `0.0.0.0/0` | All traffic (`-1`) to `tesp-ecs-tasks-sg` | ALB Ingress Firewall |
| `tesp-ecs-tasks-sg` | `8080-8092/tcp`, `80/tcp` from `tesp-alb-sg`; self intra-cluster | All traffic (`-1`) to Internet / Data Subnets | ECS Microservices Firewall |
| `tesp-elasticache-sg` | `6379/tcp` from `tesp-ecs-tasks-sg` | None | ElastiCache Redis Protection |
| `tesp-msk-sg` | `9092/tcp`, `9094/tcp`, `9096/tcp` from `tesp-ecs-tasks-sg` | None | Amazon MSK Broker Protection |

---

## 10. IAM Roles & Least-Privilege Policies

1. **ECS Task Execution Role (`tesp-ecs-execution-role-prod`)**:
   * Attached Managed Policy: `AmazonECSTaskExecutionRolePolicy`
   * Custom Inline Policy: `secretsmanager:GetSecretValue` and `kms:Decrypt` for fetching runtime secrets from AWS Secrets Manager.
2. **ECS Task Role (`tesp-ecs-task-role-prod`)**:
   * Grants runtime permissions to `employee-service` for AWS KMS PII Client-Side Field Level Encryption (`kms:Encrypt`, `kms:Decrypt`).
   * Grants `project-config-service` read/write permissions for S3 tenant assets bucket (`tesp-tenant-assets-prod`).

---

## 11. AWS Secrets Manager Configuration

Microservices fetch database credentials and secrets dynamically at startup via ECS Task Definition environment bindings (`valueFrom` ARNs):

| Secret Key Name | Production Value Source | Secret Purpose |
|---|---|---|
| `tesp/prod/mongodb-uri` | MongoDB Atlas Connection String (`mongodb+srv://...`) | Primary Database Access |
| `tesp/prod/redis-host` | ElastiCache Redis Primary Endpoint Host | Caching & Rate Limiting |
| `tesp/prod/kafka-bootstrap-servers` | Amazon MSK Broker Endpoints (`b-1...:9092,b-2...:9092`) | Event Bus Communication |
| `tesp/prod/jwt-secret` | 256-bit Hex HMAC Signing Key | JWT Token Generation & Verification |
| `tesp/prod/openai-api-key` | OpenAI Secret API Key | AI Analytics Integration |

---

## 12. MongoDB Atlas Production Architecture

### 12.1 Cluster Provisioning
* **Tier**: Dedicated `M10` or `M20` cluster (Multi-AZ with automatic failover).
* **Region**: AWS `ap-south-1` (same region as ECS cluster for lowest latency).
* **Security & Network Access**:
  * Create AWS VPC Peering Connection or AWS PrivateLink between TESP VPC (`10.0.0.0/16`) and MongoDB Atlas VPC.
  * Configure IP Access List restricted to TESP VPC CIDR `10.0.0.0/16`.
* **Database User & Authentication**:
  * Database user: `tesp_db_user` with SCRAM-SHA-256 or X.509 authentication.
  * Role: `readWriteAnyDatabase`.

### 12.2 Database Connection String Injection

```text
mongodb+srv://tesp_db_user:<PASSWORD>@tesp-cluster.abcde.mongodb.net/tesp_config_db?retryWrites=true&w=majority&authSource=admin
```

Store this URI in AWS Secrets Manager: `tesp/prod/mongodb-uri`.

---

## 13. AWS ElastiCache for Redis Setup

### 13.1 Cluster Configuration
* **Engine**: Redis 7.x (or Valkey-compatible cluster).
* **Node Type**: `cache.t4g.small` or `cache.m6g.large` (Multi-AZ enabled).
* **Subnet Group**: Placed in Private Data Subnets (`tesp-private-data-subnet-*`).
* **Security Group**: `tesp-elasticache-sg` allowing port `6379` from `tesp-ecs-tasks-sg`.
* **Transit Encryption**: Enable TLS encryption in transit.
* **Auth**: Enable Redis AUTH token.

---

## 14. Amazon MSK (Kafka) Production Setup

### 14.1 Cluster Configuration
* **Broker Nodes**: 3 Brokers across 3 Availability Zones (`ap-south-1a`, `ap-south-1b`, `ap-south-1c`).
* **Instance Type**: `kafka.m5.large`.
* **Subnets**: Placed in Private Data Subnets (`tesp-private-data-subnet-*`).
* **Encryption**: Enable TLS in transit (Broker-to-Broker and Client-to-Broker).
* **Topics & Replication**:
  * `offering-events`, `survey-response-events`, `analytics-calculated-events`, `notification-events`, `audit-events`.
  * Replication Factor: `3`, Minimum In-Sync Replicas (ISR): `2`.

---

## 15. Cloudflare DNS & SSL/TLS Setup

### 15.1 Cloudflare DNS Records Table

| Type | Name | Target / Value | Proxy Status | Purpose |
|---|---|---|---|---|
| **CNAME** | `tesp` | `<ALB-DNS-NAME>.ap-south-1.elb.amazonaws.com` | **Proxied (Orange Cloud)** | Frontend SPA Ingress |
| **CNAME** | `tesp-api` | `<ALB-DNS-NAME>.ap-south-1.elb.amazonaws.com` | **Proxied (Orange Cloud)** | API Gateway Ingress |

### 15.2 SSL/TLS Mode
* Set Cloudflare SSL/TLS encryption mode to **Full (Strict)**.
* ACM SSL Certificate issued on AWS ALB covers `*.talnova.io` and `tesp.talnova.io`.

---

## 16. Infrastructure-as-Code (Terraform) Execution

All production infrastructure is managed declaratively under `infra/terraform/`.

```bash
# 1. Navigate to terraform directory
cd infra/terraform

# 2. Initialize Terraform modules and S3 state backend
terraform init

# 3. Validate HCL configuration files
terraform validate

# 4. Generate deployment execution plan
terraform plan \
  -var="environment=prod" \
  -var="acm_certificate_arn=arn:aws:acm:ap-south-1:123456789012:certificate/abc-123" \
  -out=tfplan

# 5. Apply infrastructure plan
terraform apply tfplan
```

---

## 17. Sequential Step-by-Step Deployment Pipeline

1. **Stage 1 (AWS Account & IAM)**: Configure AWS CLI credentials and initialize S3/DynamoDB state backend.
2. **Stage 2 (VPC & Networking)**: Apply `vpc.tf` and `security_groups.tf` to create 3-AZ network.
3. **Stage 3 (Managed Databases)**: Provision MongoDB Atlas cluster, ElastiCache Redis, and Amazon MSK.
4. **Stage 4 (Secrets Management)**: Populate secrets in AWS Secrets Manager (`tesp/prod/*`).
5. **Stage 5 (ECR Repositories)**: Apply `ecr.tf` to provision 14 container repositories.
6. **Stage 6 (Container Build & Push)**: Execute build scripts to build and push container images tagged with `git-<sha>`.
7. **Stage 7 (Load Balancer & ACM)**: Apply `alb.tf` to provision ALB and listeners.
8. **Stage 8 (ECS Cluster & Services)**: Apply `ecs.tf` to register task definitions and spin up 14 ECS Fargate services.
9. **Stage 9 (Cloudflare DNS Validation)**: Create CNAME records pointing `tesp.talnova.io` and `tesp-api.talnova.io` to ALB DNS.
10. **Stage 10 (Verification)**: Execute automated curl and health check checks.

---

## 18. Verification Checklist & Curl Commands

Run these health verification commands against the live production endpoints:

```bash
# 1. Verify Frontend SPA Ingress & TLS
curl -Iv https://tesp.talnova.io

# 2. Verify API Gateway Health Endpoint
curl -i https://tesp-api.talnova.io/actuator/health

# 3. Verify CORS Configuration for Frontend Origin
curl -i -X OPTIONS https://tesp-api.talnova.io/api/v1/projects \
  -H "Origin: https://tesp.talnova.io" \
  -H "Access-Control-Request-Method: GET"

# Expected Response Headers:
# HTTP/2 200
# access-control-allow-origin: https://tesp.talnova.io
# access-control-allow-credentials: true
```

---

## 19. Troubleshooting Matrix

| Problem / Symptom | Possible Cause | Troubleshooting & Resolution |
|---|---|---|
| **HTTP 502 Bad Gateway at Cloudflare** | ALB target group health checks failing | Inspect CloudWatch log group `/ecs/tesp-api-gateway` or `/ecs/tesp-frontend`. Verify security group allows 80/8080 from ALB. |
| **ECS Task Fails at Launch (`CannotPullContainerError`)** | Execution Role lacks ECR pull permissions | Verify `AmazonECSTaskExecutionRolePolicy` is attached to `tesp-ecs-execution-role-prod`. |
| **MongoDB Connection Timeout** | Atlas IP Access List or VPC Peering missing | Ensure TESP VPC CIDR `10.0.0.0/16` is added to MongoDB Atlas Network Access rules. |
| **Redis Auth Error in Gateway** | Incorrect secret in Secrets Manager | Verify `tesp/prod/redis-host` value and ensure task execution role has `secretsmanager:GetSecretValue`. |
| **CORS Preflight Blocked in Browser** | Mismatched allowed origin | Verify `CORS_ALLOWED_ORIGINS` environment variable in `api-gateway` matches `https://tesp.talnova.io`. |

---

## 20. Rollback Strategy & Cost Optimization

### 20.1 Immutable Image Rollback Procedure

To roll back a bad deployment, update the ECS Service to point to the previous immutable image tag:

```bash
# Roll back api-gateway to previous git commit SHA tag
aws ecs update-service \
  --cluster tesp-cluster-prod \
  --service tesp-api-gateway \
  --task-definition tesp-api-gateway:git-previousSha \
  --region ap-south-1
```

### 20.2 Cost Optimization Summary
* **ECS Fargate**: Memory and CPU scaling configured dynamically based on peak workload demand (downscaling off-peak).
* **NAT Gateways**: Single NAT Gateway used for staging environments to save ~$64/month.
* **ElastiCache & MSK**: Right-sized t4g/m5 instances to prevent idle resource over-provisioning.
