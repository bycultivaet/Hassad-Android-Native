package com.cultivaet.hassad.domain.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "facilitator_answer")
class FacilitatorAnswer(
    @PrimaryKey
    var formId: Int,
    var farmerId: Int,
    var geolocation: String,
    var answers: String,
    var userId: Int,
    var type: String = "facilitator"
)