import re

with open("app/src/main/java/com/example/ui/screens/OnboardingScreen.kt", "r") as f:
    content = f.read()

# Add imports for animation and graphics
new_imports = """import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.graphicsLayer
import com.example.ui.theme.AderaSecondary
import androidx.compose.ui.geometry.Offset
"""

# replace import section
content = content.replace("import androidx.compose.animation.fadeIn", new_imports + "\nimport androidx.compose.animation.fadeIn")

# Replace Scaffold to add animated background
scaffold_code = r"(    Scaffold\([\s\S]*?\n    \) \{ innerPadding ->)"

new_scaffold_code = """    // Animated Mesh Gradient Background
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing)
        ),
        label = "gradientOffset"
    )

    val backgroundBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            AderaPrimary.copy(alpha = 0.08f),
            AderaSecondary.copy(alpha = 0.05f),
            MaterialTheme.colorScheme.background
        ),
        start = Offset(gradientOffset, gradientOffset),
        end = Offset(gradientOffset + 1000f, gradientOffset + 1000f)
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ADERA Header with New Brand Logo
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_adera_logo),
                        contentDescription = "ADERA Logo",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "ADERA • አደራ",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = AderaPrimary,
                            letterSpacing = 1.sp
                        )
                    )
                }
                // Language Selector
                Box {
                    OutlinedButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            languageMenuExpanded = true
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Language, contentDescription = "Language", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (currentLanguage == AderaLanguage.ENGLISH) "EN" else "አማ")
                    }
                    DropdownMenu(
                        expanded = languageMenuExpanded,
                        onDismissRequest = { languageMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("English") },
                            onClick = {
                                onLanguageSelected(AderaLanguage.ENGLISH)
                                languageMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("አማርኛ (Amharic)") },
                            onClick = {
                                onLanguageSelected(AderaLanguage.AMHARIC)
                                languageMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().background(backgroundBrush)) {
            Column("""

content = re.sub(scaffold_code, new_scaffold_code, content)
content = content.replace("    ) { innerPadding ->\n        Column(", new_scaffold_code)

# Add closing brace for Box at the end of the file.
content = re.sub(r"        \}\n    \}\n\}\n?$", "        }\n    }\n}\n}\n", content)

with open("app/src/main/java/com/example/ui/screens/OnboardingScreen.kt", "w") as f:
    f.write(content)
