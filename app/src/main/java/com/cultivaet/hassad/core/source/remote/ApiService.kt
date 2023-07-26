package com.cultivaet.hassad.core.source.remote

import com.cultivaet.hassad.core.util.Constants
import com.cultivaet.hassad.domain.model.remote.responses.Facilitator
import com.cultivaet.hassad.domain.model.remote.responses.Farmer
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET(Constants.EndPoints.GetFacilitatorByPhoneNumber)
    suspend fun getFacilitatorByPhoneNumber(@Path("phone") phoneNumber: String): Response<Facilitator>

    @GET(Constants.EndPoints.GetFacilitatorById)
    suspend fun getFacilitatorById(@Path("id") id: Int): Response<Facilitator>

    @GET(Constants.EndPoints.GetAllFarmersById)
    suspend fun getAllFarmersById(@Path("id") id: Int): Response<List<Farmer>>

    @POST(Constants.EndPoints.PostFarmer)
    suspend fun addFarmer(@Body farmer: com.cultivaet.hassad.domain.model.remote.requests.Farmer): Response<Farmer>
}