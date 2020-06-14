package com.abhishekjagushte.engage.ui.chat.fragments.chat.fragments.chatscreen

import androidx.lifecycle.ViewModel
import com.abhishekjagushte.engage.repository.DataRepository
import javax.inject.Inject

class ChatScreenViewModel @Inject constructor(
    dataRepository: DataRepository
): ViewModel() {

    private lateinit var sharedViewModel: ViewModel


}