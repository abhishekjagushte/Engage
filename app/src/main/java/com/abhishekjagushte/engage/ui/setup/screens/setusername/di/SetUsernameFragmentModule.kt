package com.abhishekjagushte.engage.ui.setup.screens.setusername.di

import androidx.lifecycle.ViewModel
import com.abhishekjagushte.engage.di.ViewModelKey
import com.abhishekjagushte.engage.ui.setup.screens.setusername.SetUsernameViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class SetUsernameFragmentModule {
    @Binds
    @IntoMap
    @ViewModelKey(SetUsernameViewModel::class)
    abstract fun bindViewModel(viewmodel: SetUsernameViewModel): ViewModel

}