# AWS Secrets Manager Infrastructure for TESP

resource "aws_secretsmanager_secret" "mongodb_uri" {
  name        = "tesp/${var.environment}/mongodb-uri"
  description = "Production MongoDB Atlas Connection URI for TESP microservices"

  tags = {
    Environment = var.environment
    ManagedBy   = "Terraform"
  }
}

resource "aws_secretsmanager_secret" "redis_host" {
  name        = "tesp/${var.environment}/redis-host"
  description = "Production ElastiCache Redis endpoint host and credentials"

  tags = {
    Environment = var.environment
    ManagedBy   = "Terraform"
  }
}

resource "aws_secretsmanager_secret" "kafka_bootstrap_servers" {
  name        = "tesp/${var.environment}/kafka-bootstrap-servers"
  description = "Production Amazon MSK Kafka bootstrap broker addresses"

  tags = {
    Environment = var.environment
    ManagedBy   = "Terraform"
  }
}

resource "aws_secretsmanager_secret" "jwt_secret" {
  name        = "tesp/${var.environment}/jwt-secret"
  description = "Production JWT HMAC signature secret key"

  tags = {
    Environment = var.environment
    ManagedBy   = "Terraform"
  }
}

resource "aws_secretsmanager_secret" "openai_api_key" {
  name        = "tesp/${var.environment}/openai-api-key"
  description = "Production OpenAI API Key for ai-analytics-service"

  tags = {
    Environment = var.environment
    ManagedBy   = "Terraform"
  }
}
