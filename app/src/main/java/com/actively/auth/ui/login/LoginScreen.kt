package com.actively.auth.ui.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.actively.R
import com.actively.auth.ui.EmailTextField
import com.actively.auth.ui.PasswordTextField
import com.actively.auth.ui.TextFieldState
import com.actively.ui.theme.ActivelyTheme


fun NavGraphBuilder.loginScreen(navController: NavController) {
    composable("login_screen") {
        var emailState by remember {
            mutableStateOf(TextFieldState(""))
        }
        var passwordState by remember {
            mutableStateOf(TextFieldState(""))
        }
        ActivelyTheme {
            LoginScreen(
                emailState = emailState,
                passwordState = passwordState,
                onEmailChange = { emailState = emailState.copy(value = it) },
                onPasswordChange = { passwordState = passwordState.copy(value = it) },
                onNavigateBack = { navController.popBackStack() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    emailState: TextFieldState,
    passwordState: TextFieldState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp),
        ) {
            Spacer(Modifier.height(50.dp))
            Text(
                text = stringResource(R.string.log_in_screen_header),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(16.dp))
            EmailTextField(
                modifier = Modifier.fillMaxWidth(),
                value = emailState.value,
                onValueChange = onEmailChange
            )
            Spacer(Modifier.height(16.dp))
            PasswordTextField(
                modifier = Modifier.fillMaxWidth(),
                value = passwordState.value,
                onValueChange = onPasswordChange,
                onDone = { }
            )
            Spacer(Modifier.height(20.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { }) {
                Text(stringResource(R.string.log_in))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    ActivelyTheme {
        LoginScreen(
            emailState = TextFieldState(value = "mail@co.", isError = true, "Invalid mail"),
            passwordState = TextFieldState(value = "password"),
            onEmailChange = {},
            onPasswordChange = {},
            onNavigateBack = {}
        )
    }
}
