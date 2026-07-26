/** @type {import('tailwindcss').Config} */
// Design tokens from: project-index/09_Design_System.md
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  darkMode: 'class', // User-selectable dark mode (class strategy)
  theme: {
    extend: {
      // ── Color Palette (PI-09 Design System) ───────────────────────────────
      colors: {
        primary: {
          DEFAULT: '#3B82F6',
          50:  '#EFF6FF',
          100: '#DBEAFE',
          200: '#BFDBFE',
          300: '#93C5FD',
          400: '#60A5FA',
          500: '#3B82F6',
          600: '#2563EB',
          700: '#1D4ED8',
          800: '#1E40AF',
          900: '#1E3A8A',
        },
        secondary: {
          DEFAULT: '#8B5CF6',
          500: '#8B5CF6',
          600: '#7C3AED',
        },
        surface: {
          DEFAULT: '#1E293B',
          dark: '#0F172A',
        },
        // Wedding-specific accent
        gold: {
          DEFAULT: '#D4AF37',
          light: '#F5D163',
          dark: '#A88A2A',
        },
      },

      // ── Typography (PI-09 Design System) ──────────────────────────────────
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
        heading: ['Inter', 'sans-serif'],
      },

      // ── Border Radius Tokens ───────────────────────────────────────────────
      borderRadius: {
        sm: '6px',
        md: '10px',
        lg: '16px',
        xl: '24px',
      },

      // ── Shadow Tokens ─────────────────────────────────────────────────────
      boxShadow: {
        sm: '0 1px 3px 0 rgb(0 0 0 / 0.1)',
        md: '0 4px 16px 0 rgb(0 0 0 / 0.12)',
        lg: '0 8px 32px 0 rgb(0 0 0 / 0.16)',
        glow: '0 0 20px rgb(59 130 246 / 0.4)',
      },

      // ── Animation Durations (PI-09 — 150-300ms) ───────────────────────────
      transitionDuration: {
        DEFAULT: '200ms',
        fast: '150ms',
        normal: '200ms',
        slow: '300ms',
      },

      // ── Screen Breakpoints (PI-08 Responsive Breakpoints) ─────────────────
      screens: {
        sm: '375px',   // Mobile
        md: '768px',   // Tablet
        lg: '1024px',  // Desktop
        xl: '1280px',  // Large Desktop
      },
    },
  },
  plugins: [],
}
