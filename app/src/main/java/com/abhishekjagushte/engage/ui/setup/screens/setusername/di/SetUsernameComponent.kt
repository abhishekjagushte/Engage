package com.abhishekjagushte.engage.ui.setup.screens.setusername.di

import com.abhishekjagushte.engage.ui.setup.screens.setusername.SetUsernameFragment
import dagger.Subcomponent

@Subcomponent(modules = [SetUsernameFragmentModule::class])
interface SetUsernameComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): SetUsernameComponent }

    fun inject(fragment: SetUsernameFragment)

}

