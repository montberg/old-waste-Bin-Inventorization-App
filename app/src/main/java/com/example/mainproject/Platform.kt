package com.example.mainproject

import com.yandex.runtime.bindings.BytesHandler
import java.io.Serializable
import java.sql.Blob

class Platform(id:Int,
               lat:Double,
               lng:Double,
               address:String,
               baseType:String,
               square:Double,
               boolIsIncreaseble:Boolean = false,
               boolWithRec:Boolean = false,
               boolWithFence:Boolean = false,
               boolNaves:Boolean = false,
               boolKGO:Boolean = false,
               fenceMat:String? = null,
               containersArray:MutableList<Container>? = mutableListOf(),
               userLogin:String?,
               base64images: MutableList<String> = mutableListOf()
): Serializable {
    val Id = id
    val Lat = lat
    val Lng = lng
    val Address = address
    val BaseType = baseType
    val Square = square
    val Boolisincreaseble = boolIsIncreaseble
    val Boolwithrec = boolWithRec
    val Boolwithfence = boolWithFence
    val BoolNaves = boolNaves
    val BoolKGO = boolKGO
    val Fencemat = fenceMat
    val Containersarray = containersArray
    val UserLogin = userLogin
    val Base64images = base64images
}