import { useState, useEffect, useCallback } from 'react'

interface CountdownTimerProps {
  eventDate: string
  startTime?: string
}

interface TimeLeft {
  days: number
  hours: number
  minutes: number
  seconds: number
  isExpired: boolean
}

/**
 * Live Countdown Timer component to event date/time.
 * See: Sprint 3 Objectives — Countdown Timer
 */
export function CountdownTimer({ eventDate, startTime }: CountdownTimerProps) {
  const calculateTimeLeft = useCallback((): TimeLeft => {
    const targetString = startTime ? `${eventDate}T${startTime}` : `${eventDate}T00:00:00`
    const targetTime = new Date(targetString).getTime()
    const now = new Date().getTime()
    const difference = targetTime - now

    if (difference <= 0) {
      return { days: 0, hours: 0, minutes: 0, seconds: 0, isExpired: true }
    }

    return {
      days: Math.floor(difference / (1000 * 60 * 60 * 24)),
      hours: Math.floor((difference / (1000 * 60 * 60)) % 24),
      minutes: Math.floor((difference / 1000 / 60) % 60),
      seconds: Math.floor((difference / 1000) % 60),
      isExpired: false,
    }
  }, [eventDate, startTime])

  const [timeLeft, setTimeLeft] = useState<TimeLeft>(calculateTimeLeft())

  useEffect(() => {
    const timer = setInterval(() => {
      setTimeLeft(calculateTimeLeft())
    }, 1000)

    return () => clearInterval(timer)
  }, [calculateTimeLeft])

  if (timeLeft.isExpired) {
    return (
      <div className="py-4 text-center">
        <span className="text-xl font-bold text-amber-400">🎉 Today is the Event Day! 🎉</span>
      </div>
    )
  }

  return (
    <div className="flex items-center justify-center gap-3 sm:gap-6 py-4">
      <div className="text-center">
        <div className="w-16 h-16 sm:w-20 sm:h-20 glass rounded-2xl flex items-center justify-center text-2xl sm:text-3xl font-bold text-white shadow-glow">
          {timeLeft.days}
        </div>
        <p className="text-xs text-slate-400 uppercase tracking-wider mt-2">Days</p>
      </div>
      <span className="text-2xl font-bold text-slate-600 mb-6">:</span>
      <div className="text-center">
        <div className="w-16 h-16 sm:w-20 sm:h-20 glass rounded-2xl flex items-center justify-center text-2xl sm:text-3xl font-bold text-white shadow-glow">
          {timeLeft.hours}
        </div>
        <p className="text-xs text-slate-400 uppercase tracking-wider mt-2">Hours</p>
      </div>
      <span className="text-2xl font-bold text-slate-600 mb-6">:</span>
      <div className="text-center">
        <div className="w-16 h-16 sm:w-20 sm:h-20 glass rounded-2xl flex items-center justify-center text-2xl sm:text-3xl font-bold text-white shadow-glow">
          {timeLeft.minutes}
        </div>
        <p className="text-xs text-slate-400 uppercase tracking-wider mt-2">Mins</p>
      </div>
      <span className="text-2xl font-bold text-slate-600 mb-6">:</span>
      <div className="text-center">
        <div className="w-16 h-16 sm:w-20 sm:h-20 glass rounded-2xl flex items-center justify-center text-2xl sm:text-3xl font-bold text-primary-400 shadow-glow">
          {timeLeft.seconds}
        </div>
        <p className="text-xs text-slate-400 uppercase tracking-wider mt-2">Secs</p>
      </div>
    </div>
  )
}
