import org.gradle.internal.os.OperatingSystem

plugins {
    application
    kotlin("jvm") version "1.5.31"
    id("org.openjfx.javafxplugin") version "0.0.9"
    id("org.beryx.runtime") version "1.12.4"
}

group = "com.icuxika"
version = "1.0.0"

application {
    applicationName = "KtFxLetsPlot"
    mainClass.set("com.icuxika.MainAppKt")
    applicationDefaultJvmArgs = listOf(
        "-XX:+UseZGC",
        "-XX:+ShowCodeDetailsInExceptionMessages",
        "-Dsun.java2d.opengl=true",
        "-Dhttps.protocols=TLSv1.1,TLSv1.2",
        "-Dkotlinx.coroutines.debug"
    )
}

repositories {
    mavenCentral()
}

javafx {
    version = "17"
    modules("javafx.controls", "javafx.fxml", "javafx.swing", "javafx.media", "javafx.web")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.5.2")

    implementation("org.jetbrains.lets-plot:lets-plot-kotlin-jvm:3.0.2")
    implementation("org.jetbrains.lets-plot:lets-plot-jfx:2.1.0")
    implementation("com.github.holgerbrandl:krangl:0.17") {
        exclude(group = "org.jetbrains.lets-plot", module = "lets-plot-kotlin-jvm")
    }

    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.14.0")
    implementation("org.apache.logging.log4j:log4j-api:2.14.0")
    implementation("org.apache.logging.log4j:log4j-core:2.14.0")

    // log4j yaml config depends on jackson
    implementation("com.fasterxml.jackson.core:jackson-core:2.12.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.0")
}

tasks.compileJava {
    options.release.set(11)
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

tasks.test {
    useJUnitPlatform()
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