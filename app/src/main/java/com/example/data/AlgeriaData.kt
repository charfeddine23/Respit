package com.example.data

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

    // 2. Match Arabic portion or French portion
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
