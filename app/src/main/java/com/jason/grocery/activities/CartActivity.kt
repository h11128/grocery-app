package com.jason.grocery.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jason.grocery.R
import com.jason.grocery.adapter.RecyclerAdapterCart
import com.jason.grocery.data.DBHelper
import com.jason.grocery.data.Data3simple
import com.jason.grocery.model.*
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.relative_add_cart.*
import kotlinx.android.synthetic.main.relative_tool_cart.view.*
import kotlinx.android.synthetic.main.tool_bar.*


class CartActivity : AppCompatActivity(), RecyclerAdapterCart.CallBack {
    private lateinit var adapter: RecyclerAdapterCart
    private var data3simpleList: ArrayList<Data3simple> = arrayListOf()
    private lateinit var dbHelper: DBHelper
    private var cartStatus = true
    private var subTotal = 0.0
    private lateinit var orderSummary: OrderSummary
    private var textView_inside_cart: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        init()
    }


    private fun init() {
        dbHelper = DBHelper(this)
        val toolbar: Toolbar = findViewById(R.id.toolbar_general)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        title = "Shopping Cart"
        button_check_out.setOnClickListener {
            Log.d("abc", "orderSummary when checkout ${orderSummary}")
            val intent = Intent(this, AddressActivity::class.java).apply { putExtra(
                KEY_OrderSummary, orderSummary) }
            startActivityForResult(intent, 0)
            refresh()
        }
        adapter = RecyclerAdapterCart(this, data3simpleList)
        recycler_view_cart.adapter = adapter
        recycler_view_cart.layoutManager = LinearLayoutManager(this)
        initSwipe()
        refresh()
    }

    private fun initSwipe(){
        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            )
        // or ItemTouchHelper.DOWN or ItemTouchHelper.UP
        {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                Toast.makeText(this@CartActivity, "on Move", Toast.LENGTH_SHORT).show()
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                //Remove swiped item from list and notify the RecyclerView
                val position = viewHolder.adapterPosition
                val data = adapter.getItem(position)
                dbHelper.delete(data)
                Toast.makeText(this@CartActivity, "delete data ${data.productName} ", Toast.LENGTH_SHORT).show()
                refresh()
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recycler_view_cart)
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

    private fun refresh() {
        data3simpleList = dbHelper.readAll()
        adapter.refreshDataList(dbHelper.readAll())
        calculate()
        switchItemUI(subTotal > 0.0)
        Log.d("abc", "refresh adapter")
        Log.d("abc","current product ${dbHelper.readAll().toArray().toString()}")
    }

    override fun changeQuantity(data: Data3simple, operation: Int) {
        dbHelper.changeQuantity(data, operation)
        refresh()
        Log.d("abc", "change quantity ${data.productName}")
    }

    private fun calculate() {
        subTotal = 0.0
        var discountTotal = 0.0
        var amountTotal = 0
        for (data in data3simpleList) {
            data.getRealPrice()
            val price = data.mrpNumber
            val mrpPrice = data.priceNumber
            var total = 0.0
            var discount = 0.0
            if (price != null && mrpPrice != null) {
                total = price * data.quantity
                discount = (price - mrpPrice) * data.quantity
            }
            subTotal += total
            discountTotal += discount
            amountTotal += data.quantity
        }


        val subText = "$subTotal$"
        val disText = "$discountTotal$"
        val priText = if (subTotal > discountTotal) {
            (subTotal - discountTotal).toString() + "$"
        } else {
            "0 $"
        }
        val ss1 = SpannableString(disText)
        ss1.setSpan(ForegroundColorSpan(Color.RED), 0, ss1.length , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val ss2 = SpannableString(priText)
        ss2.setSpan(ForegroundColorSpan(getColor(R.color.darkGreen)), 0, ss2.length , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        text_sub_total.text = subText
        text_discount.text = ss1
        text_price_total.text = ss2
        orderSummary = OrderSummary("a", 30, discountTotal, data3simpleList.size, subTotal - discountTotal, amountTotal)
    }

    private fun switchItemUI(newStatus: Boolean){
        Log.d("abc", "switch UI $newStatus")

        if (newStatus != cartStatus){
            if(newStatus){
                recycler_view_cart.visibility = View.VISIBLE
                view_price_info.visibility = View.VISIBLE
                button_check_out.visibility = View.VISIBLE
                text_no_item_in_cart.visibility = View.GONE
            }
            else{
                recycler_view_cart.visibility = View.GONE
                view_price_info.visibility = View.GONE
                button_check_out.visibility = View.GONE
                text_no_item_in_cart.visibility = View.VISIBLE
            }
            cartStatus = newStatus
        }
        updateQuantity()
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
}