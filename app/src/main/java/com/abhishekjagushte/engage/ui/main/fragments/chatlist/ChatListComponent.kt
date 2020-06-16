package com.abhishekjagushte.engage.ui.main.fragments.chatlist

import dagger.Subcomponent


@Subcomponent(modules = [ChatListFragmentModule::class])
interface ChatListComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ChatListComponent
    }

    fun inject(fragment: ChatListFragment)
}

