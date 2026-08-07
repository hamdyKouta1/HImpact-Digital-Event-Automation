# Enterprise Production Deployment & Infrastructure Operations Guide

This guide details the complete production deployment architecture, cloud provider options, infrastructure sizing, security hardening, zero-downtime deployment strategies, disaster recovery, monitoring, and maintenance procedures for **HImpact Digital Event Automation**.

---

## 1. Production Topology & Architecture

The application is deployed using a **Containerized Micro-Monolith Topology** behind an Nginx Reverse Proxy with TLS termination, database replication, and persistent cloud volume backups.

```mermaid
graph TD
    User([Public Users & Event Owners]) -->|HTTPS 443| Cloudflare[Cloudflare DNS / WAF / DDoS Protection]
    Cloudflare -->|HTTPS 443| Nginx[Nginx Reverse Proxy & SSL Termination]

    subgraph Docker Host Environment / Cloud VPS
        Nginx -->|HTTP 8080| Backend1[Spring Boot API Instance 1]
        Nginx -->|HTTP 8080| Backend2[Spring Boot API Instance 2]
        Nginx -->|Static Assets| Frontend[Nginx Static SPA Container]

        Backend1 -->|JDBC Port 5432| DB Primary[(PostgreSQL 16 Primary)]
        Backend2 -->|JDBC Port 5432| DB Primary
        DB Primary -->|WAL Replication| DB Replica[(PostgreSQL Standby Replica)]

        Backend1 -->|Actuator / Metrics| Prometheus[Prometheus Monitoring]
        Backend2 -->|Actuator / Metrics| Prometheus
        Prometheus --> Grafana[Grafana Dashboards]
    end

    Backend1 -->|Google Drive API v3| GDrive[Google Drive Cloud Storage]
    Backend2 -->|Google Drive API v3| GDrive
    Backend1 -->|SMTP Port 587| Mail[SMTP Email Provider]
    Backend2 -->|SMTP Port 587| Mail
```

---

## 2. Recommended Cloud Providers & Monthly Infrastructure Costs

### 2.1 Provider Options Comparison

| Provider | Recommended Tier | Configuration | Estimated Monthly Cost | Best For |
|---|---|---|---|---|
| **Hetzner Cloud (Recommended)** | CPX31 / CPX41 | 4 vCPU, 8 GB RAM, 160 GB NVMe | **$14 – $25 / mo** | Maximum performance/cost ratio in Europe/US. |
| **DigitalOcean** | Premium Droplet | 4 vCPU, 8 GB RAM, 160 GB SSD | **$48 / mo** | Ease of management, managed PostgreSQL options. |
| **AWS (Amazon Web Services)** | t4g.xlarge / EC2 + RDS | 4 vCPU ARM64, 16 GB RAM, RDS Postgres | **$120 – $180 / mo** | Enterprise compliance, autoscaling, IAM integration. |
| **Azure** | Standard_D4s_v5 | 4 vCPU, 16 GB RAM, Managed Postgres | **$140 – $200 / mo** | Enterprise Active Directory & Microsoft stack integration. |
| **Oracle Cloud (OCI)** | Ampere A1 (Always Free) | 4 vCPU ARM, 24 GB RAM, 200 GB Storage | **$0 / mo (Free Tier)** | Lowest cost starting deployment. |
| **PaaS (Railway / Render / Coolify)** | Managed App Service | 2 vCPU, 4 GB RAM, Managed DB | **$25 – $50 / mo** | Zero-DevOps git-push deployments. |

### 2.2 Recommended VPS Sizing Matrix

| Traffic Scale | Active Events / Mo | vCPU | RAM | NVMe SSD Storage | Bandwidth |
|---|---|---|---|---|---|
| **Starter / MVP** | 1 – 20 events | 2 vCPU | 4 GB | 50 GB | 2 TB |
| **Production Scale** | 20 – 100 events | 4 vCPU | 8 GB | 160 GB | 5 TB |
| **High-Volume Enterprise** | 100+ events | 8 vCPU | 16 GB | 320 GB | 10 TB |

---

## 3. Network, Firewall & Security Hardening

### 3.1 Firewall Rules (UFW / Cloud Security Groups)

Only ports 22 (SSH), 80 (HTTP), and 443 (HTTPS) should be exposed publicly. Database and backend internal ports must remain strictly isolated inside the Docker network.

```bash
# Reset UFW rules
sudo ufw default deny incoming
sudo ufw default allow outgoing

# Allow essential ports
sudo ufw allow 22/tcp    # SSH Access (Restrict to Admin IP in production)
sudo ufw allow 80/tcp    # HTTP (Redirects to HTTPS)
sudo ufw allow 443/tcp   # HTTPS (Nginx Proxy)

# Enable UFW
sudo ufw enable
```

### 3.2 SSL / TLS Certificate Automation (Let's Encrypt / Certbot)

```bash
sudo apt update && sudo apt install -y certbot python3-certbot-nginx
sudo certbot --nginx -d app.himpact.com -d api.himpact.com
```

Certbot automatically configures TLS 1.3 encryption, HTTP-to-HTTPS redirection, and sets up a systemd auto-renewal timer.

---

## 4. Database Administration: Backups & Disaster Recovery

### 4.1 Automated Daily PostgreSQL Backups (`pg_dump`)

Create a backup script `/opt/himpact/scripts/backup-db.sh`:

