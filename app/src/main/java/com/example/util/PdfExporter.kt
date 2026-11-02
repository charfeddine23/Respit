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
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExporter {

    fun generateAndSharePdf(context: Context, personnelList: List<Personnel>) {
        val pdfDocument = PdfDocument()

        // A4 page: 595 x 842 points
        val pageWidth = 595
        val pageHeight = 842
        var pageNumber = 1

        val titlePaint = Paint().apply {
            color = Color.rgb(15, 23, 42) // Dark Navy
            textSize = 17f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        val subTitlePaint = Paint().apply {
            color = Color.rgb(2, 132, 199) // Sky Blue
            textSize = 10.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        val metaPaint = Paint().apply {
            color = Color.rgb(71, 85, 105)
            textSize = 8.5f
            textAlign = Paint.Align.RIGHT
        }

        val headerBgPaint = Paint().apply {
            color = Color.rgb(241, 245, 249)
            style = Paint.Style.FILL
        }

        val headerTextPaint = Paint().apply {
            color = Color.rgb(15, 23, 42)
            textSize = 8.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.RIGHT
        }

        val rowTextPaint = Paint().apply {
            color = Color.rgb(30, 41, 59)
            textSize = 8f
            textAlign = Paint.Align.RIGHT
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
            textAlign = Paint.Align.RIGHT
        }

        val atHomePaint = Paint().apply {
            color = Color.rgb(2, 132, 199)
            textSize = 8f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.RIGHT
        }

        val todayPaint = Paint().apply {
            color = Color.rgb(217, 119, 6)
            textSize = 8f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.RIGHT
        }

        val dutyPaint = Paint().apply {
            color = Color.rgb(5, 150, 105)
            textSize = 8f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.RIGHT
        }

        val itemsPerPage = 22
        val chunks = personnelList.chunked(itemsPerPage)
        val totalPages = if (chunks.isEmpty()) 1 else chunks.size
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val printDate = sdf.format(Date())

        for ((index, pageItems) in (if (chunks.isEmpty()) listOf(emptyList()) else chunks).withIndex()) {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            // Header Banner
            canvas.drawText("Respite Pulse - نظام تتبع إجازات وعطل الأفراد", pageWidth / 2f, 38f, titlePaint)
            canvas.drawText("جدول متابعة تواريخ الاستفادة والدخول وحساب التواجد بالبيت - الجمهورية الجزائرية", pageWidth / 2f, 54f, subTitlePaint)

            canvas.drawText("تاريخ الاستخراج: $printDate", pageWidth - 25f, 75f, metaPaint)
            canvas.drawText("إجمالي السجلات: ${personnelList.size} فرد | صفحة ${index + 1} من $totalPages", 25f + 160f, 75f, metaPaint)

            // Table positioning (RTL Columns)
            val startY = 92f
            val rowHeight = 28f
            val tableRight = (pageWidth - 25).toFloat()
            val tableLeft = 25f

            // Columns Right-to-Left offsets:
            // 1. الرتبة: right - 5
            // 2. الاسم واللقب: right - 55
            // 3. الولاية: right - 160
            // 4. رقم التسجيل: right - 235
            // 5. الإستفادة: right - 305
            // 6. الدخول: right - 368
            // 7. الوضعية ومتبقي الدخول: right - 435

            // Draw Header Background
            canvas.drawRect(tableLeft, startY, tableRight, startY + rowHeight, headerBgPaint)
            canvas.drawRect(tableLeft, startY, tableRight, startY + rowHeight, borderPaint)

            val headerY = startY + 18f
            canvas.drawText("الرتبة", tableRight - 5f, headerY, headerTextPaint)
            canvas.drawText("الاسم واللقب", tableRight - 55f, headerY, headerTextPaint)
            canvas.drawText("الولاية", tableRight - 160f, headerY, headerTextPaint)
            canvas.drawText("رقم التسجيل", tableRight - 235f, headerY, headerTextPaint)
            canvas.drawText("الإستفادة", tableRight - 305f, headerY, headerTextPaint)
            canvas.drawText("الدخول", tableRight - 368f, headerY, headerTextPaint)
            canvas.drawText("الوضعية / متبقي الدخول", tableRight - 435f, headerY, headerTextPaint)

            var currentY = startY + rowHeight

            pageItems.forEachIndexed { i, p ->
                val bg = if (i % 2 == 1) Color.rgb(248, 250, 252) else Color.WHITE
                val rowBgPaint = Paint().apply { color = bg; style = Paint.Style.FILL }
                canvas.drawRect(tableLeft, currentY, tableRight, currentY + rowHeight, rowBgPaint)
                canvas.drawRect(tableLeft, currentY, tableRight, currentY + rowHeight, borderPaint)

                val textY = currentY + 18f
                canvas.drawText(p.rank, tableRight - 5f, textY, rowTextPaint)
                canvas.drawText(p.fullName, tableRight - 55f, textY, rowTextPaint)
                canvas.drawText(p.wilaya, tableRight - 160f, textY, rowTextPaint)
                canvas.drawText(p.registrationNumber, tableRight - 235f, textY, rowTextPaint)
                canvas.drawText(p.formattedStartDate, tableRight - 305f, textY, rowTextPaint)
                canvas.drawText(p.formattedReturnDate, tableRight - 368f, textY, rowTextPaint)

                val (statusText, statusPaint) = when (p.status) {
                    LeaveStatus.AT_HOME -> p.countdownTextAr to atHomePaint
                    LeaveStatus.RETURNING_TODAY -> "الدخول اليوم" to todayPaint
                    LeaveStatus.OVERDUE -> p.countdownTextAr to overduePaint
                    LeaveStatus.ON_DUTY -> "حاضر بالخدمة" to dutyPaint
                    LeaveStatus.UPCOMING -> p.countdownTextAr to atHomePaint
                }
                canvas.drawText(statusText, tableRight - 435f, textY, statusPaint)

                currentY += rowHeight
            }

            // Footer
            val footerPaint = Paint().apply {
                color = Color.rgb(148, 163, 184)
                textSize = 8f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("تم إنشاء هذا التقرير تلقائياً بواسطة تطبيق Respite Pulse لإدارة الإجازات ومتابعة تواريخ الدخول", pageWidth / 2f, pageHeight - 25f, footerPaint)

            pdfDocument.finishPage(page)
            pageNumber++
        }

        // Save PDF to cache
        val pdfFile = File(context.cacheDir, "تقرير_إجازات_الأفراد_Respite_Pelse.pdf")
        FileOutputStream(pdfFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        // Share via FileProvider Intent
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "تقرير إجازات وعطل الأفراد - Respite Pulse")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "حفظ أو مشاركة ملف PDF"))
    }
}
