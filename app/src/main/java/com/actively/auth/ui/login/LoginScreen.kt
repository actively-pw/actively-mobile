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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.actively.R
import com.actively.auth.ui.EmailTextField
import com.actively.auth.ui.InvalidCredentialsDialog
import com.actively.auth.ui.PasswordTextField
import com.actively.auth.ui.TextFieldState
import com.actively.ui.theme.ActivelyTheme
import org.koin.androidx.compose.getViewModel


fun NavGraphBuilder.loginScreen(navController: NavController) {
    composable("login_screen") {
        val viewModel = getViewModel<LoginViewModel>()
        val emailState by viewModel.email.collectAsState()
        val passwordState by viewModel.password.collectAsState()
        val isPasswordVisible by viewModel.isPasswordVisible.collectAsState()
        val showLoginFailedDialog by viewModel.showLoginFailedDialog.collectAsState(initial = false)
        ActivelyTheme {
            LoginScreen(
                emailState = emailState,
                passwordState = passwordState,
                onEmailChange = viewModel::onEmailChange,
                onPasswordChange = viewModel::onPasswordChange,
                onChangePasswordVisibility = viewModel::changePasswordVisibility,
                isPasswordVisible = isPasswordVisible,
                onLogin = {
                    viewModel.onSuccessfulLogin {
                        navController.navigate("authenticated_screens") {
                            popUpTo("auth_screens") { inclusive = true }
                        }
                    }
                },
                showLoginFailedDialog = showLoginFailedDialog,
                onDismissLoginFailedDialog = viewModel::onDismissLoginFailedDialog,
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
    onChangePasswordVisibility: () -> Unit,
    isPasswordVisible: Boolean,
    onLogin: () -> Unit,
    showLoginFailedDialog: Boolean,
    onDismissLoginFailedDialog: () -> Unit,
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
            if (showLoginFailedDialog) {
                InvalidCredentialsDialog(
                    message = { Text(stringResource(R.string.invalid_credentials_message)) },
                    onDismiss = onDismissLoginFailedDialog
                )
            }
            Spacer(Modifier.height(50.dp))
            Text(
                text = stringResource(R.string.log_in_screen_header),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(16.dp))
            EmailTextField(
                modifier = Modifier.fillMaxWidth(),
                value = emailState.value,
                onValueChange = onEmailChange,
                isError = !emailState.isValid,
            )
            Spacer(Modifier.height(16.dp))
            PasswordTextField(
                modifier = Modifier.fillMaxWidth(),
                value = passwordState.value,
                onValueChange = onPasswordChange,
                isError = !passwordState.isValid,
                onDone = { onLogin() },
                onChangePasswordVisibility = onChangePasswordVisibility,
                isPasswordVisible = isPasswordVisible
            )
            Spacer(Modifier.height(20.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onLogin
            ) {
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
            emailState = TextFieldState(value = "mail@co.", isValid = true),
            passwordState = TextFieldState(value = "password"),
            onEmailChange = {},
            onPasswordChange = {},
            onLogin = {},
            onChangePasswordVisibility = {},
            isPasswordVisible = true,
            showLoginFailedDialog = false,
            onDismissLoginFailedDialog = {},
            onNavigateBack = {}
        )
    }
}
