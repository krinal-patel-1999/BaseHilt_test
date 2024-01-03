package com.base.hilt.ui.home

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.base.hilt.DemoListQuery
import com.base.hilt.base.BaseRepository
import javax.inject.Inject

class HomeDataRepository @Inject constructor(
    private val apolloClient: ApolloClient
) :
    BaseRepository() {

    suspend fun callDemoList(): ApolloResponse<DemoListQuery.Data> {

        return apolloClient.query(DemoListQuery()).execute()
    }
}