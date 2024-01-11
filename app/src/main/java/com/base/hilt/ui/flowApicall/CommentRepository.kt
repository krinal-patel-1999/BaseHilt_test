package com.base.hilt.ui.flowApicall

import com.base.hilt.network.ApiService
import com.base.hilt.network.CommentFlowApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CommentsRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getComment(id: Int): Flow<CommentFlowApiState<CommentModel>> {
        return flow {


            val comment=apiService.getComments(id)


            emit(CommentFlowApiState.success(comment))
        }.flowOn(Dispatchers.IO)
    }
}