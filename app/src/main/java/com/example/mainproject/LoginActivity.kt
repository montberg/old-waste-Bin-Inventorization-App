package com.example.mainproject

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import java.util.*

class LoginActivity : AppCompatActivity(), DataBase {
    private lateinit var txtPassword:EditText
    private lateinit var txtLogin: EditText
    private lateinit var btnLogin: Button
    private var PREFERENCES_NAME = "loginPrefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        txtPassword = findViewById(R.id.txtPassword)
        txtLogin = findViewById(R.id.txtLogin)
        btnLogin = findViewById(R.id.btnLogin)

        val prefs = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("isLoggedIn", false)

        if(isLoggedIn){
            try{
            val loginSuccess = Intent(this, MapActivity::class.java)
            startActivity(loginSuccess)
            finish()
        }catch (e:Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
        }

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        btnLogin.setOnClickListener{
            if(isOnline(this)){
            if(txtLogin.length() != 0 && txtPassword.length() != 0) {
                try {
                    userLogin = txtLogin.text.toString()
                    userPassword = txtPassword.text.toString()
                    dataBaseConnection(userLogin, userPassword)
                } catch (e: Exception) {
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this, "Введите все значения.", Toast.LENGTH_SHORT).show()
            }
        } else {
                Toast.makeText(this, "Отсутствует подключение к интернету!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun dataBaseConnection(login: String, password: String){
        val editor = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).edit()
        if(checkUser(login, password)) {
                editor.putBoolean("isLoggedIn", true)
                editor.putString("login", login)
                editor.apply()
                val loginSuccess = Intent(this, MapActivity::class.java)
                startActivity(loginSuccess)
                finish()
        }
            else{
                editor.putBoolean("isLoggedIn", false)
                editor.putString("login", null)
                Toast.makeText(this, "Логин или пароль введены неверно.", Toast.LENGTH_SHORT).show()
            }
    }
    private fun isOnline(context: Context): Boolean { //функция проверки наличия подключения к инету
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }
}
