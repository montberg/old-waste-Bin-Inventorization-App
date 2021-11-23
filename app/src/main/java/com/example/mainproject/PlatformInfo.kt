package com.example.mainproject

import java.io.Serializable

class PlatformInfo (id:Int,
                lat:Double,
                lng:Double,
                address:String
): Serializable {
    val Id = id
    val Lat = lat
    val Lng = lng
    val Address = address

}