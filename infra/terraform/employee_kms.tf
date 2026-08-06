# AWS KMS Customer Master Key (CMK) for TESP Employee PII Client-Side Field Level Encryption (CSFLE)

resource "aws_kms_key" "tesp_csfle_key" {
  description             = "TESP Employee PII Client-Side Field Level Encryption (CSFLE) Key"
  deletion_window_in_days = 30
  enable_key_rotation     = true

  tags = {
    Environment = var.environment
    Service     = "employee-service"
    ManagedBy   = "Terraform"
  }
}

resource "aws_kms_alias" "tesp_csfle_key_alias" {
  name          = "alias/tesp-csfle-key-${var.environment}"
  target_key_id = aws_kms_key.tesp_csfle_key.key_id
}

output "csfle_kms_key_arn" {
  description = "ARN of the AWS KMS key used for CSFLE employee PII encryption"
  value       = aws_kms_key.tesp_csfle_key.arn
}
