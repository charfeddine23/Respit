package com.example.util

import android.content.Context
import android.net.Uri
import com.example.data.LeaveRecord
import com.example.data.LeaveStatus
import com.example.data.Personnel
import com.example.data.formatLeaveType
import com.example.data.formatRank
import com.example.data.formatWilaya
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Pure Kotlin OOXML (.xlsx) workbook generator formatted professionally
 * for A4 Landscape (orientation="landscape", paperSize="9", fitToWidth="1").
 */
object ExcelXlsxExporter {

    fun defaultFileName(): String {
        val stamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())
        return "RespitePulse_Database_$stamp.xlsx"
    }

    fun writeWorkbookToUri(
        context: Context,
        uri: Uri,
        personnelList: List<Personnel>,
        leaveRecords: List<LeaveRecord>,
        lang: AppLanguage
    ) {
        val bytes = generateWorkbookBytes(personnelList, leaveRecords, lang)
        val out = runCatching { context.contentResolver.openOutputStream(uri, "wt") }.getOrNull()
            ?: context.contentResolver.openOutputStream(uri)
            ?: error("Unable to open output stream for selected destination")
        out.use { stream ->
            stream.write(bytes)
            stream.flush()
        }
    }

    fun generateWorkbookBytes(
        personnelList: List<Personnel>,
        leaveRecords: List<LeaveRecord>,
        lang: AppLanguage
    ): ByteArray {
        val baos = ByteArrayOutputStream()
        writeWorkbookToStream(baos, personnelList, leaveRecords, lang)
        return baos.toByteArray()
    }

    fun writeWorkbookToStream(
        outputStream: OutputStream,
        personnelList: List<Personnel>,
        leaveRecords: List<LeaveRecord>,
        lang: AppLanguage
    ) {
        val sheet1Name = sanitizeSheetName(AppStrings.get("excel_sheet_personnel", lang))
        val sheet2Name = sanitizeSheetName(AppStrings.get("excel_sheet_leaves", lang))

        ZipOutputStream(outputStream).use { zip ->
            writeZipEntry(zip, "[Content_Types].xml", buildContentTypesXml())
            writeZipEntry(zip, "_rels/.rels", buildRootRelsXml())
            writeZipEntry(zip, "xl/workbook.xml", buildWorkbookXml(sheet1Name, sheet2Name))
            writeZipEntry(zip, "xl/_rels/workbook.xml.rels", buildWorkbookRelsXml())
            writeZipEntry(zip, "xl/styles.xml", buildStylesXml(isRtl = lang.isRtl))
            writeZipEntry(
                zip,
                "xl/worksheets/sheet1.xml",
                buildPersonnelSheetXml(personnelList, lang)
            )
            writeZipEntry(
                zip,
                "xl/worksheets/sheet2.xml",
                buildLeavesSheetXml(leaveRecords, lang)
            )
        }
    }

    private fun writeZipEntry(zip: ZipOutputStream, path: String, content: String) {
        zip.putNextEntry(ZipEntry(path))
        zip.write(content.toByteArray(Charsets.UTF_8))
        zip.closeEntry()
    }

    private fun buildContentTypesXml(): String = """
        <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
        <Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
            <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
            <Default Extension="xml" ContentType="application/xml"/>
            <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
            <Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
            <Override PartName="/xl/worksheets/sheet2.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
            <Override PartName="/xl/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml"/>
        </Types>
    """.trimIndent()

    private fun buildRootRelsXml(): String = """
        <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
        <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
            <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
        </Relationships>
    """.trimIndent()

    private fun buildWorkbookXml(sheet1Name: String, sheet2Name: String): String = """
        <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
        <workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main"
                  xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
            <bookViews>
                <workbookView xWindow="0" yWindow="0" windowWidth="24000" windowHeight="14000" activeTab="0"/>
            </bookViews>
            <sheets>
                <sheet name="${escapeXml(sheet1Name)}" sheetId="1" r:id="rId1"/>
                <sheet name="${escapeXml(sheet2Name)}" sheetId="2" r:id="rId2"/>
            </sheets>
        </workbook>
    """.trimIndent()

    private fun buildWorkbookRelsXml(): String = """
        <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
        <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
            <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
            <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet2.xml"/>
            <Relationship Id="rId3" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
        </Relationships>
    """.trimIndent()

    private fun buildStylesXml(isRtl: Boolean): String {
        val textAlign = if (isRtl) "right" else "left"
        return """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <styleSheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
                <fonts count="9">
                    <font>
                        <sz val="10"/>
                        <color rgb="FF0F172A"/>
                        <name val="Arial"/>
                    </font>
                    <font>
                        <b/>
                        <sz val="15"/>
                        <color rgb="FFFFFFFF"/>
                        <name val="Arial"/>
                    </font>
                    <font>
                        <b/>
                        <sz val="10"/>
                        <color rgb="FF0F766E"/>
                        <name val="Arial"/>
                    </font>
                    <font>
                        <b/>
                        <sz val="10.5"/>
                        <color rgb="FFFFFFFF"/>
                        <name val="Arial"/>
                    </font>
                    <font>
                        <b/>
                        <sz val="10"/>
                        <color rgb="FF0F172A"/>
                        <name val="Arial"/>
                    </font>
                    <font>
                        <b/>
                        <sz val="10"/>
                        <color rgb="FF065F46"/>
                        <name val="Arial"/>
                    </font>
                    <font>
                        <b/>
                        <sz val="10"/>
                        <color rgb="FF0369A1"/>
                        <name val="Arial"/>
                    </font>
                    <font>
                        <b/>
                        <sz val="10"/>
                        <color rgb="FFB45309"/>
                        <name val="Arial"/>
                    </font>
                    <font>
                        <b/>
                        <sz val="10"/>
                        <color rgb="FFB91C1C"/>
                        <name val="Arial"/>
                    </font>
                </fonts>
                <fills count="11">
                    <fill><patternFill patternType="none"/></fill>
                    <fill><patternFill patternType="gray125"/></fill>
                    <fill><patternFill patternType="solid"><fgColor rgb="FF0F294A"/></patternFill></fill>
                    <fill><patternFill patternType="solid"><fgColor rgb="FFE0F2FE"/></patternFill></fill>
                    <fill><patternFill patternType="solid"><fgColor rgb="FF1E3A5F"/></patternFill></fill>
                    <fill><patternFill patternType="solid"><fgColor rgb="FFFFFFFF"/></patternFill></fill>
                    <fill><patternFill patternType="solid"><fgColor rgb="FFF8FAFC"/></patternFill></fill>
                    <fill><patternFill patternType="solid"><fgColor rgb="FFDCFCE7"/></patternFill></fill>
                    <fill><patternFill patternType="solid"><fgColor rgb="FFE0F2FE"/></patternFill></fill>
                    <fill><patternFill patternType="solid"><fgColor rgb="FFFEF3C7"/></patternFill></fill>
                    <fill><patternFill patternType="solid"><fgColor rgb="FFFEE2E2"/></patternFill></fill>
                </fills>
                <borders count="3">
                    <border><left/><right/><top/><bottom/><diagonal/></border>
                    <border>
                        <left style="thin"><color rgb="FFCBD5E1"/></left>
                        <right style="thin"><color rgb="FFCBD5E1"/></right>
                        <top style="thin"><color rgb="FFCBD5E1"/></top>
                        <bottom style="thin"><color rgb="FFCBD5E1"/></bottom>
                        <diagonal/>
                    </border>
                    <border>
                        <left style="thin"><color rgb="FF334155"/></left>
                        <right style="thin"><color rgb="FF334155"/></right>
                        <top style="thin"><color rgb="FF334155"/></top>
                        <bottom style="medium"><color rgb="FF0EA5E9"/></bottom>
                        <diagonal/>
                    </border>
                </borders>
                <cellStyleXfs count="1">
                    <xf numFmtId="0" fontId="0" fillId="0" borderId="0"/>
                </cellStyleXfs>
                <cellXfs count="12">
                    <xf numFmtId="0" fontId="0" fillId="0" borderId="0" xfId="0"/>
                    <xf numFmtId="0" fontId="1" fillId="2" borderId="0" xfId="0" applyFont="1" applyFill="1" applyAlignment="1">
                        <alignment horizontal="center" vertical="center" wrapText="1"/>
                    </xf>
                    <xf numFmtId="0" fontId="2" fillId="3" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1">
                        <alignment horizontal="center" vertical="center" wrapText="1"/>
                    </xf>
                    <xf numFmtId="0" fontId="3" fillId="4" borderId="2" xfId="0" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1">
                        <alignment horizontal="center" vertical="center" wrapText="1"/>
                    </xf>
                    <xf numFmtId="0" fontId="0" fillId="5" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1">
                        <alignment horizontal="center" vertical="center" wrapText="1"/>
                    </xf>
                    <xf numFmtId="0" fontId="4" fillId="5" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1">
                        <alignment horizontal="$textAlign" vertical="center" wrapText="1"/>
                    </xf>
                    <xf numFmtId="0" fontId="0" fillId="6" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1">
                        <alignment horizontal="center" vertical="center" wrapText="1"/>
                    </xf>
                    <xf numFmtId="0" fontId="4" fillId="6" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1">
                        <alignment horizontal="$textAlign" vertical="center" wrapText="1"/>
                    </xf>
                    <xf numFmtId="0" fontId="5" fillId="7" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1">
                        <alignment horizontal="center" vertical="center" wrapText="1"/>
                    </xf>
                    <xf numFmtId="0" fontId="6" fillId="8" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1">
                        <alignment horizontal="center" vertical="center" wrapText="1"/>
                    </xf>
                    <xf numFmtId="0" fontId="7" fillId="9" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1">
                        <alignment horizontal="center" vertical="center" wrapText="1"/>
                    </xf>
                    <xf numFmtId="0" fontId="8" fillId="10" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1">
                        <alignment horizontal="center" vertical="center" wrapText="1"/>
                    </xf>
                </cellXfs>
                <cellStyles count="1">
                    <cellStyle name="Normal" xfId="0" builtinId="0"/>
                </cellStyles>
                <dxfs count="0"/>
                <tableStyles count="0" defaultTableStyle="TableStyleMedium2" defaultPivotStyle="PivotStyleLight16"/>
            </styleSheet>
        """.trimIndent()
    }

    private fun buildPersonnelSheetXml(
        personnelList: List<Personnel>,
        lang: AppLanguage
    ): String {
        val lastCol = 'K'
        val colCount = 11
        val lastRow = (3 + personnelList.size).coerceAtLeast(3)
        val printDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())
        val rtlFlag = if (lang.isRtl) "1" else "0"

        val titleText = "Respite Pulse — ${AppStrings.get("excel_personnel_report_title", lang)}"
        val metaText = "${AppStrings.get("pdf_extracted_at", lang)} $printDate   |   ${AppStrings.get("pdf_total_records", lang)} ${personnelList.size}"

        val headers = listOf(
            "#",
            AppStrings.get("pdf_col_name", lang),
            AppStrings.get("pdf_col_rank", lang),
            AppStrings.get("pdf_col_reg", lang),
            AppStrings.get("pdf_col_wilaya", lang),
            AppStrings.get("leave_type", lang),
            AppStrings.get("pdf_col_start", lang),
            AppStrings.get("pdf_col_return", lang),
            AppStrings.get("excel_col_days", lang),
            AppStrings.get("pdf_col_status", lang),
            AppStrings.get("notes_plain", lang)
        )

        val widths = listOf(6.0, 24.0, 17.0, 15.0, 18.0, 16.0, 13.0, 13.0, 10.0, 22.0, 22.0)

        val sb = StringBuilder(8192)
        sb.append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""")
        sb.append(
            """<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">"""
        )
        sb.append("""<sheetPr><pageSetUpPr fitToPage="1"/></sheetPr>""")
        sb.append("""<dimension ref="A1:$lastCol$lastRow"/>""")
        sb.append(
            """<sheetViews><sheetView tabSelected="1" rightToLeft="$rtlFlag" workbookViewId="0"><pane ySplit="3" topLeftCell="A4" activePane="bottomLeft" state="frozen"/><selection pane="bottomLeft" activeCell="A4" sqref="A4"/></sheetView></sheetViews>"""
        )
        sb.append("""<sheetFormatPr defaultRowHeight="22"/>""")
        sb.append("<cols>")
        widths.forEachIndexed { idx, w ->
            val colIdx = idx + 1
            sb.append("""<col min="$colIdx" max="$colIdx" width="$w" customWidth="1"/>""")
        }
        sb.append("</cols>")
        sb.append("<sheetData>")

        // Row 1: Title Banner
        sb.append("""<row r="1" ht="34" customHeight="1">""")
        for (c in 0 until colCount) {
            val ref = "${colLetter(c)}1"
            if (c == 0) appendTextCell(sb, ref, titleText, styleId = 1)
            else appendEmptyStyledCell(sb, ref, styleId = 1)
        }
        sb.append("</row>")

        // Row 2: Subtitle Metadata
        sb.append("""<row r="2" ht="24" customHeight="1">""")
        for (c in 0 until colCount) {
            val ref = "${colLetter(c)}2"
            if (c == 0) appendTextCell(sb, ref, metaText, styleId = 2)
            else appendEmptyStyledCell(sb, ref, styleId = 2)
        }
        sb.append("</row>")

        // Row 3: Column Headers
        sb.append("""<row r="3" ht="28" customHeight="1">""")
        headers.forEachIndexed { c, header ->
            appendTextCell(sb, "${colLetter(c)}3", header, styleId = 3)
        }
        sb.append("</row>")

        // Data rows
        personnelList.forEachIndexed { index, person ->
            val rowNum = index + 4
            val isOdd = index % 2 == 1
            val centerStyle = if (isOdd) 6 else 4
            val textStyle = if (isOdd) 7 else 5

            val statusStyle = when (person.status) {
                LeaveStatus.ON_DUTY -> 8
                LeaveStatus.AT_HOME, LeaveStatus.UPCOMING -> 9
                LeaveStatus.RETURNING_TODAY -> 10
                LeaveStatus.OVERDUE -> 11
            }

            sb.append("""<row r="$rowNum" ht="23" customHeight="1">""")
            appendNumberCell(sb, "A$rowNum", (index + 1).toLong(), styleId = centerStyle)
            appendTextCell(sb, "B$rowNum", person.getLocalizedName(lang), styleId = textStyle)
            appendTextCell(sb, "C$rowNum", formatRank(person.rank, lang), styleId = centerStyle)
            appendTextCell(sb, "D$rowNum", person.registrationNumber, styleId = centerStyle)
            appendTextCell(sb, "E$rowNum", formatWilaya(person.wilaya, lang, includeCode = false), styleId = centerStyle)
            appendTextCell(sb, "F$rowNum", formatLeaveType(person.leaveType, lang), styleId = centerStyle)
            appendTextCell(sb, "G$rowNum", stripBidi(person.formattedStartDate), styleId = centerStyle)
            appendTextCell(sb, "H$rowNum", stripBidi(person.formattedReturnDate), styleId = centerStyle)
            appendNumberCell(sb, "I$rowNum", person.totalLeaveDays, styleId = centerStyle)
            appendTextCell(sb, "J$rowNum", person.getCountdownText(lang), styleId = statusStyle)
            appendTextCell(sb, "K$rowNum", person.notes.ifBlank { "—" }, styleId = textStyle)
            sb.append("</row>")
        }

        sb.append("</sheetData>")
        sb.append("""<autoFilter ref="A3:$lastCol$lastRow"/>""")
        sb.append("""<mergeCells count="2"><mergeCell ref="A1:${lastCol}1"/><mergeCell ref="A2:${lastCol}2"/></mergeCells>""")
        sb.append("""<pageMargins left="0.35" right="0.35" top="0.45" bottom="0.45" header="0.2" footer="0.2"/>""")
        sb.append("""<pageSetup paperSize="9" orientation="landscape" fitToWidth="1" fitToHeight="0" horizontalDpi="300" verticalDpi="300"/>""")
        sb.append("""<headerFooter><oddFooter>&amp;CRespite Pulse — &amp;P / &amp;N</oddFooter></headerFooter>""")
        sb.append("</worksheet>")
        return sb.toString()
    }

    private fun buildLeavesSheetXml(
        leaveRecords: List<LeaveRecord>,
        lang: AppLanguage
    ): String {
        val lastCol = 'J'
        val colCount = 10
        val lastRow = (3 + leaveRecords.size).coerceAtLeast(3)
        val printDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())
        val rtlFlag = if (lang.isRtl) "1" else "0"

        val titleText = "Respite Pulse — ${AppStrings.get("excel_leaves_report_title", lang)}"
        val metaText = "${AppStrings.get("pdf_extracted_at", lang)} $printDate   |   ${AppStrings.get("room_total_leaves_count", lang)}: ${leaveRecords.size}"

        val headers = listOf(
            "#",
            AppStrings.get("pdf_col_name", lang),
            AppStrings.get("pdf_col_rank", lang),
            AppStrings.get("pdf_col_reg", lang),
            AppStrings.get("leave_type", lang),
            AppStrings.get("pdf_col_start", lang),
            AppStrings.get("pdf_col_return", lang),
            AppStrings.get("excel_col_days", lang),
            AppStrings.get("excel_col_leave_status", lang),
            AppStrings.get("notes_plain", lang)
        )

        val widths = listOf(6.0, 24.0, 17.0, 15.0, 17.0, 13.0, 13.0, 10.0, 16.0, 24.0)

        val sb = StringBuilder(8192)
        sb.append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""")
        sb.append(
            """<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">"""
        )
        sb.append("""<sheetPr><pageSetUpPr fitToPage="1"/></sheetPr>""")
        sb.append("""<dimension ref="A1:$lastCol$lastRow"/>""")
        sb.append(
            """<sheetViews><sheetView tabSelected="0" rightToLeft="$rtlFlag" workbookViewId="0"><pane ySplit="3" topLeftCell="A4" activePane="bottomLeft" state="frozen"/><selection pane="bottomLeft" activeCell="A4" sqref="A4"/></sheetView></sheetViews>"""
        )
        sb.append("""<sheetFormatPr defaultRowHeight="22"/>""")
        sb.append("<cols>")
        widths.forEachIndexed { idx, w ->
            val colIdx = idx + 1
            sb.append("""<col min="$colIdx" max="$colIdx" width="$w" customWidth="1"/>""")
        }
        sb.append("</cols>")
        sb.append("<sheetData>")

        sb.append("""<row r="1" ht="34" customHeight="1">""")
        for (c in 0 until colCount) {
            val ref = "${colLetter(c)}1"
            if (c == 0) appendTextCell(sb, ref, titleText, styleId = 1)
            else appendEmptyStyledCell(sb, ref, styleId = 1)
        }
        sb.append("</row>")

        sb.append("""<row r="2" ht="24" customHeight="1">""")
        for (c in 0 until colCount) {
            val ref = "${colLetter(c)}2"
            if (c == 0) appendTextCell(sb, ref, metaText, styleId = 2)
            else appendEmptyStyledCell(sb, ref, styleId = 2)
        }
        sb.append("</row>")

        sb.append("""<row r="3" ht="28" customHeight="1">""")
        headers.forEachIndexed { c, header ->
            appendTextCell(sb, "${colLetter(c)}3", header, styleId = 3)
        }
        sb.append("</row>")

        leaveRecords.forEachIndexed { index, record ->
            val rowNum = index + 4
            val isOdd = index % 2 == 1
            val centerStyle = if (isOdd) 6 else 4
            val textStyle = if (isOdd) 7 else 5

            val isCompleted = record.statusText.contains("منتهية") ||
                record.statusText.contains("مكتملة") ||
                record.statusText.contains("Termin", ignoreCase = true) ||
                record.statusText.contains("Complet", ignoreCase = true) ||
                record.statusText.contains("Yemmed", ignoreCase = true)
            val statusStyle = if (isCompleted) 8 else 9

            sb.append("""<row r="$rowNum" ht="23" customHeight="1">""")
            appendNumberCell(sb, "A$rowNum", (index + 1).toLong(), styleId = centerStyle)
            appendTextCell(
                sb,
                "B$rowNum",
                Personnel.resolveLocalizedName(record.personnelName, record.personnelNameLatin, lang),
                styleId = textStyle
            )
            appendTextCell(sb, "C$rowNum", formatRank(record.rank, lang), styleId = centerStyle)
            appendTextCell(sb, "D$rowNum", record.registrationNumber, styleId = centerStyle)
            appendTextCell(sb, "E$rowNum", formatLeaveType(record.leaveType, lang), styleId = centerStyle)
            appendTextCell(sb, "F$rowNum", stripBidi(record.formattedStartDate), styleId = centerStyle)
            appendTextCell(sb, "G$rowNum", stripBidi(record.formattedReturnDate), styleId = centerStyle)
            appendNumberCell(sb, "H$rowNum", record.durationDays, styleId = centerStyle)
            appendTextCell(sb, "I$rowNum", record.statusText, styleId = statusStyle)
            appendTextCell(sb, "J$rowNum", record.notes.ifBlank { "—" }, styleId = textStyle)
            sb.append("</row>")
        }

        sb.append("</sheetData>")
        sb.append("""<autoFilter ref="A3:$lastCol$lastRow"/>""")
        sb.append("""<mergeCells count="2"><mergeCell ref="A1:${lastCol}1"/><mergeCell ref="A2:${lastCol}2"/></mergeCells>""")
        sb.append("""<pageMargins left="0.35" right="0.35" top="0.45" bottom="0.45" header="0.2" footer="0.2"/>""")
        sb.append("""<pageSetup paperSize="9" orientation="landscape" fitToWidth="1" fitToHeight="0" horizontalDpi="300" verticalDpi="300"/>""")
        sb.append("""<headerFooter><oddFooter>&amp;CRespite Pulse — &amp;P / &amp;N</oddFooter></headerFooter>""")
        sb.append("</worksheet>")
        return sb.toString()
    }

    private fun appendTextCell(sb: StringBuilder, ref: String, value: String, styleId: Int) {
        sb.append("""<c r="$ref" t="inlineStr" s="$styleId"><is><t xml:space="preserve">${escapeXml(value)}</t></is></c>""")
    }

    private fun appendEmptyStyledCell(sb: StringBuilder, ref: String, styleId: Int) {
        sb.append("""<c r="$ref" s="$styleId"/>""")
    }

    private fun appendNumberCell(sb: StringBuilder, ref: String, value: Long, styleId: Int) {
        sb.append("""<c r="$ref" s="$styleId"><v>$value</v></c>""")
    }

    private fun colLetter(zeroBasedIndex: Int): String = ('A' + zeroBasedIndex).toString()

    private fun stripBidi(value: String): String =
        value.replace("\u200E", "").replace("\u200F", "").trim()

    private fun sanitizeSheetName(name: String): String {
        val cleaned = name
            .replace(Regex("[\\\\/?*\\[\\]:]"), " ")
            .trim()
            .trim('\'')
        if (cleaned.isBlank()) return "Sheet"
        val out = StringBuilder(31)
        var i = 0
        while (i < cleaned.length) {
            val cp = cleaned.codePointAt(i)
            val count = Character.charCount(cp)
            if (out.length + count > 31) break
            out.appendCodePoint(cp)
            i += count
        }
        return out.toString().trim().trim('\'').ifBlank { "Sheet" }
    }

    private fun escapeXml(input: String): String {
        val out = StringBuilder(input.length + 16)
        var i = 0
        while (i < input.length) {
            val cp = input.codePointAt(i)
            val charCount = Character.charCount(cp)
            when (cp) {
                '&'.code -> out.append("&amp;")
                '<'.code -> out.append("&lt;")
                '>'.code -> out.append("&gt;")
                '"'.code -> out.append("&quot;")
                '\''.code -> out.append("&apos;")
                0x9, 0xA, 0xD -> out.appendCodePoint(cp)
                else -> {
                    val isValidXml10 = (cp in 0x20..0xD7FF) ||
                        (cp in 0xE000..0xFFFD) ||
                        (cp in 0x10000..0x10FFFF)
                    if (isValidXml10) {
                        out.appendCodePoint(cp)
                    }
                }
            }
            i += charCount
        }
        return out.toString()
    }
}
