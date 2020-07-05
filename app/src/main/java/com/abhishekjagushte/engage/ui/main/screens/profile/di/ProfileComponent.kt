package com.abhishekjagushte.engage.ui.main.screens.profile.di

import com.abhishekjagushte.engage.ui.main.screens.profile.ProfileFragment
import dagger.Subcomponent


@Subcomponent(modules = [ProfileFragmentModule::class])
interface ProfileComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ProfileComponent
    }

    fun inject(fragment: ProfileFragment)
}

