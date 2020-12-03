package com.jason.grocery.model

import android.content.Context
import android.widget.Toast
import com.android.volley.toolbox.Volley

const val file_name = "userdata"
const val mode = Context.MODE_PRIVATE
const val key_name = "username"
const val key_password = "password"
const val key_login = "loginKey"
const val key_token = "token"
const val key_id = "key_id"
const val key_mobile = "key_mobile"
const val key_email = "email"
const val key_orderSummary = "key_order"

class SessionManager(var mContext: Context) {

    val sharedPreferences = mContext.getSharedPreferences("userdata", Context.MODE_PRIVATE)
    private var data: HashMap<String, String> = hashMapOf()
    var isLogin: Boolean = sharedPreferences.getBoolean(key_login, false)
    var queue = Volley.newRequestQueue(mContext)
    fun login(username: String, password: String): Boolean {
        updateData()
        if (data.containsKey(username) && data[username] == password) {
            loginStatus(true)
            return true
        }
        return false
    }

    fun loginStatus(status: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(key_login, status)
            apply()
        }
    }

    fun loginData(username: String, userId:String, mobile:String) {
        with(sharedPreferences.edit()) {
            putString(key_name, username)
            putString(key_id, userId)
            putString(key_mobile, mobile)
            apply()
        }
    }

    fun getUserName(): String {
        return sharedPreferences.getString(key_name, "abc").toString()
    }

    fun getUserId(): String {
        return sharedPreferences.getString(key_id, "abc").toString()
    }

    fun getMobile(): String {
        return sharedPreferences.getString(key_mobile, "abc").toString()
    }

    fun getData(): ArrayList<String> {
        val usernameString =
            sharedPreferences.getString(key_name, "abc")
                .toString()
        val passwordString =
            sharedPreferences.getString(key_password, "abc")
                .toString()
        val token = sharedPreferences.getString(key_token, "abc").toString()
        return arrayListOf(usernameString, passwordString, token)
    }

    private fun getToken(): List<String> {
        val tokens = sharedPreferences.getString(key_token, "abc").toString()
        return tokens.split(";")
    }

    fun updateToken(newToken: String) {
        val tokens = sharedPreferences.getString(key_token, "abc").toString() + newToken + ";"
        with(sharedPreferences.edit()) {
            putString(key_token, tokens)
            apply()
        }
    }

    fun findToken(targetToken: String): Boolean {
        val result = getToken().find { it == targetToken }
        return result != null
    }

    private fun updateData() {
        val al = getData()
        val usernameString = al[0]
        val passwordString = al[1]
        if (usernameString.isNotEmpty() && passwordString.isNotEmpty()) {
            val usernames = usernameString.split(";")
            val passwords = passwordString.split(";")
            for (i in usernames.indices) {
                data[usernames[i]] = passwords[i]
            }
        }
    }

    fun register(
        username: String,
        password: String
    ) {
        data[username] = password
        with(sharedPreferences.edit()) {
            clear()
            putString(key_name, username)
            putString(key_password, password)
            apply()
        }
        Toast.makeText(
            mContext,
            "Register Success, username $username, password $password",
            Toast.LENGTH_SHORT
        ).show()

    }


}