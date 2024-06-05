package com.example.projeto

import com.google.firebase.database.FirebaseDatabase

object DatabaseManager {
    val instance: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }
}
