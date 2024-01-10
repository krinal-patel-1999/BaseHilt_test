package com.base.hilt.ui.flowApicall

import com.google.gson.annotations.SerializedName
import dagger.hilt.android.lifecycle.HiltViewModel


data class CommentModel(
    val postId: Int?=null,
    val id: Int?=null,
    val email: String?=null,
    val name:String?=null,

    @SerializedName("body")
    val comment: String?=null
)

