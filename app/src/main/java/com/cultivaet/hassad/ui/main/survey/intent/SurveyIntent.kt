package com.cultivaet.hassad.ui.main.survey.intent

sealed class SurveyIntent {
    object GetUserId : SurveyIntent()

    object FetchAllFarmers : SurveyIntent()

    object FetchFacilitatorForm : SurveyIntent()

    object SubmitFacilitatorAnswer : SurveyIntent()

    object InsertFacilitatorAnswerOffline : SurveyIntent()

    object SubmitOfflineFacilitatorAnswersList : SurveyIntent()
}