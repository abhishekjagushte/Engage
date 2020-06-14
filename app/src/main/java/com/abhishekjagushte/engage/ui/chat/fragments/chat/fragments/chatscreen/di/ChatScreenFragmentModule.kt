package com.abhishekjagushte.engage.ui.chat.fragments.chat.fragments.chatscreen.di

import androidx.lifecycle.ViewModel
import com.abhishekjagushte.engage.di.ViewModelKey
import com.abhishekjagushte.engage.ui.chat.fragments.chat.fragments.chatscreen.ChatScreenViewModel
import com.abhishekjagushte.engage.ui.setup.fragments.login.LoginFragmentViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class ChatScreenFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(ChatScreenViewModel::class)
    abstract fun bindViewModel(viewmodel: ChatScreenViewModel): ViewModel
}

