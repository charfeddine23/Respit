package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.LeaveStatus
import com.example.data.Personnel
import com.example.data.formatRank
import com.example.data.formatWilaya
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExporter {

    fun generateAndSharePdf(
        context: Context,
        personnelList: List<Personnel>,
        lang: AppLanguage = AppLanguage.ARABIC
    ) {
        val pdfDocument = PdfDocument()

        // A4 page: 595 x 842 points
        val pageWidth = 595
        val pageHeight = 842
        var pageNumber = 1
        val isRtl = lang.isRtl
        val defaultTextAlign = if (isRtl) Paint.Align.RIGHT else Paint.Align.LEFT

        val titlePaint = Paint().apply {
            color = Color.rgb(15, 23, 42)
            textSize = 17f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            this.textAlign = Paint.Align.CENTER
        }

        val subTitlePaint = Paint().apply {
            color = Color.rgb(2, 132, 199)
            textSize = 10.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            this.textAlign = Paint.Align.CENTER
        }

        val metaPaint = Paint().apply {
            color = Color.rgb(71, 85, 105)
            textSize = 8.5f
            this.textAlign = defaultTextAlign
        }

        val headerBgPaint = Paint().apply {
            color = Color.rgb(241, 245, 249)
            style = Paint.Style.FILL
        }

        val headerTextPaint = Paint().apply {
            color = Color.rgb(15, 23, 42)
            textSize = 8.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            this.textAlign = defaultTextAlign
        }

        val rowTextPaint = Paint().apply {
            color = Color.rgb(30, 41, 59)
            textSize = 8f
            this.textAlign = defaultTextAlign
        }

        val borderPaint = Paint().apply {
            color = Color.rgb(203, 213, 225)
            style = Paint.Style.STROKE
            strokeWidth = 0.7f
        }

        val overduePaint = Paint().apply {
            color = Color.rgb(220, 38, 38)
            textSize = 8f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            this.textAlign = defaultTextAlign
        }

        val atHomePaint = Paint().apply {
            color = Color.rgb(2, 132, 199)
            textSize = 8f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            this.textAlign = defaultTextAlign
        }

        val todayPaint = Paint().apply {
            color = Color.rgb(217, 119, 6)
            textSize = 8f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            this.textAlign = defaultTextAlign
        }

        val dutyPaint = Paint().apply {
            color = Color.rgb(5, 150, 105)
            textSize = 8f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            this.textAlign = defaultTextAlign
        }

        val itemsPerPage = 22
        val chunks = personnelList.chunked(itemsPerPage)
        val totalPages = if (chunks.isEmpty()) 1 else chunks.size
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        val printDate = sdf.format(Date())

        val titleText = "Respite Pulse - ${AppStrings.get("pdf_export_title", lang)}"
        val subtitleText = AppStrings.get("pdf_export_subtitle", lang)
        val colRank = AppStrings.get("pdf_col_rank", lang)
        val colName = AppStrings.get("pdf_col_name", lang)
        val colWilaya = AppStrings.get("pdf_col_wilaya", lang)
        val colReg = AppStrings.get("pdf_col_reg", lang)
        val colStart = AppStrings.get("pdf_col_start", lang)
        val colReturn = AppStrings.get("pdf_col_return", lang)
        val colStatus = AppStrings.get("pdf_col_status", lang)

        for ((index, pageItems) in (if (chunks.isEmpty()) listOf(emptyList()) else chunks).withIndex()) {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            canvas.drawText(titleText, pageWidth / 2f, 38f, titlePaint)
            canvas.drawText(subtitleText, pageWidth / 2f, 54f, subTitlePaint)

            val metaLeft = "${AppStrings.get("pdf_extracted_at", lang)} $printDate"
            val metaRight = "${AppStrings.get("pdf_total_records", lang)} ${personnelList.size} | ${AppStrings.get("pdf_page", lang)} ${index + 1} ${AppStrings.get("pdf_of", lang)} $totalPages"

            if (isRtl) {
                canvas.drawText(metaLeft, pageWidth - 25f, 75f, metaPaint)
                canvas.drawText(metaRight, 25f + 160f, 75f, metaPaint)
            } else {
                canvas.drawText(metaLeft, 25f, 75f, metaPaint)
                canvas.drawText(metaRight, pageWidth - 190f, 75f, metaPaint)
            }

            val startY = 92f
            val rowHeight = 28f
            val tableRight = (pageWidth - 25).toFloat()
            val tableLeft = 25f

            val xRank = if (isRtl) tableRight - 5f else tableLeft + 5f
            val xName = if (isRtl) tableRight - 72f else tableLeft + 75f
            val xWilaya = if (isRtl) tableRight - 170f else tableLeft + 170f
            val xReg = if (isRtl) tableRight - 245f else tableLeft + 245f
            val xStart = if (isRtl) tableRight - 315f else tableLeft + 315f
            val xReturn = if (isRtl) tableRight - 378f else tableLeft + 378f
            val xStatus = if (isRtl) tableRight - 442f else tableLeft + 442f

            canvas.drawRect(tableLeft, startY, tableRight, startY + rowHeight, headerBgPaint)
            canvas.drawRect(tableLeft, startY, tableRight, startY + rowHeight, borderPaint)

            val headerY = startY + 18f
            canvas.drawText(colRank, xRank, headerY, headerTextPaint)
            canvas.drawText(colName, xName, headerY, headerTextPaint)
            canvas.drawText(colWilaya, xWilaya, headerY, headerTextPaint)
            canvas.drawText(colReg, xReg, headerY, headerTextPaint)
            canvas.drawText(colStart, xStart, headerY, headerTextPaint)
            canvas.drawText(colReturn, xReturn, headerY, headerTextPaint)
            canvas.drawText(colStatus, xStatus, headerY, headerTextPaint)

            var currentY = startY + rowHeight

            pageItems.forEachIndexed { i, p ->
                val bg = if (i % 2 == 1) Color.rgb(248, 250, 252) else Color.WHITE
                val rowBgPaint = Paint().apply { color = bg; style = Paint.Style.FILL }
                canvas.drawRect(tableLeft, currentY, tableRight, currentY + rowHeight, rowBgPaint)
                canvas.drawRect(tableLeft, currentY, tableRight, currentY + rowHeight, borderPaint)

                val textY = currentY + 18f
                canvas.drawText(formatRank(p.rank, lang), xRank, textY, rowTextPaint)
                canvas.drawText(p.fullName, xName, textY, rowTextPaint)
                canvas.drawText(formatWilaya(p.wilaya, lang, includeCode = false), xWilaya, textY, rowTextPaint)
                canvas.drawText(p.registrationNumber, xReg, textY, rowTextPaint)
                canvas.drawText(p.formattedStartDate, xStart, textY, rowTextPaint)
                canvas.drawText(p.formattedReturnDate, xReturn, textY, rowTextPaint)

                val (statusText, statusPaint) = when (p.status) {
                    LeaveStatus.AT_HOME -> p.getCountdownText(lang) to atHomePaint
                    LeaveStatus.RETURNING_TODAY -> p.status.getCompactBadgeLabel(lang) to todayPaint
                    LeaveStatus.OVERDUE -> p.getCountdownText(lang) to overduePaint
                    LeaveStatus.ON_DUTY -> p.status.getCompactBadgeLabel(lang) to dutyPaint
                    LeaveStatus.UPCOMING -> p.getCountdownText(lang) to atHomePaint
                }
                canvas.drawText(statusText, xStatus, textY, statusPaint)

                currentY += rowHeight
            }

            val footerPaint = Paint().apply {
                color = Color.rgb(148, 163, 184)
                textSize = 8f
                this.textAlign = Paint.Align.CENTER
            }
            canvas.drawText(AppStrings.get("pdf_footer", lang), pageWidth / 2f, pageHeight - 25f, footerPaint)

            pdfDocument.finishPage(page)
            pageNumber++
        }

        val pdfFile = File(context.cacheDir, "respite_pulse_report_${lang.code}.pdf")
        FileOutputStream(pdfFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, titleText)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, AppStrings.get("pdf_share_chooser", lang)))
    }
}
