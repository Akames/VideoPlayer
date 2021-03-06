package com.akame.videoplayer.utils

import android.net.Uri

sealed class MediaType {
    class StringType(val mediaPath: String) : MediaType()

    class UriType(val mediaPath: Uri) : MediaType()
}
