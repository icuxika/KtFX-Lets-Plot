package com.icuxika

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXTabPane
import javafx.application.Application
import javafx.beans.property.SimpleStringProperty
import javafx.embed.swing.SwingNode
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.StackPane
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
import krangl.DataFrame
import krangl.readCSV
import krangl.toMap
import org.jetbrains.numkt.LibraryLoader
import org.jetbrains.numkt.arange
import org.jetbrains.numkt.core.reshape
import java.io.File
import javax.swing.SwingUtilities

fun main(args: Array<String>) {
    Application.launch(MainApp::class.java, *args)
}

class MainApp : Application() {
    override fun start(primaryStage: Stage?) {
        primaryStage?.let { stage ->
            val jfxTabPane = JFXTabPane()

            val buttonNameProperty = SimpleStringProperty("Hello, world")
            val button = JFXButton().apply {
                textProperty().bind(buttonNameProperty)
                buttonType = JFXButton.ButtonType.RAISED
                textFill = Color.WHITE
                background = Background(BackgroundFill(Color.DODGERBLUE, CornerRadii(4.0), Insets.EMPTY))
            }

            val swingNode = SwingNode()
//            createPlot(swingNode)
            createPlotWithPandas(swingNode)

            stage.scene = Scene(StackPane(swingNode), 600.0, 400.0)
            stage.show()
        }
    }

    private fun createPlot(swingNode: SwingNode) {
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

        val processedSpec =
            MonolithicCommon.processRawSpecs(plots[selectedPlotKey]!!.toSpec(), frontendOnly = false)
        val pane = DefaultPlotPanelJfx(
            processedSpec = processedSpec,
            preserveAspectRatio = true,
            preferredSizeFromPlot = false,
            repaintDelay = 10
        ) {
            println(it)
        }

        SwingUtilities.invokeLater {
            swingNode.content = pane
        }
    }

    private fun createPlotWithPandas(swingNode: SwingNode) {
        javaClass.getResource("/mpg.csv")?.let { url ->
            val mpg = DataFrame.readCSV(File(url.toURI().path))
            val p = ggplot(mpg.toMap()) + geomPoint(
                stat = Stat.count(),
            ) {
                x = "displ"
                y = "hwy"
                color = "..count.."
                size = "..count.."
            }
            val processedSpec = MonolithicCommon.processRawSpecs(p.toSpec(), frontendOnly = false)
            val pane = DefaultPlotPanelJfx(
                processedSpec = processedSpec,
                preserveAspectRatio = true,
                preferredSizeFromPlot = false,
                repaintDelay = 10
            ) {
                println(it)
            }

            SwingUtilities.invokeLater {
                swingNode.content = pane
            }

//            createPlotWithNumpy()
        }
    }

    private fun createPlotWithNumpy() {
        LibraryLoader.setPythonConfig("C:\\ProgramData\\Anaconda3")
        val a = arange(15).reshape(3, 5)
        println(a)
    }
}