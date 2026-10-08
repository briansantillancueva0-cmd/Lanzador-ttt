package com.example.data.model

enum class PspButton {
    UP, DOWN, LEFT, RIGHT,
    TRIANGLE, CIRCLE, CROSS, SQUARE,
    L_SHOULDER, R_SHOULDER,
    START, SELECT, HOME
}

data class PspControlsConfig(
    val opacity: Float = 0.85f,
    val buttonScale: Float = 1.0f,
    val hapticsEnabled: Boolean = true,
    val showAnalogStick: Boolean = true,
    val showDpad: Boolean = true,
    val vibrationIntensityMs: Long = 25L
)

data class PspInputState(
    val pressedButtons: Set<PspButton> = emptySet(),
    val analogX: Float = 0f, // -1f to 1f
    val analogY: Float = 0f, // -1f to 1f
    val lastPressedButtonName: String = "Ninguno",
    val comboCounter: Int = 0
)
