package com.base.hilt.ui.cleanArchitecher.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.base.hilt.R
import com.base.hilt.databinding.ActivityCategoryBinding
import com.base.hilt.utils.ResponseHandler
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CategoryActivity : AppCompatActivity() {


    private val viewModel: CategoryViewModel by viewModels()
    private lateinit var binding: ActivityCategoryBinding
    private val adapter = CategoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()
        observeViewModel()
        setupSearchView()
    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.categoryState.collect { responseHandler ->
                when (responseHandler) {
                    is ResponseHandler.Success -> {
                        adapter.submitList(responseHandler.data)
                    }
                    is ResponseHandler.Error -> {
                        // Handle error state
                    }
                    is ResponseHandler.Loading -> {
                        // Handle loading state
                    }
                }
            }
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterCategories(newText.orEmpty())
                return true
            }
        })
    }
}