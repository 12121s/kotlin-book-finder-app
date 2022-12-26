package com.illis.bookfinderapp.data.model

data class Books(
    val items: List<Item> = listOf(),
    val kind: String = "",
    val totalItems: Int = 0
)