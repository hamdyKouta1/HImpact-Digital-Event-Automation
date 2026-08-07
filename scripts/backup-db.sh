#!/bin/bash
# HImpact Production PostgreSQL Automated Backup Script
# Usage: ./scripts/backup-db.sh

set -e

BACKUP_DIR="/var/backups/himpact"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="${BACKUP_DIR}/HIMPACT_DB_prod_${TIMESTAMP}.sql.gz"
RETENTION_DAYS=30

mkdir -p "${BACKUP_DIR}"

echo "[$(date)] Starting PostgreSQL production database backup..."

docker exec -t himpact-prod-postgres pg_dump -U himpact_prod HIMPACT_DB | gzip > "${BACKUP_FILE}"

echo "[$(date)] Backup completed successfully: ${BACKUP_FILE}"
echo "[$(date)] File size: $(du -h ${BACKUP_FILE} | cut -f1)"

# Purge backups older than RETENTION_DAYS
echo "[$(date)] Purging backups older than ${RETENTION_DAYS} days..."
find "${BACKUP_DIR}" -type f -name "HIMPACT_DB_prod_*.sql.gz" -mtime +${RETENTION_DAYS} -delete

echo "[$(date)] Backup process complete."
