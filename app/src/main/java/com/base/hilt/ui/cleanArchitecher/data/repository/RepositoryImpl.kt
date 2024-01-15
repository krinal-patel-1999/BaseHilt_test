package com.base.hilt.ui.cleanArchitecher.data.repository

import com.base.hilt.ui.cleanArchitecher.data.remote.ApiInterface
import com.base.hilt.ui.cleanArchitecher.domain.model.Category
import com.base.hilt.ui.cleanArchitecher.domain.repository.CategoryRepository
import retrofit2.Response
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val apiInterface: ApiInterface,
): CategoryRepository {
    /*
        override fun getCategory(): Flow<CategoryListRespDTO> {
            return flow {
                apiInterface.getCategoryList()
            }
        }
    */


    override suspend fun getCategories() = apiInterface.getCategoryList()


}