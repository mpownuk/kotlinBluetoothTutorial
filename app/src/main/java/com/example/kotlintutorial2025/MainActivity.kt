package com.example.kotlintutorial2025

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.bluetooth.BluetoothManager
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

class MainActivity : AppCompatActivity() {
    private var textView: TextView? = null
    private val bluetoothManager: BluetoothManager by lazy {
        getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter: BluetoothAdapter? by lazy {bluetoothManager.adapter}
    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null
    private val HC05_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkBluetoothPermissions()

        val button1: Button = findViewById(R.id.button1)
        val button2: Button = findViewById(R.id.button2)
        val button3: Button = findViewById(R.id.button3)
        val button4: Button = findViewById(R.id.button4)
        val reconnectButton: Button = findViewById(R.id.reconnectButton)
        textView = findViewById(R.id.textView)

        connectToHC05()

        button1.setOnClickListener{sendData("4")}
        button2.setOnClickListener{sendData("5")}
        button3.setOnClickListener{sendData("6")}
        button4.setOnClickListener{sendData("7")}

        reconnectButton.setOnClickListener{
            textView?.text = ""
            textView?.append("Reconnection...")
            Thread.sleep(100)
            reconnectHC05()
        }
    }

    private fun connectToHC05() {
        val HC05Address = "58:56:00:01:21:AE"
        val device: BluetoothDevice? = bluetoothAdapter?.getRemoteDevice(HC05Address)

        if (device == null) {
            textView?.append("\nNie znaleziono urządzenia HC-05\n")
            return
        } else {
            textView?.append(device.address)
        }

        var attempt = 0
        val maxAttempts = 150

        while (attempt < maxAttempts) {
            try {
                bluetoothSocket = device?.createRfcommSocketToServiceRecord(HC05_UUID)
                bluetoothSocket?.connect()
                outputStream = bluetoothSocket?.outputStream
                textView?.append("\nDevice Connected\n")
                return
            } catch (e: IOException) {
                textView?.text = ""
                textView?.append("\nConnection attempt no: $attempt failed!\n ${e.message}\n")
                e.printStackTrace()
                try {
                    Thread.sleep(100)
                } catch (e:InterruptedException) {
                    e.printStackTrace()
                }
            }
            attempt++
        }
    }

    private fun sendData(data: String) {
        try {
            outputStream?.write(data.toByteArray())
            outputStream?.flush()
            textView?.text = ""
            textView?.append("Sended: $data")
            Thread.sleep(200)
        } catch ( e:IOException){
            textView?.append("\nError! ${e.message}\n")
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            outputStream?.close()
            bluetoothSocket?.close()
        } catch ( e:IOException){
            e.printStackTrace()
        }
    }

    private  fun checkBluetoothPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 1)
            }
        }
    }

    private fun reconnectHC05 (){
        sendData("-1")
        bluetoothSocket?.close()
        outputStream?.close()
        connectToHC05()
    }
}