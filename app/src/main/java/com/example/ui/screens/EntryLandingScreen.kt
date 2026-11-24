package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DutyReferenceInfo
import com.example.data.LeaveStatus
import com.example.data.Personnel
import com.example.data.formatRank
import com.example.data.formatWilaya
import com.example.ui.DashboardStats
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.LocalThemePreset
import com.example.util.AppLanguage
import com.example.util.AppStrings
import java.util.Locale

private const val ENTRY_MAX_VISIBLE_PERSONNEL = 4

/**
 * Filters by [searchQuery] and orders personnel so that:
 * 1. Pinned personnel always appear at the top (most recently pinned first).
 * 2. Remaining personnel are ordered by highest days count descending.
 * 3. Total visible items are capped at 4.
 */
fun selectEntryLandingPersonnel(
    personnelList: List<Personnel>,
    dutyReferenceMap: Map<Long, DutyReferenceInfo>,
    searchQuery: String,
    lang: AppLanguage
): List<Personnel> {
    val q = searchQuery.trim().lowercase(Locale.getDefault())
    val filtered = if (q.isEmpty()) {
        personnelList
    } else {
        personnelList.filter { person ->
            person.fullName.lowercase(Locale.getDefault()).contains(q) ||
                person.fullNameLatin.lowercase(Locale.getDefault()).contains(q) ||
                person.registrationNumber.lowercase(Locale.getDefault()).contains(q) ||
                person.rank.lowercase(Locale.getDefault()).contains(q) ||
                formatRank(person.rank, lang).lowercase(Locale.getDefault()).contains(q) ||
                person.wilaya.lowercase(Locale.getDefault()).contains(q) ||
                formatWilaya(person.wilaya, lang, includeCode = true).lowercase(Locale.getDefault()).contains(q)
        }
    }

    return filtered.sortedWith(
        compareByDescending<Personnel> { it.isPinned }
            .thenByDescending { if (it.isPinned) it.pinnedAtMillis else 0L }
            .thenByDescending { person ->
                dutyReferenceMap[person.id]?.daysSinceReference ?: person.daysSinceDutyReturn
            }
            .thenByDescending { it.totalLeaveDays }
            .thenBy { it.getLocalizedName(lang) }
    ).take(ENTRY_MAX_VISIBLE_PERSONNEL)
}

