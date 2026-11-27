package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import com.example.ui.theme.CoralRedAccent
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.LocalThemePreset
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.RoyalBlueAccent
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SyncDialogLiquidGlassWindow
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.liquidGlassDialogShell
import com.example.ui.theme.liquidGlassPanel
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.SyncDialogSecureWindow
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExportDialog(
    csvContent: String,
    jsonContent: String,
    onExportPdf: () -> Unit,
    onImportJson: (jsonString: String, replaceAll: Boolean) -> Pair<Boolean, String> = { _, _ -> false to "" },
    onImportJsonAsync: ((jsonString: String, replaceAll: Boolean, onComplete: (Boolean, String) -> Unit) -> Unit)? = null,
    onDismiss: () -> Unit,
    currentLang: AppLanguage = AppLanguage.ARABIC,
    isDarkMode: Boolean? = null,
    animationsEnabled: Boolean = true,
    isNoScreenshotEnabled: Boolean = false
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) }
    var importJsonText by remember { mutableStateOf("") }
    var replaceAllMode by remember { mutableStateOf(false) }

    val preset = LocalThemePreset.current
    val effectiveDark = preset?.isDark ?: isDarkMode ?: (MaterialTheme.colorScheme.surface.luminance() < 0.5f)
    val primaryColor = preset?.primary ?: if (effectiveDark) CyanGlow else VibrantCyan
    val secondaryColor = preset?.secondary ?: if (effectiveDark) PurpleAccent else RoyalBlueAccent
    val textPrimary = preset?.textPrimary ?: if (effectiveDark) Color.White else SlateTextPrimary
    val textSecondary = preset?.textSecondary ?: if (effectiveDark) Color(0xFFCBD5E1) else Color(0xFF475569)
    val glassDividerColor = if (effectiveDark) Color.White.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.62f)

    val jsonFileImportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    stream.bufferedReader(Charsets.UTF_8).readText()
                }
            }.onSuccess { loadedText ->
                if (!loadedText.isNullOrBlank()) {
                    importJsonText = loadedText
                    Toast.makeText(
                        context,
                        AppStrings.get("import_pasted_toast", currentLang),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.onFailure {
                Toast.makeText(
                    context,
                    AppStrings.get("import_file_error", currentLang),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    val entranceState = remember { MutableTransitionState(!animationsEnabled).apply { targetState = true } }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        SyncDialogSecureWindow(enabled = isNoScreenshotEnabled)
        SyncDialogLiquidGlassWindow()

        CompositionLocalProvider(
            LocalLayoutDirection provides if (currentLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
        ) {
            AnimatedVisibility(
                visibleState = entranceState,
                enter = if (animationsEnabled) {
                    fadeIn(tween(180)) + scaleIn(
                        initialScale = 0.94f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 420f)
                    )
                } else EnterTransition.None,
                exit = if (animationsEnabled) {
                    fadeOut(tween(120)) + scaleOut(targetScale = 0.96f)
                } else ExitTransition.None
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
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
                            .testTag("export_import_dialog")
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            LiquidGlassDialogHeader(
                                title = AppStrings.get("export_data", currentLang),
                                subtitle = AppStrings.get("full_backup_summary_badge", currentLang),
                                icon = Icons.Default.CloudSync,
                                primaryAccent = primaryColor,
                                secondaryAccent = secondaryColor,
                                isDark = effectiveDark,
                                closeContentDescription = AppStrings.get("close", currentLang),
                                onDismiss = onDismiss
                            )

                            HorizontalDivider(color = glassDividerColor)

                            // Liquid Glass Segmented Tab Switcher (Export vs Import)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                                    .liquidGlassPanel(
                                        shape = RoundedCornerShape(16.dp),
                                        cornerRadius = 16.dp,
                                        isDark = effectiveDark,
                                        accent = primaryColor,
                                        secondaryAccent = secondaryColor
                                    )
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                ExportTabButton(
                                    label = AppStrings.get("export_tab_label", currentLang),
                                    icon = Icons.Default.FileDownload,
                                    selected = selectedTab == 0,
                                    accent = primaryColor,
                                    secondary = secondaryColor,
                                    isDark = effectiveDark,
                                    textPrimary = textPrimary,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        selectedTab = 0
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                ExportTabButton(
                                    label = AppStrings.get("import_tab_label", currentLang),
                                    icon = Icons.Default.FileUpload,
                                    selected = selectedTab == 1,
                                    accent = secondaryColor,
                                    secondary = primaryColor,
                                    isDark = effectiveDark,
                                    textPrimary = textPrimary,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        selectedTab = 1
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f, fill = false)
                                    .verticalScroll(rememberScrollState())
                                    .padding(start = 16.dp, end = 16.dp, bottom = 18.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                if (selectedTab == 0) {
                                    // EXPORT TAB
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .liquidGlassPanel(
                                                shape = RoundedCornerShape(16.dp),
                                                cornerRadius = 16.dp,
                                                isDark = effectiveDark,
                                                accent = primaryColor,
                                                secondaryAccent = secondaryColor,
                                                isSelected = true,
                                                softTintWhenSelected = true
                                            )
                                            .padding(12.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.Top,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.VerifiedUser,
                                                contentDescription = null,
                                                tint = primaryColor,
                                                modifier = Modifier
                                                    .padding(top = 2.dp)
                                                    .size(20.dp)
                                            )
                                            Text(
                                                text = AppStrings.get("export_desc", currentLang),
                                                style = MaterialTheme.typography.bodySmall,
                                                fontSize = 12.sp,
                                                lineHeight = 18.sp,
                                                color = textSecondary
                                            )
                                        }
                                    }

                                    ExportOptionGlassRow(
                                        title = AppStrings.get("export_save_share_pdf", currentLang),
                                        badge = "PDF",
                                        icon = Icons.Default.PictureAsPdf,
                                        accent = CoralRedAccent,
                                        isDark = effectiveDark,
                                        textPrimary = textPrimary,
                                        isPrimaryFill = true,
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            onDismiss()
                                            onExportPdf()
                                        }
                                    )

                                    ExportOptionGlassRow(
                                        title = AppStrings.get("export_share_csv", currentLang),
                                        badge = "CSV",
                                        icon = Icons.Default.TableChart,
                                        accent = EmeraldSuccess,
                                        isDark = effectiveDark,
                                        textPrimary = textPrimary,
                                        isPrimaryFill = true,
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            scope.launch {
                                                shareBackupFile(
                                                    context = context,
                                                    content = csvContent,
                                                    fileExtension = "csv",
                                                    mimeType = "text/csv",
                                                    title = AppStrings.get("export_share_csv", currentLang)
                                                )
                                                onDismiss()
                                            }
                                        }
                                    )

                                    ExportOptionGlassRow(
                                        title = AppStrings.get("export_share_json", currentLang),
                                        badge = "JSON",
                                        icon = Icons.Default.Share,
                                        accent = primaryColor,
                                        isDark = effectiveDark,
                                        textPrimary = textPrimary,
                                        isPrimaryFill = false,
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            scope.launch {
                                                shareBackupFile(
                                                    context = context,
                                                    content = jsonContent,
                                                    fileExtension = "json",
                                                    mimeType = "application/json",
                                                    title = AppStrings.get("export_share_json", currentLang)
                                                )
                                                onDismiss()
                                            }
                                        }
                                    )

                                    ExportOptionGlassRow(
                                        title = AppStrings.get("export_copy_json", currentLang),
                                        badge = "CLIPBOARD",
                                        icon = Icons.Default.ContentCopy,
                                        accent = secondaryColor,
                                        isDark = effectiveDark,
                                        textPrimary = textPrimary,
                                        isPrimaryFill = false,
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            copyToClipboard(context, jsonContent, "RespitePulse Full JSON Backup")
                                            Toast.makeText(
                                                context,
                                                AppStrings.get("export_json_copied_toast", currentLang),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            onDismiss()
                                        }
                                    )
                                } else {
                                    // IMPORT TAB
                                    Text(
                                        text = AppStrings.get("import_desc", currentLang),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 12.sp,
                                        color = textSecondary
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        LiquidGlassActionButton(
                                            text = AppStrings.get("import_pick_file_btn", currentLang),
                                            icon = Icons.Default.FileOpen,
                                            isDark = effectiveDark,
                                            primaryAccent = primaryColor,
                                            secondaryAccent = secondaryColor,
                                            isPrimary = false,
                                            onClick = {
                                                jsonFileImportLauncher.launch(arrayOf("application/json", "text/*"))
                                            },
                                            modifier = Modifier.weight(1f)
                                        )

                                        LiquidGlassActionButton(
                                            text = AppStrings.get("import_paste_btn", currentLang),
                                            icon = Icons.Default.ContentPaste,
                                            isDark = effectiveDark,
                                            primaryAccent = secondaryColor,
                                            secondaryAccent = primaryColor,
                                            isPrimary = false,
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                                val clip = clipboard?.primaryClip
                                                if (clip != null && clip.itemCount > 0) {
                                                    val text = clip.getItemAt(0).text?.toString() ?: ""
                                                    importJsonText = text
                                                    Toast.makeText(
                                                        context,
                                                        AppStrings.get("import_pasted_toast", currentLang),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    OutlinedTextField(
                                        value = importJsonText,
                                        onValueChange = { importJsonText = it },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(140.dp),
                                        placeholder = {
                                            Text(
                                                "{\"personnel\": [...], \"leaveRecords\": [...], \"settings\": {...}}",
                                                color = textSecondary.copy(alpha = 0.7f),
                                                fontSize = 11.sp
                                            )
                                        },
                                        textStyle = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            color = textPrimary
                                        ),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = textPrimary,
                                            unfocusedTextColor = textPrimary,
                                            focusedContainerColor = if (effectiveDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.56f),
                                            unfocusedContainerColor = if (effectiveDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.36f),
                                            focusedBorderColor = primaryColor,
                                            unfocusedBorderColor = if (effectiveDark) Color.White.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.78f),
                                            cursorColor = primaryColor
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    )

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .liquidGlassPanel(
                                                shape = RoundedCornerShape(16.dp),
                                                cornerRadius = 16.dp,
                                                isDark = effectiveDark,
                                                accent = primaryColor,
                                                secondaryAccent = secondaryColor
                                            )
                                            .padding(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Column {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .defaultMinSize(minHeight = 48.dp)
                                                    .clickable { replaceAllMode = false }
                                            ) {
                                                RadioButton(
                                                    selected = !replaceAllMode,
                                                    onClick = { replaceAllMode = false },
                                                    colors = RadioButtonDefaults.colors(selectedColor = primaryColor)
                                                )
                                                Text(
                                                    text = AppStrings.get("import_mode_append", currentLang),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = if (!replaceAllMode) FontWeight.Bold else FontWeight.Medium,
                                                    color = textPrimary
                                                )
                                            }
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .defaultMinSize(minHeight = 48.dp)
                                                    .clickable { replaceAllMode = true }
                                            ) {
                                                RadioButton(
                                                    selected = replaceAllMode,
                                                    onClick = { replaceAllMode = true },
                                                    colors = RadioButtonDefaults.colors(selectedColor = CoralRedAccent)
                                                )
                                                Text(
                                                    text = AppStrings.get("import_mode_replace", currentLang),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = if (replaceAllMode) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (replaceAllMode) CoralRedAccent else textPrimary
                                                )
                                            }
                                        }
                                    }

                                    LiquidGlassActionButton(
                                        text = AppStrings.get("import_start_btn", currentLang),
                                        icon = Icons.Default.FileUpload,
                                        isDark = effectiveDark,
                                        primaryAccent = primaryColor,
                                        secondaryAccent = secondaryColor,
                                        isPrimary = true,
                                        onClick = {
                                            if (importJsonText.isBlank()) {
                                                Toast.makeText(
                                                    context,
                                                    AppStrings.get("import_paste_first_toast", currentLang),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                return@LiquidGlassActionButton
                                            }
                                            if (onImportJsonAsync != null) {
                                                onImportJsonAsync(importJsonText, replaceAllMode) { success, message ->
                                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                                    if (success) {
                                                        onDismiss()
                                                    }
                                                }
                                            } else {
                                                val (success, message) = onImportJson(importJsonText, replaceAllMode)
                                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                                if (success) {
                                                    onDismiss()
                                                }
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth()
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
private fun ExportTabButton(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    accent: Color,
    secondary: Color,
    isDark: Boolean,
    textPrimary: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent,
        modifier = modifier
            .height(48.dp)
            .liquidGlassPanel(
                shape = RoundedCornerShape(12.dp),
                cornerRadius = 12.dp,
                isDark = isDark,
                accent = accent,
                secondaryAccent = secondary,
                isSelected = selected,
                softTintWhenSelected = false
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) Color.White else accent,
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 12.5.sp,
                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                color = if (selected) Color.White else textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ExportOptionGlassRow(
    title: String,
    badge: String,
    icon: ImageVector,
    accent: Color,
    isDark: Boolean,
    textPrimary: Color,
    isPrimaryFill: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 52.dp)
            .liquidGlassPanel(
                shape = RoundedCornerShape(16.dp),
                cornerRadius = 16.dp,
                isDark = isDark,
                accent = accent,
                secondaryAccent = accent,
                isSelected = isPrimaryFill,
                softTintWhenSelected = !isPrimaryFill
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
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
                        .size(34.dp)
                        .liquidGlassPanel(
                            shape = RoundedCornerShape(10.dp),
                            cornerRadius = 10.dp,
                            isDark = isDark,
                            accent = if (isPrimaryFill) Color.White else accent,
                            secondaryAccent = accent,
                            isSelected = !isPrimaryFill,
                            softTintWhenSelected = true
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (isPrimaryFill) Color.White else accent,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = if (isPrimaryFill) FontWeight.ExtraBold else FontWeight.Bold,
                    color = if (isPrimaryFill) Color.White else textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(
                modifier = Modifier.liquidGlassPanel(
                    shape = RoundedCornerShape(50.dp),
                    cornerRadius = 50.dp,
                    isDark = isDark,
                    accent = if (isPrimaryFill) Color.White else accent,
                    secondaryAccent = accent
                )
            ) {
                Text(
                    text = badge,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = if (isPrimaryFill) Color.White else accent,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

private suspend fun shareBackupFile(
    context: Context,
    content: String,
    fileExtension: String,
    mimeType: String,
    title: String
) {
    val stamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())
    val fileName = "RespitePulse_FullBackup_$stamp.$fileExtension"
    val fileResult = try {
        withContext(Dispatchers.IO) {
            val outFile = File(context.cacheDir, fileName)
            outFile.writeText(content, Charsets.UTF_8)
            Result.success(FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", outFile))
        }
    } catch (ce: CancellationException) {
        throw ce
    } catch (e: Exception) {
        Result.failure(e)
    }

    val sendIntent = fileResult.fold(
        onSuccess = { uri ->
            Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TITLE, title)
                if (content.length <= 16_000) {
                    putExtra(Intent.EXTRA_TEXT, content)
                }
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        },
        onFailure = {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, content)
                putExtra(Intent.EXTRA_TITLE, title)
            }
        }
    )
    runCatching {
        val chooser = Intent.createChooser(sendIntent, title)
        if (context !is android.app.Activity) {
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }.onFailure { err ->
        Toast.makeText(context, err.localizedMessage ?: title, Toast.LENGTH_SHORT).show()
    }
}

private fun copyToClipboard(context: Context, text: String, label: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
}
