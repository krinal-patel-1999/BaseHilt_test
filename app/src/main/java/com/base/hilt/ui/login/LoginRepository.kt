package com.base.hilt.ui.login

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.base.hilt.DemoListQuery
import com.base.hilt.LoginMutation
import com.base.hilt.base.BaseRepository
import javax.inject.Inject

class LoginRepository @Inject constructor(
                                          private val apolloClient: ApolloClient
) :
    BaseRepository() {



    suspend fun callLoginAPi(email: String): ApolloResponse<LoginMutation.Data>?/*: ApolloResponse<LoginMutation.Data> */{
        /*val response = try {
            apolloClient.mutation(
                LoginMutation(
                    email,
                    password
                )
            ).execute()
        } catch (e: Exception) {
            DebugLog.print(e)
            null
        }*/

        return apolloClient.mutation(
            LoginMutation(
                email = Optional.present(email)
            )
        ).execute()

    }

    suspend fun callDemoList(): ApolloResponse<DemoListQuery.Data> {

        return apolloClient.query(DemoListQuery()).execute()
    }
}