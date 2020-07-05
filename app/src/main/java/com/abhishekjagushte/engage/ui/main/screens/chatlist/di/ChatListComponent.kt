package com.abhishekjagushte.engage.ui.main.screens.chatlist.di

import com.abhishekjagushte.engage.ui.main.screens.chatlist.ChatListFragment
import dagger.Subcomponent


@Subcomponent(modules = [ChatListFragmentModule::class])
interface ChatListComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ChatListComponent
    }

    fun inject(fragment: ChatListFragment)
}

