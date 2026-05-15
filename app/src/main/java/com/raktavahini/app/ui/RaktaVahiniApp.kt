@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.raktavahini.app.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TextButton
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.compose.runtime.rememberCoroutineScope
import android.content.Intent
import android.provider.CallLog
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.material3.DividerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
import com.raktavahini.app.data.local.AppDatabaseProvider
import com.raktavahini.app.ui.auth.AuthStore
import com.raktavahini.app.ui.auth.LoginScreen
import com.raktavahini.app.ui.auth.ProfileForm
import com.raktavahini.app.ui.auth.UserProfile
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.raktavahini.app.ui.theme.RaktaRed
import com.raktavahini.app.ui.theme.RaktaSoftRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.net.Uri
import com.raktavahini.app.data.local.entity.DonorEntity
import java.util.UUID
import kotlinx.coroutines.launch
import kotlin.math.round

@Composable
fun RaktaVahiniApp() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    var isLoggedIn by remember { mutableStateOf(AuthStore.isLoggedIn(context)) }
    var currentUserProfile by remember { mutableStateOf(AuthStore.getProfile(context)) }
    var showProfilePage by remember { mutableStateOf(false) }
    
    val tabs = listOf(
        BottomTab("Home", Icons.Default.Home),
        BottomTab("Search", Icons.Default.Search),
        BottomTab("Donate", Icons.Default.Bloodtype),
        BottomTab("History", Icons.AutoMirrored.Filled.DirectionsWalk),
        BottomTab("Call Logs", Icons.Default.Call)
    )

    if (!isLoggedIn) {
        LoginScreen(onLogin = {
            AuthStore.setLoggedIn(context, true)
            isLoggedIn = true
            currentUserProfile = AuthStore.getProfile(context)
        })
        return
    }

    Surface(color = MaterialTheme.colorScheme.background) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Rakta-Vahini", fontWeight = FontWeight.Bold, color = Color.White) },
                    colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                        containerColor = RaktaRed,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    ),
                    actions = {
                        IconButton(onClick = {
                            Toast.makeText(context, "No new notifications", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                        }
                        IconButton(onClick = { showProfilePage = true }) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    tabs.forEachIndexed { index, tab ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.label
                                )
                            },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            if (currentUserProfile == null) {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors()
                    ) {
                        ProfileForm(initial = null, onSave = { userProfile ->
                            AuthStore.saveProfile(context, userProfile)
                            currentUserProfile = userProfile
                        })
                    }
                }
            } else {
                AppContent(
                    modifier = Modifier.padding(innerPadding),
                    selectedTab = selectedTab,
                    isCallLogsTabActive = selectedTab == 4,
                    profile = currentUserProfile
                )
            }
        }
    }

    if (showProfilePage) {
        ProfilePage(
            profile = currentUserProfile,
            onClose = { showProfilePage = false },
            onLogout = {
                AuthStore.clearSession(context)
                showProfilePage = false
                isLoggedIn = false
                currentUserProfile = null
                selectedTab = 0
            },
            onEdit = {
                currentUserProfile?.let { profile ->
                    AuthStore.saveProfile(context, profile)
                }
            }
        )
    }
}

@Composable
private fun ProfilePage(
    profile: UserProfile?,
    onClose: () -> Unit,
    onLogout: () -> Unit,
    onEdit: () -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White,
                            RaktaSoftRed.copy(alpha = 0.16f)
                        )
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close profile")
                    }
                },
                actions = {
                    TextButton(onClick = onEdit) { Text("Edit") }
                    TextButton(onClick = onLogout) { Text("Logout") }
                }
            )

            if (profile == null) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("No profile found")
                        Text("Please complete login to save your details in the database.")
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = RaktaRed)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(58.dp)
                                    .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(18.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = profile.firstName.take(1).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.headlineSmall
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${profile.firstName} ${profile.lastName}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = profile.currentLocation,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }

                        AssistChip(
                            onClick = { },
                            label = { Text(profile.bloodGroup) },
                            leadingIcon = { Icon(Icons.Default.Bloodtype, contentDescription = null) }
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("Personal details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        ProfileDetailRow(label = "First name", value = profile.firstName)
                        ProfileDetailRow(label = "Last name", value = profile.lastName)
                        ProfileDetailRow(label = "Blood group", value = profile.bloodGroup)
                        ProfileDetailRow(label = "Phone number", value = profile.phoneNumber)
                        if (profile.email.isNotBlank()) {
                            ProfileDetailRow(label = "Gmail / Email", value = profile.email)
                        }
                        ProfileDetailRow(label = "Location", value = profile.currentLocation)
                    }
                }
            }

            Button(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
                Text("Back to Home")
            }
        }
    }
}

