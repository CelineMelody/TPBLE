package fr.isen.paulin.androidsmartdevicev2

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.widget.Toast
import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

class ScanActivity : ComponentActivity() {
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var scanning = false
    private val handler = Handler(Looper.getMainLooper())
    private val SCAN_PERIOD: Long = 10000
    private val devicesList = mutableStateListOf<ScanResult>()
    private val PERMISSIONS_REQUEST_CODE = 100

    private val leScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val newDevice = result.device
            if (devicesList.none { it.device.address == newDevice.address }) {
                devicesList.add(result)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ScanActivityLayout()
                }
            }
        }
    }

    @Composable
    fun ScanActivityLayout() {
        Column(/*modifier = Modifier.padding(16.dp)*/) {
            ScanActivityHeader()
            Spacer(modifier = Modifier.height(16.dp))
            if (checkPermissions()) {
                if (bluetoothAdapter.isEnabled) {
                    ScanControlButton()
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(Color(0xFF007BFF))
                    )
                    BluetoothDevicesList(devices = devicesList){ device ->
                        navigateToDeviceActivity(device)
                    }
                } else {
                    RequestBluetoothActivationDialog()
                }
            } else {
                RequestPermissions()
                Text("Bluetooth or Location Permissions not granted", color = Color.Red)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun navigateToDeviceActivity(device: BluetoothDevice) {
        val intent = Intent(this, DeviceActivity::class.java)
        intent.putExtra("device_name", device.name ?: "Unknown Device")
        intent.putExtra("device_address", device.address)
        startActivity(intent)
    }


    @Composable
    fun ScanActivityHeader() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp) // Typical height for an app bar
                .background(Color(0xFF007BFF)), // A blue color for the background
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "AndroidSmartDevice",
                //color = Color.Black,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                //textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }

    @Composable
    fun ScanControlButton() {
        val scanText = if (scanning) "BLE Scan loading" else "Launch BLE Scan"
        val imageResource = if (scanning) R.drawable.pausebutton else R.drawable.playbutton

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White, shape = RoundedCornerShape(4.dp)) // Définir un arrière-plan blanc avec des coins arrondis
                .clickable(onClick = { toggleScanning() }),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(12.dp)
            ) {
                Image(
                    painter = painterResource(id = imageResource),
                    contentDescription = if (scanning) "Pause Scan" else "Start Scan",
                    modifier = Modifier.height(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(scanText, color = Color.Black)
            }
        }
    }



    @Composable
    fun BluetoothDevicesList(devices: List<ScanResult>, onClick: (BluetoothDevice) -> Unit) {
        LazyColumn {
            items(devices) { device ->
                DeviceRow(device, onClick)
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Composable
    fun DeviceRow(device: ScanResult, onClick: (BluetoothDevice) -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(device.device) }  // Add clickable with a lambda passing the device
                .padding(vertical = 8.dp),
            //.background(MaterialTheme.colorScheme.surfaceVariant),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp) // Taille du cercle
                    .background(color = Color(0xFF007BFF), shape = CircleShape) // Cercle bleu
                    .padding(8.dp) // Padding interne au cercle
            ) {
                Text(
                    text = "${device.rssi} dBm",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 12.sp,fontWeight = FontWeight.Bold), // Texte en gras
                    color = Color.White // Texte en blanc
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = device.device.name ?: "Unknown Device",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Address: ${device.device.address}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFF007BFF))
        )
    }


    private fun checkPermissions(): Boolean {
        val neededPermissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        return neededPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun RequestPermissions() {
        val neededPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        ActivityCompat.requestPermissions(this, neededPermissions, PERMISSIONS_REQUEST_CODE)
    }

    @Composable
    fun RequestBluetoothActivationDialog() {
        val startForResult = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (!checkPermissions()) {
                    RequestPermissions()
                }
            }
        }

        if (!bluetoothAdapter.isEnabled) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Activation Bluetooth requise") },
                text = { Text("Cette application nécessite que le Bluetooth soit activé. Veuillez activer le Bluetooth pour continuer.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                            startForResult.launch(enableBtIntent)
                        }
                    ) {
                        Text("Activer")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { finish() }
                    ) {
                        Text("Annuler")
                    }
                }
            )
        }
    }

    private fun toggleScanning() {
        if (!scanning && checkPermissions()) {
            try {
                scanning = true
                bluetoothAdapter.bluetoothLeScanner.startScan(leScanCallback)
                handler.postDelayed({
                    scanning = false
                    bluetoothAdapter.bluetoothLeScanner.stopScan(leScanCallback)
                }, SCAN_PERIOD)
            } catch (e: SecurityException) {
                scanning = false
                Toast.makeText(this, "Required permissions are not granted", Toast.LENGTH_SHORT).show()
            }
        } else {
            scanning = false
            try {
                bluetoothAdapter.bluetoothLeScanner.stopScan(leScanCallback)
            } catch (e: SecurityException) {
                Toast.makeText(this, "Failed to stop scan due to permission issue", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
