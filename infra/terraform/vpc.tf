# AWS VPC & Networking Architecture for TESP

data "aws_availability_zones" "available" {
  state = "available"
}

resource "aws_vpc" "main" {
  cidr_block           = var.vpc_cidr
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = {
    Name        = "tesp-vpc-${var.environment}"
    Environment = var.environment
  }
}

# Public Subnets (3 AZs)
resource "aws_subnet" "public" {
  count                   = 3
  vpc_id                  = aws_vpc.main.id
  cidr_block              = cidrsubnet(var.vpc_cidr, 4, count.index) # 10.0.0.0/20, 10.0.16.0/20, 10.0.32.0/20
  availability_zone       = data.aws_availability_zones.available.names[count.index]
  map_public_ip_on_launch = true

  tags = {
    Name        = "tesp-public-subnet-${count.index + 1}-${var.environment}"
    Type        = "Public"
    Environment = var.environment
  }
}

# Private Application Subnets (3 AZs - for ECS Fargate tasks)
resource "aws_subnet" "private_app" {
  count             = 3
  vpc_id            = aws_vpc.main.id
  cidr_block        = cidrsubnet(var.vpc_cidr, 4, count.index + 4) # 10.0.64.0/20, 10.0.80.0/20, 10.0.96.0/20
  availability_zone = data.aws_availability_zones.available.names[count.index]

  tags = {
    Name        = "tesp-private-app-subnet-${count.index + 1}-${var.environment}"
    Type        = "Private-App"
    Environment = var.environment
  }
}

# Private Data Subnets (3 AZs - for ElastiCache Redis & MSK Kafka)
resource "aws_subnet" "private_data" {
  count             = 3
  vpc_id            = aws_vpc.main.id
  cidr_block        = cidrsubnet(var.vpc_cidr, 4, count.index + 8) # 10.0.128.0/20, 10.0.144.0/20, 10.0.160.0/20
  availability_zone = data.aws_availability_zones.available.names[count.index]

  tags = {
    Name        = "tesp-private-data-subnet-${count.index + 1}-${var.environment}"
    Type        = "Private-Data"
    Environment = var.environment
  }
}

# Internet Gateway
resource "aws_internet_gateway" "gw" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name        = "tesp-igw-${var.environment}"
    Environment = var.environment
  }
}

# NAT Gateways & Elastic IPs for Private Subnets Egress
resource "aws_eip" "nat" {
  count  = var.environment == "prod" ? 3 : 1
  domain = "vpc"

  tags = {
    Name = "tesp-nat-eip-${count.index + 1}-${var.environment}"
  }
}

resource "aws_nat_gateway" "nat" {
  count         = var.environment == "prod" ? 3 : 1
  allocation_id = aws_eip.nat[count.index].id
  subnet_id     = aws_subnet.public[count.index].id

  tags = {
    Name = "tesp-nat-gw-${count.index + 1}-${var.environment}"
  }

  depends_on = [aws_internet_gateway.gw]
}

# Public Route Table
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.gw.id
  }

  tags = {
    Name = "tesp-public-rt-${var.environment}"
  }
}

resource "aws_route_table_association" "public" {
  count          = 3
  subnet_id      = aws_subnet.public[count.index].id
  route_table_id = aws_route_table.public.id
}

# Private Route Tables
resource "aws_route_table" "private" {
  count  = var.environment == "prod" ? 3 : 1
  vpc_id = aws_vpc.main.id

  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.nat[count.index].id
  }

  tags = {
    Name = "tesp-private-rt-${count.index + 1}-${var.environment}"
  }
}

resource "aws_route_table_association" "private_app" {
  count          = 3
  subnet_id      = aws_subnet.private_app[count.index].id
  route_table_id = aws_route_table.private[var.environment == "prod" ? count.index : 0].id
}

resource "aws_route_table_association" "private_data" {
  count          = 3
  subnet_id      = aws_subnet.private_data[count.index].id
  route_table_id = aws_route_table.private[var.environment == "prod" ? count.index : 0].id
}
