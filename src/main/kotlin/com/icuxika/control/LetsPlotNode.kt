package com.icuxika.control

import javafx.embed.swing.SwingNode
import jetbrains.datalore.plot.MonolithicCommon
import jetbrains.datalore.vis.swing.jfx.DefaultPlotPanelJfx
import javax.swing.SwingUtilities

abstract class LetsPlotNode : SwingNode() {

    lateinit var spec: MutableMap<String, Any>

    abstract fun prepareSpecData(): MutableMap<String, Any>

    fun generate() {
        spec = prepareSpecData()
        createNode()
    }

    private fun createNode() {
        val processedSpec = MonolithicCommon.processRawSpecs(spec, frontendOnly = false)
        val pane = DefaultPlotPanelJfx(
            processedSpec = processedSpec,
            preserveAspectRatio = false,
            preferredSizeFromPlot = true,
            repaintDelay = 5
        ) {
            it.forEach(::println)
        }

        SwingUtilities.invokeLater { content = pane }
    }
}