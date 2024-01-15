package com.base.hilt.ui.cleanArchitecher.data.remote

import com.base.hilt.ui.cleanArchitecher.domain.model.Category
import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {

    @GET("api/json/v1/1/categories.php")
    suspend fun getCategoryList(): Response<Category>
}