#!/bin/bash
# HImpact Production Database Disaster Recovery Restore Script
# Usage: ./scripts/restore-db.sh /path/to/himpact_prod_YYYYMMDD_HHMMSS.sql.gz

set -e

if [ -z "$1" ]; then
  echo "Error: Please specify target backup file (.sql.gz)"
  echo "Usage: ./scripts/restore-db.sh /path/to/himpact_prod_20260731.sql.gz"
  exit 1
fi

BACKUP_FILE="$1"

if [ ! -f "${BACKUP_FILE}" ]; then
  echo "Error: Backup file not found at ${BACKUP_FILE}"
  exit 1
fi

echo "[$(date)] WARNING: Restoring database will overwrite current data!"
read -p "Are you sure you want to proceed with restore? (y/N) " confirm

if [ "$confirm" != "y" ] && [ "$confirm" != "Y" ]; then
  echo "Restore aborted by user."
  exit 0
fi

echo "[$(date)] Restoring database from ${BACKUP_FILE}..."

gunzip -c "${BACKUP_FILE}" | docker exec -i himpact-prod-postgres psql -U himpact -d himpact_prod

echo "[$(date)] Disaster recovery restore completed successfully!"
