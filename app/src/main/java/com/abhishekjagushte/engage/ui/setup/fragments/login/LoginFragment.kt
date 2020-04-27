package com.abhishekjagushte.engage.ui.setup.fragments.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider

import com.abhishekjagushte.engage.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.login_fragment.*

class LoginFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.login_fragment, container, false)

        val emailInput = view.findViewById<TextInputEditText>(R.id.email_input)
        val passwordInput = view.findViewById<TextInputEditText>(R.id.password_input)
        val loginButton = view.findViewById<Button>(R.id.login_button)

        loginButton.setOnClickListener {

            mAuth = FirebaseAuth.getInstance()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()){
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    task -> {

                    if(task.isSuccessful){

                    }
                    else{

                    }
                }
                }
            }

        }

        return view
    }

}
