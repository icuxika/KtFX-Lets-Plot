package com.icuxika

import com.icuxika.control.LetsPlotContainer
import com.icuxika.control.LetsPlotNode
import javafx.application.Application
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.ScrollPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import jetbrains.letsPlot.Stat
import jetbrains.letsPlot.geom.geomDensity
import jetbrains.letsPlot.geom.geomHistogram
import jetbrains.letsPlot.geom.geomPoint
import jetbrains.letsPlot.ggplot
import jetbrains.letsPlot.intern.toSpec
import jetbrains.letsPlot.letsPlot
import krangl.DataFrame
import krangl.GuessSpec
import krangl.readDelim
import krangl.toMap
import org.apache.commons.csv.CSVFormat

fun main(args: Array<String>) {
    Application.launch(MainApp::class.java, *args)
}

class MainApp : Application() {
    override fun start(primaryStage: Stage?) {
        primaryStage?.let { stage ->
            val root = ScrollPane()
            val plotsContainer = FlowPane().apply {
                alignment = Pos.CENTER
                hgap = 4.0
                vgap = 4.0
                children.addAll(createPlots())
            }
            val layoutAnimator = LayoutAnimator()
            layoutAnimator.observe(plotsContainer.children)
            root.content = plotsContainer
            plotsContainer.prefWidthProperty().bind(root.widthProperty())
            stage.scene = Scene(root, 1200.0, 760.0)
            stage.show()
        }
    }

    private fun createPlots(): List<StackPane> {
        return listOf(
            LetsPlotContainer(object : LetsPlotNode() {
                override fun prepareSpecData(): MutableMap<String, Any> {
                    val mpg = DataFrame.readDelim(
                        javaClass.getResourceAsStream("/mpg.csv"),
                        CSVFormat.DEFAULT.withHeader(),
                        false,
                        GuessSpec()
                    )
                    val p = ggplot(mpg.toMap()) + geomPoint(
                        stat = Stat.count(),
                    ) {
                        x = "displ"
                        y = "hwy"
                        color = "..count.."
                        size = "..count.."
                    }
                    return p.toSpec()
                }
            }),
            LetsPlotContainer(object : LetsPlotNode() {
                override fun prepareSpecData(): MutableMap<String, Any> {
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
                    return plots[selectedPlotKey]!!.toSpec()
                }
            }),
            LetsPlotContainer(object : LetsPlotNode() {
                override fun prepareSpecData(): MutableMap<String, Any> {
                    val mpg = DataFrame.readDelim(
                        javaClass.getResourceAsStream("/mpg.csv"),
                        CSVFormat.DEFAULT.withHeader(),
                        false,
                        GuessSpec()
                    )
                    val p = ggplot(mpg.toMap()) + geomHistogram(
                        data = mpg.toMap(),
                        binWidth = 0.5
                    ) {
                        x = "displ"
                    }
                    return p.toSpec()
                }
            }),
            LetsPlotContainer(object : LetsPlotNode() {
                override fun prepareSpecData(): MutableMap<String, Any> {
                    val mpg = DataFrame.readDelim(
                        javaClass.getResourceAsStream("/mpg.csv"),
                        CSVFormat.DEFAULT.withHeader(),
                        false,
                        GuessSpec()
                    )
                    val p = ggplot(mpg.toMap()) {
                        x = "displ"
                    } + geomPoint {
                        x = "displ"
                        y = "hwy"
                        color = "cyl"
                    }
                    return p.toSpec()
                }
            }),
        )
    }

//    private fun createPlotWithNumpy() {
//        LibraryLoader.setPythonConfig("C:\\ProgramData\\Anaconda3")
//        val a = arange(15).reshape(3, 5)
//        println(a)
//    }
}