package com.example.devapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.devapp.database.api.ApiClient
import com.example.devapp.database.models.Customer
import com.example.devapp.databinding.ActivityProfileBinding
import com.example.devapp.views.ProfileViewModel

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получение ID пользователя
        val userId = intent.getIntExtra("USER_ID", -1)
        Log.d("ProfileActivity", "Получен USER_ID: $userId")

        if (userId != -1) {
            profileViewModel.fetchCustomer(userId)
        } else {
            profileViewModel.error.value = "Некорректный ID пользователя"
        }

        // Обработка LiveData с клиентом
        profileViewModel.customer.observe(this) { customer ->
            if (customer != null) {
                // Устанавливаем данные клиента в поля
                binding.etFullName.setText(customer.fullname)
                binding.etPhoneNumber.setText(customer.phoneNum)
                binding.etEmail.setText(customer.mail)

                // Включаем редактирование
                binding.etFullName.isEnabled = false
                binding.etPhoneNumber.isEnabled = false
                binding.etEmail.isEnabled = false

                // Включаем кнопку редактирования
                binding.btnEdit.isEnabled = true
            }
        }

        profileViewModel.error.observe(this) { errorMessage ->
            if (errorMessage != null) {
                showErrorDialog(errorMessage)
            }
        }

        // Обработка кнопки редактирования
        binding.btnEdit.setOnClickListener {
            enableEditing(true)
        }

        // Обработка кнопки сохранения
        binding.btnSave.setOnClickListener {
            val updatedCustomer = Customer(
                idclient = userId,
                fullname = binding.etFullName.text.toString(),
                phoneNum = binding.etPhoneNumber.text.toString(),
                mail = binding.etEmail.text.toString()
            )

            profileViewModel.saveOrUpdateCustomer(userId, updatedCustomer)
            enableEditing(false)
        }

        // Кнопка отмены редактирования
        binding.btnCancel.setOnClickListener {
            enableEditing(false)
        }
    }

    private fun enableEditing(enable: Boolean) {
        // Включаем/выключаем поля для редактирования
        binding.etFullName.isEnabled = enable
        binding.etPhoneNumber.isEnabled = enable
        binding.etEmail.isEnabled = enable

        // Включаем/выключаем кнопку редактирования и сохранения
        binding.btnEdit.isEnabled = !enable
        binding.btnSave.isEnabled = enable
        binding.btnCancel.isEnabled = enable
    }

    private fun showErrorDialog(message: String) {
        // Отображение ошибки через диалоговое окно
        AlertDialog.Builder(this)
            .setTitle("Ошибка")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}