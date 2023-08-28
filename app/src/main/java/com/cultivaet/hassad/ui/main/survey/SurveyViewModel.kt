package com.cultivaet.hassad.ui.main.survey

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cultivaet.hassad.core.source.remote.Resource
import com.cultivaet.hassad.domain.model.remote.requests.Answer
import com.cultivaet.hassad.domain.model.remote.requests.FacilitatorAnswer
import com.cultivaet.hassad.domain.usecase.SurveyUseCase
import com.cultivaet.hassad.ui.main.farmers.FarmerDataItem
import com.cultivaet.hassad.ui.main.survey.intent.SurveyIntent
import com.cultivaet.hassad.ui.main.survey.viewstate.SurveyState
import com.google.gson.Gson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.InputStream

@ExperimentalCoroutinesApi
class SurveyViewModel(
    private val surveyUseCase: SurveyUseCase
) : ViewModel() {
    val surveyIntent = Channel<SurveyIntent>(Channel.UNLIMITED)
    private val _state = MutableStateFlow<SurveyState>(SurveyState.Idle)
    val state: StateFlow<SurveyState> = _state
    var farmersList: List<FarmerDataItem>? = null
    val facilitatorAnswer = FacilitatorAnswer()

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            surveyIntent.consumeAsFlow().collect {
                when (it) {
                    is SurveyIntent.GetUserId -> getUserId()

                    is SurveyIntent.FetchAllFarmers -> getAllFarmersById(facilitatorAnswer.userId)

                    is SurveyIntent.FetchFacilitatorForm -> getFacilitatorForm(facilitatorAnswer.userId)

                    is SurveyIntent.SubmitFacilitatorAnswer -> submitFacilitatorAnswer(
                        facilitatorAnswer
                    )

                    is SurveyIntent.InsertFacilitatorAnswerOffline -> insertFacilitatorAnswerOffline(
                        facilitatorAnswer
                    )
                }
            }
        }
    }

    private fun getUserId() {
        runBlocking {
            viewModelScope.launch {
                surveyUseCase.userId().collect { id ->
                    if (id != null) {
                        facilitatorAnswer.userId = id
                        getAllFarmersById(facilitatorAnswer.userId)
                    }
                }
            }
        }
    }

    private fun getAllFarmersById(id: Int) {
        viewModelScope.launch {
            _state.value = SurveyState.Loading
            _state.value = when (val resource = surveyUseCase.getAllFarmersById(id)) {
                is Resource.Success -> {
                    farmersList = resource.data?.map { it.toFarmerDataItem() }
                    SurveyState.Success(farmersList)
                }

                is Resource.Error -> SurveyState.Error(resource.error)
            }
        }
    }

    private fun getFacilitatorForm(id: Int) {
        viewModelScope.launch {
            _state.value = SurveyState.Loading
            _state.value = when (val resource = surveyUseCase.getFacilitatorForm(id)) {
                is Resource.Success -> {
                    SurveyState.Success(resource.data)
                }

                is Resource.Error -> SurveyState.Error(resource.error)
            }
        }
    }

    private fun submitFacilitatorAnswer(facilitatorAnswer: FacilitatorAnswer) {
        viewModelScope.launch {
            _state.value = SurveyState.Loading
            _state.value =
                when (val resource = surveyUseCase.submitFacilitatorAnswer(facilitatorAnswer)) {
                    is Resource.Success -> {
                        SurveyState.Success(resource.data)
                    }

                    is Resource.Error -> SurveyState.Error(resource.error)
                }
        }
    }

    private fun insertFacilitatorAnswerOffline(facilitatorAnswer: FacilitatorAnswer) {
        val facilitatorAnswerLocal = com.cultivaet.hassad.domain.model.local.FacilitatorAnswer(
            facilitatorAnswer.formId,
            facilitatorAnswer.farmerId,
            facilitatorAnswer.geolocation,
            listOfAnswersToJson(facilitatorAnswer.answers),
            facilitatorAnswer.userId,
        )

        viewModelScope.launch { surveyUseCase.insertFacilitatorAnswer(facilitatorAnswerLocal) }
    }

    private fun listOfAnswersToJson(answers: List<Answer>): String {
        return Gson().toJson(answers)
    }

    fun uploadImage(inputStream: InputStream) {
        viewModelScope.launch {
            _state.value = SurveyState.Loading
            _state.value = when (val resource = surveyUseCase.uploadImage(
                MultipartBody.Part.createFormData(
                    "pic", "myPic", RequestBody.create(
                        "image/*".toMediaTypeOrNull(),
                        inputStream.readBytes()
                    )
                )
            )) {
                is Resource.Success -> {
                    Log.d("TAG", "uploadImage: ${resource.data}")
                    SurveyState.Success(resource.data)
                }

                is Resource.Error -> SurveyState.Error(resource.error)
            }
        }
    }
}