package com.base.hilt.network

// A helper class to handle states
data class CommentFlowApiState<out T>(val status: Status, val data: T?, val message: String?) {

    companion object {

        // In case of Success,set status as
        // Success and data as the response
        fun <T> success(data: T?): CommentFlowApiState<T> {
            return CommentFlowApiState(Status.SUCCESS, data, null)
        }

        // In case of failure ,set state to Error ,
        // add the error message,set data to null
        fun <T> error(msg: String): CommentFlowApiState<T> {
            return CommentFlowApiState(Status.ERROR, null, msg)
        }

        // When the call is loading set the state
        // as Loading and rest as null
        fun <T> loading(): CommentFlowApiState<T> {
            return CommentFlowApiState(Status.LOADING, null, null)
        }
    }
}

// An enum to store the
// current state of api call
enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}
