package com.example.PropertyFinder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.PropertyFinder.API.ApiClient
import com.example.PropertyFinder.DataClass.ServerResponse
import com.example.PropertyFinder.DataClass.User
import com.example.PropertyFinder.Utils.ErrorHandler
import com.example.PropertyFinder.Utils.isEmailValid
import com.example.PropertyFinder.Utils.isPasswordValid
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Register : Fragment() {
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
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        val usernameEditText = view.findViewById<EditText>(R.id.emailInput)
        val passwordEditText = view.findViewById<EditText>(R.id.passwordInput)
        val registerButton : Button = view.findViewById(R.id.registerButton)
        val arrowButton = view.findViewById<ImageButton>(R.id.back_button)

        val navController = view.findNavController()
        arrowButton.setOnClickListener {
            val action = RegisterDirections.actionRegisterToLogin()
            findNavController().navigate(action)

        }


        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()


            if (username.isEmpty()) {
                usernameEditText.error = "Username required"
            } else if (password.isEmpty()) {
                passwordEditText.error = "Password required"
            } else if (!isEmailValid(username)) {
                usernameEditText.error = "Email invalid"
            } else if (!isPasswordValid(password)) {
                passwordEditText.error = "Password invalid"
            }
                else {
                ApiClient.client.register(User(username, password))
                    .enqueue(object : Callback<ServerResponse> {
                        override fun onResponse(
                            call: Call<ServerResponse>,
                            response: Response<ServerResponse>
                        ) {
                            if (response.isSuccessful && response.body()?.success == true) {
                                navController.navigate(R.id.action_register_to_login)
                            }
                            else {
                                val errorMessage = ErrorHandler.extractServerError(response.errorBody())
                                    ?: "Errore sconosciuto"

                                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()                                //ErrorHandler.handleResponse(requireContext(), response)

                            }

            }
                        override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                            ErrorHandler.handleFailure(requireContext(), t)

                        }
        })
    }

}


}
}