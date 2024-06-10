package fr.isen.paulin.androidsmartdevicev2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class GattUpdateReceiver(private val updateHandler: (String) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.action?.let { action ->
            Log.d("GattUpdateReceiver", "Received action: $action")
            updateHandler(action)
        }
    }
}
