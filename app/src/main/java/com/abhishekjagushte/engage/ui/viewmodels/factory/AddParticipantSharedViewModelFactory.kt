package com.abhishekjagushte.engage.ui.viewmodels.factory

import com.abhishekjagushte.engage.ui.viewmodels.AddParticipantSharedViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abhishekjagushte.engage.repository.DataRepository

class AddParticipantSharedViewModelFactory (
    private val dataRepository: DataRepository
):ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddParticipantSharedViewModel::class.java)) {
            return AddParticipantSharedViewModel(
                dataRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}