package com.icuxika.fx

import com.icuxika.extensions.logger
import com.icuxika.fx.annotation.AppFXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import kotlin.reflect.KClass

class AppView<T : Any>(controllerClass: KClass<T>) {

    /**
     * 根节点
     */
    private lateinit var root: Parent

    private lateinit var fxmlLoader: FXMLLoader

    private lateinit var stylesheets: Array<String>

    init {
        controllerClass.annotations.find {
            it.annotationClass == AppFXML::class
        }?.let { annotation ->
            (annotation as AppFXML).let { appFXML ->
                val fxml = appFXML.fxml
                if (fxml.isNotBlank()) {
                    fxmlLoader = FXMLLoader()
                    fxmlLoader.location = AppResource.loadResource(fxml)
                    root = fxmlLoader.load()
                } else {
                    throw IllegalArgumentException("@AppFXML注解值[fxml]为空")
                }
                stylesheets = appFXML.stylesheets
            }
        } ?: run {
            throw IllegalArgumentException("未检测到@AppFXML注解")
        }
    }

    fun root() = root

    fun controller(): T = fxmlLoader.getController()

    /**
     * 加载css样式文件
     */
    private fun assembleStylesheets(scene: Scene) {
        if (stylesheets.isNotEmpty()) {
            stylesheets.forEach {
                AppResource.loadResource(it)?.let { url ->
                    scene.stylesheets.add(url.toExternalForm())
                }
            }
        }
    }

    private fun Stage.defaultScene() = apply {
        this.scene = Scene(root).apply {
            this.fill = null
            assembleStylesheets(this)
        }
    }

    fun show(title: String) = Stage().apply { this.title = title }.defaultScene().show()
    fun show(stage: Stage) = stage.defaultScene().show()

    companion object {
        val L = logger()
    }
}