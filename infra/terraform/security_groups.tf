# Security Groups Architecture for TESP

# ALB Security Group
resource "aws_security_group" "alb" {
  name        = "tesp-alb-sg-${var.environment}"
  description = "Security group for TESP Application Load Balancer"
  vpc_id      = aws_vpc.main.id

  ingress {
    description = "Allow HTTP ingress from Cloudflare / Public"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "Allow HTTPS ingress from Cloudflare / Public"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    description = "Allow egress to internal containers"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name        = "tesp-alb-sg-${var.environment}"
    Environment = var.environment
  }
}

# ECS Fargate Tasks Security Group
resource "aws_security_group" "ecs_tasks" {
  name        = "tesp-ecs-tasks-sg-${var.environment}"
  description = "Security group for TESP ECS Fargate microservices tasks"
  vpc_id      = aws_vpc.main.id

  ingress {
    description     = "Allow traffic from ALB to API Gateway and microservices"
    from_port       = 80
    to_port         = 8092
    protocol        = "tcp"
    security_groups = [aws_security_group.alb.id]
  }

  ingress {
    description = "Allow intra-cluster microservice communication"
    from_port   = 8080
    to_port     = 8092
    protocol    = "tcp"
    self        = true
  }

  egress {
    description = "Allow egress for AWS API calls, NAT gateway, MongoDB Atlas, etc."
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name        = "tesp-ecs-tasks-sg-${var.environment}"
    Environment = var.environment
  }
}

# ElastiCache Redis Security Group
resource "aws_security_group" "elasticache" {
  name        = "tesp-elasticache-sg-${var.environment}"
  description = "Security group for TESP ElastiCache Redis Cluster"
  vpc_id      = aws_vpc.main.id

  ingress {
    description     = "Allow Redis access from ECS Fargate tasks"
    from_port       = 6379
    to_port         = 6379
    protocol        = "tcp"
    security_groups = [aws_security_group.ecs_tasks.id]
  }

  egress {
    description = "Disallow outbound connections from cache"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name        = "tesp-elasticache-sg-${var.environment}"
    Environment = var.environment
  }
}

# Amazon MSK Kafka Security Group
resource "aws_security_group" "msk" {
  name        = "tesp-msk-sg-${var.environment}"
  description = "Security group for TESP Amazon MSK Kafka Cluster"
  vpc_id      = aws_vpc.main.id

  ingress {
    description     = "Allow Plaintext Kafka access from ECS Fargate tasks"
    from_port       = 9092
    to_port         = 9092
    protocol        = "tcp"
    security_groups = [aws_security_group.ecs_tasks.id]
  }

  ingress {
    description     = "Allow TLS Kafka access from ECS Fargate tasks"
    from_port       = 9094
    to_port         = 9094
    protocol        = "tcp"
    security_groups = [aws_security_group.ecs_tasks.id]
  }

  ingress {
    description     = "Allow SASL/SCRAM Kafka access from ECS Fargate tasks"
    from_port       = 9096
    to_port         = 9096
    protocol        = "tcp"
    security_groups = [aws_security_group.ecs_tasks.id]
  }

  egress {
    description = "Disallow outbound connections from broker"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name        = "tesp-msk-sg-${var.environment}"
    Environment = var.environment
  }
}
