package com.jason.grocery.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.jason.grocery.R
import com.jason.grocery.adapter.RecyclerAdapterMain
import com.jason.grocery.adapter.ViewPagerAdapterMain
import com.jason.grocery.data.DBHelper
import com.jason.grocery.model.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header.view.*
import kotlinx.android.synthetic.main.relative_tool_cart.view.*
import kotlinx.android.synthetic.main.tool_bar.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    RecyclerAdapterMain.ClickCallBack {
    private lateinit var imageList: ArrayList<Data1>
    private lateinit var queue: RequestQueue
    private lateinit var category: Category
    private lateinit var viewPagerAdapter: ViewPagerAdapterMain
    private lateinit var sessionManager:SessionManager
    private lateinit var drawerLayout: DrawerLayout
    private val catUrl = url_cate
    private var textViewInsideCart: TextView? = null
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //supportActionBar?.setDisplayShowHomeEnabled(true)
        init()
    }

    private fun init() {
        val toolbar: Toolbar = toolbar_general
        setSupportActionBar(toolbar)
        sessionManager = SessionManager(this)
        Log.d("abc", "id from MainActivity ${sessionManager.getUserId()}")
        imageList = arrayListOf()

        viewPagerAdapter = ViewPagerAdapterMain(imageList, supportFragmentManager, lifecycle)
        view_pager_main.adapter = viewPagerAdapter
        getCategory()
        drawerLayout = drawer_main
        val navView = nav_view
        val headerView = navView.getHeaderView(0)
        headerView.text_header_username.text = sessionManager.getUserName().split("@")[0]
        headerView.text_header_idk.text = sessionManager.getUserName()

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar_general, 0,0)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
        dbHelper = DBHelper(this)
        Log.d("abc", "item count ${viewPagerAdapter.itemCount}")
    }

    private fun getCategory() {
        queue = Volley.newRequestQueue(applicationContext)
        val catRequest = StringRequest(Request.Method.GET, catUrl, {
            category = Gson().fromJson(it, Category::class.java)
            for (count in category.data.indices) {
                viewPagerAdapter.updateItem(count, category.data[count])
            }

            recycler_view_main.adapter = RecyclerAdapterMain(this, category.data as ArrayList<Data1>)
            recycler_view_main.layoutManager = StaggeredGridLayoutManager(2, 1)

        }, {
            Log.d("abc", "volley error $it")
        })
        queue.add(catRequest)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            super.onBackPressed()
        }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_shopping_cart -> {
                val intent = Intent(this, CartActivity::class.java)
                startActivityForResult(intent, 0)
            }
            R.id.item_logout -> logout()
            android.R.id.home -> onBackPressed()

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            Result_Code_Log_out -> logout()
            Result_Code_To_Main -> Toast.makeText(this, "back to main activity", Toast.LENGTH_SHORT)
                .show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun logout() {
        val sessionManager = SessionManager(this)
        sessionManager.loginStatus(false)
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_shopping_cart -> {
                val intent = Intent(this, CartActivity::class.java)
                startActivityForResult(intent, 0)
            }
            R.id.item_logout -> logout()
            android.R.id.home -> onBackPressed()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true

    }
    private fun updateQuantity() {
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

    override fun onItemClick(catId: Int, count: Int, categoryName: String) {
            Log.d("abc", "onItemClick")
            Log.d("abc", "catId $catId count $count")
            val intent1 = Intent(this, SubcategoryActivity::class.java).apply {
                Log.d("abc", "catId $catId count $count")
                putExtra(KEY_Category, catId.toString())
                putExtra(KEY_Count, count)
                putExtra(Name_Category, categoryName)
            }
            startActivityForResult(intent1, 0)

    }
}