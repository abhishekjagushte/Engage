package com.abhishekjagushte.engage.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abhishekjagushte.engage.repository.DataRepository

class MainActivityViewModelFactory (
    private val dataRepository: DataRepository
):ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            return MainActivityViewModel(
                dataRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}