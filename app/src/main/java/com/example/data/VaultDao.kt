package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultDao {
    @Query("SELECT * FROM vault_items ORDER BY updatedAt DESC")
    fun getAllVaultItems(): Flow<List<VaultEntity>>

    @Query("SELECT * FROM vault_items ORDER BY updatedAt DESC")
    suspend fun getAllVaultItemsSync(): List<VaultEntity>

    @Query("SELECT * FROM vault_items WHERE id = :id")
    suspend fun getVaultItemById(id: String): VaultEntity?

    @Query("SELECT * FROM vault_items WHERE isFavorite = 1 ORDER BY title ASC")
    fun getFavoriteVaultItems(): Flow<List<VaultEntity>>

    @Query("SELECT * FROM vault_items ORDER BY lastUsedAt DESC LIMIT 10")
    fun getRecentlyUsedItems(): Flow<List<VaultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaultItem(item: VaultEntity)

    @Update
    suspend fun updateVaultItem(item: VaultEntity)

    @Query("DELETE FROM vault_items WHERE id = :id")
    suspend fun deleteVaultItemById(id: String)

    @Query("DELETE FROM vault_items")
    suspend fun deleteAllVaultItems()
}