@Composable
private fun ProfileDetailRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun AppContent(
    modifier: Modifier = Modifier,
    selectedTab: Int,
    isCallLogsTabActive: Boolean,
    profile: UserProfile?
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val donorDao = remember { AppDatabaseProvider.get(context).donorDao() }
    val donorEntries by donorDao.observeAllDonors().collectAsState(initial = emptyList())
    
    val realDonors = remember(donorEntries) {
        donorEntries.map { entity ->
            val isEligible = entity.lastDonationAtEpochMillis == null || 
                             (System.currentTimeMillis() - entity.lastDonationAtEpochMillis > 90L * 24 * 60 * 60 * 1000)
            DonorCardUiModel(
                name = entity.fullName,
                bloodGroup = entity.bloodGroup,
                city = entity.city,
                distanceKm = round(Math.random() * 15 * 10) / 10.0,
                lastDonationLabel = if (isEligible) "Eligible now" else "Donation in 30 days",
                phoneNumber = entity.phoneNumber
            )
        }
    }
    
    val currentDonors = if (realDonors.isEmpty()) SAMPLE_DONORS else realDonors

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White,
                        RaktaSoftRed.copy(alpha = 0.18f)
                    )
                )
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        var query by remember { mutableStateOf("") }
        var radiusKm by remember { mutableIntStateOf(10) }
        var eligible by remember { mutableStateOf(false) }

        val donorResults = remember(currentDonors, query, radiusKm, eligible) {
            val q = query.trim()
            val filtered = currentDonors.filter { donor ->
                val matchesQuery = if (q.isBlank()) true else donor.bloodGroup.equals(q, ignoreCase = true) || donor.city.contains(q, ignoreCase = true) || donor.name.contains(q, ignoreCase = true)
                val withinRadius = donor.distanceKm <= radiusKm
                matchesQuery && withinRadius
            }
            if (eligible) filtered.filter { it.lastDonationLabel.contains("Eligible", ignoreCase = true) } else filtered
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 700.dp)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (profile != null) {
                item { Greeting(profile) }
            }
            item { HeroCard() }
            if (realDonors.isEmpty()) {
                item {
                    Button(
                        onClick = {
                            scope.launch {
                                val testDonors = listOf(
                                    DonorEntity(UUID.randomUUID().toString(), "Ramesh Iyer", "O+", "Raipur", 21.2514, 81.6296, "9876543210"),
                                    DonorEntity(UUID.randomUUID().toString(), "Priya Sharma", "A-", "Bilaspur", 22.0797, 82.1409, "9876543211", lastDonationAtEpochMillis = System.currentTimeMillis() - 20L * 24 * 60 * 60 * 1000),
                                    DonorEntity(UUID.randomUUID().toString(), "Karan Singh", "B+", "Durg", 21.1904, 81.2849, "9876543212")
                                )
                                donorDao.upsertDonors(testDonors)
                                Toast.makeText(context, "Test data loaded!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Load Test Data into Local DB") }
                }
            }
            item {
                SearchCard(onSearch = { q, r, e ->
                    query = q; radiusKm = r; eligible = e
                })
            }
            item { QuickActionsRow() }
            item {
                when (selectedTab) {
                    0 -> DonorListSection(title = "Nearby eligible donors", donors = donorResults)
                    1 -> DonorListSection(title = "Precision search results", donors = donorResults)
                    2 -> DonateGuideCard()
                    3 -> DonationHistoryCard()
                    else -> CallLogsCard(isActive = isCallLogsTabActive)
                }
            }
        }
    }
}

@Composable
private fun HeroCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = RaktaRed),
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            QuoteCarousel()
        }
    }
}

