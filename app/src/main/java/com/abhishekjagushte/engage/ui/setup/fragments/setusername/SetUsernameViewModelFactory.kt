package com.abhishekjagushte.engage.ui.setup.fragments.setusername

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abhishekjagushte.engage.database.DatabaseDao
import java.lang.Appendable

class SetUsernameViewModelFactory (
    private val databaseDao: DatabaseDao,
    private val application: Application
): ViewModelProvider.Factory{

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SetUsernameViewModel::class.java)) {
            //return SetUsernameViewModel(databaseDao, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}