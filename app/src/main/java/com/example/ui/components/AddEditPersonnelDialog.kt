package com.example.ui.components

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.Personnel
import com.example.data.algerianLeaveTypes
import com.example.data.algerianRanks
import com.example.data.algerianWilayas
import com.example.data.formatLeaveType
import com.example.data.formatRank
import com.example.data.normalizeRank
import com.example.data.normalizeWilaya
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.LocalThemePreset
import com.example.ui.theme.RoyalBlueAccent
import com.example.ui.theme.SyncDialogLiquidGlassWindow
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.liquidGlassDialogShell
import com.example.ui.theme.liquidGlassPanel
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.SyncDialogSecureWindow
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private data class WilayaEntry(
    val canonical: String,
    val code: String,
    val arabicName: String,
    val latinName: String,
    val normalizedLatin: String
)

private fun foldLatinAccents(input: String): String {
    val decomposed = Normalizer.normalize(input, Normalizer.Form.NFD)
    return decomposed.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "").lowercase(Locale.US)
}

private fun parseWilayaEntry(raw: String): WilayaEntry {
    val canonical = normalizeWilaya(raw)
    val parts = canonical.split(" - ").map { it.trim() }
    val code = parts.getOrNull(0).orEmpty()
    val ar = parts.getOrNull(1).orEmpty().ifBlank { canonical }
    val latin = parts.getOrNull(2).orEmpty().ifBlank { ar }
    return WilayaEntry(
        canonical = canonical,
        code = code,
        arabicName = ar,
        latinName = latin,
        normalizedLatin = foldLatinAccents(latin)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPersonnelDialog(
    personnelToEdit: Personnel?,
    onDismiss: () -> Unit,
    onSave: (
        id: Long,
        fullName: String,
        fullNameLatin: String,
        rank: String,
        registrationNumber: String,
        startDateMillis: Long,
        returnDateMillis: Long,
        isReturnedToDuty: Boolean,
        leaveType: String,
        wilaya: String,
        notes: String
    ) -> Unit,
    isDarkMode: Boolean = false,
    currentLang: AppLanguage = AppLanguage.ARABIC,
    animationsEnabled: Boolean = true,
    isNoScreenshotEnabled: Boolean = false
) {
    val haptic = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current
    val secondNameFocusRequester = remember { FocusRequester() }
    val regNumberFocusRequester = remember { FocusRequester() }
    val isEdit = personnelToEdit != null

    val now = remember { System.currentTimeMillis() }
    fun getOffsetMillis(baseMillis: Long, days: Int): Long {
        val c = Calendar.getInstance().apply {
            timeInMillis = baseMillis
            add(Calendar.DAY_OF_YEAR, days)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return c.timeInMillis
    }

    val hasInitialLeave = personnelToEdit != null &&
        personnelToEdit.startDateMillis > 0L &&
        personnelToEdit.returnDateMillis > 0L

    var fullName by remember { mutableStateOf(personnelToEdit?.fullName ?: "") }
    var fullNameLatin by remember { mutableStateOf(personnelToEdit?.fullNameLatin ?: "") }
    var rank by remember { mutableStateOf(personnelToEdit?.rank?.let { normalizeRank(it) } ?: algerianRanks.first()) }
    var wilaya by remember { mutableStateOf(personnelToEdit?.wilaya?.let { normalizeWilaya(it) } ?: "16 - الجزائر - Alger") }
    var regNumber by remember { mutableStateOf(personnelToEdit?.registrationNumber ?: "") }

    // Optional leave details (not forced during initial personnel creation)
    var hasLeaveDetails by remember { mutableStateOf(hasInitialLeave) }
    var startDateMillis by remember {
        mutableLongStateOf(
            if (hasInitialLeave) personnelToEdit.startDateMillis else Personnel.toStartOfDay(now)
        )
    }
    var returnDateMillis by remember {
        mutableLongStateOf(
            if (hasInitialLeave) personnelToEdit.returnDateMillis else getOffsetMillis(Personnel.toStartOfDay(now), 9)
        )
    }
    var leaveType by remember {
        mutableStateOf(
            personnelToEdit?.leaveType?.takeIf { it.isNotBlank() } ?: algerianLeaveTypes.first()
        )
    }
    var isReturnedToDuty by remember {
        mutableStateOf(personnelToEdit?.isReturnedToDuty ?: true)
    }
    var notes by remember { mutableStateOf(personnelToEdit?.notes ?: "") }

    var rankDropdownExpanded by remember { mutableStateOf(false) }
    var showWilayaPickerDialog by remember { mutableStateOf(false) }
    var showOptionalLeaveDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
    val selectedWilayaEntry = remember(wilaya) { parseWilayaEntry(wilaya) }
    val totalDays = remember(startDateMillis, returnDateMillis, hasLeaveDetails) {
        if (hasLeaveDetails) Personnel.inclusiveLeaveDays(startDateMillis, returnDateMillis) else 0L
    }

    // Liquid Glass Theme Tokens (5% transparency via liquidGlassDialogShell)
    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode
    val primaryColor = preset?.primary ?: if (effectiveDark) CyanGlow else VibrantCyan
    val secondaryColor = preset?.secondary ?: RoyalBlueAccent
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color.White else Color(0xFF0F172A)
    val textSecondary = preset?.textSecondary ?: if (effectiveDark) Color(0xFFCBD5E1) else Color(0xFF475569)
    val glassDividerColor = if (effectiveDark) Color.White.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.65f)

    // Highlighted field colors
    val inputColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = textPrimary,
        unfocusedTextColor = textPrimary,
        focusedContainerColor = if (effectiveDark) primaryColor.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.85f),
        unfocusedContainerColor = if (effectiveDark) Color.White.copy(alpha = 0.06f) else Color.White.copy(alpha = 0.62f),
        focusedBorderColor = primaryColor,
        unfocusedBorderColor = primaryColor.copy(alpha = if (effectiveDark) 0.42f else 0.34f),
        focusedLabelColor = primaryColor,
        unfocusedLabelColor = primaryColor.copy(alpha = if (effectiveDark) 0.92f else 0.88f),
        focusedPlaceholderColor = textSecondary.copy(alpha = 0.65f),
        unfocusedPlaceholderColor = textSecondary.copy(alpha = 0.65f),
        cursorColor = primaryColor
    )

    val entranceState = remember { MutableTransitionState(!animationsEnabled).apply { targetState = true } }
    val dialogLayoutDir = if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        SyncDialogSecureWindow(enabled = isNoScreenshotEnabled)
        SyncDialogLiquidGlassWindow()

        CompositionLocalProvider(LocalLayoutDirection provides dialogLayoutDir) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .imePadding(),
                contentAlignment = Alignment.Center
            ) {
                AnimatedVisibility(
                    visibleState = entranceState,
                    enter = if (animationsEnabled) {
                        fadeIn(tween(200, easing = FastOutSlowInEasing)) + scaleIn(
                            initialScale = 0.93f,
                            animationSpec = spring(
                                dampingRatio = 0.78f,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                    } else EnterTransition.None,
                    exit = if (animationsEnabled) {
                        fadeOut(tween(140)) + scaleOut(targetScale = 0.95f)
                    } else ExitTransition.None
                ) {
                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        color = Color.Transparent,
                        shadowElevation = 0.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 560.dp)
                            .heightIn(max = 680.dp)
                            .liquidGlassDialogShell(
                                shape = RoundedCornerShape(28.dp),
                                cornerRadius = 28.dp,
                                isDark = effectiveDark,
                                primaryAccent = primaryColor,
                                secondaryAccent = secondaryColor
                            )
                            .testTag("add_edit_personnel_dialog")
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // 1) STICKY LIQUID GLASS HEADER
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                primaryColor.copy(alpha = if (effectiveDark) 0.22f else 0.13f),
                                                secondaryColor.copy(alpha = if (effectiveDark) 0.12f else 0.06f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                                    .padding(horizontal = 18.dp, vertical = 14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .liquidGlassPanel(
                                                    shape = RoundedCornerShape(14.dp),
                                                    cornerRadius = 14.dp,
                                                    isDark = effectiveDark,
                                                    accent = primaryColor,
                                                    secondaryAccent = secondaryColor,
                                                    isSelected = true,
                                                    softTintWhenSelected = false
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (isEdit) Icons.Default.Edit else Icons.Default.PersonAdd,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = AppStrings.get(
                                                    if (isEdit) "edit_personnel_title" else "add_personnel_title",
                                                    currentLang
                                                ),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Black,
                                                color = textPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = AppStrings.get(
                                                    if (isEdit) "edit_personnel_sub" else "add_personnel_sub",
                                                    currentLang
                                                ),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 11.sp,
                                                color = textSecondary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }

                                    Surface(
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            onDismiss()
                                        },
                                        shape = CircleShape,
                                        color = Color.Transparent,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .liquidGlassPanel(
                                                shape = CircleShape,
                                                cornerRadius = 24.dp,
                                                isDark = effectiveDark,
                                                accent = primaryColor,
                                                secondaryAccent = secondaryColor
                                            )
                                            .testTag("add_edit_close_button")
                                    ) {
                                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = AppStrings.get("cancel", currentLang),
                                                tint = textSecondary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(color = glassDividerColor, thickness = 1.dp)

                            // 2) SCROLLABLE STRUCTURED FORM CONTENT
                            Column(
                                modifier = Modifier
                                    .weight(1f, fill = false)
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                                    .testTag("dialog_form_scroll")
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                // Animated Validation Error Banner
                                AnimatedVisibility(
                                    visible = errorMessage != null,
                                    enter = if (animationsEnabled) {
                                        expandVertically(
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioNoBouncy,
                                                stiffness = 380f,
                                                visibilityThreshold = IntSize.VisibilityThreshold
                                            )
                                        ) + fadeIn(tween(200))
                                    } else EnterTransition.None,
                                    exit = if (animationsEnabled) {
                                        shrinkVertically(
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioNoBouncy,
                                                stiffness = 440f,
                                                visibilityThreshold = IntSize.VisibilityThreshold
                                            )
                                        ) + fadeOut(tween(140))
                                    } else ExitTransition.None
                                ) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.errorContainer,
                                        shape = RoundedCornerShape(14.dp),
                                        border = BorderStroke(
                                            1.dp,
                                            MaterialTheme.colorScheme.error.copy(alpha = 0.45f)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ErrorOutline,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = errorMessage.orEmpty(),
                                                color = MaterialTheme.colorScheme.onErrorContainer,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                // SECTION CARD 1: Identity & Military Profile (With Highlighted Titles & Field Labels)
                                DialogSectionCard(
                                    title = AppStrings.get("dialog_sec_identity", currentLang),
                                    icon = Icons.Default.Person,
                                    accentColor = primaryColor,
                                    secondaryAccent = secondaryColor,
                                    isDarkMode = effectiveDark
                                ) {
                                    val arabicNameField: @Composable (Boolean) -> Unit = { isFirst ->
                                        HighlightedFieldWrapper(
                                            label = AppStrings.get("full_name_ar_label", currentLang),
                                            accentColor = primaryColor,
                                            isDarkMode = effectiveDark
                                        ) {
                                            OutlinedTextField(
                                                value = fullName,
                                                onValueChange = {
                                                    fullName = it
                                                    errorMessage = null
                                                },
                                                placeholder = { Text(AppStrings.get("full_name_ar_placeholder", currentLang)) },
                                                leadingIcon = {
                                                    Icon(
                                                        imageVector = Icons.Default.Person,
                                                        contentDescription = null,
                                                        tint = primaryColor,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                },
                                                trailingIcon = {
                                                    if (fullName.isNotEmpty()) {
                                                        IconButton(
                                                            onClick = { fullName = "" },
                                                            modifier = Modifier.size(48.dp)
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.Clear,
                                                                contentDescription = AppStrings.get("clear_icon_desc", currentLang),
                                                                tint = textSecondary,
                                                                modifier = Modifier.size(17.dp)
                                                            )
                                                        }
                                                    }
                                                },
                                                keyboardOptions = KeyboardOptions(
                                                    capitalization = KeyboardCapitalization.Words,
                                                    imeAction = ImeAction.Next
                                                ),
                                                keyboardActions = KeyboardActions(
                                                    onNext = {
                                                        if (isFirst) secondNameFocusRequester.requestFocus()
                                                        else regNumberFocusRequester.requestFocus()
                                                    }
                                                ),
                                                singleLine = true,
                                                colors = inputColors,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .then(if (!isFirst) Modifier.focusRequester(secondNameFocusRequester) else Modifier)
                                                    .testTag("full_name_input"),
                                                shape = RoundedCornerShape(14.dp)
                                            )
                                        }
                                    }

                                    val latinNameField: @Composable (Boolean) -> Unit = { isFirst ->
                                        HighlightedFieldWrapper(
                                            label = AppStrings.get("full_name_latin_label", currentLang),
                                            accentColor = primaryColor,
                                            isDarkMode = effectiveDark
                                        ) {
                                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                                OutlinedTextField(
                                                    value = fullNameLatin,
                                                    onValueChange = {
                                                        fullNameLatin = it
                                                        errorMessage = null
                                                    },
                                                    placeholder = { Text(AppStrings.get("full_name_latin_placeholder", currentLang)) },
                                                    leadingIcon = {
                                                        Icon(
                                                            imageVector = Icons.Default.Translate,
                                                            contentDescription = null,
                                                            tint = primaryColor,
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                    },
                                                    trailingIcon = {
                                                        if (fullNameLatin.isNotEmpty()) {
                                                            IconButton(
                                                                onClick = { fullNameLatin = "" },
                                                                modifier = Modifier.size(48.dp)
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.Clear,
                                                                    contentDescription = AppStrings.get("clear_icon_desc", currentLang),
                                                                    tint = textSecondary,
                                                                    modifier = Modifier.size(17.dp)
                                                                )
                                                            }
                                                        }
                                                    },
                                                    keyboardOptions = KeyboardOptions(
                                                        capitalization = KeyboardCapitalization.Words,
                                                        imeAction = ImeAction.Next
                                                    ),
                                                    keyboardActions = KeyboardActions(
                                                        onNext = {
                                                            if (isFirst) secondNameFocusRequester.requestFocus()
                                                            else regNumberFocusRequester.requestFocus()
                                                        }
                                                    ),
                                                    singleLine = true,
                                                    colors = inputColors,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .then(if (!isFirst) Modifier.focusRequester(secondNameFocusRequester) else Modifier)
                                                        .testTag("full_name_latin_input"),
                                                    shape = RoundedCornerShape(14.dp)
                                                )
                                            }
                                        }
                                    }

                                    if (currentLang == AppLanguage.ARABIC) {
                                        arabicNameField(true)
                                        latinNameField(false)
                                    } else {
                                        latinNameField(true)
                                        arabicNameField(false)
                                    }

                                    Text(
                                        text = AppStrings.get("bilingual_name_hint", currentLang),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.5.sp,
                                        color = textSecondary,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )

                                    // Highlighted Military Rank Selector
                                    HighlightedFieldWrapper(
                                        label = AppStrings.get("rank_label", currentLang),
                                        accentColor = primaryColor,
                                        isDarkMode = effectiveDark
                                    ) {
                                        ExposedDropdownMenuBox(
                                            expanded = rankDropdownExpanded,
                                            onExpandedChange = {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                rankDropdownExpanded = !rankDropdownExpanded
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            OutlinedTextField(
                                                value = formatRank(rank, currentLang),
                                                onValueChange = {},
                                                readOnly = true,
                                                leadingIcon = {
                                                    Icon(
                                                        imageVector = Icons.Default.Shield,
                                                        contentDescription = null,
                                                        tint = primaryColor,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                },
                                                trailingIcon = {
                                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = rankDropdownExpanded)
                                                },
                                                colors = inputColors,
                                                modifier = Modifier
                                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                                                    .fillMaxWidth(),
                                                shape = RoundedCornerShape(14.dp)
                                            )
                                            ExposedDropdownMenu(
                                                expanded = rankDropdownExpanded,
                                                onDismissRequest = { rankDropdownExpanded = false },
                                                modifier = Modifier.heightIn(max = 300.dp)
                                            ) {
                                                algerianRanks.forEach { r ->
                                                    val selected = r == rank
                                                    DropdownMenuItem(
                                                        text = {
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                Text(
                                                                    text = formatRank(r, currentLang),
                                                                    fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                                                                    color = if (selected) primaryColor else Color.Unspecified
                                                                )
                                                                if (selected) {
                                                                    Icon(
                                                                        imageVector = Icons.Default.Check,
                                                                        contentDescription = null,
                                                                        tint = primaryColor,
                                                                        modifier = Modifier.size(16.dp)
                                                                    )
                                                                }
                                                            }
                                                        },
                                                        onClick = {
                                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                            rank = r
                                                            rankDropdownExpanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Highlighted Registration Number
                                    HighlightedFieldWrapper(
                                        label = AppStrings.get("reg_number_label", currentLang),
                                        accentColor = primaryColor,
                                        isDarkMode = effectiveDark
                                    ) {
                                        OutlinedTextField(
                                            value = regNumber,
                                            onValueChange = {
                                                regNumber = it
                                                errorMessage = null
                                            },
                                            placeholder = {
                                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                                    Text("1.2026.00000")
                                                }
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Badge,
                                                    contentDescription = null,
                                                    tint = primaryColor,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            },
                                            trailingIcon = {
                                                if (regNumber.isNotEmpty()) {
                                                    IconButton(
                                                        onClick = { regNumber = "" },
                                                        modifier = Modifier.size(48.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Clear,
                                                            contentDescription = AppStrings.get("clear_icon_desc", currentLang),
                                                            tint = textSecondary,
                                                            modifier = Modifier.size(17.dp)
                                                        )
                                                    }
                                                }
                                            },
                                            keyboardOptions = KeyboardOptions(
                                                capitalization = KeyboardCapitalization.Characters,
                                                imeAction = ImeAction.Done
                                            ),
                                            keyboardActions = KeyboardActions(
                                                onDone = { focusManager.clearFocus() }
                                            ),
                                            singleLine = true,
                                            colors = inputColors,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .focusRequester(regNumberFocusRequester)
                                                .testTag("reg_number_input"),
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                    }

                                    // Highlighted Luxury Wilaya Selector Tile (Opens Dedicated Searchable Glass Picker)
                                    HighlightedFieldWrapper(
                                        label = AppStrings.get("wilaya_label", currentLang).replace(":", ""),
                                        accentColor = primaryColor,
                                        isDarkMode = effectiveDark
                                    ) {
                                        Surface(
                                            onClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                showWilayaPickerDialog = true
                                            },
                                            shape = RoundedCornerShape(16.dp),
                                            color = Color.Transparent,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .defaultMinSize(minHeight = 56.dp)
                                                .liquidGlassPanel(
                                                    shape = RoundedCornerShape(16.dp),
                                                    cornerRadius = 16.dp,
                                                    isDark = effectiveDark,
                                                    accent = primaryColor,
                                                    secondaryAccent = secondaryColor,
                                                    isSelected = showWilayaPickerDialog,
                                                    softTintWhenSelected = true
                                                )
                                                .testTag("wilaya_selector_button")
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(38.dp)
                                                            .liquidGlassPanel(
                                                                shape = RoundedCornerShape(12.dp),
                                                                cornerRadius = 12.dp,
                                                                isDark = effectiveDark,
                                                                accent = primaryColor,
                                                                secondaryAccent = secondaryColor,
                                                                isSelected = true,
                                                                softTintWhenSelected = false
                                                            ),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = selectedWilayaEntry.code.ifBlank { "--" },
                                                            color = Color.White,
                                                            fontWeight = FontWeight.Black,
                                                            fontFamily = FontFamily.Monospace,
                                                            fontSize = 14.sp
                                                        )
                                                    }

                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = if (currentLang == AppLanguage.ARABIC) {
                                                                selectedWilayaEntry.arabicName
                                                            } else {
                                                                selectedWilayaEntry.latinName
                                                            },
                                                            fontWeight = FontWeight.ExtraBold,
                                                            fontSize = 14.sp,
                                                            color = textPrimary,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                        Text(
                                                            text = if (currentLang == AppLanguage.ARABIC) {
                                                                selectedWilayaEntry.latinName
                                                            } else {
                                                                selectedWilayaEntry.arabicName
                                                            },
                                                            fontSize = 11.sp,
                                                            color = textSecondary,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                }

                                                Box(
                                                    modifier = Modifier
                                                        .size(34.dp)
                                                        .liquidGlassPanel(
                                                            shape = CircleShape,
                                                            cornerRadius = 17.dp,
                                                            isDark = effectiveDark,
                                                            accent = primaryColor,
                                                            secondaryAccent = secondaryColor,
                                                            isSelected = true,
                                                            softTintWhenSelected = true
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Search,
                                                        contentDescription = AppStrings.get("wilaya_picker_title", currentLang),
                                                        tint = primaryColor,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // OPTIONAL LEAVE ENTRY BUTTON CARD ("إضافة إجازة / عطلة")
                                Surface(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        showOptionalLeaveDialog = true
                                    },
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color.Transparent,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .defaultMinSize(minHeight = 60.dp)
                                        .liquidGlassPanel(
                                            shape = RoundedCornerShape(20.dp),
                                            cornerRadius = 20.dp,
                                            isDark = effectiveDark,
                                            accent = secondaryColor,
                                            secondaryAccent = primaryColor,
                                            isSelected = hasLeaveDetails,
                                            softTintWhenSelected = true
                                        )
                                        .testTag("open_optional_leave_dialog_btn")
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(38.dp)
                                                        .liquidGlassPanel(
                                                            shape = RoundedCornerShape(12.dp),
                                                            cornerRadius = 12.dp,
                                                            isDark = effectiveDark,
                                                            accent = secondaryColor,
                                                            secondaryAccent = primaryColor,
                                                            isSelected = true,
                                                            softTintWhenSelected = false
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.EventAvailable,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }

                                                Column(modifier = Modifier.weight(1f)) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        Text(
                                                            text = AppStrings.get(
                                                                if (hasLeaveDetails) "edit_leave_vacation_btn" else "add_leave_vacation_btn",
                                                                currentLang
                                                            ),
                                                            style = MaterialTheme.typography.titleSmall,
                                                            fontWeight = FontWeight.Black,
                                                            color = if (hasLeaveDetails) secondaryColor else textPrimary,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis,
                                                            modifier = Modifier.weight(1f, fill = false)
                                                        )
                                                        if (!hasLeaveDetails) {
                                                            Box(
                                                                modifier = Modifier.liquidGlassPanel(
                                                                    shape = RoundedCornerShape(50.dp),
                                                                    cornerRadius = 50.dp,
                                                                    isDark = effectiveDark,
                                                                    accent = primaryColor,
                                                                    secondaryAccent = secondaryColor,
                                                                    isSelected = true,
                                                                    softTintWhenSelected = true
                                                                )
                                                            ) {
                                                                Text(
                                                                    text = AppStrings.get("optional_badge", currentLang),
                                                                    fontSize = 10.sp,
                                                                    fontWeight = FontWeight.ExtraBold,
                                                                    color = primaryColor,
                                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                                )
                                                            }
                                                        }
                                                    }
                                                    if (!hasLeaveDetails) {
                                                        Text(
                                                            text = AppStrings.get("optional_leave_sub", currentLang),
                                                            fontSize = 11.sp,
                                                            color = textSecondary,
                                                            maxLines = 2,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                }
                                            }

                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                                contentDescription = null,
                                                tint = secondaryColor,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }

                                        if (hasLeaveDetails) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .liquidGlassPanel(
                                                        shape = RoundedCornerShape(14.dp),
                                                        cornerRadius = 14.dp,
                                                        isDark = effectiveDark,
                                                        accent = primaryColor,
                                                        secondaryAccent = secondaryColor,
                                                        isSelected = true,
                                                        softTintWhenSelected = true
                                                    )
                                                    .padding(horizontal = 12.dp, vertical = 9.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column(
                                                    modifier = Modifier.weight(1f),
                                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                                ) {
                                                    Text(
                                                        text = "${formatLeaveType(leaveType, currentLang)} • $totalDays ${AppStrings.get("days", currentLang)}",
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = primaryColor,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                                        Text(
                                                            text = "${sdf.format(Date(startDateMillis))} → ${sdf.format(Date(returnDateMillis))}",
                                                            fontFamily = FontFamily.Monospace,
                                                            fontSize = 11.5.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = textPrimary,
                                                            maxLines = 1
                                                        )
                                                    }
                                                }

                                                IconButton(
                                                    onClick = {
                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                        hasLeaveDetails = false
                                                        if (!isEdit) isReturnedToDuty = true
                                                    },
                                                    modifier = Modifier
                                                        .size(48.dp)
                                                        .testTag("clear_optional_leave_btn")
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.DeleteOutline,
                                                        contentDescription = AppStrings.get("remove_leave_vacation", currentLang),
                                                        tint = textSecondary,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // SECTION CARD 2: Current Duty Status & Notes (Highlighted)
                                DialogSectionCard(
                                    title = AppStrings.get("dialog_sec_status_notes", currentLang),
                                    icon = Icons.Default.CheckCircle,
                                    accentColor = EmeraldSuccess,
                                    secondaryAccent = primaryColor,
                                    isDarkMode = effectiveDark
                                ) {
                                    val statusDotColor by animateColorAsState(
                                        targetValue = if (isReturnedToDuty) EmeraldSuccess else primaryColor,
                                        animationSpec = if (animationsEnabled) tween(220) else tween(0),
                                        label = "dutyDotColor"
                                    )

                                    Surface(
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            isReturnedToDuty = !isReturnedToDuty
                                        },
                                        shape = RoundedCornerShape(14.dp),
                                        color = Color.Transparent,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .liquidGlassPanel(
                                                shape = RoundedCornerShape(14.dp),
                                                cornerRadius = 14.dp,
                                                isDark = effectiveDark,
                                                accent = EmeraldSuccess,
                                                secondaryAccent = primaryColor,
                                                isSelected = isReturnedToDuty,
                                                softTintWhenSelected = true
                                            )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(10.dp)
                                                        .clip(CircleShape)
                                                        .background(statusDotColor)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(
                                                    text = AppStrings.get("on_duty_switch_label", currentLang),
                                                    fontSize = 12.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = textPrimary,
                                                    maxLines = 2
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Switch(
                                                checked = isReturnedToDuty,
                                                onCheckedChange = {
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    isReturnedToDuty = it
                                                },
                                                colors = SwitchDefaults.colors(
                                                    checkedThumbColor = Color.White,
                                                    checkedTrackColor = EmeraldSuccess,
                                                    uncheckedThumbColor = Color.White,
                                                    uncheckedTrackColor = if (effectiveDark) Color(0xFF475569) else Color(0xFFCBD5E1)
                                                )
                                            )
                                        }
                                    }

                                    HighlightedFieldWrapper(
                                        label = AppStrings.get("notes_label", currentLang).replace(":", ""),
                                        accentColor = EmeraldSuccess,
                                        isDarkMode = effectiveDark
                                    ) {
                                        OutlinedTextField(
                                            value = notes,
                                            onValueChange = { notes = it },
                                            placeholder = { Text(AppStrings.get("notes_placeholder", currentLang)) },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.EditNote,
                                                    contentDescription = null,
                                                    tint = EmeraldSuccess,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            },
                                            colors = inputColors,
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(14.dp),
                                            maxLines = 3
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = glassDividerColor, thickness = 1.dp)

                            // 3) STICKY LIQUID GLASS ACTION FOOTER (Always reachable 48dp buttons)
                            val saveInteraction = remember { MutableInteractionSource() }
                            val savePressed by saveInteraction.collectIsPressedAsState()
                            val saveScale by animateFloatAsState(
                                targetValue = if (savePressed) 0.97f else 1f,
                                animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
                                label = "saveBtnScale"
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    onClick = {
                                        if ((fullName.isBlank() && fullNameLatin.isBlank()) || regNumber.isBlank()) {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            errorMessage = AppStrings.get("fill_required", currentLang)
                                            return@Surface
                                        }
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onSave(
                                            personnelToEdit?.id ?: 0L,
                                            fullName.trim(),
                                            fullNameLatin.trim(),
                                            rank,
                                            regNumber.trim(),
                                            if (hasLeaveDetails) startDateMillis else 0L,
                                            if (hasLeaveDetails) returnDateMillis else 0L,
                                            if (hasLeaveDetails) isReturnedToDuty else true,
                                            if (hasLeaveDetails) leaveType else "",
                                            wilaya,
                                            notes.trim()
                                        )
                                    },
                                    interactionSource = saveInteraction,
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color.Transparent,
                                    modifier = Modifier
                                        .weight(1.4f)
                                        .height(48.dp)
                                        .graphicsLayer {
                                            if (animationsEnabled) {
                                                scaleX = saveScale
                                                scaleY = saveScale
                                            }
                                        }
                                        .shadow(
                                            elevation = 8.dp,
                                            shape = RoundedCornerShape(16.dp),
                                            ambientColor = primaryColor.copy(alpha = 0.35f),
                                            spotColor = secondaryColor.copy(alpha = 0.35f)
                                        )
                                        .liquidGlassPanel(
                                            shape = RoundedCornerShape(16.dp),
                                            cornerRadius = 16.dp,
                                            isDark = effectiveDark,
                                            accent = primaryColor,
                                            secondaryAccent = secondaryColor,
                                            isSelected = true,
                                            softTintWhenSelected = false
                                        )
                                        .testTag("save_personnel_button")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 14.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = AppStrings.get(if (isEdit) "save_changes" else "save", currentLang),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 13.5.sp,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Surface(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onDismiss()
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color.Transparent,
                                    modifier = Modifier
                                        .weight(0.8f)
                                        .height(48.dp)
                                        .liquidGlassPanel(
                                            shape = RoundedCornerShape(16.dp),
                                            cornerRadius = 16.dp,
                                            isDark = effectiveDark,
                                            accent = primaryColor,
                                            secondaryAccent = secondaryColor
                                        )
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                        Text(
                                            text = AppStrings.get("cancel", currentLang),
                                            color = textSecondary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Secondary Window 1: Luxury Wilaya Picker (Search by Number 01-69 or Arabic/Latin Name)
    if (showWilayaPickerDialog) {
        LuxuryWilayaPickerDialog(
            selectedWilaya = wilaya,
            onSelectWilaya = { chosen ->
                wilaya = chosen
                showWilayaPickerDialog = false
            },
            onDismiss = { showWilayaPickerDialog = false },
            effectiveDark = effectiveDark,
            primaryColor = primaryColor,
            secondaryColor = secondaryColor,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            inputColors = inputColors,
            currentLang = currentLang,
            animationsEnabled = animationsEnabled,
            isNoScreenshotEnabled = isNoScreenshotEnabled
        )
    }

    // Secondary Window 2: Optional Leave Type & Schedule Dialog ("إضافة إجازة / عطلة")
    if (showOptionalLeaveDialog) {
        OptionalLeaveEditorDialog(
            initialLeaveType = leaveType,
            initialStartMillis = startDateMillis,
            initialReturnMillis = returnDateMillis,
            hasExistingLeave = hasLeaveDetails,
            onConfirmLeave = { chosenType, chosenStart, chosenReturn ->
                leaveType = chosenType
                startDateMillis = chosenStart
                returnDateMillis = chosenReturn
                hasLeaveDetails = true
                if (!isEdit) {
                    isReturnedToDuty = false
                }
                showOptionalLeaveDialog = false
            },
            onRemoveLeave = {
                hasLeaveDetails = false
                if (!isEdit) {
                    isReturnedToDuty = true
                }
                showOptionalLeaveDialog = false
            },
            onDismiss = { showOptionalLeaveDialog = false },
            effectiveDark = effectiveDark,
            primaryColor = primaryColor,
            secondaryColor = secondaryColor,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            currentLang = currentLang,
            animationsEnabled = animationsEnabled,
            isNoScreenshotEnabled = isNoScreenshotEnabled
        )
    }
}

@Composable
private fun LuxuryWilayaPickerDialog(
    selectedWilaya: String,
    onSelectWilaya: (String) -> Unit,
    onDismiss: () -> Unit,
    effectiveDark: Boolean,
    primaryColor: Color,
    secondaryColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    inputColors: androidx.compose.material3.TextFieldColors,
    currentLang: AppLanguage,
    animationsEnabled: Boolean,
    isNoScreenshotEnabled: Boolean
) {
    val haptic = LocalHapticFeedback.current
    var searchQuery by remember { mutableStateOf("") }
    val allEntries = remember { algerianWilayas.map { parseWilayaEntry(it) } }
    val selectedCanonical = remember(selectedWilaya) { normalizeWilaya(selectedWilaya) }

    val filteredEntries = remember(searchQuery, allEntries) {
        val rawQ = searchQuery.trim()
        if (rawQ.isEmpty()) {
            allEntries
        } else {
            val foldedQ = foldLatinAccents(rawQ)
            val paddedDigits = rawQ.takeIf { it.all { ch -> ch.isDigit() } }?.padStart(2, '0')
            allEntries.filter { entry ->
                entry.code.contains(rawQ, ignoreCase = true) ||
                    (paddedDigits != null && entry.code == paddedDigits) ||
                    entry.arabicName.contains(rawQ, ignoreCase = true) ||
                    entry.latinName.contains(rawQ, ignoreCase = true) ||
                    entry.normalizedLatin.contains(foldedQ) ||
                    entry.canonical.contains(rawQ, ignoreCase = true)
            }.sortedWith(
                compareByDescending<WilayaEntry> {
                    paddedDigits != null && it.code == paddedDigits
                }.thenByDescending {
                    it.code.startsWith(rawQ)
                }
            )
        }
    }

    val dialogLayoutDir = if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
    val glassDividerColor = if (effectiveDark) Color.White.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.65f)
    val entranceState = remember { MutableTransitionState(!animationsEnabled).apply { targetState = true } }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        SyncDialogSecureWindow(enabled = isNoScreenshotEnabled)
        SyncDialogLiquidGlassWindow()

        CompositionLocalProvider(LocalLayoutDirection provides dialogLayoutDir) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .imePadding(),
                contentAlignment = Alignment.Center
            ) {
                AnimatedVisibility(
                    visibleState = entranceState,
                    enter = if (animationsEnabled) {
                        fadeIn(tween(180)) + scaleIn(initialScale = 0.94f)
                    } else EnterTransition.None,
                    exit = if (animationsEnabled) {
                        fadeOut(tween(120)) + scaleOut(targetScale = 0.96f)
                    } else ExitTransition.None
                ) {
                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        color = Color.Transparent,
                        shadowElevation = 0.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 520.dp)
                            .heightIn(max = 600.dp)
                            .liquidGlassDialogShell(
                                shape = RoundedCornerShape(28.dp),
                                cornerRadius = 28.dp,
                                isDark = effectiveDark,
                                primaryAccent = primaryColor,
                                secondaryAccent = secondaryColor
                            )
                            .testTag("luxury_wilaya_picker_dialog")
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Header
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                primaryColor.copy(alpha = if (effectiveDark) 0.24f else 0.14f),
                                                secondaryColor.copy(alpha = if (effectiveDark) 0.12f else 0.06f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                                    .padding(horizontal = 18.dp, vertical = 14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .liquidGlassPanel(
                                                    shape = RoundedCornerShape(13.dp),
                                                    cornerRadius = 13.dp,
                                                    isDark = effectiveDark,
                                                    accent = primaryColor,
                                                    secondaryAccent = secondaryColor,
                                                    isSelected = true,
                                                    softTintWhenSelected = false
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.LocationOn,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(21.dp)
                                            )
                                        }

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(
                                                    text = AppStrings.get("wilaya_picker_title", currentLang),
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Black,
                                                    color = textPrimary,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Box(
                                                    modifier = Modifier.liquidGlassPanel(
                                                        shape = RoundedCornerShape(50.dp),
                                                        cornerRadius = 50.dp,
                                                        isDark = effectiveDark,
                                                        accent = primaryColor,
                                                        secondaryAccent = secondaryColor,
                                                        isSelected = true,
                                                        softTintWhenSelected = true
                                                    )
                                                ) {
                                                    Text(
                                                        text = "${filteredEntries.size}",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Black,
                                                        color = primaryColor,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = AppStrings.get("wilaya_picker_sub", currentLang),
                                                fontSize = 10.5.sp,
                                                color = textSecondary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }

                                    Surface(
                                        onClick = onDismiss,
                                        shape = CircleShape,
                                        color = Color.Transparent,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .liquidGlassPanel(
                                                shape = CircleShape,
                                                cornerRadius = 24.dp,
                                                isDark = effectiveDark,
                                                accent = primaryColor,
                                                secondaryAccent = secondaryColor
                                            )
                                            .testTag("wilaya_picker_close_btn")
                                    ) {
                                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = AppStrings.get("close", currentLang),
                                                tint = textSecondary
                                            )
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(color = glassDividerColor)

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Luxury Search Box (Number or Name)
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    placeholder = {
                                        Text(
                                            AppStrings.get("wilaya_search_placeholder", currentLang),
                                            fontSize = 12.5.sp,
                                            color = textSecondary
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = null,
                                            tint = primaryColor
                                        )
                                    },
                                    trailingIcon = {
                                        if (searchQuery.isNotEmpty()) {
                                            IconButton(onClick = { searchQuery = "" }) {
                                                Icon(
                                                    imageVector = Icons.Default.Clear,
                                                    contentDescription = AppStrings.get("clear_icon_desc", currentLang),
                                                    tint = textSecondary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(16.dp),
                                    colors = inputColors,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("wilaya_dropdown_search_input")
                                )

                                if (filteredEntries.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                            .liquidGlassPanel(
                                                shape = RoundedCornerShape(18.dp),
                                                cornerRadius = 18.dp,
                                                isDark = effectiveDark,
                                                accent = primaryColor,
                                                secondaryAccent = secondaryColor
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.padding(16.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.LocationOn,
                                                contentDescription = null,
                                                tint = primaryColor.copy(alpha = 0.7f),
                                                modifier = Modifier.size(32.dp)
                                            )
                                            Text(
                                                text = AppStrings.get("wilaya_not_found", currentLang),
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = textSecondary,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(min = 120.dp, max = 380.dp)
                                            .testTag("wilaya_picker_list"),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(filteredEntries, key = { it.canonical }) { entry ->
                                            val isSelected = entry.canonical == selectedCanonical
                                            Surface(
                                                onClick = {
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    onSelectWilaya(entry.canonical)
                                                },
                                                shape = RoundedCornerShape(16.dp),
                                                color = Color.Transparent,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .liquidGlassPanel(
                                                        shape = RoundedCornerShape(16.dp),
                                                        cornerRadius = 16.dp,
                                                        isDark = effectiveDark,
                                                        accent = primaryColor,
                                                        secondaryAccent = secondaryColor,
                                                        isSelected = isSelected,
                                                        softTintWhenSelected = true
                                                    )
                                                    .testTag("wilaya_item_${entry.code}")
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                                ) {
                                                    // Glowing 2-Digit Wilaya Code Badge
                                                    Box(
                                                        modifier = Modifier
                                                            .size(40.dp)
                                                            .liquidGlassPanel(
                                                                shape = RoundedCornerShape(12.dp),
                                                                cornerRadius = 12.dp,
                                                                isDark = effectiveDark,
                                                                accent = primaryColor,
                                                                secondaryAccent = secondaryColor,
                                                                isSelected = isSelected,
                                                                softTintWhenSelected = !isSelected
                                                            ),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            text = entry.code,
                                                            fontFamily = FontFamily.Monospace,
                                                            fontWeight = FontWeight.Black,
                                                            fontSize = 14.sp,
                                                            color = if (isSelected) Color.White else primaryColor
                                                        )
                                                    }

                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = if (currentLang == AppLanguage.ARABIC) {
                                                                entry.arabicName
                                                            } else {
                                                                entry.latinName
                                                            },
                                                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                                            fontSize = 14.sp,
                                                            color = if (isSelected) primaryColor else textPrimary,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                        Text(
                                                            text = if (currentLang == AppLanguage.ARABIC) {
                                                                entry.latinName
                                                            } else {
                                                                entry.arabicName
                                                            },
                                                            fontSize = 11.sp,
                                                            color = textSecondary,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }

                                                    if (isSelected) {
                                                        Icon(
                                                            imageVector = Icons.Default.Check,
                                                            contentDescription = null,
                                                            tint = primaryColor,
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionalLeaveEditorDialog(
    initialLeaveType: String,
    initialStartMillis: Long,
    initialReturnMillis: Long,
    hasExistingLeave: Boolean,
    onConfirmLeave: (leaveType: String, startMillis: Long, returnMillis: Long) -> Unit,
    onRemoveLeave: () -> Unit,
    onDismiss: () -> Unit,
    effectiveDark: Boolean,
    primaryColor: Color,
    secondaryColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    currentLang: AppLanguage,
    animationsEnabled: Boolean,
    isNoScreenshotEnabled: Boolean
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    fun getOffsetMillis(baseMillis: Long, days: Int): Long {
        val c = Calendar.getInstance().apply {
            timeInMillis = baseMillis
            add(Calendar.DAY_OF_YEAR, days)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return c.timeInMillis
    }

    val todayStart = remember { Personnel.toStartOfDay(System.currentTimeMillis()) }
    var draftLeaveType by remember {
        mutableStateOf(initialLeaveType.takeIf { it.isNotBlank() } ?: algerianLeaveTypes.first())
    }
    var draftStartMillis by remember {
        mutableLongStateOf(if (initialStartMillis > 0L) initialStartMillis else todayStart)
    }
    var draftReturnMillis by remember {
        mutableLongStateOf(
            if (initialReturnMillis > 0L) initialReturnMillis else getOffsetMillis(draftStartMillis, 9)
        )
    }

    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }

    val startCal = Calendar.getInstance().apply { timeInMillis = draftStartMillis }
    val startDatePickerDialog = remember(draftStartMillis) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val c = Calendar.getInstance()
                c.set(year, month, dayOfMonth, 0, 0, 0)
                c.set(Calendar.MILLISECOND, 0)
                draftStartMillis = c.timeInMillis
                if (draftReturnMillis < draftStartMillis) {
                    draftReturnMillis = getOffsetMillis(draftStartMillis, 9)
                }
            },
            startCal.get(Calendar.YEAR),
            startCal.get(Calendar.MONTH),
            startCal.get(Calendar.DAY_OF_MONTH)
        )
    }

    val returnCal = Calendar.getInstance().apply { timeInMillis = draftReturnMillis }
    val returnDatePickerDialog = remember(draftReturnMillis, draftStartMillis) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val c = Calendar.getInstance()
                c.set(year, month, dayOfMonth, 0, 0, 0)
                c.set(Calendar.MILLISECOND, 0)
                draftReturnMillis = if (c.timeInMillis < draftStartMillis) draftStartMillis else c.timeInMillis
            },
            returnCal.get(Calendar.YEAR),
            returnCal.get(Calendar.MONTH),
            returnCal.get(Calendar.DAY_OF_MONTH)
        )
    }

    val draftTotalDays = remember(draftStartMillis, draftReturnMillis) {
        Personnel.inclusiveLeaveDays(draftStartMillis, draftReturnMillis)
    }

    val dialogLayoutDir = if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
    val glassDividerColor = if (effectiveDark) Color.White.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.65f)
    val entranceState = remember { MutableTransitionState(!animationsEnabled).apply { targetState = true } }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        SyncDialogSecureWindow(enabled = isNoScreenshotEnabled)
        SyncDialogLiquidGlassWindow()

        CompositionLocalProvider(LocalLayoutDirection provides dialogLayoutDir) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedVisibility(
                    visibleState = entranceState,
                    enter = if (animationsEnabled) {
                        fadeIn(tween(180)) + scaleIn(initialScale = 0.94f)
                    } else EnterTransition.None,
                    exit = if (animationsEnabled) {
                        fadeOut(tween(120)) + scaleOut(targetScale = 0.96f)
                    } else ExitTransition.None
                ) {
                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        color = Color.Transparent,
                        shadowElevation = 0.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 540.dp)
                            .heightIn(max = 640.dp)
                            .liquidGlassDialogShell(
                                shape = RoundedCornerShape(28.dp),
                                cornerRadius = 28.dp,
                                isDark = effectiveDark,
                                primaryAccent = secondaryColor,
                                secondaryAccent = primaryColor
                            )
                            .testTag("optional_leave_editor_dialog")
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Header
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                secondaryColor.copy(alpha = if (effectiveDark) 0.24f else 0.14f),
                                                primaryColor.copy(alpha = if (effectiveDark) 0.12f else 0.06f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                                    .padding(horizontal = 18.dp, vertical = 14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .liquidGlassPanel(
                                                    shape = RoundedCornerShape(13.dp),
                                                    cornerRadius = 13.dp,
                                                    isDark = effectiveDark,
                                                    accent = secondaryColor,
                                                    secondaryAccent = primaryColor,
                                                    isSelected = true,
                                                    softTintWhenSelected = false
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.EventAvailable,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(21.dp)
                                            )
                                        }

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = AppStrings.get("add_leave_vacation_btn", currentLang),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Black,
                                                color = textPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = AppStrings.get("dialog_sec_schedule", currentLang),
                                                fontSize = 11.sp,
                                                color = textSecondary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }

                                    Surface(
                                        onClick = onDismiss,
                                        shape = CircleShape,
                                        color = Color.Transparent,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .liquidGlassPanel(
                                                shape = CircleShape,
                                                cornerRadius = 24.dp,
                                                isDark = effectiveDark,
                                                accent = secondaryColor,
                                                secondaryAccent = primaryColor
                                            )
                                    ) {
                                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = AppStrings.get("close", currentLang),
                                                tint = textSecondary
                                            )
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(color = glassDividerColor)

                            // Scrollable Leave Details Body
                            Column(
                                modifier = Modifier
                                    .weight(1f, fill = false)
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                // Leave Type 2x2 Glass Chips
                                DialogSectionCard(
                                    title = AppStrings.get("leave_type_label", currentLang),
                                    icon = Icons.Default.AddCircleOutline,
                                    accentColor = primaryColor,
                                    secondaryAccent = secondaryColor,
                                    isDarkMode = effectiveDark
                                ) {
                                    val leaveRows = remember { algerianLeaveTypes.chunked(2) }
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        leaveRows.forEach { rowItems ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                rowItems.forEach { lt ->
                                                    val isSelected = lt == draftLeaveType
                                                    TactileSelectorChip(
                                                        label = formatLeaveType(lt, currentLang),
                                                        selected = isSelected,
                                                        accentColor = primaryColor,
                                                        secondaryAccent = secondaryColor,
                                                        isDarkMode = effectiveDark,
                                                        animationsEnabled = animationsEnabled,
                                                        onClick = {
                                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                            draftLeaveType = lt
                                                        },
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // Dates & Quick Duration Chips
                                DialogSectionCard(
                                    title = AppStrings.get("dialog_sec_schedule", currentLang),
                                    icon = Icons.Default.CalendarMonth,
                                    accentColor = secondaryColor,
                                    secondaryAccent = primaryColor,
                                    isDarkMode = effectiveDark
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        InteractiveDateTile(
                                            label = AppStrings.get("start_date_label", currentLang),
                                            dateText = sdf.format(Date(draftStartMillis)),
                                            accentColor = primaryColor,
                                            secondaryAccent = secondaryColor,
                                            isDarkMode = effectiveDark,
                                            animationsEnabled = animationsEnabled,
                                            onClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                startDatePickerDialog.show()
                                            },
                                            modifier = Modifier.weight(1f)
                                        )

                                        InteractiveDateTile(
                                            label = AppStrings.get("return_date_label", currentLang),
                                            dateText = sdf.format(Date(draftReturnMillis)),
                                            accentColor = secondaryColor,
                                            secondaryAccent = primaryColor,
                                            isDarkMode = effectiveDark,
                                            animationsEnabled = animationsEnabled,
                                            onClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                returnDatePickerDialog.show()
                                            },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    HighlightedFieldLabelPill(
                                        label = AppStrings.get("quick_duration_add", currentLang),
                                        accentColor = primaryColor,
                                        isDarkMode = effectiveDark
                                    )

                                    val durationPills = listOf(
                                        2 to AppStrings.get("dur_48h", currentLang),
                                        3 to AppStrings.get("dur_72h", currentLang),
                                        4 to AppStrings.get("dur_4d", currentLang),
                                        8 to AppStrings.get("dur_8d", currentLang),
                                        10 to AppStrings.get("dur_10d", currentLang)
                                    )
                                    val durationRows = remember(durationPills) { durationPills.chunked(3) }
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        durationRows.forEach { rowItems ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                rowItems.forEach { (days, label) ->
                                                    val isSelected = draftTotalDays == days.toLong()
                                                    TactileDurationChip(
                                                        label = label,
                                                        selected = isSelected,
                                                        accentColor = primaryColor,
                                                        secondaryAccent = secondaryColor,
                                                        isDarkMode = effectiveDark,
                                                        animationsEnabled = animationsEnabled,
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .testTag("quick_duration_chip_$days"),
                                                        onClick = {
                                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                            draftReturnMillis = getOffsetMillis(
                                                                draftStartMillis,
                                                                (days - 1).coerceAtLeast(0)
                                                            )
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Leave Duration Summary Pill
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .liquidGlassPanel(
                                                shape = RoundedCornerShape(14.dp),
                                                cornerRadius = 14.dp,
                                                isDark = effectiveDark,
                                                accent = primaryColor,
                                                secondaryAccent = secondaryColor,
                                                isSelected = true,
                                                softTintWhenSelected = true
                                            )
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Schedule,
                                                contentDescription = null,
                                                tint = primaryColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = AppStrings.get("leave_duration", currentLang),
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = primaryColor
                                            )
                                        }

                                        Box(
                                            modifier = Modifier.liquidGlassPanel(
                                                shape = RoundedCornerShape(50.dp),
                                                cornerRadius = 50.dp,
                                                isDark = effectiveDark,
                                                accent = primaryColor,
                                                secondaryAccent = secondaryColor,
                                                isSelected = true,
                                                softTintWhenSelected = false
                                            )
                                        ) {
                                            AnimatedContent(
                                                targetState = draftTotalDays,
                                                transitionSpec = {
                                                    if (animationsEnabled) {
                                                        fadeIn(tween(180)) togetherWith fadeOut(tween(120))
                                                    } else {
                                                        EnterTransition.None togetherWith ExitTransition.None
                                                    }
                                                },
                                                label = "draftTotalDaysCounter"
                                            ) { daysVal ->
                                                Text(
                                                    text = "$daysVal ${AppStrings.get("days", currentLang)}",
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 12.sp,
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                if (hasExistingLeave) {
                                    Surface(
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            onRemoveLeave()
                                        },
                                        shape = RoundedCornerShape(14.dp),
                                        color = Color.Transparent,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .liquidGlassPanel(
                                                shape = RoundedCornerShape(14.dp),
                                                cornerRadius = 14.dp,
                                                isDark = effectiveDark,
                                                accent = AmberWarning,
                                                secondaryAccent = AmberWarning,
                                                isSelected = true,
                                                softTintWhenSelected = true
                                            )
                                            .testTag("remove_optional_leave_btn")
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DeleteOutline,
                                                contentDescription = null,
                                                tint = AmberWarning,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = AppStrings.get("remove_leave_vacation", currentLang),
                                                color = AmberWarning,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 12.5.sp
                                            )
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(color = glassDividerColor)

                            // Footer Buttons
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    onClick = onDismiss,
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color.Transparent,
                                    modifier = Modifier
                                        .weight(0.85f)
                                        .height(48.dp)
                                        .liquidGlassPanel(
                                            shape = RoundedCornerShape(16.dp),
                                            cornerRadius = 16.dp,
                                            isDark = effectiveDark,
                                            accent = primaryColor,
                                            secondaryAccent = secondaryColor
                                        )
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                        Text(
                                            text = AppStrings.get("cancel", currentLang),
                                            fontWeight = FontWeight.Bold,
                                            color = textSecondary
                                        )
                                    }
                                }

                                Surface(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onConfirmLeave(draftLeaveType, draftStartMillis, draftReturnMillis)
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color.Transparent,
                                    modifier = Modifier
                                        .weight(1.35f)
                                        .height(48.dp)
                                        .shadow(
                                            elevation = 8.dp,
                                            shape = RoundedCornerShape(16.dp),
                                            ambientColor = EmeraldSuccess.copy(alpha = 0.35f),
                                            spotColor = EmeraldSuccess.copy(alpha = 0.35f)
                                        )
                                        .liquidGlassPanel(
                                            shape = RoundedCornerShape(16.dp),
                                            cornerRadius = 16.dp,
                                            isDark = effectiveDark,
                                            accent = EmeraldSuccess,
                                            secondaryAccent = Color(0xFF059669),
                                            isSelected = true,
                                            softTintWhenSelected = false
                                        )
                                        .testTag("confirm_optional_leave_btn")
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = AppStrings.get("confirm_leave_details", currentLang),
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogSectionCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    secondaryAccent: Color,
    isDarkMode: Boolean,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlassPanel(
                shape = RoundedCornerShape(20.dp),
                cornerRadius = 20.dp,
                isDark = isDarkMode,
                accent = accentColor,
                secondaryAccent = secondaryAccent
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Highlighted Section Title Banner
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlassPanel(
                        shape = RoundedCornerShape(14.dp),
                        cornerRadius = 14.dp,
                        isDark = isDarkMode,
                        accent = accentColor,
                        secondaryAccent = secondaryAccent,
                        isSelected = true,
                        softTintWhenSelected = true
                    )
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .liquidGlassPanel(
                            shape = RoundedCornerShape(9.dp),
                            cornerRadius = 9.dp,
                            isDark = isDarkMode,
                            accent = accentColor,
                            secondaryAccent = secondaryAccent,
                            isSelected = true,
                            softTintWhenSelected = false
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(9.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Black,
                    color = accentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            content()
        }
    }
}

@Composable
private fun HighlightedFieldLabelPill(
    label: String,
    accentColor: Color,
    isDarkMode: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(accentColor.copy(alpha = if (isDarkMode) 0.16f else 0.10f))
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(accentColor)
        )
        Text(
            text = label,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.ExtraBold,
            color = accentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun HighlightedFieldWrapper(
    label: String,
    accentColor: Color,
    isDarkMode: Boolean,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        HighlightedFieldLabelPill(
            label = label,
            accentColor = accentColor,
            isDarkMode = isDarkMode
        )
        content()
    }
}

@Composable
private fun TactileSelectorChip(
    label: String,
    selected: Boolean,
    accentColor: Color,
    secondaryAccent: Color,
    isDarkMode: Boolean,
    animationsEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
        label = "selectorChipScale"
    )
    val labelColor = when {
        selected -> accentColor
        isDarkMode -> Color(0xFFE2E8F0)
        else -> Color(0xFF1E293B)
    }

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(14.dp),
        color = Color.Transparent,
        modifier = modifier
            .defaultMinSize(minHeight = 44.dp)
            .graphicsLayer {
                if (animationsEnabled) {
                    scaleX = scale
                    scaleY = scale
                }
            }
            .liquidGlassPanel(
                shape = RoundedCornerShape(14.dp),
                cornerRadius = 14.dp,
                isDark = isDarkMode,
                accent = accentColor,
                secondaryAccent = secondaryAccent,
                isSelected = selected,
                softTintWhenSelected = true
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
            }
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                color = labelColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun InteractiveDateTile(
    label: String,
    dateText: String,
    accentColor: Color,
    secondaryAccent: Color,
    isDarkMode: Boolean,
    animationsEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
        label = "dateTileScale"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
        modifier = modifier
            .defaultMinSize(minHeight = 64.dp)
            .graphicsLayer {
                if (animationsEnabled) {
                    scaleX = scale
                    scaleY = scale
                }
            }
            .liquidGlassPanel(
                shape = RoundedCornerShape(16.dp),
                cornerRadius = 16.dp,
                isDark = isDarkMode,
                accent = accentColor,
                secondaryAccent = secondaryAccent
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 11.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Text(
                    text = dateText,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    color = if (isDarkMode) Color.White else Color(0xFF0F172A),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun TactileDurationChip(
    label: String,
    selected: Boolean,
    accentColor: Color,
    secondaryAccent: Color,
    isDarkMode: Boolean,
    animationsEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.93f else 1f,
        animationSpec = spring(dampingRatio = 0.68f, stiffness = Spring.StiffnessMedium),
        label = "durationChipScale"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(50.dp),
        color = Color.Transparent,
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .graphicsLayer {
                if (animationsEnabled) {
                    scaleX = scale
                    scaleY = scale
                }
            }
            .liquidGlassPanel(
                shape = RoundedCornerShape(50.dp),
                cornerRadius = 50.dp,
                isDark = isDarkMode,
                accent = accentColor,
                secondaryAccent = secondaryAccent,
                isSelected = selected,
                softTintWhenSelected = false
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp)
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Bold,
                color = if (selected) Color.White else (if (isDarkMode) Color(0xFFE2E8F0) else Color(0xFF1E293B)),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
