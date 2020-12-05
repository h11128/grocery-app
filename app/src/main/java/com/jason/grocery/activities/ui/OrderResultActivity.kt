package com.jason.grocery.activities.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.jason.grocery.R
import com.jason.grocery.model.*
import kotlinx.android.synthetic.main.activity_order_result.*
import org.json.JSONArray
import org.json.JSONObject

class OrderResultActivity : AppCompatActivity() {
    private var orderList = arrayListOf<Order>()
    private var orderStatus = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_result)

        val toolbar: Toolbar = findViewById(R.id.toolbar_general)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = "Order Result"

        init()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
            R.id.item_logout -> {
                setResult(Result_Code_Log_out)
                finish()
            }
            R.id.item_MainMenu -> {
                setResult(Result_Code_To_Main)
                finish()
            }
            R.id.item_shopping_cart -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.item_shopping_cart)?.isEnabled = false
        menu?.findItem(R.id.item_shopping_cart)?.isVisible = false
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        setResult(Result_Code_back)
        super.onBackPressed()
    }


    private fun init() {
        getOrderOnline()
        text_order_result.text = "order submit fail ${intent.getStringExtra("result")}"
    }

    private fun getOrderOnline() {
        val queue = Volley.newRequestQueue(applicationContext)
        val jsonObjectRequest =
            StringRequest(Request.Method.GET, url_order + "/${SessionManager(this).getUserId()}", {
                val data = JSONObject(it).get("data") as JSONArray
                for (index in 0 until data.length()) {
                    val te = Gson().fromJson(data.get(index).toString(), Order::class.java)
                    if (te._id == intent.getStringExtra("orderId")) {
                        orderStatus = true
                        text_order_result.text = "success: ${Gson().toJson(te)}"

                    }
                    orderList.add(te)
                }

            }, {
                Log.d("error", it.toString())
                Log.d("error", "${ParseError(it).stackTrace}")
                Log.d("error", "${it.message}")
                Log.d("error", "${it.networkResponse.statusCode}")
                Toast.makeText(this, "error $it", Toast.LENGTH_SHORT).show()
            })
        queue.add(jsonObjectRequest)
    }

}