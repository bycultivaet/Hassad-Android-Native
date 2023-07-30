package com.cultivaet.hassad.ui.main.addfarmer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cultivaet.hassad.core.source.remote.Resource
import com.cultivaet.hassad.domain.model.remote.responses.Farmer
import com.cultivaet.hassad.domain.usecase.AddFarmerUseCase
import com.cultivaet.hassad.ui.main.addfarmer.intent.AddFarmerIntent
import com.cultivaet.hassad.ui.main.addfarmer.viewstate.AddFarmerState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
class AddFarmerViewModel(
    private val addFarmerUseCase: AddFarmerUseCase
) : ViewModel() {
    val addFarmerIntent = Channel<AddFarmerIntent>(Channel.UNLIMITED)
    private val _state = MutableStateFlow<AddFarmerState>(AddFarmerState.Idle)
    val state: StateFlow<AddFarmerState> = _state
    private var userId: Int = -1
    var farmersList: List<Farmer>? = null

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            addFarmerIntent.consumeAsFlow().collect {
                when (it) {
                    is AddFarmerIntent.GetUserId -> getUserId()

                    is AddFarmerIntent.FetchAllFarmers -> getFacilitatorById(userId)

                    is AddFarmerIntent.AddFarmer -> TODO()
                }
            }
        }
    }

    private fun getUserId() {
        runBlocking {
            viewModelScope.launch {
                addFarmerUseCase.userId().collect { id ->
                    if (id != null) {
                        userId = id
                        getFacilitatorById(userId)
                    }
                }
            }
        }
    }

    private fun getFacilitatorById(id: Int) {
        viewModelScope.launch {
            _state.value = AddFarmerState.Loading
            _state.value =
                when (val resource = addFarmerUseCase.getAllFarmersById(id)) {
                    is Resource.Success -> {
                        farmersList = resource.data
                        AddFarmerState.Success(farmersList)
                    }

                    is Resource.Error -> AddFarmerState.Error(resource.error)
                }
        }
    }
}