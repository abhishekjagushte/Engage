package com.abhishekjagushte.engage.ui.chat.screens.chat

import dagger.Subcomponent


@Subcomponent(modules = [ChatFragmentModule::class])
interface ChatComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ChatComponent
    }

    fun inject(fragment: ChatFragment)
}

