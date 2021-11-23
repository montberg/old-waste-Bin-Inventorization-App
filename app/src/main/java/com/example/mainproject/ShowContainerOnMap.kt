package com.example.mainproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

var newPos : Point? = null
class ShowContainerOnMap : AppCompatActivity() {
    private lateinit var mapview: MapView
    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.setApiKey("6f2989af-38b6-4884-b695-e32115108530")
        MapKitFactory.initialize(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_container_on_map)
        mapview = findViewById(R.id.map)
        if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ){
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            ActivityCompat.requestPermissions(this, permissions,0)
        }
        val containerPos = Point(intent.getDoubleExtra("lng", 0.0),intent.getDoubleExtra("lat",0.0))
        mapview.map.mapObjects.addPlacemark(containerPos, ImageProvider.fromResource(this, R.drawable.ic_marker_dumpster))
        moveCamera(containerPos, 16F)
        val addContainerButton = findViewById<Button>(R.id.changePos)
        addContainerButton.setOnClickListener {
                    newPos = Point(mapview.map.cameraPosition.target.longitude, mapview.map.cameraPosition.target.latitude)
                    finish()
        }
    }
     private fun moveCamera(point: Point, zoom: Float){
        mapview.map.move(
                CameraPosition(point, zoom, 0.0f, 0.0f),
                Animation(Animation.Type.LINEAR, 0.5F),
                null
        )
    }
}