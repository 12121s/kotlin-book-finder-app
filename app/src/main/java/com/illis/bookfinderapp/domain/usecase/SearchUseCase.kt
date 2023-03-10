package com.illis.bookfinderapp.domain.usecase

import com.illis.bookfinderapp.data.Resource
import com.illis.bookfinderapp.data.model.Books
import com.illis.bookfinderapp.domain.repository.BookRepository

class SearchUseCase(private val bookRepository: BookRepository) {
    suspend operator fun invoke(searchText: String, page: Int) : Resource<Books> {
        return bookRepository.search(searchText, page)
    }
}