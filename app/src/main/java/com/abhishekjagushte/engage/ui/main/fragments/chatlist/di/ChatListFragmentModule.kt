package com.abhishekjagushte.engage.ui.main.fragments.chatlist.di

import androidx.lifecycle.ViewModel
import com.abhishekjagushte.engage.di.ViewModelKey
import com.abhishekjagushte.engage.ui.main.fragments.chatlist.ChatListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class ChatListFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(ChatListViewModel::class)
    abstract fun bindViewModel(viewmodel: ChatListViewModel): ViewModel
}