private val SAMPLE_QUOTES = listOf(
    "Donating blood is giving the gift of life.",
    "A small act of kindness, a lifetime of hope — donate blood.",
    "Be a hero. Donate blood and save a life today.",
    "Your blood can give someone a second chance at life.",
    "Give blood. Give life. It’s in you to give."
)

@Composable
private fun QuoteCarousel() {
    val context = LocalContext.current
    var index by remember { mutableStateOf(0) }
    val quotes = SAMPLE_QUOTES
    LaunchedEffect(quotes) {
        while (true) {
            delay(5000)
            index = (index + 1) % quotes.size
        }
    }

    val quote = quotes.getOrNull(index) ?: "Donate blood, save lives."
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "“$quote”", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, quote)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Share quote"))
                }) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share")
                }
                FilledTonalButton(onClick = {
                    Toast.makeText(context, "Thanks for keeping the spirit alive!", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Inspire")
                }
            }
        }
    }
}

@Composable
private fun SearchCard(onSearch: (query: String, radiusKm: Int, eligibleOnly: Boolean) -> Unit) {
    var query by remember { mutableStateOf("") }
    var selectedRadiusKm by remember { mutableIntStateOf(10) }
    var eligibleOnly by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Search donor directory",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Blood group, city, or specialty") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AssistChip(
                    onClick = { selectedRadiusKm = 10 },
                    label = { Text(if (selectedRadiusKm == 10) "10 km selected" else "10 km") }
                )
                AssistChip(
                    onClick = { selectedRadiusKm = 20 },
                    label = { Text(if (selectedRadiusKm == 20) "20 km selected" else "20 km") }
                )
                AssistChip(onClick = { eligibleOnly = !eligibleOnly }, label = { Text(if (eligibleOnly) "Eligible only: ON" else "Eligible only") })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { onSearch(query, selectedRadiusKm, eligibleOnly) }) {
                    Text("Search")
                }
                Text("Showing results for: ${if (query.isBlank()) "all" else query}")
            }
        }
    }
}

@Composable
private fun QuickActionsRow() {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val isCompact = maxWidth < 440.dp
        if (isCompact) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionTile(
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.Bloodtype,
                    title = "Log donation",
                    subtitle = "Starts thank-you flow"
                )
                ActionTile(
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.LocationOn,
                    title = "Use my location",
                    subtitle = "Radius matched"
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Bloodtype,
                    title = "Log donation",
                    subtitle = "Starts thank-you flow"
                )
                ActionTile(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.LocationOn,
                    title = "Use my location",
                    subtitle = "Radius matched"
                )
            }
        }
    }
}

@Composable
private fun ActionTile(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, contentDescription = null, tint = RaktaRed)
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DonorListSection(title: String, donors: List<DonorCardUiModel>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        if (donors.isEmpty()) {
            Text("No donors found", style = MaterialTheme.typography.bodyMedium)
        } else {
            donors.forEach { donor ->
                DonorCard(
                    name = donor.name,
                    bloodGroup = donor.bloodGroup,
                    city = donor.city,
                    distanceKm = donor.distanceKm,
                    lastDonation = donor.lastDonationLabel,
                    phoneNumber = donor.phoneNumber
                )
            }
        }
    }
}

@Composable
private fun DonorCard(
    name: String,
    bloodGroup: String,
    city: String,
    distanceKm: Double,
    lastDonation: String,
    phoneNumber: String
) {
    val context = LocalContext.current
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        color = RaktaSoftRed,
                        shape = MaterialTheme.shapes.extraLarge,
                        modifier = Modifier.fillMaxSize()
                    ) { }
                    Text(
                        text = bloodGroup,
                        color = RaktaRed,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(name, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "$city · $distanceKm km away",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                AssistChip(onClick = { }, label = { Text(lastDonation) })
            }
            HorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, tint = RaktaRed)
                    Text(if (phoneNumber.isNotBlank()) "Tap to call donor" else "No contact info")
                }
                Button(onClick = {
                    if (phoneNumber.isNotBlank()) {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "No phone number available", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Call")
                }
            }
        }
    }
}

