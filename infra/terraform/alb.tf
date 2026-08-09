# AWS Application Load Balancer (ALB) and Listeners for TESP

resource "aws_lb" "main" {
  name               = "tesp-alb-${var.environment}"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb.id]
  subnets            = aws_subnet.public[*].id

  enable_deletion_protection = var.environment == "prod" ? true : false

  tags = {
    Name        = "tesp-alb-${var.environment}"
    Environment = var.environment
  }
}

# Target Group: API Gateway (Port 8080)
resource "aws_lb_target_group" "api_gateway" {
  name        = "tesp-tg-api-gateway-${var.environment}"
  port        = 8080
  protocol    = "HTTP"
  vpc_id      = aws_vpc.main.id
  target_type = "ip"

  health_check {
    enabled             = true
    path                = "/actuator/health"
    port                = "8080"
    protocol            = "HTTP"
    healthy_threshold   = 2
    unhealthy_threshold = 5
    timeout             = 5
    interval            = 15
    matcher             = "200"
  }

  tags = {
    Name        = "tesp-tg-api-gateway-${var.environment}"
    Environment = var.environment
  }
}

# Target Group: Frontend SPA (Port 80)
resource "aws_lb_target_group" "frontend" {
  name        = "tesp-tg-frontend-${var.environment}"
  port        = 80
  protocol    = "HTTP"
  vpc_id      = aws_vpc.main.id
  target_type = "ip"

  health_check {
    enabled             = true
    path                = "/"
    port                = "80"
    protocol            = "HTTP"
    healthy_threshold   = 2
    unhealthy_threshold = 5
    timeout             = 5
    interval            = 15
    matcher             = "200-399"
  }

  tags = {
    Name        = "tesp-tg-frontend-${var.environment}"
    Environment = var.environment
  }
}

# HTTP Listener (Port 80) - Redirects HTTP to HTTPS
resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.main.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type = "redirect"

    redirect {
      port        = "443"
      protocol    = "HTTPS"
      status_code = "HTTP_301"
    }
  }
}

# HTTPS Listener (Port 443)
resource "aws_lb_listener" "https" {
  count             = var.acm_certificate_arn != "" ? 1 : 0
  load_balancer_arn = aws_lb.main.arn
  port              = "443"
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-TLS13-1-2-2021-06"
  certificate_arn   = var.acm_certificate_arn

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.frontend.arn
  }
}

# Rule 1: Route tesp-api.talnova.io to API Gateway Target Group
resource "aws_lb_listener_rule" "api_routing" {
  count        = var.acm_certificate_arn != "" ? 1 : 0
  listener_arn = aws_lb_listener.https[0].arn
  priority     = 10

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.api_gateway.arn
  }

  condition {
    host_header {
      values = [var.api_domain]
    }
  }
}

# Rule 2: Route tesp.talnova.io to Frontend SPA Target Group
resource "aws_lb_listener_rule" "frontend_routing" {
  count        = var.acm_certificate_arn != "" ? 1 : 0
  listener_arn = aws_lb_listener.https[0].arn
  priority     = 20

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.frontend.arn
  }

  condition {
    host_header {
      values = [var.frontend_domain]
    }
  }
}
