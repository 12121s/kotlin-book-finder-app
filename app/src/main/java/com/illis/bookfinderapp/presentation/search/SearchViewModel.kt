package com.illis.bookfinderapp.presentation.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illis.bookfinderapp.data.Resource
import com.illis.bookfinderapp.data.model.Books
import com.illis.bookfinderapp.domain.usecase.SearchUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchUseCase: SearchUseCase
) : ViewModel() {
    val booksResponse: MutableLiveData<Books?> = MutableLiveData()

    fun getBooks(content: String) = viewModelScope.launch(Dispatchers.IO) {
            val response: Resource<Books> = searchUseCase(content)
            booksResponse.postValue(response.data)
        }
}