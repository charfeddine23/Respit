package com.example.data

import com.example.util.AppLanguage

val algerianRanks = listOf(
    "عميد - Général / Brigadier",
    "عقيد - Colonel",
    "مقدم - Lieutenant-Colonel",
    "رائد - Commandant",
    "نقيب - Capitaine",
    "ملازم أول - Lieutenant",
    "ملازم - Sous-Lieutenant",
    "مرشح - Aspirant / Élève-Officier",
    "مساعد رئيسي - Adjudant-Major",
    "مساعد أول - Adjudant-Chef",
    "مساعد - Adjudant",
    "رقيب أول - Sergent-Chef",
    "رقيب - Sergent",
    "عريف أول - Caporal-Chef",
    "عريف - Caporal",
    "جندي - Djoundi / Soldat"
)

private val rankEnglishMap = mapOf(
    "عميد - Général / Brigadier" to "Brigadier General",
    "عقيد - Colonel" to "Colonel",
    "مقدم - Lieutenant-Colonel" to "Lieutenant Colonel",
    "رائد - Commandant" to "Major",
    "نقيب - Capitaine" to "Captain",
    "ملازم أول - Lieutenant" to "First Lieutenant",
    "ملازم - Sous-Lieutenant" to "Second Lieutenant",
    "مرشح - Aspirant / Élève-Officier" to "Officer Cadet",
    "مساعد رئيسي - Adjudant-Major" to "Sergeant Major",
    "مساعد أول - Adjudant-Chef" to "Chief Warrant Officer",
    "مساعد - Adjudant" to "Warrant Officer",
    "رقيب أول - Sergent-Chef" to "Staff Sergeant",
    "رقيب - Sergent" to "Sergeant",
    "عريف أول - Caporal-Chef" to "Master Corporal",
    "عريف - Caporal" to "Corporal",
    "جندي - Djoundi / Soldat" to "Soldier"
)

val algerianLeaveTypes = listOf(
    "إجازة",
    "إجازة قصيرة",
    "عطلة سنوية",
    "نقاهة"
)

val algerianWilayas = listOf(
    "01 - أدرار - Adrar",
    "02 - الشلف - Chlef",
    "03 - الأغواط - Laghouat",
    "04 - أم البواقي - Oum El Bouaghi",
    "05 - باتنة - Batna",
    "06 - بجاية - Béjaïa",
    "07 - بسكرة - Biskra",
    "08 - بشار - Béchar",
    "09 - البليدة - Blida",
    "10 - البويرة - Bouira",
    "11 - تمنراست - Tamanrasset",
    "12 - تبسة - Tébessa",
    "13 - تلمسان - Tlemcen",
    "14 - تيارت - Tiaret",
    "15 - تيزي وزو - Tizi Ouzou",
    "16 - الجزائر - Alger",
    "17 - الجلفة - Djelfa",
    "18 - جيجل - Jijel",
    "19 - سطيف - Sétif",
    "20 - سعيدة - Saïda",
    "21 - سكيكدة - Skikda",
    "22 - سيدي بلعباس - Sidi Bel Abbès",
    "23 - عنابة - Annaba",
    "24 - قالمة - Guelma",
    "25 - قسنطينة - Constantine",
    "26 - المدية - Médéa",
    "27 - مستغانم - Mostaganem",
    "28 - المسيلة - M'Sila",
    "29 - معسكر - Mascara",
    "30 - ورقلة - Ouargla",
    "31 - وهران - Oran",
    "32 - البيض - El Bayadh",
    "33 - إليزي - Illizi",
    "34 - برج بوعريريج - Bordj Bou Arreridj",
    "35 - بومرداس - Boumerdès",
    "36 - الطارف - El Tarf",
    "37 - تندوف - Tindouf",
    "38 - تيسمسيلت - Tissemsilt",
    "39 - الوادي - El Oued",
    "40 - خنشلة - Khenchela",
    "41 - سوق أهراس - Souk Ahras",
    "42 - تيبازة - Tipaza",
    "43 - ميلة - Mila",
    "44 - عين الدفلى - Aïn Defla",
    "45 - النعامة - Naâma",
    "46 - عين تموشنت - Aïn Témouchent",
    "47 - غرداية - Ghardaïa",
    "48 - غليزان - Relizane",
    "49 - تيميمون - Timimoun",
    "50 - برج باجي مختار - Bordj Badji Mokhtar",
    "51 - أولاد جلال - Ouled Djellal",
    "52 - بني عباس - Béni Abbès",
    "53 - عين صالح - In Salah",
    "54 - عين قزام - In Guezzam",
    "55 - تقرت - Touggourt",
    "56 - جانت - Djanet",
    "57 - المغير - El M'Ghair",
    "58 - المنيعة - El Meniaa",
    "59 - أفلو - Aflou",
    "60 - بريكة - Barika",
    "61 - القنطرة - El Kantara",
    "62 - بئر العاتر - Bir El Ater",
    "63 - العريشة - El Aricha",
    "64 - قصر الشلالة - Ksar Chellala",
    "65 - عين وسارة - Aïn Ouessara",
    "66 - مسعد - Messaad",
    "67 - قصر البخاري - Ksar El Boukhari",
    "68 - بوسعادة - Bou Saâda",
    "69 - الأبيض سيدي الشيخ - El Abiodh Sidi Cheikh"
)

/**
 * Normalizes older/legacy wilaya string to the new standardized format:
 * "Code - Arabic Name - Latin Name".
 * Ensures backward compatibility with existing databases and imports.
 */
