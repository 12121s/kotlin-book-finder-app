package com.illis.bookfinderapp.data.model

import com.google.gson.annotations.SerializedName

data class Books(
    @SerializedName("items")
    val bookInfoLists: List<BookInfoList> = listOf(),
    val kind: String = "",
    val totalItems: Int = 0
)