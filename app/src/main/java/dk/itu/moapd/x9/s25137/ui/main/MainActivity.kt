package dk.itu.moapd.x9.s25137.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.x9.s25137.ui.auth.LoginActivity
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
                MainScaffold(
                    auth = auth,
                    onLogout = {
                        auth.signOut()
                        startLoginActivity()
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        auth.currentUser ?: startLoginActivity()
    }

    private fun startLoginActivity() {
        Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }.let(::startActivity)
    }
}
