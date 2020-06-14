package com.abhishekjagushte.engage.ui.chat.fragments.chat

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.abhishekjagushte.engage.ui.chat.fragments.chat.fragments.chatscreen.ChatScreenFragment
import com.abhishekjagushte.engage.ui.chat.fragments.chat.fragments.eventscreen.EventScreenFragment

class ChatStateAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        if (position==0){
            return ChatScreenFragment()
        }
        else{
            return EventScreenFragment()
        }
    }

}