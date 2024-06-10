package fr.isen.paulin.androidsmartdevicev2

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

class BluetoothLeService : Service() {
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private val binder = LocalBinder()
    private val TAG = "BluetoothLeService"

    fun initialize(): Boolean {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.")
            return false
        }
        return true
    }

    @SuppressLint("MissingPermission")
    fun connect(address: String): Boolean {
        val device = bluetoothAdapter?.getRemoteDevice(address)
        device?.let {
            bluetoothGatt = it.connectGatt(this, false, bluetoothGattCallback)
            return true
        }
        return false
    }

    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                broadcastUpdate("com.example.bluetooth.le.ACTION_GATT_CONNECTED")
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                broadcastUpdate("com.example.bluetooth.le.ACTION_GATT_DISCONNECTED")
            }
        }

    }

    private fun broadcastUpdate(action: String) {
        sendBroadcast(Intent(action))
    }

    inner class LocalBinder : Binder() {
        fun getService(): BluetoothLeService = this@BluetoothLeService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        close()
        return super.onUnbind(intent)
    }

    @SuppressLint("MissingPermission")
    private fun close() {
        bluetoothGatt?.close()
        bluetoothGatt = null
    }

    companion object {
        const val ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED"
    }

}
