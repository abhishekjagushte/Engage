package com.abhishekjagushte.engage.ui.main.screens.creategroup.addparticipants.di

import androidx.lifecycle.ViewModel
import com.abhishekjagushte.engage.di.ViewModelKey
import com.abhishekjagushte.engage.ui.main.screens.creategroup.addparticipants.AddParticipantsViewModel
import com.abhishekjagushte.engage.ui.main.screens.search.SearchFragmentViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class AddParticipantsFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(AddParticipantsViewModel::class)
    abstract fun bindViewModel(viewmodel: AddParticipantsViewModel): ViewModel
}

