package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// Aurora Mesh Gradient Background Palette
// ==========================================
val AuroraCyanTop = Color(0xFFE0F7FA)         // Soft light cyan/blue at the top
val AuroraWhiteMid = Color(0xFFFFFFFF)        // Clean white transition in the middle
val AuroraPurpleBottom = Color(0xFFF3E5F5)    // Gentle muted purple/blue at the bottom

// Alternative subtle accents for gradients
val AuroraCyanLight = Color(0xFFEBFBFC)
val AuroraPurpleLight = Color(0xFFF8EFFB)

// ==========================================
// Glassmorphism Card & Container Surfaces
// ==========================================
val GlassCardSurface = Color(0xD9FFFFFF)      // rgba(255, 255, 255, 0.85)
val GlassCardSurfaceElevated = Color(0xEBFFFFFF) // ~92% white for headers & popups
val GlassCardBorder = Color(0x66CBD5E1)       // Soft translucent border
val GlassCardBorderHighlight = Color(0xB3FFFFFF) // Specular top border highlight

// ==========================================
// Interactive Elements & Accents
// ==========================================
// Primary Vibrant Cyan/Turquoise Accent
val VibrantCyan = Color(0xFF00BCD4)           // Primary action buttons, active chips, key icons
val CyanGlow = Color(0xFF00E5FF)              // High-vibrancy glow accent
val CyanDark = Color(0xFF00838F)              // Darker cyan for text-on-cyan backgrounds
val CyanContainer = Color(0xFFE0F7FA)         // Soft cyan container for selected chips
val OnCyanContainer = Color(0xFF006064)       // Contrast text for cyan containers

// Warning & Status Accents
val AmberWarning = Color(0xFFF59E0B)          // Warm amber for alerts & due dates
val AmberWarningDark = Color(0xFFD97706)
val AmberContainer = Color(0xFFFEF3C7)        // Soft warm amber background
val OnAmberContainer = Color(0xFF92400E)

val EmeraldSuccess = Color(0xFF10B981)        // Soft green for successful / returned states
val EmeraldSuccessDark = Color(0xFF059669)
val EmeraldContainer = Color(0xFFD1FAE5)      // Soft green background
val OnEmeraldContainer = Color(0xFF065F46)

val PurpleAccent = Color(0xFF8B5CF6)          // Secondary purple/blue for badges & tracks
val PurpleAccentDark = Color(0xFF7C3AED)
val PurpleContainer = Color(0xFFEDE9FE)       // Soft purple background
val OnPurpleContainer = Color(0xFF5B21B6)

val RedDanger = Color(0xFFEF4444)             // Alert red for overdue/critical
val RedDangerDark = Color(0xFFDC2626)
val RedContainer = Color(0xFFFEE2E2)
val OnRedContainer = Color(0xFF991B1B)

// ==========================================
// Typography & Text Hierarchy
// ==========================================
val SlateTextPrimary = Color(0xFF0F172A)      // Deep dark slate/gray for high contrast readability
val SlateTextSecondary = Color(0xFF64748B)    // Muted blue-gray for descriptions, subtitles & labels
val SlateTextTertiary = Color(0xFF94A3B8)     // Subtle captions & disabled states

// ==========================================
// Dark Pill Bottom Navigation Bar
// ==========================================
val DarkPillNavBackground = Color(0xFF0F172A) // Floating dark slate pill matching reference screens
val DarkPillNavSelected = Color(0xFF00BCD4)   // Vibrant cyan active indicator
val DarkPillNavUnselected = Color(0xFF94A3B8) // Muted slate unselected icons

// Legacy aliases for backward compatibility across existing calls
val NavyPrimary = SlateTextPrimary
val SkyAccent = VibrantCyan
val SkyDayBackground = AuroraCyanTop
val SkyDayBackgroundMid = AuroraWhiteMid
val SkyDayBackgroundBottom = AuroraPurpleBottom
val CardSurface = GlassCardSurface
val SlateBorder = GlassCardBorder
val OrangeDue = AmberWarningDark
val DarkNavyBg = Color(0xFF0B1120)
val DarkSurface = Color(0xFF1E293B)
val DarkTextPrimary = Color(0xFFF1F5F9)
val DarkTextSecondary = Color(0xFF94A3B8)
