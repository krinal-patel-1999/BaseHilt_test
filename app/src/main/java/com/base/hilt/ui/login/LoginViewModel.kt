package com.base.hilt.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.exception.ApolloException
import com.apollographql.apollo3.exception.ApolloHttpException
import com.apollographql.apollo3.exception.ApolloNetworkException
import com.base.hilt.DemoListQuery
import com.base.hilt.LoginMutation
import com.base.hilt.base.ViewModelBase
import com.base.hilt.network.GraphQLResponseHandler
import com.base.hilt.network.HttpCommonMethod
import com.base.hilt.network.HttpErrorCode
import com.base.hilt.network.ResponseData
import com.base.hilt.network.ResponseHandler
import com.base.hilt.utils.DebugLog
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel  @Inject constructor(private val loginRepository: LoginRepository) :
    ViewModelBase() {


    //    var email = ""
    var email = "me@example.com"

    /*var responseLiveHomeVendorListResponse =
        MutableLiveData<ResponseHandler<ResponseData<HomeScreenVendorsListResponse>?>>()
*/
    private val _loginResponse =
        MutableLiveData<GraphQLResponseHandler<ApolloResponse<LoginMutation.Data>>>()

    val loginResponse: LiveData<GraphQLResponseHandler<ApolloResponse<LoginMutation.Data>>>
        get() = _loginResponse

    private val _demoListResponse =
        MutableLiveData<ApolloResponse<DemoListQuery.Data>>()

    val demoListResponse: LiveData<ApolloResponse<DemoListQuery.Data>>
        get() = _demoListResponse


    fun callLoginAPI() {

        viewModelScope.launch {
                    _loginResponse.postValue(GraphQLResponseHandler.Loading())
                    try {
                        val response =
                            loginRepository.callLoginAPi(email = email)

                        if (response == null) {
                            _loginResponse.postValue(
                                GraphQLResponseHandler.Error(
                                    code = HttpErrorCode.BAD_RESPONSE.code,
                                    message = "Opps!! something went wrong with request"
                                )
                            )
                        } else if (response?.hasErrors()!!) {
                            val errorModel = HttpCommonMethod.getErrorMessageForGraph(
                                response.errors!!
                            )

                            _loginResponse.postValue(
                                GraphQLResponseHandler.Error(
                                    code = errorModel.first,
                                    message = errorModel.second
                                )
                            )

                        } else {
                            _loginResponse.postValue(GraphQLResponseHandler.Success(response))
                        }
                    } catch (exception: ApolloException) {

                        when (exception) {
                            is ApolloNetworkException -> {
                                _loginResponse.postValue(
                                    GraphQLResponseHandler.Error(
                                        code= HttpErrorCode.NO_CONNECTION.code,
                                        message = "Network timeout"
                                    )
                                )
                            }
                            is ApolloHttpException -> {
                                _loginResponse.postValue(
                                    GraphQLResponseHandler.Error(
                                        code= HttpErrorCode.BAD_RESPONSE.code,
                                        message = exception.message
                                    )
                                )
                            }
                            else -> {
                                DebugLog.printE("System out", "Unknown network error")
                                _loginResponse.postValue(
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