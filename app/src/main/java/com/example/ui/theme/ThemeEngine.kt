package com.example.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

enum class ThemeCategory {
    CLASSIC_DARK,
    CLASSIC_LIGHT,
    LIQUID_GLASS
}

data class AppThemePreset(
    val id: String,
    val name: String,
    val category: ThemeCategory,
    val isDark: Boolean,
    val isLiquidGlass: Boolean,
    val backgroundTop: Color,
    val backgroundMid: Color,
    val backgroundBottom: Color,
    val surfacePrimary: Color,
    val surfaceSecondary: Color,
    val surfaceVariant: Color,
    val cardTintTop: Color,
    val cardTintBottom: Color,
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val borderHighlight: Color,
    val borderAccent: Color,
    val orb1: Color,
    val orb2: Color,
    val orb3: Color
) {
    val onPrimary: Color
        get() = if (primary.luminance() > 0.45f) Color(0xFF0B1120) else Color.White

    val onSecondary: Color
        get() = if (secondary.luminance() > 0.45f) Color(0xFF0B1120) else Color.White

    val onTertiary: Color
        get() = if (tertiary.luminance() > 0.45f) Color(0xFF0B1120) else Color.White

    val previewSwatches: List<Color>
        get() = listOf(backgroundMid, surfacePrimary, primary, secondary)
}

val LocalThemePreset = staticCompositionLocalOf<AppThemePreset?> { null }

object ThemeEngine {

    const val DEFAULT_ID = ""

