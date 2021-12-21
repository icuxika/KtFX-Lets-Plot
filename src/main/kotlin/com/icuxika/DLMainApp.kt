package com.icuxika

import com.icuxika.examples.visualization.exLeNetMnistVisualization
import com.icuxika.extensions.logger
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.kotlinx.dl.api.core.Sequential
import org.jetbrains.kotlinx.dl.api.core.WritingMode
import org.jetbrains.kotlinx.dl.api.core.layer.core.Dense
import org.jetbrains.kotlinx.dl.api.core.layer.core.Input
import org.jetbrains.kotlinx.dl.api.core.layer.reshaping.Flatten
import org.jetbrains.kotlinx.dl.api.core.loss.Losses
import org.jetbrains.kotlinx.dl.api.core.metric.Metrics
import org.jetbrains.kotlinx.dl.api.core.optimizer.Adam
import org.jetbrains.kotlinx.dl.api.core.summary.printSummary
import org.jetbrains.kotlinx.dl.api.inference.TensorFlowInferenceModel
import org.jetbrains.kotlinx.dl.dataset.fashionMnist
import java.io.File

fun main(args: Array<String>) {
    Application.launch(DLMainApp::class.java, *args)
//    exLeNetMnistVisualization()
}

class DLMainApp : Application() {

    override fun start(primaryStage: Stage?) {
        primaryStage?.let { stage ->
            stage.scene = Scene(Button("RUN").apply {
                onAction = EventHandler {
                    CoroutineScope(context = Dispatchers.IO).launch {
                        exLeNetMnistVisualization()
                    }
                }
            })
            stage.show()
        }
    }

    companion object {
        val L = logger()
    }
}

fun startGuideTrain() {
    val model = Sequential.of(
        Input(28, 28, 1),
        Flatten(),
        Dense(300),
        Dense(100),
        Dense(10)
    )
    val (train, test) = fashionMnist()
    model.use {
        it.compile(
            optimizer = Adam(),
            loss = Losses.SOFT_MAX_CROSS_ENTROPY_WITH_LOGITS,
            metric = Metrics.ACCURACY
        )
        it.printSummary()
        // You can think of the training process as "fitting" the model to describe the given data :)
        it.fit(
            dataset = train,
            epochs = 10,
            batchSize = 100
        )
        val accuracy = it.evaluate(dataset = test, batchSize = 100).metrics[Metrics.ACCURACY]
        println("Accuracy: $accuracy")
        it.save(File("model/my_model"), writingMode = WritingMode.OVERRIDE)
    }
}

fun startGuideUse() {
    val stringLabels = mapOf(
        0 to "T-shirt/top",
        1 to "Trousers",
        2 to "Pullover",
        3 to "Dress",
        4 to "Coat",
        5 to "Sandals",
        6 to "Shirt",
        7 to "Sneakers",
        8 to "Bag",
        9 to "Ankle boots"
    )
    val (train, test) = fashionMnist()
    TensorFlowInferenceModel.load(File("model/my_model")).use {
        it.reshape(28, 28, 1)
        val prediction = it.predict(test.getX(0))
        val actualLabel = test.getY(0)

        println("Predicted label is: $prediction. This corresponds to class ${stringLabels[prediction]}.")
        println("Actual label is: $actualLabel.")
    }
}