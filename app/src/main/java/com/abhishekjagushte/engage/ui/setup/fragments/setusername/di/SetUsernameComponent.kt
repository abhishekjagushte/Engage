package com.abhishekjagushte.engage.ui.setup.fragments.setusername.di

import com.abhishekjagushte.engage.ui.setup.fragments.setusername.SetUsernameFragment
import com.abhishekjagushte.engage.ui.setup.fragments.signup.SignUpFragment
import com.abhishekjagushte.engage.ui.setup.fragments.signup.di.SignUpFragmentModule
import dagger.Subcomponent

@Subcomponent(modules = [SetUsernameFragmentModule::class])
interface SetUsernameComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): SetUsernameComponent }

    fun inject(fragment: SetUsernameFragment)

}

