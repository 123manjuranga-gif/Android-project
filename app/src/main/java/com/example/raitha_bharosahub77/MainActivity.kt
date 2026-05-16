package com.example.raitha_bharosahub77

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF2E7D32), // Rural Green
                    secondary = Color(0xFFC8E6C9),
                    error = Color(0xFFD32F2F) // Alert Red
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

// Navigation Routes
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object InputCenter : Screen("input", "Input Center", Icons.Default.EditNote)
    object KrishiCalendar : Screen("calendar", "Krishi Calendar", Icons.Default.CalendarMonth)
    object History : Screen("history", "History", Icons.Default.History)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var farmerName by remember { mutableStateOf("AMMU") }
    var selectedCrop by remember { mutableStateOf("Sugarcane") }
    var sowingIndex by remember { mutableIntStateOf(85) } // Percentage
    var nitrogen by remember { mutableStateOf("45") }
    var phosphorus by remember { mutableStateOf("30") }
    var potassium by remember { mutableStateOf("20") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Raitha-Bharosa Hub", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.secondary) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val items = listOf(Screen.Dashboard, Screen.InputCenter, Screen.KrishiCalendar, Screen.History)

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title, modifier = Modifier.size(28.dp)) },
                        label = { Text(screen.title, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(farmerName, selectedCrop, sowingIndex)
            }
            composable(Screen.InputCenter.route) {
                InputCenterScreen(
                    nitrogen = nitrogen,
                    phosphorus = phosphorus,
                    potassium = potassium,
                    onNChange = { nitrogen = it },
                    onPChange = { phosphorus = it },
                    onKChange = { potassium = it },
                    onSave = {
                        // Simulate updating algorithm based on inputs
                        sowingIndex = (60..95).random()
                    }
                )
            }
            composable(Screen.KrishiCalendar.route) {
                KrishiCalendarScreen()
            }
            composable(Screen.History.route) {
                HistoryScreen()
            }
        }
    }
}

// --- SCREEN 1: DASHBOARD ---
@Composable
fun DashboardScreen(farmerName: String, crop: String, sowingIndex: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome, $farmerName SHREE!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )
        Text(
            text = "Active Crop: $crop",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.Start).padding(bottom = 24.dp)
        )

        // Sowing Index Circular/Card Display
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Current Sowing Index", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "$sowingIndex%",
                    fontSize = 64.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (sowingIndex >= 80) Color(0xFF2E7D32) else Color(0xFFE65100)
                )
                Text(
                    text = if (sowingIndex >= 80) "OPTIMAL SOWING WINDOW OPEN" else "CONDITIONS SUB-OPTIMAL",
                    fontWeight = FontWeight.Bold,
                    color = if (sowingIndex >= 80) Color(0xFF2E7D32) else Color(0xFFE65100)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Today's Action Indicator (Large High-Contrast Button Design)
        val isOpen = sowingIndex >= 80
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth().height(90.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isOpen) Color(0xFF2E7D32) else Color(0xFFD32F2F)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isOpen) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = if (isOpen) "ACTION TODAY: SOW NOW" else "ACTION TODAY: DELAY SOWING",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (isOpen) "Soil moisture & temperature are perfect." else "Heavy rain predicted in 48 hours.",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

// --- SCREEN 2: INPUT CENTER ---
@Composable
fun InputCenterScreen(
    nitrogen: String, phosphorus: String, potassium: String,
    onNChange: (String) -> Unit, onPChange: (String) -> Unit, onKChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Log Soil Test Metrics (N-P-K)", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = nitrogen,
            onValueChange = onNChange,
            label = { Text("Nitrogen Level (N)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )
        OutlinedTextField(
            value = phosphorus,
            onValueChange = onPChange,
            label = { Text("Phosphorus Level (P)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )
        OutlinedTextField(
            value = potassium,
            onValueChange = onKChange,
            label = { Text("Potassium Level (K)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth().height(55.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Update Krishi Metrics", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// --- SCREEN 3: KRISHI CALENDAR (7-Day Action Plan) ---
data class CalendarDay(val day: String, val status: String, val alert: Boolean, val instruction: String)

@Composable
fun KrishiCalendarScreen() {
    val sampleWeek = listOf(
        CalendarDay("Today", "Heavy Rain Warning", true, "Delay fertilization activity 24 hours prior to storm."),
        CalendarDay("Tomorrow", "Overcast", false, "Ideal soil moisture setup expected."),
        CalendarDay("Day 3", "Clear Sky", false, "Excellent window for weeding operations."),
        CalendarDay("Day 4", "Light Showers", false, "Natural irrigation processing."),
        CalendarDay("Day 5", "Sunny", false, "Standard monitoring window."),
        CalendarDay("Day 6", "Sunny", false, "Standard monitoring window."),
        CalendarDay("Day 7", "Moderate Winds", true, "Secure delicate crop structures.")
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("7-Day Krishi Calendar", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(sampleWeek) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (item.alert) Color(0xFFFFEBEE) else Color(0xFFF1F8E9)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.day, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                            Text(item.status, fontWeight = FontWeight.Medium, color = if (item.alert) Color.Red else Color(0xFF388E3C))
                            Text(item.instruction, fontSize = 12.sp, color = Color.DarkGray)
                        }
                        if (item.alert) {
                            Icon(Icons.Default.Warning, contentDescription = "Alert", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

// --- SCREEN 4: LOCAL HISTORY LOG ---
data class PastSeason(val year: String, val crop: String, val yield: String)

@Composable
fun HistoryScreen() {
    val historyData = listOf(
        PastSeason("Meghna 2025", "Meghna", "85 Tons/Acre"),
        PastSeason("kalpana 2024", "kalpana", "18 Quintals/Acre"),
        PastSeason("Divya 2024", "Divya", "28 Quintals/Acre")
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Seasonal Yield History", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(historyData) { record ->
                ListItem(
                    headlineContent = { Text(record.crop, fontWeight = FontWeight.Bold) },
                    supportingContent = { Text("Season: ${record.year}") },
                    trailingContent = { Text(record.yield, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                    colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        }
    }
}