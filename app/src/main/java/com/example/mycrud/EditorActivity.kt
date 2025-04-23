package com.example.mycrud

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.mycrud.data.Status
import com.example.mycrud.data.AppDatabase
import com.example.mycrud.data.entity.User

class EditorActivity : AppCompatActivity() {
    private lateinit var fullName: EditText
    private lateinit var email: EditText
    private lateinit var phone: EditText
    private lateinit var address: EditText
    private lateinit var statusSpinner: Spinner
    private lateinit var btnSave: Button
    private lateinit var database: AppDatabase

    private var userId: Int? = null
    private var currentStatus: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editor)

        // Inisialisasi View
        fullName = findViewById(R.id.full_name)
        email = findViewById(R.id.email)
        phone = findViewById(R.id.phone)
        address = findViewById(R.id.address)
        statusSpinner = findViewById(R.id.spinner_status)
        btnSave = findViewById(R.id.btn_save)

        // Inisialisasi Database
        database = AppDatabase.getInstance(applicationContext)

        // Setup dropdown enum status
        val statusOptions = Status.values().map { it.displayName }
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        statusSpinner.adapter = spinnerAdapter

        // Ambil data jika mode edit
        val intent = intent.extras
        if (intent != null) {
            userId = intent.getInt("id", 0)
            if (userId != 0) {
                val user = database.userDao().get(userId!!)
                fullName.setText(user.fullname)
                email.setText(user.email)
                phone.setText(user.phone)
                address.setText(user.address)
                currentStatus = user.status

                // Set pilihan status ke spinner
                val selectedIndex = statusOptions.indexOf(currentStatus)
                if (selectedIndex >= 0) {
                    statusSpinner.setSelection(selectedIndex)
                }
            }
        }

        // Tombol Simpan
        btnSave.setOnClickListener {
            if (fullName.text.isNotEmpty() && email.text.isNotEmpty() && phone.text.isNotEmpty()) {
                val selectedStatus = statusOptions[statusSpinner.selectedItemPosition]

                if (userId != null && userId != 0) {
                    // UPDATE
                    val updatedUser = User(
                        id = userId,
                        fullname = fullName.text.toString(),
                        email = email.text.toString(),
                        phone = phone.text.toString(),
                        address = address.text.toString(),
                        status = selectedStatus
                    )
                    database.userDao().update(updatedUser)
                    Toast.makeText(this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
                } else {
                    // INSERT
                    val newUser = User(
                        id = null,
                        fullname = fullName.text.toString(),
                        email = email.text.toString(),
                        phone = phone.text.toString(),
                        address = address.text.toString(),
                        status = selectedStatus
                    )
                    database.userDao().insertAll(newUser)
                    Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                }

                finish()
            } else {
                Toast.makeText(this, "Silahkan isi semua data dengan benar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
