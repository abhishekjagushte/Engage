package com.abhishekjagushte.engage.ui.main.screens.addparticipants.di

import com.abhishekjagushte.engage.ui.main.screens.addparticipants.AddParticipantsFragment
import com.abhishekjagushte.engage.ui.main.screens.search.SearchFragment
import dagger.Subcomponent


@Subcomponent(modules = [AddParticipantsFragmentModule::class])
interface AddParticipantsComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): AddParticipantsComponent
    }

    fun inject(fragment: AddParticipantsFragment)
}

