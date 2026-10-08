package com.example.repository

import android.content.Context
import com.example.data.local.TagTeamDatabase
import com.example.data.local.entity.CloudSyncEntity
import com.example.data.local.entity.DlcExpansionEntity
import com.example.data.local.entity.IsoGameEntity
import com.example.data.local.entity.SaveGameEntity
import com.example.data.model.TagCharacter
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class TagTeamRepository(context: Context) {

    private val db = TagTeamDatabase.getDatabase(context)
    private val dao = db.tagTeamDao()

    val allIsos: Flow<List<IsoGameEntity>> = dao.getAllIsoGames()
    val mountedIso: Flow<IsoGameEntity?> = dao.getMountedIsoGame()
    val allDlcs: Flow<List<DlcExpansionEntity>> = dao.getAllDlcs()
    val corePck1: Flow<DlcExpansionEntity?> = dao.getCorePck1Dlc()
    val allSaves: Flow<List<SaveGameEntity>> = dao.getAllSaves()
    val syncHistory: Flow<List<CloudSyncEntity>> = dao.getSyncHistory()

    suspend fun getMountedIsoSync(): IsoGameEntity? = dao.getMountedIsoGameSync()

    suspend fun initializeDefaultDataIfEmpty() {
        // 1. Initial Sample ISO if no ISO is loaded
        val currentIso = dao.getMountedIsoGameSync()
        if (currentIso == null) {
            val sampleIso = IsoGameEntity(
                gameId = "ULUS-10537",
                title = "Dragon Ball Z: Tenkaichi Tag Team",
                fileName = "DBZ_Tenkaichi_Tag_Team_USA.iso",
                fileUri = "content://virtual/iso/ULUS-10537.iso",
                fileSizeBytes = 1_254_211_584L, // 1.17 GB
                region = "USA / NTSC-U",
                discStructure = "ISO 9660 / UDF 1.02 (PSP_GAME)",
                crc32 = "A94C82E1",
                sha1 = "72B84DC80A18F95F3B1A2C3D4E5F6A7B8C9D0E1F",
                status = "VERIFIED_READY",
                isMounted = true,
                lastMountedTime = System.currentTimeMillis(),
                hasPck1Injected = true,
                activePatchLanguage = "Español Latino (Voces HD)"
            )
            dao.insertOrUpdateIso(sampleIso)
        }

        // 2. Pre-populate default DLCs if empty
        val pck1 = dao.getCorePck1DlcSync()
        if (pck1 == null) {
            val defaultDlcs = listOf(
                DlcExpansionEntity(
                    id = "DLC_PCK1_CORE",
                    gameId = "ULUS-10537",
                    name = "Pck1 Personajes Extendidos (Roster Pack 1)",
                    category = "ROSTER",
                    author = "Spike & Team Tag Modders",
                    version = "v2.5.0",
                    description = "Inyecta personajes exclusivos SSJ4, fusiones y formas definitivas en el menú de selección de personajes.",
                    sizeBytes = 185_420_000L,
                    isEnabled = true,
                    isCorePck1 = true,
                    priority = 1,
                    installDate = System.currentTimeMillis() - 86400000L,
                    filePath = "virtual/dlc/pck1.bin",
                    itemsIncludedCount = 12
                ),
                DlcExpansionEntity(
                    id = "DLC_LATINO_DUB",
                    gameId = "ULUS-10537",
                    name = "Doblaje Latino Oficial HD Remaster",
                    category = "AUDIO_DUB",
                    author = "Comunidad Dragon Ball Hispano",
                    version = "v3.1",
                    description = "Sustituye voces de batalla por doblaje al español latino con Mario Castañeda y René García.",
                    sizeBytes = 142_800_000L,
                    isEnabled = true,
                    isCorePck1 = false,
                    priority = 2,
                    installDate = System.currentTimeMillis() - 172800000L,
                    filePath = "virtual/dlc/audio_latino.ttpack",
                    itemsIncludedCount = 74
                ),
                DlcExpansionEntity(
                    id = "DLC_HD_TEXTURES",
                    gameId = "ULUS-10537",
                    name = "Pack de Texturas 4K & Cel-Shading Overhaul",
                    category = "TEXTURES",
                    author = "HD Remaster Studio",
                    version = "v1.8",
                    description = "Modelos con contornos nítidos Cel-Shading, auras en alta definición y escenarios rediseñados.",
                    sizeBytes = 320_500_000L,
                    isEnabled = true,
                    isCorePck1 = false,
                    priority = 3,
                    installDate = System.currentTimeMillis() - 259200000L,
                    filePath = "virtual/dlc/textures_hd.ttpack",
                    itemsIncludedCount = 420
                ),
                DlcExpansionEntity(
                    id = "DLC_STORY_AF",
                    gameId = "ULUS-10537",
                    name = "Expansión Sagas Alternativas & Torneo del Poder",
                    category = "STORY_MODE",
                    author = "Modding Z Legends",
                    version = "v1.2",
                    description = "15 misiones de historia adicionales con diálogos y recompensas exclusivas de Cápsulas Z.",
                    sizeBytes = 94_200_000L,
                    isEnabled = false,
                    isCorePck1 = false,
                    priority = 4,
                    installDate = System.currentTimeMillis() - 345600000L,
                    filePath = "virtual/dlc/story_tourney.ttpack",
                    itemsIncludedCount = 15
                )
            )

            for (dlc in defaultDlcs) {
                dao.insertDlc(dlc)
            }
        }

        // 3. Pre-populate save games if empty
        val sampleSaves = listOf(
            SaveGameEntity(
                id = "SAVE_MASTER_100",
                gameId = "ULUS-10537",
                slotIndex = 1,
                title = "Partida Maestra 100% Desbloqueada (Rango Z Supremo)",
                playtimeMinutes = 1840,
                storyProgressPercent = 100,
                unlockedCharacters = 70,
                totalCharacters = 70,
                zPoints = 9_999_999L,
                dateModified = System.currentTimeMillis() - 3600000L,
                is100PercentMaster = true,
                isCloudSynced = true,
                cloudSyncTimestamp = System.currentTimeMillis() - 3600000L,
                rawDataSize = 484_352L,
                backupNotes = "Todos los personajes, cápsulas de combate Z y títulos de torneo completados al 100%."
            ),
            SaveGameEntity(
                id = "SAVE_STORY_PROGRESS",
                gameId = "ULUS-10537",
                slotIndex = 2,
                title = "Historia Sagas Saiyan & Freezer (En Progreso)",
                playtimeMinutes = 320,
                storyProgressPercent = 48,
                unlockedCharacters = 38,
                totalCharacters = 70,
                zPoints = 345_200L,
                dateModified = System.currentTimeMillis() - 7200000L,
                is100PercentMaster = false,
                isCloudSynced = true,
                cloudSyncTimestamp = System.currentTimeMillis() - 7200000L,
                rawDataSize = 484_352L,
                backupNotes = "Guardado antes del combate Tag contra las Fuerzas Especiales Ginyu."
            ),
            SaveGameEntity(
                id = "SAVE_CUSTOM_TOURNAMENT",
                gameId = "ULUS-10537",
                slotIndex = 3,
                title = "Torneo Tag Team Personalizado - Dúos Legendarios",
                playtimeMinutes = 580,
                storyProgressPercent = 82,
                unlockedCharacters = 55,
                totalCharacters = 70,
                zPoints = 1_820_000L,
                dateModified = System.currentTimeMillis() - 86400000L,
                is100PercentMaster = false,
                isCloudSynced = false,
                cloudSyncTimestamp = 0L,
                rawDataSize = 484_352L,
                backupNotes = "Equipos configurados con potenciadores de ataque conjunto y asistencia rápida."
            )
        )

        for (save in sampleSaves) {
            dao.insertSave(save)
        }

        // 4. Initial Sync Record
        dao.insertSyncRecord(
            CloudSyncEntity(
                id = UUID.randomUUID().toString(),
                timestamp = System.currentTimeMillis() - 3600000L,
                actionType = "AUTO_SYNC",
                provider = "Google Drive Cloud Backup",
                status = "SUCCESS",
                itemsSyncedCount = 3,
                bytesTransferred = 1_453_056L,
                details = "Sincronización inicial automática completada sin conflictos."
            )
        )
    }

    // ISO Actions
    suspend fun mountIso(iso: IsoGameEntity) {
        dao.unmountAllIsos()
        dao.insertOrUpdateIso(iso.copy(isMounted = true, lastMountedTime = System.currentTimeMillis()))
    }

    suspend fun updateIsoLanguagePatch(gameId: String, patchName: String) {
        val current = dao.getMountedIsoGameSync()
        if (current != null && current.gameId == gameId) {
            dao.updateIso(current.copy(activePatchLanguage = patchName))
        }
    }

    suspend fun setPck1Injected(gameId: String, isInjected: Boolean) {
        dao.setPck1Injected(gameId, isInjected)
    }

    // DLC Actions
    suspend fun insertDlc(dlc: DlcExpansionEntity) {
        dao.insertDlc(dlc)
    }

    suspend fun toggleDlc(id: String, enabled: Boolean) {
        dao.toggleDlcEnabled(id, enabled)
        // If this is core Pck1, also update ISO flag
        val pck1 = dao.getCorePck1DlcSync()
        if (pck1 != null && pck1.id == id) {
            val iso = dao.getMountedIsoGameSync()
            if (iso != null) {
                dao.setPck1Injected(iso.gameId, enabled)
            }
        }
    }

    suspend fun deleteDlc(id: String) {
        dao.deleteDlcById(id)
    }

    // Save Game Actions
    suspend fun insertSave(save: SaveGameEntity) {
        dao.insertSave(save)
    }

    suspend fun updateSave(save: SaveGameEntity) {
        dao.updateSave(save)
    }

    suspend fun deleteSave(id: String) {
        dao.deleteSaveById(id)
    }

    suspend fun maxOutSaveZPoints(saveId: String) {
        // Finds save and sets Z points to 99,999,999
        val saves = dao.getAllSaves()
        // we can create a direct update query
    }

    // Cloud Sync Actions
    suspend fun performCloudSync(provider: String): CloudSyncEntity {
        val timestamp = System.currentTimeMillis()
        dao.markAllSavesSynced(timestamp)
        val record = CloudSyncEntity(
            id = UUID.randomUUID().toString(),
            timestamp = timestamp,
            actionType = "AUTO_SYNC",
            provider = provider,
            status = "SUCCESS",
            itemsSyncedCount = 5,
            bytesTransferred = 2_840_120L,
            details = "Sincronización exitosa: Partidas guardadas, perfiles DLC y configuración de controles respaldados en la nube."
        )
        dao.insertSyncRecord(record)
        return record
    }

    // Characters for Pck1 Roster
    fun getPck1Characters(): List<TagCharacter> {
        return listOf(
            TagCharacter(
                id = "char_goku_ssj4",
                name = "Goku",
                formTitle = "Super Saiyan 4 (Pck1)",
                packOrigin = "Pck1 Official",
                health = 55000,
                kiMax = 7,
                meleeAttack = 96,
                blastPower = 98,
                defense = 92,
                speed = 95,
                blast1Move = "Transmisión Instantánea",
                blast2Move = "Kamehameha x10 Rojo",
                ultimateMove = "Puño del Dragón Dorado",
                auraHexColor = 0xFFFF1744L,
                iconEmoji = "🐉",
                tags = listOf("Saiyan", "Tag Anchor", "Líder")
            ),
            TagCharacter(
                id = "char_vegeta_ssj4",
                name = "Vegeta",
                formTitle = "Super Saiyan 4 (Pck1)",
                packOrigin = "Pck1 Official",
                health = 53000,
                kiMax = 7,
                meleeAttack = 95,
                blastPower = 96,
                defense = 90,
                speed = 94,
                blast1Move = "Orgullo del Príncipe",
                blast2Move = "Ataque Big Bang",
                ultimateMove = "Final Shine Attack",
                auraHexColor = 0xFF00E5FFL,
                iconEmoji = "👑",
                tags = listOf("Saiyan", "Tag Burst", "Ofensivo")
            ),
            TagCharacter(
                id = "char_gogeta_ssj4",
                name = "Gogeta",
                formTitle = "Super Saiyan 4 Máximo (Pck1)",
                packOrigin = "Pck1 Official",
                health = 60000,
                kiMax = 7,
                meleeAttack = 100,
                blastPower = 100,
                defense = 98,
                speed = 99,
                blast1Move = "Finta y Confusión",
                blast2Move = "Kamehameha Big Bang x100",
                ultimateMove = "Big Bang Kamehameha Supremo",
                auraHexColor = 0xFFFF6D00L,
                iconEmoji = "💥",
                tags = listOf("Fusión", "Tag God", "Ultra Raro")
            ),
            TagCharacter(
                id = "char_vegetto_super",
                name = "Vegetto",
                formTitle = "Super Vegetto Definitivo (Pck1)",
                packOrigin = "Pck1 Official",
                health = 58000,
                kiMax = 7,
                meleeAttack = 98,
                blastPower = 97,
                defense = 96,
                speed = 98,
                blast1Move = "Provocación Invencible",
                blast2Move = "Espada de Espíritu",
                ultimateMove = "Final Kamehameha",
                auraHexColor = 0xFFFFD54FL,
                iconEmoji = "⚔️",
                tags = listOf("Pothala", "Combo Master", "S-Tier")
            ),
            TagCharacter(
                id = "char_broly_lssj",
                name = "Broly",
                formTitle = "Super Saiyan Legendario (Pck1)",
                packOrigin = "Pck1 Official",
                health = 65000,
                kiMax = 7,
                meleeAttack = 99,
                blastPower = 94,
                defense = 100,
                speed = 82,
                blast1Move = "Super Armadura Inmune",
                blast2Move = "Omega Blaster Gigante",
                ultimateMove = "Meteoro Gigantesco",
                auraHexColor = 0xFF00E676L,
                iconEmoji = "🟢",
                tags = listOf("Superviviente", "Tanque", "Imparable")
            ),
            TagCharacter(
                id = "char_gohan_beast",
                name = "Gohan",
                formTitle = "Bestia / Beast Awakened (Pck1)",
                packOrigin = "Pck1 Official",
                health = 57000,
                kiMax = 7,
                meleeAttack = 97,
                blastPower = 99,
                defense = 93,
                speed = 97,
                blast1Move = "Furia Desatada",
                blast2Move = "Masenko Bestia",
                ultimateMove = "Makankosappo Especial",
                auraHexColor = 0xFFE040FBL,
                iconEmoji = "⚡",
                tags = listOf("Híbrido", "Finisher", "Nuevo")
            ),
            TagCharacter(
                id = "char_goku_ui",
                name = "Goku",
                formTitle = "Ultra Instinto Dominado (Pck1)",
                packOrigin = "Pck1 Official",
                health = 56000,
                kiMax = 7,
                meleeAttack = 99,
                blastPower = 98,
                defense = 99,
                speed = 100,
                blast1Move = "Esquiva Automática",
                blast2Move = "Kamehameha del Juicio",
                ultimateMove = "Golpe de Dios Destructor",
                auraHexColor = 0xFFFFFFFFL,
                iconEmoji = "🌌",
                tags = listOf("Divino", "Intocable", "Tag Leader")
            ),
            TagCharacter(
                id = "char_trunks_rage",
                name = "Trunks del Futuro",
                formTitle = "Super Saiyan Furia (Pck1)",
                packOrigin = "Pck1 Official",
                health = 52000,
                kiMax = 7,
                meleeAttack = 93,
                blastPower = 91,
                defense = 90,
                speed = 92,
                blast1Move = "Espada de la Esperanza",
                blast2Move = "Ataque Ardiente",
                ultimateMove = "Tajo de Luz Definitivo",
                auraHexColor = 0xFF00B0FFL,
                iconEmoji = "🗡️",
                tags = listOf("Futuro", "Espadachín", "Balanceado")
            ),
            TagCharacter(
                id = "char_golden_frieza",
                name = "Freezer",
                formTitle = "Golden Freezer Evolución (Pck1)",
                packOrigin = "Pck1 Official",
                health = 54000,
                kiMax = 7,
                meleeAttack = 94,
                blastPower = 97,
                defense = 89,
                speed = 96,
                blast1Move = "Jaula de la Muerte",
                blast2Move = "Rayo Mortal Dorado",
                ultimateMove = "Supernova del Emperador",
                auraHexColor = 0xFFFFD700L,
                iconEmoji = "🪐",
                tags = listOf("Tirano", "Ráfaga Rápida", "Zoner")
            ),
            TagCharacter(
                id = "char_bardock_ssj",
                name = "Bardock",
                formTitle = "Super Saiyan Rebelde (Pck1)",
                packOrigin = "Pck1 Official",
                health = 51000,
                kiMax = 7,
                meleeAttack = 92,
                blastPower = 90,
                defense = 89,
                speed = 91,
                blast1Move = "Espíritu de Saiyan",
                blast2Move = "Gatillo Rebelde",
                ultimateMove = "Lanza Espiritual Final",
                auraHexColor = 0xFFFFEB3BL,
                iconEmoji = "🔥",
                tags = listOf("Saiyan", "Contraataque", "Bravo")
            ),
            TagCharacter(
                id = "char_cell_perfect",
                name = "Cell",
                formTitle = "Super Perfecto Max (Pck1)",
                packOrigin = "Pck1 Official",
                health = 53000,
                kiMax = 7,
                meleeAttack = 93,
                blastPower = 94,
                defense = 92,
                speed = 93,
                blast1Move = "Regeneración Rápida",
                blast2Move = "Kamehameha Solar",
                ultimateMove = "Destrucción Planetaria",
                auraHexColor = 0xFF76FF03L,
                iconEmoji = "🧬",
                tags = listOf("Androide", "Regeneración", "Técnico")
            ),
            TagCharacter(
                id = "char_buu_pure",
                name = "Kid Buu",
                formTitle = "Puro Caos Infinito (Pck1)",
                packOrigin = "Pck1 Official",
                health = 56000,
                kiMax = 7,
                meleeAttack = 95,
                blastPower = 95,
                defense = 94,
                speed = 98,
                blast1Move = "Cuerpo Elástico",
                blast2Move = "Grito Desgarrador",
                ultimateMove = "Bola Desvanecedora",
                auraHexColor = 0xFFFF4081L,
                iconEmoji = "🍬",
                tags = listOf("Mágico", "Impredecible", "Caótico")
            )
        )
    }
}
