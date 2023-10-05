package com.actively.welcome.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.actively.ui.theme.ActivelyTheme

fun NavGraphBuilder.welcomeScreen(navController: NavController) {
    composable("welcome_screen") {
        ActivelyTheme {
            WelcomeScreen(
                onNavigateToLogIn = { navController.navigate("login_screen") },
                onNavigateToRegister = { navController.navigate("register_screen") }
            )
        }
    }
}

@Composable
fun WelcomeScreen(
    onNavigateToLogIn: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(50.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Actively",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Track your active life in one place",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(80.dp))
            Button(modifier = Modifier.fillMaxWidth(), onClick = onNavigateToRegister) {
                Text("Join for free")
            }
            OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = onNavigateToLogIn) {
                Text("Log in")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    ActivelyTheme {
        WelcomeScreen(onNavigateToLogIn = {}, onNavigateToRegister = {})
    }
}
