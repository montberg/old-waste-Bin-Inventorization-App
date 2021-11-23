package com.example.mainproject

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class TestingStuff : AppCompatActivity(), DataBase {
    private var PREFERENCES_NAME = "testPrefs"
    lateinit var prefs:SharedPreferences
    lateinit var spinner:AutoCompleteTextView
    lateinit var defaultArray:Array<String?>
    lateinit var pic:ImageView
    lateinit var relativeLayout:RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing_stuff)
        prefs = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
        defaultArray = arrayOf("Основание 1", "Основание 2", "Основание 3")
        spinner = findViewById(R.id.spinner)
        relativeLayout = findViewById(R.id.relativeLayout)
        val updateList: Button = findViewById(R.id.updateList)
        val ltInflater = layoutInflater
        getCachedArray()
        var i = 0;
        updateList.setOnClickListener{
            i++
           // try{
           //     val arrayList: Array<String?> = getListValues("select type from basetype;")
           //     val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayList)
           //     spinner.setAdapter(adapter)
           //     saveArray(arrayList, "cached")
           // }
           // catch (e: Exception){
           //     Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
           // }
           //val item: View = ltInflater.inflate(R.layout.container, relativeLayout, false)
           //val containerRubbishType:TextView = item.findViewById(R.id.containerRubbishType)
           //containerRubbishType.text = "Органические отходы"
           //val containerVolume:TextView = item.findViewById(R.id.containerVolume)
           //containerVolume.text = "12 кв м"
           //val containerNumber:TextView = item.findViewById(R.id.containerNumber)
           //containerNumber.text = i.toString()
           //item.layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT
           //relativeLayout.addView(item)
        }
    }
    //override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
    //    super.onActivityResult(requestCode, resultCode, imageReturnedIntent)
    //    var bitmap: Bitmap? = null
    //    when (requestCode) {
    //        1 -> if (resultCode == RESULT_OK) {
    //            val selectedImage: Uri? = imageReturnedIntent?.data
    //            try {
    //                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
    //            } catch (e: IOException) {
    //                e.printStackTrace()
    //            }
    //            val picture = ImageView(this)
    //            picture.scaleX = 120F
    //            picture.scaleY = 120F
    //            picture.setImageBitmap(bitmap)
    //            relativeLayout.addView(picture)
    //        }
    //    }
    //}
    private fun saveArray(list: Array<String?>?, key: String?) {
        val prefs = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
        val editor = prefs.edit()
        val gson = Gson()
        val json: String = gson.toJson(list)
        editor.putString(key, json)
        editor.apply()
    }
    private fun getArray(key: String?): Array<String> {
        val prefs = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString(key, null)
        val type: Type = object : TypeToken<Array<String>>(){}.type
        return gson.fromJson(json, type)
    }
    private fun getCachedArray(){
        if(!prefs.contains("cached")){
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, defaultArray)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.setAdapter(adapter)
        }
        else
        {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, getArray("cached"))
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.setAdapter(adapter)
        }
        spinner.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                spinner.showDropDown()
            }
        }
    }

}