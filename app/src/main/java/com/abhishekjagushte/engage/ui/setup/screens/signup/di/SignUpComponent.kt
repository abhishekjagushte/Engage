package com.abhishekjagushte.engage.ui.setup.screens.signup.di

import com.abhishekjagushte.engage.ui.setup.screens.signup.SignUpFragment
import dagger.Subcomponent

@Subcomponent(modules = [SignUpFragmentModule::class])
interface SignUpComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): SignUpComponent }

    fun inject(fragment: SignUpFragment)

}