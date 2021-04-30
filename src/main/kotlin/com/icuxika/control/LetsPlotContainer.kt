package com.icuxika.control

import javafx.geometry.Insets
import javafx.scene.layout.*
import javafx.scene.paint.Color

class LetsPlotContainer(node: LetsPlotNode) : StackPane() {
    init {
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
        node.generate()
        children.add(node)
    }
}