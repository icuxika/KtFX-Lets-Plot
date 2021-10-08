package com.icuxika

import com.icuxika.extensions.logger
import javafx.application.Application
import javafx.embed.swing.SwingNode
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.ScrollPane
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.stage.Stage
import jetbrains.datalore.plot.MonolithicCommon
import jetbrains.datalore.vis.swing.jfx.DefaultPlotPanelJfx
import jetbrains.letsPlot.Stat
import jetbrains.letsPlot.geom.geomDensity
import jetbrains.letsPlot.geom.geomHistogram
import jetbrains.letsPlot.geom.geomPoint
import jetbrains.letsPlot.ggplot
import jetbrains.letsPlot.intern.toSpec
import jetbrains.letsPlot.letsPlot
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import krangl.DataFrame
import krangl.GuessSpec
import krangl.readDelim
import krangl.toMap
import org.apache.commons.csv.CSVFormat
import javax.swing.SwingUtilities

fun main(args: Array<String>) {
    Application.launch(MainApp::class.java, *args)
}

fun DataFrame.Companion.readCSVAsStream(csvPath: String): DataFrame? {
    MainApp::class.java.getResourceAsStream(csvPath)?.let { inputStream ->
        return readDelim(inputStream, CSVFormat.DEFAULT.withHeader(), false, GuessSpec())
    }
    return null
}

class MainApp : Application() {

    override fun start(primaryStage: Stage?) {
        L.info("应用程序启动")
        primaryStage?.let { stage ->
            val root = ScrollPane()
            val plotsContainer = FlowPane().apply {
                alignment = Pos.CENTER
                hgap = 4.0
                vgap = 4.0
            }
            val layoutAnimator = LayoutAnimator()
            layoutAnimator.observe(plotsContainer.children)
            root.content = plotsContainer
            plotsContainer.prefWidthProperty().bind(root.widthProperty())
            stage.scene = Scene(root, 1200.0, 760.0)
            stage.show()

            plotsContainer.addPlot {
                val mpg = DataFrame.readCSVAsStream("/mpg.csv")
                val p = ggplot(mpg!!.toMap()) + geomPoint(
                    stat = Stat.count(),
                ) {
                    x = "displ"
                    y = "hwy"
                    color = "..count.."
                    size = "..count.."
                }
                return@addPlot p.toSpec()
            }

            plotsContainer.addPlot {
                val rand = java.util.Random()
                val n = 200
                val data = mapOf<String, Any>(
                    "x" to List(n) { rand.nextGaussian() }
                )

                val plots = mapOf(
                    "Density" to letsPlot(data) + geomDensity(
                        color = "dark-green",
                        fill = "green",
                        alpha = .3,
                        size = 2.0
                    ) { x = "x" },
                    "Count" to letsPlot(data) + geomHistogram(
                        color = "dark-green",
                        fill = "green",
                        alpha = .3,
                        size = 2.0
                    ) { x = "x" },

                    )

                val selectedPlotKey = plots.keys.first()
                return@addPlot plots[selectedPlotKey]!!.toSpec()
            }

            plotsContainer.addPlot {
                val mpg = DataFrame.readCSVAsStream("/mpg.csv")
                val p = ggplot(mpg!!.toMap()) + geomHistogram(
                    data = mpg.toMap(),
                    binWidth = 0.5
                ) {
                    x = "displ"
                }
                return@addPlot p.toSpec()
            }

            plotsContainer.addPlot {
                val mpg = DataFrame.readCSVAsStream("/mpg.csv")
                val p = ggplot(mpg!!.toMap()) {
                    x = "displ"
                } + geomPoint {
                    x = "displ"
                    y = "hwy"
                    color = "cyl"
                }
                return@addPlot p.toSpec()
            }
        }
    }

    private fun Pane.addPlot(block: () -> MutableMap<String, Any>) {
        CoroutineScope(context = Dispatchers.JavaFx + CoroutineName("JavaFx")).launch {
            L.info(Thread.currentThread().name)
            children.add(buildPlotPaneFromSpec(block.invoke()))
        }
    }

    private suspend fun buildPlotPaneFromSpec(spec: MutableMap<String, Any>): Node =
        withContext(Dispatchers.Default + CoroutineName("Default")) {
            // 主动延迟不定长时间来演示协程的效果
            val time = (1..10).random().toLong()
            L.info(Thread.currentThread().name + " 主动延迟 " + time + "s")
            delay(time * 1000)

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

            StackPane().apply {
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
        }

    companion object {
        val L = logger()
    }
}