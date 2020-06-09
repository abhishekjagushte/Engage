package com.abhishekjagushte.engage.ui.chat.fragments.chat

import dagger.Subcomponent


@Subcomponent(modules = [ChatFragmentModule::class])
interface ChatComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ChatComponent
    }

    fun inject(fragment: ChatFragment)
}

