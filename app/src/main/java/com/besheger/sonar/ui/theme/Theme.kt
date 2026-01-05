import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SonarCyan,
    onPrimary = Color.Black,
    secondary = DarkGreyBlue,
    background = DeepCharcoal,
    surface = DeepCharcoal,
    onSurface = TextWhite,
    onSurfaceVariant = TextSecondary
)

@Composable
fun SonarAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        // You can also add custom Typography.kt and Shapes.kt here later
        content = content
    )
}