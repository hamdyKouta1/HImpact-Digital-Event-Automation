import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { commentService } from '@/services/commentService'

interface CongratulationsWallProps {
  eventId: string
  invitationCode: string
  guestName: string
}

/**
 * Digital Congratulations Wall component.
 * See: Sprint 3 Objectives — Digital Congratulations Wall
 */
export function CongratulationsWall({ eventId, invitationCode, guestName }: CongratulationsWallProps) {
  const queryClient = useQueryClient()
  const [message, setMessage] = useState('')
  const [error, setError] = useState<string | null>(null)

  const { data: wishesData, isLoading } = useQuery({
    queryKey: ['wishes', eventId],
    queryFn: () => commentService.getWishes(eventId),
    enabled: Boolean(eventId),
  })

  const postMutation = useMutation({
    mutationFn: () => commentService.postWish(eventId, invitationCode, message.trim()),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['wishes', eventId] })
      setMessage('')
      setError(null)
    },
    onError: (err: Error) => setError(err.message || 'Failed to post wish. Please try again.'),
  })

  return (
    <div className="card space-y-6">
      <h2 className="text-2xl font-bold text-white text-center">Digital Wishes Wall 💖</h2>
      <p className="text-slate-400 text-sm text-center -mt-4">
        Leave a warm congratulatory wish for the couple!
      </p>

      {/* Post Wish Input */}
      <form
        onSubmit={(e) => {
          e.preventDefault()
          if (message.trim()) postMutation.mutate()
        }}
        className="space-y-3"
      >
        {error && <p className="error-text">{error}</p>}
        <textarea
          rows={3}
          className="input"
          placeholder={`Write a warm wish, ${guestName}…`}
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          required
        />
        <div className="flex justify-end">
          <button type="submit" disabled={!message.trim() || postMutation.isPending} className="btn-primary">
            {postMutation.isPending ? 'Posting…' : 'Post Wish ✨'}
          </button>
        </div>
      </form>

      {/* Wishes List */}
      {isLoading && <div className="p-4 text-center text-slate-400 text-sm">Loading wishes…</div>}

      {wishesData && wishesData.content.length === 0 && (
        <div className="text-center py-6 text-slate-400 text-sm">
          Be the first guest to leave a congratulatory wish!
        </div>
      )}

      {wishesData && wishesData.content.length > 0 && (
        <div className="space-y-4 pt-4 border-t border-white/5 max-h-96 overflow-y-auto pr-2">
          {wishesData.content.map((wish) => (
            <div key={wish.id} className="glass p-4 rounded-xl space-y-1">
              <div className="flex items-center justify-between">
                <span className="font-semibold text-white text-sm">{wish.guestName}</span>
                <span className="text-xs text-slate-500">
                  {new Date(wish.createdAt).toLocaleDateString()}
                </span>
              </div>
              <p className="text-slate-300 text-sm leading-relaxed">{wish.message}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
