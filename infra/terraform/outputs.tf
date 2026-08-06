output "ecs_cluster_name" {
  value       = aws_ecs_cluster.main.name
  description = "Name of the provisioned ECS cluster"
}
