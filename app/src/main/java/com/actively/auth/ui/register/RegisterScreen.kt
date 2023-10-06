package com.actively.auth.ui.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.actively.R
import com.actively.auth.ui.EmailTextField
import com.actively.auth.ui.InvalidCredentialsDialog
import com.actively.auth.ui.PasswordTextField
import com.actively.field.Field
import com.actively.ui.theme.ActivelyTheme
import org.koin.androidx.compose.getViewModel

fun NavGraphBuilder.registerScreen(navController: NavController) {
    composable("register_screen") {
        val viewModel = getViewModel<RegisterViewModel>()
        val nameState by viewModel.name.collectAsState()
        val surnameState by viewModel.surname.collectAsState()
        val emailState by viewModel.email.collectAsState()
        val passwordState by viewModel.password.collectAsState()
        val isPasswordVisible by viewModel.isPasswordVisible.collectAsState()
        val showRegistrationFailedDialog by viewModel.showRegistrationFailedDialog
            .collectAsState(initial = false)
        ActivelyTheme {
            RegisterScreen(
                nameState = nameState,
                surnameState = surnameState,
                onNameChange = viewModel::onNameChange,
                onSurnameChange = viewModel::onSurnameChange,
                emailState = emailState,
                passwordState = passwordState,
                onEmailChange = viewModel::onEmailChange,
                onPasswordChange = viewModel::onPasswordChange,
                onRegister = {
                    viewModel.onSuccessfulRegister {
                        navController.navigate("authenticated_screens") {
                            popUpTo("auth_screens") { inclusive = true }
                        }
                    }
                },
                isPasswordVisible = isPasswordVisible,
                onChangePasswordVisibility = viewModel::changePasswordVisibility,
                showInvalidCredentialsDialog = showRegistrationFailedDialog,
                onDismissInvalidCredentialsDialog = viewModel::onDismissRegistrationFailedDialog,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    nameState: Field.State,
    surnameState: Field.State,
    onNameChange: (String) -> Unit,
    onSurnameChange: (String) -> Unit,
    emailState: Field.State,
    passwordState: Field.State,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRegister: () -> Unit,
    isPasswordVisible: Boolean,
    onChangePasswordVisibility: () -> Unit,
    showInvalidCredentialsDialog: Boolean,
    onDismissInvalidCredentialsDialog: () -> Unit,
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
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            if (showInvalidCredentialsDialog) {
                InvalidCredentialsDialog(
                    message = {
                        Text(stringResource(R.string.email_already_taken))
                    },
                    onDismiss = onDismissInvalidCredentialsDialog
                )
            }
            Spacer(Modifier.height(50.dp))
            Text(
                text = stringResource(R.string.create_an_account),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = nameState.value,
                onValueChange = onNameChange,
                isError = !nameState.isValid,
                label = { Text(stringResource(R.string.name)) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next,
                )
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = surnameState.value,
                onValueChange = onSurnameChange,
                isError = !surnameState.isValid,
                label = { Text(stringResource(R.string.surname)) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next,
                )
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
                onDone = { onRegister() },
                isPasswordVisible = isPasswordVisible,
                onChangePasswordVisibility = onChangePasswordVisibility,
            )
            Spacer(Modifier.height(20.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onRegister
            ) {
                Text(stringResource(R.string.sign_up))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    ActivelyTheme {
        RegisterScreen(
            nameState = Field.State(value = "John", isValid = true),
            surnameState = Field.State(value = "Smith", isValid = true),
            onNameChange = {},
            onSurnameChange = {},
            emailState = Field.State(value = "mail@co.", isValid = true),
            passwordState = Field.State(value = "password", isValid = false),
            onEmailChange = {},
            onPasswordChange = {},
            onRegister = {},
            onNavigateBack = {},
            isPasswordVisible = false,
            onChangePasswordVisibility = {},
            showInvalidCredentialsDialog = false,
            onDismissInvalidCredentialsDialog = {}
        )
    }
}
