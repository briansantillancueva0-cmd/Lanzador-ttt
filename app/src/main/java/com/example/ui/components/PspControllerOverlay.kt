package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PspButton
import com.example.data.model.PspControlsConfig
import com.example.data.model.PspInputState
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun PspControllerOverlay(
    config: PspControlsConfig,
    inputState: PspInputState,
    onButtonPress: (PspButton) -> Unit,
    onButtonRelease: (PspButton) -> Unit,
    onAnalogMove: (Float, Float) -> Unit,
    onQuickTagSwitch: () -> Unit,
    onQuickBurst: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .alpha(config.opacity)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. Top Shoulder Buttons (L & R)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PspShoulderButton(
                label = "L  TAG ASSIST",
                isPressed = inputState.pressedButtons.contains(PspButton.L_SHOULDER),
                onPress = { onButtonPress(PspButton.L_SHOULDER) },
                onRelease = { onButtonRelease(PspButton.L_SHOULDER) },
                testTag = "psp_btn_l",
                modifier = Modifier.weight(1f).padding(end = 12.dp)
            )

            // Middle Tag Quick Action Pills
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalButton(
                    onClick = onQuickTagSwitch,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = TagOrange.copy(alpha = 0.85f),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("psp_quick_tag_switch")
                ) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("RELEVO", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                FilledTonalButton(
                    onClick = onQuickBurst,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = TagBlue.copy(alpha = 0.85f),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("psp_quick_burst")
                ) {
                    Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("BURST", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            PspShoulderButton(
                label = "ULTIMATE  R",
                isPressed = inputState.pressedButtons.contains(PspButton.R_SHOULDER),
                onPress = { onButtonPress(PspButton.R_SHOULDER) },
                onRelease = { onButtonRelease(PspButton.R_SHOULDER) },
                testTag = "psp_btn_r",
                modifier = Modifier.weight(1f).padding(start = 12.dp)
            )
        }

        // 2. Middle Main Cluster (D-Pad + Analog Left, Action Diamond Right)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .scale(config.buttonScale),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Controls: D-Pad & Analog Nub
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PspDpadCluster(
                    pressedButtons = inputState.pressedButtons,
                    onPress = onButtonPress,
                    onRelease = onButtonRelease
                )

                if (config.showAnalogStick) {
                    PspAnalogNub(
                        analogX = inputState.analogX,
                        analogY = inputState.analogY,
                        onMove = onAnalogMove
                    )
                }
            }

            // Right Controls: Action Diamond (△, ○, ✕, □)
            PspActionDiamond(
                pressedButtons = inputState.pressedButtons,
                onPress = onButtonPress,
                onRelease = onButtonRelease
            )
        }

        // 3. Bottom System Bar (Home, Select, Start)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PspSystemPill(
                label = "HOME",
                isPressed = inputState.pressedButtons.contains(PspButton.HOME),
                onPress = { onButtonPress(PspButton.HOME) },
                onRelease = { onButtonRelease(PspButton.HOME) },
                testTag = "psp_btn_home"
            )
            Spacer(Modifier.width(16.dp))
            PspSystemPill(
                label = "SELECT",
                isPressed = inputState.pressedButtons.contains(PspButton.SELECT),
                onPress = { onButtonPress(PspButton.SELECT) },
                onRelease = { onButtonRelease(PspButton.SELECT) },
                testTag = "psp_btn_select"
            )
            Spacer(Modifier.width(16.dp))
            PspSystemPill(
                label = "START",
                isPressed = inputState.pressedButtons.contains(PspButton.START),
                onPress = { onButtonPress(PspButton.START) },
                onRelease = { onButtonRelease(PspButton.START) },
                testTag = "psp_btn_start"
            )
        }
    }
}

