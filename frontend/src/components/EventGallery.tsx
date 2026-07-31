import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { mediaService, type MediaFileItem } from '@/services/mediaService'

interface EventGalleryProps {
  eventId: string
  canDelete?: boolean
}

/**
 * Event Photo Gallery Grid component.
 * Displays uploaded photos with guest uploader attribution and lightbox preview modal.
 *
 * See: project-index/08_UI_UX_Specification.md — Gallery View
 */
export function EventGallery({ eventId, canDelete = false }: EventGalleryProps) {
  const queryClient = useQueryClient()
  const [selectedPhoto, setSelectedPhoto] = useState<MediaFileItem | null>(null)

  const { data: galleryData, isLoading } = useQuery({
    queryKey: ['gallery', eventId],
    queryFn: () => mediaService.getEventGallery(eventId),
    enabled: Boolean(eventId),
  })

  const deleteMutation = useMutation({
    mutationFn: (mediaId: string) => mediaService.deleteMedia(mediaId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['gallery', eventId] })
      setSelectedPhoto(null)
    },
  })

  return (
    <div className="card space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-xl font-bold text-white">Event Gallery 📸</h2>
          <p className="text-slate-400 text-xs mt-0.5">Memories shared by event guests.</p>
        </div>
        <span className="text-xs text-slate-400">
          Total: <strong className="text-white">{galleryData?.totalElements ?? 0}</strong>
        </span>
      </div>

      {isLoading && <div className="p-8 text-center text-slate-400 text-sm">Loading event photos…</div>}

      {galleryData && galleryData.content.length === 0 && (
        <div className="text-center py-12 text-slate-400 text-sm">
          No photos uploaded yet. Be the first to share a moment!
        </div>
      )}

      {/* Responsive Grid */}
      {galleryData && galleryData.content.length > 0 && (
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3">
          {galleryData.content.map((item) => (
            <div
              key={item.id}
              onClick={() => setSelectedPhoto(item)}
              className="group relative aspect-square rounded-xl overflow-hidden bg-white/5 cursor-pointer border border-white/5 hover:border-primary-500/50 transition-all duration-200"
            >
              {/* Local dev file path preview stub or image placeholder */}
              <div className="w-full h-full flex flex-col items-center justify-center p-2 text-center bg-gradient-to-br from-surface to-surface-dark group-hover:scale-105 transition-transform duration-200">
                <span className="text-2xl mb-1">🖼️</span>
                <span className="text-xs text-slate-300 truncate w-full font-medium">{item.originalFilename}</span>
                <span className="text-[10px] text-primary-400 truncate w-full">by {item.guestName}</span>
              </div>
              <div className="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity duration-200 flex items-center justify-center">
                <span className="text-xs text-white font-semibold">View</span>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Lightbox Modal */}
      {selectedPhoto && (
        <div className="fixed inset-0 bg-black/85 flex items-center justify-center p-4 z-50">
          <div className="card w-full max-w-lg bg-surface border border-white/10 p-6 space-y-4 shadow-2xl">
            <div className="flex items-center justify-between border-b border-white/5 pb-3">
              <div>
                <h3 className="font-bold text-white text-base">{selectedPhoto.originalFilename}</h3>
                <p className="text-xs text-slate-400">Uploaded by {selectedPhoto.guestName}</p>
              </div>
              <button onClick={() => setSelectedPhoto(null)} className="text-slate-400 hover:text-white text-sm">
                ✕
              </button>
            </div>

            <div className="aspect-video bg-surface-dark rounded-xl flex items-center justify-center text-4xl">
              📸
            </div>

            <div className="flex items-center justify-between pt-2">
              <span className="text-xs text-slate-500">
                Size: {(selectedPhoto.fileSize / 1024).toFixed(1)} KB
              </span>
              {canDelete && (
                <button
                  onClick={() => {
                    if (confirm('Delete this photo from event gallery?')) {
                      deleteMutation.mutate(selectedPhoto.id)
                    }
                  }}
                  className="text-xs text-red-400 hover:underline font-semibold"
                >
                  Delete Photo
                </button>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
