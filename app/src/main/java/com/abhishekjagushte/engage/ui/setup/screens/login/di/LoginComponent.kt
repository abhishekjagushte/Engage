package com.abhishekjagushte.engage.ui.setup.screens.login.di

import com.abhishekjagushte.engage.ui.setup.screens.login.LoginFragment
import dagger.Subcomponent


@Subcomponent(modules = [LoginFragmentModule::class])
interface LoginComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): LoginComponent
    }

    fun inject(fragment: LoginFragment)
}

