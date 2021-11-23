package com.example.mainproject

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import java.util.*

var URL = "jdbc:firebirdsql://[[[URL до бд в виде jdbc:firebirdsql://<IP-адрес либо сайт>:<порт>/<полный путь до БД>]]]"
var userLogin = ""
var userPassword = ""

interface DataBase {
    fun getConnectionProperties():Properties{
        Class.forName("org.firebirdsql.jdbc.FBDriver")
        val props = Properties()
        props.setProperty("user", "")
        props.setProperty("password", "")
        props.setProperty("encoding", "WIN1251")
        return props
    }

    fun getUserProperties():Properties{
        Class.forName("org.firebirdsql.jdbc.FBDriver")
        val props = Properties()
        props.setProperty("user", "")
        props.setProperty("password", "")
        props.setProperty("encoding", "WIN1251")
        return props
    }

    fun checkUser(login: String, password: String): Boolean {
        val connection: Connection = DriverManager.getConnection(URL, getConnectionProperties())
        val query = "select * from users where login='$login'"
        val statement: Statement = connection.createStatement()
        val dataBaseResponse: ResultSet = statement.executeQuery(query)
        while(dataBaseResponse.next()) {
            try {
                if (dataBaseResponse.getString("login") == login && dataBaseResponse.getString("password")  == password) return true
            } catch (e: Exception) {
                return false
            }
        }
        connection.close()
        return false
    }

