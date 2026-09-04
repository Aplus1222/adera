package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.runtime.getValue
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AderaDanger
import com.example.ui.theme.AderaPrimary
import com.example.ui.theme.AderaSecondary
import com.example.ui.theme.AderaSuccess
import com.example.ui.theme.AderaWarning

@Composable
fun OnboardingSecurityIllustration(
    modifier: Modifier = Modifier.size(260.dp)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "security")
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = -8f, targetValue = 8f,
        animationSpec = infiniteRepeatable(tween(2500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "shieldFloat"
    )
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(12000, easing = LinearEasing), RepeatMode.Restart),
        label = "ringRot"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f, targetValue = 0.25f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "auraPulse"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Background subtle glowing aura
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(AderaPrimary.copy(alpha = pulseAlpha), Color.Transparent),
                center = Offset(w / 2, h / 2),
                radius = w / 1.5f
            )
        )

        // Outer dotted tech ring (Rotating)
        rotate(degrees = ringRotation, pivot = Offset(w / 2, h / 2)) {
            drawCircle(
                color = AderaSecondary.copy(alpha = 0.3f),
                center = Offset(w / 2, h / 2),
                radius = w * 0.38f,
                style = Stroke(
                    width = 4f,
                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(12f, 12f))
                )
            )
        }

        // Floating Shield & Lock
        translate(top = floatAnim) {
            // Beautiful Gradient Shield
            val shieldPath = Path().apply {
                moveTo(w * 0.5f, h * 0.22f)
                cubicTo(w * 0.7f, h * 0.25f, w * 0.78f, h * 0.28f, w * 0.78f, h * 0.45f)
                cubicTo(w * 0.78f, h * 0.65f, w * 0.62f, h * 0.82f, w * 0.5f, h * 0.88f)
                cubicTo(w * 0.38f, h * 0.82f, w * 0.22f, h * 0.65f, w * 0.22f, h * 0.45f)
                cubicTo(w * 0.22f, h * 0.28f, w * 0.3f, h * 0.25f, w * 0.5f, h * 0.22f)
                close()
            }

            drawPath(
                path = shieldPath,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF34D399), Color(0xFF059669)),
                    start = Offset(w * 0.3f, h * 0.2f),
                    end = Offset(w * 0.7f, h * 0.8f)
                )
            )
            
            // Inner glowing shield layer
            val innerShieldPath = Path().apply {
                moveTo(w * 0.5f, h * 0.28f)
                cubicTo(w * 0.65f, h * 0.31f, w * 0.71f, h * 0.33f, w * 0.71f, h * 0.46f)
                cubicTo(w * 0.71f, h * 0.61f, w * 0.58f, h * 0.74f, w * 0.5f, h * 0.79f)
                cubicTo(w * 0.42f, h * 0.74f, w * 0.29f, h * 0.61f, w * 0.29f, h * 0.46f)
                cubicTo(w * 0.29f, h * 0.33f, w * 0.35f, h * 0.31f, w * 0.5f, h * 0.28f)
                close()
            }
            drawPath(
                path = innerShieldPath,
                color = Color.White.copy(alpha = 0.15f)
            )

            // Lock keyhole inside shield
            drawCircle(
                color = Color.White,
                center = Offset(w * 0.5f, h * 0.48f),
                radius = 16f
            )
            val keyholePath = Path().apply {
                moveTo(w * 0.46f, h * 0.48f)
                lineTo(w * 0.54f, h * 0.48f)
                lineTo(w * 0.56f, h * 0.62f)
                lineTo(w * 0.44f, h * 0.62f)
                close()
            }
            drawPath(keyholePath, color = Color.White, style = androidx.compose.ui.graphics.drawscope.Fill)
        }
    }
}

