import org.gradle.internal.os.OperatingSystem

plugins {
    application
    kotlin("jvm") version "1.4.32"
    id("org.beryx.jlink") version "2.23.7"
    id("extra-java-module-info")
}

group = "com.icuxika"
version = "1.0.0"

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
val compileJava: JavaCompile by tasks
compileJava.destinationDir = compileKotlin.destinationDir

application {
    applicationName = "JavaFXSample"
    mainModule.set("sample")
    mainClass.set("com.icuxika.MainAppKt")
    applicationDefaultJvmArgs = listOf(
        // Java16的ZGC似乎有大幅度优化
        "-XX:+UseZGC",
        // 当遇到空指针异常时显示更详细的信息
        "-XX:+ShowCodeDetailsInExceptionMessages",
        "-Dsun.java2d.opengl=true",
        // 不添加此参数，打包成exe后，https协议的网络图片资源无法加载
        "-Dhttps.protocols=TLSv1.1,TLSv1.2",
        "--add-exports=javafx.controls/com.sun.javafx.scene.control.behavior=com.jfoenix"
    )
}

val platform = when {
    OperatingSystem.current().isWindows -> {
        "win"
    }
    OperatingSystem.current().isMacOsX -> {
        "mac"
    }
    else -> {
        "linux"
    }
}

jlink {
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))

    launcher {
        name = application.applicationName
        imageName.set(application.applicationName)
    }

    imageZip.set(project.file("${project.buildDir}/image-zip/JavaFXSample.zip"))

    jpackage {
        outputDir = "build-package"
        imageName = application.applicationName
        skipInstaller = false
        installerName = application.applicationName
        appVersion = version.toString()

        application.applicationDefaultJvmArgs.forEach {
            jvmArgs.add(it)
        }

        when {
            OperatingSystem.current().isWindows -> {
                icon = "src/main/resources/application.ico"
                installerOptions =
                    listOf("--win-dir-chooser", "--win-menu", "--win-shortcut", "--install-dir", "Shimmer")
            }
            OperatingSystem.current().isMacOsX -> {
                icon = "src/main/resources/application.icns"
            }
            else -> {
                icon = "src/main/resources/application.png"
                installerOptions = listOf(
                    "--linux-deb-maintainer",
                    "icuxika@outlook.com",
                    "--linux-menu-group",
                    application.applicationName,
                    "--linux-shortcut"
                )
            }
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    implementation("org.openjfx:javafx-base:16:${platform}")
    implementation("org.openjfx:javafx-controls:16:${platform}")
    implementation("org.openjfx:javafx-graphics:16:${platform}")
    implementation("org.openjfx:javafx-fxml:16:${platform}")
    implementation("org.openjfx:javafx-swing:16:${platform}")
    implementation("org.openjfx:javafx-media:16:${platform}")
    implementation("org.openjfx:javafx-web:16:${platform}")
    implementation("com.jfoenix:jfoenix:9.0.10")

    implementation("org.jetbrains.lets-plot:lets-plot-jfx:2.0.2") {
        exclude(group = "org.jetbrains.lets-plot", module = "lets-plot-common")
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-html-jvm")
    }
    implementation("org.jetbrains.lets-plot:lets-plot-image-export:2.0.2")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.3") {
        exclude(group = "org.jetbrains.lets-plot", module = "lets-plot-common")
    }
    implementation("org.jetbrains.lets-plot:lets-plot-kotlin-api:2.0.1") {
        exclude(group = "org.jetbrains.lets-plot", module = "lets-plot-common")
    }
    implementation("io.github.microutils:kotlin-logging:2.0.6")
    implementation("org.slf4j:slf4j-simple:1.7.30")
}

extraJavaModuleInfo {
    module("kotlin-stdlib-common-1.4.32.jar", "kotlin.stdlib.common", "1.4.32")
    module("annotations-13.0.jar", "annotations", "13.0")
    module("lets-plot-kotlin-api-2.0.1.jar", "lets.plot.kotlin.api", "2.0.1") {
        requires("kotlin.stdlib")

        exports("jetbrains.letsPlot")
        exports("jetbrains.letsPlot.geom")
        exports("jetbrains.letsPlot.intern")
        exports("jetbrains.letsPlot.intern.layer.geom")
    }
    module("lets-plot-jfx-2.0.2.jar", "lets.plot.jfx", "2.0.2") {
        requires("kotlin.stdlib")
        requires("kotlin.logging.jvm")
        requires("org.slf4j")
        requires("java.desktop")
        requires("javafx.graphics")
        requires("javafx.swing")

        exports("jetbrains.datalore.plot")
        exports("jetbrains.datalore.vis.swing.jfx")
    }
    module("lets-plot-image-export-2.0.2.jar", "lets.plot.image.export", "2.0.2")
    module("kotlin-logging-jvm-2.0.5.jar", "kotlin.logging.jvm", "2.0.5")
    module("kotlinx-html-jvm-0.7.3.jar", "kotlinx.html.jvm", "0.7.3")
    module("batik-codec-1.12.jar", "batik.codec", "1.12")
    module("batik-transcoder-1.12.jar", "batik.transcoder", "1.12")
    module("batik-bridge-1.12.jar", "batik.bridge", "1.12")
    module("batik-script-1.12.jar", "batik.script", "1.12")
    module("batik-anim-1.12.jar", "batik.anim", "1.12")
    module("batik-gvt-1.12.jar", "batik.gvt", "1.12")
    module("batik-svggen-1.12.jar", "batik.svggen", "1.12")
    module("batik-svg-dom-1.12.jar", "batik.svg.dom", "1.12")
    module("batik-parser-1.12.jar", "batik.parser", "1.12")
    module("batik-awt-util-1.12.jar", "batik.awt.util", "1.12")
    module("batik-dom-1.12.jar", "batik.dom", "1.12")
    module("batik-xml-1.12.jar", "batik.xml", "1.12")
    module("batik-css-1.12.jar", "batik.css", "1.12")
    module("batik-util-1.12.jar", "batik.util", "1.12")
    module("batik-ext-1.12.jar", "batik.ext", "1.12")
    module("batik-constants-1.12.jar", "batik.constants", "1.12")
    module("batik-i18n-1.12.jar", "batik.i18n", "1.12")
    module("xml-apis-ext-1.3.04.jar", "xml.apis.ext", "1.3.04")
    module("xml-apis-1.4.01.jar", "xml.apis", "1.4.01")
    module("imageio-tiff-3.6.4.jar", "imageio.tiff", "3.6.4")
    module("imageio-metadata-3.6.4.jar", "imageio.metadata", "3.6.4")
    module("imageio-core-3.6.4.jar", "imageio.core", "3.6.4")
    module("common-image-3.6.4.jar", "common.image", "3.6.4")
    module("common-io-3.6.4.jar", "common.io", "3.6.4")
    module("common-lang-3.6.4.jar", "common.lang", "3.6.4")
    module("commons-io-1.3.1.jar", "commons.io", "1.3.1")
    module("commons-logging-1.0.4.jar", "commons.logging", "1.0.4")
    module("xmlgraphics-commons-2.4.jar", "xmlgraphics.commons", "2.4")
    module("xalan-2.7.2.jar", "xalan", "2.7.2")
    module("serializer-2.7.2.jar", "serializer", "2.7.2")
    module("kotlin-logging-jvm-2.0.6.jar", "kotlin.logging.jvm", "2.0.6") {
        requires("kotlin.stdlib")
        requires("org.slf4j")
    }
}

tasks.compileJava {
    options.javaModuleVersion.set(provider { project.version as String })
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}