    val allThemes: List<AppThemePreset> = listOf(
        // 1. Monokai
        darkPreset(
            id = "monokai",
            name = "Monokai",
            bgTop = Color(0xFF1E1F1C),
            bgMid = Color(0xFF272822),
            bgBot = Color(0xFF1B1C18),
            surface = Color(0xFF2E2F29),
            surfaceAlt = Color(0xFF3E3D32),
            primary = Color(0xFFA6E22E),
            secondary = Color(0xFFF92672),
            tertiary = Color(0xFF66D9EF),
            textPrimary = Color(0xFFF8F8F2),
            textSecondary = Color(0xFFA59F85)
        ),
        // 2. Monokai Pro
        darkPreset(
            id = "monokai_pro",
            name = "Monokai Pro",
            bgTop = Color(0xFF221F22),
            bgMid = Color(0xFF2D2A2E),
            bgBot = Color(0xFF19181A),
            surface = Color(0xFF363338),
            surfaceAlt = Color(0xFF403E41),
            primary = Color(0xFFFFD866),
            secondary = Color(0xFFFF6188),
            tertiary = Color(0xFF78DCE8),
            textPrimary = Color(0xFFFCFCFA),
            textSecondary = Color(0xFF939293)
        ),
        // 3. Solarized Dark
        darkPreset(
            id = "solarized_dark",
            name = "Solarized Dark",
            bgTop = Color(0xFF001E26),
            bgMid = Color(0xFF002B36),
            bgBot = Color(0xFF00171D),
            surface = Color(0xFF073642),
            surfaceAlt = Color(0xFF0D4452),
            primary = Color(0xFF2AA198),
            secondary = Color(0xFF268BD2),
            tertiary = Color(0xFFB58900),
            textPrimary = Color(0xFFEEE8D5),
            textSecondary = Color(0xFF93A1A1)
        ),
        // 4. Solarized Light
        lightPreset(
            id = "solarized_light",
            name = "Solarized Light",
            bgTop = Color(0xFFFDF6E3),
            bgMid = Color(0xFFF7EED6),
            bgBot = Color(0xFFEEE8D5),
            surface = Color(0xFFFDF8EC),
            surfaceAlt = Color(0xFFEEE8D5),
            primary = Color(0xFF268BD2),
            secondary = Color(0xFF2AA198),
            tertiary = Color(0xFFCB4B16),
            textPrimary = Color(0xFF073642),
            textSecondary = Color(0xFF586E75)
        ),
        // 5. One Dark Pro
        darkPreset(
            id = "one_dark_pro",
            name = "One Dark Pro",
            bgTop = Color(0xFF1E222A),
            bgMid = Color(0xFF282C34),
            bgBot = Color(0xFF1B1E25),
            surface = Color(0xFF2F343F),
            surfaceAlt = Color(0xFF3B4048),
            primary = Color(0xFF61AFEF),
            secondary = Color(0xFFC678DD),
            tertiary = Color(0xFF98C379),
            textPrimary = Color(0xFFECEFF4),
            textSecondary = Color(0xFF9DA5B4)
        ),
        // 6. One Light
        lightPreset(
            id = "one_light",
            name = "One Light",
            bgTop = Color(0xFFFAFAFA),
            bgMid = Color(0xFFF0F1F4),
            bgBot = Color(0xFFE5E7EB),
            surface = Color(0xFFFFFFFF),
            surfaceAlt = Color(0xFFEAECEF),
            primary = Color(0xFF4078F2),
            secondary = Color(0xFFA626A4),
            tertiary = Color(0xFF50A14F),
            textPrimary = Color(0xFF24292E),
            textSecondary = Color(0xFF696C77)
        ),
        // 7. Dracula
        darkPreset(
            id = "dracula",
            name = "Dracula",
            bgTop = Color(0xFF21222C),
            bgMid = Color(0xFF282A36),
            bgBot = Color(0xFF191A21),
            surface = Color(0xFF343746),
            surfaceAlt = Color(0xFF44475A),
            primary = Color(0xFFBD93F9),
            secondary = Color(0xFFFF79C6),
            tertiary = Color(0xFF50FA7B),
            textPrimary = Color(0xFFF8F8F2),
            textSecondary = Color(0xFFA7B3D9)
        ),
        // 8. Tomorrow Night Eighties
        darkPreset(
            id = "tomorrow_night_eighties",
            name = "Tomorrow Night Eighties",
            bgTop = Color(0xFF242424),
            bgMid = Color(0xFF2D2D2D),
            bgBot = Color(0xFF1E1E1E),
            surface = Color(0xFF393939),
            surfaceAlt = Color(0xFF474747),
            primary = Color(0xFF6699CC),
            secondary = Color(0xFFCC99CC),
            tertiary = Color(0xFFF99157),
            textPrimary = Color(0xFFEEEEEE),
            textSecondary = Color(0xFFA9A9A9)
        ),
        // 9. Nord
        darkPreset(
            id = "nord",
            name = "Nord",
            bgTop = Color(0xFF242933),
            bgMid = Color(0xFF2E3440),
            bgBot = Color(0xFF1F242D),
            surface = Color(0xFF3B4252),
            surfaceAlt = Color(0xFF434C5E),
            primary = Color(0xFF88C0D0),
            secondary = Color(0xFF81A1C1),
            tertiary = Color(0xFFA3BE8C),
            textPrimary = Color(0xFFECEFF4),
            textSecondary = Color(0xFFD8DEE9)
        ),
        // 10. Night Owl
        darkPreset(
            id = "night_owl",
            name = "Night Owl",
            bgTop = Color(0xFF010E1A),
            bgMid = Color(0xFF011627),
            bgBot = Color(0xFF010B14),
            surface = Color(0xFF0B253A),
            surfaceAlt = Color(0xFF123552),
            primary = Color(0xFF82AAFF),
            secondary = Color(0xFFC792EA),
            tertiary = Color(0xFF7FDBCA),
            textPrimary = Color(0xFFD6DEEB),
            textSecondary = Color(0xFF89A4BB)
        ),
        // 11. Bluloco Dark
        darkPreset(
            id = "bluloco_dark",
            name = "Bluloco Dark",
            bgTop = Color(0xFF1E212B),
            bgMid = Color(0xFF282C38),
            bgBot = Color(0xFF181A22),
            surface = Color(0xFF313646),
            surfaceAlt = Color(0xFF3E4457),
            primary = Color(0xFF3691FF),
            secondary = Color(0xFFFF6480),
            tertiary = Color(0xFF3FC56B),
            textPrimary = Color(0xFFEDEFF7),
            textSecondary = Color(0xFF9EA6BD)
        ),
        // 12. Ayu Dark
        darkPreset(
            id = "ayu_dark",
            name = "Ayu Dark",
            bgTop = Color(0xFF0A0E14),
            bgMid = Color(0xFF0F1419),
            bgBot = Color(0xFF070A0E),
            surface = Color(0xFF151A22),
            surfaceAlt = Color(0xFF1E242E),
            primary = Color(0xFFFFB454),
            secondary = Color(0xFF39BAE6),
            tertiary = Color(0xFFC2D94C),
            textPrimary = Color(0xFFECEAE4),
            textSecondary = Color(0xFF919B9E)
        ),
        // 13. Ayu Mirage
        darkPreset(
            id = "ayu_mirage",
            name = "Ayu Mirage",
            bgTop = Color(0xFF191E2A),
            bgMid = Color(0xFF1F2430),
            bgBot = Color(0xFF141821),
            surface = Color(0xFF272D3C),
            surfaceAlt = Color(0xFF32394A),
            primary = Color(0xFFFFCC66),
            secondary = Color(0xFF73D0FF),
            tertiary = Color(0xFFBAE67E),
            textPrimary = Color(0xFFEDEAE0),
            textSecondary = Color(0xFFA0A8B8)
        ),
        // 14. Ayu Light
        lightPreset(
            id = "ayu_light",
            name = "Ayu Light",
            bgTop = Color(0xFFFAFAFA),
            bgMid = Color(0xFFF3F4F6),
            bgBot = Color(0xFFEAECF0),
            surface = Color(0xFFFFFFFF),
            surfaceAlt = Color(0xFFF0F2F5),
            primary = Color(0xFFFF9940),
            secondary = Color(0xFF399EE6),
            tertiary = Color(0xFF86B300),
            textPrimary = Color(0xFF3B4252),
            textSecondary = Color(0xFF6C7680)
        ),
        // 15. Winter is Coming
        darkPreset(
            id = "winter_is_coming",
            name = "Winter is Coming",
            bgTop = Color(0xFF011221),
            bgMid = Color(0xFF011627),
            bgBot = Color(0xFF021C33),
            surface = Color(0xFF09223A),
            surfaceAlt = Color(0xFF113152),
            primary = Color(0xFF00BFF9),
            secondary = Color(0xFF7FDBCA),
            tertiary = Color(0xFFB39DDB),
            textPrimary = Color(0xFFD6DEEB),
            textSecondary = Color(0xFF8FAFD0)
        ),
        // 16. Zenburn
        darkPreset(
            id = "zenburn",
            name = "Zenburn",
            bgTop = Color(0xFF333333),
            bgMid = Color(0xFF3F3F3F),
            bgBot = Color(0xFF2B2B2B),
            surface = Color(0xFF494949),
            surfaceAlt = Color(0xFF565656),
            primary = Color(0xFFF0DFAF),
            secondary = Color(0xFF8CD0D3),
            tertiary = Color(0xFF7F9F7F),
            textPrimary = Color(0xFFDCDCCC),
            textSecondary = Color(0xFFB8B8A8)
        ),
        // 17. SynthWave '84
        darkPreset(
            id = "synthwave_84",
            name = "SynthWave '84",
            bgTop = Color(0xFF241B2F),
            bgMid = Color(0xFF262335),
            bgBot = Color(0xFF171324),
            surface = Color(0xFF312B45),
            surfaceAlt = Color(0xFF3E3657),
            primary = Color(0xFFFF7EDB),
            secondary = Color(0xFF36F9F6),
            tertiary = Color(0xFFFEDE5D),
            textPrimary = Color(0xFFFDF8FF),
            textSecondary = Color(0xFFB8B0D4)
        ),
        // 18. Shades of Purple
        darkPreset(
            id = "shades_of_purple",
            name = "Shades of Purple",
            bgTop = Color(0xFF1E1E3F),
            bgMid = Color(0xFF2D2B55),
            bgBot = Color(0xFF191738),
            surface = Color(0xFF373468),
            surfaceAlt = Color(0xFF464282),
            primary = Color(0xFFFAD000),
            secondary = Color(0xFFFF628C),
            tertiary = Color(0xFF9EFFFF),
            textPrimary = Color(0xFFFFFFFF),
            textSecondary = Color(0xFFA599E9)
        ),
        // 19. LaserWave
        darkPreset(
            id = "laserwave",
            name = "LaserWave",
            bgTop = Color(0xFF1F1B2C),
            bgMid = Color(0xFF27212E),
            bgBot = Color(0xFF18141F),
            surface = Color(0xFF342B3E),
            surfaceAlt = Color(0xFF42374F),
            primary = Color(0xFFEB64B9),
            secondary = Color(0xFF40B4C4),
            tertiary = Color(0xFFB381C5),
            textPrimary = Color(0xFFFFFFFF),
            textSecondary = Color(0xFFBFAACC)
        ),
        // 20. Abyss
        darkPreset(
            id = "abyss",
            name = "Abyss",
            bgTop = Color(0xFF000814),
            bgMid = Color(0xFF000C18),
            bgBot = Color(0xFF00050A),
            surface = Color(0xFF05182C),
            surfaceAlt = Color(0xFF0B2542),
            primary = Color(0xFF22AA44),
            secondary = Color(0xFF6688CC),
            tertiary = Color(0xFFDDAE66),
            textPrimary = Color(0xFFDDE6F2),
            textSecondary = Color(0xFF7B93B3)
        ),
        // 21. Cobalt2
        darkPreset(
            id = "cobalt2",
            name = "Cobalt2",
            bgTop = Color(0xFF122738),
            bgMid = Color(0xFF193549),
            bgBot = Color(0xFF0D1E2C),
            surface = Color(0xFF1F4662),
            surfaceAlt = Color(0xFF285778),
            primary = Color(0xFFFFC600),
            secondary = Color(0xFF80FCFF),
            tertiary = Color(0xFF3AD900),
            textPrimary = Color(0xFFFFFFFF),
            textSecondary = Color(0xFF9EBCD4)
        ),
        // 22. Red
        darkPreset(
            id = "red",
            name = "Red",
            bgTop = Color(0xFF2C0000),
            bgMid = Color(0xFF390000),
            bgBot = Color(0xFF1F0000),
            surface = Color(0xFF4A0707),
            surfaceAlt = Color(0xFF5D0E0E),
            primary = Color(0xFFFF6161),
            secondary = Color(0xFFFEC758),
            tertiary = Color(0xFFEC9797),
            textPrimary = Color(0xFFFFF5F5),
            textSecondary = Color(0xFFE0A8A8)
        ),
        // 23. Poimandres
        darkPreset(
            id = "poimandres",
            name = "Poimandres",
            bgTop = Color(0xFF161822),
            bgMid = Color(0xFF1B1E28),
            bgBot = Color(0xFF12141C),
            surface = Color(0xFF252936),
            surfaceAlt = Color(0xFF303546),
            primary = Color(0xFF5DE4C7),
            secondary = Color(0xFFADD7FF),
            tertiary = Color(0xFFF087BD),
            textPrimary = Color(0xFFE4F0FB),
            textSecondary = Color(0xFFA6ACCD)
        ),
        // 24. Hopscotch
        darkPreset(
            id = "hopscotch",
            name = "Hopscotch",
            bgTop = Color(0xFF29252E),
            bgMid = Color(0xFF322931),
            bgBot = Color(0xFF221C22),
            surface = Color(0xFF3F353E),
            surfaceAlt = Color(0xFF4D424C),
            primary = Color(0xFFFD8B19),
            secondary = Color(0xFF1290BF),
            tertiary = Color(0xFF8FC13E),
            textPrimary = Color(0xFFF4F1F4),
            textSecondary = Color(0xFFB9B5B8)
        ),
        // 25. Gruvbox Dark
        darkPreset(
            id = "gruvbox_dark",
            name = "Gruvbox Dark",
            bgTop = Color(0xFF1D2021),
            bgMid = Color(0xFF282828),
            bgBot = Color(0xFF1B1B1B),
            surface = Color(0xFF32302F),
            surfaceAlt = Color(0xFF3C3836),
            primary = Color(0xFFFE8019),
            secondary = Color(0xFFFABD2F),
            tertiary = Color(0xFFB8BB26),
            textPrimary = Color(0xFFFBF1C7),
            textSecondary = Color(0xFFD5C4A1)
        ),
        // 26. Gruvbox Light
        lightPreset(
            id = "gruvbox_light",
            name = "Gruvbox Light",
            bgTop = Color(0xFFFBF1C7),
            bgMid = Color(0xFFF2E5BC),
            bgBot = Color(0xFFEBDBB2),
            surface = Color(0xFFFDF5D7),
            surfaceAlt = Color(0xFFEBDAB2),
            primary = Color(0xFFAF3A03),
            secondary = Color(0xFF076678),
            tertiary = Color(0xFF79740E),
            textPrimary = Color(0xFF282828),
            textSecondary = Color(0xFF504945)
        ),
        // 27. Oceanic Next
        darkPreset(
            id = "oceanic_next",
            name = "Oceanic Next",
            bgTop = Color(0xFF16232B),
            bgMid = Color(0xFF1B2B34),
            bgBot = Color(0xFF121D24),
            surface = Color(0xFF253844),
            surfaceAlt = Color(0xFF343D46),
            primary = Color(0xFF5FB3B3),
            secondary = Color(0xFF6699CC),
            tertiary = Color(0xFFC594C5),
            textPrimary = Color(0xFFD8DEE9),
            textSecondary = Color(0xFFA7ADBA)
        ),
        // 28. Base16 Eighties
        darkPreset(
            id = "base16_eighties",
            name = "Base16 Eighties",
            bgTop = Color(0xFF252525),
            bgMid = Color(0xFF2D2D2D),
            bgBot = Color(0xFF1F1F1F),
            surface = Color(0xFF393939),
            surfaceAlt = Color(0xFF515151),
            primary = Color(0xFF6699CC),
            secondary = Color(0xFFF2777A),
            tertiary = Color(0xFF99CC99),
            textPrimary = Color(0xFFF2F0EC),
            textSecondary = Color(0xFFA09F93)
        ),
        // 29. Tomorrow Night Blue
        darkPreset(
            id = "tomorrow_night_blue",
            name = "Tomorrow Night Blue",
            bgTop = Color(0xFF001C3D),
            bgMid = Color(0xFF002451),
            bgBot = Color(0xFF001633),
            surface = Color(0xFF00346E),
            surfaceAlt = Color(0xFF003F8E),
            primary = Color(0xFFBBDAFF),
            secondary = Color(0xFFFFEEAD),
            tertiary = Color(0xFF99FFFF),
            textPrimary = Color(0xFFFFFFFF),
            textSecondary = Color(0xFFA8C6E8)
        ),
        // 30. Material Theme (Oceanic)
        darkPreset(
            id = "material_oceanic",
            name = "Material Theme (Oceanic)",
            bgTop = Color(0xFF1E292E),
            bgMid = Color(0xFF263238),
            bgBot = Color(0xFF192227),
            surface = Color(0xFF2E3C43),
            surfaceAlt = Color(0xFF37474F),
            primary = Color(0xFF80CBC4),
            secondary = Color(0xFF82AAFF),
            tertiary = Color(0xFFC3E88D),
            textPrimary = Color(0xFFECEFF1),
            textSecondary = Color(0xFFB0BEC5)
        ),
        // 31. Material Theme (Palenight)
        darkPreset(
            id = "material_palenight",
            name = "Material Theme (Palenight)",
            bgTop = Color(0xFF212433),
            bgMid = Color(0xFF292D3E),
            bgBot = Color(0xFF1B1E2B),
            surface = Color(0xFF32374D),
            surfaceAlt = Color(0xFF3E445E),
            primary = Color(0xFF82AAFF),
            secondary = Color(0xFFC792EA),
            tertiary = Color(0xFF89DDFF),
            textPrimary = Color(0xFFEEFFFF),
            textSecondary = Color(0xFFA6ACCD)
        ),
        // 32. Material Theme (Darker)
        darkPreset(
            id = "material_darker",
            name = "Material Theme (Darker)",
            bgTop = Color(0xFF191919),
            bgMid = Color(0xFF212121),
            bgBot = Color(0xFF141414),
            surface = Color(0xFF2B2B2B),
            surfaceAlt = Color(0xFF363636),
            primary = Color(0xFFFF9800),
            secondary = Color(0xFF82AAFF),
            tertiary = Color(0xFFC3E88D),
            textPrimary = Color(0xFFEEFFFF),
            textSecondary = Color(0xFF9E9E9E)
        ),
        // 33. Catppuccin (Mocha)
        darkPreset(
            id = "catppuccin_mocha",
            name = "Catppuccin (Mocha)",
            bgTop = Color(0xFF181825),
            bgMid = Color(0xFF1E1E2E),
            bgBot = Color(0xFF11111B),
            surface = Color(0xFF29293D),
            surfaceAlt = Color(0xFF313244),
            primary = Color(0xFFCBA6F7),
            secondary = Color(0xFF89B4FA),
            tertiary = Color(0xFFA6E3A1),
            textPrimary = Color(0xFFCDD6F4),
            textSecondary = Color(0xFFA6ADC8)
        ),
        // 34. Kanagawa
        darkPreset(
            id = "kanagawa",
            name = "Kanagawa",
            bgTop = Color(0xFF16161D),
            bgMid = Color(0xFF1F1F28),
            bgBot = Color(0xFF121218),
            surface = Color(0xFF2A2A37),
            surfaceAlt = Color(0xFF363646),
            primary = Color(0xFF7E9CD8),
            secondary = Color(0xFF957FB8),
            tertiary = Color(0xFF76946A),
            textPrimary = Color(0xFFDCD7BA),
            textSecondary = Color(0xFFA6A18C)
        ),
        // 35. Rose Pine
        darkPreset(
            id = "rose_pine",
            name = "Rose Pine",
            bgTop = Color(0xFF151320),
            bgMid = Color(0xFF191724),
            bgBot = Color(0xFF110F1A),
            surface = Color(0xFF232033),
            surfaceAlt = Color(0xFF2D2940),
            primary = Color(0xFFEBBCBA),
            secondary = Color(0xFFC4A7E7),
            tertiary = Color(0xFF9CCFD8),
            textPrimary = Color(0xFFE0DEF4),
            textSecondary = Color(0xFF908CAA)
        ),
        // 36. GitHub Dark
        darkPreset(
            id = "github_dark",
            name = "GitHub Dark",
            bgTop = Color(0xFF090C10),
            bgMid = Color(0xFF0D1117),
            bgBot = Color(0xFF06080B),
            surface = Color(0xFF161B22),
            surfaceAlt = Color(0xFF21262D),
            primary = Color(0xFF58A6FF),
            secondary = Color(0xFFBC8CFF),
            tertiary = Color(0xFF3FB950),
            textPrimary = Color(0xFFF0F6FC),
            textSecondary = Color(0xFF8B949E)
        ),
        // 37. GitHub Light
        lightPreset(
            id = "github_light",
            name = "GitHub Light",
            bgTop = Color(0xFFFFFFFF),
            bgMid = Color(0xFFF6F8FA),
            bgBot = Color(0xFFEFF2F5),
            surface = Color(0xFFFFFFFF),
            surfaceAlt = Color(0xFFEAECEF),
            primary = Color(0xFF0969DA),
            secondary = Color(0xFF8250DF),
            tertiary = Color(0xFF1A7F37),
            textPrimary = Color(0xFF1F2328),
            textSecondary = Color(0xFF656D76)
        ),
        // 38. Panda Theme
        darkPreset(
            id = "panda_theme",
            name = "Panda Theme",
            bgTop = Color(0xFF1F2026),
            bgMid = Color(0xFF292A2B),
            bgBot = Color(0xFF1A1B1E),
            surface = Color(0xFF34363A),
            surfaceAlt = Color(0xFF42454A),
            primary = Color(0xFFFF75B5),
            secondary = Color(0xFF19F9D8),
            tertiary = Color(0xFFFFB86C),
            textPrimary = Color(0xFFE6E6E6),
            textSecondary = Color(0xFF9CA3AF)
        ),
        // 39. Atom One Dark
        darkPreset(
            id = "atom_one_dark",
            name = "Atom One Dark",
            bgTop = Color(0xFF21252B),
            bgMid = Color(0xFF282C34),
            bgBot = Color(0xFF1B1E23),
            surface = Color(0xFF2C313A),
            surfaceAlt = Color(0xFF3A404B),
            primary = Color(0xFF528BFF),
            secondary = Color(0xFFC678DD),
            tertiary = Color(0xFF56B6C2),
            textPrimary = Color(0xFFABB2BF),
            textSecondary = Color(0xFF7F848E)
        ),
        // 40. Tokyo Night
        darkPreset(
            id = "tokyo_night",
            name = "Tokyo Night",
            bgTop = Color(0xFF16161E),
            bgMid = Color(0xFF1A1B26),
            bgBot = Color(0xFF101017),
            surface = Color(0xFF24283B),
            surfaceAlt = Color(0xFF2F3549),
            primary = Color(0xFF7AA2F7),
            secondary = Color(0xFFBB9AF7),
            tertiary = Color(0xFF7DCFFF),
            textPrimary = Color(0xFFC0CAF5),
            textSecondary = Color(0xFF8A94B8)
        ),

        // ============ 20 LIQUID & REFRACTIVE GLASS THEMES ============
        // 41. Obsidian Liquid Glass
        liquidGlassDarkPreset(
            id = "obsidian_liquid_glass",
            name = "Obsidian Liquid Glass",
            bgTop = Color(0xFF070709),
            bgMid = Color(0xFF0E1015),
            bgBot = Color(0xFF050608),
            surface = Color(0xFF161922),
            primary = Color(0xFF38BDF8),
            secondary = Color(0xFFA78BFA),
            tertiary = Color(0xFF34D399),
            borderAccent = Color(0xFF38BDF8)
        ),
        // 42. Frosted Arctic Glass
        liquidGlassLightPreset(
            id = "frosted_arctic_glass",
            name = "Frosted Arctic Glass",
            bgTop = Color(0xFFE0F2FE),
            bgMid = Color(0xFFF0F9FF),
            bgBot = Color(0xFFDCEEFB),
            surface = Color(0xFFF8FCFF),
            primary = Color(0xFF0284C7),
            secondary = Color(0xFF06B6D4),
            tertiary = Color(0xFF3B82F6),
            textPrimary = Color(0xFF0C2438),
            textSecondary = Color(0xFF476985)
        ),
        // 43. Cyber Neon Liquid
        liquidGlassDarkPreset(
            id = "cyber_neon_liquid",
            name = "Cyber Neon Liquid",
            bgTop = Color(0xFF070314),
            bgMid = Color(0xFF0D0826),
            bgBot = Color(0xFF05020F),
            surface = Color(0xFF161038),
            primary = Color(0xFF00F5D4),
            secondary = Color(0xFFFF007F),
            tertiary = Color(0xFFFEE440),
            borderAccent = Color(0xFF00F5D4)
        ),
        // 44. Smoked Quartz Glass
        liquidGlassDarkPreset(
            id = "smoked_quartz_glass",
            name = "Smoked Quartz Glass",
            bgTop = Color(0xFF141214),
            bgMid = Color(0xFF1E1B1E),
            bgBot = Color(0xFF0E0C0E),
            surface = Color(0xFF2A262A),
            primary = Color(0xFFE2B887),
            secondary = Color(0xFFA8A29E),
            tertiary = Color(0xFFD4A373),
            borderAccent = Color(0xFFE2B887)
        ),
        // 45. Midnight Velvet Glass
        liquidGlassDarkPreset(
            id = "midnight_velvet_glass",
            name = "Midnight Velvet Glass",
            bgTop = Color(0xFF0B0719),
            bgMid = Color(0xFF140D2E),
            bgBot = Color(0xFF070411),
            surface = Color(0xFF1F1644),
            primary = Color(0xFFC084FC),
            secondary = Color(0xFFF472B6),
            tertiary = Color(0xFF818CF8),
            borderAccent = Color(0xFFC084FC)
        ),
        // 46. Aurora Borealis Glass
        liquidGlassDarkPreset(
            id = "aurora_borealis_glass",
            name = "Aurora Borealis Glass",
            bgTop = Color(0xFF04141A),
            bgMid = Color(0xFF071F28),
            bgBot = Color(0xFF0A1228),
            surface = Color(0xFF0F2C38),
            primary = Color(0xFF2EE59D),
            secondary = Color(0xFF38BDF8),
            tertiary = Color(0xFFA855F7),
            borderAccent = Color(0xFF2EE59D)
        ),
        // 47. Crystal Prism Liquid
        liquidGlassLightPreset(
            id = "crystal_prism_liquid",
            name = "Crystal Prism Liquid",
            bgTop = Color(0xFFF3E8FF),
            bgMid = Color(0xFFF8FAFC),
            bgBot = Color(0xFFE0F7FA),
            surface = Color(0xFFFFFFFF),
            primary = Color(0xFF7C3AED),
            secondary = Color(0xFF06B6D4),
            tertiary = Color(0xFFEC4899),
            textPrimary = Color(0xFF1E1B4B),
            textSecondary = Color(0xFF5B5578)
        ),
        // 48. Deep Ocean Glass
        liquidGlassDarkPreset(
            id = "deep_ocean_glass",
            name = "Deep Ocean Glass",
            bgTop = Color(0xFF02111F),
            bgMid = Color(0xFF041C32),
            bgBot = Color(0xFF020D1A),
            surface = Color(0xFF082947),
            primary = Color(0xFF00D2FF),
            secondary = Color(0xFF3A7BD5),
            tertiary = Color(0xFF2DD4BF),
            borderAccent = Color(0xFF00D2FF)
        ),
        // 49. Amber Mercury Glass
        liquidGlassDarkPreset(
            id = "amber_mercury_glass",
            name = "Amber Mercury Glass",
            bgTop = Color(0xFF140D05),
            bgMid = Color(0xFF1F1508),
            bgBot = Color(0xFF0E0903),
            surface = Color(0xFF2D1F0E),
            primary = Color(0xFFF59E0B),
            secondary = Color(0xFFFBBF24),
            tertiary = Color(0xFFFB923C),
            borderAccent = Color(0xFFF59E0B)
        ),
        // 50. Titanium Mist Glass
        liquidGlassLightPreset(
            id = "titanium_mist_glass",
            name = "Titanium Mist Glass",
            bgTop = Color(0xFFE2E8F0),
            bgMid = Color(0xFFF1F5F9),
            bgBot = Color(0xFFDBE2EA),
            surface = Color(0xFFF8FAFC),
            primary = Color(0xFF334155),
            secondary = Color(0xFF0284C7),
            tertiary = Color(0xFF64748B),
            textPrimary = Color(0xFF0F172A),
            textSecondary = Color(0xFF475569)
        ),
        // 51. Holographic Liquid Glass
        liquidGlassDarkPreset(
            id = "holographic_liquid_glass",
            name = "Holographic Liquid Glass",
            bgTop = Color(0xFF0D0B1A),
            bgMid = Color(0xFF141126),
            bgBot = Color(0xFF091420),
            surface = Color(0xFF1E1A36),
            primary = Color(0xFF67E8F9),
            secondary = Color(0xFFF0ABFC),
            tertiary = Color(0xFF6EE7B7),
            borderAccent = Color(0xFFF0ABFC)
        ),
        // 52. Carbon Blur Glass
        liquidGlassDarkPreset(
            id = "carbon_blur_glass",
            name = "Carbon Blur Glass",
            bgTop = Color(0xFF0B0C0E),
            bgMid = Color(0xFF121417),
            bgBot = Color(0xFF08090B),
            surface = Color(0xFF1C1F24),
            primary = Color(0xFF60A5FA),
            secondary = Color(0xFF94A3B8),
            tertiary = Color(0xFF22D3EE),
            borderAccent = Color(0xFF60A5FA)
        ),
        // 53. Emerald Translucent Glass
        liquidGlassDarkPreset(
            id = "emerald_translucent_glass",
            name = "Emerald Translucent Glass",
            bgTop = Color(0xFF03140E),
            bgMid = Color(0xFF062018),
            bgBot = Color(0xFF020E0A),
            surface = Color(0xFF0B3024),
            primary = Color(0xFF10B981),
            secondary = Color(0xFF34D399),
            tertiary = Color(0xFF2DD4BF),
            borderAccent = Color(0xFF10B981)
        ),
        // 54. Ultraviolet Frost Glass
        liquidGlassDarkPreset(
            id = "ultraviolet_frost_glass",
            name = "Ultraviolet Frost Glass",
            bgTop = Color(0xFF100720),
            bgMid = Color(0xFF180C2E),
            bgBot = Color(0xFF0B0417),
            surface = Color(0xFF241342),
            primary = Color(0xFFA855F7),
            secondary = Color(0xFFD946EF),
            tertiary = Color(0xFF6366F1),
            borderAccent = Color(0xFFA855F7)
        ),
        // 55. Sapphire Liquid Glass
        liquidGlassDarkPreset(
            id = "sapphire_liquid_glass",
            name = "Sapphire Liquid Glass",
            bgTop = Color(0xFF050E24),
            bgMid = Color(0xFF0A183B),
            bgBot = Color(0xFF040919),
            surface = Color(0xFF102454),
            primary = Color(0xFF3B82F6),
            secondary = Color(0xFF38BDF8),
            tertiary = Color(0xFF818CF8),
            borderAccent = Color(0xFF3B82F6)
        ),
        // 56. Pearlescent Dark Glass
        liquidGlassDarkPreset(
            id = "pearlescent_dark_glass",
            name = "Pearlescent Dark Glass",
            bgTop = Color(0xFF12131A),
            bgMid = Color(0xFF191B24),
            bgBot = Color(0xFF0E1016),
            surface = Color(0xFF242733),
            primary = Color(0xFFE2E8F0),
            secondary = Color(0xFFF5D0FE),
            tertiary = Color(0xFFBAE6FD),
            borderAccent = Color(0xFFE2E8F0)
        ),
        // 57. Ruby Refractive Glass
        liquidGlassDarkPreset(
            id = "ruby_refractive_glass",
            name = "Ruby Refractive Glass",
            bgTop = Color(0xFF19060B),
            bgMid = Color(0xFF260912),
            bgBot = Color(0xFF120408),
            surface = Color(0xFF380F1B),
            primary = Color(0xFFFB7185),
            secondary = Color(0xFFF43F5E),
            tertiary = Color(0xFFFBBF24),
            borderAccent = Color(0xFFFB7185)
        ),
        // 58. Chromatic Aberration Glass
        liquidGlassDarkPreset(
            id = "chromatic_aberration_glass",
            name = "Chromatic Aberration Glass",
            bgTop = Color(0xFF0A0812),
            bgMid = Color(0xFF110E1E),
            bgBot = Color(0xFF080D14),
            surface = Color(0xFF1B172E),
            primary = Color(0xFF00E5FF),
            secondary = Color(0xFFFF2A85),
            tertiary = Color(0xFFA3E635),
            borderAccent = Color(0xFFFF2A85)
        ),
        // 59. Liquid Mercury Dark
        liquidGlassDarkPreset(
            id = "liquid_mercury_dark",
            name = "Liquid Mercury Dark",
            bgTop = Color(0xFF0E1116),
            bgMid = Color(0xFF161B22),
            bgBot = Color(0xFF090B0E),
            surface = Color(0xFF21262D),
            primary = Color(0xFFCBD5E1),
            secondary = Color(0xFF60A5FA),
            tertiary = Color(0xFF94A3B8),
            borderAccent = Color(0xFFCBD5E1)
        ),
        // 60. Bioluminescent Glass
        liquidGlassDarkPreset(
            id = "bioluminescent_glass",
            name = "Bioluminescent Glass",
            bgTop = Color(0xFF020D14),
            bgMid = Color(0xFF041724),
            bgBot = Color(0xFF020A12),
            surface = Color(0xFF082436),
            primary = Color(0xFF00FFC6),
            secondary = Color(0xFF00B4D8),
            tertiary = Color(0xFF72EFDD),
            borderAccent = Color(0xFF00FFC6)
        )
    )