@Composable
fun OnboardingVaultIllustration(
    modifier: Modifier = Modifier.size(260.dp)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "vault")
    val vaultFloat by infiniteTransition.animateFloat(
        initialValue = -5f, targetValue = 5f,
        animationSpec = infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "vaultFloat"
    )
    val cardFloat by infiniteTransition.animateFloat(
        initialValue = 6f, targetValue = -6f,
        animationSpec = infiniteRepeatable(tween(2500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "cardFloat"
    )
    val dialRotation by infiniteTransition.animateFloat(
        initialValue = -15f, targetValue = 15f,
        animationSpec = infiniteRepeatable(tween(4000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "dialRot"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f, targetValue = 0.2f,
        animationSpec = infiniteRepeatable(tween(2500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "auraPulse"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Background subtle glowing aura
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(AderaSecondary.copy(alpha = pulseAlpha), Color.Transparent),
                center = Offset(w / 2, h / 2),
                radius = w / 1.5f
            )
        )

        // Floating Vault
        translate(top = vaultFloat) {
            // Digital vault/safe structure
            val vaultW = w * 0.55f
            val vaultH = h * 0.55f
            val vaultLeft = (w - vaultW) / 2
            val vaultTop = (h - vaultH) / 2

            // Vault Body Drop Shadow
            drawRoundRect(
                color = Color.Black.copy(alpha = 0.1f),
                topLeft = Offset(vaultLeft + 10f, vaultTop + 15f),
                size = Size(vaultW, vaultH),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(32f, 32f)
            )

            // Vault Body
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF34D399), Color(0xFF059669)),
                    start = Offset(vaultLeft, vaultTop),
                    end = Offset(vaultLeft + vaultW, vaultTop + vaultH)
                ),
                topLeft = Offset(vaultLeft, vaultTop),
                size = Size(vaultW, vaultH),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(32f, 32f)
            )

            // Vault Door Inner
            drawRoundRect(
                color = Color.White.copy(alpha = 0.15f),
                topLeft = Offset(vaultLeft + 16f, vaultTop + 16f),
                size = Size(vaultW - 32f, vaultH - 32f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f, 20f)
            )

            // Combination dial wheel (Rotating)
            rotate(degrees = dialRotation, pivot = Offset(w * 0.5f, vaultTop + vaultH * 0.5f)) {
                drawCircle(
                    color = Color.White,
                    center = Offset(w * 0.5f, vaultTop + vaultH * 0.5f),
                    radius = 36f
                )
                drawCircle(
                    color = Color(0xFF059669),
                    center = Offset(w * 0.5f, vaultTop + vaultH * 0.5f),
                    radius = 18f
                )
                drawCircle(
                    color = Color.White,
                    center = Offset(w * 0.5f, vaultTop + vaultH * 0.5f - 24f),
                    radius = 6f
                )
            }
        }

        // Floating Data Cards/Nodes
        translate(top = cardFloat) {
            drawRoundRect(
                brush = Brush.linearGradient(listOf(Color(0xFF34D399), Color(0xFF059669))),
                topLeft = Offset(w * 0.12f, h * 0.25f),
                size = Size(64f, 48f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
            )
            drawRoundRect(
                brush = Brush.linearGradient(listOf(Color(0xFFFBBF24), Color(0xFFD97706))),
                topLeft = Offset(w * 0.68f, h * 0.18f),
                size = Size(72f, 52f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
            )
        }
    }
}

@Composable
fun OnboardingGeneratorIllustration(
    modifier: Modifier = Modifier.size(260.dp)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "generator")
    val cardFloat by infiniteTransition.animateFloat(
        initialValue = 6f, targetValue = -6f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "cardFloat"
    )
    val sparkleScale by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "sparkleScale"
    )
    val sparkleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "sparkleAlpha"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Background subtle glowing aura
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(AderaSuccess.copy(alpha = 0.15f), Color.Transparent),
                center = Offset(w / 2, h / 2),
                radius = w / 1.5f
            )
        )

        // Sparkle / Star shapes around (Animated Scale & Alpha)
        val drawSparkle = { cx: Float, cy: Float, size: Float ->
            scale(scale = sparkleScale, pivot = Offset(cx, cy)) {
                val p = Path().apply {
                    moveTo(cx, cy - size)
                    quadraticBezierTo(cx, cy, cx + size, cy)
                    quadraticBezierTo(cx, cy, cx, cy + size)
                    quadraticBezierTo(cx, cy, cx - size, cy)
                    quadraticBezierTo(cx, cy, cx, cy - size)
                    close()
                }
                drawPath(p, color = Color(0xFF34D399).copy(alpha = sparkleAlpha))
            }
        }
        drawSparkle(w * 0.2f, h * 0.3f, 16f)
        drawSparkle(w * 0.8f, h * 0.25f, 20f)
        drawSparkle(w * 0.75f, h * 0.75f, 12f)

        // Central floating password card
        translate(top = cardFloat) {
            val cardW = w * 0.6f
            val cardH = h * 0.35f
            val cardLeft = (w - cardW) / 2
            val cardTop = (h - cardH) / 2

            // Shadow
            drawRoundRect(
                color = Color.Black.copy(alpha = 0.08f),
                topLeft = Offset(cardLeft + 8f, cardTop + 12f),
                size = Size(cardW, cardH),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(24f, 24f)
            )

            // Card Body
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF10B981), Color(0xFF047857))
                ),
                topLeft = Offset(cardLeft, cardTop),
                size = Size(cardW, cardH),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(24f, 24f)
            )

            // Password asterisks and key
            drawCircle(
                color = Color.White,
                center = Offset(w * 0.35f, h * 0.5f),
                radius = 6f
            )
            drawCircle(
                color = Color.White,
                center = Offset(w * 0.45f, h * 0.5f),
                radius = 6f
            )
            drawCircle(
                color = Color.White,
                center = Offset(w * 0.55f, h * 0.5f),
                radius = 6f
            )
            
            // Key Graphic
            drawCircle(
                color = Color.White,
                center = Offset(w * 0.7f, h * 0.5f),
                radius = 12f,
                style = Stroke(width = 4f)
            )
            drawLine(
                color = Color.White,
                start = Offset(w * 0.7f, h * 0.5f + 12f),
                end = Offset(w * 0.7f, h * 0.5f + 32f),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.White,
                start = Offset(w * 0.7f, h * 0.5f + 20f),
                end = Offset(w * 0.7f + 8f, h * 0.5f + 20f),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.White,
                start = Offset(w * 0.7f, h * 0.5f + 28f),
                end = Offset(w * 0.7f + 8f, h * 0.5f + 28f),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun SecurityScoreGauge(
    score: Int,
    safeCount: Int,
    weakCount: Int,
    reusedCount: Int,
    compromisedCount: Int,
    modifier: Modifier = Modifier.size(180.dp)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 24f
        val radius = (w - strokeWidth) / 2

        val total = (safeCount + weakCount + reusedCount + compromisedCount).coerceAtLeast(1)

        val safeAngle = (safeCount.toFloat() / total) * 360f
        val weakAngle = (weakCount.toFloat() / total) * 360f
        val reusedAngle = (reusedCount.toFloat() / total) * 360f
        val compAngle = (compromisedCount.toFloat() / total) * 360f

        if (total == 1 && safeCount == 0 && weakCount == 0 && reusedCount == 0 && compromisedCount == 0) {
            drawArc(
                color = Color.Gray.copy(alpha = 0.2f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = Size(radius * 2, radius * 2)
            )
            return@Canvas
        }
        var startAngle = -90f

        // Safe Arc
        if (safeAngle > 0) {
            drawArc(
                color = AderaSuccess,
                startAngle = startAngle,
                sweepAngle = safeAngle - 4f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = Size(radius * 2, radius * 2)
            )
            startAngle += safeAngle
        }

        // Weak Arc
        if (weakAngle > 0) {
            drawArc(
                color = AderaWarning,
                startAngle = startAngle,
                sweepAngle = weakAngle - 4f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = Size(radius * 2, radius * 2)
            )
            startAngle += weakAngle
        }

        // Reused Arc
        if (reusedAngle > 0) {
            drawArc(
                color = AderaPrimary,
                startAngle = startAngle,
                sweepAngle = reusedAngle - 4f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = Size(radius * 2, radius * 2)
            )
            startAngle += reusedAngle
        }

        // Compromised Arc
        if (compAngle > 0) {
            drawArc(
                color = AderaDanger,
                startAngle = startAngle,
                sweepAngle = compAngle - 4f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = Size(radius * 2, radius * 2)
            )
        }
    }
}
