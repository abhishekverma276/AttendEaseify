package com.example.attendease

import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import java.io.IOException
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.attendease.databinding.ActivityStudentBinding

class Student : AppCompatActivity() {

    private lateinit var binding: ActivityStudentBinding
    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNFC()
        setupFormSubmission()
    }

    private fun setupNFC() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available on this device.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        Toast.makeText(this, "NFC is available.", Toast.LENGTH_LONG).show()
        nfcAdapter?.enableReaderMode(this, { tag: Tag? ->
            tag?.let { handleTag(it) }
        }, NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null)
    }

    private fun handleTag(tag: Tag) {
        // Assuming NDEF message reading
        NfcReadUtility.readFromTag(tag)?.let { ndefMessage ->
            runOnUiThread {
                Toast.makeText(this, "NFC Tag detected with content: ${ndefMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(this)
    }

    private fun setupFormSubmission() {
        binding.sendButton.setOnClickListener {
            val name = binding.nameInput.text.toString()
            val enroll = binding.nameInput.text.toString()
            val batch = binding.batchInput.text.toString()
            val branch = binding.branchInput.text.toString()

            if (name.isEmpty() || enroll.isEmpty() || batch.isEmpty() || branch.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // data transmission via NFC could be implemented here
            sendData(name, enroll, batch, branch)
        }
    }

    private fun sendData(name: String, enroll: String, batch: String, branch: String) {
        val studentInfo = "Name: $name\nEnroll: $enroll\nBatch: $batch\nBranch: $branch"
        // data sending logic here
        Toast.makeText(this, "Data prepared to send: $studentInfo", Toast.LENGTH_LONG).show()

    }
}

object NfcReadUtility {
    fun readFromTag(tag: Tag): String? {
        val ndef = Ndef.get(tag) ?: return null // Get NDEF tech from the NFC Tag
        try {
            ndef.connect()
            val ndefMessage = ndef.ndefMessage // Attempt to read the NDEF message
            val record = ndefMessage.records.firstOrNull() // Get the first record to simplify
            return record?.let { String(it.payload, Charsets.UTF_8) } // Convert payload to string
        } catch (e: IOException) {
            e.printStackTrace() // Handle exceptions, possibly return error message or log
        } finally {
            try {
                if (ndef.isConnected) {
                    ndef.close() // Ensure the connection is closed after operations
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null // Return null if unsuccessful
    }
}
