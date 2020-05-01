package com.abhishekjagushte.engage.ui.setup.fragments.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.abhishekjagushte.engage.R
import com.google.android.material.textfield.TextInputEditText

class SignUpFragment : Fragment() {

    private lateinit var viewModel: SignUpViewModel
    private val TAG: String = "SignUpFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.sign_up_fragment, container, false)

        val emailInput = view.findViewById<TextInputEditText>(R.id.email_input)
        val passwordInput = view.findViewById<TextInputEditText>(R.id.password_input)
        val signupButton = view.findViewById<Button>(R.id.sign_up_button)

        signupButton.setOnClickListener {
            if(emailInput.text.toString().isNotEmpty()){
                firebaseSignup(emailInput.text.toString(), passwordInput.text.toString())
            }
        }
        return view
    }

    private fun firebaseSignup(email: String, password: String) {

    }

    private fun updateUI(email: String, password: String) {
                findNavController().navigate(
                    SignUpFragmentDirections.actionSignUpFragmentToSetUsernameFragment
                        (email = email, password =  password ))
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