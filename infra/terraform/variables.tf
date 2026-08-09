variable "aws_region" {
  type        = string
  default     = "ap-south-1"
  description = "AWS Deployment Region (ap-south-1 Mumbai optimized for South Asia users)"
}

variable "environment" {
  type        = string
  default     = "prod"
  description = "Deployment Environment (dev, staging, prod)"
}

variable "vpc_cidr" {
  type        = string
  default     = "10.0.0.0/16"
  description = "CIDR block for main AWS VPC"
}

variable "frontend_domain" {
  type        = string
  default     = "tesp.talnova.io"
  description = "Production domain for frontend SPA application"
}

variable "api_domain" {
  type        = string
  default     = "tesp-api.talnova.io"
  description = "Production domain for API Gateway"
}

variable "image_tag" {
  type        = string
  default     = "latest"
  description = "Default image tag for container deployments (prefer git SHA in CI/CD)"
}

variable "acm_certificate_arn" {
  type        = string
  default     = ""
  description = "ARN of AWS ACM SSL certificate for *.talnova.io or tesp.talnova.io (optional if created externally)"
}
