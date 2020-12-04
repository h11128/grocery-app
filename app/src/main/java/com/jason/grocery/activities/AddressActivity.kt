package com.jason.grocery.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.jason.grocery.R
import com.jason.grocery.adapter.ViewPagerAdapterAddress
import com.jason.grocery.data.DBHelper
import com.jason.grocery.fragment.AddressListFragment
import com.jason.grocery.fragment.EditAddressFragment
import com.jason.grocery.model.*
import kotlinx.android.synthetic.main.activity_address.*
import kotlinx.android.synthetic.main.relative_tool_cart.view.*
import org.json.JSONArray
import org.json.JSONObject

class AddressActivity : AppCompatActivity(), EditAddressFragment.passAddress {
    private lateinit var viewPagerAdapater: ViewPagerAdapterAddress
    private lateinit var viewPager2: ViewPager2
    private lateinit var orderSummary: OrderSummary
    private lateinit var sessionManager: SessionManager
    private lateinit var queue: RequestQueue
    private lateinit var dbHelper: DBHelper
    private var textView_inside_cart: TextView? = null

    var addressList: ArrayList<Address> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)
        orderSummary = intent.getSerializableExtra(KEY_OrderSummary) as OrderSummary
        Log.d("abc", "get orderSummary $orderSummary from intent")
        sessionManager = SessionManager(this)
        dbHelper = DBHelper(this)

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

    override fun onBackPressed() {
        setResult(Result_Code_back)
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val view = menu.findItem(R.id.item_shopping_cart).actionView
        view.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivityForResult(intent, 0)
        }
        textView_inside_cart = view.text_inside_cart
        updateQuantity()
        return super.onCreateOptionsMenu(menu)
    }

    private fun updateQuantity() {
        val total_count = dbHelper.countAll()
        Log.d("abc", "total count in $total_count")
        if (total_count <= 0){
            textView_inside_cart?.visibility = View.GONE

        }
        else{
            textView_inside_cart?.visibility = View.VISIBLE
            textView_inside_cart?.text = total_count.toString()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            Result_Code_Log_out -> {
                setResult(Result_Code_Log_out)
                finish()
            }
            Result_Code_To_Main -> {
                setResult(Result_Code_To_Main)
                finish()
            }
            Result_Code_back -> {
                Log.d("abc", "$resultCode")
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun init() {
        viewPager2 = view_pager_address
        queue = Volley.newRequestQueue(applicationContext)

        val toolbar: Toolbar = findViewById(R.id.toolbar_general)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = "Address"


        viewPagerAdapater = ViewPagerAdapterAddress(supportFragmentManager, lifecycle)
        viewPager2.adapter = viewPagerAdapater
        getAddressOnline()

        button_add_address.setOnClickListener {
            viewPager2.currentItem = 1
            Log.d("ABC", viewPager2.currentItem.toString())
        }
    }

    private fun postAddressOnline(data: Address) {
        val sendData = HashMap<String, String>()
        sendData["pincode"] = data.pincode.toString()
        sendData["city"] = data.city
        sendData["streetName"] = data.streetName
        sendData["houseNo"] = data.houseNo
        sendData["type"] = data.type
        sendData["userId"] = data.userId

        val sendDataObject = JSONObject(sendData as Map<*, *>)
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url_address, sendDataObject, {
            Log.d("success", "post address success")


        }, {
            Log.d("error", it.toString())
            Log.d("error", "${ParseError(it).stackTrace}")
            Log.d("error", "${it.message}")
            Log.d("error", "${it.networkResponse.statusCode}")
            Toast.makeText(this, "error $it", Toast.LENGTH_SHORT).show()
        })
        queue.add(jsonObjectRequest)
    }

    private fun getAddressOnline() {
        val userId = sessionManager.getUserId()
        val jsonObjectRequest = StringRequest(Request.Method.GET, url_user_address + userId, {
            Log.d("abc", "get address online success $it")
            val data = JSONObject(it).get("data") as JSONArray
            val onlineList = ArrayList<Address>()

            for (index in 0 until data.length()) {
                val te = Gson().fromJson(data.get(index).toString(), Address::class.java)
                onlineList.add(te)
                Log.d("abc", te.toString())
            }
            addressList = onlineList
            viewPagerAdapater.update(orderSummary, addressList)
        }, {
            Log.d("error", it.toString())
            Log.d("error", "${ParseError(it).stackTrace}")
            Log.d("error", "${it.message}")

        })
        queue.add(jsonObjectRequest)
    }

    override fun editAddressCallback(data: Address) {

        Log.d("abc", "get Address ${data}")
        postAddressOnline(data)
        getAddressOnline()
        viewPagerAdapater.update(orderSummary, addressList)
        viewPager2.currentItem = 0
    }
}