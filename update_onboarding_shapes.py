import re

with open("app/src/main/java/com/example/ui/screens/OnboardingScreen.kt", "r") as f:
    content = f.read()

# Add a floating shapes layer
background_layer_code = """        Box(modifier = Modifier.fillMaxSize().background(backgroundBrush)) {
            // Floating background shapes
            val floatAnim1 by infiniteTransition.animateFloat(
                initialValue = 0f, targetValue = 20f,
                animationSpec = infiniteRepeatable(animation = tween(4000, easing = LinearEasing), repeatMode = androidx.compose.animation.core.RepeatMode.Reverse),
                label = "float1"
            )
            val floatAnim2 by infiniteTransition.animateFloat(
                initialValue = 20f, targetValue = -10f,
                animationSpec = infiniteRepeatable(animation = tween(5500, easing = LinearEasing), repeatMode = androidx.compose.animation.core.RepeatMode.Reverse),
                label = "float2"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = 0.6f }
            ) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .graphicsLayer {
                            translationX = -50f + floatAnim1
                            translationY = 100f + floatAnim2
                        }
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(AderaPrimary.copy(alpha = 0.2f), Color.Transparent)
                            )
                        )
                )
                
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(250.dp)
                        .graphicsLayer {
                            translationX = 80f - floatAnim2
                            translationY = -60f + floatAnim1
                        }
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(AderaSecondary.copy(alpha = 0.15f), Color.Transparent)
                            )
                        )
                )
            }
            
            Column("""

content = content.replace("        Box(modifier = Modifier.fillMaxSize().background(backgroundBrush)) {\n            Column(", background_layer_code)

with open("app/src/main/java/com/example/ui/screens/OnboardingScreen.kt", "w") as f:
    f.write(content)
