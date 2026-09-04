package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.R

@Composable
fun getBrandIcon(title: String, website: String): Painter? {
    val searchStr = "$title $website".lowercase()
    return when {
        searchStr.contains("google") || searchStr.contains("gmail") -> painterResource(id = R.drawable.ic_brand_google)
        searchStr.contains("youtube") -> painterResource(id = R.drawable.ic_brand_youtube)
        searchStr.contains("apple") || searchStr.contains("icloud") || searchStr.contains("mac") -> painterResource(id = R.drawable.ic_brand_apple)
        searchStr.contains("facebook") || searchStr.contains("meta") -> painterResource(id = R.drawable.ic_brand_facebook)
        searchStr.contains("twitter") || searchStr.contains(" x ") || searchStr.contains("x.com") -> painterResource(id = R.drawable.ic_brand_x)
        searchStr.contains("telegram") -> painterResource(id = R.drawable.ic_brand_telegram)
        searchStr.contains("github") -> painterResource(id = R.drawable.ic_brand_github)
        searchStr.contains("instagram") || searchStr.contains("insta") -> painterResource(id = R.drawable.ic_brand_instagram)
        searchStr.contains("tiktok") -> painterResource(id = R.drawable.ic_brand_tiktok)
        searchStr.contains("linkedin") -> painterResource(id = R.drawable.ic_brand_linkedin)
        else -> null
    }
}
