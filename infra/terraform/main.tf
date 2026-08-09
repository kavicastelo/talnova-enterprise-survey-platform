terraform {
  required_version = ">= 1.5.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
  backend "s3" {
    bucket         = "tesp-terraform-state-ap-south-1"
    key            = "platform/terraform.tfstate"
    region         = "ap-south-1"
    dynamodb_table = "tesp-terraform-locks"
  }
}

provider "aws" {
  region = var.aws_region
  default_tags {
    tags = {
      Platform    = "Talnova Enterprise Survey Platform"
      Environment = var.environment
      ManagedBy   = "Terraform"
    }
  }
}

# ECS Cluster
resource "aws_ecs_cluster" "main" {
  name = "tesp-cluster-${var.environment}"
}
