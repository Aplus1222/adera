package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.DecryptedVaultItem
import com.example.model.VaultItemType
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExportHelper {
    fun generateAndSharePdf(
        context: Context,
        items: List<DecryptedVaultItem>,
        selectedTypes: Set<VaultItemType>,
        includePasswords: Boolean
    ): Boolean {
        return try {
            val pdfDocument = PdfDocument()
            val dateFormat = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
            val exportDate = dateFormat.format(Date())
            
            val filteredItems = items.filter { selectedTypes.contains(it.itemType) }.sortedBy { it.title }
            val groupedItems = filteredItems.groupBy { it.itemType }
            
            val pageWidth = 595f // A4 width
            val pageHeight = 842f // A4 height
            val margin = 40f
            
            var pageNumber = 1
            var pageInfo = PdfDocument.PageInfo.Builder(pageWidth.toInt(), pageHeight.toInt(), pageNumber).create()
            var page = pdfDocument.startPage(pageInfo)
            var canvas = page.canvas
            
            // Premium Ocean & Obsidian Theme Paints
            val primaryColor = Color.parseColor("#2563EB") // Royal Blue
            val accentColor = Color.parseColor("#0EA5E9") // Ocean/Cyan
            val bannerTextColor = Color.WHITE
            
            val headerBgPaint = Paint().apply { color = primaryColor }
            val headerTitlePaint = Paint().apply {
                color = bannerTextColor
                textSize = 24f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            val headerSubPaint = Paint().apply {
                color = Color.parseColor("#E0E7FF") // Indigo 100
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }
            val sectionBgPaint = Paint().apply { color = Color.parseColor("#F1F5F9") } // Slate 100
            val sectionTitlePaint = Paint().apply {
                color = primaryColor
                textSize = 14f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                letterSpacing = 0.05f
            }
            val itemTitlePaint = Paint().apply {
                color = Color.parseColor("#0F172A") // Slate 900
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            val labelPaint = Paint().apply {
                color = Color.parseColor("#64748B") // Slate 500
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            val valuePaint = Paint().apply {
                color = Color.parseColor("#0F172A")
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }
            val valueMonoPaint = Paint().apply {
                color = primaryColor
                textSize = 10f
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                isAntiAlias = true
            }
            val borderPaint = Paint().apply {
                color = Color.parseColor("#CBD5E1") // Slate 300
                strokeWidth = 0.5f
                style = Paint.Style.STROKE
            }
            val zebraPaint = Paint().apply { color = Color.parseColor("#F8FAFC") } // Slate 50
            val watermarkPaint = Paint().apply {
                color = Color.parseColor("#F1F5F9")
                textSize = 64f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
                alpha = 60
                textAlign = Paint.Align.CENTER
            }
            
            var y = 0f
            
            fun drawWatermark() {
                canvas.save()
                canvas.translate(pageWidth / 2, pageHeight / 2)
                canvas.rotate(-45f)
                val wmText = if (includePasswords) "CONFIDENTIAL & UNENCRYPTED" else "PASSWORDS REDACTED"
                canvas.drawText(wmText, 0f, 0f, watermarkPaint)
                canvas.restore()
            }
            
            fun drawHeader() {
                // Background Watermark
                drawWatermark()

                // Top Corporate Banner
                val bannerHeight = 110f
                canvas.drawRect(0f, 0f, pageWidth, bannerHeight, headerBgPaint)
                
                // Left side
                canvas.drawText("ADERA VAULT", margin, 45f, headerTitlePaint)
                canvas.drawText("OFFICIAL SECURE EXPORT STATEMENT", margin, 70f, headerSubPaint)
                canvas.drawText("CONFIDENTIAL & PRIVATE", margin, 85f, headerSubPaint)
                
                // Right side
                val alignRight = Paint(headerSubPaint).apply { textAlign = Paint.Align.RIGHT }
                canvas.drawText("STATEMENT DATE:", pageWidth - margin, 45f, alignRight)
                val dateValPaint = Paint(headerTitlePaint).apply { textSize = 14f; textAlign = Paint.Align.RIGHT }
                canvas.drawText(exportDate, pageWidth - margin, 65f, dateValPaint)
                
                val mode = if (includePasswords) "UNENCRYPTED SECRETS INCLUDED" else "PASSWORDS REDACTED"
                val warningColor = if (includePasswords) Paint(alignRight).apply { color = Color.parseColor("#FCA5A5"); isFakeBoldText = true } else alignRight
                canvas.drawText("MODE: $mode", pageWidth - margin, 85f, warningColor)
                
                y = bannerHeight + 40f
            }
            
            fun drawFooter(pageNum: Int) {
                val footerY = pageHeight - 30f
                val footerPaint = Paint(labelPaint).apply { textAlign = Paint.Align.CENTER }
                canvas.drawLine(margin, footerY - 15f, pageWidth - margin, footerY - 15f, borderPaint)
                canvas.drawText("Page $pageNum", pageWidth / 2, footerY, footerPaint)
                
                val rightFooter = Paint(labelPaint).apply { textAlign = Paint.Align.RIGHT }
                canvas.drawText("Generated by Adera Vault", pageWidth - margin, footerY, rightFooter)
            }
            
            fun checkPageBreak(requiredSpace: Float) {
                if (y + requiredSpace > pageHeight - 60f) { // Leave room for footer
                    drawFooter(pageNumber)
                    pdfDocument.finishPage(page)
                    pageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth.toInt(), pageHeight.toInt(), pageNumber).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    drawHeader()
                }
            }
            
            drawHeader()
            
            // Draw Account Summary Table
            checkPageBreak(120f)
            canvas.drawText("VAULT OVERVIEW", margin, y, sectionTitlePaint)
            y += 8f
            canvas.drawLine(margin, y, pageWidth - margin, y, borderPaint)
            y += 24f
            
            var summaryX = margin
            var summaryCount = 0
            groupedItems.forEach { (type, items) ->
                val typeName = type.name.replace("_", " ")
                canvas.drawText("$typeName", summaryX, y, labelPaint)
                canvas.drawText("${items.size}", summaryX, y + 16f, valuePaint)
                summaryX += 130f
                summaryCount++
                if (summaryCount % 4 == 0) {
                    summaryX = margin
                    y += 45f
                }
            }
            if (summaryCount % 4 != 0) y += 45f
            
            // Draw Detailed Sections
            groupedItems.forEach { (type, typeItems) ->
                checkPageBreak(100f)
                
                val typeName = type.name.replace("_", " ")
                
                // Section Header (Bank Statement Category Block)
                y += 15f
                canvas.drawRoundRect(RectF(margin, y, pageWidth - margin, y + 26f), 4f, 4f, sectionBgPaint)
                canvas.drawText(typeName, margin + 12f, y + 18f, sectionTitlePaint)
                y += 40f
                
                var isZebra = false
                typeItems.forEach { item ->
                    var fields = mutableListOf<Pair<String, String>>()
                    when (type) {
                        VaultItemType.LOGIN -> {
                            if (item.username.isNotEmpty()) fields.add("Username" to item.username)
                            if (item.email.isNotEmpty()) fields.add("Email" to item.email)
                            fields.add("Password" to (if (includePasswords) item.password else "••••••••"))
                            if (item.website.isNotEmpty()) fields.add("Website" to item.website)
                        }
                        VaultItemType.CREDIT_CARD -> {
                            if (item.username.isNotEmpty()) fields.add("Cardholder" to item.username)
                            fields.add("Card Number" to (if (includePasswords) item.password else "••••••••••••••••"))
                            if (item.email.isNotEmpty()) fields.add("Expiry" to item.email)
                            if (item.website.isNotEmpty()) fields.add("CVV" to (if (includePasswords) item.website else "•••"))
                        }
                        VaultItemType.CRYPTO_WALLET -> {
                            if (item.email.isNotEmpty()) fields.add("Network" to item.email)
                            if (item.username.isNotEmpty()) fields.add("Address" to item.username)
                            if (item.password.isNotEmpty()) fields.add("Seed Phrase" to (if (includePasswords) item.password else "••••••••"))
                            if (item.website.isNotEmpty()) fields.add("Private Key" to (if (includePasswords) item.website else "••••••••"))
                        }
                        VaultItemType.API_KEY -> {
                            if (item.website.isNotEmpty()) fields.add("Service" to item.website)
                            fields.add("API Key" to (if (includePasswords) item.password else "••••••••"))
                        }
                        VaultItemType.IDENTITY -> {
                            if (item.username.isNotEmpty()) fields.add("Phone" to item.username)
                            if (item.email.isNotEmpty()) fields.add("Email" to item.email)
                            if (item.website.isNotEmpty()) fields.add("Address" to item.website)
                        }
                        VaultItemType.WIFI -> {
                            fields.add("Password" to (if (includePasswords) item.password else "••••••••"))
                            if (item.username.isNotEmpty()) fields.add("Security" to item.username)
                        }
                        VaultItemType.EMAIL -> {
                            if (item.email.isNotEmpty()) fields.add("Email Address" to item.email)
                            fields.add("Password" to (if (includePasswords) item.password else "••••••••"))
                            if (item.username.isNotEmpty()) fields.add("Recovery" to item.username)
                            if (item.website.isNotEmpty()) fields.add("Provider" to item.website)
                        }
                        VaultItemType.SECURE_NOTE -> {
                            // Handled separately below
                        }
                        else -> {
                            if (item.username.isNotEmpty()) fields.add("Identifier" to item.username)
                            fields.add("Secret" to (if (includePasswords) item.password else "••••••••"))
                        }
                    }
                    if (item.notes.isNotEmpty()) {
                        fields.add("Notes" to item.notes.replace("\n", " "))
                    }
                    
                    val itemHeight = 40f + (fields.size * 18f)
                    checkPageBreak(itemHeight + 20f)
                    
                    // Draw Zebra bg for readability
                    if (isZebra) {
                        canvas.drawRoundRect(RectF(margin, y - 10f, pageWidth - margin, y + itemHeight - 10f), 6f, 6f, zebraPaint)
                    }
                    
                    // Item Title
                    canvas.drawText(item.title.ifEmpty { "Untitled" }, margin + 8f, y + 8f, itemTitlePaint)
                    y += 28f
                    
                    // Draw Fields
                    fields.forEach { (lbl, v) ->
                        canvas.drawText(lbl, margin + 12f, y, labelPaint)
                        // truncate value if too long (approx 70 chars max for layout)
                        val maxChars = 75
                        val displayVal = if (v.length > maxChars) v.take(maxChars) + "..." else v
                        val isSensitive = lbl.contains("Password", true) || lbl.contains("Seed", true) || lbl.contains("Key", true) || lbl.contains("Number", true) || lbl.contains("Secret", true)
                        canvas.drawText(displayVal, margin + 120f, y, if(isSensitive) valueMonoPaint else valuePaint)
                        y += 18f
                    }
                    
                    canvas.drawLine(margin, y, pageWidth - margin, y, borderPaint)
                    y += 14f
                    
                    isZebra = !isZebra
                }
            }
            
            // Final page footer
            drawFooter(pageNumber)
            pdfDocument.finishPage(page)
            
            // Save PDF
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()
            val pdfFile = File(exportDir, "ADERA_Statement_${System.currentTimeMillis()}.pdf")
            val outputStream = FileOutputStream(pdfFile)
            pdfDocument.writeTo(outputStream)
            outputStream.close()
            pdfDocument.close()
            
            val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", pdfFile)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, "ADERA Vault Statement")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(shareIntent, "Save or Share Statement")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
            
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
