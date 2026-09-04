package com.example.autofill

import android.app.PendingIntent
import android.content.Intent
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.Dataset
import android.service.autofill.FillCallback
import android.service.autofill.FillContext
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.SaveCallback
import android.service.autofill.SaveRequest
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.AderaDatabase
import com.example.data.VaultRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import android.app.assist.AssistStructure
import androidx.annotation.RequiresApi
import android.os.Build

@RequiresApi(Build.VERSION_CODES.O)
class AderaAutofillService : AutofillService() {

    private lateinit var vaultRepository: VaultRepository

    override fun onCreate() {
        super.onCreate()
        val dao = AderaDatabase.getInstance(applicationContext).vaultDao()
        vaultRepository = VaultRepository(applicationContext, dao)
    }

    @Suppress("DEPRECATION")
    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {
        val structure = request.fillContexts.last().structure
        
        // Find the package name of the app being filled
        val targetPackageName = structure.activityComponent?.packageName ?: ""

        // Parse structure to find username and password fields
        val parsedStructure = parseStructure(structure)
        
        if (parsedStructure.usernameId == null && parsedStructure.passwordId == null) {
            callback.onSuccess(null) // No relevant fields found
            return
        }

        // Check if vault is unlocked
        if (!vaultRepository.isVaultUnlocked()) {
            val unlockIntent = Intent(this, MainActivity::class.java).apply {
                putExtra("from_autofill", true)
            }
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                unlockIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
            )
            
            val presentation = RemoteViews(packageName, R.layout.autofill_unlock_presentation)
            
            val ids = mutableListOf<AutofillId>()
            if (parsedStructure.usernameId != null) ids.add(parsedStructure.usernameId!!)
            if (parsedStructure.passwordId != null) ids.add(parsedStructure.passwordId!!)
            
            val response = FillResponse.Builder()
                .setAuthentication(
                    ids.toTypedArray(),
                    pendingIntent.intentSender,
                    presentation
                )
                .build()
                
            callback.onSuccess(response)
            return
        }

        // Vault is unlocked. Search for matching credentials.
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val allItems = vaultRepository.getAllVaultItems().first()
                val domain = parsedStructure.webDomain ?: ""
                val matchingItems = allItems.filter { item ->
                    item.itemType == com.example.model.VaultItemType.LOGIN && (
                        (targetPackageName.isNotEmpty() && !targetPackageName.contains("chrome") && !targetPackageName.contains("browser") && !targetPackageName.contains("firefox") && (
                            item.website.contains(targetPackageName, ignoreCase = true) || 
                            targetPackageName.contains(item.website, ignoreCase = true) ||
                            item.title.contains(targetPackageName, ignoreCase = true)
                        )) ||
                        (domain.isNotEmpty() && (
                            item.website.contains(domain, ignoreCase = true) ||
                            domain.contains(item.website, ignoreCase = true) ||
                            item.title.contains(domain, ignoreCase = true)
                        ))
                    )
                }.take(5) // Limit to 5 suggestions

                if (matchingItems.isEmpty()) {
                    callback.onSuccess(null)
                    return@launch
                }

                val responseBuilder = FillResponse.Builder()
                
                for (item in matchingItems) {
                    val datasetBuilder = Dataset.Builder()
                    
                    val presentation = RemoteViews(packageName, R.layout.autofill_presentation)
                    presentation.setTextViewText(R.id.autofill_title, item.title)
                    val subtitle = if (item.username.isNotEmpty()) item.username else item.email
                    presentation.setTextViewText(R.id.autofill_subtitle, subtitle)
                    
                    if (parsedStructure.usernameId != null) {
                        val usernameToFill = if (item.username.isNotEmpty()) item.username else item.email
                        datasetBuilder.setValue(
                            parsedStructure.usernameId!!,
                            AutofillValue.forText(usernameToFill),
                            presentation
                        )
                    }
                    if (parsedStructure.passwordId != null) {
                        datasetBuilder.setValue(
                            parsedStructure.passwordId!!,
                            AutofillValue.forText(item.password),
                            presentation
                        )
                    }
                    
                    responseBuilder.addDataset(datasetBuilder.build())
                }
                
                callback.onSuccess(responseBuilder.build())
                
            } catch (e: Exception) {
                callback.onFailure(e.message)
            }
        }
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        callback.onSuccess()
    }
    
    private data class ParsedStructure(
        var usernameId: AutofillId? = null,
        var passwordId: AutofillId? = null,
        var webDomain: String? = null
    )

    private fun parseStructure(structure: AssistStructure): ParsedStructure {
        val parsed = ParsedStructure()
        val nodesToProcess = mutableListOf<AssistStructure.ViewNode>()
        val numWindowNodes = structure.windowNodeCount

        for (i in 0 until numWindowNodes) {
            nodesToProcess.add(structure.getWindowNodeAt(i).rootViewNode)
        }
        
        while (nodesToProcess.isNotEmpty()) {
            val node = nodesToProcess.removeAt(0)
            
            val hints = node.autofillHints
            var isUsername = false
            var isPassword = false
            
            if (hints != null) {
                if (hints.contains(android.view.View.AUTOFILL_HINT_USERNAME) || hints.contains(android.view.View.AUTOFILL_HINT_EMAIL_ADDRESS)) {
                    isUsername = true
                }
                if (hints.contains(android.view.View.AUTOFILL_HINT_PASSWORD)) {
                    isPassword = true
                }
            } else {
                // Heuristic fallback
                val className = node.className ?: ""
                if (className.contains("EditText", ignoreCase = true)) {
                    val idEntry = node.idEntry ?: ""
                    val hint = node.hint ?: ""
                    val text = node.text?.toString() ?: ""
                    if (idEntry.contains("username", ignoreCase = true) || idEntry.contains("email", ignoreCase = true) || hint.contains("username", ignoreCase = true) || hint.contains("email", ignoreCase = true) || text.contains("email", ignoreCase = true) || text.contains("username", ignoreCase = true)) {
                        isUsername = true
                    }
                    if (idEntry.contains("password", ignoreCase = true) || hint.contains("password", ignoreCase = true) || text.contains("password", ignoreCase = true)) {
                        isPassword = true
                    }
                }
            }
            
            if (isUsername && parsed.usernameId == null && node.autofillId != null) {
                parsed.usernameId = node.autofillId
            }
            if (isPassword && parsed.passwordId == null && node.autofillId != null) {
                parsed.passwordId = node.autofillId
            }
            val domain = node.webDomain
            if (domain != null && parsed.webDomain == null) {
                parsed.webDomain = domain
            }
            
            for (i in 0 until node.childCount) {
                nodesToProcess.add(node.getChildAt(i))
            }
        }

        return parsed
    }
}