@Composable
fun PspShoulderButton(
    label: String,
    isPressed: Boolean,
    onPress: () -> Unit,
    onRelease: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(if (isPressed) 0.94f else 1f, label = "shoulder_scale")
    val bgColor = if (isPressed) TagGold.copy(alpha = 0.5f) else DarkSurfaceHigh.copy(alpha = 0.85f)
    val borderColor = if (isPressed) TagGold else DarkBorder

    Box(
        modifier = modifier
            .scale(scale)
            .height(38.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(bgColor, DarkSurface)
                )
            )
            .border(1.5.dp, borderColor, RoundedCornerShape(8.dp))
            .testTag(testTag)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onPress()
                        tryAwaitRelease()
                        onRelease()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isPressed) TagGold else TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun PspDpadCluster(
    pressedButtons: Set<PspButton>,
    onPress: (PspButton) -> Unit,
    onRelease: (PspButton) -> Unit
) {
    val dpadSize = 138.dp
    val armWidth = 42.dp

    Box(
        modifier = Modifier
            .size(dpadSize)
            .testTag("psp_dpad_cluster"),
        contentAlignment = Alignment.Center
    ) {
        // Background cross shape
        Box(
            modifier = Modifier
                .width(armWidth)
                .height(dpadSize)
                .clip(RoundedCornerShape(6.dp))
                .background(DarkSurfaceVariant.copy(alpha = 0.9f))
                .border(1.dp, DarkBorder, RoundedCornerShape(6.dp))
        )
        Box(
            modifier = Modifier
                .width(dpadSize)
                .height(armWidth)
                .clip(RoundedCornerShape(6.dp))
                .background(DarkSurfaceVariant.copy(alpha = 0.9f))
                .border(1.dp, DarkBorder, RoundedCornerShape(6.dp))
        )

        // Center circular depression
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(DarkBg)
                .border(1.dp, DarkBorder, CircleShape)
        )

        // UP
        PspDirectionalArm(
            symbol = "▲",
            button = PspButton.UP,
            isPressed = pressedButtons.contains(PspButton.UP),
            onPress = onPress,
            onRelease = onRelease,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(armWidth, 46.dp)
                .testTag("psp_dpad_up")
        )

        // DOWN
        PspDirectionalArm(
            symbol = "▼",
            button = PspButton.DOWN,
            isPressed = pressedButtons.contains(PspButton.DOWN),
            onPress = onPress,
            onRelease = onRelease,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(armWidth, 46.dp)
                .testTag("psp_dpad_down")
        )

        // LEFT
        PspDirectionalArm(
            symbol = "◀",
            button = PspButton.LEFT,
            isPressed = pressedButtons.contains(PspButton.LEFT),
            onPress = onPress,
            onRelease = onRelease,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(46.dp, armWidth)
                .testTag("psp_dpad_left")
        )

        // RIGHT
        PspDirectionalArm(
            symbol = "▶",
            button = PspButton.RIGHT,
            isPressed = pressedButtons.contains(PspButton.RIGHT),
            onPress = onPress,
            onRelease = onRelease,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(46.dp, armWidth)
                .testTag("psp_dpad_right")
        )
    }
}

