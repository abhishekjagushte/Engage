package com.abhishekjagushte.engage.ui.chat.screens.chat.fragments.eventscreen.di

import com.abhishekjagushte.engage.ui.chat.screens.chat.fragments.eventscreen.EventScreenFragment
import dagger.Subcomponent


@Subcomponent(modules = [EventFragmentModule::class])
interface EventComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): EventComponent
    }

    fun inject(fragment: EventScreenFragment)
}

