package com.base.hilt.network

import com.apollographql.apollo3.api.Error
import com.base.hilt.ui.cleanArchitecher.data.remote.ApiInterface
import com.base.hilt.ui.cleanArchitecher.data.repository.RepositoryImpl
import com.base.hilt.ui.cleanArchitecher.domain.repository.CategoryRepository
import com.base.hilt.utils.DebugLog
import com.base.hilt.utils.Validation
import com.fasterxml.jackson.databind.JsonNode
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object HttpCommonMethod {

    const val AUTHORIZATION = "authentication"
    const val RESPONSE_CATEGORY = "category"
    const val VALIDATION = "validation"
    const val CUSTOM = "Custom"

    /**
     * check whether API response is success or not
     */
    fun isSuccessResponse(responseCode: Int): Boolean {
        return responseCode in 200..300
    }

    /**
     * check Error Message
     */
    fun getErrorMessage(error: JsonNode?): String {
        var value = ""
        if (error != null) {
            when {
                error.isArray -> {
                    for (objInArray in error) {
                        value = (objInArray).toString()
                    }
                }
                error.isContainerNode -> {
                    val it: Iterator<Map.Entry<String, JsonNode>> = error.fields()
                    while (it.hasNext()) {
                        val field = it.next()
                        value = if (Validation.isNotNull(value)) {
                            value + "," + removeArrayBrace(field.value.toString())
                        } else {
                            removeArrayBrace(field.value.toString())
                        }
                    }
                }
                else -> {
                    value = (error).toString()
                }
            }
        }
        return value
    }

    /**
     * Remove [] from Error Objects when there are multiple errors
     *
     * @param message as String
     * @return replacedString
     */
    fun removeArrayBrace(message: String): String {
        return message.replace("[\"", "").replace("\"]", "").replace(".", "")
    }

    fun getErrorMessageForGraph(error: List<Error>?): Pair<Int, String> {
//        var errorMessage = Pair<String, String>("", "")
        var value = ""
        var statusCode = -1
        if (error?.get(0)?.extensions?.get(RESPONSE_CATEGORY) == AUTHORIZATION) {
            value = "Your token is expired. Kindly re-login again"//error[0].message
            statusCode = HttpErrorCode.UNAUTHORIZED.code
        } else if (error?.get(0)?.extensions?.get(RESPONSE_CATEGORY) == VALIDATION) {
            statusCode = HttpErrorCode.SERVER_SIDE_VALIDATION.code
            val validation = (error[0].extensions?.get(VALIDATION)  as LinkedHashMap<*, *>)
            val keys = validation.keys
            for (i in keys) {
                value = if (Validation.isNotNull(value)) {
                    value + "," + (validation[i] as ArrayList<String>)[0]
                } else {
                    (validation[i] as ArrayList<String>)[0]
                }
            }
        } else if (error?.get(0)?.extensions?.get(RESPONSE_CATEGORY) == CUSTOM) {
            statusCode = HttpErrorCode.BAD_RESPONSE.code
            value = error[0].extensions?.get("message") as String
        }
        DebugLog.printI("Error::::: $value")
        return Pair(statusCode, value)
    }


    //for Flow api in comment
    // Base url of the api
    private const val BASE_URL = "https://www.themealdb.com/"



    // create retrofit service

    @Provides
    @Singleton
    fun ApiService(): ApiService =
        Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(ApiService::class.java)


    @Provides
    @Singleton
    fun provideNewsApi(retrofit: Retrofit): ApiInterface {
        return retrofit.create(ApiInterface::class.java)
    }

    @Provides
    fun provideDomainRepository(apiInterface: ApiInterface): CategoryRepository {
        return RepositoryImpl(apiInterface)
    }

}
