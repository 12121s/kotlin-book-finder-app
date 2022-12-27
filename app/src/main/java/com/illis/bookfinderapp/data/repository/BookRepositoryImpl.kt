package com.illis.bookfinderapp.data.repository

import com.illis.bookfinderapp.consts.ServerConsts
import com.illis.bookfinderapp.data.Resource
import com.illis.bookfinderapp.data.api.RetrofitInstance
import com.illis.bookfinderapp.data.model.Books
import com.illis.bookfinderapp.domain.repository.BookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder

class BookRepositoryImpl : BookRepository {

    override suspend fun search(searchText: String, page: Int): Resource<Books> {
        // '하얀 늑대'처럼 공백 있는 데이터 검색시 '하얀', '늑대' 각각 검색한 것 같은 결과가 나와 직접 인코딩
        val encodedText = withContext(Dispatchers.IO) {
            URLEncoder.encode(searchText.replace(" ", ""), "UTF-8")
        }

        return Resource.responseToResource(
            RetrofitInstance.booksApi.searchBooks(
                inTitle = "$encodedText+intitle:$encodedText", // title 에서만 검색
                startIndex = page * ServerConsts.BOOKS_API_MAX_RESULTS,
                maxResults = ServerConsts.BOOKS_API_MAX_RESULTS))
    }
}