      fun insertDataToTable(platform: Platform):Deferred<Boolean>{
         return GlobalScope.async {
             val connection: Connection = DriverManager.getConnection(URL, getUserProperties())
             var lastID = 0
             val statement: Statement = connection.createStatement()
             var strSQL = "INSERT INTO platform(lat, lng, address, basetype, square, boolisinc, boolwithrec, boolwithfence, fencemat, containeramount, userlogin, boolwithcanopy, boolkgo) " +
                     "VALUES(${platform.Lat}, ${platform.Lng}, '${platform.Address}', '${platform.BaseType}', ${platform.Square}, ${platform.Boolisincreaseble}," +
                     " ${platform.Boolwithrec}, ${platform.Boolwithfence}, '${platform.Fencemat}', ${platform.Containersarray?.size}, ${platform.UserLogin}, ${platform.BoolNaves}, ${platform.BoolKGO}) returning id;"
             val dataBaseResponse: ResultSet = statement.executeQuery(strSQL)
             while (dataBaseResponse.next()) {
                 lastID = dataBaseResponse.getInt(1)
             }
             if (platform.Containersarray!!.isNotEmpty()) {
                 platform.Containersarray.forEach { c ->
                     strSQL = "insert into container(rubbishtype, volume, parent_id) values('${c.RubbishType}', ${c.Volume}, ${lastID});"
                     statement.execute(strSQL)
                 }
             }
             if (platform.Base64images.isNotEmpty()) {
                 platform.Base64images.forEach { c ->
                     strSQL = "insert into pictures(picture, parent_id) values('${c}', ${lastID});"
                     statement.execute(strSQL)
                 }
             }
             connection.close()
             true
         }
    }
    fun deletePlatform(id: Int){
        GlobalScope.async {
            val connection: Connection = DriverManager.getConnection(URL, getUserProperties())
            val statement: Statement = connection.createStatement()
            var deleteQuery = "delete from platform where id = $id"
            statement.execute(deleteQuery)
            deleteQuery = "delete from pictures where parent_id = $id"
            statement.execute(deleteQuery)
            connection.close()
        }
    }
    fun insertDataToTable(platform: Platform, id: Int):Deferred<Boolean>{
        return GlobalScope.async {
            val connection: Connection = DriverManager.getConnection(URL, getUserProperties())
            val lastID: Int = id
            val statement: Statement = connection.createStatement()
            var strSQL =
                "UPDATE OR INSERT INTO platform(id, lat, lng, address, basetype, square, boolisinc, boolwithrec, boolwithfence, fencemat, containeramount, userlogin, boolwithcanopy, boolkgo) " +
                        "VALUES(${lastID}, ${platform.Lat}, ${platform.Lng}, '${platform.Address}', '${platform.BaseType}', ${platform.Square}, ${platform.Boolisincreaseble}," +
                        " ${platform.Boolwithrec}, ${platform.Boolwithfence}, '${platform.Fencemat}', ${platform.Containersarray?.size}, '${platform.UserLogin}', ${platform.BoolNaves}, ${platform.BoolKGO}) MATCHING(id);"
            statement.execute(strSQL)
            strSQL = "delete from container where parent_id = $lastID;"
            statement.execute(strSQL)
            platform.Containersarray?.forEach { c ->
                strSQL =
                    "insert into container(rubbishtype, volume, parent_id) values('${c.RubbishType}', ${c.Volume}, ${lastID});"
                statement.execute(strSQL)
            }
            strSQL = "delete from pictures where parent_id = $lastID;"
            statement.execute(strSQL)
            platform.Base64images.forEach { c ->
                strSQL = "insert into pictures(picture, parent_id) values('${c}', ${lastID});"
                statement.execute(strSQL)
            }
            connection.close()
            true
        }
    }
    fun getPlatform(): Deferred<MutableList<PlatformInfo>> {
        return GlobalScope.async {
        val connection: Connection = DriverManager.getConnection(URL, getConnectionProperties())
        val statement: Statement = connection.createStatement()
        val getPlatforms = "select id, lat, lng, address from platform"
        val response:ResultSet = statement.executeQuery(getPlatforms)
        val PlatformArray:MutableList<PlatformInfo> = arrayListOf()
        while(response.next()){
            val id = response.getInt(1)
            val Lat = response.getFloat(2)
            val Lng =  response.getFloat(3)
            val Address = response.getString(4)
            val tempPlatform = PlatformInfo(id, Lat.toDouble(), Lng.toDouble(), Address.toString())
            PlatformArray.add(tempPlatform)
        }
        connection.close()
        PlatformArray
        }
    }
    fun getFullPlatformInfo(id: Int): Deferred<Platform> {
        return GlobalScope.async {
            var Id = 0
            var Lat = 0.0F
            var Lng= 0.0F
            var Address  = ""
            var BaseType = ""
            var Square= 0.0F
            var Boolisincreaseble= false
            var Boolwithrec= false
            var Boolwithfence= false
            var Fencemat = ""
            var UserLogin = ""
            var Boolwithcanopy = false
            var Boolkgo= false
            val tempContainerList:MutableList<Container> = arrayListOf()
            val pictures = mutableListOf<String>()
        val connection: Connection = DriverManager.getConnection(URL, getConnectionProperties())
        val statement: Statement = connection.createStatement()
        val getPlatforms = "select * from platform where id = $id"
        val response = statement.executeQuery(getPlatforms)
        while(response.next()){
            Id = response.getInt(1)
            Lat = response.getFloat(2)
            Lng =  response.getFloat(3)
            Address = response.getString(4)
            BaseType = response.getString(5)
            Square = response.getFloat(6)
            Boolisincreaseble = response.getBoolean(7)
            Boolwithrec = response.getBoolean(8)
            Boolwithfence = response.getBoolean(9)
            Fencemat = response.getString(10)
            UserLogin = response.getString(11)
            Boolwithcanopy = response.getBoolean(13)
            Boolkgo = response.getBoolean(14)
        }
            val getContainersQuery = "select * from container where parent_id = '${id}'"
            val statement2: Statement = connection.createStatement()
            val containersListResponse = statement2.executeQuery(getContainersQuery)
            while(containersListResponse.next()) {
                val rubbishtype = containersListResponse.getString(2)
                val volume = containersListResponse.getString(3)
                val container = Container(rubbishtype, volume.toDouble())
                tempContainerList.add(container)
            }
            containersListResponse.close()
            val picturesQuery = "select * from pictures where parent_id = '${id}'"
            val statement3: Statement = connection.createStatement()
            val picturesResponse = statement3.executeQuery(picturesQuery)
            while(picturesResponse.next()){
                val base64picture = picturesResponse.getString(2)
                pictures.add(base64picture)
            }
            picturesResponse.close()

            connection.close()
            val returnedPlatform = Platform(Id, Lat.toDouble(), Lng.toDouble(), Address, BaseType, Square.toDouble(), Boolisincreaseble, Boolwithrec, Boolwithfence, Boolwithcanopy, Boolkgo, Fencemat, tempContainerList, UserLogin, pictures)
            returnedPlatform
        }
    }
    }
