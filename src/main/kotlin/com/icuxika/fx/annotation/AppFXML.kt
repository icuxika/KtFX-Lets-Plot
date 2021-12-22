package com.icuxika.fx.annotation

/**
 * @param fxml FXML文件路径
 * @param stylesheets css样式表文件路径集合
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class AppFXML(val fxml: String, val stylesheets: Array<String> = [])
