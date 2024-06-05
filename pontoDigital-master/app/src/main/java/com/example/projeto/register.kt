package com.example.projeto

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.example.projeto.databinding.FragmentRegisterBinding

class register : Fragment(R.layout.fragment_register) {
    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegisterBinding.bind(view)

        auth = FirebaseAuth.getInstance()

        // Ocultar o BottomNavigationView
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNav?.visibility = View.GONE

        binding.registerButton.setOnClickListener {
            val name = binding.name.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()

            registerUser(name, email, password, confirmPassword)
        }
    }

    private fun registerUser(name: String, email: String, password: String, confirmPassword: String) {
        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(context, "As senhas não correspondem", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "User registered successfully", Toast.LENGTH_SHORT).show()

                                val navController = findNavController()
                                navController.navigate(R.id.navigation_home)
                            }
                        }
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(context, "Falha ao registrar usuário", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Mostrar o BottomNavigationView
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNav?.visibility = View.VISIBLE

        _binding = null
    }
}