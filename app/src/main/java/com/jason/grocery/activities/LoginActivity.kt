package com.jason.grocery.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.jason.grocery.R
import com.jason.grocery.model.SessionManager
import com.jason.grocery.model.User
import com.jason.grocery.model.url_login
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        title = "Login"
        sessionManager = SessionManager(this)
        if (sessionManager.isLogin) {
            loginSuccess()
        }
        init()
    }

    private fun init() {
        button_login.setOnClickListener {
            val input_username = edit_login_user.text.toString()
            val input_password = edit_login_password.text.toString()
            loginOnline(input_username, input_password)

        }
        button_register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)

        }
    }

    private fun loginSuccess() {
        val intent = Intent(this, MainActivity::class.java)
        finish()
        startActivity(intent)
    }

    private fun loginOnline(username: String, password: String) {
        val queue = Volley.newRequestQueue(this)
        val data = HashMap<String, String>()
        data["email"] = username
        data["password"] = password
        val sendData = JSONObject(data as Map<*, *>)
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url_login, sendData, {
            Log.d("success", "success $it")
            val token = it.getString("token")
            val user = Gson().fromJson(it.getString("user"), User::class.java)
            Log.d("abc", "get user $user")
            sessionManager.updateToken(token)
            sessionManager.loginStatus(true)
            sessionManager.loginData(username, user._id, user.mobile)
            Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT)
                .show()
            loginSuccess()
        }, {
            Log.d("error", it.toString())
            Log.d("error", "${ParseError(it).stackTrace}")
            Log.d("error", "${it.message}")
            Log.d("error", "${it.networkResponse.statusCode}")
            Toast.makeText(this, "error $it", Toast.LENGTH_SHORT)
                .show()
        })
        queue.add(jsonObjectRequest)
    }


}