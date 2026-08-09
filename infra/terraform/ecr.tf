# Amazon ECR Repositories for TESP Microservices and Frontend SPA

locals {
  services = [
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
  ]
}

resource "aws_ecr_repository" "repo" {
  for_each             = toset(local.services)
  name                 = "tesp/${each.key}"
  image_tag_mutability = "IMMUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Environment = var.environment
    ManagedBy   = "Terraform"
  }
}

resource "aws_ecr_lifecycle_policy" "repo_policy" {
  for_each   = toset(local.services)
  repository = aws_ecr_repository.repo[each.key].name

  policy = jsonencode({
    rules = [
      {
        rulePriority = 1
        description  = "Keep last 30 tagged images"
        selection = {
          tagStatus     = "any"
          countType     = "sinceImagePushed"
          countUnit     = "days"
          countNumber   = 30
        }
        action = {
          type = "expire"
        }
      }
    ]
  })
}
