package com.illis.bookfinderapp.data.repository

import com.illis.bookfinderapp.data.Resource
import com.illis.bookfinderapp.data.api.RetrofitInstance
import com.illis.bookfinderapp.data.model.Books
import com.illis.bookfinderapp.domain.repository.BookRepository

class BookRepositoryImpl : BookRepository {

    override suspend fun search(content: String): Resource<Books> {
        return Resource.responseToResource(RetrofitInstance.booksApi.searchBooks(content, "10"))
    }
}