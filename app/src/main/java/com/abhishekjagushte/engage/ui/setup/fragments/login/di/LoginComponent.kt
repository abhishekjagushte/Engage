package com.abhishekjagushte.engage.ui.setup.fragments.login.di

import com.abhishekjagushte.engage.ui.setup.fragments.login.LoginFragment
import dagger.Subcomponent


@Subcomponent(modules = [LoginFragmentModule::class])
interface LoginComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): LoginComponent
    }

    fun inject(fragment: LoginFragment)
}

