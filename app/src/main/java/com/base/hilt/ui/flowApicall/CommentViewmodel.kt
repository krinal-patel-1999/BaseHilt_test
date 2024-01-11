package com.base.hilt.ui.flowApicall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.base.hilt.network.CommentFlowApiState
import com.base.hilt.network.HttpCommonMethod
import com.base.hilt.network.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor() : ViewModel() {


    private val repository = CommentsRepository(
        HttpCommonMethod.ApiService()
    )

    val commentState = MutableStateFlow(
        CommentFlowApiState(Status.LOADING, CommentModel(), "")
    )

    init {

        getNewComment(1)
    }



    fun getNewComment(id: Int) {


        commentState.value = CommentFlowApiState.loading<CommentModel>()


        viewModelScope.launch {


            repository.getComment(id)

                .catch {
                    commentState.value = CommentFlowApiState.error<CommentModel>(it.message.toString())
                }

                .collect {
                    commentState.value = CommentFlowApiState.success<CommentModel>(it.data)
                }
        }
    }
}
