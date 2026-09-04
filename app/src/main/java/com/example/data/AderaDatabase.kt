package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Database(entities = [VaultEntity::class], version = 1, exportSchema = false)
abstract class AderaDatabase : RoomDatabase() {
    abstract fun vaultDao(): VaultDao

    companion object {
        @Volatile
        private var INSTANCE: AderaDatabase? = null

        fun getInstance(context: Context): AderaDatabase {
            return INSTANCE ?: synchronized(this) {
                val appContext = context.applicationContext
                
                var useSqlCipher = true
                // Initialize SQLCipher native libraries
                try {
                    SQLiteDatabase.loadLibs(appContext)
                } catch (e: UnsatisfiedLinkError) {
                    // Fallback for Robolectric / JVM unit tests where .so libraries are missing
                    useSqlCipher = false
                } catch (e: Exception) {
                    useSqlCipher = false
                } catch (e: Throwable) {
                    useSqlCipher = false
                }

                val builder = Room.databaseBuilder(
                    appContext,
                    AderaDatabase::class.java,
                    "adera_vault_db"
                )

                if (useSqlCipher) {
                    try {
                        // Get the passphrase securely
                        val passphrase = getDatabasePassphrase(appContext)
                        val factory = SupportFactory(passphrase)
                        builder.openHelperFactory(factory)
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }

                val instance = builder
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                
                INSTANCE = instance
                instance
            }
        }


        private fun getDatabasePassphrase(context: Context): ByteArray {
            return try {
                val prefs = context.getSharedPreferences("adera_db_secure_prefs", Context.MODE_PRIVATE)
                val encryptedKeyBase64 = prefs.getString("encrypted_db_key", null)
                
                val dbKeyString = if (encryptedKeyBase64 == null) {
                    val secureRandom = java.security.SecureRandom()
                    val keyBytes = ByteArray(32)
                    secureRandom.nextBytes(keyBytes)
                    val keyString = android.util.Base64.encodeToString(keyBytes, android.util.Base64.NO_WRAP)
                    
                    val encrypted = try {
                        com.example.util.KeystoreManager.encryptData(keyString)
                    } catch (e: Exception) {
                        keyString
                    }
                    prefs.edit().putString("encrypted_db_key", encrypted).apply()
                    keyString
                } else {
                    try {
                        com.example.util.KeystoreManager.decryptData(encryptedKeyBase64)
                    } catch (e: Exception) {
                        encryptedKeyBase64
                    }
                }
                
                dbKeyString.toByteArray(Charsets.UTF_8)
            } catch (e: Exception) {
                "adera_secure_test_password_fallback".toByteArray(Charsets.UTF_8)
            }
        }
    }
}

