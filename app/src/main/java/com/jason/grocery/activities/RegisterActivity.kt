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
import com.jason.grocery.R
import com.jason.grocery.model.SessionManager
import com.jason.grocery.model.url_register
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        sessionManager = SessionManager(this)
        title = "Register"
        init()
    }


    private fun init() {
        button_login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        button_register.setOnClickListener {
            val username = edit_register_user.text.toString()
            val password = edit_register_password.text.toString()
            val firstName = edit_register_firstName.text.toString()
            val mobile = edit_register_mobile.text.toString()
            registerOnline(firstName, username, password, mobile)
            //val al = sessionManager.getData()
            //sessionManager.register(al[0] + ";" + username, al[1] + ";" + password)

        }
    }

    private fun registerOnline(
        firstName: String,
        username: String,
        password: String,
        mobile: String
    ) {
        val queue = Volley.newRequestQueue(this)
        val data = HashMap<String, String>()
        data["email"] = username
        data["password"] = password
        data["firstName"] = firstName
        data["mobile"] = mobile
        val sendData = JSONObject(data as Map<*, *>)
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url_register, sendData, {
            Toast.makeText(
                this,
                "register success username $username password $password",
                Toast.LENGTH_SHORT
            ).show()
            Log.d("success", it.toString())

        }, {
            Log.d("error", it.toString())
            Log.d("error", it.message.toString())
            Log.d("error", "${ParseError(it).stackTrace}")
            Log.d("error", it.networkResponse.toString())
            Log.d("error", "${it.networkResponse.statusCode}")
            Toast.makeText(this, "error $it", Toast.LENGTH_SHORT)
                .show()
        })
        queue.add(jsonObjectRequest)
    }
}