package com.abhishekjagushte.engage.ui.setup.fragments.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.abhishekjagushte.engage.EngageApplication
import com.abhishekjagushte.engage.R
import com.abhishekjagushte.engage.utils.Constants
import com.google.android.material.textfield.TextInputEditText
import javax.inject.Inject

class LoginFragment : Fragment() {


    private val TAG: String = "LoginFragment"

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<LoginFragmentViewModel> { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.login_fragment, container, false)

        val emailInput = view.findViewById<TextInputEditText>(R.id.email_input)
        val passwordInput = view.findViewById<TextInputEditText>(R.id.password_input)
        val loginButton = view.findViewById<Button>(R.id.login_button)


        viewModel.completeStatus.observe(viewLifecycleOwner, Observer {
            when(it){
                Constants.LOCAL_DB_SUCCESS -> {
                    Log.d(TAG, "Successfully logged in")
                }
            }
        })

        loginButton.setOnClickListener {

            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()){
                viewModel.login(email,password)
            }

        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as EngageApplication).appComponent.addLoginComponent()
            .create().inject(this)
    }

}
