package com.illis.bookfinderapp.domain.repository

import com.illis.bookfinderapp.data.Resource
import com.illis.bookfinderapp.data.model.Books

interface BookRepository {
    suspend fun search(searchText: String, page: Int) : Resource<Books>
}