@Composable
fun PspDirectionalArm(
    symbol: String,
    button: PspButton,
    isPressed: Boolean,
    onPress: (PspButton) -> Unit,
    onRelease: (PspButton) -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (isPressed) TagGold.copy(alpha = 0.4f) else Color.Transparent

    Box(
        modifier = modifier
            .background(bg)
            .pointerInput(button) {
                detectTapGestures(
                    onPress = {
                        onPress(button)
                        tryAwaitRelease()
                        onRelease(button)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            color = if (isPressed) TagGold else TextSecondary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun PspAnalogNub(
    analogX: Float,
    analogY: Float,
    onMove: (Float, Float) -> Unit
) {
    val baseSize = 84.dp
    val stickSize = 46.dp
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .size(baseSize)
            .clip(CircleShape)
            .background(DarkSurfaceVariant.copy(alpha = 0.8f))
            .border(1.5.dp, DarkBorder, CircleShape)
            .testTag("psp_analog_nub")
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { },
                    onDragEnd = {
                        offsetX = 0f
                        offsetY = 0f
                        onMove(0f, 0f)
                    },
                    onDragCancel = {
                        offsetX = 0f
                        offsetY = 0f
                        onMove(0f, 0f)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val maxRadius = 38f
                        val newX = offsetX + dragAmount.x
                        val newY = offsetY + dragAmount.y
                        val dist = sqrt(newX * newX + newY * newY)
                        if (dist <= maxRadius) {
                            offsetX = newX
                            offsetY = newY
                        } else {
                            offsetX = (newX / dist) * maxRadius
                            offsetY = (newY / dist) * maxRadius
                        }
                        onMove(offsetX / maxRadius, offsetY / maxRadius)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Grooved concentric circles on the Nub
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = DarkBorder,
                radius = size.minDimension / 2 - 8.dp.toPx(),
                style = Stroke(width = 1.dp.toPx())
            )
            drawCircle(
                color = DarkBorder.copy(alpha = 0.5f),
                radius = size.minDimension / 2 - 16.dp.toPx(),
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // Draggable Nub head
        Box(
            modifier = Modifier
                .offset(x = (offsetX * 0.8).dp, y = (offsetY * 0.8).dp)
                .size(stickSize)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(DarkSurfaceHigh, DarkBg)
                    )
                )
                .border(2.dp, TagBlue.copy(alpha = 0.7f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(TagBlue.copy(alpha = 0.4f))
            )
        }
    }
}

@Composable
fun PspActionDiamond(
    pressedButtons: Set<PspButton>,
    onPress: (PspButton) -> Unit,
    onRelease: (PspButton) -> Unit
) {
    val diamondSize = 144.dp
    val buttonSize = 46.dp

    Box(
        modifier = Modifier
            .size(diamondSize)
            .testTag("psp_action_diamond"),
        contentAlignment = Alignment.Center
    ) {
        // Circular translucent background holder
        Box(
            modifier = Modifier
                .size(136.dp)
                .clip(CircleShape)
                .background(DarkSurfaceVariant.copy(alpha = 0.6f))
                .border(1.dp, DarkBorder.copy(alpha = 0.6f), CircleShape)
        )

        // TRIANGLE (Top) - Green
        PspActionButton(
            symbol = "△",
            color = PspTriangle,
            button = PspButton.TRIANGLE,
            isPressed = pressedButtons.contains(PspButton.TRIANGLE),
            onPress = onPress,
            onRelease = onRelease,
            testTag = "psp_btn_triangle",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(buttonSize)
        )

        // CIRCLE (Right) - Red
        PspActionButton(
            symbol = "○",
            color = PspCircle,
            button = PspButton.CIRCLE,
            isPressed = pressedButtons.contains(PspButton.CIRCLE),
            onPress = onPress,
            onRelease = onRelease,
            testTag = "psp_btn_circle",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(buttonSize)
        )

        // CROSS (Bottom) - Blue
        PspActionButton(
            symbol = "✕",
            color = PspCross,
            button = PspButton.CROSS,
            isPressed = pressedButtons.contains(PspButton.CROSS),
            onPress = onPress,
            onRelease = onRelease,
            testTag = "psp_btn_cross",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(buttonSize)
        )

        // SQUARE (Left) - Pink
        PspActionButton(
            symbol = "□",
            color = PspSquare,
            button = PspButton.SQUARE,
            isPressed = pressedButtons.contains(PspButton.SQUARE),
            onPress = onPress,
            onRelease = onRelease,
            testTag = "psp_btn_square",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(buttonSize)
        )
    }
}

@Composable
fun PspActionButton(
    symbol: String,
    color: Color,
    button: PspButton,
    isPressed: Boolean,
    onPress: (PspButton) -> Unit,
    onRelease: (PspButton) -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(if (isPressed) 0.90f else 1f, label = "btn_scale")
    val currentBg = if (isPressed) color.copy(alpha = 0.5f) else DarkSurfaceHigh.copy(alpha = 0.9f)
    val borderCol = if (isPressed) color else color.copy(alpha = 0.4f)

    Box(
        modifier = modifier
            .scale(scale)
            .clip(CircleShape)
            .background(currentBg)
            .border(2.dp, borderCol, CircleShape)
            .testTag(testTag)
            .pointerInput(button) {
                detectTapGestures(
                    onPress = {
                        onPress(button)
                        tryAwaitRelease()
                        onRelease(button)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            color = color,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun PspSystemPill(
    label: String,
    isPressed: Boolean,
    onPress: () -> Unit,
    onRelease: () -> Unit,
    testTag: String
) {
    val scale by animateFloatAsState(if (isPressed) 0.92f else 1f, label = "pill_scale")
    val bg = if (isPressed) TagGold.copy(alpha = 0.4f) else DarkSurfaceHigh.copy(alpha = 0.8f)

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.dp, if (isPressed) TagGold else DarkBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag(testTag)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onPress()
                        tryAwaitRelease()
                        onRelease()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isPressed) TagGold else TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}
