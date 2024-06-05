package com.example.projeto.app.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.projeto.R
import com.example.projeto.databinding.FragmentForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgotPasswordFragment : Fragment() {
    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        setListeners()
    }

    private fun setListeners() {
        binding.cancel.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.loginButton.setOnClickListener {
            binding.progressBar.isVisible = true
            val email = binding.email.text.toString().trim()
            if (email.isNotEmpty()) {
                resetPassword(email)
            } else {
                Toast.makeText(requireContext(), "Por favor, insira seu e-mail", Toast.LENGTH_SHORT).show()
                binding.progressBar.isVisible = false
            }
        }
    }

    private fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                binding.progressBar.isVisible = false
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(),
                        "Um link com as instruções para recuperar a senha foi enviado por email",
                        Toast.LENGTH_LONG).show()
                        findNavController().navigate(R.id.action_navigation_forgotPassword_to_login)
                } else {
                    Toast.makeText(requireContext(),
                        task.exception?.message,
                        Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
