package com.abhishekjagushte.engage.ui.chat.fragments.chat

import androidx.lifecycle.ViewModel
import com.abhishekjagushte.engage.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class ChatFragmentModule {

    @Binds
    @IntoMap
    @ViewModelKey(ChatViewModel::class)
    abstract fun bindViewModel(viewmodel: ChatViewModel): ViewModel
}

