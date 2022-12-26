package com.illis.bookfinderapp.data.model

data class VolumeInfo(
    val allowAnonLogging: Boolean = false,
    val authors: List<String>? = null,
    val averageRating: Double? = null,
    val canonicalVolumeLink: String = "",
    val categories: List<String>? = null,
    val contentVersion: String = "",
    val description: String? = null,
    val imageLinks: ImageLinks = ImageLinks(),
    val industryIdentifiers: List<IndustryIdentifier> = listOf(),
    val infoLink: String = "",
    val language: String = "",
    val maturityRating: String = "",
    val pageCount: Int? = null,
    val previewLink: String = "",
    val printType: String = "",
    val publishedDate: String? = null,
    val publisher: String? = null,
    val ratingsCount: Int? = null,
    val readingModes: ReadingModes = ReadingModes(),
    val subtitle: String? = null,
    val title: String = ""
) {
    data class ReadingModes(
        val image: Boolean = false,
        val text: Boolean = false
    )

    data class IndustryIdentifier(
        val identifier: String = "",
        val type: String = ""
    )

    data class ImageLinks(
        val smallThumbnail: String = "",
        val thumbnail: String = ""
    )
}