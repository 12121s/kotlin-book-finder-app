package com.illis.bookfinderapp.data.repository

import com.illis.bookfinderapp.consts.ServerConsts
import com.illis.bookfinderapp.data.Resource
import com.illis.bookfinderapp.data.api.RetrofitInstance
import com.illis.bookfinderapp.data.model.Books
import com.illis.bookfinderapp.domain.repository.BookRepository

class BookRepositoryImpl : BookRepository {

    override suspend fun search(searchText: String, page: Int): Resource<Books> {
        return Resource.responseToResource(
            RetrofitInstance.booksApi.searchBooks(
                inTitle = "$searchText+intitle:$searchText", // title 에서만 검색
                startIndex = page * ServerConsts.BOOKS_API_MAX_RESULTS,
                maxResults = ServerConsts.BOOKS_API_MAX_RESULTS))
    }
}