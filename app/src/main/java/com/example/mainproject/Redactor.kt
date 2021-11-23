package com.example.mainproject

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import net.iharder.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URL
import java.util.*



class Redactor : AppCompatActivity(), DataBase {
    lateinit var listofcontainers: MutableList<Container>
    lateinit var txtTipMusora : AutoCompleteTextView
    lateinit var txtObjemKonteinerov : EditText
    lateinit var txtAddress: AutoCompleteTextView
    lateinit var txtTipOsnovania : AutoCompleteTextView
    lateinit var txtPloshad : EditText
    lateinit var checkUvelichitPloshad : CheckBox
    lateinit var checkSReconstrukcjei : CheckBox
    private lateinit var photoFile: File
    private val FILE_NAME = "photo.jpg"
    private var REQUEST_CODE = 1
    lateinit var checkOgrazhdenie : CheckBox
    lateinit var txtMaterialOgrazhdenia : AutoCompleteTextView
    lateinit var btnAddContainer : Button
    lateinit var btnCommitChanges : Button
    lateinit var containerListView: RecyclerView
    lateinit var showOnMap: ImageButton
    lateinit var deletePlatform:Button
    lateinit var btnAddPhoto: Button
    lateinit var imageList:MutableList<Bitmap>
    lateinit var picList:RecyclerView
    lateinit var mAdapter:PictureListAdapter
    lateinit var adapter:ContainerAdapter
    lateinit var imageBase64list:MutableList<String>
    lateinit var checkNaves:CheckBox
    lateinit var checkKGO:CheckBox
    lateinit var spisokkont:TextView
    lateinit var photografii:TextView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redactor)
        findAllElements()
        var lat = 0.0
        var lng = 0.0
        imageList = mutableListOf()
        val platformID = intent.getSerializableExtra("platformID") as Int
        var platform = Platform(-1,0.0, 0.0, "", "", 0.0, false, false, false, false, false, "", mutableListOf(), "", mutableListOf())
        adapter = ContainerAdapter(mutableListOf())
        MainScope().launch {
            val mProgressDialog = ProgressDialog.show(this@Redactor,  "Загрузка", "Пожалуйста, подождите");
            mProgressDialog.setCanceledOnTouchOutside(false); // main method that force user cannot click outside
            mProgressDialog.setCancelable(true)
            platform = getFullPlatformInfo(platformID).await()
            lat = platform.Lat
            lng = platform.Lng
            txtAddress.setText(platform.Address)
            txtMaterialOgrazhdenia.setText(platform.Fencemat)
            txtTipOsnovania.setText(platform.BaseType)
            txtPloshad.setText(platform.Square.toString())
            checkKGO.isChecked = platform.BoolKGO
            checkNaves.isChecked = platform.BoolNaves
            checkSReconstrukcjei.isChecked = platform.Boolwithrec
            checkOgrazhdenie.isChecked = platform.Boolwithfence
            checkUvelichitPloshad.isChecked = platform.Boolisincreaseble
            listofcontainers = platform.Containersarray!!
            imageBase64list = platform.Base64images
            imageBase64list.forEach {
                val imageByteArray = Base64.decode(it)
                val bmp:Bitmap? = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
                if(bmp != null) imageList.add(bmp)
            }
            mAdapter = PictureListAdapter(imageList, imageBase64list)
            mAdapter.notifyDataSetChanged()
            val recyclerView = findViewById<RecyclerView>(R.id.picList)
            val mLayoutManager = LinearLayoutManager(applicationContext)
            mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
            recyclerView.layoutManager = mLayoutManager
            recyclerView.itemAnimator = DefaultItemAnimator()
            recyclerView.adapter = mAdapter
            mAdapter.notifyDataSetChanged()
            adapter = ContainerAdapter(listofcontainers)
            adapter.notifyDataSetChanged()
            val containerListView = findViewById<RecyclerView>(R.id.containerListView)
            adapter = ContainerAdapter(listofcontainers)
            val cLayoutManager = LinearLayoutManager(applicationContext)
            cLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
            containerListView.layoutManager = cLayoutManager
            containerListView.itemAnimator = DefaultItemAnimator()
            containerListView.adapter = adapter
            adapter.notifyDataSetChanged()
            mProgressDialog.hide()
        }

        val prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE)
        val login = prefs.getString("login", null)

        val baseType = listOf("Бетон", "Плита", "Щебень", "Отсутствует")
        val fenceMat = listOf("Металл", "Профнастил")
        val rubbishType = listOf("ТКО", "Негабарит", "Стекло", "Бумага", "Пищевые отходы")

        val tipOsnovaniaAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
                this, android.R.layout.simple_dropdown_item_1line, baseType)
        txtTipOsnovania.setAdapter(tipOsnovaniaAdapter)

        val og: ArrayAdapter<String> = ArrayAdapter<String>(
                this, android.R.layout.simple_dropdown_item_1line, fenceMat)
        txtMaterialOgrazhdenia.setAdapter(og)

        val rt: ArrayAdapter<String> = ArrayAdapter<String>(
                this, android.R.layout.simple_dropdown_item_1line, rubbishType)
        txtTipMusora.setAdapter(rt)
        txtTipOsnovania.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus) txtTipOsnovania.showDropDown()
        }
        txtTipOsnovania.setOnClickListener {
            txtTipOsnovania.showDropDown()
        }
        txtMaterialOgrazhdenia.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus) txtMaterialOgrazhdenia.showDropDown()
        }
        txtMaterialOgrazhdenia.setOnClickListener {
            txtMaterialOgrazhdenia.showDropDown()
        }

        txtTipMusora.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus) txtTipMusora.showDropDown()
        }
        txtTipMusora.setOnClickListener {
            txtTipMusora.showDropDown()
        }


        if(checkUvelichitPloshad.isChecked) checkSReconstrukcjei.visibility = View.VISIBLE
        else checkSReconstrukcjei.visibility = View.GONE
        if(checkOgrazhdenie.isChecked) txtMaterialOgrazhdenia.visibility = View.VISIBLE
        else txtMaterialOgrazhdenia.visibility = View.GONE
        checkUvelichitPloshad.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) showHide(checkSReconstrukcjei)
            else showHide(checkSReconstrukcjei)
        }
        checkOgrazhdenie.setOnCheckedChangeListener{ _, isChecked ->
            if(isChecked) showHide(txtMaterialOgrazhdenia)
            else showHide(txtMaterialOgrazhdenia)

        }
        listofcontainers = platform.Containersarray!!
        imageBase64list = platform.Base64images








        deletePlatform.setOnClickListener {
            try{
                val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                builder.setMessage("Вы уверены, что хотите удалить площадку?")
                        .setCancelable(true)
                        .setPositiveButton("Да") { _, _ ->
                            deletePlatform(platform.Id)
                            finish()
                        }
                        .setNegativeButton("Нет") { dialog, _ ->
                            dialog.dismiss()
                        }
                val alert = builder.create()
                alert.show()
            }catch (e: Exception){
                Toast.makeText(Redactor(), e.toString(), Toast.LENGTH_LONG).show()
            }
        }
        btnCommitChanges.setOnClickListener {
            val mProgressDialog = ProgressDialog.show(this@Redactor,  "Загрузка", "Пожалуйста, подождите");
            mProgressDialog.setCanceledOnTouchOutside(false); // main method that force user cannot click outside
            mProgressDialog.setCancelable(true)
            btnCommitChanges.isClickable = false
            btnCommitChanges.isEnabled = false
            btnCommitChanges.setBackgroundResource(R.drawable.loginbutton2state)
            try{
            val address = txtAddress.text.toString()
            val baseType = txtTipOsnovania.text.toString()
            val square = txtPloshad.text.toString().toDouble()
            val boolIsIncreaseble = checkUvelichitPloshad.isChecked
                var boolWithRec: Boolean
                if(boolIsIncreaseble) {
                        boolWithRec = checkSReconstrukcjei.isChecked
                    } else {
                        boolWithRec = false
                    }
            val boolWithFence = checkOgrazhdenie.isChecked
            val boolNaves = checkNaves.isChecked
            val boolKGO = checkKGO.isChecked
            var fenceMat:String? = ""
            if(boolNaves) {
                if (txtMaterialOgrazhdenia.text.isNotBlank()) {
                    fenceMat = txtMaterialOgrazhdenia.text.toString()
                } else {
                    txtMaterialOgrazhdenia.setError("Не заполнено")
                }
            }else{
                fenceMat = null
            }
            val containersArray: MutableList<Container>? = if(listofcontainers.isEmpty()) mutableListOf() else listofcontainers
            val lat:Double
            val lng:Double
            val pictures = imageBase64list

            if(newPos!=null){
                lat = newPos!!.latitude
                lng = newPos!!.longitude
            }else{
                lat = platform.Lat
                lng = platform.Lng
            }
            val newPlatform = Platform(platform.Id, lat, lng, address, baseType, square, boolIsIncreaseble, boolWithRec, boolWithFence, boolNaves, boolKGO, fenceMat, containersArray, login, pictures)
            try{
                if(containersArray.isNullOrEmpty()){
                    spisokkont.text = "Добавьте контейнеры"
                    spisokkont.setTextColor(resources.getColor(R.color.red))

                }
                else if(pictures.isNullOrEmpty()){
                    photografii.text = "Приложите фото"
                    photografii.setTextColor(resources.getColor(R.color.red))

                }
                else {
                    MainScope().launch {
                    insertDataToTable(newPlatform, platform.Id).await()
                    mProgressDialog.hide()
                    Toast.makeText(this@Redactor, "Площадка успешно изменена", Toast.LENGTH_LONG).show()
                    finish()
                    }
                }

            }
            catch (e: Exception){
                btnCommitChanges.isEnabled = true
                btnCommitChanges.setBackgroundResource(R.drawable.loginbutton)
                Toast.makeText(this, "Что-то пошло не так: $e", Toast.LENGTH_LONG).show()
            }
            finally {
                mProgressDialog.hide()
                newPos = null
                btnCommitChanges.isClickable = true
                btnCommitChanges.isEnabled = true
                btnCommitChanges.setBackgroundResource(R.drawable.loginbutton)
            }
        }catch (e:Exception){
                mProgressDialog.hide()
                btnCommitChanges.isEnabled = true
                btnCommitChanges.setBackgroundResource(R.drawable.loginbutton)
                checkPlatform()
            }
        }
        btnAddContainer.setOnClickListener {
            try{
            val container = Container(txtTipMusora.text.toString(), txtObjemKonteinerov.text.toString().toDouble())
            listofcontainers.add(container)
            adapter.notifyDataSetChanged()}
            catch (e:java.lang.Exception) {
                checkContainer()
            }
        }
        showOnMap.setOnClickListener {
            val i = Intent(this, ShowContainerOnMap::class.java)
            i.putExtra("lat", lat)
            i.putExtra("lng", lng)
            startActivity(i)
        }
        btnAddPhoto.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile(FILE_NAME)
            val fileProvider = FileProvider.getUriForFile(this, "com.example.mainproject.fileprovider", photoFile)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            if(takePictureIntent.resolveActivity(this.packageManager)!= null){
                startActivityForResult(takePictureIntent, REQUEST_CODE)
            }else{
                Toast.makeText(this, "Невозможно открыть камеру", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            var takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
            if(takenImage.width > takenImage.height){
                takenImage = Bitmap.createScaledBitmap(
                        takenImage,
                        900,
                        480,
                        true
                )} else{
                takenImage = Bitmap.createScaledBitmap(
                        takenImage,
                        480,
                        900,
                        true)
            }
            val stream = ByteArrayOutputStream()
            takenImage.compress(Bitmap.CompressFormat.JPEG, 15, stream)
            val bitmapBytearray = stream.toByteArray()
            val base64 = net.iharder.Base64.encodeBytes(bitmapBytearray)

            imageBase64list.add(base64)
            imageList.add(takenImage)
            mAdapter.notifyDataSetChanged()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showHide(view: View) {
        if (view.visibility == View.VISIBLE){
            view.visibility = View.GONE
        } else{
            view.visibility = View.VISIBLE
        }
    }
    private fun findAllElements(){
        btnAddPhoto = findViewById(R.id.btnAddPhoto)
        picList = findViewById(R.id.picList)
        txtAddress = findViewById(R.id.chooseAddress)
        showOnMap = findViewById(R.id.showOnMap)
        txtTipOsnovania = findViewById(R.id.autoTipOsnovania)
        txtPloshad = findViewById(R.id.txtPloshad)
        checkUvelichitPloshad = findViewById(R.id.checkUvelichitPloshad)
        checkSReconstrukcjei = findViewById(R.id.checkSReconstrukcjei)
        checkOgrazhdenie = findViewById(R.id.checkOgrazhdenie)
        txtMaterialOgrazhdenia = findViewById(R.id.autoOgrazhdenie)
        txtTipMusora = findViewById(R.id.autoTipMusora)
        txtObjemKonteinerov = findViewById(R.id.autoObjemKonteinerov)
        btnAddContainer = findViewById(R.id.addContainer)
        btnCommitChanges = findViewById(R.id.commitChanges)
        containerListView = findViewById(R.id.containerListView)
        deletePlatform = findViewById(R.id.deletePlatform)
        checkKGO = findViewById(R.id.checkKGO)
        checkNaves = findViewById(R.id.checkNaves)
        spisokkont = findViewById(R.id.spisokkont)
        photografii = findViewById(R.id.photografii)
    }
    private fun getAddressLines(RESPONSE: String) : List<String> {
        val jsonObject: JsonObject = Gson().fromJson(RESPONSE, JsonObject::class.java)
        val response = jsonObject.getAsJsonObject("response")
        val geoObjectCollection = response.getAsJsonObject("GeoObjectCollection")
        val featureMember = geoObjectCollection.getAsJsonArray("featureMember")
        return featureMember.map { jsonElement ->
            val memberChild = jsonElement.asJsonObject
            val geoObject = memberChild.getAsJsonObject("GeoObject")
            val metaData = geoObject.getAsJsonObject("metaDataProperty")
            val geocoder = metaData.getAsJsonObject("GeocoderMetaData")
            val addressDetails = geocoder.getAsJsonObject("AddressDetails")
            val country = addressDetails.getAsJsonObject("Country")
            country.get("AddressLine").asString
        }
    }
    override fun onResume(){
        if(newPos != null){
            val apiKey = "18e0a04c-f1d7-4665-af58-499ad280cd46"
            val geocodeURL = "https://geocode-maps.yandex.ru/1.x/?apikey=$apiKey&format=json&geocode=${newPos!!.latitude},${newPos!!.longitude}"
            val apiResponse = URL(geocodeURL).readText()
            val txtAddressArray = getAddressLines(apiResponse)
            txtAddress.setText(txtAddressArray[0])
        }
        super.onResume()
    }
    private fun checkPlatform(){
        if(txtAddress.text.isEmpty()) txtAddress.error = "Не заполнено"
        if(txtTipOsnovania.text.isEmpty()) txtTipOsnovania.error = "Не заполнено"
        if(checkOgrazhdenie.isChecked){
            if(txtMaterialOgrazhdenia.text.isEmpty()) txtMaterialOgrazhdenia.error = "Не заполнено"
        }
        if(txtPloshad.text.isEmpty()) txtPloshad.error = "Не заполнено"
        if(txtMaterialOgrazhdenia.text.isEmpty()) txtAddress.error = "Не заполнено"
    }
    private fun checkContainer(){
        if(txtTipMusora.text.isEmpty()) txtTipMusora.error = "Не заполнено"
        if(txtObjemKonteinerov.text.isEmpty()) txtObjemKonteinerov.error = "Не заполнено"
    }
}