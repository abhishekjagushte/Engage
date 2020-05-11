package com.abhishekjagushte.engage.ui.main.fragments.profile.di

import com.abhishekjagushte.engage.ui.main.fragments.profile.ProfileActivity
import dagger.Subcomponent


@Subcomponent(modules = [ProfileFragmentModule::class])
interface ProfileComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ProfileComponent
    }

    fun inject(fragment: ProfileActivity)
}

