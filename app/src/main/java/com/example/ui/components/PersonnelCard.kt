package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.util.addPointerInputChange
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.data.LastShortLeaveInfo
import com.example.data.LeaveStatus
import com.example.data.Personnel
import com.example.data.formatLeaveType
import com.example.data.formatRank
import com.example.data.formatWilaya
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CoralRedAccent
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.LocalLivingLuxuryMode
import com.example.ui.theme.LocalThemePreset
import com.example.ui.theme.RedDanger
import com.example.ui.theme.RoyalBlueAccent
import com.example.ui.theme.SkyLeaveBlue
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.liquidGlass
import com.example.ui.theme.liquidGlassPanel
import com.example.util.AppLanguage
import com.example.util.AppStrings
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.tanh

object SwipeDeleteGestureHelper {
    const val HOLD_ARM_DURATION_MS = 500L
    const val FLING_VELOCITY_THRESHOLD_PX = 780f

    /** Physical screen X direction sign: -1f (Left) for Arabic/RTL, +1f (Right) for English/French. */
    fun expectedDirectionSign(lang: AppLanguage): Float =
        if (lang == AppLanguage.ARABIC || lang.isRtl) -1f else 1f

    fun coerceDirectionalOffset(rawDeltaX: Float, lang: AppLanguage): Float {
        return if (expectedDirectionSign(lang) < 0f) {
            rawDeltaX.coerceAtMost(0f)
        } else {
            rawDeltaX.coerceAtLeast(0f)
        }
    }

    /**
     * Applies physically realistic drag displacement:
     * - Smooth 1:1 motion in the valid delete direction.
     * - Gentle asymptotic rubber-band resistance (max ~22px) if nudged in the opposite direction.
     */
    fun applyElasticDrag(rawDeltaX: Float, lang: AppLanguage): Float {
        val dirSign = expectedDirectionSign(lang)
        val alongValidDir = rawDeltaX * dirSign
        return if (alongValidDir >= 0f) {
            rawDeltaX
        } else {
            val maxOppositeElastic = 22f
            val damped = maxOppositeElastic * tanh(abs(alongValidDir) / (maxOppositeElastic * 2.4f))
            -dirSign * damped
        }
    }

    fun deleteThresholdPx(cardWidthPx: Float): Float {
        return if (cardWidthPx > 0f) maxOf(110f, cardWidthPx * 0.28f) else 220f
    }

    fun shouldTriggerDelete(dragOffsetX: Float, cardWidthPx: Float, lang: AppLanguage): Boolean {
        val directionalProgress = dragOffsetX * expectedDirectionSign(lang)
        return directionalProgress >= deleteThresholdPx(cardWidthPx)
    }

