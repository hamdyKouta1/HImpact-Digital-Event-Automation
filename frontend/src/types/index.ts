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

export type UserRole = 'SUPER_ADMIN' | 'ADMIN' | 'SUPPORT' | 'FINANCE' | 'OWNER' | 'GUEST'
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

export type EventType = 'WEDDING' | 'ENGAGEMENT' | 'BIRTHDAY' | 'GRADUATION' | 'CORPORATE' | 'CONFERENCE' | 'EXHIBITION' | 'PRODUCT_LAUNCH' | 'COMMUNITY' | 'PRIVATE_CELEBRATION' | 'OTHER'
export type EventStatus = 'DRAFT' | 'PUBLISHED' | 'ARCHIVED' | 'SUSPENDED'

export interface CreateEventRequest {
  title: string
  eventType: EventType
  brideName?: string
  groomName?: string
  description?: string
  venueName: string
  venueAddress?: string
  googleMapsUrl?: string
  eventDate: string
  startTime?: string
  endTime?: string
  coverImage?: string
  packageId?: string
  themeId?: string
}

export interface UpdateEventRequest {
  title?: string
  eventType?: EventType
  brideName?: string
  groomName?: string
  description?: string
  venueName?: string
  venueAddress?: string
  googleMapsUrl?: string
  eventDate?: string
  startTime?: string
  endTime?: string
  coverImage?: string
  themeId?: string
}

export interface EventSummaryResponse {
  id: string
  title: string
  eventType: EventType
  eventDate: string
  venueName: string
  coverImage?: string
  status: EventStatus
  slug?: string
  totalGuests?: number
  totalUploads?: number
}

export interface Event {
  id: string
  ownerId: string
  ownerName?: string
  title: string
  eventType: EventType
  brideName?: string
  groomName?: string
  description?: string
  venueName?: string
  venueAddress?: string
  googleMapsUrl?: string
  eventDate: string
  startTime?: string
  endTime?: string
  coverImage?: string
  packageId?: string
  packageName?: string
  themeId?: string
  themeName?: string
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
  createdAt: string
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
