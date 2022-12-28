package com.illis.bookfinderapp.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.illis.bookfinderapp.MainActivity
import com.illis.bookfinderapp.R
import com.illis.bookfinderapp.databinding.FragmentSearchBinding
import com.illis.bookfinderapp.util.BounceEdgeEffectFactory

class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {
    private val bookListAdapter = BookListAdapter()
    private lateinit var searchViewModel: SearchViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchViewModel = (activity as MainActivity).searchViewModel

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
        val layoutManager = FlexboxLayoutManager(context).apply {
            justifyContent = JustifyContent.SPACE_AROUND
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }

        bookListAdapter.setOnItemClickListener {
            searchViewModel.selectedBook.postValue(it)
            findNavController().navigate(R.id.action_searchFragment_to_bookDetailFragment)
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
        if (searchViewModel.firstOpen) {
            binding.searchText.requestFocus()
            binding.searchText.postDelayed(Runnable {
                showKeyboard(true)
            }, 100)
            searchViewModel.firstOpen = false
        }
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
        val searchText = binding.searchText.text.toString()
        searchViewModel.getBooks(searchText)
        showKeyboard(false)
    }

    private fun showKeyboard(show: Boolean) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        if (show) imm?.showSoftInput(binding.searchText, InputMethodManager.SHOW_IMPLICIT)
        else imm?.hideSoftInputFromWindow(binding.searchText.windowToken, 0)
    }

    private fun setBookList() {
        searchViewModel.volumeCount.observe(viewLifecycleOwner) { count ->
            binding.volumeCount.text = String.format(getString(R.string.result_count), count)
        }
        searchViewModel.booksResponse.observe(viewLifecycleOwner) { bookList ->
            binding.searchResultLayout.visibility = if (bookList.isNullOrEmpty()) View.GONE else View.VISIBLE
            binding.noSearchResult.visibility = if (bookList.isNullOrEmpty()) View.VISIBLE else View.GONE
            bookListAdapter.setList(bookList?.toMutableList())
        }
    }
}