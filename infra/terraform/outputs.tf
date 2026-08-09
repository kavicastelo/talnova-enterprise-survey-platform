output "ecs_cluster_name" {
  value       = aws_ecs_cluster.main.name
  description = "Name of the provisioned ECS cluster"
}

output "alb_dns_name" {
  value       = aws_lb.main.dns_name
  description = "Public DNS hostname of the Application Load Balancer for Cloudflare CNAME configuration"
}

output "vpc_id" {
  value       = aws_vpc.main.id
  description = "ID of the TESP AWS VPC"
}

output "public_subnet_ids" {
  value       = aws_subnet.public[*].id
  description = "List of public subnet IDs"
}

output "private_app_subnet_ids" {
  value       = aws_subnet.private_app[*].id
  description = "List of private application subnet IDs for ECS tasks"
}

output "private_data_subnet_ids" {
  value       = aws_subnet.private_data[*].id
  description = "List of private data subnet IDs for ElastiCache and MSK"
}

output "ecr_repository_urls" {
  value       = { for k, v in aws_ecr_repository.repo : k => v.repository_url }
  description = "Map of container service names to Amazon ECR repository URLs"
}

output "secrets_manager_arns" {
  value = {
    mongodb_uri             = aws_secretsmanager_secret.mongodb_uri.arn
    redis_host              = aws_secretsmanager_secret.redis_host.arn
    kafka_bootstrap_servers = aws_secretsmanager_secret.kafka_bootstrap_servers.arn
    jwt_secret              = aws_secretsmanager_secret.jwt_secret.arn
    openai_api_key          = aws_secretsmanager_secret.openai_api_key.arn
  }
  description = "ARNs of created AWS Secrets Manager secrets"
}
