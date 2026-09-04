package com.example.data

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object SecurityCrypto {
    private const val PBKDF2_ITERATIONS = 100_000
    private const val KEY_LENGTH_BITS = 256
    private const val GCM_TAG_LENGTH_BITS = 128
    private const val GCM_IV_LENGTH_BYTES = 12
    private const val SALT_LENGTH_BYTES = 16

    private val random = SecureRandom()

    fun generateSalt(): String {
        val salt = ByteArray(SALT_LENGTH_BYTES)
        random.nextBytes(salt)
        return Base64.encodeToString(salt, Base64.NO_WRAP)
    }

    fun deriveKey(masterPassword: String, saltBase64: String): SecretKeySpec {
        val salt = Base64.decode(saltBase64, Base64.NO_WRAP)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(masterPassword.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH_BITS)
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, "AES")
    }

    fun encrypt(plainText: String, secretKey: SecretKeySpec): String {
        if (plainText.isEmpty()) return ""
        val iv = ByteArray(GCM_IV_LENGTH_BYTES)
        random.nextBytes(iv)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)

        val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        val combined = ByteArray(iv.size + encryptedBytes.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)

        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    fun decrypt(cipherTextBase64: String, secretKey: SecretKeySpec): String {
        if (cipherTextBase64.isEmpty()) return ""
        return try {
            val combined = Base64.decode(cipherTextBase64, Base64.NO_WRAP)
            if (combined.size <= GCM_IV_LENGTH_BYTES) return ""

            val iv = ByteArray(GCM_IV_LENGTH_BYTES)
            val encryptedBytes = ByteArray(combined.size - GCM_IV_LENGTH_BYTES)
            System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH_BYTES)
            System.arraycopy(combined, GCM_IV_LENGTH_BYTES, encryptedBytes, 0, encryptedBytes.size)

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

            val decryptedBytes = cipher.doFinal(encryptedBytes)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            ""
        }
    }

    fun generatePassword(
        length: Int = 16,
        includeUpper: Boolean = true,
        includeLower: Boolean = true,
        includeNumbers: Boolean = true,
        includeSymbols: Boolean = true,
        excludeAmbiguous: Boolean = true
    ): String {
        val uppercase = "ABCDEFGHJKLMNPQRSTUVWXYZ" + if (excludeAmbiguous) "" else "IO"
        val lowercase = "abcdefghijkmnopqrstuvwxyz" + if (excludeAmbiguous) "" else "l"
        val numbers = "23456789" + if (excludeAmbiguous) "" else "01"
        val symbols = "!@#$%^&*()_+-=[]{}|;:,.<>?"

        var pool = ""
        val required = mutableListOf<Char>()

        if (includeUpper) {
            pool += uppercase
            required.add(uppercase[random.nextInt(uppercase.length)])
        }
        if (includeLower) {
            pool += lowercase
            required.add(lowercase[random.nextInt(lowercase.length)])
        }
        if (includeNumbers) {
            pool += numbers
            required.add(numbers[random.nextInt(numbers.length)])
        }
        if (includeSymbols) {
            pool += symbols
            required.add(symbols[random.nextInt(symbols.length)])
        }

        if (pool.isEmpty()) pool = lowercase + numbers

        val sb = StringBuilder()
        sb.append(required.joinToString(""))

        while (sb.length < length) {
            sb.append(pool[random.nextInt(pool.length)])
        }

        val chars = sb.toString().toCharArray()
        for (i in chars.indices) {
            val j = random.nextInt(chars.size)
            val temp = chars[i]
            chars[i] = chars[j]
            chars[j] = temp
        }

        return String(chars)
    }

    fun calculateStrengthScore(password: String): Int {
        if (password.isEmpty()) return 0
        var score = 0
        if (password.length >= 8) score += 20
        if (password.length >= 12) score += 20
        if (password.length >= 16) score += 10
        if (password.any { it.isUpperCase() }) score += 15
        if (password.any { it.isLowerCase() }) score += 15
        if (password.any { it.isDigit() }) score += 10
        if (password.any { !it.isLetterOrDigit() }) score += 10
        return score.coerceAtMost(100)
    }
}
