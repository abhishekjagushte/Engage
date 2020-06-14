package com.abhishekjagushte.engage.ui.chat.fragments.chat.fragments.chatscreen.di

import com.abhishekjagushte.engage.ui.chat.fragments.chat.fragments.chatscreen.ChatScreenFragment
import dagger.Subcomponent


@Subcomponent(modules = [ChatScreenFragmentModule::class])
interface ChatScreenComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ChatScreenComponent
    }

    fun inject(fragment: ChatScreenFragment)
}

