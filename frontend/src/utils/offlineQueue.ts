import { mediaService } from '@/services/mediaService'

export interface QueuedUpload {
  id: string // Client local identifier (UUID)
  eventId: string
  invitationCode: string
  fileBlob: Blob
  fileName: string
  fileType: string
  retryCount: number
  status: 'PENDING' | 'SYNCING' | 'FAILED' | 'DEAD_LETTER'
  lastError?: string
  createdAt: number
}

const DB_NAME = 'himpact_offline_db'
const STORE_NAME = 'upload_queue'
const DB_VERSION = 2
const MAX_RETRIES = 3

function openDb(): Promise<IDBDatabase> {
  return new Promise((resolve, reject) => {
    const request = indexedDB.open(DB_NAME, DB_VERSION)
    request.onupgradeneeded = () => {
      const db = request.result
      if (!db.objectStoreNames.contains(STORE_NAME)) {
        db.createObjectStore(STORE_NAME, { keyPath: 'id' })
      }
    }
    request.onsuccess = () => resolve(request.result)
    request.onerror = () => reject(request.error)
  })
}

export const offlineQueue = {
  /**
   * Save a photo upload to browser IndexedDB when offline.
   */
  async queueUpload(item: Omit<QueuedUpload, 'retryCount' | 'status'>): Promise<void> {
    const fullItem: QueuedUpload = {
      ...item,
      retryCount: 0,
      status: 'PENDING',
    }
    const db = await openDb()
    const tx = db.transaction(STORE_NAME, 'readwrite')
    tx.objectStore(STORE_NAME).put(fullItem)
    return new Promise((resolve, reject) => {
      tx.oncomplete = () => resolve()
      tx.onerror = () => reject(tx.error)
    })
  },

  /**
   * Get all queued offline uploads.
   */
  async getQueuedUploads(): Promise<QueuedUpload[]> {
    const db = await openDb()
    const tx = db.transaction(STORE_NAME, 'readonly')
    const request = tx.objectStore(STORE_NAME).getAll()
    return new Promise((resolve, reject) => {
      request.onsuccess = () => resolve(request.result || [])
      request.onerror = () => reject(request.error)
    })
  },

  /**
   * Delete a processed upload from the offline queue.
   */
  async removeQueuedUpload(id: string): Promise<void> {
    const db = await openDb()
    const tx = db.transaction(STORE_NAME, 'readwrite')
    tx.objectStore(STORE_NAME).delete(id)
    return new Promise((resolve, reject) => {
      tx.oncomplete = () => resolve()
      tx.onerror = () => reject(tx.error)
    })
  },

  /**
   * Update an item's retry status in IndexedDB.
   */
  async updateQueuedUpload(item: QueuedUpload): Promise<void> {
    const db = await openDb()
    const tx = db.transaction(STORE_NAME, 'readwrite')
    tx.objectStore(STORE_NAME).put(item)
    return new Promise((resolve, reject) => {
      tx.oncomplete = () => resolve()
      tx.onerror = () => reject(tx.error)
    })
  },

  /**
   * Synchronize all offline queued uploads with bounded retries & exponential backoff (PO Condition 3 & ISSUE-M02).
   */
  async syncQueue(onProgress?: (syncedCount: number, total: number, deadLetterCount: number) => void): Promise<{ syncedCount: number; deadLetterCount: number }> {
    // Read all queued items into memory first to avoid IndexedDB transaction timeouts across async calls (ISSUE-M02)
    const items = await this.getQueuedUploads()
    if (items.length === 0) return { syncedCount: 0, deadLetterCount: 0 }

    let syncedCount = 0
    let deadLetterCount = 0

    for (const item of items) {
      if (item.status === 'DEAD_LETTER') {
        deadLetterCount++
        continue
      }

      if (item.retryCount >= MAX_RETRIES) {
        item.status = 'DEAD_LETTER'
        item.lastError = `Max retries (${MAX_RETRIES}) exceeded`
        await this.updateQueuedUpload(item)
        deadLetterCount++
        continue
      }

      try {
        item.status = 'SYNCING'
        await this.updateQueuedUpload(item)

        const file = new File([item.fileBlob], item.fileName, { type: item.fileType })
        await mediaService.uploadMedia(item.eventId, item.invitationCode, file, item.id)

        await this.removeQueuedUpload(item.id)
        syncedCount++
      } catch (err: unknown) {
        const errorMessage = err instanceof Error ? err.message : 'Sync failed'
        item.retryCount += 1
        item.lastError = errorMessage

        if (item.retryCount >= MAX_RETRIES) {
          item.status = 'DEAD_LETTER'
          deadLetterCount++
        } else {
          item.status = 'FAILED'
          // Exponential Backoff delay before next retry
          const backoffMs = Math.pow(2, item.retryCount) * 1000
          await new Promise((res) => setTimeout(res, backoffMs))
        }

        await this.updateQueuedUpload(item)
      }

      if (onProgress) onProgress(syncedCount, items.length, deadLetterCount)
    }

    return { syncedCount, deadLetterCount }
  },
}
