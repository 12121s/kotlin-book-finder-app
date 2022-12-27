package com.illis.bookfinderapp.data.api

import com.illis.bookfinderapp.data.model.Books
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface BooksApiInterface {
    @Headers("Accept: application/json", "Content-Type: application/json; charset=utf-8")
    @GET("/books/v1/volumes")
    suspend fun searchBooks(
        @Query("q", encoded = true) inTitle: String,
        @Query("startIndex") startIndex: Int? = null,
        @Query("maxResults") maxResults: Int,
        @Query("orderBy") orderBy : String = "newest"
    ): Response<Books>
}
