# Terraform Provisioning for Project Configuration Service & White-Label Tenant Assets

resource "aws_s3_bucket" "tenant_assets" {
  bucket = "tesp-tenant-assets-${var.environment}"

  tags = {
    Name        = "TESP Tenant Branding Assets"
    Environment = var.environment
    Service     = "project-config-service"
    ManagedBy   = "Terraform"
  }
}

resource "aws_s3_bucket_ownership_controls" "tenant_assets_ownership" {
  bucket = aws_s3_bucket.tenant_assets.id

  rule {
    object_ownership = "BucketOwnerPreferred"
  }
}

resource "aws_s3_bucket_public_access_block" "tenant_assets_public_block" {
  bucket = aws_s3_bucket.tenant_assets.id

  block_public_acls       = false
  block_public_policy     = false
  ignore_public_acls      = false
  restrict_public_buckets = false
}

resource "aws_s3_bucket_cors_configuration" "tenant_assets_cors" {
  bucket = aws_s3_bucket.tenant_assets.id

  cors_rule {
    allowed_headers = ["*"]
    allowed_methods = ["GET", "HEAD"]
    allowed_origins = ["*"]
    max_age_seconds = 3600
  }
}

resource "aws_cloudfront_origin_access_identity" "oai" {
  comment = "OAI for TESP Tenant Assets S3 distribution"
}

resource "aws_cloudfront_distribution" "tenant_assets_cdn" {
  origin {
    domain_name = aws_s3_bucket.tenant_assets.bucket_regional_domain_name
    origin_id   = "S3-tesp-tenant-assets-${var.environment}"

    s3_origin_config {
      origin_access_identity = aws_cloudfront_origin_access_identity.oai.cloudfront_access_identity_path
    }
  }

  enabled             = true
  is_ipv6_enabled     = true
  default_root_object = "index.html"

  default_cache_behavior {
    allowed_methods  = ["GET", "HEAD", "OPTIONS"]
    cached_methods   = ["GET", "HEAD"]
    target_origin_id = "S3-tesp-tenant-assets-${var.environment}"

    forwarded_values {
      query_string = false
      headers      = ["Origin", "Access-Control-Request-Headers", "Access-Control-Request-Method"]

      cookies {
        forward = "none"
      }
    }

    viewer_protocol_policy = "redirect-to-https"
    min_ttl                = 0
    default_ttl            = 3600
    max_ttl                = 86400
  }

  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }

  viewer_certificate {
    cloudfront_default_certificate = true
  }

  tags = {
    Environment = var.environment
    Service     = "project-config-service"
  }
}
