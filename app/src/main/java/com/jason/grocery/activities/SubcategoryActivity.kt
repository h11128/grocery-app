package com.jason.grocery.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.jason.grocery.R
import com.jason.grocery.adapter.ViewPagerAdapterSub
import com.jason.grocery.data.DBHelper
import com.jason.grocery.fragment.SubFragment
import com.jason.grocery.model.*
import kotlinx.android.synthetic.main.activity_subcat.*
import kotlinx.android.synthetic.main.relative_tool_cart.view.*

class SubcategoryActivity : AppCompatActivity(), SubFragment.QuantityCallBack {
    private var catId = 0
    private lateinit var queue: RequestQueue
    private lateinit var subcategory: SubCategory
    private var subCount: Int = 0

    private lateinit var subAdapter: ViewPagerAdapterSub
    private var subList: ArrayList<Data2> = arrayListOf()
    private var counter = 0
    private var textViewInsideCart: TextView? = null
    private lateinit var dbHelper: DBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subcat)
        val toolbar: Toolbar = findViewById(R.id.toolbar_general)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
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
        textViewInsideCart = view.text_inside_cart
        updateQuantity()
        return super.onCreateOptionsMenu(menu)
    }

    private fun init() {
        val intent = intent
        queue = Volley.newRequestQueue(applicationContext)
        catId = intent.getStringExtra(KEY_Category)?.toInt() ?: 1
        subCount = intent.getIntExtra(KEY_Count, 5)
        val categoryName = intent.getStringExtra(Name_Category)
        this.title = categoryName
        Log.d("abc", "getExtra catId $catId subCount $subCount")
        val subList: ArrayList<Data2> = arrayListOf()
        subAdapter = ViewPagerAdapterSub(subList, supportFragmentManager)
        tab_sub.setupWithViewPager(view_pager_sub)

        view_pager_sub.adapter = subAdapter

        getSub(catId)
        dbHelper = DBHelper(this)


    }

    private fun getSub(catId1: Int) {
        Log.d("abc", "getSub catId $catId1")
        val catRequest = StringRequest(Request.Method.GET, getSubCategoryUrl(catId1), {
            counter += 1
            val gson = Gson()
            subcategory = gson.fromJson(it, SubCategory::class.java)
            for (j in subcategory.data.indices) {
                subAdapter.addItem(subcategory.data[j])
                subList.add(subcategory.data[j])

            }
            if (catId == 5) {
                tab_sub.selectTab(tab_sub.getTabAt(subList.size - 1))
            } else if (catId1 == catId) {
                tab_sub.selectTab(tab_sub.getTabAt(subAdapter.getItemPosition(subcategory.data[0])))
            }
        }, {
            Log.d("abc", "volley error $it")
            counter += 1
        })
        queue.add(catRequest)
    }

    override fun updateQuantity() {
        val totalCount = dbHelper.countAll()
        Log.d("abc", "total count in $totalCount")
        if (totalCount <= 0){
            textViewInsideCart?.visibility = View.GONE

        }
        else{
            textViewInsideCart?.visibility = View.VISIBLE
            textViewInsideCart?.text = totalCount.toString()
        }
    }


}