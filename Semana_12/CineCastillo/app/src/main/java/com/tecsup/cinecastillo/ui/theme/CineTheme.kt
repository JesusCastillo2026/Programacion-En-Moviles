package com.tecsup.cinecastillo.ui.theme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val cinema = darkColorScheme(
 primary=Color(0xFFE83B4F),onPrimary=Color.White,primaryContainer=Color(0xFF64202C),onPrimaryContainer=Color(0xFFFFD9DD),
 secondary=Color(0xFFFFC878),onSecondary=Color(0xFF2A140F),secondaryContainer=Color(0xFF543926),
 background=Color(0xFF0D1018),onBackground=Color(0xFFF5F2F2),surface=Color(0xFF181D28),onSurface=Color(0xFFF5F2F2),
 surfaceVariant=Color(0xFF252B38),onSurfaceVariant=Color(0xFFB9C0CC),surfaceContainer=Color(0xFF1C2230),surfaceContainerLow=Color(0xFF151A24),surfaceContainerHigh=Color(0xFF293141),
 outline=Color(0xFF6B7481),error=Color(0xFFFF7584)
)
private val cinemaType=Typography(
 headlineLarge=TextStyle(fontSize=28.sp,lineHeight=34.sp,fontWeight=FontWeight.Bold),
 headlineMedium=TextStyle(fontSize=24.sp,lineHeight=30.sp,fontWeight=FontWeight.Bold),
 headlineSmall=TextStyle(fontSize=20.sp,lineHeight=26.sp,fontWeight=FontWeight.Bold),
 titleLarge=TextStyle(fontSize=20.sp,lineHeight=26.sp,fontWeight=FontWeight.SemiBold),
 titleMedium=TextStyle(fontSize=17.sp,lineHeight=23.sp,fontWeight=FontWeight.SemiBold),
 bodyLarge=TextStyle(fontSize=16.sp,lineHeight=24.sp),
 bodyMedium=TextStyle(fontSize=14.sp,lineHeight=21.sp),
 bodySmall=TextStyle(fontSize=13.sp,lineHeight=19.sp),
 labelLarge=TextStyle(fontSize=14.sp,lineHeight=20.sp,fontWeight=FontWeight.SemiBold),
 labelSmall=TextStyle(fontSize=12.sp,lineHeight=17.sp,fontWeight=FontWeight.Medium)
)
@Composable fun CineTheme(content:@Composable ()->Unit){
 MaterialTheme(colorScheme=cinema,typography=cinemaType,shapes=Shapes(small=RoundedCornerShape(12.dp),medium=RoundedCornerShape(18.dp),large=RoundedCornerShape(26.dp)),content=content)
}
