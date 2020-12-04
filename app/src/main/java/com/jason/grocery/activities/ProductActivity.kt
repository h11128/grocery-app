package com.jason.grocery.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.jason.grocery.R
import com.jason.grocery.data.DBHelper
import com.jason.grocery.data.Data3simple
import com.jason.grocery.model.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.android.synthetic.main.relative_add_cart.*
import kotlinx.android.synthetic.main.relative_tool_cart.view.*


class ProductActivity : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var myToast: Toast
    private lateinit var productData: Data3
    private var simpleData: Data3simple? = null
    private var textView_inside_cart: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        val toolbar: Toolbar = findViewById(R.id.toolbar_general)
        setSupportActionBar(toolbar)
//        toolbar.setNavigationOnClickListener{
//            finish()
//        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        //supportActionBar?.setDisplayShowHomeEnabled(true)
        dbHelper = DBHelper(this)
        myToast = Toast.makeText(this, "abc", Toast.LENGTH_SHORT)
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
                val intent = Intent(this, CartActivity::class.java)
                startActivityForResult(intent, 0)
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

    override fun onResume() {
        super.onResume()
        Log.d("abc", "onResume is call")
        simpleData = dbHelper.read(productData.getData3Simple())
        switchCartUI(checkStatus())

    }

    private fun init() {

        productData = intent.getSerializableExtra(KEY_Product) as Data3
        simpleData = dbHelper.read(productData.getData3Simple())

        Picasso.get()
            .load(getImageUrl(productData.image))
            .into(image_des)
        text_title.text = productData.productName
        title = productData.productName
        text_des2.text = productData.description
        val details = "Price: $${productData.price} fresh"
        val endIndex = details.indexOf("fresh")
        val ss1 = SpannableString(details)
        ss1.setSpan(RelativeSizeSpan(1.2f), 7, endIndex, 0)
        ss1.setSpan(ForegroundColorSpan(Color.RED), 7, endIndex, 0)
        ss1.setSpan(ForegroundColorSpan(getColor(R.color.darkGreen)), endIndex, endIndex + 5, 0)

        button_add_cart.setOnClickListener {
            val data = productData.getData3Simple()
            simpleData = data
            Log.d("abc", "data simple quantity ${data.quantity}")
            dbHelper.updateOrInsert(data)
            switchCartUI(true)
            showToast(0)
        }

        button_add_quantity.setOnClickListener {
            simpleData = dbHelper.changeQuantity(productData.getData3SimpleSample(), 1)
            switchCartUI(checkStatus())
            showToast(1)
        }

        button_minus_quantity.setOnClickListener {
            simpleData = dbHelper.changeQuantity(productData.getData3SimpleSample(), -1)
            switchCartUI(checkStatus())
            showToast(1)
        }

        text_des3.text = ss1
    }


    private fun checkStatus(): Boolean {
        return simpleData!= null && simpleData!!.quantity >0
    }

    private fun showToast(mode: Int){
        var showText = ""
        showText = "Add ${simpleData?.productName} to cart!\nCurrent Quantity: ${simpleData?.quantity}"

        if (mode == 1){
            showText = if (checkStatus()){
                "Add ${simpleData?.productName} to cart!\nCurrent Quantity: ${simpleData?.quantity}"
            } else{
                "Delete ${productData.productName} to cart!"
            }
        }

        myToast.cancel()
        myToast = Toast.makeText(applicationContext, showText, Toast.LENGTH_SHORT)
        myToast.setGravity(Gravity.CENTER , 0, 200)
        myToast.duration = Toast.LENGTH_SHORT
        myToast.show()
    }

    private fun switchCartUI(newStatus: Boolean){
        Log.d("abc", "swtich Cart UI $newStatus")
        if(newStatus){
            button_add_cart.visibility = View.GONE
            button_add_quantity.visibility = View.VISIBLE
            button_minus_quantity.visibility = View.VISIBLE
            text_Inside.visibility = View.VISIBLE
        }
        else{
            button_add_cart.visibility = View.VISIBLE
            button_add_quantity.visibility = View.GONE
            button_minus_quantity.visibility = View.GONE
            text_Inside.visibility = View.GONE
        }
        text_Inside.text = simpleData?.quantity.toString()
        updateQuantity()


    }
    fun updateQuantity() {
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

}