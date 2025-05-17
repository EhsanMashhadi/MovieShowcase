package software.ehsan.movieshowcase.util

private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w1280/"

fun getImageUrl(baseImageUrl: String = IMAGE_BASE_URL, imageUrl: String?) =
    imageUrl?.let { "$baseImageUrl$it" }