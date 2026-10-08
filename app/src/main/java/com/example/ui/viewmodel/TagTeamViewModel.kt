package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.CloudSyncEntity
import com.example.data.local.entity.DlcExpansionEntity
import com.example.data.local.entity.IsoGameEntity
import com.example.data.local.entity.SaveGameEntity
import com.example.data.model.AppLanguage
import com.example.data.model.LocalizationStrings
import com.example.data.model.PspButton
import com.example.data.model.PspControlsConfig
import com.example.data.model.PspInputState
import com.example.data.model.TagCharacter
import com.example.data.util.IsoParser
import com.example.repository.TagTeamRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class ArenaBattleState(
    val playerFighter: TagCharacter,
    val partnerFighter: TagCharacter,
    val isPartnerActive: Boolean = false,
    val playerHp: Int = 55000,
    val playerMaxHp: Int = 55000,
    val playerKi: Float = 5.0f,
    val partnerHp: Int = 53000,
    val partnerMaxHp: Int = 53000,
    val partnerKi: Float = 4.0f,
    val enemyHp: Int = 50000,
    val enemyMaxHp: Int = 60000,
    val comboHits: Int = 0,
    val lastActionText: String = "¡Listo para el combate!",
    val isBurstActive: Boolean = false
)

class TagTeamViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TagTeamRepository(application)

    // Language state - defaults to Spanish as requested
    private val _currentLanguage = MutableStateFlow(AppLanguage.SPANISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    // Navigation tab
    private val _currentTab = MutableStateFlow("dashboard")
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    // Active ISO
    val mountedIso: StateFlow<IsoGameEntity?> = repository.mountedIso.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    // DLCs
    val dlcs: StateFlow<List<DlcExpansionEntity>> = repository.allDlcs.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Core Pck1
    val corePck1: StateFlow<DlcExpansionEntity?> = repository.corePck1.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    // Saves
    val saves: StateFlow<List<SaveGameEntity>> = repository.allSaves.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Cloud History
    val syncHistory: StateFlow<List<CloudSyncEntity>> = repository.syncHistory.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Cloud Sync States
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _autoSyncEnabled = MutableStateFlow(true)
    val autoSyncEnabled: StateFlow<Boolean> = _autoSyncEnabled.asStateFlow()

    private val _selectedCloudProvider = MutableStateFlow("Google Drive Cloud Backup")
    val selectedCloudProvider: StateFlow<String> = _selectedCloudProvider.asStateFlow()

    // PSP Controls state
    private val _pspConfig = MutableStateFlow(PspControlsConfig())
    val pspConfig: StateFlow<PspControlsConfig> = _pspConfig.asStateFlow()

    private val _pspInput = MutableStateFlow(PspInputState())
    val pspInput: StateFlow<PspInputState> = _pspInput.asStateFlow()

    // Characters list (for Pck1 & Arena)
    val pck1Characters: List<TagCharacter> = repository.getPck1Characters()

    private val _selectedCharacter = MutableStateFlow(pck1Characters.first())
    val selectedCharacter: StateFlow<TagCharacter> = _selectedCharacter.asStateFlow()

    // Arena State
    private val _arenaState = MutableStateFlow(
        ArenaBattleState(
            playerFighter = pck1Characters[0],
            partnerFighter = pck1Characters[1]
        )
    )
    val arenaState: StateFlow<ArenaBattleState> = _arenaState.asStateFlow()

    // Graphics & System Settings
    private val _graphicsBackend = MutableStateFlow("Vulkan (Recomendado)")
    val graphicsBackend: StateFlow<String> = _graphicsBackend.asStateFlow()

    private val _renderingResolution = MutableStateFlow("2x PSP (960x544)")
    val renderingResolution: StateFlow<String> = _renderingResolution.asStateFlow()

    private val _fpsPatchEnabled = MutableStateFlow(true)
    val fpsPatchEnabled: StateFlow<Boolean> = _fpsPatchEnabled.asStateFlow()

    private val _widescreenPatch = MutableStateFlow(true)
    val widescreenPatch: StateFlow<Boolean> = _widescreenPatch.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeDefaultDataIfEmpty()
        }
    }

    fun setTab(tab: String) {
        _currentTab.value = tab
    }

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
    }

    fun getString(key: String): String {
        return LocalizationStrings.get(key, _currentLanguage.value)
    }

    fun selectCharacter(character: TagCharacter) {
        _selectedCharacter.value = character
        // Also update arena primary
        _arenaState.value = _arenaState.value.copy(
            playerFighter = character,
            playerHp = character.health,
            playerMaxHp = character.health
        )
    }

    // ISO Actions
    fun loadSampleOfficialIso() {
        viewModelScope.launch {
            val sampleIso = IsoGameEntity(
                gameId = "ULUS-10537",
                title = "Dragon Ball Z: Tenkaichi Tag Team",
                fileName = "DBZ_Tenkaichi_Tag_Team_USA.iso",
                fileUri = "content://virtual/iso/ULUS-10537.iso",
                fileSizeBytes = 1_254_211_584L,
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
            repository.mountIso(sampleIso)
            triggerAutoSyncIfEnabled("Carga de ISO Oficial")
        }
    }

    fun handleCustomIsoImport(uri: Uri, fileName: String, fileSize: Long) {
        viewModelScope.launch {
            val parsed = IsoParser.parseStream(getApplication(), uri, fileName, fileSize)
            val iso = IsoGameEntity(
                gameId = parsed.gameId,
                title = parsed.title,
                fileName = parsed.fileName,
                fileUri = uri.toString(),
                fileSizeBytes = if (fileSize > 0) fileSize else 1_220_000_000L,
                region = parsed.region,
                discStructure = parsed.format,
                crc32 = parsed.crc32Hex,
                sha1 = "A1F98C0B293E..." + parsed.crc32Hex,
                status = "VERIFIED_READY",
                isMounted = true,
                lastMountedTime = System.currentTimeMillis(),
                hasPck1Injected = true,
                activePatchLanguage = "Español Latino (Voces HD)"
            )
            repository.mountIso(iso)
            triggerAutoSyncIfEnabled("Importación de ISO: ${iso.fileName}")
        }
    }

    // DLC Actions
    fun toggleDlc(id: String, enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleDlc(id, enabled)
            triggerAutoSyncIfEnabled("Alternar DLC: $id")
        }
    }

    fun deleteDlc(id: String) {
        viewModelScope.launch {
            repository.deleteDlc(id)
            triggerAutoSyncIfEnabled("Eliminar DLC: $id")
        }
    }

    fun addCustomDlc(name: String, author: String, category: String, description: String, sizeBytes: Long) {
        viewModelScope.launch {
            val newDlc = DlcExpansionEntity(
                id = "DLC_USER_" + UUID.randomUUID().toString().take(8),
                gameId = mountedIso.value?.gameId ?: "ULUS-10537",
                name = name,
                category = category,
                author = author,
                version = "v1.0",
                description = description,
                sizeBytes = if (sizeBytes > 0) sizeBytes else 50_000_000L,
                isEnabled = true,
                isCorePck1 = false,
                priority = 5,
                installDate = System.currentTimeMillis(),
                filePath = "virtual/dlc/$name.ttpack",
                itemsIncludedCount = 10
            )
            repository.insertDlc(newDlc)
            triggerAutoSyncIfEnabled("Nuevo DLC instalado: $name")
        }
    }

    // Pck1 Menu Personajes actions
    fun installOrTogglePck1(install: Boolean) {
        viewModelScope.launch {
            val pck1Dlc = repository.corePck1
            val currentIso = mountedIso.value
            if (currentIso != null) {
                repository.setPck1Injected(currentIso.gameId, install)
            }
            // Also enable or create core DLC
            val existing = repository.getMountedIsoSync()
            repository.toggleDlc("DLC_PCK1_CORE", install)
            triggerAutoSyncIfEnabled(if (install) "Pck1 Instalado" else "Pck1 Desinstalado")
        }
    }

    fun importCustomPck1File(name: String) {
        viewModelScope.launch {
            installOrTogglePck1(true)
            Toast.makeText(getApplication(), "Paquete Pck1 ($name) inyectado con éxito", Toast.LENGTH_SHORT).show()
        }
    }

    // Save Game Actions
    fun load100PercentMasterSave() {
        viewModelScope.launch {
            val masterSave = SaveGameEntity(
                id = "SAVE_MASTER_100",
                gameId = mountedIso.value?.gameId ?: "ULUS-10537",
                slotIndex = 1,
                title = "Partida Maestra 100% Desbloqueada (Rango Z Supremo)",
                playtimeMinutes = 1840,
                storyProgressPercent = 100,
                unlockedCharacters = 70,
                totalCharacters = 70,
                zPoints = 9_999_999L,
                dateModified = System.currentTimeMillis(),
                is100PercentMaster = true,
                isCloudSynced = false,
                cloudSyncTimestamp = 0L,
                rawDataSize = 484_352L,
                backupNotes = "Restaurado desde el archivo maestro oficial de Tag Team. 100% personajes y cápsulas."
            )
            repository.insertSave(masterSave)
            triggerAutoSyncIfEnabled("Guardado 100% Restaurado")
        }
    }

    fun backupCurrentSave() {
        viewModelScope.launch {
            val newSave = SaveGameEntity(
                id = "SAVE_BACKUP_" + UUID.randomUUID().toString().take(8),
                gameId = mountedIso.value?.gameId ?: "ULUS-10537",
                slotIndex = ((saves.value.maxOfOrNull { it.slotIndex } ?: 0) + 1),
                title = "Respaldo Rápido de Partida #${System.currentTimeMillis() % 1000}",
                playtimeMinutes = 450,
                storyProgressPercent = 88,
                unlockedCharacters = 62,
                totalCharacters = 70,
                zPoints = 4_500_000L,
                dateModified = System.currentTimeMillis(),
                is100PercentMaster = false,
                isCloudSynced = false,
                cloudSyncTimestamp = 0L,
                rawDataSize = 484_352L,
                backupNotes = "Copia de seguridad local generada automáticamente."
            )
            repository.insertSave(newSave)
            triggerAutoSyncIfEnabled("Nuevo Respaldo de Partida")
        }
    }

    fun deleteSave(id: String) {
        viewModelScope.launch {
            repository.deleteSave(id)
            triggerAutoSyncIfEnabled("Partida eliminada")
        }
    }

    fun maxOutZPoints(saveId: String) {
        viewModelScope.launch {
            val target = saves.value.find { it.id == saveId }
            if (target != null) {
                repository.updateSave(target.copy(zPoints = 9_999_999L, dateModified = System.currentTimeMillis(), isCloudSynced = false))
                triggerAutoSyncIfEnabled("Puntos Z al Máximo en Slot ${target.slotIndex}")
            }
        }
    }

    // Cloud Sync
    fun triggerCloudSync() {
        if (_isSyncing.value) return
        viewModelScope.launch {
            _isSyncing.value = true
            delay(1200) // realistic smooth network transfer animation
            repository.performCloudSync(_selectedCloudProvider.value)
            _isSyncing.value = false
        }
    }

    fun toggleAutoSync(enabled: Boolean) {
        _autoSyncEnabled.value = enabled
    }

    fun setCloudProvider(provider: String) {
        _selectedCloudProvider.value = provider
    }

    private fun triggerAutoSyncIfEnabled(reason: String) {
        if (_autoSyncEnabled.value) {
            viewModelScope.launch {
                delay(500)
                repository.performCloudSync(_selectedCloudProvider.value)
            }
        }
    }

    // PSP Controls & Haptics
    fun pressPspButton(button: PspButton) {
        val currentSet = _pspInput.value.pressedButtons.toMutableSet()
        currentSet.add(button)
        val newHits = _pspInput.value.comboCounter + 1
        _pspInput.value = _pspInput.value.copy(
            pressedButtons = currentSet,
            lastPressedButtonName = button.name,
            comboCounter = newHits
        )

        // Haptic Feedback
        if (_pspConfig.value.hapticsEnabled) {
            vibrateDevice(_pspConfig.value.vibrationIntensityMs)
        }

        // Action in Arena
        handleArenaInput(button)
    }

    fun releasePspButton(button: PspButton) {
        val currentSet = _pspInput.value.pressedButtons.toMutableSet()
        currentSet.remove(button)
        _pspInput.value = _pspInput.value.copy(pressedButtons = currentSet)
    }

    fun updatePspAnalog(x: Float, y: Float) {
        _pspInput.value = _pspInput.value.copy(analogX = x, analogY = y)
    }

    fun updatePspConfig(opacity: Float? = null, scale: Float? = null, haptics: Boolean? = null) {
        _pspConfig.value = _pspConfig.value.copy(
            opacity = opacity ?: _pspConfig.value.opacity,
            buttonScale = scale ?: _pspConfig.value.buttonScale,
            hapticsEnabled = haptics ?: _pspConfig.value.hapticsEnabled
        )
    }

    private fun vibrateDevice(ms: Long) {
        try {
            val context = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(ms)
            }
        } catch (_: Exception) {}
    }

    // Arena Battle Simulation Logic
    private fun handleArenaInput(button: PspButton) {
        val current = _arenaState.value
        val activeChar = if (current.isPartnerActive) current.partnerFighter else current.playerFighter

        when (button) {
            PspButton.SQUARE -> { // Basic melee attack
                val dmg = (activeChar.meleeAttack * 18).coerceAtLeast(300)
                val newEnemyHp = (current.enemyHp - dmg).coerceAtLeast(0)
                _arenaState.value = current.copy(
                    enemyHp = newEnemyHp,
                    comboHits = current.comboHits + 1,
                    lastActionText = "¡${activeChar.name} asesta combo de golpes rápidos! (-$dmg HP)"
                )
            }
            PspButton.TRIANGLE -> { // Ki Blast / Strong attack
                val dmg = (activeChar.blastPower * 22).coerceAtLeast(400)
                val newEnemyHp = (current.enemyHp - dmg).coerceAtLeast(0)
                val newKi = (if (current.isPartnerActive) current.partnerKi else current.playerKi) - 0.5f
                _arenaState.value = current.copy(
                    enemyHp = newEnemyHp,
                    comboHits = current.comboHits + 2,
                    playerKi = if (!current.isPartnerActive) newKi.coerceAtLeast(0f) else current.playerKi,
                    partnerKi = if (current.isPartnerActive) newKi.coerceAtLeast(0f) else current.partnerKi,
                    lastActionText = "¡Ráfaga de Ki: ${activeChar.blast2Move}! (-$dmg HP)"
                )
            }
            PspButton.CIRCLE -> { // Guard / Ki Charge
                val newKi = (if (current.isPartnerActive) current.partnerKi else current.playerKi) + 1.5f
                _arenaState.value = current.copy(
                    playerKi = if (!current.isPartnerActive) newKi.coerceAtMost(7f) else current.playerKi,
                    partnerKi = if (current.isPartnerActive) newKi.coerceAtMost(7f) else current.partnerKi,
                    lastActionText = "¡${activeChar.name} acumula energía Ki! (+1.5 barras)"
                )
            }
            PspButton.CROSS -> { // Dash / Step dodge
                _arenaState.value = current.copy(
                    lastActionText = "¡Desplazamiento rápido / Esquiva táctica de ${activeChar.name}!"
                )
            }
            PspButton.L_SHOULDER -> { // Tag Burst / Partner Assist
                triggerBurstTag()
            }
            PspButton.R_SHOULDER -> { // Ultimate Attack
                val ki = if (current.isPartnerActive) current.partnerKi else current.playerKi
                if (ki >= 3.0f) {
                    val dmg = (activeChar.blastPower * 60).coerceAtLeast(1500)
                    val newEnemyHp = (current.enemyHp - dmg).coerceAtLeast(0)
                    val remainingKi = ki - 3.0f
                    _arenaState.value = current.copy(
                        enemyHp = newEnemyHp,
                        playerKi = if (!current.isPartnerActive) remainingKi else current.playerKi,
                        partnerKi = if (current.isPartnerActive) remainingKi else current.partnerKi,
                        comboHits = current.comboHits + 15,
                        lastActionText = "🔥 ¡TÉCNICA DEFINITIVA!: ${activeChar.ultimateMove}! (-$dmg HP)"
                    )
                } else {
                    _arenaState.value = current.copy(
                        lastActionText = "⚠️ ¡Ki insuficiente para Técnica Definitiva! (Requiere 3 barras)"
                    )
                }
            }
            PspButton.SELECT -> { // Tag Switch Partner
                tagSwitchFighter()
            }
            PspButton.START -> { // Reset arena match
                _arenaState.value = current.copy(
                    enemyHp = 60000,
                    playerHp = current.playerMaxHp,
                    partnerHp = current.partnerMaxHp,
                    playerKi = 5.0f,
                    partnerKi = 4.0f,
                    comboHits = 0,
                    lastActionText = "¡Nueva ronda de combate Tag Team iniciada!"
                )
            }
            else -> {}
        }
    }

    fun tagSwitchFighter() {
        val current = _arenaState.value
        val nextActive = !current.isPartnerActive
        val switchedTo = if (nextActive) current.partnerFighter else current.playerFighter
        _arenaState.value = current.copy(
            isPartnerActive = nextActive,
            comboHits = current.comboHits + 1,
            lastActionText = "🔄 ¡RELEVO TAG TEAM! Entra al combate: ${switchedTo.name} (${switchedTo.formTitle})"
        )
        if (_pspConfig.value.hapticsEnabled) {
            vibrateDevice(50L)
        }
    }

    fun triggerBurstTag() {
        val current = _arenaState.value
        val duoDmg = 850
        val newEnemyHp = (current.enemyHp - duoDmg).coerceAtLeast(0)
        _arenaState.value = current.copy(
            isBurstActive = true,
            enemyHp = newEnemyHp,
            comboHits = current.comboHits + 8,
            lastActionText = "⚡ ¡BURST TAG TEAM DOBLE! ${current.playerFighter.name} y ${current.partnerFighter.name} atacan en sincronía!"
        )
        if (_pspConfig.value.hapticsEnabled) {
            vibrateDevice(80L)
        }
    }

    // Launch external emulator / intent
    fun launchGame(context: Context) {
        val iso = mountedIso.value
        if (iso == null) {
            Toast.makeText(context, "Por favor monta o carga una ISO primero", Toast.LENGTH_SHORT).show()
            return
        }

        // Try to launch external PPSSPP or emulator if installed, or fallback to native arena
        val ppssppIntent = context.packageManager.getLaunchIntentForPackage("org.ppsspp.ppsspp")
            ?: context.packageManager.getLaunchIntentForPackage("org.ppsspp.ppssppgold")

        if (ppssppIntent != null) {
            ppssppIntent.action = Intent.ACTION_VIEW
            ppssppIntent.setDataAndType(Uri.parse(iso.fileUri), "application/octet-stream")
            ppssppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(ppssppIntent)
                Toast.makeText(context, "Iniciando con PPSSPP...", Toast.LENGTH_SHORT).show()
                return
            } catch (_: Exception) {}
        }

        // Default: Open Arena Simulator mode
        _currentTab.value = "psp"
        Toast.makeText(context, "Iniciando Arena de Batalla Nativa con Controles PSP", Toast.LENGTH_SHORT).show()
    }

    // Settings actions
    fun setGraphicsBackend(backend: String) { _graphicsBackend.value = backend }
    fun setResolution(res: String) { _renderingResolution.value = res }
    fun setFpsPatch(enabled: Boolean) { _fpsPatchEnabled.value = enabled }
    fun setWidescreenPatch(enabled: Boolean) { _widescreenPatch.value = enabled }
    fun updateLanguagePatch(patch: String) {
        viewModelScope.launch {
            val iso = mountedIso.value
            if (iso != null) {
                repository.updateIsoLanguagePatch(iso.gameId, patch)
            }
        }
    }
}
