package com.icuxika.examples.visualization

import com.icuxika.visualization.showFX
import org.jetbrains.kotlinx.dl.api.core.WritingMode
import org.jetbrains.kotlinx.dl.api.core.layer.convolutional.Conv2D
import org.jetbrains.kotlinx.dl.api.core.loss.Losses
import org.jetbrains.kotlinx.dl.api.core.metric.Metrics
import org.jetbrains.kotlinx.dl.api.core.optimizer.Adam
import org.jetbrains.kotlinx.dl.api.core.summary.logSummary
import org.jetbrains.kotlinx.dl.api.inference.TensorFlowInferenceModel
import org.jetbrains.kotlinx.dl.dataset.mnist
import org.jetbrains.kotlinx.dl.visualization.letsplot.columnPlot
import org.jetbrains.kotlinx.dl.visualization.letsplot.filtersPlot
import org.jetbrains.kotlinx.dl.visualization.letsplot.flattenImagePlot
import org.jetbrains.kotlinx.dl.visualization.letsplot.modelActivationOnLayersPlot
import org.jetbrains.kotlinx.dl.visualization.swing.drawActivations
import org.jetbrains.kotlinx.dl.visualization.swing.drawFilters
import java.io.File

private const val EPOCHS = 1
private const val TRAINING_BATCH_SIZE = 500
private const val TEST_BATCH_SIZE = 1000

private const val MODEL_DIRECTORY = "model/examples/visualization/LeNetMnistVisualization"

/**
 * This examples demonstrates model activations and Conv2D filters visualisation.
 *
 * Model is trained on Mnist dataset.
 */
private fun example() {

    val (train, test) = mnist()

    val sampleIndex = 42
    val x = test.getX(sampleIndex)
    val y = test.getY(sampleIndex).toInt()

    lenet5().use {

        it.compile(
            optimizer = Adam(),
            loss = Losses.SOFT_MAX_CROSS_ENTROPY_WITH_LOGITS,
            metric = Metrics.ACCURACY
        )

        it.logSummary()

        it.fit(
            dataset = train,
            validationRate = 0.1,
            epochs = EPOCHS,
            trainBatchSize = TRAINING_BATCH_SIZE,
            validationBatchSize = TEST_BATCH_SIZE
        )

        val numbersPlots = List(3) { imageIndex ->
            flattenImagePlot(imageIndex, test, it::predict)
        }
        columnPlot(numbersPlots, 3, 256).show()

        val accuracy = it.evaluate(dataset = test, batchSize = TEST_BATCH_SIZE).metrics[Metrics.ACCURACY]
        println("Accuracy $accuracy")

        val fstConv2D = it.layers[1] as Conv2D
        val sndConv2D = it.layers[3] as Conv2D

        // lets-plot approach
        filtersPlot(fstConv2D, columns = 16).show()
        filtersPlot(sndConv2D, columns = 16).show()

        // swing approach
        drawFilters(fstConv2D.weights.values.toTypedArray()[0], colorCoefficient = 10.0)
        drawFilters(sndConv2D.weights.values.toTypedArray()[0], colorCoefficient = 10.0)

        val layersActivations = modelActivationOnLayersPlot(it, x)
        val (prediction, activations) = it.predictAndGetActivations(x)
        println("Prediction: $prediction")
        println("Ground Truth: $y")

        // lets-plot approach
        layersActivations[0].show()
        layersActivations[1].show()

        // swing approach
        drawActivations(activations)
    }
}

/**
 * 模型训练和保存
 */
private fun train() {
    val (train, test) = mnist()
    lenet5().use {
        it.compile(
            optimizer = Adam(),
            loss = Losses.SOFT_MAX_CROSS_ENTROPY_WITH_LOGITS,
            metric = Metrics.ACCURACY
        )

        it.logSummary()

        it.fit(
            dataset = train,
            validationRate = 0.1,
            epochs = EPOCHS,
            trainBatchSize = TRAINING_BATCH_SIZE,
            validationBatchSize = TEST_BATCH_SIZE
        )

        val accuracy = it.evaluate(dataset = test, batchSize = TEST_BATCH_SIZE).metrics[Metrics.ACCURACY]
        println("Accuracy $accuracy")
        it.save(File(MODEL_DIRECTORY), writingMode = WritingMode.OVERRIDE)
    }
}

/**
 * 模型加载和简单预测
 */
private fun predict1() {
    val (train, test) = mnist()
    TensorFlowInferenceModel.load(File(MODEL_DIRECTORY)).use {
        it.reshape(28, 28, 1)
        val numberPlots = List(3) { index ->
            val testY = it.predict(test.getX(index))
            val realY = test.getY(index).toInt()
            println("testY: $testY, realY: $realY")
            flattenImagePlot(index, test, it::predict)
        }
        columnPlot(numberPlots, 3, 256).show()
    }
}

/**
 * 模型加载和细节展示
 */
private fun predict2() {
    val (train, test) = mnist()
    val sampleIndex = 42
    val x = test.getX(sampleIndex)
    val y = test.getY(sampleIndex).toInt()
    lenet5().use {
        it.compile(
            optimizer = Adam(),
            loss = Losses.SOFT_MAX_CROSS_ENTROPY_WITH_LOGITS,
            metric = Metrics.ACCURACY
        )
        it.loadWeights(File(MODEL_DIRECTORY))

        val numbersPlots = List(3) { imageIndex ->
            flattenImagePlot(imageIndex, test, it::predict)
        }
//        columnPlot(numbersPlots, 3, 256).showFX("测试集预测")

        val fstConv2D = it.layers[1] as Conv2D
        val sndConv2D = it.layers[3] as Conv2D

        filtersPlot(fstConv2D, columns = 16).showFX("卷积层1 Plot展示")
//        filtersPlot(sndConv2D, columns = 16).showFX("卷积层2 Plot展示")
        filtersPlot(sndConv2D, columns = 16).show()

//        drawConv2DCanvas32(fstConv2D.weights.values.toTypedArray()[0], 10.0).showFX("卷积层1展示")
//        drawConv2DCanvas32(sndConv2D.weights.values.toTypedArray()[0], 10.0).showFX("卷积层2展示")

        val layerActivations = modelActivationOnLayersPlot(it, x)
//        layerActivations[0].showFX("激活层1 Plot展示")
//        layerActivations[1].showFX("激活层2 Plot展示")

        val (prediction, activations) = it.predictAndGetActivations(x)
        println("Prediction: $prediction")
        println("Ground Truth: $y")
//        drawActivationCanvas(activations, 0, 1)?.showFX("激活层1展示")
//        drawActivationCanvas(activations, 1, 2)?.showFX("激活层2展示")
    }
}

fun exLeNetMnistVisualization() {
    predict2()
}