    private val themeById: Map<String, AppThemePreset> = allThemes.associateBy { it.id }

    fun findById(id: String?): AppThemePreset? {
        if (id.isNullOrBlank()) return null
        return themeById[id]
    }

    private fun darkPreset(
        id: String,
        name: String,
        bgTop: Color,
        bgMid: Color,
        bgBot: Color,
        surface: Color,
        surfaceAlt: Color,
        primary: Color,
        secondary: Color,
        tertiary: Color,
        textPrimary: Color,
        textSecondary: Color
    ) = AppThemePreset(
        id = id,
        name = name,
        category = ThemeCategory.CLASSIC_DARK,
        isDark = true,
        isLiquidGlass = false,
        backgroundTop = bgTop,
        backgroundMid = bgMid,
        backgroundBottom = bgBot,
        surfacePrimary = surface,
        surfaceSecondary = bgMid,
        surfaceVariant = surfaceAlt,
        cardTintTop = surface.copy(alpha = 0.92f),
        cardTintBottom = bgMid.copy(alpha = 0.88f),
        primary = primary,
        secondary = secondary,
        tertiary = tertiary,
        textPrimary = textPrimary,
        textSecondary = textSecondary,
        borderHighlight = Color.White.copy(alpha = 0.18f),
        borderAccent = primary.copy(alpha = 0.45f),
        orb1 = primary.copy(alpha = 0.22f),
        orb2 = secondary.copy(alpha = 0.18f),
        orb3 = tertiary.copy(alpha = 0.14f)
    )

