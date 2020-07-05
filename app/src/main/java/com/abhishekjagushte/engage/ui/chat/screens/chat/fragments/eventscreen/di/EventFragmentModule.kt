package com.abhishekjagushte.engage.ui.chat.screens.chat.fragments.eventscreen.di

import androidx.lifecycle.ViewModel
import com.abhishekjagushte.engage.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class EventFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(EventScreenViewModel::class)
    abstract fun bindViewModel(viewmodel: EventScreenViewModel): ViewModel
}

