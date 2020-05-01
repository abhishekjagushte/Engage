package com.abhishekjagushte.engage.ui.setup.fragments.signup.di

import com.abhishekjagushte.engage.ui.setup.fragments.signup.SignUpFragment
import dagger.Module
import dagger.Subcomponent

@Subcomponent(modules = [SignUpFragmentModule::class])
interface SignUpComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): SignUpComponent }

    fun inject(fragment: SignUpFragment)

}