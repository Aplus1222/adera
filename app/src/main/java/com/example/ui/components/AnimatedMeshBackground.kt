package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AderaPrimary
import com.example.ui.theme.AderaSecondary
import com.example.ui.theme.AderaTertiary

@Composable
fun AnimatedMeshBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing)
        ),
        label = "gradientOffset"
    )

    val backgroundBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            AderaPrimary.copy(alpha = 0.08f),
            AderaTertiary.copy(alpha = 0.04f),
            AderaSecondary.copy(alpha = 0.06f),
            MaterialTheme.colorScheme.background
        ),
        start = Offset(gradientOffset, gradientOffset),
        end = Offset(gradientOffset + 1000f, gradientOffset + 1000f)
    )

    Box(modifier = modifier.fillMaxSize().background(backgroundBrush)) {
        // Floating background shapes for depth
        val floatAnim1 by infiniteTransition.animateFloat(
            initialValue = 0f, targetValue = 60f,
            animationSpec = infiniteRepeatable(animation = tween(9000, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
            label = "float1"
        )
        
        val floatAnim2 by infiniteTransition.animateFloat(
            initialValue = 60f, targetValue = -30f,
            animationSpec = infiniteRepeatable(animation = tween(11000, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
            label = "float2"
        )
        
        val floatAnim3 by infiniteTransition.animateFloat(
            initialValue = -30f, targetValue = 40f,
            animationSpec = infiniteRepeatable(animation = tween(13000, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
            label = "float3"
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = 1.0f }
                .blur(40.dp) // Extreme blur for the soft aurora effect
        ) {
            // Neon Violet Orb
            Box(
                modifier = Modifier
                    .size(350.dp)
                    .graphicsLayer {
                        translationX = -120f + floatAnim1
                        translationY = 100f + floatAnim2
                    }
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(AderaPrimary.copy(alpha = 0.35f), Color.Transparent)
                        )
                    )
            )
            
            // Electric Cyan Orb
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(450.dp)
                    .graphicsLayer {
                        translationX = 80f - floatAnim2
                        translationY = 80f + floatAnim1
                    }
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(AderaSecondary.copy(alpha = 0.25f), Color.Transparent)
                        )
                    )
            )
            
            // Radiant Rose Orb
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(300.dp)
                    .graphicsLayer {
                        translationX = 150f - floatAnim3
                        translationY = -150f + floatAnim2
                    }
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(AderaTertiary.copy(alpha = 0.20f), Color.Transparent)
                        )
                    )
            )
        }
    }
}