@Composable
fun EntryLandingScreen(
    personnelList: List<Personnel>,
    dutyReferenceMap: Map<Long, DutyReferenceInfo>,
    stats: DashboardStats,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onQuickAddLeave: (Personnel) -> Unit,
    onOpenPersonnelHistory: (Personnel) -> Unit,
    onTogglePin: (Personnel) -> Unit,
    onAddPersonnel: () -> Unit,
    onSeeMore: () -> Unit,
    currentLang: AppLanguage,
    isDarkMode: Boolean,
    animationsEnabled: Boolean = true
) {
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode
    val haptic = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current

    val topPersonnel = remember(personnelList, dutyReferenceMap, searchQuery, currentLang) {
        selectEntryLandingPersonnel(
            personnelList = personnelList,
            dutyReferenceMap = dutyReferenceMap,
            searchQuery = searchQuery,
            lang = currentLang
        )
    }

    val canvasGradient = remember(effectiveDark, preset?.id) {
        if (effectiveDark) {
            Brush.verticalGradient(
                listOf(
                    Color(0xFF06130C),
                    Color(0xFF0A1F14),
                    Color(0xFF07160E)
                )
            )
        } else {
            Brush.verticalGradient(
                listOf(
                    Color(0xFFF6FBF8),
                    Color(0xFFEDF7F1),
                    Color(0xFFF9FCFA)
                )
            )
        }
    }

    val ambientPulse by if (animationsEnabled) {
        val transition = rememberInfiniteTransition(label = "entryAmbientPulse")
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 5200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "entryGlowShift"
        )
    } else {
        remember { mutableFloatStateOf(0.5f) }
    }

    val searchTextPrimary = if (effectiveDark) Color(0xFFECFDF5) else Color(0xFF133A23)
    val searchPlaceholderColor = if (effectiveDark) Color(0xFF86EFAC).copy(alpha = 0.60f) else Color(0xFF3E6850).copy(alpha = 0.72f)
    val forestIconColor = if (effectiveDark) Color(0xFF6EE7B7) else Color(0xFF144528)

    CompositionLocalProvider(
        LocalLayoutDirection provides (if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(canvasGradient)
                .testTag("entry_landing_screen")
        ) {
            // Soft serene emerald ambient orbs behind the canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val topOrbColor = if (effectiveDark) Color(0xFF10B981).copy(alpha = 0.10f) else Color(0xFF34D399).copy(alpha = 0.12f)
                val bottomOrbColor = if (effectiveDark) Color(0xFF059669).copy(alpha = 0.14f) else Color(0xFF6EE7B7).copy(alpha = 0.14f)
                drawCircle(
                    brush = Brush.radialGradient(listOf(topOrbColor, Color.Transparent)),
                    radius = size.width * 0.65f,
                    center = Offset(
                        x = size.width * (0.82f - ambientPulse * 0.08f),
                        y = size.height * 0.12f
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(listOf(bottomOrbColor, Color.Transparent)),
                    radius = size.width * 0.72f,
                    center = Offset(
                        x = size.width * (0.20f + ambientPulse * 0.08f),
                        y = size.height * 0.78f
                    )
                )
            }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                contentAlignment = Alignment.TopCenter
            ) {
                val isTablet = maxWidth >= 600.dp
                val horizontalPad = if (isTablet) 28.dp else 18.dp

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 560.dp)
                        .padding(horizontal = horizontalPad)
                ) {
                    Spacer(modifier = Modifier.height(14.dp))

                    // 1. Top Pill Search Bar (matching reference screenshot)
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = if (effectiveDark) Color(0xFF0F261B).copy(alpha = 0.94f) else Color.White.copy(alpha = 0.97f),
                        border = BorderStroke(
                            1.dp,
                            if (effectiveDark) Color(0xFF34D399).copy(alpha = 0.24f) else Color(0xFF144528).copy(alpha = 0.08f)
                        ),
                        shadowElevation = 10.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = RoundedCornerShape(50.dp),
                                ambientColor = if (effectiveDark) Color(0x88000000) else Color(0x1F0F3D21),
                                spotColor = if (effectiveDark) Color(0x4410B981) else Color(0x24144528)
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = AppStrings.get("search_label", currentLang),
                                tint = forestIconColor,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = AppStrings.get("entry_search_hint", currentLang),
                                        color = searchPlaceholderColor,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                BasicTextField(
                                    value = searchQuery,
                                    onValueChange = onSearchQueryChange,
                                    singleLine = true,
                                    textStyle = TextStyle(
                                        color = searchTextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    cursorBrush = SolidColor(forestIconColor),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("entry_search_field")
                                )
                            }
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { onSearchQueryChange("") },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .testTag("entry_search_clear_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = AppStrings.get("clear_search", currentLang),
                                        tint = searchPlaceholderColor,
                                        modifier = Modifier.size(19.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. Top 4 Personnel Cards (Pinned first, then highest days descending; max 4 cards)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        if (topPersonnel.isEmpty()) {
                            EntryEmptyOrNoResultsCard(
                                hasAnyPersonnel = personnelList.isNotEmpty(),
                                currentLang = currentLang,
                                isDark = effectiveDark,
                                onAddPersonnel = onAddPersonnel,
                                onClearSearch = { onSearchQueryChange("") },
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("entry_personnel_list"),
                                contentPadding = PaddingValues(vertical = 2.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                itemsIndexed(
                                    items = topPersonnel,
                                    key = { _, item -> item.id }
                                ) { _, person ->
                                    val effectiveDays = dutyReferenceMap[person.id]?.daysSinceReference
                                        ?: person.daysSinceDutyReturn
                                    EntryPersonnelCompactCard(
                                        personnel = person,
                                        effectiveDays = effectiveDays,
                                        currentLang = currentLang,
                                        isDark = effectiveDark,
                                        modifier = if (animationsEnabled) Modifier.animateItem() else Modifier,
                                        onCardClick = { onOpenPersonnelHistory(person) },
                                        onPlusClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            onQuickAddLeave(person)
                                        },
                                        onTogglePin = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            onTogglePin(person)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3. Calm & Beautiful Bottom "See more" Window
                    EntrySeeMoreCalmWindow(
                        stats = stats,
                        currentLang = currentLang,
                        onSeeMore = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onSeeMore()
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}

@Composable
private fun EntryPersonnelCompactCard(
    personnel: Personnel,
    effectiveDays: Long,
    currentLang: AppLanguage,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit,
    onPlusClick: () -> Unit,
    onTogglePin: () -> Unit
) {
    val displayName = personnel.getLocalizedName(currentLang)
    val localizedRank = formatRank(personnel.rank, currentLang)

    val daysText = remember(effectiveDays, currentLang) {
        val unit = AppStrings.get("entry_days_served_unit", currentLang)
        if (effectiveDays >= 0L) {
            "$effectiveDays $unit"
        } else {
            val leftPrefix = AppStrings.get("entry_days_left_prefix", currentLang)
            "$leftPrefix ${-effectiveDays} $unit"
        }
    }

    val cardBg = if (isDark) Color(0xFF0E2318).copy(alpha = 0.94f) else Color.White.copy(alpha = 0.97f)
    val titleColor = if (isDark) Color(0xFFECFDF5) else Color(0xFF133A23)
    val subtitleColor = if (isDark) Color(0xFF86EFAC).copy(alpha = 0.78f) else Color(0xFF52796F)
    val borderStroke = when {
        personnel.isPinned -> BorderStroke(1.3.dp, AmberWarning.copy(alpha = 0.75f))
        isDark -> BorderStroke(1.dp, Color(0xFF34D399).copy(alpha = 0.18f))
        else -> BorderStroke(1.dp, Color(0xFF144528).copy(alpha = 0.06f))
    }

    val avatarAccent = when (personnel.status) {
        LeaveStatus.OVERDUE -> Color(0xFFEF4444)
        LeaveStatus.RETURNING_TODAY -> Color(0xFFF59E0B)
        LeaveStatus.AT_HOME -> Color(0xFF0EA5E9)
        LeaveStatus.UPCOMING -> Color(0xFF8B5CF6)
        LeaveStatus.ON_DUTY -> Color(0xFF10B981)
    }

    val initials = remember(displayName) {
        val parts = displayName.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        when {
            parts.size >= 2 -> "${parts[0].take(1)}${parts[1].take(1)}"
            parts.size == 1 -> parts[0].take(2)
            else -> "?"
        }.uppercase(Locale.getDefault())
    }

    val plusInteraction = remember { MutableInteractionSource() }
    val plusPressed by plusInteraction.collectIsPressedAsState()
    val plusScale by animateFloatAsState(
        targetValue = if (plusPressed) 0.90f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium),
        label = "entryPlusScale"
    )

    Surface(
        onClick = onCardClick,
        shape = RoundedCornerShape(22.dp),
        color = cardBg,
        border = borderStroke,
        shadowElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 70.dp)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = if (isDark) Color(0x80000000) else Color(0x160F3D21),
                spotColor = if (isDark) Color(0x3310B981) else Color(0x1C144528)
            )
            .testTag("entry_personnel_card_${personnel.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular Portrait / Avatar (tappable to Pin / Unpin)
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clickable(
                        onClickLabel = AppStrings.get(
                            if (personnel.isPinned) "unpin_action" else "pin_action",
                            currentLang
                        ),
                        onClick = onTogglePin
                    )
                    .testTag("entry_pin_toggle_${personnel.id}"),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(
                                    avatarAccent.copy(alpha = if (isDark) 0.30f else 0.16f),
                                    Color(0xFF00E676).copy(alpha = if (isDark) 0.18f else 0.10f)
                                )
                            )
                        )
                        .border(
                            width = 1.4.dp,
                            color = avatarAccent.copy(alpha = 0.55f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = if (isDark) Color.White else Color(0xFF134E2E),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        maxLines = 1
                    )
                }

                if (personnel.isPinned) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(19.dp)
                            .clip(CircleShape)
                            .background(AmberWarning)
                            .border(1.5.dp, cardBg, CircleShape)
                            .testTag("entry_pin_badge_${personnel.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = AppStrings.get("unpin_action", currentLang),
                            tint = Color.White,
                            modifier = Modifier
                                .size(11.dp)
                                .rotate(30f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Name & Days Subtitle
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = displayName,
                        color = titleColor,
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }

                Text(
                    text = "$daysText • $localizedRank",
                    color = subtitleColor,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Vibrant Mint-Green Circular '+' Button (matching screenshot)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .minimumInteractiveComponentSize()
                    .graphicsLayer {
                        scaleX = plusScale
                        scaleY = plusScale
                    }
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = plusInteraction,
                        indication = null,
                        onClickLabel = AppStrings.get("add_leave_dialog_title", currentLang),
                        onClick = onPlusClick
                    )
                    .testTag("entry_plus_btn_${personnel.id}"),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = CircleShape,
                            ambientColor = Color(0xFF00E676).copy(alpha = 0.45f),
                            spotColor = Color(0xFF10B981).copy(alpha = 0.45f)
                        )
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(
                                    Color(0xFF12F189),
                                    Color(0xFF00D66F)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = AppStrings.get("add_leave_dialog_title", currentLang),
                        tint = Color(0xFF053319),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EntrySeeMoreCalmWindow(
    stats: DashboardStats,
    currentLang: AppLanguage,
    onSeeMore: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.985f else 1f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
        label = "seeMoreCardScale"
    )

    val mintAccent = Color(0xFF7CE8A5)
    val deepCalmBrush = Brush.verticalGradient(
        listOf(
            Color(0xFF0C4A22),
            Color(0xFF083919),
            Color(0xFF062C13)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = Color(0x66062C13),
                spotColor = Color(0x88083919)
            )
            .clip(RoundedCornerShape(28.dp))
            .background(deepCalmBrush)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        mintAccent.copy(alpha = 0.34f),
                        mintAccent.copy(alpha = 0.12f)
                    )
                ),
                shape = RoundedCornerShape(28.dp)
            )
            .clickable(
                interactionSource = interaction,
                indication = androidx.compose.material3.ripple(color = mintAccent),
                onClickLabel = "See more",
                onClick = onSeeMore
            )
            .padding(horizontal = 22.dp, vertical = 18.dp)
            .testTag("entry_see_more_card")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "See more",
                color = mintAccent,
                fontSize = 19.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.4.sp,
                modifier = Modifier.testTag("entry_see_more_button")
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .width(156.dp)
                    .height(1.dp)
                    .background(mintAccent.copy(alpha = 0.28f))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Serene dotted endpoint row matching the reference bottom card
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "${stats.atHome} ${AppStrings.get("stat_on_leave", currentLang)}",
                    color = Color.White,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Canvas(
                    modifier = Modifier
                        .weight(1f)
                        .height(10.dp)
                ) {
                    val dotCount = 9
                    val dotRadius = 2.1.dp.toPx()
                    val usableWidth = (size.width - dotRadius * 2f).coerceAtLeast(1f)
                    val step = if (dotCount > 1) usableWidth / (dotCount - 1) else 0f
                    val yCenter = size.height / 2f
                    for (i in 0 until dotCount) {
                        drawCircle(
                            color = mintAccent.copy(alpha = 0.85f),
                            radius = dotRadius,
                            center = Offset(x = dotRadius + i * step, y = yCenter)
                        )
                    }
                }

                Text(
                    text = "${stats.onDuty} ${AppStrings.get("badge_on_duty", currentLang)}",
                    color = Color.White,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            val summaryText = remember(stats.total, stats.eligible, currentLang) {
                String.format(
                    Locale.getDefault(),
                    AppStrings.get("entry_bottom_summary", currentLang),
                    stats.total,
                    stats.eligible
                )
            }

            Text(
                text = summaryText,
                color = Color(0xFFD1FAE5).copy(alpha = 0.84f),
                fontSize = 11.5.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun EntryEmptyOrNoResultsCard(
    hasAnyPersonnel: Boolean,
    currentLang: AppLanguage,
    isDark: Boolean,
    onAddPersonnel: () -> Unit,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBg = if (isDark) Color(0xFF0E2318).copy(alpha = 0.92f) else Color.White.copy(alpha = 0.96f)
    val titleColor = if (isDark) Color(0xFFECFDF5) else Color(0xFF133A23)
    val subtitleColor = if (isDark) Color(0xFF86EFAC).copy(alpha = 0.78f) else Color(0xFF52796F)

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = cardBg,
        border = BorderStroke(
            1.dp,
            if (isDark) Color(0xFF34D399).copy(alpha = 0.22f) else Color(0xFF144528).copy(alpha = 0.08f)
        ),
        shadowElevation = 6.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("entry_empty_state")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF10B981).copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (hasAnyPersonnel) Icons.Default.Search else Icons.Default.PersonAdd,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(26.dp)
                )
            }

            Text(
                text = AppStrings.get(
                    if (hasAnyPersonnel) "entry_no_search_results" else "entry_empty_title",
                    currentLang
                ),
                color = titleColor,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            if (!hasAnyPersonnel) {
                Text(
                    text = AppStrings.get("entry_empty_sub", currentLang),
                    color = subtitleColor,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = if (hasAnyPersonnel) onClearSearch else onAddPersonnel,
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0E6B36),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .defaultMinSize(minHeight = 48.dp)
                    .testTag(if (hasAnyPersonnel) "entry_clear_search_cta" else "entry_empty_add_btn")
            ) {
                Text(
                    text = AppStrings.get(
                        if (hasAnyPersonnel) "clear_search" else "add_personnel",
                        currentLang
                    ),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                )
            }
        }
    }
}