    /**
     * Evaluates both displacement and flick velocity so tossing a card feels responsive and real,
     * while flicking back toward origin cancels even if the card briefly crossed the threshold.
     */
    fun shouldTriggerDeleteWithVelocity(
        dragOffsetX: Float,
        velocityX: Float,
        cardWidthPx: Float,
        lang: AppLanguage
    ): Boolean {
        val dirSign = expectedDirectionSign(lang)
        val directionalDist = dragOffsetX * dirSign
        val directionalVel = velocityX * dirSign
        val threshold = deleteThresholdPx(cardWidthPx)

        // If user strongly flicks BACK toward rest, cancel delete cleanly
        if (directionalVel < -FLING_VELOCITY_THRESHOLD_PX * 0.85f) return false
        // Fast intentional flick in delete direction after meaningful drag (>= 38% of threshold)
        if (directionalVel >= FLING_VELOCITY_THRESHOLD_PX && directionalDist >= threshold * 0.38f) return true
        return directionalDist >= threshold
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PersonnelCard(
    personnel: Personnel,
    dutyThresholdDays: Int,
    onConfirmDuty: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = false,
    isNeumorphic: Boolean = false,
    animationsEnabled: Boolean = true,
    currentLang: AppLanguage = AppLanguage.ARABIC,
    effectiveDaysSinceDuty: Long = personnel.daysSinceDutyReturn,
    lastShortLeaveInfo: LastShortLeaveInfo? = null,
    onTogglePin: () -> Unit = {},
    onSwipeDelete: () -> Unit = onDelete,
    onViewHistory: () -> Unit = {},
    onExtendLeave: (() -> Unit)? = null,
    onQuickAddLeave: (() -> Unit)? = null
) {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color.White else SlateTextPrimary
    val textSecondary = preset?.textSecondary ?: if (effectiveDark) Color(0xFF94A3B8) else SlateTextSecondary
    val primaryAccent = preset?.primary ?: if (effectiveDark) CyanGlow else VibrantCyan

    val status = personnel.status
    val isEligible = personnel.isReturnedToDuty && effectiveDaysSinceDuty >= dutyThresholdDays
    val progress = personnel.leaveProgressFraction

    val animatedProgress = if (animationsEnabled && !personnel.isReturnedToDuty) {
        animateFloatAsState(
            targetValue = progress,
            animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing),
            label = "leaveProgress"
        ).value
    } else progress

    data class StatusVisual(
        val color: Color,
        val bgTint: Color,
        val icon: ImageVector,
        val badgeKey: String
    )

    val visual = when {
        status == LeaveStatus.AT_HOME -> {
            val atHomeAccent = if (effectiveDark) Color(0xFF38BDF8) else SkyLeaveBlue
            StatusVisual(
                color = atHomeAccent,
                bgTint = atHomeAccent.copy(alpha = if (effectiveDark) 0.16f else 0.12f),
                icon = Icons.Default.Home,
                badgeKey = "badge_at_home"
            )
        }
        status == LeaveStatus.RETURNING_TODAY -> StatusVisual(
            color = AmberWarning,
            bgTint = if (effectiveDark) Color(0x29D97706) else Color(0xFFFEF3C7),
            icon = Icons.Default.NotificationsActive,
            badgeKey = "badge_returning_today"
        )
        status == LeaveStatus.OVERDUE -> StatusVisual(
            color = RedDanger,
            bgTint = if (effectiveDark) Color(0x29DC2626) else Color(0xFFFEE2E2),
            icon = Icons.Default.Warning,
            badgeKey = "badge_overdue"
        )
        status == LeaveStatus.ON_DUTY && isEligible -> StatusVisual(
            color = AmberWarning,
            bgTint = if (effectiveDark) Color(0x29D97706) else Color(0xFFFEF3C7),
            icon = Icons.Default.Star,
            badgeKey = "badge_eligible"
        )
        status == LeaveStatus.ON_DUTY -> StatusVisual(
            color = EmeraldSuccess,
            bgTint = if (effectiveDark) Color(0x29059669) else Color(0xFFE6F7F0),
            icon = Icons.Default.Shield,
            badgeKey = "badge_on_duty"
        )
        else -> StatusVisual(
            color = if (effectiveDark) Color(0xFF94A3B8) else SlateTextSecondary,
            bgTint = if (effectiveDark) Color(0x2464748B) else Color(0xFFF1F5F9),
            icon = Icons.Default.Schedule,
            badgeKey = "badge_upcoming"
        )
    }

    val localizedRank = formatRank(personnel.rank, currentLang)
    val localizedWilaya = formatWilaya(personnel.wilaya, currentLang, includeCode = false)
    val localizedLeaveType = formatLeaveType(personnel.leaveType, currentLang)

    val avatarShape = if (isNeumorphic) RoundedCornerShape(18.dp) else CircleShape
    var isExpanded by rememberSaveable(personnel.id) { mutableStateOf(false) }

    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = if (animationsEnabled) {
            spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = 420f
            )
        } else tween(0),
        label = "arrowRotation"
    )

    val isLivingLuxury = LocalLivingLuxuryMode.current
    val isLivingActive = isLivingLuxury && animationsEnabled
    val cardBreath = if (isLivingActive) {
        val inf = rememberInfiniteTransition(label = "livingCard_${personnel.id}")
        val p by inf.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 3600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "cardBreath"
        )
        p
    } else 0f

    val haptic = LocalHapticFeedback.current
    val holdScope = rememberCoroutineScope()
    val latestOnTogglePin by rememberUpdatedState(onTogglePin)
    val latestOnSwipeDelete by rememberUpdatedState(onSwipeDelete)
    val latestLang by rememberUpdatedState(currentLang)

    var holdProgress by remember { mutableFloatStateOf(0f) }
    var isSwipeArmed by remember { mutableStateOf(false) }
    var isHorizontalDragging by remember { mutableStateOf(false) }
    var isFingerTracking by remember(personnel.id) { mutableStateOf(false) }
    var fingerDragX by remember(personnel.id) { mutableFloatStateOf(0f) }
    var cardWidthPx by remember { mutableFloatStateOf(0f) }
    val releaseOffsetAnim = remember(personnel.id) { Animatable(0f) }

    // Single source of truth for card X offset: direct CPU write while finger is down (0ms lag)
    // and velocity-aware Animatable spring/fling once released.
    fun currentOffsetX(): Float = if (isFingerTracking) fingerDragX else releaseOffsetAnim.value

    val showBackdrop by remember {
        derivedStateOf { isHorizontalDragging || abs(currentOffsetX()) > 2f }
    }
    val isPastDeleteThreshold by remember(currentLang) {
        derivedStateOf {
            SwipeDeleteGestureHelper.shouldTriggerDelete(currentOffsetX(), cardWidthPx, currentLang)
        }
    }

    val cardPressScale by animateFloatAsState(
        targetValue = when {
            isHorizontalDragging -> 1.012f // Subtle physical lift off the deck while sliding
            isSwipeArmed -> 0.985f
            holdProgress > 0.02f -> 0.988f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "pinHoldScale"
    )

    val trashIconPopScale by animateFloatAsState(
        targetValue = if (isPastDeleteThreshold) 1.24f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "trashIconPop"
    )

    val trashIconTilt by animateFloatAsState(
        targetValue = if (isPastDeleteThreshold) -12f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "trashIconTilt"
    )

    val cardShape = RoundedCornerShape(22.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged { cardWidthPx = it.width.toFloat() }
            .pointerInput(personnel.id, currentLang) {
                val slop = viewConfiguration.touchSlop
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                    val startPos = down.position
                    val velocityTracker = VelocityTracker()
                    velocityTracker.addPointerInputChange(down)

                    var triggeredPin = false
                    var dragging = false
                    var finished = false
                    var handedOffRelease = false
                    var dragOriginX = startPos.x
                    var lastDragX = 0f
                    var wasPastDetent = false

                    val holdJob = holdScope.launch {
                        // Exact 0.5s (500ms) hold arms directional swipe-to-delete
                        delay(SwipeDeleteGestureHelper.HOLD_ARM_DURATION_MS)
                        if (finished) return@launch
                        isSwipeArmed = true
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                        // Continuing stationary hold toggles Pin after an extra 700ms
                        val pinStartTime = System.currentTimeMillis()
                        val pinRemainingMs = 700L
                        while (!finished && !dragging) {
                            val elapsed = System.currentTimeMillis() - pinStartTime
                            val p = (elapsed.toFloat() / pinRemainingMs.toFloat()).coerceIn(0f, 1f)
                            holdProgress = p
                            if (p >= 1f) {
                                triggeredPin = true
                                holdProgress = 0f
                                isSwipeArmed = false
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                latestOnTogglePin()
                                break
                            }
                            delay(16L)
                        }
                    }

                    try {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            val matching = event.changes.firstOrNull { it.id == down.id }
                            if (matching == null || !matching.pressed) {
                                if (triggeredPin || dragging) {
                                    event.changes.forEach { it.consume() }
                                }
                                break
                            }
                            velocityTracker.addPointerInputChange(matching)

                            if (triggeredPin) {
                                event.changes.forEach { it.consume() }
                                continue
                            }

                            val totalDx = matching.position.x - startPos.x
                            val totalDy = matching.position.y - startPos.y

                            if (!isSwipeArmed) {
                                if (hypot(totalDx, totalDy) > slop) {
                                    break
                                }
                            } else {
                                if (!dragging) {
                                    val dirSign = SwipeDeleteGestureHelper.expectedDirectionSign(latestLang)
                                    val directionalDx = totalDx * dirSign
                                    if (directionalDx > slop * 0.55f && abs(totalDx) > abs(totalDy) * 0.65f) {
                                        dragging = true
                                        isHorizontalDragging = true
                                        isFingerTracking = true
                                        // Anchor at current finger position on the stationary outer box -> zero jump & zero coordinate loop!
                                        dragOriginX = matching.position.x
                                        fingerDragX = 0f
                                        lastDragX = 0f
                                        holdProgress = 0f
                                        holdJob.cancel()
                                    } else if (abs(totalDy) > slop * 2.2f && directionalDx <= slop) {
                                        break
                                    }
                                }
                                if (dragging) {
                                    event.changes.forEach { it.consume() }
                                    val rawDeltaFromAnchor = matching.position.x - dragOriginX
                                    val physicalX = SwipeDeleteGestureHelper.applyElasticDrag(rawDeltaFromAnchor, latestLang)
                                    lastDragX = physicalX
                                    // Direct synchronous state write from stationary coordinates: 0ms lag, 0 trembling!
                                    fingerDragX = physicalX

                                    val nowPastDetent = SwipeDeleteGestureHelper.shouldTriggerDelete(
                                        physicalX,
                                        cardWidthPx,
                                        latestLang
                                    )
                                    if (nowPastDetent != wasPastDetent) {
                                        wasPastDetent = nowPastDetent
                                        // Physical magnetic detent feedback when crossing delete threshold!
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                                }
                            }
                        }

                        finished = true
                        holdJob.cancel()
                        holdProgress = 0f

                        if (dragging) {
                            handedOffRelease = true
                            val releaseVelX = velocityTracker.calculateVelocity().x
                            val shouldDelete = SwipeDeleteGestureHelper.shouldTriggerDeleteWithVelocity(
                                dragOffsetX = lastDragX,
                                velocityX = releaseVelX,
                                cardWidthPx = cardWidthPx,
                                lang = latestLang
                            )
                            isHorizontalDragging = false
                            isSwipeArmed = false

                            holdScope.launch {
                                releaseOffsetAnim.snapTo(lastDragX)
                                isFingerTracking = false
                                fingerDragX = 0f

                                if (shouldDelete) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    val dirSign = SwipeDeleteGestureHelper.expectedDirectionSign(latestLang)
                                    val exitX = dirSign * (cardWidthPx.coerceAtLeast(650f) * 1.18f)
                                    releaseOffsetAnim.animateTo(
                                        targetValue = exitX,
                                        animationSpec = tween(durationMillis = 165, easing = FastOutLinearInEasing),
                                        initialVelocity = if (releaseVelX * dirSign > 0f) releaseVelX else dirSign * 650f
                                    )
                                    latestOnSwipeDelete()
                                    releaseOffsetAnim.snapTo(0f)
                                } else {
                                    // Smooth damped spring rebound inheriting actual finger release velocity
                                    releaseOffsetAnim.animateTo(
                                        targetValue = 0f,
                                        initialVelocity = releaseVelX,
                                        animationSpec = spring(
                                            dampingRatio = 0.72f,
                                            stiffness = Spring.StiffnessMediumLow
                                        )
                                    )
                                }
                            }
                        }
                    } finally {
                        finished = true
                        holdJob.cancel()
                        holdProgress = 0f
                        if (!handedOffRelease) {
                            isHorizontalDragging = false
                            isFingerTracking = false
                            fingerDragX = 0f
                            isSwipeArmed = false
                        }
                    }
                }
            }
    ) {
        // Directional Trash Bin Reveal Backdrop (pure GPU-drawn ripple & smooth icon scale, zero frame recomposition lag)
        if (showBackdrop) {
            val isRtlSwipeLeft = SwipeDeleteGestureHelper.expectedDirectionSign(currentLang) < 0f
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(24.dp))
                        .drawBehind {
                            val thresh = SwipeDeleteGestureHelper.deleteThresholdPx(cardWidthPx)
                            val dirSign = SwipeDeleteGestureHelper.expectedDirectionSign(latestLang)
                            val directionalPx = (currentOffsetX() * dirSign).coerceAtLeast(0f)
                            val ratio = (directionalPx / thresh).coerceIn(0f, 1.35f)

                            // Base warm crimson track
                            drawRect(
                                brush = Brush.horizontalGradient(
                                    listOf(
                                        RedDanger.copy(alpha = 0.22f + 0.55f * ratio.coerceAtMost(1f)),
                                        CoralRedAccent.copy(alpha = 0.28f + 0.55f * ratio.coerceAtMost(1f))
                                    )
                                )
                            )

                            // Expanding circular radial wave anchored at the revealed side
                            val anchorX = if (isRtlSwipeLeft) size.width - 42.dp.toPx() else 42.dp.toPx()
                            val maxRadius = size.width * 1.15f
                            val waveRadius = (24.dp.toPx() + maxRadius * (ratio * ratio * 0.72f)).coerceAtMost(maxRadius)
                            drawCircle(
                                color = if (ratio >= 1f) RedDanger.copy(alpha = 0.92f) else CoralRedAccent.copy(alpha = 0.42f * ratio),
                                radius = waveRadius,
                                center = Offset(anchorX, size.height / 2f)
                            )
                        }
                        .padding(horizontal = 22.dp),
                    contentAlignment = if (isRtlSwipeLeft) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.graphicsLayer {
                            val thresh = SwipeDeleteGestureHelper.deleteThresholdPx(cardWidthPx)
                            val dirSign = SwipeDeleteGestureHelper.expectedDirectionSign(latestLang)
                            val ratio = ((currentOffsetX() * dirSign) / thresh).coerceIn(0f, 1.15f)
                            alpha = (0.35f + ratio * 0.65f).coerceIn(0f, 1f)
                        }
                    ) {
                        if (!isRtlSwipeLeft) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .graphicsLayer {
                                        scaleX = trashIconPopScale
                                        scaleY = trashIconPopScale
                                        rotationZ = trashIconTilt
                                    }
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = if (isPastDeleteThreshold) 0.28f else 0.16f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = AppStrings.get("delete", currentLang),
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Text(
                            text = AppStrings.get(
                                if (isPastDeleteThreshold) "swipe_delete_release"
                                else if (isRtlSwipeLeft) "swipe_delete_hint_rtl" else "swipe_delete_hint_ltr",
                                currentLang
                            ),
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.5.sp
                        )
                        if (isRtlSwipeLeft) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .graphicsLayer {
                                        scaleX = trashIconPopScale
                                        scaleY = trashIconPopScale
                                        rotationZ = -trashIconTilt
                                    }
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = if (isPastDeleteThreshold) 0.28f else 0.16f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = AppStrings.get("delete", currentLang),
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Foreground Draggable Frosted Glass Personnel Card matching mockup literally
        val cardFillBrush = if (preset != null) {
            Brush.verticalGradient(
                listOf(
                    preset.surfacePrimary.copy(alpha = if (effectiveDark) 0.86f else 0.88f),
                    preset.surfacePrimary.copy(alpha = if (effectiveDark) 0.78f else 0.78f)
                )
            )
        } else if (effectiveDark) {
            Brush.verticalGradient(listOf(Color(0xD9152430), Color(0xC9101C26)))
        } else {
            Brush.verticalGradient(listOf(Color(0xEBFFFFFF), Color(0xD4F8FCFB)))
        }

        val cardBorderBrush = if (preset != null) {
            Brush.verticalGradient(
                listOf(
                    Color.White.copy(alpha = if (effectiveDark) (if (isLivingActive) 0.20f + 0.14f * cardBreath else 0.18f) else 0.90f),
                    if (isLivingActive) {
                        primaryAccent.copy(alpha = 0.22f + 0.18f * cardBreath)
                    } else {
                        preset.borderAccent.copy(alpha = 0.30f)
                    }
                )
            )
        } else if (effectiveDark) {
            Brush.verticalGradient(
                listOf(
                    Color.White.copy(alpha = if (isLivingActive) 0.18f + 0.14f * cardBreath else 0.16f),
                    if (isLivingActive) primaryAccent.copy(alpha = 0.20f + 0.16f * cardBreath) else Color(0xFF233645)
                )
            )
        } else {
            Brush.verticalGradient(
                listOf(
                    Color.White.copy(alpha = 0.96f),
                    if (isLivingActive) primaryAccent.copy(alpha = 0.18f + 0.14f * cardBreath) else Color(0xFFD5E3E2)
                )
            )
        }

        val daysBadgeText = remember(effectiveDaysSinceDuty) {
            if (effectiveDaysSinceDuty < 0L) "\u200E$effectiveDaysSinceDuty\u200E" else "$effectiveDaysSinceDuty"
        }

        val neutralChipBg = if (effectiveDark) Color(0xBF172633) else Color(0xE8FFFFFF)
        val neutralChipBorder = if (effectiveDark) Color(0xFF283B4A) else Color(0xFFDDE7E6)
        val dividerColor = if (effectiveDark) Color(0xFF223342) else Color(0xFFE7EFEF)

        val resolvedBorder = when {
            isSwipeArmed || isHorizontalDragging -> BorderStroke(1.5.dp, CoralRedAccent.copy(alpha = 0.85f))
            personnel.isPinned -> BorderStroke(1.3.dp, AmberWarning.copy(alpha = 0.65f))
            else -> BorderStroke(if (isLivingActive) 1.15.dp else 1.dp, cardBorderBrush)
        }

        val luxurySheenModifier = if (isLivingActive) {
            Modifier.drawWithContent {
                drawContent()
                val sheenAlpha = (if (effectiveDark) 0.13f else 0.34f) * (0.55f + 0.45f * cardBreath)
                drawLine(
                    brush = Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = sheenAlpha),
                            primaryAccent.copy(alpha = sheenAlpha * 0.65f),
                            Color.Transparent
                        )
                    ),
                    start = Offset(size.width * 0.12f, 1.5.dp.toPx()),
                    end = Offset(size.width * 0.88f, 1.5.dp.toPx()),
                    strokeWidth = 1.6.dp.toPx()
                )
            }
        } else Modifier

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    val x = currentOffsetX()
                    val safeWidth = cardWidthPx.coerceAtLeast(600f)
                    val dirSign = SwipeDeleteGestureHelper.expectedDirectionSign(latestLang)
                    val progress = ((x * dirSign) / safeWidth).coerceIn(0f, 1.2f)

                    translationX = x
                    rotationZ = (x / safeWidth) * 5.2f
                    rotationY = -(x / safeWidth) * 6.5f
                    cameraDistance = 14f * density

                    alpha = if (progress > 0.72f) {
                        (1f - ((progress - 0.72f) * 1.15f)).coerceIn(0.25f, 1f)
                    } else {
                        1f
                    }
                    if (animationsEnabled) {
                        scaleX = cardPressScale
                        scaleY = cardPressScale
                    }
                }
                .clip(cardShape)
                .background(cardFillBrush)
                .then(luxurySheenModifier)
                .border(resolvedBorder, cardShape)
                .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    isExpanded = !isExpanded
                }
                .testTag("personnel_item_${personnel.id}")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 15.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Header Row: [Days Counter Mint Tile] [Name + Rank] [Status Dot Pill]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(15.dp),
                        color = visual.color.copy(alpha = if (effectiveDark) 0.20f else 0.11f),
                        border = BorderStroke(
                            1.dp,
                            visual.color.copy(alpha = if (effectiveDark) 0.34f else 0.22f)
                        ),
                        modifier = Modifier
                            .size(50.dp)
                            .testTag("personnel_days_counter_${personnel.id}")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = daysBadgeText,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = when {
                                    daysBadgeText.length >= 4 -> 12.5.sp
                                    daysBadgeText.length == 3 -> 14.5.sp
                                    else -> 17.5.sp
                                },
                                color = visual.color,
                                maxLines = 1
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = personnel.getLocalizedName(currentLang),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary,
                                fontSize = 15.5.sp,
                                lineHeight = 20.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            if (personnel.isPinned) {
                                Surface(
                                    onClick = onTogglePin,
                                    shape = CircleShape,
                                    color = AmberWarning.copy(alpha = if (effectiveDark) 0.18f else 0.12f),
                                    modifier = Modifier
                                        .size(24.dp)
                                        .testTag("pinned_badge_${personnel.id}")
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.PushPin,
                                            contentDescription = AppStrings.get("unpin_action", currentLang),
                                            tint = AmberWarning,
                                            modifier = Modifier
                                                .size(12.dp)
                                                .rotate(30f)
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = localizedRank,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Trailing Tinted Status Dot Pill ("• On Duty")
                    Surface(
                        shape = CircleShape,
                        color = visual.bgTint,
                        border = BorderStroke(
                            1.dp,
                            visual.color.copy(alpha = if (effectiveDark) 0.30f else 0.18f)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(if (isLivingActive) 12.dp else 7.dp)
                            ) {
                                if (isLivingActive) {
                                    Box(
                                        modifier = Modifier
                                            .size(11.dp)
                                            .graphicsLayer {
                                                val haloScale = 0.85f + 0.50f * cardBreath
                                                scaleX = haloScale
                                                scaleY = haloScale
                                                alpha = (1f - cardBreath) * 0.48f
                                            }
                                            .clip(CircleShape)
                                            .background(visual.color)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .graphicsLayer {
                                            if (isLivingActive) {
                                                val s = 1f + 0.16f * cardBreath
                                                scaleX = s
                                                scaleY = s
                                            }
                                        }
                                        .clip(CircleShape)
                                        .background(visual.color)
                                )
                            }
                            Text(
                                text = AppStrings.get(visual.badgeKey, currentLang),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = visual.color,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // 2. Middle Metadata Chips Row: [Rank Mint Chip] [Registration Pill] [📍 Wilaya Pill]
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = primaryAccent.copy(alpha = if (effectiveDark) 0.18f else 0.10f),
                        border = BorderStroke(
                            1.dp,
                            primaryAccent.copy(alpha = if (effectiveDark) 0.30f else 0.18f)
                        )
                    ) {
                        Text(
                            text = localizedRank,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = primaryAccent,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = neutralChipBg,
                        border = BorderStroke(1.dp, neutralChipBorder)
                    ) {
                        Text(
                            text = personnel.registrationNumber,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = textPrimary,
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    if (localizedWilaya.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = neutralChipBg,
                            border = BorderStroke(1.dp, neutralChipBorder),
                            modifier = Modifier.testTag("personnel_wilaya_${personnel.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = AppStrings.get("wilaya_label", currentLang),
                                    tint = CoralRedAccent,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = localizedWilaya,
                                    fontSize = 11.5.sp,
                                    color = textPrimary,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    if (lastShortLeaveInfo != null) {
                        val shortAccent = when {
                            lastShortLeaveInfo.isCurrentlyOnShortLeave -> if (effectiveDark) CyanGlow else SkyLeaveBlue
                            lastShortLeaveInfo.isReturningToday -> AmberWarning
                            else -> primaryAccent
                        }
                        val shortCounterText = if (lastShortLeaveInfo.daysCounter < 0L) {
                            "\u200E${lastShortLeaveInfo.daysCounter}\u200E"
                        } else {
                            "${lastShortLeaveInfo.daysCounter}"
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = shortAccent.copy(alpha = if (effectiveDark) 0.18f else 0.10f),
                            border = BorderStroke(
                                1.dp,
                                shortAccent.copy(alpha = if (effectiveDark) 0.34f else 0.22f)
                            ),
                            modifier = Modifier.testTag("personnel_short_leave_badge_${personnel.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = AppStrings.get("last_short_leave_counter", currentLang),
                                    tint = shortAccent,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "${AppStrings.get("short_leave_compact_label", currentLang)}: $shortCounterText",
                                    fontSize = 11.5.sp,
                                    color = shortAccent,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // 3. Hairline Divider + Expand/Details Footer Row
                HorizontalDivider(
                    thickness = 1.dp,
                    color = dividerColor
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 1.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = primaryAccent,
                            modifier = Modifier
                                .size(16.dp)
                                .graphicsLayer { rotationZ = arrowRotation }
                        )
                        Text(
                            text = AppStrings.get(
                                if (isExpanded) "click_to_collapse" else "click_to_expand",
                                currentLang
                            ),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryAccent,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        text = AppStrings.get("personnel_details_label", currentLang),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Expandable Details Section (interruptible critically-damped spring + lead fadeOut)
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = if (animationsEnabled) {
                        expandVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = 380f,
                                visibilityThreshold = IntSize.VisibilityThreshold
                            ),
                            expandFrom = Alignment.Top
                        ) + fadeIn(animationSpec = tween(220, delayMillis = 35, easing = FastOutSlowInEasing))
                    } else EnterTransition.None,
                    exit = if (animationsEnabled) {
                        shrinkVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = 440f,
                                visibilityThreshold = IntSize.VisibilityThreshold
                            ),
                            shrinkTowards = Alignment.Top
                        ) + fadeOut(animationSpec = tween(140, easing = FastOutSlowInEasing))
                    } else ExitTransition.None
                ) {
                    val innerSurface = if (effectiveDark) Color(0xFF1A2538) else Color(0xFFF8FAFC)
                    val innerBorder = if (effectiveDark) Color(0xFF283548) else Color(0xFFE2E8F0)
                    val tileBg = if (effectiveDark) Color(0xFF151F31) else Color.White

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = innerSurface,
                            border = BorderStroke(1.dp, innerBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(9.dp)
                            ) {
                                if (personnel.startDateMillis > 0L && personnel.returnDateMillis > 0L) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(primaryAccent.copy(alpha = if (effectiveDark) 0.16f else 0.10f))
                                        ) {
                                            Text(
                                                text = localizedLeaveType,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = primaryAccent,
                                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                                            )
                                        }

                                        Text(
                                            text = "${AppStrings.get("leave_duration", currentLang)} ${personnel.totalLeaveDays} ${AppStrings.get("days", currentLang)}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = textPrimary
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = tileBg,
                                            border = BorderStroke(1.dp, innerBorder),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 7.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.CalendarMonth,
                                                    contentDescription = null,
                                                    tint = primaryAccent,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(5.dp))
                                                Text(
                                                    text = "${AppStrings.get("benefit_date", currentLang)} ${personnel.formattedStartDate}",
                                                    fontSize = 10.5.sp,
                                                    color = textSecondary,
                                                    fontWeight = FontWeight.Medium,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (personnel.isOverdue) {
                                                RedDanger.copy(alpha = if (effectiveDark) 0.16f else 0.08f)
                                            } else {
                                                tileBg
                                            },
                                            border = BorderStroke(
                                                1.dp,
                                                if (personnel.isOverdue) RedDanger.copy(alpha = 0.45f) else innerBorder
                                            ),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = "${AppStrings.get("return_date", currentLang)} ${personnel.formattedReturnDate}",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (personnel.isOverdue) RedDanger else textPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 7.dp)
                                            )
                                        }
                                    }
                                } else {
                                    Text(
                                        text = AppStrings.get("no_leave_recorded_short", currentLang),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = textSecondary
                                    )
                                }

                                if (!personnel.isReturnedToDuty) {
                                    LinearProgressIndicator(
                                        progress = { animatedProgress },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(50.dp)),
                                        color = visual.color,
                                        trackColor = if (effectiveDark) Color(0xFF283548) else Color(0xFFE2E8F0)
                                    )

                                    Text(
                                        text = personnel.getCountdownText(currentLang),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = visual.color
                                    )
                                } else {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${AppStrings.get("days_served", currentLang)}: $effectiveDaysSinceDuty ${AppStrings.get("days", currentLang)}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldSuccess
                                        )
                                        if (isEligible) {
                                            Text(
                                                text = AppStrings.get("status_eligible", currentLang),
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AmberWarning
                                            )
                                        }
                                    }
                                }

                                if (lastShortLeaveInfo != null) {
                                    val shortAccent = when {
                                        lastShortLeaveInfo.isCurrentlyOnShortLeave -> if (effectiveDark) CyanGlow else SkyLeaveBlue
                                        lastShortLeaveInfo.isReturningToday -> AmberWarning
                                        else -> primaryAccent
                                    }
                                    val shortCounterDisplay = if (lastShortLeaveInfo.daysCounter < 0L) {
                                        "\u200E${lastShortLeaveInfo.daysCounter}\u200E"
                                    } else {
                                        "${lastShortLeaveInfo.daysCounter}"
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = tileBg,
                                        border = BorderStroke(
                                            1.dp,
                                            shortAccent.copy(alpha = if (effectiveDark) 0.32f else 0.22f)
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("personnel_short_leave_detail_${personnel.id}")
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 10.dp, vertical = 8.dp),
                                            verticalArrangement = Arrangement.spacedBy(3.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = AppStrings.get("last_short_leave_counter", currentLang),
                                                    fontSize = 10.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = shortAccent
                                                )
                                                Text(
                                                    text = shortCounterDisplay,
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = shortAccent
                                                )
                                            }
                                            Text(
                                                text = lastShortLeaveInfo.getCounterStatusText(currentLang),
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = textPrimary
                                            )
                                            Text(
                                                text = "${lastShortLeaveInfo.record.formattedStartDate} → ${lastShortLeaveInfo.record.formattedReturnDate}",
                                                fontSize = 10.sp,
                                                color = textSecondary
                                            )
                                        }
                                    }
                                }

                                if (personnel.notes.isNotBlank()) {
                                    Text(
                                        text = "${AppStrings.get("notes_label", currentLang)} ${personnel.notes}",
                                        fontSize = 10.5.sp,
                                        color = textSecondary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        // Action Buttons Section (48dp minimum touch targets, frosted glass actions)
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (!personnel.isReturnedToDuty) {
                                val canExtendActiveLeave = onExtendLeave != null &&
                                    personnel.startDateMillis > 0L &&
                                    personnel.returnDateMillis > 0L
                                if (canExtendActiveLeave) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        LiquidGlassActionButton(
                                            text = AppStrings.get("confirm_duty_btn", currentLang),
                                            icon = Icons.Default.Check,
                                            onClick = onConfirmDuty,
                                            isDark = effectiveDark,
                                            primaryAccent = EmeraldSuccess,
                                            isPrimary = true,
                                            modifier = Modifier
                                                .weight(1.1f)
                                                .testTag("personnel_quick_action_${personnel.id}")
                                        )
                                        Surface(
                                            onClick = onExtendLeave!!,
                                            shape = RoundedCornerShape(14.dp),
                                            color = primaryAccent.copy(alpha = if (effectiveDark) 0.16f else 0.10f),
                                            border = BorderStroke(1.dp, primaryAccent.copy(alpha = 0.38f)),
                                            modifier = Modifier
                                                .weight(1f)
                                                .defaultMinSize(minHeight = 48.dp)
                                                .testTag("personnel_extend_btn_${personnel.id}")
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 10.dp, vertical = 12.dp),
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.MoreTime,
                                                    contentDescription = AppStrings.get("extend_leave_btn", currentLang),
                                                    tint = primaryAccent,
                                                    modifier = Modifier.size(17.dp)
                                                )
                                                Spacer(modifier = Modifier.width(5.dp))
                                                Text(
                                                    text = AppStrings.get("extend_leave_btn", currentLang),
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = primaryAccent,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    LiquidGlassActionButton(
                                        text = AppStrings.get("confirm_duty_btn", currentLang),
                                        icon = Icons.Default.Check,
                                        onClick = onConfirmDuty,
                                        isDark = effectiveDark,
                                        primaryAccent = EmeraldSuccess,
                                        isPrimary = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("personnel_quick_action_${personnel.id}")
                                    )
                                }
                            } else {
                                LiquidGlassActionButton(
                                    text = AppStrings.get("add_leave_vacation_btn", currentLang),
                                    icon = Icons.Default.Add,
                                    onClick = { (onQuickAddLeave ?: onEdit).invoke() },
                                    isDark = effectiveDark,
                                    primaryAccent = primaryAccent,
                                    isPrimary = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("personnel_quick_action_${personnel.id}")
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    onClick = onViewHistory,
                                    shape = RoundedCornerShape(13.dp),
                                    color = innerSurface,
                                    border = BorderStroke(1.dp, innerBorder),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("personnel_history_btn_${personnel.id}")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = AppStrings.get("history_btn", currentLang),
                                            tint = textPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = AppStrings.get("history_btn", currentLang),
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = textPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Surface(
                                    onClick = onEdit,
                                    shape = RoundedCornerShape(13.dp),
                                    color = primaryAccent.copy(alpha = if (effectiveDark) 0.16f else 0.10f),
                                    border = BorderStroke(1.dp, primaryAccent.copy(alpha = 0.35f)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = AppStrings.get("edit", currentLang),
                                            tint = primaryAccent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = AppStrings.get("edit", currentLang),
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = primaryAccent,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Surface(
                                    onClick = onDelete,
                                    shape = RoundedCornerShape(13.dp),
                                    color = RedDanger.copy(alpha = if (effectiveDark) 0.16f else 0.08f),
                                    border = BorderStroke(1.dp, RedDanger.copy(alpha = 0.32f)),
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = AppStrings.get("delete", currentLang),
                                            tint = RedDanger,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
            }
        }
        }

            // Floating zero-layout-shift armed hint & pin progress pill (anchored to stationary outer Box)
            AnimatedVisibility(
                visible = (isSwipeArmed || holdProgress > 0.02f) && !isHorizontalDragging,
                enter = if (animationsEnabled) fadeIn(animationSpec = tween(140)) else EnterTransition.None,
                exit = if (animationsEnabled) fadeOut(animationSpec = tween(100)) else ExitTransition.None,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 6.dp)
            ) {
                val isRtlSwipeLeft = SwipeDeleteGestureHelper.expectedDirectionSign(currentLang) < 0f
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = if (isDarkMode) Color(0xEA1E1B24) else Color(0xF2FFF1F2),
                    border = BorderStroke(1.dp, CoralRedAccent.copy(alpha = 0.75f)),
                    shadowElevation = 6.dp
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(3.dp),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = null,
                                tint = CoralRedAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = AppStrings.get(
                                    if (isRtlSwipeLeft) "swipe_delete_hint_rtl" else "swipe_delete_hint_ltr",
                                    currentLang
                                ),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isDarkMode) Color(0xFFFDA4AF) else RedDanger,
                                maxLines = 1
                            )
                        }
                        if (holdProgress > 0.02f) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                LinearProgressIndicator(
                                    progress = { holdProgress },
                                    modifier = Modifier
                                        .width(88.dp)
                                        .height(3.dp)
                                        .clip(RoundedCornerShape(50.dp)),
                                    color = AmberWarning,
                                    trackColor = AmberWarning.copy(alpha = 0.22f)
                                )
                                Text(
                                    text = AppStrings.get(
                                        if (personnel.isPinned) "unpin_hold_hint" else "pin_hold_hint",
                                        currentLang
                                    ),
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberWarning,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
    }
}
