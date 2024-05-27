package com.example.attendease

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.nfc.tech.Ndef
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.IOException


class Professor : AppCompatActivity() {
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var infoTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_professor)
        setupEdgeToEdge()
        setupNfcAdapter()

        infoTextView = findViewById(R.id.infoTextView)
        val startReceivingButton: Button = findViewById(R.id.startReceivingButton)

        startReceivingButton.setOnClickListener {
            Toast.makeText(this, "Ready to receive NFC data.", Toast.LENGTH_LONG).show()
            // Add more logic if needed for starting NFC data reception
        }
    }

    private fun setupEdgeToEdge() {
        // Assuming that the ConstraintLayout is the root of the layout and you want to set the insets on it.
        val rootView = findViewById<ConstraintLayout>(R.id.constraintLayoutRoot) // You'll need to add an ID to the ConstraintLayout in your XML.
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    private fun setupNfcAdapter() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)?.also {
            Toast.makeText(this, "NFC is available on this device.", Toast.LENGTH_LONG).show()
        } ?: run {
            Toast.makeText(this, "NFC is not available on this device.", Toast.LENGTH_LONG).show()
            finish()
            return
        }
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntentFlags =
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, pendingIntentFlags
        )
        val intentFilter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED).apply {
            addDataType("text/plain")
        }
        val filters = arrayOf(intentFilter)
        val techLists = arrayOf(arrayOf(Ndef::class.java.name))
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, techLists)
    }


    override fun onPause() {
        super.onPause()
        nfcAdapter.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent?.action) {
            val rawMessages = intent.getParcelableArrayListExtra<NdefMessage>(NfcAdapter.EXTRA_NDEF_MESSAGES)
            rawMessages?.let { messages ->
                handleNdefMessages(messages)
            }
        }
    }



    private fun handleNdefMessages(ndefMessages: List<NdefMessage>) {
        if (ndefMessages.isNotEmpty()) {
            val payload = ndefMessages[0].records[0].payload
            val studentInfo = String(payload, Charsets.UTF_8)
            updateStudentInfo(studentInfo)
            promptForFileName(studentInfo)
        }
    }

    private fun updateStudentInfo(info: String) {
        infoTextView.text = info
    }

    private fun promptForFileName(data: String) {
        val editText = EditText(this).apply {
            hint = "Enter file name"
        }
        AlertDialog.Builder(this)
            .setTitle("New File Name")
            .setView(editText)
            .setPositiveButton("Save") { dialog, _ ->
                val fileName = editText.text.toString()
                if (fileName.isNotEmpty()) {
                    writeToFile(data, fileName)
                } else {
                    Toast.makeText(this, "Invalid file name. Data not saved.", Toast.LENGTH_LONG).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun writeToFile(data: String, fileName: String) {
        val file = File(getExternalFilesDir(null), "$fileName.txt")
        try {
            file.appendText(data + System.lineSeparator())
            Toast.makeText(this, "Data saved to file: $fileName.txt", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(this, "Failed to save data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
