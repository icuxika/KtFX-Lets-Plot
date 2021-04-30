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
import jetbrains.letsPlot.geom.geomDensity
import jetbrains.letsPlot.geom.geomHistogram
import jetbrains.letsPlot.intern.toSpec
import jetbrains.letsPlot.letsPlot
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
            createPlot(swingNode)

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
}