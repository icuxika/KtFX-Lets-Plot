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
import jetbrains.letsPlot.Figure
import jetbrains.letsPlot.GGBunch
import jetbrains.letsPlot.intern.Plot
import jetbrains.letsPlot.intern.toSpec
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import javax.swing.SwingUtilities
import kotlin.math.max
import kotlin.math.min

internal typealias TensorImageData = Array<Array<Array<FloatArray>>>

private fun show(title: String = "", parent: Parent) {
    CoroutineScope(context = Dispatchers.JavaFx).launch {
        Stage().apply {
            this.title = title
            this.scene = Scene(parent)
        }.show()
    }
}

fun Canvas.showFX(title: String = "") = show(title, StackPane(this))

fun drawConv2DCanvas32(
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

fun drawReluGraphicsCanvas32(dst: TensorImageData): Canvas {
    return Canvas(1200.0, 800.0).apply {
        graphicsContext2D.let { gc ->
            for (k in 0 until 32) {
                for (i in dst[0].indices) {
                    for (j in dst[0][i].indices) {
                        val width = 5.0
                        val height = 5.0
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

                        val float = dst[0][i][j][k]
                        val grey = (min(1.0f, max(float * 4, 0.0f)) * 255).toInt()
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

fun drawReluGraphicsCanvas64(dst: TensorImageData): Canvas {
    return Canvas(1200.0, 800.0).apply {
        graphicsContext2D.let { gc ->
            for (k in 0 until 64) {
                for (i in dst[0].indices) {
                    for (j in dst[0][i].indices) {
                        val width = 7.0
                        val height = 7.0

                        var x = 10 + i * width
                        val y = 10 + j * height + k % 8 * 100 // 14 * width <= 100

                        when (k) {
                            in 8..15 -> {
                                x += 100
                            }
                            in 16..23 -> {
                                x += 100 * 2
                            }
                            in 24..31 -> {
                                x += 100 * 3
                            }
                            in 32..39 -> {
                                x += 100 * 4
                            }
                            in 40..47 -> {
                                x += 100 * 5
                            }
                            in 48..55 -> {
                                x += 100 * 6
                            }
                            in 56..63 -> {
                                x += 100 * 7
                            }
                        }

                        val float = dst[0][i][j][k]
                        val grey = (min(1.0f, max(float, 0.0f)) * 255).toInt()
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

fun drawActivationCanvas(activations: List<*>, dataIndex: Int, canvasIndex: Int): Canvas? {
    @Suppress("UNCHECKED_CAST")
    val dst = activations[dataIndex] as TensorImageData
    return when (canvasIndex) {
        1 -> {
            drawReluGraphicsCanvas32(dst)
        }
        2 -> {
            drawReluGraphicsCanvas64(dst)
        }
        else -> {
            null
        }
    }
}

fun Figure.showFX(title: String = "") {
    ((this as? GGBunch)?.toSpec() ?: (this as? Plot)?.toSpec())?.let { spec ->
        val processedSpec = MonolithicCommon.processRawSpecs(spec, frontendOnly = false)

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

        show(title, stackPane)
    }
}