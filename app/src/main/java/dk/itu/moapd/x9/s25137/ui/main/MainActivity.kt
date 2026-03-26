package dk.itu.moapd.x9.s25137.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dk.itu.moapd.x9.s25137.data.repositories.AuthRepository
import dk.itu.moapd.x9.s25137.ui.auth.LoginActivity
import dk.itu.moapd.x9.s25137.ui.theme.AppTheme

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        authRepository = AuthRepository()

        setContent {
            AppTheme {
                MainScaffold(
                    authRepository = authRepository,
                    onLogout = {
                        authRepository.signOut()
                        startLoginActivity()
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        authRepository.currentUser ?: startLoginActivity()
    }

    private fun startLoginActivity() {
        Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }.let(::startActivity)
    }
}
