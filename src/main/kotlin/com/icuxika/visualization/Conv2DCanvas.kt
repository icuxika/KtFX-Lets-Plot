package com.icuxika.visualization

import javafx.embed.swing.SwingNode
import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.stage.Stage
import jetbrains.datalore.plot.MonolithicCommon
import jetbrains.datalore.vis.swing.jfx.DefaultPlotPanelJfx
import jetbrains.letsPlot.GGBunch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import javax.swing.SwingUtilities
import kotlin.math.max
import kotlin.math.min

internal typealias TensorImageData = Array<Array<Array<FloatArray>>>

private fun show(parent: Parent) {
    CoroutineScope(context = Dispatchers.JavaFx).launch {
        Stage().apply { scene = Scene(parent) }.show()
    }
}

fun Canvas.showFX() = show(StackPane(this))

fun drawConv2DCanvas(
    filters: Array<*>,
    colorCoefficient: Double = 2.0
): Canvas {
    @Suppress("UNCHECKED_CAST")
    val dst = filters as TensorImageData
    return Canvas(1200.0, 800.0).apply {
        graphicsContext2D.let { gc ->
            for (k in 0 until 32) {
                for (i in dst.indices) {
                    for (j in dst[i].indices) {
                        val width = 15.0
                        val height = 15.0
                        var x = 10 + i * width
                        val y = 10 + j * height + k % 8 * 150
                        when (k) {
                            in 8..15 -> {
                                x += 150
                            }
                            in 16..23 -> {
                                x += 150 * 2
                            }
                            in 24..31 -> {
                                x += 150 * 3
                            }
                        }
                        val float = dst[i][j][0][k]
                        val grey = (min(1.0f, max(float * colorCoefficient.toFloat(), 0.0f)) * 255).toInt()
                        val color = Color.rgb(grey, grey, grey)
                        gc.fill = color
                        gc.fillRect(y, x, width, height)
                        gc.stroke = Color.BLACK
                        gc.strokeRect(y, x, width, height)
                    }
                }
            }
        }
    }
}

fun GGBunch.showFX() {
    val processedSpec = MonolithicCommon.processRawSpecs(this.toSpec(), frontendOnly = false)

    val pane = DefaultPlotPanelJfx(
        processedSpec = processedSpec,
        preserveAspectRatio = false,
        preferredSizeFromPlot = true,
        repaintDelay = 5
    ) {}

    val swingNode = SwingNode().apply {
        SwingUtilities.invokeLater { content = pane }
    }

    val stackPane = StackPane().apply {
        setPrefSize(1200.0, 800.0)
        padding = Insets(4.0)
        border = Border(
            BorderStroke(
                Color.DODGERBLUE,
                Color.DODGERBLUE,
                Color.DODGERBLUE,
                Color.DODGERBLUE,
                BorderStrokeStyle.SOLID,
                BorderStrokeStyle.SOLID,
                BorderStrokeStyle.SOLID,
                BorderStrokeStyle.SOLID,
                CornerRadii(4.0),
                BorderWidths(2.0, 2.0, 2.0, 2.0),
                null
            )
        )
        children.add(swingNode)
    }

    show(stackPane)
}