fun normalizeWilaya(raw: String?): String {
    if (raw.isNullOrBlank()) return algerianWilayas[15] // "16 - الجزائر - Alger"
    val trimmed = raw.trim()

    // 1. Exact match
    val exact = algerianWilayas.find { it.equals(trimmed, ignoreCase = true) }
    if (exact != null) return exact

    // 2. Match by 2-digit number prefix (e.g. "16", "05", "16 - الجزائر")
    val codePrefix = trimmed.takeWhile { it.isDigit() }
    if (codePrefix.isNotEmpty()) {
        val formattedCode = codePrefix.padStart(2, '0')
        val matchCode = algerianWilayas.find { it.startsWith(formattedCode) }
        if (matchCode != null) return matchCode
    }

    // 3. Match by Arabic or Latin sub-strings
    val matchByName = algerianWilayas.find { item ->
        val parts = item.split(" - ").map { it.trim() }
        parts.any { p -> trimmed.contains(p, ignoreCase = true) || p.contains(trimmed, ignoreCase = true) }
    }
    if (matchByName != null) return matchByName

    return trimmed
}

/**
 * Normalizes older/legacy military rank string to the new bilingual standard:
 * "Arabic Name - French / Latin Name".
 * Ensures backward compatibility with existing databases and imports.
 */
fun normalizeRank(raw: String?): String {
    if (raw.isNullOrBlank()) return algerianRanks.last() // "جندي - Djoundi / Soldat"
    val trimmed = raw.trim()

    // 1. Exact match
    val exact = algerianRanks.find { it.equals(trimmed, ignoreCase = true) }
    if (exact != null) return exact

    // 2. Match English rank name
    val enMatch = rankEnglishMap.entries.find { it.value.equals(trimmed, ignoreCase = true) }?.key
    if (enMatch != null) return enMatch

    // 3. Match Arabic portion or French portion
    val match = algerianRanks.find { item ->
        val arPart = item.substringBefore(" - ").trim()
        val frPart = item.substringAfter(" - ").trim()
        trimmed.equals(arPart, ignoreCase = true) ||
        arPart.contains(trimmed, ignoreCase = true) ||
        frPart.split("/").any { it.trim().equals(trimmed, ignoreCase = true) }
    }
    if (match != null) return match

    return trimmed
}

/**
 * Formats a rank for display in the selected UI language:
 * - ARABIC -> Arabic rank only
 * - FRENCH -> French rank only
 * - ENGLISH -> English rank translation
 */
fun formatRank(rawRank: String?, lang: AppLanguage): String {
    val canonical = normalizeRank(rawRank)
    return when (lang) {
        AppLanguage.ARABIC -> canonical.substringBefore(" - ").trim()
        AppLanguage.FRENCH, AppLanguage.TAMAZIGHT -> canonical.substringAfter(" - ", canonical).trim()
        AppLanguage.ENGLISH -> rankEnglishMap[canonical] ?: canonical.substringAfter(" - ", canonical).trim()
    }
}

/**
 * Formats a Wilaya for display in the selected UI language.
 * By default (`includeCode = false`), returns only the province name without its code number.
 */
fun formatWilaya(rawWilaya: String?, lang: AppLanguage, includeCode: Boolean = false): String {
    val canonical = normalizeWilaya(rawWilaya)
    val parts = canonical.split(" - ").map { it.trim() }
    return if (parts.size >= 3) {
        val code = parts[0]
        val localizedName = if (lang == AppLanguage.ARABIC) parts[1] else parts[2]
        if (includeCode) "$code - $localizedName" else localizedName
    } else if (parts.size == 2) {
        if (includeCode) canonical else parts[1]
    } else {
        canonical
    }
}

/**
 * Formats an Algerian leave type for display in the selected UI language.
 */
fun formatLeaveType(rawType: String?, lang: AppLanguage): String {
    val t = rawType?.trim() ?: ""
    return when (lang) {
        AppLanguage.ARABIC -> when {
            t.equals("Permission", ignoreCase = true) || t.equals("Leave", ignoreCase = true) -> "إجازة"
            t.equals("Permission courte", ignoreCase = true) || t.equals("Short Leave", ignoreCase = true) -> "إجازة قصيرة"
            t.equals("Congé annuel", ignoreCase = true) || t.equals("Annual Leave", ignoreCase = true) -> "عطلة سنوية"
            t.equals("Convalescence", ignoreCase = true) || t.equals("Convalescence Leave", ignoreCase = true) -> "نقاهة"
            t.isBlank() -> "عطلة سنوية"
            else -> t
        }
        AppLanguage.FRENCH -> when (t) {
            "إجازة", "Leave" -> "Permission"
            "إجازة قصيرة", "Short Leave" -> "Permission courte"
            "عطلة سنوية", "Annual Leave", "" -> "Congé annuel"
            "نقاهة", "Convalescence Leave" -> "Convalescence"
            else -> t
        }
        AppLanguage.TAMAZIGHT -> when (t) {
            "إجازة", "Permission", "Leave" -> "Turagt"
            "إجازة قصيرة", "Permission courte", "Short Leave" -> "Turagt tawezzlant"
            "عطلة سنوية", "Congé annuel", "Annual Leave", "" -> "Imuras n useggas"
            "نقاهة", "Convalescence", "Convalescence Leave" -> "Tujjya"
            else -> t
        }
        AppLanguage.ENGLISH -> when (t) {
            "إجازة", "Permission" -> "Leave"
            "إجازة قصيرة", "Permission courte" -> "Short Leave"
            "عطلة سنوية", "Congé annuel", "" -> "Annual Leave"
            "نقاهة", "Convalescence" -> "Convalescence Leave"
            else -> t
        }
    }
}
