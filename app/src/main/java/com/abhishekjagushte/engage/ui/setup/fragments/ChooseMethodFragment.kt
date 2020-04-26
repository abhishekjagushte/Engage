package com.abhishekjagushte.engage.ui.setup.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.abhishekjagushte.engage.R

class ChooseMethodFragment : Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.choose_method_fragment, container, false)

        val loginButton = view.findViewById<Button>(R.id.login_button)
        val signupButton = view.findViewById<Button>(R.id.signup_button)

        loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_chooseMethodFragment_to_loginFragment)
        }

        signupButton.setOnClickListener {
            findNavController().navigate(R.id.action_chooseMethodFragment_to_signUpFragment)
        }

        return view
    }

}