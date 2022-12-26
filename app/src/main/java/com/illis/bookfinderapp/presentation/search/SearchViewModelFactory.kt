package com.illis.bookfinderapp.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.illis.bookfinderapp.domain.usecase.SearchUseCase

class SearchViewModelFactory(
    private val searchUseCase: SearchUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(searchUseCase) as T
    }
}