package com.illis.bookfinderapp.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.illis.bookfinderapp.MainActivity
import com.illis.bookfinderapp.R
import com.illis.bookfinderapp.consts.ServerConsts
import com.illis.bookfinderapp.data.Resource
import com.illis.bookfinderapp.databinding.FragmentSearchBinding
import com.illis.bookfinderapp.util.BounceEdgeEffectFactory

class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {
    private val bookListAdapter = BookListAdapter()
    private val searchViewModel: SearchViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSearchLayout()
        setBookList()
        observeSearchState()
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

        binding.searchResult.apply {
            this.layoutManager = layoutManager
            this.setHasFixedSize(true)
            this.adapter = bookListAdapter
            this.edgeEffectFactory = BounceEdgeEffectFactory()
        }

        bookListAdapter.setOnItemClickListener { book ->
            searchViewModel.selectedBook.postValue(book)
            (activity as MainActivity).bookTitle.postValue(book.title)
            findNavController().navigate(R.id.action_searchFragment_to_bookDetailFragment)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initScrollListener() {
        binding.searchResult.setOnTouchListener { _, _ ->
            binding.searchResult.requestFocus()
            showKeyboard(false)
            false
        }
        binding.searchResult.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition = (recyclerView.layoutManager as FlexboxLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount - 1

                // 스크롤이 끝에 도달했는지 확인 (2열이므로 마지막 아이템 2개 모두 체크)
                if (lastVisibleItemPosition == itemTotalCount || lastVisibleItemPosition == itemTotalCount - 1) {
                    if (bookListAdapter.loadComplete) { // 서버 request 중복 호출 방지
                        bookListAdapter.loadComplete = false
                        searchViewModel.getNextPageBooks()
                    }
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
            binding.searchResultLayout.post {
                // 아이템 개수가 1 페이지 개수보다 적으면 로딩 애니메이션 미리 제거
                if (count < ServerConsts.BOOKS_API_MAX_RESULTS) {
                    bookListAdapter.deleteLoading()
                }
                // 리사이클러뷰 스크롤 초기화
                binding.searchResult.postDelayed(Runnable {
                    binding.searchResult.scrollToPosition(0)
                }, 100)
            }
            binding.volumeCount.text = String.format(getString(R.string.result_count), count)
        }
        searchViewModel.booksResponse.observe(viewLifecycleOwner) { bookList ->
            binding.searchResultLayout.visibility = if (bookList.isNullOrEmpty()) View.GONE else View.VISIBLE
            binding.noSearchResult.visibility = if (bookList.isNullOrEmpty()) View.VISIBLE else View.GONE

            bookListAdapter.setList(bookList?.toMutableList())
        }
    }

    private fun observeSearchState() {
        searchViewModel.searchState.postValue(Resource.Success("")) // 초기화
        searchViewModel.searchState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is Resource.Loading -> {
                    binding.searchLoading.isIndeterminate = true
                    binding.searchLoading.visibility = View.VISIBLE
                }
                else -> {
                    binding.searchLoading.isIndeterminate = false
                    binding.searchLoading.visibility = View.GONE
                    if (state is Resource.Error) {
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}