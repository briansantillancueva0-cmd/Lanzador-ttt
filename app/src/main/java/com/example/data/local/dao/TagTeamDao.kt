package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.CloudSyncEntity
import com.example.data.local.entity.DlcExpansionEntity
import com.example.data.local.entity.IsoGameEntity
import com.example.data.local.entity.SaveGameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagTeamDao {

    // ISO Games
    @Query("SELECT * FROM iso_games ORDER BY lastMountedTime DESC")
    fun getAllIsoGames(): Flow<List<IsoGameEntity>>

    @Query("SELECT * FROM iso_games WHERE isMounted = 1 LIMIT 1")
    fun getMountedIsoGame(): Flow<IsoGameEntity?>

    @Query("SELECT * FROM iso_games WHERE isMounted = 1 LIMIT 1")
    suspend fun getMountedIsoGameSync(): IsoGameEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateIso(iso: IsoGameEntity)

    @Update
    suspend fun updateIso(iso: IsoGameEntity)

    @Query("UPDATE iso_games SET isMounted = 0")
    suspend fun unmountAllIsos()

    @Query("UPDATE iso_games SET hasPck1Injected = :isInjected WHERE gameId = :gameId")
    suspend fun setPck1Injected(gameId: String, isInjected: Boolean)

    // DLC Expansions
    @Query("SELECT * FROM dlc_expansions ORDER BY priority ASC, installDate DESC")
    fun getAllDlcs(): Flow<List<DlcExpansionEntity>>

    @Query("SELECT * FROM dlc_expansions WHERE isEnabled = 1")
    fun getActiveDlcs(): Flow<List<DlcExpansionEntity>>

    @Query("SELECT * FROM dlc_expansions WHERE isCorePck1 = 1 LIMIT 1")
    fun getCorePck1Dlc(): Flow<DlcExpansionEntity?>

    @Query("SELECT * FROM dlc_expansions WHERE isCorePck1 = 1 LIMIT 1")
    suspend fun getCorePck1DlcSync(): DlcExpansionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDlc(dlc: DlcExpansionEntity)

    @Update
    suspend fun updateDlc(dlc: DlcExpansionEntity)

    @Delete
    suspend fun deleteDlc(dlc: DlcExpansionEntity)

    @Query("DELETE FROM dlc_expansions WHERE id = :id")
    suspend fun deleteDlcById(id: String)

    @Query("UPDATE dlc_expansions SET isEnabled = :enabled WHERE id = :id")
    suspend fun toggleDlcEnabled(id: String, enabled: Boolean)

    // Savegames
    @Query("SELECT * FROM save_games ORDER BY slotIndex ASC")
    fun getAllSaves(): Flow<List<SaveGameEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSave(save: SaveGameEntity)

    @Update
    suspend fun updateSave(save: SaveGameEntity)

    @Delete
    suspend fun deleteSave(save: SaveGameEntity)

    @Query("DELETE FROM save_games WHERE id = :id")
    suspend fun deleteSaveById(id: String)

    @Query("UPDATE save_games SET isCloudSynced = 1, cloudSyncTimestamp = :timestamp")
    suspend fun markAllSavesSynced(timestamp: Long)

    // Cloud Sync Records
    @Query("SELECT * FROM cloud_sync_records ORDER BY timestamp DESC LIMIT 20")
    fun getSyncHistory(): Flow<List<CloudSyncEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncRecord(record: CloudSyncEntity)
}
