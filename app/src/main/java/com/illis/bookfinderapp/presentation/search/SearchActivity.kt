package com.illis.bookfinderapp.presentation.search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.illis.bookfinderapp.data.repository.BookRepositoryImpl
import com.illis.bookfinderapp.databinding.ActivitySearchBinding
import com.illis.bookfinderapp.domain.usecase.SearchUseCase

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchViewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModelFactory = SearchViewModelFactory(SearchUseCase(BookRepositoryImpl()))
        searchViewModel = ViewModelProvider(this, viewModelFactory)[SearchViewModel::class.java]
        searchViewModel.getBooks("love")
    }
}