```bash
#!/usr/bin/env bash
set -e

BACKUP_DIR="/var/backups/himpact"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="${BACKUP_DIR}/HIMPACT_DB_prod_${TIMESTAMP}.sql.gz"

mkdir -p "${BACKUP_DIR}"

# Execute compressed database dump
docker exec -t himpact-prod-postgres pg_dump -U himpact_prod -d HIMPACT_DB | gzip > "${BACKUP_FILE}"

# Retain backups for 30 days
find "${BACKUP_DIR}" -type f -name "*.sql.gz" -mtime +30 -delete

echo "Database backup completed: ${BACKUP_FILE}"
```

Make executable and add to crontab for 2:00 AM daily execution:

```bash
chmod +x /opt/himpact/scripts/backup-db.sh
(crontab -l 2>/dev/null; echo "0 2 * * * /opt/himpact/scripts/backup-db.sh >> /var/log/himpact-backup.log 2>&1") | crontab -
```

### 4.2 Database Disaster Recovery & Restore Procedure

To restore the database from a backup file:

```bash
# 1. Stop backend services to prevent incoming writes
docker compose -f docker-compose.prod.yml stop backend proxy

# 2. Drop existing database and restore from backup
gunzip -c /var/backups/himpact/HIMPACT_DB_prod_20260731_020000.sql.gz | docker exec -i himpact-prod-postgres psql -U himpact_prod -d HIMPACT_DB

# 3. Restart application containers
docker compose -f docker-compose.prod.yml start backend proxy
```

---

## 5. Monitoring & Observability Stack

The platform exposes native observability endpoints monitored via **Prometheus** and visualised in **Grafana**.

### 5.1 Prometheus Configuration (`prometheus.yml`)

```yaml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'himpact-backend'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['backend:8080']
```

### 5.2 Key Grafana Alerting Thresholds

- **JVM Heap Memory Usage**: Alert when > 85% for 5 minutes.
- **HikariCP Connection Pool Exhaustion**: Alert when pending connections > 5.
- **HTTP 5xx Error Rate**: Alert when 5xx responses exceed 1% of total requests over 5 minutes.
- **Disk Usage**: Alert when persistent storage disk free space < 15%.

---

## 6. Zero-Downtime Deployment & Rollback Strategy

### 6.1 Rolling Green/Blue Docker Deployment

Use the deployment script `/opt/himpact/scripts/deploy.sh`:

```bash
#!/usr/bin/env bash
set -e

echo "=== Pulling Latest Source Code & Rebuilding Images ==="
git pull origin main

# Build new images without taking down running production services
docker compose -f docker-compose.prod.yml build --no-cache

echo "=== Executing Flyway Schema Migrations ==="
docker compose -f docker-compose.prod.yml run --rm backend java -jar app.jar --spring.profiles.active=prod --flyway.migrate

echo "=== Performing Rolling Restart of Containers ==="
docker compose -f docker-compose.prod.yml up -d --no-deps --scale backend=2 backend
sleep 10
docker compose -f docker-compose.prod.yml up -d --no-deps --scale backend=1 backend
docker compose -f docker-compose.prod.yml exec proxy nginx -s reload

echo "=== Production Deployment Completed Successfully ==="
```

### 6.2 Emergency Rollback Procedure

If a deployment introduces critical bugs, execute the rollback script:

```bash
# Rollback git commit to previous stable tag
git checkout tags/v1.0.4

# Rebuild and restart application
docker compose -f docker-compose.prod.yml up -d --build --force-recreate
```

---

## 7. Performance Tuning & JVM Optimizations

### 7.1 JVM Garbage Collection Options (G1GC)

Set environment variables in `docker-compose.prod.yml` for optimal memory management:

```yaml
JAVA_OPTS: "-Xms1024m -Xmx2048m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseStringDeduplication"
```

### 7.2 PostgreSQL Production Parameter Tuning (`postgresql.conf`)

```ini
max_connections = 100
shared_buffers = 2GB            # 25% of total RAM
effective_cache_size = 6GB      # 75% of total RAM
maintenance_work_mem = 512MB
work_mem = 16MB
wal_buffers = 16MB
random_page_cost = 1.1          # Optimized for NVMe/SSD storage
```

---

## 8. Go-Live Security & Production Checklist

### Pre-Launch Verification Checklist
- [ ] **JWT Secret**: Environment variable `JWT_SECRET` set to unique 64-character random key.
- [ ] **Database Passwords**: Default passwords replaced with strong generated credentials.
- [ ] **CORS Origins**: Restricted strictly to production domains (`https://himpact.app`).
- [ ] **HTTPS / SSL**: SSL certificate active with automatic renewal configured.
- [ ] **Actuator Exposure**: Actuator endpoints restricted behind firewall/internal access.
- [ ] **Google Drive Credentials**: Service account key uploaded and verified.
- [ ] **SMTP / Mail**: Production mail gateway tested for event notifications.
- [ ] **Database Backups**: Cron backup script verified with successful restore test.
- [ ] **Monitoring**: Prometheus and Grafana alerting connected to Slack/Email.

---

## 9. Incident Response Procedure

1. **Severity 1 (System Down / DB Corruption)**:
   - Check `docker compose logs -f --tail=100 backend`.
   - Inspect `/actuator/health` and disk usage (`df -h`).
   - If DB corrupted, execute restore procedure from latest daily backup.
2. **Severity 2 (High Latency / Memory Leak)**:
   - Monitor JVM heap via Grafana.
   - Restart backend container instance (`docker restart himpact-prod-backend`).
   - Collect heap dump for post-mortem analysis.
