package com.illis.bookfinderapp.data.model

data class BookInfoList(
    val etag: String = "",
    val id: String = "",
    val kind: String = "",
    val selfLink: String = "",
    val volumeInfo: VolumeInfo = VolumeInfo()
)