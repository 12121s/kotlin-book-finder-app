package com.illis.bookfinderapp.presentation.search

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.BounceInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.illis.bookfinderapp.R
import com.illis.bookfinderapp.data.repository.BookRepositoryImpl
import com.illis.bookfinderapp.databinding.ActivitySearchBinding
import com.illis.bookfinderapp.domain.usecase.SearchUseCase
import com.illis.bookfinderapp.util.BounceEdgeEffectFactory

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

        binding.searchResult.apply {
            this.layoutManager = layoutManager
            this.setHasFixedSize(true)
            this.adapter = bookListAdapter
            this.edgeEffectFactory = BounceEdgeEffectFactory()
        }
    }

    private fun initScrollListener() {
        binding.searchResult.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as FlexboxLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount - 1

                // 스크롤이 끝에 도달했는지 확인
                if (lastVisibleItemPosition == itemTotalCount) {
                    bookListAdapter.deleteLoading()
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
        binding.searchBtn.isEnabled = false
        binding.searchBtn.setOnClickListener {
            performSearch()
        }

        binding.searchText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH)
                performSearch()
            false
        })
    }

    private fun performSearch() {
        val imm = baseContext?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        val searchText = binding.searchText.text.toString()
        searchViewModel.getBooks(searchText)
        imm?.hideSoftInputFromWindow(binding.searchText.windowToken, 0)
    }

    private fun setBookList() {
        searchViewModel.volumeCount.observe(this) { count ->
            binding.volumeCount.text = String.format(getString(R.string.result_count), count)
        }
        searchViewModel.booksResponse.observe(this) { bookList ->
            binding.searchResultLayout.visibility = if (bookList.isNullOrEmpty()) View.GONE else View.VISIBLE
            binding.noSearchResult.visibility = if (bookList.isNullOrEmpty()) View.VISIBLE else View.GONE
            bookListAdapter.setList(bookList?.toMutableList())
        }
    }
}