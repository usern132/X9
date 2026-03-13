package dk.itu.moapd.x9.s25137.ui.main

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.x9.s25137.databinding.ActivityMainBinding

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() called")
        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()

        setContent {
            MaterialTheme {

            }
        }

//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
    }
}
