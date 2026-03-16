package dk.itu.moapd.x9.s25137.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.x9.s25137.ui.theme.AppTheme

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()

        setContent {
            AppTheme {
                MainScaffold()
            }
        }
    }
}
