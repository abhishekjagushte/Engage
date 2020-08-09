package com.abhishekjagushte.engage.ui.fragments.bindingadapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.abhishekjagushte.engage.database.entities.Contact

@BindingAdapter("setSubHeading")
fun TextView.setSubHeading(contact: Contact){
    text = contact.username
}