package com.example.mainproject

import java.io.Serializable

class Container(rubbishType:String = "", volume:Double = 0.0):Serializable{
    val RubbishType = rubbishType
    val Volume = volume
}