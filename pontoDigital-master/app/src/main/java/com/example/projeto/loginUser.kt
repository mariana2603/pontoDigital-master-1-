package com.example.projeto

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.projeto.databinding.FragmentLoginUserBinding
import com.google.firebase.auth.FirebaseAuth

class loginUser : Fragment(R.layout.fragment_login_user) {
    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentLoginUserBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginUserBinding.bind(view)

        auth = FirebaseAuth.getInstance()

        // Verifique se o usuário já está logado
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // O usuário já está logado, navegue para o HomeFragment
            val navController = findNavController()
            navController.navigate(R.id.navigation_home)
            return
        }

        binding.loginButton.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            loginUser(email, password)
        }

        binding.textRegister.setOnClickListener {
            // Navigate to RegisterFragment
            val navController = findNavController()
            navController.navigate(R.id.navigation_register)
        }

        // Envia o usuario para a tela de redefinição de senha
        binding.forgotPassword.setOnClickListener {
            // Navigate to ForgotPasswordFragment
            val navController = findNavController()
            navController.navigate(R.id.action_navigation_login_to_forgotPassword)
        }

    }

    private fun loginUser(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()

                    // Navigate to HomeFragment
                    val navController = findNavController()
                    navController.navigate(R.id.navigation_home)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Rest of your code...
}