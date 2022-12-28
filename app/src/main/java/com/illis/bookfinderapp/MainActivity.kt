
package com.illis.bookfinderapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.illis.bookfinderapp.data.repository.BookRepositoryImpl
import com.illis.bookfinderapp.databinding.ActivityMainBinding
import com.illis.bookfinderapp.domain.usecase.SearchUseCase
import com.illis.bookfinderapp.presentation.SearchViewModel
import com.illis.bookfinderapp.presentation.SearchViewModelFactory

class MainActivity : AppCompatActivity() {

    lateinit var searchViewModel: SearchViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModelFactory = SearchViewModelFactory(SearchUseCase(BookRepositoryImpl()))
        searchViewModel = ViewModelProvider(this, viewModelFactory)[SearchViewModel::class.java]
    }
}