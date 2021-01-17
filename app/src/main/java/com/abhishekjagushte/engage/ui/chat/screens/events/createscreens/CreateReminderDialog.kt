package com.abhishekjagushte.engage.ui.chat.screens.events.createscreens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abhishekjagushte.engage.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class CreateReminderDialog : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_reminder_dialog, container, false)
    }

}