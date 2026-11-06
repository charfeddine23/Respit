package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import kotlin.math.hypot

@Composable
fun PatternLockView(
    onPatternComplete: (String) -> Unit,
    modifier: Modifier = Modifier,
    isDark: Boolean = false,
    isError: Boolean = false,
    enabled: Boolean = true
) {
    val selectedNodes = remember { mutableStateListOf<Int>() }
    var currentTouchPoint by remember { mutableStateOf<Offset?>(null) }

    val activeColor = if (isError) {
        Color(0xFFEF4444)
    } else {
        if (isDark) Color(0xFF00E5FF) else Color(0xFF00BCD4)
    }

    val inactiveDotColor = if (isDark) {
        Color(0xFF475569)
    } else {
        Color(0xFF94A3B8)
    }

    val activeDotGlow = if (isError) {
        Color(0x40EF4444)
    } else {
        if (isDark) Color(0x4D00E5FF) else Color(0x4D00BCD4)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(16.dp)
            .testTag("pattern_lock_view")
    ) {
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(enabled) {
                    if (!enabled) return@pointerInput
                    detectDragGestures(
                        onDragStart = { offset ->
                            selectedNodes.clear()
                            currentTouchPoint = offset
                            val hit = findHitNode(offset, size.width.toFloat(), size.height.toFloat())
                            if (hit != null && !selectedNodes.contains(hit)) {
                                selectedNodes.add(hit)
                            }
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val pos = change.position
                            currentTouchPoint = pos
                            val hit = findHitNode(pos, size.width.toFloat(), size.height.toFloat())
                            if (hit != null && !selectedNodes.contains(hit)) {
                                selectedNodes.add(hit)
                            }
                        },
                        onDragEnd = {
                            currentTouchPoint = null
                            if (selectedNodes.isNotEmpty()) {
                                onPatternComplete(selectedNodes.joinToString(","))
                            }
                        },
                        onDragCancel = {
                            currentTouchPoint = null
                            selectedNodes.clear()
                        }
                    )
                }
        ) {
            val width = size.width
            val height = size.height

            fun getNodeCenter(index: Int): Offset {
                val row = index / 3
                val col = index % 3
                val cellW = width / 3f
                val cellH = height / 3f
                return Offset(col * cellW + cellW / 2f, row * cellH + cellH / 2f)
            }

            // Draw connecting lines between selected nodes
            for (i in 0 until selectedNodes.size - 1) {
                val start = getNodeCenter(selectedNodes[i])
                val end = getNodeCenter(selectedNodes[i + 1])
                drawLine(
                    color = activeColor,
                    start = start,
                    end = end,
                    strokeWidth = 10f,
                    cap = StrokeCap.Round
                )
            }

            // Draw line to current touch point while dragging
            if (currentTouchPoint != null && selectedNodes.isNotEmpty()) {
                val lastNodeCenter = getNodeCenter(selectedNodes.last())
                drawLine(
                    color = activeColor.copy(alpha = 0.7f),
                    start = lastNodeCenter,
                    end = currentTouchPoint!!,
                    strokeWidth = 8f,
                    cap = StrokeCap.Round
                )
            }

            // Draw the 9 nodes (dots and active rings)
            for (i in 0 until 9) {
                val center = getNodeCenter(i)
                val isSelected = selectedNodes.contains(i)

                if (isSelected) {
                    // Outer glow ring
                    drawCircle(
                        color = activeDotGlow,
                        radius = 28f,
                        center = center
                    )
                    // Mid stroke ring
                    drawCircle(
                        color = activeColor,
                        radius = 18f,
                        center = center,
                        style = Stroke(width = 4f)
                    )
                    // Inner active dot
                    drawCircle(
                        color = activeColor,
                        radius = 8f,
                        center = center
                    )
                } else {
                    // Inactive dot
                    drawCircle(
                        color = inactiveDotColor,
                        radius = 7f,
                        center = center
                    )
                }
            }
        }
    }
}

private fun findHitNode(pos: Offset, width: Float, height: Float): Int? {
    val cellW = width / 3f
    val cellH = height / 3f
    val hitRadius = cellW * 0.38f

    for (row in 0 until 3) {
        for (col in 0 until 3) {
            val cx = col * cellW + cellW / 2f
            val cy = row * cellH + cellH / 2f
            val dist = hypot((pos.x - cx).toDouble(), (pos.y - cy).toDouble()).toFloat()
            if (dist <= hitRadius) {
                return row * 3 + col
            }
        }
    }
    return null
}
