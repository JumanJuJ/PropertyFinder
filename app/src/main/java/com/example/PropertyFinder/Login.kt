package com.example.PropertyFinder

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.PropertyFinder.API.ApiClient
import com.example.PropertyFinder.DataClass.ServerResponse
import com.example.PropertyFinder.DataClass.User
import com.example.PropertyFinder.Utils.ErrorHandler
import com.example.PropertyFinder.Utils.isEmailValid
import com.example.PropertyFinder.Utils.isPasswordValid
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginButton = view.findViewById<Button>(R.id.loginButton)
        val registerButton = view.findViewById<Button>(R.id.registerButton)
        val emailInput = view.findViewById<EditText>(R.id.emailInput)
        val passwordInput = view.findViewById<EditText>(R.id.passwordInput)

        loginButton.setOnClickListener {
            val username = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username.isEmpty()) {
                emailInput.error = "Email required"
                return@setOnClickListener
            }

            if (!isEmailValid(username)) {
                emailInput.error = "Email invalid"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                passwordInput.error = "Password required"
                return@setOnClickListener
            }

            if (!isPasswordValid(password)) {
                passwordInput.error = "Password too weak"
                return@setOnClickListener
            }

            ApiClient.client.login(User(username, password))
                .enqueue(object : Callback<ServerResponse> {
                    override fun onResponse(
                        call: Call<ServerResponse>,
                        response: Response<ServerResponse>
                    ) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            val userId = response.body()?.userId
                            if (!userId.isNullOrEmpty()) {
                                val action = LoginDirections.actionLoginToHome2(userId)
                                view.findNavController().navigate(action)
                                Log.e("Error", "userId = ${userId}")
                            } else {
                                Log.e("Error", "userId = null o vuoto")
                            }


                        } else {
                            val errorMessage = ErrorHandler.extractServerError(response.errorBody())
                                ?: "Errore durante il login"
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                        ErrorHandler.handleFailure(requireContext(), t)
                    }
                })
        }
        registerButton.setOnClickListener {
            val navController = view.findNavController()
            navController.navigate(R.id.action_login_to_register)
        }

    }
}

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AddMyListing : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_my_listings, container, false)
    }


}