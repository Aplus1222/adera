#!/bin/bash
sed -i '/fun getAllVaultItems(): Flow<List<DecryptedVaultItem>>/i \
    suspend fun getAllDecryptedLoginsSync(): List<DecryptedVaultItem> {\n        if (!isVaultUnlocked()) return emptyList()\n        val entities = vaultDao.getAllVaultItemsSync()\n        return entities.mapNotNull { decryptItem(it) }\n            .filter { it.itemType == com.example.model.VaultItemType.LOGIN }\n    }\n' app/src/main/java/com/example/data/VaultRepository.kt