@Composable
private fun DonateGuideCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = RaktaRed)
                Text("Donation Flow", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
            Text("Log your donation, mark eligibility, and notify the community seamlessly.", color = Color.Gray)
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Bloodtype, contentDescription = null, tint = RaktaRed, modifier = Modifier.size(20.dp))
                Text("Step 1: Confirm donor & blood unit")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = RaktaRed, modifier = Modifier.size(20.dp))
                Text("Step 2: Save secure donation log")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Notifications, contentDescription = null, tint = RaktaRed, modifier = Modifier.size(20.dp))
                Text("Step 3: Trigger thank-you notifications")
            }
        }
    }
}

@Composable
private fun DonationHistoryCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.AutoMirrored.Filled.DirectionsWalk, contentDescription = null, tint = RaktaRed)
                Text("Recent Activity", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
            Text("Activity from your connected network", color = Color.Gray)
            HorizontalDivider()
            Text("• Donation logged for O+ patient securely", style = MaterialTheme.typography.bodyMedium)
            Text("• Radius search returned 12 matches", style = MaterialTheme.typography.bodyMedium)
            Text("• Privacy-safe call action launched", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun CallLogsCard(isActive: Boolean) {
    val context = LocalContext.current
    val donorEntries by AppDatabaseProvider.get(context)
        .donorDao()
        .observeAllDonors()
        .collectAsState(initial = emptyList())
    var hasCallLogPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CALL_LOG
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var callLogs by remember { mutableStateOf(emptyList<CallLogEntryUiModel>()) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCallLogPermission = granted
    }

    val donorLookup = remember(donorEntries) {
        donorEntries.associateBy { normalizePhoneNumber(it.phoneNumber) }
    }

    fun reloadCallLogs() {
        if (!hasCallLogPermission) {
            callLogs = emptyList()
            return
        }

        val entries = buildList {
            val projection = arrayOf(
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION
            )

            context.contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                projection,
                null,
                null,
                "${CallLog.Calls.DATE} DESC"
            )?.use { cursor ->
                val nameIndex = cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME)
                val numberIndex = cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER)
                val typeIndex = cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE)
                val dateIndex = cursor.getColumnIndexOrThrow(CallLog.Calls.DATE)
                val durationIndex = cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION)

                while (cursor.moveToNext()) {
                    val rawNumber = cursor.getString(numberIndex).orEmpty()
                    val matchedDonorName = donorLookup[normalizePhoneNumber(rawNumber)]?.fullName
                    if (matchedDonorName != null) {
                        add(
                            CallLogEntryUiModel(
                                displayName = matchedDonorName,
                                type = cursor.getInt(typeIndex),
                                dateMillis = cursor.getLong(dateIndex),
                                durationSeconds = cursor.getLong(durationIndex),
                                matchedDonorName = matchedDonorName
                            )
                        )
                    }
                }
            }
        }

        callLogs = entries
    }

    LaunchedEffect(isActive, hasCallLogPermission, donorEntries) {
        if (isActive) {
            reloadCallLogs()
        } else if (!hasCallLogPermission) {
            callLogs = emptyList()
        }
    }

    var activeFilter by remember { mutableStateOf(CallLogFilter.All) }
    val visibleLogs = remember(callLogs, activeFilter) {
        when (activeFilter) {
            CallLogFilter.All -> callLogs
            CallLogFilter.Missed -> callLogs.filter { it.type == CallLog.Calls.MISSED_TYPE }
            CallLogFilter.Incoming -> callLogs.filter { it.type == CallLog.Calls.INCOMING_TYPE }
            CallLogFilter.Outgoing -> callLogs.filter { it.type == CallLog.Calls.OUTGOING_TYPE }
        }
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Call, contentDescription = null, tint = RaktaRed)
                Column(modifier = Modifier.weight(1f)) {
                    Text("Call Logs", fontWeight = FontWeight.Bold)
                    Text(
                        "Recent calls first",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                AssistChip(onClick = { reloadCallLogs() }, label = { Text("Refresh") })
            }

            if (hasCallLogPermission) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CallFilterChip(
                        label = "All",
                        selected = activeFilter == CallLogFilter.All,
                        onClick = { activeFilter = CallLogFilter.All }
                    )
                    CallFilterChip(
                        label = "Incoming",
                        selected = activeFilter == CallLogFilter.Incoming,
                        onClick = { activeFilter = CallLogFilter.Incoming }
                    )
                    CallFilterChip(
                        label = "Outgoing",
                        selected = activeFilter == CallLogFilter.Outgoing,
                        onClick = { activeFilter = CallLogFilter.Outgoing }
                    )
                    CallFilterChip(
                        label = "Missed",
                        selected = activeFilter == CallLogFilter.Missed,
                        onClick = { activeFilter = CallLogFilter.Missed }
                    )
                }
            }

            if (!hasCallLogPermission) {
                Text("Allow call log access to show your recent incoming, outgoing, and missed calls.")
                Button(onClick = { permissionLauncher.launch(Manifest.permission.READ_CALL_LOG) }) {
                    Text("Allow access")
                }
            } else if (visibleLogs.isEmpty()) {
                Text("No call logs found on this device.")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    visibleLogs.take(10).forEach { entry ->
                        CallLogRow(entry)
                    }
                }
            }
        }
    }
}

