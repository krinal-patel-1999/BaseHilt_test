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

    // Create a Repository and pass the api
    // service we created in AppConfig file
    private val repository = CommentsRepository(
        HttpCommonMethod.ApiService()
    )

    val commentState = MutableStateFlow(
        CommentFlowApiState(Status.LOADING, CommentModel(), "")
    )

    init {
        // Initiate a starting
        // search with comment Id 1
        getNewComment(1)
    }


    // Function to get new Comments
    fun getNewComment(id: Int) {

        // Since Network Calls takes time,Set the
        // initial value to loading state
        commentState.value = CommentFlowApiState.loading<CommentModel>()

        // ApiCalls takes some time, So it has to be
        // run and background thread. Using viewModelScope
        // to call the api
        viewModelScope.launch {

            // Collecting the data emitted
            // by the function in repository
            repository.getComment(id)
                // If any errors occurs like 404 not found
                // or invalid query, set the state to error
                  // State to show some info
                // on screen
                .catch {
                    commentState.value = CommentFlowApiState.error<CommentModel>(it.message.toString())
                }
                // If Api call is succeeded, set the State to Success
                // and set the response data to data received from api
                .collect {
                    commentState.value = CommentFlowApiState.success<CommentModel>(it.data)
                }
        }
    }
}
