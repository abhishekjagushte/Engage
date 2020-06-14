package com.abhishekjagushte.engage.ui.chat.fragments.chat.fragments.eventscreen.di

import com.abhishekjagushte.engage.ui.chat.fragments.chat.fragments.eventscreen.EventScreenFragment
import dagger.Subcomponent


@Subcomponent(modules = [EventFragmentModule::class])
interface EventComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): EventComponent
    }

    fun inject(fragment: EventScreenFragment)
}

