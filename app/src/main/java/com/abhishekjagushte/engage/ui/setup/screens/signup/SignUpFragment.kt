package com.abhishekjagushte.engage.ui.setup.screens.signup

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.google.android.material.textfield.TextInputEditText
import javax.inject.Inject

class SignUpFragment : Fragment() {


    private val TAG: String = "SignUpFragment"

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<SignUpFragmentViewModel> { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_sign_up, container, false)

        val emailInput = view.findViewById<TextInputEditText>(R.id.email_input)
        val passwordInput = view.findViewById<TextInputEditText>(R.id.password_input)
        val signupButton = view.findViewById<Button>(R.id.sign_up_button)
        val noteText = view.findViewById<TextView>(R.id.noteText)

        var email: String = ""
        var password: String =""

        viewModel.signUpComplete.observe(viewLifecycleOwner, Observer {
            if(it){
                updateUI(email, password)
            }
        })

        viewModel.noteText.observe( viewLifecycleOwner, Observer {
            noteText.setText(it?: "")
        })

        signupButton.setOnClickListener {
            email = emailInput.text.toString()
            password = passwordInput.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()){
                viewModel.firebaseSignup(emailInput.text.toString(), passwordInput.text.toString())
            }
        }

        return view
    }

    private fun updateUI(email: String, password: String) {
                findNavController().navigate(
                    SignUpFragmentDirections.actionSignUpFragmentToSetUsernameFragment
                        (email = email, password =  password ))
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as EngageApplication).appComponent.addSignUpComponent()
            .create().inject(this)
    }

}



/*
    Firebase Signup

     mAuth = FirebaseAuth.getInstance()

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity as Activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    Toast.makeText(context, "Account created", Toast.LENGTH_SHORT)
                    val user = mAuth.currentUser
                    updateUI(email, password)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }

                // ...
            }


 */