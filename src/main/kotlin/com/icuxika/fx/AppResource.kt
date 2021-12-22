package com.icuxika.fx

import com.icuxika.extensions.logger
import java.net.URL

class AppResource {
    companion object {

        private val L = logger()

        /**
         * 基于 /com/icuxika/fx 路径来加载资源
         */
        fun loadResource(path: String): URL? {
            return AppResource::class.java.getResource(path).also { url: URL? ->
                url?.let {
                    L.trace("$path 加载成功，URL[$it]")
                } ?: run {
                    L.warn("$path 加载失败")
                }
            }
        }
    }
}