    private fun lightPreset(
        id: String,
        name: String,
        bgTop: Color,
        bgMid: Color,
        bgBot: Color,
        surface: Color,
        surfaceAlt: Color,
        primary: Color,
        secondary: Color,
        tertiary: Color,
        textPrimary: Color,
        textSecondary: Color
    ) = AppThemePreset(
        id = id,
        name = name,
        category = ThemeCategory.CLASSIC_LIGHT,
        isDark = false,
        isLiquidGlass = false,
        backgroundTop = bgTop,
        backgroundMid = bgMid,
        backgroundBottom = bgBot,
        surfacePrimary = surface,
        surfaceSecondary = bgMid,
        surfaceVariant = surfaceAlt,
        cardTintTop = surface.copy(alpha = 0.95f),
        cardTintBottom = bgTop.copy(alpha = 0.90f),
        primary = primary,
        secondary = secondary,
        tertiary = tertiary,
        textPrimary = textPrimary,
        textSecondary = textSecondary,
        borderHighlight = Color.White.copy(alpha = 0.92f),
        borderAccent = primary.copy(alpha = 0.28f),
        orb1 = primary.copy(alpha = 0.18f),
        orb2 = secondary.copy(alpha = 0.15f),
        orb3 = tertiary.copy(alpha = 0.12f)
    )

