package com.illis.bookfinderapp.data.interceptor

import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.net.SocketTimeoutException


class ErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        try {
            val response = chain.proceed(request)
            val bodyString = response.body!!.string()

            return response.newBuilder()
                .body(bodyString.toResponseBody(response.body?.contentType()))
                .build()
        } catch (e: Exception) {
            var msg = ""
            var interceptorCode = 0
            when (e) {
                is SocketTimeoutException -> {
                    msg = "Socket timeout error"
                    interceptorCode = 5060
                }
                else -> {
                    msg = "unknown error $e"
                    interceptorCode = 4060
                }
            }
            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_2)
                .code(interceptorCode)
                .message(msg)
                .body("{${e}}".toResponseBody(null)).build()
        }
    }

}