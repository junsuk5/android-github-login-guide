@file:OptIn(ExperimentalMaterial3Api::class)

package com.surivalcoding.githubloginguide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.surivalcoding.githubloginguide.data.GithubLogin
import com.surivalcoding.githubloginguide.domain.SocialLogin
import com.surivalcoding.githubloginguide.ui.theme.GithubLoginGuideTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GithubLoginGuideTheme {
                val viewModel: SocialLoginViewModel = viewModel(
                    factory = viewModelFactory {
                        addInitializer(SocialLoginViewModel::class) {
                            SocialLoginViewModel(GithubLogin(this@MainActivity))
                        }
                    }
                )

                val isLogin by viewModel.isLogin.collectAsState()
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen(
                        loginStatus = isLogin,
                        onGithubLoginClick = {
                            if (!isLogin) {
                                viewModel.login()
                            } else {
                                viewModel.logout()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    loginStatus: Boolean = false,
    onGithubLoginClick: () -> Unit = {},
) {
    Scaffold {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onGithubLoginClick,
            ) {
                Text(text = if (!loginStatus) "Github Login" else "Logout")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GithubLoginGuideTheme {
        LoginScreen(
            loginStatus = true
        )
    }
}

class SocialLoginViewModel(private val socialLogin: SocialLogin) : ViewModel() {
    var isLogin = MutableStateFlow(false)
        private set

    init {
        Firebase.auth.addAuthStateListener {
            isLogin.value = it.currentUser != null
        }
    }

    fun login() {
        viewModelScope.launch {
            socialLogin.login()
        }
    }

    fun logout() {
        viewModelScope.launch {
            socialLogin.logout()
        }
    }
}