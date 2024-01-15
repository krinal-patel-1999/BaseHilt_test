package com.base.hilt.ui.cleanArchitecher.domain.repository

import com.base.hilt.ui.cleanArchitecher.domain.model.Category
import retrofit2.Response

interface CategoryRepository {
    suspend fun getCategories(): Response<Category>
}