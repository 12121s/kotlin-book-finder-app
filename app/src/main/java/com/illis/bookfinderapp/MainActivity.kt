
package com.illis.bookfinderapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.illis.bookfinderapp.data.repository.BookRepositoryImpl
import com.illis.bookfinderapp.databinding.ActivityMainBinding
import com.illis.bookfinderapp.domain.usecase.SearchUseCase
import com.illis.bookfinderapp.presentation.SearchViewModel
import com.illis.bookfinderapp.presentation.SearchViewModelFactory

class MainActivity : AppCompatActivity() {

    val bookTitle = MutableLiveData<String>()
    lateinit var searchViewModel: SearchViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModelFactory = SearchViewModelFactory(SearchUseCase(BookRepositoryImpl()))
        searchViewModel = ViewModelProvider(this, viewModelFactory)[SearchViewModel::class.java]

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController)
        setBookDetailTitle()
    }

    private fun setBookDetailTitle() {
        bookTitle.observe(this) { title ->
            supportActionBar?.title = title
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp()
    }
}