@Composable
private fun CallFilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) }
    )
}

@Composable
private fun CallLogRow(entry: CallLogEntryUiModel) {
    val dateFormatter = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val title = entry.displayName.ifBlank { "Unknown caller" }
    val subtitle = when (entry.type) {
        CallLog.Calls.OUTGOING_TYPE -> if (entry.matchedDonorName != null) "Outgoing call to donor" else "Outgoing call"
        CallLog.Calls.INCOMING_TYPE -> "Incoming call"
        CallLog.Calls.MISSED_TYPE -> "Missed call"
        else -> "Call activity"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(42.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        color = RaktaSoftRed,
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.fillMaxSize()
                    ) { }
                    Text(
                        text = title.take(1).uppercase(Locale.getDefault()),
                        color = RaktaRed,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                AssistChip(onClick = { }, label = { Text(entry.typeLabel) })
            }
            Text(
                text = "${dateFormatter.format(Date(entry.dateMillis))} · ${entry.durationLabel}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private data class BottomTab(
    val label: String,
    val icon: ImageVector
)

private data class CallLogEntryUiModel(
    val displayName: String,
    val type: Int,
    val dateMillis: Long,
    val durationSeconds: Long,
    val matchedDonorName: String? = null
) {
    val typeLabel: String
        get() = when (type) {
            CallLog.Calls.INCOMING_TYPE -> "Incoming"
            CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
            CallLog.Calls.MISSED_TYPE -> "Missed"
            CallLog.Calls.REJECTED_TYPE -> "Rejected"
            CallLog.Calls.BLOCKED_TYPE -> "Blocked"
            CallLog.Calls.ANSWERED_EXTERNALLY_TYPE -> "External"
            else -> "Other"
        }

    val durationLabel: String
        get() = if (durationSeconds <= 0L) {
            "No duration recorded"
        } else {
            val minutes = durationSeconds / 60
            val seconds = durationSeconds % 60
            if (minutes > 0) {
                "${minutes}m ${seconds}s"
            } else {
                "${seconds}s"
            }
        }
}

private fun normalizePhoneNumber(value: String): String {
    return value.filter { it.isDigit() }
}

private enum class CallLogFilter {
    All,
    Missed,
    Incoming,
    Outgoing
}

private data class DonorCardUiModel(
    val name: String,
    val bloodGroup: String,
    val city: String,
    val distanceKm: Double,
    val lastDonationLabel: String,
    val phoneNumber: String = ""
)

private val SAMPLE_DONORS = listOf(
    DonorCardUiModel(
        name = "Suresh Kumar",
        bloodGroup = "O+",
        city = "Raipur",
        distanceKm = 4.2,
        lastDonationLabel = "Eligible now",
        phoneNumber = "9876543210"
    ),
    DonorCardUiModel(
        name = "Asha Devi",
        bloodGroup = "A-",
        city = "Bilaspur",
        distanceKm = 7.9,
        lastDonationLabel = "Eligible now",
        phoneNumber = "9876543211"
    ),
    DonorCardUiModel(
        name = "Imran Ali",
        bloodGroup = "B+",
        city = "Durg",
        distanceKm = 14.0,
        lastDonationLabel = "Donation in 21 days",
        phoneNumber = "9876543212"
    )
)

@Composable
private fun Greeting(profile: UserProfile) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
        Text(text = "Welcome, ${profile.firstName}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(text = profile.bloodGroup, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
