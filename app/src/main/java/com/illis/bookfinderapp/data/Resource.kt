package com.illis.bookfinderapp.data

import retrofit2.Response

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)

    companion object {
        fun <ResponseType> responseToResource(response: Response<ResponseType>) : Resource<ResponseType> {
            if(response.isSuccessful){
                response.body()?.let {result->
                    return Success(result)
                }
            }
            return Error(response.message())
        }
    }
}
