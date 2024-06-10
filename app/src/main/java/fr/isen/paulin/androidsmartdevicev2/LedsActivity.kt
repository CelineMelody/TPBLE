package fr.isen.paulin.androidsmartdevicev2

import android.bluetooth.BluetoothManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.isen.paulin.androidsmartdevicev2.ui.theme.AndroidSmartDevicev2Theme

class LedsActivity : ComponentActivity() {
    // Assume BluetoothManager is initialized and connected elsewhere
    private lateinit var bluetoothManager: BluetoothManager
    private var buttonPressCount by mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidSmartDevicev2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    Column {
                        LedsActivityHeader()
                        LedScreen(bluetoothManager)
                    }
                }
            }
        }

        // Setup Bluetooth and button press counting
        //setupBluetoothAndButtonPressCounting()
    }

    /*private fun setupBluetoothAndButtonPressCounting() {
        // Setup notification listening for button press count from a specific characteristic
        val buttonPressCharacteristicUUID = UUID.fromString("YOUR_BUTTON_PRESS_CHARACTERISTIC_UUID")
        bluetoothManager.setupNotificationForButtonPress(
            bluetoothManager.getCharacteristic(buttonPressCharacteristicUUID)
        ) { count ->
            buttonPressCount = count
        }
    }*/
}

@Composable
fun LedScreen(bluetoothManager: BluetoothManager) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "TPBLE",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color(0xFF007BFF)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Affichage des différentes LED",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            LedToggle("LED 1", bluetoothManager, 1)
            LedToggle("LED 2", bluetoothManager, 2)
            LedToggle("LED 3", bluetoothManager, 3)
        }
        Spacer(modifier = Modifier.height(20.dp))
        /*Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = buttonPressCount > 0,
                onCheckedChange = null // Checkbox is just for display
            )
            Text("Number of button presses: $buttonPressCount", color = Color.Gray)
        }*/
    }
}

@Composable
fun LedsActivityHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(0xFF007BFF)),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "AndroidSmartDevice",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun LedToggle(label: String, bluetoothManager: BluetoothManager, ledIndex: Int) {
    var isOn by remember { mutableStateOf(false) }
    val imageResource = if (isOn) R.drawable.led_on else R.drawable.led_off

    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = imageResource),
            contentDescription = label,
            modifier = Modifier
                .size(48.dp)
                .clickable {
                    isOn = !isOn
                    //bluetoothManager.sendLEDCommand(ledIndex, isOn)
                }
        )
        Text(label, color = Color.Gray)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    // Provide a dummy BluetoothManager for the preview
    //val dummyBluetoothManager = BluetoothManager()
    AndroidSmartDevicev2Theme {
        //LedScreen(dummyBluetoothManager)
    }
}
/*package fr.isen.paulin.androidsmartdevicev2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.isen.paulin.androidsmartdevicev2.ui.theme.AndroidSmartDevicev2Theme

class LedsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidSmartDevicev2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White // Set the background to white
                ) {
                    Column {
                        LedsActivityHeader() // Include the header
                        LedScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun LedScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "TPBLE",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color(0xFF007BFF) // Apply gray text color
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Affichage des différentes LED",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray // Apply gray text color
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            LedToggle("LED 1")
            LedToggle("LED 2")
            LedToggle("LED 3")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = false,
                onCheckedChange = {}
            )
            Text("Abonnez vous pour recevoir le nombre d'incrémentation", color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text("Nombre: 6", color = Color.Gray) // Dynamically update based on state
    }
}

@Composable
fun LedsActivityHeader() {
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
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun LedToggle(label: String) {
    var isOn by remember { mutableStateOf(false) }
    val imageResource = if (isOn) R.drawable.led_on else R.drawable.led_off

    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = imageResource),
            contentDescription = label,
            modifier = Modifier
                .size(48.dp)
                .clickable { isOn = !isOn }
        )
        Text(label, color = Color.Gray)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AndroidSmartDevicev2Theme {
        LedScreen()
    }
}*/
