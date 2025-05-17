package software.ehsan.movieshowcase.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import software.ehsan.movieshowcase.R

private val PoppinsFontFamily = FontFamily(
    Font(R.font.poppins_regular, weight = FontWeight.Normal),
    Font(R.font.poppins_bold, weight = FontWeight.Bold)
)
val MovieShowCaseTypography = Typography().run {
    copy(
        displayLarge = displayLarge.copy(fontFamily = PoppinsFontFamily),
        displayMedium = displayMedium.copy(fontFamily = PoppinsFontFamily),
        displaySmall = displaySmall.copy(fontFamily = PoppinsFontFamily),
        headlineLarge = headlineLarge.copy(
            fontFamily = PoppinsFontFamily,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        ),
        headlineMedium = headlineMedium.copy(fontFamily = PoppinsFontFamily),
        headlineSmall = headlineSmall.copy(fontFamily = PoppinsFontFamily),
        titleLarge = titleLarge.copy(
            fontFamily = PoppinsFontFamily,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        ),
        titleMedium = titleMedium.copy(fontFamily = PoppinsFontFamily),
        titleSmall = titleSmall.copy(fontFamily = PoppinsFontFamily),
        bodyLarge = bodyLarge.copy(fontFamily = PoppinsFontFamily),
        bodySmall = bodySmall.copy(fontFamily = PoppinsFontFamily)
    )
}


