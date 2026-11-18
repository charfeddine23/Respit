package com.example.ui.components

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.IntSize
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
import com.example.data.formatWilaya
import com.example.data.normalizeRank
import com.example.data.normalizeWilaya
import com.example.ui.theme.EmeraldSuccess
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.SyncDialogSecureWindow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

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
    isNeumorphic: Boolean = false,
    animationsEnabled: Boolean = true,
    isNoScreenshotEnabled: Boolean = false
) {
    val context = LocalContext.current
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

    var fullName by remember { mutableStateOf(personnelToEdit?.fullName ?: "") }
    var fullNameLatin by remember { mutableStateOf(personnelToEdit?.fullNameLatin ?: "") }
    var rank by remember { mutableStateOf(personnelToEdit?.rank?.let { normalizeRank(it) } ?: algerianRanks.first()) }
    var wilaya by remember { mutableStateOf(personnelToEdit?.wilaya?.let { normalizeWilaya(it) } ?: "16 - الجزائر - Alger") }
    var regNumber by remember { mutableStateOf(personnelToEdit?.registrationNumber ?: "") }

    var startDateMillis by remember {
        mutableLongStateOf(personnelToEdit?.startDateMillis ?: Personnel.toStartOfDay(now))
    }
    var returnDateMillis by remember {
        mutableLongStateOf(
            personnelToEdit?.returnDateMillis ?: getOffsetMillis(startDateMillis, 9)
        )
    }
    var isReturnedToDuty by remember {
        mutableStateOf(personnelToEdit?.isReturnedToDuty ?: false)
    }

    var leaveType by remember { mutableStateOf(personnelToEdit?.leaveType ?: algerianLeaveTypes.first()) }
    var notes by remember { mutableStateOf(personnelToEdit?.notes ?: "") }

    var rankDropdownExpanded by remember { mutableStateOf(false) }
    var wilayaDropdownExpanded by remember { mutableStateOf(false) }
    var wilayaSearchQuery by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }

    // Date pickers
    val startCal = Calendar.getInstance().apply { timeInMillis = startDateMillis }
    val startDatePickerDialog = remember(startDateMillis) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val c = Calendar.getInstance()
                c.set(year, month, dayOfMonth, 0, 0, 0)
                c.set(Calendar.MILLISECOND, 0)
                startDateMillis = c.timeInMillis
                if (returnDateMillis < startDateMillis) {
                    returnDateMillis = getOffsetMillis(startDateMillis, 9)
                }
            },
            startCal.get(Calendar.YEAR),
            startCal.get(Calendar.MONTH),
            startCal.get(Calendar.DAY_OF_MONTH)
        )
    }

    val returnCal = Calendar.getInstance().apply { timeInMillis = returnDateMillis }
    val returnDatePickerDialog = remember(returnDateMillis) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val c = Calendar.getInstance()
                c.set(year, month, dayOfMonth, 0, 0, 0)
                c.set(Calendar.MILLISECOND, 0)
                returnDateMillis = if (c.timeInMillis < startDateMillis) startDateMillis else c.timeInMillis
            },
            returnCal.get(Calendar.YEAR),
            returnCal.get(Calendar.MONTH),
            returnCal.get(Calendar.DAY_OF_MONTH)
        )
    }

    val totalDays = remember(startDateMillis, returnDateMillis) {
        Personnel.inclusiveLeaveDays(startDateMillis, returnDateMillis)
    }

    // Theme-synchronized color palette
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val dialogContainerBg = when {
        isNeumorphic && isDarkMode -> Color(0xFF161F30)
        isNeumorphic -> Color(0xFFE6EEF8)
        isDarkMode -> Color(0xF50F172A)
        else -> Color(0xFAFFFFFF)
    }
    val sectionCardBg = when {
        isNeumorphic && isDarkMode -> Color(0xFF1C273B)
        isNeumorphic -> Color(0xFFF0F5FC)
        isDarkMode -> Color(0xFF182338)
        else -> Color(0xFFF8FAFC)
    }
    val sectionBorderColor = when {
        isDarkMode -> primaryColor.copy(alpha = 0.22f)
        else -> primaryColor.copy(alpha = 0.16f)
    }
    val textPrimary = if (isDarkMode) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF64748B)
    val readableAccent = if (isDarkMode) primaryColor else MaterialTheme.colorScheme.primary

    val inputColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = textPrimary,
        unfocusedTextColor = textPrimary,
        focusedContainerColor = if (isDarkMode) Color(0x400F172A) else Color.White,
        unfocusedContainerColor = if (isDarkMode) Color(0x280F172A) else Color.White.copy(alpha = 0.82f),
        focusedBorderColor = primaryColor,
        unfocusedBorderColor = if (isDarkMode) Color(0x45475569) else Color(0x55CBD5E1),
        focusedLabelColor = primaryColor,
        unfocusedLabelColor = textSecondary,
        focusedPlaceholderColor = textSecondary.copy(alpha = 0.65f),
        unfocusedPlaceholderColor = textSecondary.copy(alpha = 0.65f),
        cursorColor = primaryColor
    )

    val entranceState = remember { MutableTransitionState(false).apply { targetState = true } }
    val dialogLayoutDir = if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        SyncDialogSecureWindow(enabled = isNoScreenshotEnabled)

        CompositionLocalProvider(LocalLayoutDirection provides dialogLayoutDir) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = if (isDarkMode) 0.60f else 0.38f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss
                    )
                    .imePadding()
                    .padding(horizontal = 14.dp, vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                val maxDialogHeight = (maxHeight * 0.90f).coerceAtLeast(360.dp)

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
                    } else fadeIn(tween(0)),
                    exit = fadeOut(tween(140)) + scaleOut(targetScale = 0.95f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        color = dialogContainerBg,
                        border = BorderStroke(
                            1.3.dp,
                            Brush.linearGradient(
                                listOf(
                                    primaryColor.copy(alpha = if (isDarkMode) 0.50f else 0.38f),
                                    secondaryColor.copy(alpha = if (isDarkMode) 0.35f else 0.25f),
                                    Color.White.copy(alpha = if (isDarkMode) 0.14f else 0.90f)
                                )
                            )
                        ),
                        shadowElevation = 14.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 560.dp)
                            .heightIn(max = maxDialogHeight)
                            .pointerInput(Unit) { detectTapGestures {} }
                            .testTag("add_edit_personnel_dialog")
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // 1) STICKY LUXURY HEADER
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                primaryColor.copy(alpha = if (isDarkMode) 0.16f else 0.08f),
                                                secondaryColor.copy(alpha = if (isDarkMode) 0.10f else 0.05f),
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
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(
                                                    Brush.linearGradient(listOf(primaryColor, secondaryColor))
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

                                    IconButton(
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            onDismiss()
                                        },
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isDarkMode) Color(0x33475569) else Color(0x2294A3B8)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = AppStrings.get("cancel", currentLang),
                                                tint = textSecondary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(
                                color = primaryColor.copy(alpha = if (isDarkMode) 0.22f else 0.14f),
                                thickness = 1.dp
                            )

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
                                    enter = expandVertically(
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioNoBouncy,
                                            stiffness = 380f,
                                            visibilityThreshold = IntSize.VisibilityThreshold
                                        )
                                    ) + fadeIn(tween(200)),
                                    exit = shrinkVertically(
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioNoBouncy,
                                            stiffness = 440f,
                                            visibilityThreshold = IntSize.VisibilityThreshold
                                        )
                                    ) + fadeOut(tween(140))
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

                                // SECTION CARD 1: Identity & Military Profile
                                DialogSectionCard(
                                    title = AppStrings.get("dialog_sec_identity", currentLang),
                                    icon = Icons.Default.Person,
                                    accentColor = primaryColor,
                                    containerColor = sectionCardBg,
                                    borderColor = sectionBorderColor,
                                    isDarkMode = isDarkMode
                                ) {
                                    // Bilingual Full Names: primary active-language field first, secondary language right below
                                    val arabicNameField: @Composable (Boolean) -> Unit = { isFirst ->
                                        OutlinedTextField(
                                            value = fullName,
                                            onValueChange = {
                                                fullName = it
                                                errorMessage = null
                                            },
                                            label = { Text(AppStrings.get("full_name_ar_label", currentLang)) },
                                            placeholder = { Text(AppStrings.get("full_name_ar_placeholder", currentLang)) },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Person,
                                                    contentDescription = null,
                                                    tint = readableAccent,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            },
                                            trailingIcon = {
                                                if (fullName.isNotEmpty()) {
                                                    IconButton(
                                                        onClick = { fullName = "" },
                                                        modifier = Modifier.size(40.dp)
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

                                    val latinNameField: @Composable (Boolean) -> Unit = { isFirst ->
                                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                            OutlinedTextField(
                                                value = fullNameLatin,
                                                onValueChange = {
                                                    fullNameLatin = it
                                                    errorMessage = null
                                                },
                                                label = { Text(AppStrings.get("full_name_latin_label", currentLang)) },
                                                placeholder = { Text(AppStrings.get("full_name_latin_placeholder", currentLang)) },
                                                leadingIcon = {
                                                    Icon(
                                                        imageVector = Icons.Default.Translate,
                                                        contentDescription = null,
                                                        tint = readableAccent,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                },
                                                trailingIcon = {
                                                    if (fullNameLatin.isNotEmpty()) {
                                                        IconButton(
                                                            onClick = { fullNameLatin = "" },
                                                            modifier = Modifier.size(40.dp)
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

                                    // Military Rank Selector
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
                                            label = { Text(AppStrings.get("rank_label", currentLang)) },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Shield,
                                                    contentDescription = null,
                                                    tint = readableAccent,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            },
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = rankDropdownExpanded)
                                            },
                                            colors = inputColors,
                                            modifier = Modifier
                                                .menuAnchor()
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

                                    // Registration Number
                                    OutlinedTextField(
                                        value = regNumber,
                                        onValueChange = {
                                            regNumber = it
                                            errorMessage = null
                                        },
                                        label = { Text(AppStrings.get("reg_number_label", currentLang)) },
                                        placeholder = { Text("DZ-123456") },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Badge,
                                                contentDescription = null,
                                                tint = readableAccent,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        },
                                        trailingIcon = {
                                            if (regNumber.isNotEmpty()) {
                                                IconButton(
                                                    onClick = { regNumber = "" },
                                                    modifier = Modifier.size(40.dp)
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

                                    // Searchable Wilaya Selector
                                    ExposedDropdownMenuBox(
                                        expanded = wilayaDropdownExpanded,
                                        onExpandedChange = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            wilayaDropdownExpanded = !wilayaDropdownExpanded
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        OutlinedTextField(
                                            value = formatWilaya(wilaya, currentLang, includeCode = true),
                                            onValueChange = {},
                                            readOnly = true,
                                            label = { Text(AppStrings.get("wilaya_label", currentLang).replace(":", "")) },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.LocationOn,
                                                    contentDescription = null,
                                                    tint = readableAccent,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            },
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = wilayaDropdownExpanded)
                                            },
                                            colors = inputColors,
                                            modifier = Modifier
                                                .menuAnchor()
                                                .fillMaxWidth(),
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                        ExposedDropdownMenu(
                                            expanded = wilayaDropdownExpanded,
                                            onDismissRequest = {
                                                wilayaDropdownExpanded = false
                                                wilayaSearchQuery = ""
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .heightIn(max = 340.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                            ) {
                                                OutlinedTextField(
                                                    value = wilayaSearchQuery,
                                                    onValueChange = { wilayaSearchQuery = it },
                                                    placeholder = {
                                                        Text(
                                                            AppStrings.get("wilaya_search_placeholder", currentLang),
                                                            fontSize = 12.sp
                                                        )
                                                    },
                                                    singleLine = true,
                                                    leadingIcon = {
                                                        Icon(
                                                            Icons.Default.Search,
                                                            contentDescription = null,
                                                            modifier = Modifier.size(18.dp),
                                                            tint = primaryColor
                                                        )
                                                    },
                                                    trailingIcon = {
                                                        if (wilayaSearchQuery.isNotEmpty()) {
                                                            IconButton(
                                                                onClick = { wilayaSearchQuery = "" },
                                                                modifier = Modifier.size(24.dp)
                                                            ) {
                                                                Icon(
                                                                    Icons.Default.Clear,
                                                                    contentDescription = AppStrings.get("clear_icon_desc", currentLang),
                                                                    modifier = Modifier.size(16.dp)
                                                                )
                                                            }
                                                        }
                                                    },
                                                    colors = inputColors,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .testTag("wilaya_dropdown_search_input"),
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                            }

                                            val filteredWilayas = remember(wilayaSearchQuery, currentLang) {
                                                if (wilayaSearchQuery.isBlank()) {
                                                    algerianWilayas
                                                } else {
                                                    val q = wilayaSearchQuery.trim()
                                                    algerianWilayas.filter {
                                                        it.contains(q, ignoreCase = true) ||
                                                            formatWilaya(it, currentLang, includeCode = true).contains(q, ignoreCase = true)
                                                    }
                                                }
                                            }

                                            if (filteredWilayas.isEmpty()) {
                                                DropdownMenuItem(
                                                    text = {
                                                        Text(
                                                            AppStrings.get("wilaya_not_found", currentLang),
                                                            color = textSecondary,
                                                            fontSize = 13.sp
                                                        )
                                                    },
                                                    onClick = {},
                                                    enabled = false
                                                )
                                            } else {
                                                filteredWilayas.forEach { w ->
                                                    val selected = w == wilaya
                                                    DropdownMenuItem(
                                                        text = {
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                Text(
                                                                    formatWilaya(w, currentLang, includeCode = true),
                                                                    fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Normal,
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
                                                            wilaya = w
                                                            wilayaDropdownExpanded = false
                                                            wilayaSearchQuery = ""
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // SECTION CARD 2: Leave Type & Interactive Dates
                                DialogSectionCard(
                                    title = AppStrings.get("dialog_sec_schedule", currentLang),
                                    icon = Icons.Default.EventAvailable,
                                    accentColor = secondaryColor,
                                    containerColor = sectionCardBg,
                                    borderColor = sectionBorderColor,
                                    isDarkMode = isDarkMode
                                ) {
                                    // Leave Type 2x2 Interactive Animated Pills (Instant 1-tap selection!)
                                    Text(
                                        text = AppStrings.get("leave_type_label", currentLang),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = textSecondary
                                    )

                                    val leaveRows = remember(algerianLeaveTypes) { algerianLeaveTypes.chunked(2) }
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        leaveRows.forEach { rowItems ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                rowItems.forEach { lt ->
                                                    val isSelected = lt == leaveType
                                                    TactileSelectorChip(
                                                        label = formatLeaveType(lt, currentLang),
                                                        selected = isSelected,
                                                        accentColor = primaryColor,
                                                        isDarkMode = isDarkMode,
                                                        animationsEnabled = animationsEnabled,
                                                        onClick = {
                                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                            leaveType = lt
                                                        },
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Side-by-side Interactive Start Date & Return Date Cards
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        InteractiveDateTile(
                                            label = AppStrings.get("start_date_label", currentLang),
                                            dateText = sdf.format(Date(startDateMillis)),
                                            accentColor = primaryColor,
                                            isDarkMode = isDarkMode,
                                            animationsEnabled = animationsEnabled,
                                            onClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                startDatePickerDialog.show()
                                            },
                                            modifier = Modifier.weight(1f)
                                        )

                                        InteractiveDateTile(
                                            label = AppStrings.get("return_date_label", currentLang),
                                            dateText = sdf.format(Date(returnDateMillis)),
                                            accentColor = secondaryColor,
                                            isDarkMode = isDarkMode,
                                            animationsEnabled = animationsEnabled,
                                            onClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                returnDatePickerDialog.show()
                                            },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    // Quick Duration Selector Chips (48h, 72h, 4d, 8d, 10d)
                                    Text(
                                        text = AppStrings.get("quick_duration_add", currentLang),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = textSecondary
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
                                                    val isSelected = totalDays == days.toLong()
                                                    TactileDurationChip(
                                                        label = label,
                                                        selected = isSelected,
                                                        accentColor = primaryColor,
                                                        isDarkMode = isDarkMode,
                                                        animationsEnabled = animationsEnabled,
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .testTag("quick_duration_chip_$days"),
                                                        onClick = {
                                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                            returnDateMillis = getOffsetMillis(startDateMillis, (days - 1).coerceAtLeast(0))
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Crisp High-Contrast Animated Leave Duration Summary Pill
                                    val durationPillBg = primaryColor.copy(alpha = if (isDarkMode) 0.16f else 0.10f)
                                    val durationPillTextColor = if (isDarkMode) Color(0xFFE0F7FA) else primaryColor
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = durationPillBg,
                                        border = BorderStroke(
                                            1.dp,
                                            primaryColor.copy(alpha = if (isDarkMode) 0.45f else 0.30f)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Schedule,
                                                    contentDescription = null,
                                                    tint = durationPillTextColor,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = AppStrings.get("leave_duration", currentLang),
                                                    style = MaterialTheme.typography.labelLarge,
                                                    fontWeight = FontWeight.Bold,
                                                    color = durationPillTextColor
                                                )
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(50.dp),
                                                color = primaryColor,
                                                shadowElevation = 2.dp
                                            ) {
                                                AnimatedContent(
                                                    targetState = totalDays,
                                                    transitionSpec = {
                                                        fadeIn(tween(180)) togetherWith fadeOut(tween(120))
                                                    },
                                                    label = "totalDaysCounter"
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
                                }

                                // SECTION CARD 3: Current Duty Status & Notes
                                DialogSectionCard(
                                    title = AppStrings.get("dialog_sec_status_notes", currentLang),
                                    icon = Icons.Default.CheckCircle,
                                    accentColor = EmeraldSuccess,
                                    containerColor = sectionCardBg,
                                    borderColor = sectionBorderColor,
                                    isDarkMode = isDarkMode
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
                                        color = if (isReturnedToDuty) {
                                            EmeraldSuccess.copy(alpha = if (isDarkMode) 0.16f else 0.10f)
                                        } else {
                                            if (isDarkMode) Color(0x380F172A) else Color.White
                                        },
                                        border = BorderStroke(
                                            1.dp,
                                            if (isReturnedToDuty) EmeraldSuccess.copy(alpha = 0.50f)
                                            else (if (isDarkMode) Color(0x33475569) else Color(0x33CBD5E1))
                                        ),
                                        modifier = Modifier.fillMaxWidth()
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
                                                    uncheckedTrackColor = if (isDarkMode) Color(0xFF475569) else Color(0xFFCBD5E1)
                                                )
                                            )
                                        }
                                    }

                                    OutlinedTextField(
                                        value = notes,
                                        onValueChange = { notes = it },
                                        label = { Text(AppStrings.get("notes_label", currentLang).replace(":", "")) },
                                        placeholder = { Text(AppStrings.get("notes_placeholder", currentLang)) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.EditNote,
                                                contentDescription = null,
                                                tint = readableAccent,
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

                            HorizontalDivider(
                                color = primaryColor.copy(alpha = if (isDarkMode) 0.22f else 0.14f),
                                thickness = 1.dp
                            )

                            // 3) STICKY ACTION FOOTER (Always reachable 48dp buttons)
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
                                Button(
                                    onClick = {
                                        if ((fullName.isBlank() && fullNameLatin.isBlank()) || regNumber.isBlank()) {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            errorMessage = AppStrings.get("fill_required", currentLang)
                                            return@Button
                                        }
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onSave(
                                            personnelToEdit?.id ?: 0L,
                                            fullName.trim(),
                                            fullNameLatin.trim(),
                                            rank,
                                            regNumber.trim(),
                                            startDateMillis,
                                            returnDateMillis,
                                            isReturnedToDuty,
                                            leaveType,
                                            wilaya,
                                            notes.trim()
                                        )
                                    },
                                    interactionSource = saveInteraction,
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = primaryColor,
                                        contentColor = Color.White
                                    ),
                                    contentPadding = PaddingValues(horizontal = 14.dp),
                                    modifier = Modifier
                                        .weight(1.4f)
                                        .defaultMinSize(minHeight = 48.dp)
                                        .height(48.dp)
                                        .testTag("save_personnel_button")
                                        .graphicsLayer {
                                            scaleX = saveScale
                                            scaleY = saveScale
                                        }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = AppStrings.get(if (isEdit) "save_changes" else "save", currentLang),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.5.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                OutlinedButton(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onDismiss()
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isDarkMode) Color(0x4594A3B8) else Color(0x4564748B)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 14.dp),
                                    modifier = Modifier
                                        .weight(0.8f)
                                        .defaultMinSize(minHeight = 48.dp)
                                        .height(48.dp)
                                ) {
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

@Composable
private fun DialogSectionCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    containerColor: Color,
    borderColor: Color,
    isDarkMode: Boolean,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(accentColor.copy(alpha = if (isDarkMode) 0.22f else 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor
                )
            }
            content()
        }
    }
}

@Composable
private fun TactileSelectorChip(
    label: String,
    selected: Boolean,
    accentColor: Color,
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
    val bgColor by animateColorAsState(
        targetValue = when {
            selected && isDarkMode -> accentColor.copy(alpha = 0.24f)
            selected -> accentColor.copy(alpha = 0.14f)
            isDarkMode -> Color(0x380F172A)
            else -> Color.White
        },
        animationSpec = if (animationsEnabled) tween(170) else tween(0),
        label = "selectorChipBg"
    )
    val labelColor = when {
        selected && isDarkMode -> Color.White
        selected -> accentColor
        isDarkMode -> Color(0xFFE2E8F0)
        else -> Color(0xFF1E293B)
    }

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) accentColor else (if (isDarkMode) Color(0x33475569) else Color(0x33CBD5E1))
        ),
        modifier = modifier
            .defaultMinSize(minHeight = 44.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
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
        color = if (isDarkMode) Color(0x400F172A) else Color.White,
        border = BorderStroke(1.dp, accentColor.copy(alpha = if (isDarkMode) 0.40f else 0.28f)),
        modifier = modifier
            .defaultMinSize(minHeight = 64.dp)
            .graphicsLayer {
                if (animationsEnabled) {
                    scaleX = scale
                    scaleY = scale
                }
            }
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
    val bgColor by animateColorAsState(
        targetValue = when {
            selected -> accentColor
            isDarkMode -> Color(0x400F172A)
            else -> Color.White
        },
        animationSpec = if (animationsEnabled) tween(160) else tween(0),
        label = "durationChipBg"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(50.dp),
        color = bgColor,
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) accentColor else (if (isDarkMode) Color(0x38475569) else Color(0x38CBD5E1))
        ),
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
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
