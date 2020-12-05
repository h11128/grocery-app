package com.jason.grocery.model

import android.content.Context
import android.content.SharedPreferences
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley


const val key_name = "username"
const val key_password = "password"
const val key_login = "loginKey"
const val key_token = "token"
const val key_id = "key_id"
const val key_mobile = "key_mobile"
const val key_orderSummary = "key_order"

class SessionManager(mContext: Context) {

    private val sharedPreferences: SharedPreferences =
        mContext.getSharedPreferences("userdata", Context.MODE_PRIVATE)
    var isLogin: Boolean = sharedPreferences.getBoolean(key_login, false)
    var queue: RequestQueue = Volley.newRequestQueue(mContext)

    fun loginStatus(status: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(key_login, status)
            apply()
        }
    }

    fun loginData(username: String, userId: String, mobile: String) {
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

    fun updateToken(newToken: String) {
        val tokens = sharedPreferences.getString(key_token, "abc").toString() + newToken + ";"
        with(sharedPreferences.edit()) {
            putString(key_token, tokens)
            apply()
        }
    }


}