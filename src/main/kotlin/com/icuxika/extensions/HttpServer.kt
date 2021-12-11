package com.icuxika.extensions

import com.sun.net.httpserver.HttpContext
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress

fun HttpServer.initContext(block: HttpServer.() -> Unit) = this.block()

fun HttpServer.applyContext(path: String, block: HttpExchange.() -> Unit): HttpContext =
    createContext(path) { block.invoke(it) }

fun HttpExchange.reply(message: String) {
    sendResponseHeaders(200, 0)
    responseBody.write(message.toByteArray())
}

fun HttpExchange.configure(block: (uri: String, param: String) -> Unit) {
    responseHeaders.add("Access-Control-Allow-Origin", "*")
    responseHeaders.add("Access-Control-Allow-Headers", "*")
    responseHeaders.add("Access-Control-Allow-Methods", "*")
    responseHeaders.add("Access-Control-Allow-Credentials", "true")

    println(requestMethod)
    println(requestHeaders)
    println(requestURI)
    println(requestBody)

    reply("message")

    responseBody.flush()
    responseBody.close()
}

/**
 * 一个简易的内置服务器
 */
fun startLocalServer() {
    HttpServer.create(InetSocketAddress(9999), 0).apply {
        initContext {
            applyContext("/test1") {
                configure { uri, param ->
                    println(uri)
                    println(param)
                }
            }
            applyContext("/test2") {
                configure { uri, param ->
                    println(uri)
                    println(param)
                }
            }
        }
    }.start()
}