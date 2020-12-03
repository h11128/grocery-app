package com.jason.grocery.activities.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.jason.grocery.R
import com.jason.grocery.data.DBHelper
import com.jason.grocery.model.*
import kotlinx.android.synthetic.main.activity_paymen.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap

class PaymenActivity : AppCompatActivity() {
    lateinit var sessionManager: SessionManager
    lateinit var total_order: Order
    lateinit var queue: RequestQueue
    lateinit var result: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paymen)
        sessionManager = SessionManager(this)
        val toolbar: Toolbar = findViewById(R.id.toolbar_general)
        setSupportActionBar(toolbar)
//        toolbar.setNavigationOnClickListener{
//            finish()
//        }
        queue = Volley.newRequestQueue(applicationContext)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        title = "Place Your Order"
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

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.item_shopping_cart)?.isEnabled = false
        menu?.findItem(R.id.item_shopping_cart)?.isVisible = false

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
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
        val userId = sessionManager.getUserId()

        val data = intent.getSerializableExtra(KEY_Address)
        var orderSummary = intent.getSerializableExtra(key_orderSummary) as OrderSummary
        orderSummary._id = userId
        Log.d("orderSummary", "$orderSummary")
        val shippingAddress = (data as Address).toShipAddress()
        val dbHelper = DBHelper(this)
        val orders = dbHelper.readAll()
        val productXList = arrayListOf<ProductX>()
        for (order in orders) {
            val p = order.toProductX()
            if (p != null) {
                p._id = userId
                productXList.add(p)
            }
        }

        val user = User(userId, sessionManager.getUserName(), sessionManager.getMobile())
        total_order =
            Order(
                0, userId, "abc", "Confirmed",
                orderSummary, productXList, shippingAddress, user, userId
            )
        val shippingString =
            "Shipping to: ${sessionManager.getUserName()}, ${shippingAddress.houseNo}, ${shippingAddress.city}, ${shippingAddress.pincode}"
        Log.d("abc", shippingString)
        val ss0 = SpannableString(shippingString)
        val index0 = ss0.lastIndexOf("to:") + 4
        ss0.setSpan(StyleSpan(Typeface.BOLD), index0, ss0.length, 0)
        ss0.setSpan(ForegroundColorSpan(Color.BLACK), index0, ss0.length, 0)

        text_shipping_to.text = ss0

        val DeliveryString = "Delivery: ${Calendar.getInstance().time}"
        val ss1 = SpannableString(DeliveryString)
        val index1 = ss1.indexOf("Delivery:") + 10
        Log.d("abc", "index ${index0} ${index1}")
        ss1.setSpan(StyleSpan(Typeface.BOLD), index1, ss1.length, 0)
        ss1.setSpan(RelativeSizeSpan(1f), index1, ss1.length, 0)
        ss1.setSpan(ForegroundColorSpan(getColor(R.color.darkGreen)), index1, ss1.length, 0)

        text_order_delivery.text = ss1
        text_item_count.text = "Items (${orderSummary.totalAmount})"
        text_items.text = "$${orderSummary.ourPrice + orderSummary.discount}"

        text_order_shipping.text = "$${orderSummary.deliveryCharges}"
        text_order_promotion.text = "-$${orderSummary.discount}"
        text_order_beforetax.text = "$${orderSummary.ourPrice - orderSummary.deliveryCharges}"
        val tax = (orderSummary.ourPrice - orderSummary.deliveryCharges)* 0.05
        text_order_tax.text = "$${tax}"
        text_order_total.text = "$${orderSummary.ourPrice - orderSummary.deliveryCharges + tax}"

        button_confirm_payment.setOnClickListener {
            postOrderOnline1(total_order)

        }


    }

    private fun returnOrderResult(orderResult: String, id: String) {
        result = orderResult
        val intent = Intent(this, OrderResultActivity::class.java).apply {
            putExtra("result", result)
            putExtra("orderId", id)
        }
        startActivityForResult(intent, 0)

    }


    private fun postOrderOnline(data: Order) {
        val sendData = HashMap<String, String>()
        sendData["orderStatus"] = Gson().toJson(data.orderStatus)
        sendData["orderSummary"] = Gson().toJson(data.orderSummary)
        sendData["products"] = Gson().toJson(data.products)
        sendData["shippingAddress"] = Gson().toJson(data.shippingAddress)
        sendData["user"] = Gson().toJson(data.user)
        sendData["userId"] = Gson().toJson(data.userId)


        val sendDataObject = JSONObject(sendData as Map<*, *>)

        val sendDataObject2 = JSONObject(Gson().toJson(data))
        val sendDataObject3 = JSONObject(Gson().toJson(data.toSendOrder()))
        val sendDataObject4 = Gson().toJson(data.toSendOrder())
        val sendDataObject5 = JSONObject(Gson().toJson(sendDataObject))
        val sendDataObject7 = JSONObject()
        sendDataObject7.put("orderStatus",  data.orderStatus)
        sendDataObject7.put("orderSummary",  JSONObject(Gson().toJson(data.orderSummary)))
        sendDataObject7.put("products",  JSONArray(Gson().toJson(data.products)))
        sendDataObject7.put("shippingAddress",  JSONObject(Gson().toJson(data.shippingAddress)))
        sendDataObject7.put("user",  JSONObject(Gson().toJson(data.user)))
        sendDataObject7.put("userId",  data.userId)

        for (key in sendData.keys) {
            Log.d("abc", "key $key value ${sendData[key]}")
        }
        Log.d("abc", "sendDataObject7 ${sendDataObject7}")
        Log.d("abc", "sendDataObject ${sendDataObject}")
        Log.d("abc", "sendDataObject2 ${sendDataObject2}")
        Log.d("abc", "sendDataObject3 ${sendDataObject3}")
        Log.d("abc", "sendDataObject4 ${sendDataObject4}")
        Log.d("abc", "sendDataObject5 ${sendDataObject5}")


        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url_order, sendDataObject7, {
            val orderId = it.getJSONObject("data").getString("_id")
            returnOrderResult("post order success", orderId)


        }, {
            result = it.toString()
            Log.d("error", it.toString())
            Log.d("error", "${ParseError(it).stackTrace}")
            Log.d("error", "${it.message}")
            Log.d("error", "${it.networkResponse.statusCode}")
            Toast.makeText(this, "error $it", Toast.LENGTH_SHORT).show()
            returnOrderResult("fail to post order, error $it", "0")


        })
        queue.add(jsonObjectRequest)
    }

    private fun postOrderOnline1(data: Order){
        val api = SendOrder.SendOrderApi()
        api.postOrder(data.toSendOrder()).enqueue(object: Callback<OrderResponse>{
            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                val data1 = response.body()
                Log.d("abc","data1 ${data1}")

                if (data1 != null){
                    val orderId = data1.data._id
                    Log.d("abc","post order success $orderId")
                    returnOrderResult("post order success", orderId)
                }

            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                Log.d("abc", "error $t ${t.cause} ${t.message}")
                returnOrderResult("fail to post order, error $t ${t.cause} ${t.message}" , "0")

            }
        })

    }
}