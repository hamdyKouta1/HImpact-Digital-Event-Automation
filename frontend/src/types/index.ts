// API type definitions aligned with PI-07 API Specification
// All types match the documented API contract exactly

// ── Auth ──────────────────────────────────────────────────────────────────────

export interface GoogleLoginRequest {
  idToken: string
}

export interface AuthTokenResponse {
  accessToken: string
  refreshToken: string
  expiresIn: number
  tokenType: string
  role: UserRole
  mobileVerified: boolean
}

export interface PhoneVerificationRequest {
  phoneNumber: string
  otpCode?: string
}

// ── Users ─────────────────────────────────────────────────────────────────────

export type UserRole = 'ADMIN' | 'OWNER' | 'GUEST'
export type UserStatus = 'ACTIVE' | 'SUSPENDED' | 'PENDING_VERIFICATION'

export interface User {
  id: string
  googleId: string
  fullName: string
  email: string
  mobileNumber?: string
  profilePicture?: string
  role: UserRole
  status: UserStatus
  mobileVerified: boolean
  lastLogin?: string
  createdAt: string
}

// ── Events ────────────────────────────────────────────────────────────────────

export type EventType = 'WEDDING' | 'BIRTHDAY' | 'GRADUATION' | 'CORPORATE' | 'CONFERENCE' | 'OTHER'
export type EventStatus = 'DRAFT' | 'PUBLISHED' | 'ARCHIVED' | 'SUSPENDED'

export interface Event {
  id: string
  ownerId: string
  title: string
  eventType: EventType
  brideName?: string
  groomName?: string
  description?: string
  venueName?: string
  venueAddress?: string
  googleMapsUrl?: string
  eventDate: string // ISO date
  startTime?: string
  endTime?: string
  coverImage?: string
  packageId?: string
  themeId?: string
  status: EventStatus
  slug?: string
  createdAt: string
  updatedAt: string
}

// ── Guests ────────────────────────────────────────────────────────────────────

export type GuestStatus = 'INVITED' | 'REGISTERED' | 'ATTENDED' | 'DECLINED' | 'BLOCKED'

export interface Guest {
  id: string
  eventId: string
  fullName: string
  mobile?: string
  email?: string
  invitationCode: string
  invitationUrl?: string
  uploadLimit: number
  uploadedCount: number
  storageUsedMb: number
  status: GuestStatus
}

// ── RSVP ──────────────────────────────────────────────────────────────────────

export type AttendanceStatus = 'PENDING' | 'ACCEPTED' | 'DECLINED' | 'MAYBE'

export interface Rsvp {
  id: string
  guestId: string
  eventId: string
  attendanceStatus: AttendanceStatus
  attendeeCount: number
  responseTime?: string
  notes?: string
}

// ── Media ─────────────────────────────────────────────────────────────────────

export type UploadStatus = 'PENDING' | 'UPLOADING' | 'COMPLETED' | 'FAILED' | 'DELETED'

export interface MediaFile {
  id: string
  eventId: string
  guestId: string
  originalFilename: string
  storagePath: string
  mimeType: string
  fileSize: number
  imageWidth?: number
  imageHeight?: number
  uploadStatus: UploadStatus
  uploadedAt?: string
}

// ── API Response Envelope (PI-07) ─────────────────────────────────────────────

export interface ApiSuccess<T = unknown> {
  success: true
  message: string
  data: T
}

export interface ApiError {
  success: false
  errorCode: string
  message: string
  fieldErrors?: Record<string, string>
}

export type ApiResponse<T = unknown> = ApiSuccess<T> | ApiError
