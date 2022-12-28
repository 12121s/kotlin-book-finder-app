package com.illis.bookfinderapp.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illis.bookfinderapp.consts.ServerConsts
import com.illis.bookfinderapp.data.Resource
import com.illis.bookfinderapp.data.model.Books
import com.illis.bookfinderapp.data.model.VolumeInfo
import com.illis.bookfinderapp.domain.usecase.SearchUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel(private val searchUseCase: SearchUseCase) : ViewModel() {
    val booksResponse = MutableLiveData<List<VolumeInfo>?>()
    val selectedBook = MutableLiveData<VolumeInfo>()
    val volumeCount = MutableLiveData<Int>()

    private var searchText = ""
    private var page = 0 // 현재 페이지
    private var maxPage = 0 // 마지막 페이지

    fun getBooks(searchText: String) = viewModelScope.launch(Dispatchers.IO) {
        // 검색어, 페이지 초기화
        this@SearchViewModel.searchText = searchText
        page = 0
        // TODO Loading state
        val response: Resource<Books> = searchUseCase(searchText, page)
        val bookList = response.data?.bookInfoLists?.map { it.volumeInfo }
        booksResponse.postValue(bookList)
        volumeCount.postValue(response.data?.totalItems)

        maxPage = response.data?.totalItems?.div(ServerConsts.BOOKS_API_MAX_RESULTS) ?: 0
    }

    fun getNextPageBooks() = viewModelScope.launch(Dispatchers.IO) {
        if (page <= maxPage) {
            val response: Resource<Books> = searchUseCase(searchText, ++page)

            val tempBookList = booksResponse.value?.toMutableList()
            val addBookList = response.data?.bookInfoLists?.map { it.volumeInfo }
            if (!addBookList.isNullOrEmpty()) {
                tempBookList?.addAll(addBookList)
                booksResponse.postValue(tempBookList)
            } else { // 더이상 추가할 값이 없으면 서버 요청 안하도록
                maxPage = page - 1
            }
        }
    }
}