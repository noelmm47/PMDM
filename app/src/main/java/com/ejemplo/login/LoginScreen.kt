package com.ejemplo.login

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onGoToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var showPassword by remember { mutableStateOf(false) }
    var showPassword2 by remember { mutableStateOf(false) }
    //Que quieras ver lo que escribes en un campo, no significa que quieras revelar lo revelado en otro.
    var rememberSession by remember {mutableStateOf(false)}
    var enterButtonAbled by remember {mutableStateOf(false)}

    val focusManager = LocalFocusManager.current
    val emailFocusRequester = remember { FocusRequester() }
    val emailRequest= stringResource(R.string.email_request)
    val minimalPassword= stringResource(R.string.minimal_password)
    val inequivalentPasswords= stringResource(R.string.inequivalent_passwords)
    val originalPassInvalid= stringResource(R.string.original_pass_invalid)

    LaunchedEffect(Unit) {
        emailFocusRequester.requestFocus()
    }


    fun validarEmail(): Boolean {
        val emailValido = email.isNotBlank() &&
                Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
        //EMAIL_ADRESS debe ser un patrón regex generalizado
        emailError = if (!emailValido) emailRequest else null
        return emailValido
    }

    fun validarPassword(): Boolean {
        val regex = "^(?=.*[A-Z])(?=.*[0-9]).{8,}$".toRegex()
        val passValida = regex.matches(password)
        passwordError = if (!passValida) {
            minimalPassword
        } else null
        return passValida
    }

    fun validarConfirmacionPassword(): Boolean {
        if(validarPassword() && confirmPassword.equals(password)){
            confirmPasswordError = null //Borra el error, si lo hay
            return true
        } //Si es falso, no hay return y sigue a lo siguiente

        if(!validarPassword()) //La contraseña original es inválida
            confirmPasswordError = originalPassInvalid
        else //La segunda contraseña es distinta a la original
            confirmPasswordError = inequivalentPasswords
        return false

    }

    Column( //Configuración / parámetros de la columna
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) { //Contenido de la columna
        Spacer(modifier = Modifier.height(48.dp)) //Espaciado entre el inicio y el siguiente elemento

        Icon( //Logo del login
            painter = painterResource(id = R.drawable.ic_launcher_foreground), //La imagen
            contentDescription = stringResource(R.string.logo_desc),
            modifier = Modifier.size(120.dp) //Tamaño 120x120
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.titulo_bienvenida),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField( //Un tipo de campo de texto con bordes específicos
            value = email,
            onValueChange = { //Lo que se ejecuta cada vez que se cambia el valor
                email = it
                if (it.length > 2) validarEmail()
                else if(it.isEmpty()) emailError = null;
                enterButtonAbled= validarEmail() && validarConfirmacionPassword()
            },
            label = { Text(stringResource(R.string.hint_email)) },
            isError = emailError != null,
            supportingText = emailError?.let { { Text(it) } }, //Texto que sale cuando hay un error
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next //Acción tomada por el teclado
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down) //Mueve el foco al elemento que esté debajo
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(emailFocusRequester)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                if (it.isNotEmpty()) validarPassword()
                enterButtonAbled= validarEmail() && validarConfirmacionPassword()
            },
            label = { Text(stringResource(R.string.hint_password)) },
            isError = passwordError != null,
            supportingText = passwordError?.let { { Text(it) } },
            visualTransformation = if (showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation() //Oculta el texto con asteriscos
            },
            trailingIcon = { //Info del icono de visibilidad de la contraseña
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) { //Dibuja un icono u otro según el valor de showPassword
                            Icons.Filled.Lock
                        } else {
                            Icons.Filled.LockOpen
                        },
                        contentDescription = stringResource(R.string.toggle_password)
                    )
                }
            },
            keyboardOptions = KeyboardOptions( //configuración del teclado para este campo de texto
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                if (it.isNotEmpty())
                    enterButtonAbled= validarEmail() && validarConfirmacionPassword()
            },
            label = { Text(stringResource(R.string.hint_password2)) },
            isError = confirmPasswordError != null,
            supportingText = confirmPasswordError?.let { { Text(it) } },
            visualTransformation = if (showPassword2) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation() //Oculta el texto con asteriscos
            },
            trailingIcon = { //Info del icono de visibilidad de la contraseña
                IconButton(onClick = { showPassword2 = !showPassword2 }) {
                    Icon(
                        imageVector = if (showPassword2) { //Dibuja un icono u otro según el valor de showPassword
                            Icons.Filled.Lock
                        } else {
                            Icons.Filled.LockOpen
                        },
                        contentDescription = stringResource(R.string.toggle_password)
                    )
                }
            },
            keyboardOptions = KeyboardOptions( //configuración del teclado para este campo de texto
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = rememberSession,
                onCheckedChange = { rememberSession = !rememberSession }
            )
            Text(stringResource(R.string.recordar_sesion))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(verticalArrangement = Arrangement.spacedBy(0.dp)){
            Button( // El botón "Entrar"
                onClick = {
                    if (validarEmail() && validarPassword()) {
                        focusManager.clearFocus()
                        onLoginSuccess(email.trim())
                    }
                },
                enabled = enterButtonAbled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.boton_entrar))
            }

            Row(horizontalArrangement= Arrangement.End,
                modifier = Modifier.fillMaxWidth()) {
                TextButton(
                    onClick = { /* Acción */ }
                ) { //Botón "Olvidaste tu contraseña"
                    Text(stringResource(R.string.olvidaste_password))

                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onGoToRegister) {
                Text(stringResource(R.string.ya_tienes_cuenta))
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(onLoginSuccess = { _ -> }, onGoToRegister = {}) //Funciones vacías
    }
}