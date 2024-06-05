package com.example.projeto.app.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.projeto.databinding.FragmentScheduleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Schedules")
        setListeners()
    }

    private fun setListeners() {
        binding.saveButton.setOnClickListener {
            Log.d("ScheduleFragment", "Save button clicked")
            saveSchedule()
        }
    }

    private fun saveSchedule() {
        Log.d("ScheduleFragment", "saveSchedule called")

        // Check if user is authenticated
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Você precisa estar logado para agendar", Toast.LENGTH_SHORT).show()
            return
        }

        // Obter data e hora do DatePicker e TimePicker
        val day = binding.datePicker.dayOfMonth
        val month = binding.datePicker.month
        val year = binding.datePicker.year

        val hour = binding.timePicker.hour
        val minute = binding.timePicker.minute

        Log.d("ScheduleFragment", "Selected date and time: $day/$month/$year $hour:$minute")

        // Criar objeto Calendar para manipular data e hora
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)

        // Criar objeto Schedule para salvar no Firebase
        val schedule = Schedule(calendar.timeInMillis)

        // Salvar no Firebase
        binding.progressBar.isVisible = true
        databaseReference.push().setValue(schedule)
            .addOnCompleteListener { task ->
                binding.progressBar.isVisible = false
                if (task.isSuccessful) {
                    Log.d("ScheduleFragment", "Schedule saved successfully")
                    Toast.makeText(requireContext(), "Agendamento salvo com sucesso", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("ScheduleFragment", "Failed to save schedule: ${task.exception?.message}")
                    Toast.makeText(requireContext(), "Falha ao salvar agendamento: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    data class Schedule(val timestamp: Long = 0L)
}