    private fun liquidGlassDarkPreset(
        id: String,
        name: String,
        bgTop: Color,
        bgMid: Color,
        bgBot: Color,
        surface: Color,
        primary: Color,
        secondary: Color,
        tertiary: Color,
        borderAccent: Color
    ) = AppThemePreset(
        id = id,
        name = name,
        category = ThemeCategory.LIQUID_GLASS,
        isDark = true,
        isLiquidGlass = true,
        backgroundTop = bgTop,
        backgroundMid = bgMid,
        backgroundBottom = bgBot,
        surfacePrimary = surface,
        surfaceSecondary = bgMid,
        surfaceVariant = surface.copy(alpha = 0.95f),
        cardTintTop = surface.copy(alpha = 0.80f),
        cardTintBottom = bgMid.copy(alpha = 0.72f),
        primary = primary,
        secondary = secondary,
        tertiary = tertiary,
        textPrimary = Color(0xFFF8FAFC),
        textSecondary = Color(0xFFB6C2D6),
        borderHighlight = Color.White.copy(alpha = 0.34f),
        borderAccent = borderAccent.copy(alpha = 0.65f),
        orb1 = primary.copy(alpha = 0.32f),
        orb2 = secondary.copy(alpha = 0.26f),
        orb3 = tertiary.copy(alpha = 0.20f)
    )

    private fun liquidGlassLightPreset(
        id: String,
        name: String,
        bgTop: Color,
        bgMid: Color,
        bgBot: Color,
        surface: Color,
        primary: Color,
        secondary: Color,
        tertiary: Color,
        textPrimary: Color,
        textSecondary: Color
    ) = AppThemePreset(
        id = id,
        name = name,
        category = ThemeCategory.LIQUID_GLASS,
        isDark = false,
        isLiquidGlass = true,
        backgroundTop = bgTop,
        backgroundMid = bgMid,
        backgroundBottom = bgBot,
        surfacePrimary = surface,
        surfaceSecondary = bgMid,
        surfaceVariant = bgBot,
        cardTintTop = surface.copy(alpha = 0.84f),
        cardTintBottom = bgMid.copy(alpha = 0.74f),
        primary = primary,
        secondary = secondary,
        tertiary = tertiary,
        textPrimary = textPrimary,
        textSecondary = textSecondary,
        borderHighlight = Color.White.copy(alpha = 0.98f),
        borderAccent = primary.copy(alpha = 0.42f),
        orb1 = primary.copy(alpha = 0.25f),
        orb2 = secondary.copy(alpha = 0.22f),
        orb3 = tertiary.copy(alpha = 0.18f)
    )
}
