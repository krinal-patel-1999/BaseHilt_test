package com.base.hilt.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.exception.ApolloException
import com.apollographql.apollo3.exception.ApolloHttpException
import com.apollographql.apollo3.exception.ApolloNetworkException
import com.base.hilt.DemoListQuery
import com.base.hilt.base.ViewModelBase
import com.base.hilt.network.GraphQLResponseHandler
import com.base.hilt.network.HttpCommonMethod
import com.base.hilt.network.HttpErrorCode
import com.base.hilt.utils.DebugLog
import com.base.hilt.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class HomeViewModel @Inject constructor(private val repository: HomeDataRepository): ViewModelBase() {

    var editProfileClickEvent = SingleLiveEvent<Any>()


    private val _demoListResponse =
        MutableLiveData<GraphQLResponseHandler<ApolloResponse<DemoListQuery.Data>>>()

    val demoListResponse: LiveData<GraphQLResponseHandler<ApolloResponse<DemoListQuery.Data>>>
        get() = _demoListResponse

    init {
        callDemoListApi()
    }

    fun callDemoListApi() {

                viewModelScope.launch {
                    _demoListResponse.postValue(GraphQLResponseHandler.Loading())
                    try {
                        val response =
                            repository.callDemoList()

                        if (response == null) {
                            _demoListResponse.postValue(
                                GraphQLResponseHandler.Error(
                                    code = HttpErrorCode.BAD_RESPONSE.code,
                                    message = "Opps!! something went wrong with request"
                                )
                            )
                        } else if (response?.hasErrors()!!) {
                            val errorModel = HttpCommonMethod.getErrorMessageForGraph(
                                response.errors!!
                            )

                            _demoListResponse.postValue(
                                GraphQLResponseHandler.Error(
                                    code = errorModel.first,
                                    message = errorModel.second
                                )
                            )

                        } else {
                            _demoListResponse.postValue(GraphQLResponseHandler.Success(response))
                        }
                    } catch (exception: ApolloException) {

                        when (exception) {
                            is ApolloNetworkException -> {
                                _demoListResponse.postValue(
                                    GraphQLResponseHandler.Error(
                                        code= HttpErrorCode.NO_CONNECTION.code,
                                        message = "Network timeout"
                                    )
                                )
                            }
                            is ApolloHttpException -> {
                                _demoListResponse.postValue(
                                    GraphQLResponseHandler.Error(
                                        code= HttpErrorCode.BAD_RESPONSE.code,
                                        message = exception.message
                                    )
                                )
                            }
                            else -> {
                                DebugLog.printE("System out", "Unknown network error")
                                _demoListResponse.postValue(
                                    GraphQLResponseHandler.Error(
                                        code= HttpErrorCode.BAD_RESPONSE.code,
                                        message = exception.message
                                    )
                                )
                            }
                        }
                    }
                }

    }
}