package com.raktavahini.app.ui.auth

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.telephony.SmsManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun LoginScreen(onLogin: () -> Unit) {
    val context = LocalContext.current
    val showPhone = remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFD32F2F), Color(0xFF7A0F14))
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Logo",
                tint = Color.White,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Rakta Vahini", color = Color.White, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Give Blood. Save Life.", color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.titleMedium)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { showPhone.value = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (showPhone.value) Color.White else Color.Transparent,
                    contentColor = if (showPhone.value) Color(0xFF7A0F14) else Color.White
                ),
                shape = RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp),
                modifier = Modifier.weight(1f).height(48.dp)
            ) { Text("Phone OTP", fontWeight = FontWeight.Bold) }
            
            Button(
                onClick = { showPhone.value = false },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!showPhone.value) Color.White else Color.Transparent,
                    contentColor = if (!showPhone.value) Color(0xFF7A0F14) else Color.White
                ),
                shape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp),
                modifier = Modifier.weight(1f).height(48.dp)
            ) { Text("Google", fontWeight = FontWeight.Bold) }
        }

        if (showPhone.value) {
            PhoneOtpFlow(onVerified = { profile ->
                AuthStore.saveProfile(context, profile)
                AuthStore.setLoggedIn(context, true)
                onLogin()
            })
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color(0xFF7A0F14))
                    Text("Sign in with Google", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF7A0F14))
                    Text("Use your Google account to quickly sign in and access the app features.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            Toast.makeText(context, "Signed in with Google (simulated)", Toast.LENGTH_SHORT).show()
                            AuthStore.setLoggedIn(context, true)
                            onLogin()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7A0F14), contentColor = Color.White),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Continue", fontSize = androidx.compose.ui.unit.TextUnit(16f, androidx.compose.ui.unit.TextUnitType.Sp), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhoneOtpFlow(onVerified: (UserProfile) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fusedLocationClient = remember(context) { LocationServices.getFusedLocationProviderClient(context) }

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var bloodGroupExpanded by remember { mutableStateOf(false) }
    val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "Bombay Blood Group")
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var currentLocation by remember { mutableStateOf("") }
    var enteredOtp by remember { mutableStateOf("") }
    var generatedOtp by remember { mutableStateOf<String?>(null) }
    var otpSent by remember { mutableStateOf(false) }
    var locationPermissionPending by remember { mutableStateOf(false) }

    val smsPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(context, "SMS permission denied", Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }
        val otpToSend = generatedOtp
        if (otpToSend == null) return@rememberLauncherForActivityResult
        if (sendOtpSms(phoneNumber, otpToSend)) {
            otpSent = true
            Toast.makeText(context, "OTP sent successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed to send OTP", Toast.LENGTH_SHORT).show()
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted && locationPermissionPending) {
            locationPermissionPending = false
            fetchCurrentLocation(context, fusedLocationClient) { currentLocation = it }
        } else if (locationPermissionPending) {
            locationPermissionPending = false
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    fun validateRequiredFields(): Boolean {
        if (firstName.isBlank() || lastName.isBlank() || bloodGroup.isBlank() || phoneNumber.isBlank() || currentLocation.isBlank()) {
            Toast.makeText(context, "Please fill in all mandatory fields", Toast.LENGTH_LONG).show()
            return false
        }
        if (phoneNumber.length < 10) {
            Toast.makeText(context, "Enter a valid phone number", Toast.LENGTH_SHORT).show()
            return false
        }
        if (email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Enter a valid email address", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    fun sendOtp() {
        if (!validateRequiredFields()) return
        val newOtp = Random.nextInt(100000, 1000000).toString()
        generatedOtp = newOtp
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            if (sendOtpSms(phoneNumber, newOtp)) {
                otpSent = true
                Toast.makeText(context, "OTP sent successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to send OTP", Toast.LENGTH_SHORT).show()
            }
        } else {
            smsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
        }
    }

    fun verifyAndContinue() {
        if (generatedOtp == null || !otpSent) {
            Toast.makeText(context, "Please request OTP first", Toast.LENGTH_SHORT).show()
            return
        }
        if (enteredOtp != generatedOtp) {
            Toast.makeText(context, "Incorrect OTP", Toast.LENGTH_SHORT).show()
            return
        }
        val profile = UserProfile(
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            bloodGroup = bloodGroup.trim(),
            dateOfBirth = "",
            currentLocation = currentLocation.trim(),
            phoneNumber = phoneNumber.trim(),
            email = email.trim()
        )
        scope.launch { onVerified(profile) }
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color(0xFF7A0F14),
        focusedLabelColor = Color(0xFF7A0F14),
        cursorColor = Color(0xFF7A0F14)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            Text("Create Profile", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF7A0F14))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name *") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = textFieldColors
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name *") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = textFieldColors
                )
            }

            ExposedDropdownMenuBox(
                expanded = bloodGroupExpanded,
                onExpandedChange = { bloodGroupExpanded = !bloodGroupExpanded }
            ) {
                OutlinedTextField(
                    value = bloodGroup,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Blood Group *") },
                    leadingIcon = { Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFFD32F2F)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodGroupExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    singleLine = true,
                    colors = textFieldColors
                )
                ExposedDropdownMenu(
                    expanded = bloodGroupExpanded,
                    onDismissRequest = { bloodGroupExpanded = false }
                ) {
                    bloodGroups.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                bloodGroup = selectionOption
                                bloodGroupExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it.filter { ch -> ch.isDigit() } },
                label = { Text("Phone Number *") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Color.Gray) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = textFieldColors
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Gmail / Email (Optional)") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.Gray) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = textFieldColors
            )

            OutlinedTextField(
                value = currentLocation,
                onValueChange = { currentLocation = it },
                label = { Text("Location *") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray) },
                trailingIcon = {
                    IconButton(onClick = {
                        val fineGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        val coarseGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        if (fineGranted || coarseGranted) {
                            fetchCurrentLocation(context, fusedLocationClient) { currentLocation = it }
                        } else {
                            locationPermissionPending = true
                            locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                        }
                    }) {
                        Icon(Icons.Default.MyLocation, contentDescription = "Get Location", tint = Color(0xFF7A0F14))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = textFieldColors
            )

            Button(
                onClick = { sendOtp() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFDECEC), contentColor = Color(0xFF7A0F14))
            ) {
                Text(if (otpSent) "Resend OTP" else "Get OTP", fontWeight = FontWeight.Bold)
            }

            if (otpSent) {
                OutlinedTextField(
                    value = enteredOtp,
                    onValueChange = { enteredOtp = it.filter { ch -> ch.isDigit() }.take(6) },
                    label = { Text("Enter 6-digit OTP") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = textFieldColors
                )
            }

            Button(
                onClick = { verifyAndContinue() },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7A0F14), contentColor = Color.White)
            ) {
                Text(if (otpSent) "Verify & Login" else "Login", fontSize = androidx.compose.ui.unit.TextUnit(16f, androidx.compose.ui.unit.TextUnitType.Sp), fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun sendOtpSms(phoneNumber: String, otp: String): Boolean {
    return try {
        val smsManager = SmsManager.getDefault()
        val message = "Your Rakta Vahini OTP is $otp. It is valid for one-time verification."
        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        true
    } catch (_: Exception) {
        false
    }
}

@SuppressLint("MissingPermission")
private fun fetchCurrentLocation(
    context: android.content.Context,
    fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient,
    onLocationReady: (String) -> Unit
) {
    val cancellationTokenSource = CancellationTokenSource()
    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
        .addOnSuccessListener { location: Location? ->
            if (location == null) {
                Toast.makeText(context, "Could not fetch current location", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }
            val readableLocation = try {
                val geocoder = Geocoder(context)
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                val address = addresses?.firstOrNull()
                val placeName = buildString {
                    address?.locality?.let { append(it) }
                    address?.subAdminArea?.let { if (isNotEmpty()) append(", ") ; append(it) }
                    address?.adminArea?.let { if (isNotEmpty()) append(", ") ; append(it) }
                }
                placeName.ifBlank { "${location.latitude}, ${location.longitude}" }
            } catch (_: Exception) {
                "${location.latitude}, ${location.longitude}"
            }
            onLocationReady(readableLocation)
            Toast.makeText(context, "Location captured", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to fetch location", Toast.LENGTH_SHORT).show()
        }
}

@Composable
fun ProfileForm(initial: UserProfile? = null, onSave: (UserProfile) -> Unit) {
    val first = remember { mutableStateOf(initial?.firstName ?: "") }
    val last = remember { mutableStateOf(initial?.lastName ?: "") }
    val blood = remember { mutableStateOf(initial?.bloodGroup ?: "") }
    val dob = remember { mutableStateOf(initial?.dateOfBirth ?: "") }
    val location = remember { mutableStateOf(initial?.currentLocation ?: "") }
    val phone = remember { mutableStateOf(initial?.phoneNumber ?: "") }
    val email = remember { mutableStateOf(initial?.email ?: "") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(8.dp)) {
        OutlinedTextField(value = first.value, onValueChange = { first.value = it }, label = { Text("First name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = last.value, onValueChange = { last.value = it }, label = { Text("Last name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = blood.value, onValueChange = { blood.value = it }, label = { Text("Blood group") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = dob.value, onValueChange = { dob.value = it }, label = { Text("Date of birth") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = phone.value, onValueChange = { phone.value = it }, label = { Text("Phone number") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = email.value, onValueChange = { email.value = it }, label = { Text("Gmail / Email") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = location.value, onValueChange = { location.value = it }, label = { Text("Current location") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = {
            onSave(UserProfile(first.value, last.value, blood.value, dob.value, location.value, phone.value, email.value))
        }, modifier = Modifier.align(Alignment.End)) { Text("Save profile") }
    }
}
