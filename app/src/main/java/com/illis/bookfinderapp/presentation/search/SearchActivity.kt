package com.illis.bookfinderapp.presentation.search

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.*
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

        initSearchLayout()
        setBookList()
    }

    private fun initSearchLayout() {
        initSearchAdapter()
        initScrollListener()
        initSearchBox()
        initSearchAction()
    }

    private fun initSearchAdapter() {
        val layoutManager = FlexboxLayoutManager(baseContext).apply {
            justifyContent = JustifyContent.SPACE_AROUND
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }
        binding.searchResult.layoutManager = layoutManager
        binding.searchResult.setHasFixedSize(true)
        binding.searchResult.adapter = bookListAdapter
    }

    private fun initScrollListener() {
        binding.searchResult.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as FlexboxLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount-1

                // 스크롤이 끝에 도달했는지 확인
                if (lastVisibleItemPosition == itemTotalCount) {
//                    bookListAdapter.deleteLoading()
                    searchViewModel.getNextPageBooks()
                }
            }
        })
    }

    private fun initSearchBox() {
        val imm = baseContext?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        binding.searchText.requestFocus()
        binding.searchText.postDelayed(Runnable {
            imm?.showSoftInput(binding.searchText, InputMethodManager.SHOW_IMPLICIT)
        }, 100)
        binding.searchText.addTextChangedListener {
            binding.searchBtn.isEnabled = (it.toString().isNotEmpty())
        }
    }

    private fun initSearchAction() {
        val imm = baseContext?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        binding.searchBtn.isEnabled = false
        binding.searchBtn.setOnClickListener {
            val searchText = binding.searchText.text.toString()
            searchViewModel.getBooks(searchText)
            imm?.hideSoftInputFromWindow(binding.searchText.windowToken, 0)
        }
    }

    private fun setBookList() {
        searchViewModel.booksResponse.observe(this) { bookList ->
            binding.searchResult.visibility = if (bookList.isNullOrEmpty()) View.GONE else View.VISIBLE
            binding.noSearchResult.visibility = if (bookList.isNullOrEmpty()) View.VISIBLE else View.GONE
            bookListAdapter.setList(bookList?.toMutableList())
        }
    }
}