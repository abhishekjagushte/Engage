package com.abhishekjagushte.engage.ui.main.screens.events.di

import androidx.lifecycle.ViewModel
import com.abhishekjagushte.engage.di.ViewModelKey
import com.abhishekjagushte.engage.ui.main.screens.events.EventsFragmentViewModel
import com.abhishekjagushte.engage.ui.main.screens.profile.ProfileFragmentViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class EventFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(EventsFragmentViewModel::class)
    abstract fun bindViewModel(viewmodel: EventsFragmentViewModel): ViewModel
}

