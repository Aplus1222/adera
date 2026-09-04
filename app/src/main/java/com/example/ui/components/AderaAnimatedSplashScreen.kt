package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AderaAnimatedSplashScreen(onAnimationFinished: () -> Unit) {
    val rotationY = remember { Animatable(0f) }
    val rotationX = remember { Animatable(0f) }
    val scale = remember { Animatable(0.3f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Fade in
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600, easing = LinearOutSlowInEasing)
            )
        }
        
        // Scale up with a slight bounce effect
        launch {
            scale.animateTo(
                targetValue = 1.3f,
                animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
            )
        }
        
        // 3D Flip animation on Y axis
        launch {
            rotationY.animateTo(
                targetValue = 720f, // 2 full rotations
                animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
            )
        }
        
        // Slight tilt on X axis for extra 3D feel
        launch {
            rotationX.animateTo(
                targetValue = 20f,
                animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
            )
            rotationX.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
            )
        }
        
        // Wait for all animations to finish
        delay(1600)
        
        // Fade out
        alpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
        )
        
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_adera_logo),
                contentDescription = "Adera Vault Logo",
                modifier = Modifier
                    .size(160.dp)
                    .graphicsLayer {
                        this.rotationY = rotationY.value
                        this.rotationX = rotationX.value
                        this.scaleX = scale.value
                        this.scaleY = scale.value
                        this.alpha = alpha.value
                        this.cameraDistance = 8f * density
                    }
            )
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(24.dp))
            
            androidx.compose.material3.Text(
                text = "Adera Vault",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    letterSpacing = 4.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.graphicsLayer {
                    this.alpha = alpha.value
                    this.scaleX = 0.5f + (scale.value * 0.5f)
                    this.scaleY = 0.5f + (scale.value * 0.5f)
                }
            )
        }
    }
}
