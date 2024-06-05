package com.example.projeto.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.projeto.R
import com.example.projeto.databinding.FragmentHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var database: FirebaseDatabase
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialize o Firebase Realtime Database
        database = FirebaseDatabase.getInstance()

        // Inicialize o FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Solicite as permissões de localização
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Adicione um ouvinte de clique ao botão "Bater Ponto"
        binding.btnBaterPonto.setOnClickListener {
            registerTime()
        }

        // Adicione um ouvinte de clique ao botão "logoutButton"
        binding.logoutButton.setOnClickListener {
            // Desloga o usuário
            FirebaseAuth.getInstance().signOut()

            // Navega de volta para a tela de login
            findNavController().navigate(R.id.navigation_login)
        }
    }

    private fun registerTime() {
        // Verifique se a permissão de localização foi concedida
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Permissão de localização necessária", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtenha a localização atual
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            // Verifique se a localização está disponível
            if (location == null) {
                Toast.makeText(context, "Não foi possível obter a localização", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }

            // Verifique se o usuário está dentro da área permitida (Brasil)
            val brazilLocation = Location("").apply {

                //localização do Brasil
                latitude = -10.3333333
                longitude = -53.2

                //localização de Nova York para teste
                //latitude = 40.7128
                //longitude = -74.0060
            }
            val distanceToBrazil = location.distanceTo(brazilLocation)

            if (distanceToBrazil > 5000000) { // Altere este valor para ajustar a área permitida
                Toast.makeText(context, "Você só pode registrar o ponto se estiver no Brasil", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }

            // Obtenha a data e a hora atuais no formato brasileiro
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale("pt", "BR"))
            sdf.timeZone = TimeZone.getTimeZone("America/Sao_Paulo")
            val currentDateAndTime = sdf.format(Date())

            // Registre a data e a hora no Firebase Realtime Database
            val myRef = database.getReference("times")
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val record = mapOf(
                "userId" to userId,
                "time" to currentDateAndTime
            )
            myRef.push().setValue(record).addOnSuccessListener {
                // Mostre a mensagem de sucesso com a data e a hora
                Toast.makeText(context, "Ponto registrado com sucesso: $currentDateAndTime", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                // Mostre a mensagem de erro
                Toast.makeText(context, "Erro ao registrar o ponto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // A permissão foi concedida
            } else {
                // A permissão foi negada
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}