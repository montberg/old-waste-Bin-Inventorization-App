package com.example.mainproject

import android.content.Intent
import android.view.Menu
import android.view.MenuItem

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

open class OptionsMenu: AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.main, menu)
        return true

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.itemGoToMap -> {
                val goToMap = Intent(this, MapActivity::class.java)
                this.finish()
                startActivity(goToMap)
            }
            R.id.itemGoToList -> {
                val goToList = Intent(this, ListActivity::class.java)
                startActivity(goToList)
            }
            R.id.itemLogOut -> {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Выйти из аккаунта?")
                        .setCancelable(true)
                        .setPositiveButton("Да") { _, _ ->
                            val editor = getSharedPreferences("loginPrefs", MODE_PRIVATE).edit()
                            editor.putBoolean("isLoggedIn", false)
                            editor.apply()
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                            finishAffinity()
                            finish()
                        }
                        .setNegativeButton("Нет") { dialog, _ ->
                            dialog.dismiss()
                        }
                val alert = builder.create()
                alert.show()
            }
        }
        return true
    }
}