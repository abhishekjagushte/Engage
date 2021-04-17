package com.abhishekjagushte.engage.ui.main.screens.events.di

import com.abhishekjagushte.engage.ui.main.screens.events.EventsFragment
import dagger.Subcomponent


@Subcomponent(modules = [EventFragmentModule::class])
interface EventFragmentComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): EventFragmentComponent
    }

    fun inject(fragment: EventsFragment)
}

