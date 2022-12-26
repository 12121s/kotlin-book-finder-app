package com.illis.bookfinderapp.presentation.search

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.illis.bookfinderapp.data.repository.BookRepositoryImpl
import com.illis.bookfinderapp.databinding.ActivitySearchBinding
import com.illis.bookfinderapp.domain.usecase.SearchUseCase

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchViewModel: SearchViewModel

    private val bookListAdapter = BookListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModelFactory = SearchViewModelFactory(SearchUseCase(BookRepositoryImpl()))
        searchViewModel = ViewModelProvider(this, viewModelFactory)[SearchViewModel::class.java]
        searchViewModel.booksResponse.postValue(null) // init

        initSearchLayout()
        setBookList()
    }

    private fun initSearchLayout() {

        val layoutManager = GridLayoutManager(baseContext, 2, RecyclerView.VERTICAL, false)
        binding.searchResult.layoutManager = layoutManager
        binding.searchResult.adapter = bookListAdapter

        val imm = baseContext?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        binding.searchText.requestFocus()
        binding.searchText.postDelayed(Runnable {
            imm?.showSoftInput(binding.searchText, InputMethodManager.SHOW_IMPLICIT)
        }, 100)
        binding.searchText.addTextChangedListener {
            binding.searchBtn.isEnabled = (it.toString().isNotEmpty())
        }

        binding.searchBtn.isEnabled = false
        binding.searchBtn.setOnClickListener {
            val searchText = binding.searchText.text.toString()
            searchViewModel.getBooks(searchText)
            imm?.hideSoftInputFromWindow(binding.searchText.windowToken, 0)
        }
    }

    private fun setBookList() {
        searchViewModel.booksResponse.observe(this) { response ->
            binding.searchResult.visibility = if (response?.items.isNullOrEmpty()) View.GONE else View.VISIBLE
            binding.noSearchResult.visibility = if (response?.items.isNullOrEmpty()) View.VISIBLE else View.GONE
            bookListAdapter.setList(response?.items?.map { it.volumeInfo }?.toMutableList())
        }
    }
}