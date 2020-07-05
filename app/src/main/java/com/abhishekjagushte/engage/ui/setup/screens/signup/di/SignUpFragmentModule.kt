package com.abhishekjagushte.engage.ui.setup.screens.signup.di

import androidx.lifecycle.ViewModel
import com.abhishekjagushte.engage.di.ViewModelKey
import com.abhishekjagushte.engage.ui.setup.screens.signup.SignUpFragmentViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class SignUpFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(SignUpFragmentViewModel::class)
    abstract fun bindViewModel(viewmodel: SignUpFragmentViewModel): ViewModel
}
