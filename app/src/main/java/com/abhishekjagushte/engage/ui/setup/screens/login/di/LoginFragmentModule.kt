package com.abhishekjagushte.engage.ui.setup.screens.login.di

import androidx.lifecycle.ViewModel
import com.abhishekjagushte.engage.di.ViewModelKey
import com.abhishekjagushte.engage.ui.setup.screens.login.LoginFragmentViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class LoginFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(LoginFragmentViewModel::class)
    abstract fun bindViewModel(viewmodel: LoginFragmentViewModel): ViewModel
}

