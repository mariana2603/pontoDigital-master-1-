package com.example.projeto.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.projeto.databinding.FragmentDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("times")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView: ListView = binding.listView
        val userId = auth.currentUser?.uid

        database.orderByChild("userId").equalTo(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (isAdded) { // Verifique se o fragmento ainda está anexado à atividade
                    val times = mutableListOf<String>()
                    for (postSnapshot in dataSnapshot.children) {
                        val time = postSnapshot.child("time").getValue(String::class.java)
                        time?.let { times.add(it) }
                    }
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, times)
                    listView.adapter = adapter
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Falha ao ler valor
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}