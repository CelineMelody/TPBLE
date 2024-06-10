package fr.isen.paulin.androidsmartdevicev2

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.isen.paulin.androidsmartdevicev2.ui.theme.AndroidSmartDevicev2Theme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidSmartDevicev2Theme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = Color.White /*MaterialTheme.colorScheme.background*/) {
                    Column {
                        Header()
                        MainContent()
                    }
                }
            }
        }
    }
}
@Composable
fun Header() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp) // Typical height for an app bar
            .background(Color(0xFF007BFF)), // A blue color for the background
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "AndroidSmartDevice",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
@Composable
fun MainContent() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to AndroidSmartDevice",
            color = Color(0xFF007BFF),
            style = MaterialTheme.typography.headlineMedium
            //modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Tap the button below to start scanning for BLE devices.",
            color = Color.LightGray,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )
        Spacer(modifier = Modifier.height(100.dp))
        Image(
            painter = painterResource(id = R.drawable.logobluetooth),
            contentDescription = "Bluetooth Logo",
            modifier = Modifier
                .height(200.dp) // Set the height, adjust as needed
                .fillMaxWidth() // Ensure it fills the width of the column
        )
        Spacer(modifier = Modifier.height(160.dp))
        Button(
            onClick = { context.startActivity(Intent(context, ScanActivity::class.java)) },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF007BFF), // Blue background
                contentColor = Color.White // White text
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp) // Set the height of the button
        ) {
            Text("Begin to Scan")
        }

    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    AndroidSmartDevicev2Theme {
        Column {
            Header()
            MainContent()
        }
    }
}