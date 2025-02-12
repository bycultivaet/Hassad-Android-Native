package com.cultivaet.hassad.domain.repository

import com.cultivaet.hassad.core.source.remote.Resource
import com.cultivaet.hassad.domain.model.remote.responses.FacilitatorAnswer
import com.cultivaet.hassad.domain.model.remote.responses.Farmer
import com.cultivaet.hassad.domain.model.remote.responses.Form
import com.cultivaet.hassad.domain.model.remote.responses.ImageUUID
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface SurveyRepository {
    suspend fun getAllFarmersById(id: Int, filter: Boolean): Resource<List<Farmer>>

    suspend fun getFacilitatorForm(id: Int): Resource<Form>

    suspend fun submitFacilitatorAnswer(
        facilitatorAnswer: com.cultivaet.hassad.domain.model.remote.requests.FacilitatorAnswer
    ): Resource<FacilitatorAnswer>

    suspend fun insertFacilitatorAnswer(
        facilitatorAnswer: com.cultivaet.hassad.domain.model.local.FacilitatorAnswer
    )

    suspend fun uploadImage(
        image: MultipartBody.Part
    ): Resource<ImageUUID>

    suspend fun setFacilitatorForm(facilitatorFormJson: String)

    suspend fun getFacilitatorForm(): Flow<String?>
}