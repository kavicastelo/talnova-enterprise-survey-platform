# AWS ECS Fargate Task Definitions and Services for TESP Platform

# CloudWatch Log Groups
resource "aws_cloudwatch_log_group" "ecs_logs" {
  for_each          = toset(local.services)
  name              = "/ecs/tesp-${each.key}"
  retention_in_days = 30

  tags = {
    Environment = var.environment
    Service     = each.key
  }
}

# --- 1. FRONTEND SPA SERVICE ---
resource "aws_ecs_task_definition" "frontend" {
  family                   = "tesp-frontend"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "512"  # 0.5 vCPU
  memory                   = "1024" # 1 GB
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn
  task_role_arn            = aws_iam_role.ecs_task_role.arn

  container_definitions = jsonencode([{
    name      = "frontend"
    image     = "${aws_ecr_repository.repo["frontend"].repository_url}:${var.image_tag}"
    essential = true
    portMappings = [{
      containerPort = 80
      hostPort      = 80
    }]
    logConfiguration = {
      logDriver = "awslogs"
      options = {
        "awslogs-group"         = "/ecs/tesp-frontend"
        "awslogs-region"        = var.aws_region
        "awslogs-stream-prefix" = "ecs"
      }
    }
  }])
}

resource "aws_ecs_service" "frontend" {
  name            = "tesp-frontend"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.frontend.arn
  desired_count   = 2
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = aws_subnet.private_app[*].id
    security_groups  = [aws_security_group.ecs_tasks.id]
    assign_public_ip = false
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.frontend.arn
    container_name   = "frontend"
    container_port   = 80
  }
}

# --- 2. API GATEWAY SERVICE ---
resource "aws_ecs_task_definition" "api_gateway" {
  family                   = "tesp-api-gateway"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "1024" # 1.0 vCPU
  memory                   = "2048" # 2 GB
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn
  task_role_arn            = aws_iam_role.ecs_task_role.arn

  container_definitions = jsonencode([{
    name      = "api-gateway"
    image     = "${aws_ecr_repository.repo["api-gateway"].repository_url}:${var.image_tag}"
    essential = true
    portMappings = [{
      containerPort = 8080
      hostPort      = 8080
    }]
    environment = [
      { name = "SPRING_PROFILES_ACTIVE", value = "prod" },
      { name = "CORS_ALLOWED_ORIGINS", value = "https://${var.frontend_domain}" },
      { name = "PROJECT_CONFIG_SERVICE_URI", value = "http://project-config-service.tesp.local:8081" },
      { name = "ORGANIZATION_SERVICE_URI", value = "http://organization-service.tesp.local:8082" },
      { name = "EMPLOYEE_SERVICE_URI", value = "http://employee-service.tesp.local:8083" },
      { name = "SURVEY_BUILDER_SERVICE_URI", value = "http://survey-builder-service.tesp.local:8084" },
      { name = "SURVEY_DISTRIBUTION_SERVICE_URI", value = "http://survey-distribution-service.tesp.local:8085" },
      { name = "RESPONSE_INGESTION_SERVICE_URI", value = "http://response-ingestion-service.tesp.local:8086" },
      { name = "ANALYTICS_ENGINE_SERVICE_URI", value = "http://analytics-engine-service.tesp.local:8087" },
      { name = "AI_ANALYTICS_SERVICE_URI", value = "http://ai-analytics-service.tesp.local:8088" },
      { name = "REPORTING_SERVICE_URI", value = "http://reporting-service.tesp.local:8089" },
      { name = "ACTION_PLANNING_SERVICE_URI", value = "http://action-planning-service.tesp.local:8090" },
      { name = "NOTIFICATION_SERVICE_URI", value = "http://notification-service.tesp.local:8091" },
      { name = "AUDIT_SERVICE_URI", value = "http://audit-service.tesp.local:8092" }
    ]
    secrets = [
      { name = "REDIS_HOST", valueFrom = aws_secretsmanager_secret.redis_host.arn }
    ]
    logConfiguration = {
      logDriver = "awslogs"
      options = {
        "awslogs-group"         = "/ecs/tesp-api-gateway"
        "awslogs-region"        = var.aws_region
        "awslogs-stream-prefix" = "ecs"
      }
    }
  }])
}

resource "aws_ecs_service" "api_gateway" {
  name            = "tesp-api-gateway"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.api_gateway.arn
  desired_count   = 3
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = aws_subnet.private_app[*].id
    security_groups  = [aws_security_group.ecs_tasks.id]
    assign_public_ip = false
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.api_gateway.arn
    container_name   = "api-gateway"
    container_port   = 8080
  }
}
