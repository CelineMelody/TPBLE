package fr.isen.paulin.androidsmartdevicev2

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class DeviceActivity : ComponentActivity() {
    private var bluetoothService: BluetoothLeService? = null
    private var connected = false
    private lateinit var gattUpdateReceiver: GattUpdateReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gattUpdateReceiver = GattUpdateReceiver { action ->
            handleGattUpdate(action)
        }

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    DeviceActivityContent()
                }
            }
        }

        val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
        bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(gattUpdateReceiver, createGattUpdateIntentFilter())
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(gattUpdateReceiver)
    }

    private fun handleGattUpdate(action: String) {
        connected = when (action) {
            BluetoothLeService.ACTION_GATT_CONNECTED -> true
            BluetoothLeService.ACTION_GATT_DISCONNECTED -> false
            else -> connected
        }
    }

    private fun createGattUpdateIntentFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
            addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            bluetoothService = (service as BluetoothLeService.LocalBinder).getService()
            if (!bluetoothService?.initialize()!!) {
                Log.e("DeviceActivity", "Unable to initialize Bluetooth")
                finish()
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            bluetoothService = null
        }
    }

    @Composable
    fun DeviceActivityContent() {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Bluetooth Device Connection Screen")
                Text("Status: ${if (connected) "Connected" else "Disconnected"}")
            }
        }
    }
}

/*package fr.isen.paulin.androidsmartdevicev2

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

class DeviceActivity : ComponentActivity() {
    private var bluetoothService: BluetoothLeService? = null
    private var connected = false
    private val TAG = "DeviceActivity"


    // Observable state for connection status message
    private var connectionStatus by mutableStateOf("Disconnected")

    private val gattUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "Broadcast received with action: ${intent.action}") // Log the action of the received intent
            when (intent.action) {
                BluetoothLeService.ACTION_GATT_CONNECTED -> {
                    connected = true
                    connectionStatus = "Connected"
                    Log.d(TAG, "Bluetooth is connected.") // Log Bluetooth connected
                    // update UI or handle connection established
                }
                BluetoothLeService.ACTION_GATT_DISCONNECTED -> {
                    connected = false
                    connectionStatus = "Disconnected"
                    Log.d(TAG, "Bluetooth is disconnected.") // Log Bluetooth disconnected
                    // update UI or handle disconnection
                }
            }
        }
    }

    private fun makeGattUpdateIntentFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
            addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Activity created.") // Log when activity is created
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    DeviceActivityHeader()
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Bluetooth Device Connection Screen")
                        Text(text = "Status: $connectionStatus")
                    }
                }
            }
        }
        val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
        bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Activity resumed.") // Log when activity is resumed
        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter())
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: Activity paused.") // Log when activity is paused
        unregisterReceiver(gattUpdateReceiver)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            bluetoothService = (service as BluetoothLeService.LocalBinder).getService()
            Log.d(TAG, "onServiceConnected: Bluetooth service connected.") // Log when service is connected
            if (!bluetoothService?.initialize()!!) {
                Log.e(TAG, "Unable to initialize Bluetooth")
                finish()
            }
            // Assuming "device_address" is passed as an extra
            val deviceAddress = intent.getStringExtra("device_address") ?: "No Address"
            bluetoothService?.connect(deviceAddress)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            bluetoothService = null
        }
    }

    @Composable
    fun DeviceActivityHeader() {
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
}*/
