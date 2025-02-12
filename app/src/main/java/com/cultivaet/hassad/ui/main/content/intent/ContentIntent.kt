package com.cultivaet.hassad.ui.main.content.intent

sealed class ContentIntent {
    object FetchAllComments : ContentIntent()

    object FetchAnswerById : ContentIntent()

    object FetchFile : ContentIntent()
}