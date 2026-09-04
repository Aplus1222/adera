package com.example.crypto

import java.security.MessageDigest
import java.security.SecureRandom
import kotlin.experimental.and

object Bip39Mnemonic {

    private val secureRandom = SecureRandom()

    /**
     * Generates a 12-word BIP-39 mnemonic from 128 bits of entropy.
     */
    fun generate12WordMnemonic(wordList: List<String>): List<String> {
        require(wordList.size == 2048) { "Wordlist must contain exactly 2048 words" }
        
        // 1. Generate 16 bytes (128 bits) of entropy
        val entropy = ByteArray(16)
        secureRandom.nextBytes(entropy)
        
        return entropyToMnemonic(entropy, wordList)
    }

    /**
     * Converts entropy to a mnemonic phrase.
     */
    fun entropyToMnemonic(entropy: ByteArray, wordList: List<String>): List<String> {
        // 2. Compute SHA-256 hash of the entropy
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(entropy)
        
        // 3. Extract checksum (for 128 bit entropy, it's the first 4 bits of the hash)
        val checksumBits = (hash[0].toInt() and 0xFF) ushr 4
        
        // 4 & 5. Combine entropy and checksum into 11-bit chunks
        val bitString = buildString {
            for (byte in entropy) {
                val byteValue = byte.toInt() and 0xFF
                append(byteValue.toString(2).padStart(8, '0'))
            }
            append(checksumBits.toString(2).padStart(4, '0'))
        }
        
        // 6 & 7. Split into 11-bit chunks and map to words
        val words = mutableListOf<String>()
        for (i in 0 until 12) {
            val chunk = bitString.substring(i * 11, (i + 1) * 11)
            val index = chunk.toInt(2)
            words.add(wordList[index])
        }
        
        return words
    }

    /**
     * Validates a given mnemonic phrase.
     */
    fun validateMnemonic(phrase: List<String>, wordList: List<String>): Boolean {
        if (phrase.size != 12) return false
        
        val bitString = buildString {
            for (word in phrase) {
                val index = wordList.indexOf(word)
                if (index == -1) return false // Word not in wordlist
                append(index.toString(2).padStart(11, '0'))
            }
        }
        
        val entropyBits = bitString.substring(0, 128)
        val checksumBits = bitString.substring(128, 132)
        
        val entropy = ByteArray(16)
        for (i in 0 until 16) {
            val byteString = entropyBits.substring(i * 8, (i + 1) * 8)
            entropy[i] = byteString.toInt(2).toByte()
        }
        
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(entropy)
        
        val expectedChecksum = (hash[0].toInt() and 0xFF) ushr 4
        val expectedChecksumBits = expectedChecksum.toString(2).padStart(4, '0')
        
        return checksumBits == expectedChecksumBits
    }
}
