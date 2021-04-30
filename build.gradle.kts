import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.4.32"
    id("org.openjfx.javafxplugin") version "0.0.9"
    id("org.beryx.runtime") version "1.12.4"
}

group = "com.icuxika"
version = "1.0.0"

val compileKotlin: KotlinCompile by tasks
val compileJava: JavaCompile by tasks
compileJava.destinationDir = compileKotlin.destinationDir

application {
    applicationName = "KtFxLetsPlot"
    mainClass.set("com.icuxika.MainAppKt")
    applicationDefaultJvmArgs = listOf(
        // Java16的ZGC似乎有大幅度优化
        "-XX:+UseZGC",
        // 当遇到空指针异常时显示更详细的信息
        "-XX:+ShowCodeDetailsInExceptionMessages",
        "-Dsun.java2d.opengl=true",
        // 不添加此参数，打包成exe后，https协议的网络图片资源无法加载
        "-Dhttps.protocols=TLSv1.1,TLSv1.2"
    )
}

repositories {
    mavenCentral()
    maven("https://repo.kotlin.link")
    maven("https://dl.bintray.com/kotlin/kotlin-datascience")
}

javafx {
    version = "16"
    modules("javafx.controls", "javafx.fxml", "javafx.swing", "javafx.media", "javafx.web")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

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

    implementation("com.github.holgerbrandl:krangl:0.16.2")
    implementation("space.kscience:kmath-core:0.3.0-dev-3")
    implementation("org.jetbrains:kotlin-numpy:0.1.5")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

runtime {
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    modules.set(listOf("jdk.unsupported", "java.desktop", "jdk.unsupported.desktop"))

    launcher {
        noConsole = true
    }

    jpackage {
        imageName = application.applicationName

        val currentOS = OperatingSystem.current()
        if (currentOS.isMacOsX) {
            imageOptions.addAll(listOf("--icon", "src/main/resources/application.icns"))
        }
        if (currentOS.isWindows) {
            imageOptions.addAll(listOf("--icon", "src/main/resources/application.ico"))
            installerOptions.addAll(
                listOf(
                    "--win-dir-chooser",
                    "--win-menu",
                    "--win-shortcut",
                    "--install-dir",
                    application.applicationName
                )
            )
        }
        if (currentOS.isLinux) {
            imageOptions.addAll(listOf("--icon", "src/main/resources/application.png"))
            installerOptions.addAll(
                listOf(
                    "--linux-deb-maintainer",
                    "icuxika@outlook.com",
                    "--linux-menu-group",
                    application.applicationName,
                    "--linux-shortcut"
                )
            )
        }
    }
}