import { useState, useEffect } from 'react'
import { mediaService } from '@/services/mediaService'
import { offlineQueue } from '@/utils/offlineQueue'

interface MediaUploaderProps {
  eventId: string
  invitationCode: string
  uploadLimit: number
  uploadedCount: number
  onUploadSuccess: () => void
}

/**
 * Drag-and-drop & Camera Photo Picker Component.
 * Supports online direct uploads with progress monitoring AND offline IndexedDB queueing.
 *
 * See: project-index/08_UI_UX_Specification.md — Media Upload Experience
 */
export function MediaUploader({
  eventId,
  invitationCode,
  uploadLimit,
  uploadedCount,
  onUploadSuccess,
}: MediaUploaderProps) {
  const [isOnline, setIsOnline] = useState(navigator.onLine)
  const [selectedFile, setSelectedFile] = useState<File | null>(null)
  const [progress, setProgress] = useState(0)
  const [isUploading, setIsUploading] = useState(false)
  const [message, setMessage] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)

  // Track online/offline status & trigger auto-sync on reconnect
  useEffect(() => {
    const handleOnline = async () => {
      setIsOnline(true)
      const { syncedCount, deadLetterCount } = await offlineQueue.syncQueue()
      if (syncedCount > 0) {
        setMessage(`Online again! Successfully synced ${syncedCount} queued photos.`)
        onUploadSuccess()
      }
      if (deadLetterCount > 0) {
        setError(`${deadLetterCount} photo(s) failed after ${3} retries and require manual upload.`)
      }
    }
    const handleOffline = () => setIsOnline(false)

    window.addEventListener('online', handleOnline)
    window.addEventListener('offline', handleOffline)
    return () => {
      window.removeEventListener('online', handleOnline)
      window.removeEventListener('offline', handleOffline)
    }
  }, [onUploadSuccess])

  const remainingQuota = Math.max(0, uploadLimit - uploadedCount)

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setSelectedFile(e.target.files[0])
      setError(null)
      setMessage(null)
    }
  }

  const handleUpload = async () => {
    if (!selectedFile) return
    if (remainingQuota <= 0) {
      setError(`Upload limit reached (${uploadLimit} photos).`)
      return
    }

    setIsUploading(true)
    setProgress(0)
    setError(null)
    setMessage(null)

    try {
      if (isOnline) {
        // Direct Online Upload
        await mediaService.uploadMedia(
          eventId,
          invitationCode,
          selectedFile,
          undefined,
          (percent) => setProgress(percent)
        )
        setMessage('Photo uploaded successfully! ✨')
        setSelectedFile(null)
        onUploadSuccess()
      } else {
        // Offline IndexedDB Queueing
        const clientUuid = crypto.randomUUID()
        await offlineQueue.queueUpload({
          id: clientUuid,
          eventId,
          invitationCode,
          fileBlob: selectedFile,
          fileName: selectedFile.name,
          fileType: selectedFile.type,
          createdAt: Date.now(),
        })
        setMessage('Saved to offline queue! Will auto-upload when reconnected. 📲')
        setSelectedFile(null)
      }
    } catch (err: unknown) {
      const errMessage = err instanceof Error ? err.message : 'Upload failed. Please try again.'
      setError(errMessage)
    } finally {
      setIsUploading(false)
    }
  }

  return (
    <div className="card space-y-4">
      {/* Offline Alert Banner */}
      {!isOnline && (
        <div className="p-3 rounded-lg bg-amber-500/10 border border-amber-500/20 text-amber-400 text-xs flex items-center gap-2">
          <span>📶</span>
          <span>You are currently offline. Photos will be saved locally and auto-uploaded when reconnected.</span>
        </div>
      )}

      {/* Quota Progress Header */}
      <div className="flex items-center justify-between text-sm">
        <span className="font-semibold text-white">Upload Photos & Videos</span>
        <span className="text-xs text-slate-400">
          Quota: <strong className="text-primary-400">{uploadedCount}</strong> / {uploadLimit}
        </span>
      </div>

      {/* File Dropzone / Picker */}
      <div className="border-2 border-dashed border-white/10 hover:border-primary-500/50 rounded-2xl p-6 text-center transition-all duration-200">
        <input
          id="media-file-input"
          type="file"
          accept="image/*,video/*"
          className="hidden"
          onChange={handleFileChange}
          disabled={isUploading || remainingQuota <= 0}
        />
        <label htmlFor="media-file-input" className="cursor-pointer space-y-2 block">
          <div className="w-12 h-12 bg-primary-500/10 rounded-full flex items-center justify-center mx-auto text-primary-400 text-xl">
            📷
          </div>
          {selectedFile ? (
            <p className="text-sm font-semibold text-white truncate max-w-xs mx-auto">
              Selected: {selectedFile.name} ({(selectedFile.size / (1024 * 1024)).toFixed(2)} MB)
            </p>
          ) : (
            <>
              <p className="text-sm font-semibold text-white">Click or tap to choose a photo/video</p>
              <p className="text-xs text-slate-500">Supports JPG, PNG, WEBP, MP4 (max 50 MB)</p>
            </>
          )}
        </label>
      </div>

      {/* Upload Progress Bar */}
      {isUploading && (
        <div className="space-y-1">
          <div className="h-2 w-full bg-white/10 rounded-full overflow-hidden">
            <div
              className="h-full bg-primary-500 transition-all duration-200"
              style={{ width: `${progress}%` }}
            />
          </div>
          <p className="text-xs text-slate-400 text-right font-mono">{progress}%</p>
        </div>
      )}

      {/* Messages */}
      {message && <div className="p-3 rounded-lg bg-green-500/10 text-green-400 text-xs">{message}</div>}
      {error && <div className="p-3 rounded-lg bg-red-500/10 text-red-400 text-xs">{error}</div>}

      {/* Submit Button */}
      {selectedFile && (
        <button
          onClick={handleUpload}
          disabled={isUploading || remainingQuota <= 0}
          className="btn-primary w-full"
        >
          {isUploading ? 'Uploading…' : isOnline ? 'Upload Photo Now ✨' : 'Queue for Offline Sync 📲'}
        </button>
      )}
    </div>
  )
}
