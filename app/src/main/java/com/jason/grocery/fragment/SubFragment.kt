package com.jason.grocery.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.jason.grocery.R
import com.jason.grocery.activities.ProductActivity
import com.jason.grocery.adapter.RecyclerAdapterSub
import com.jason.grocery.data.DBHelper
import com.jason.grocery.data.Data3simple
import com.jason.grocery.model.*
import kotlinx.android.synthetic.main.fragment_sub.view.*

class SubFragment : Fragment(), RecyclerAdapterSub.Callback {
    // TODO: Rename and change types of parameters
    private lateinit var queue: RequestQueue
    lateinit var subcategory: Data2
    private lateinit var product: Product
    private var productList: ArrayList<Data3> = arrayListOf()
    lateinit var dbHelper: DBHelper
    lateinit var adapter: RecyclerAdapterSub
    lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sub, container, false)
        recyclerView = view.recycler_view_sub
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        getSub()
        return view
    }

    private fun init() {
        queue = Volley.newRequestQueue(context?.applicationContext)
        adapter = RecyclerAdapterSub(this, productList)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        init()
        dbHelper = DBHelper(context)
    }

    private fun getSub() {
        val subcategoryId = subcategory.subId
        val subUrl = getProductUrl(subcategoryId)
        val catRequest = StringRequest(Request.Method.GET, subUrl, {
            product = Gson().fromJson(it, Product::class.java)
            for (count in product.data.indices) {
                productList.add(product.data[count])
                //Log.d("abc", "add product to product list: ${product.data[count].productName}")
            }
            adapter.refreshDataList(productList)
        }, {
            Log.d("abc", "volley error $it")
        })
        queue.add(catRequest)
    }

    companion object {
        @JvmStatic
        fun newInstance(sub: Data2) =
            SubFragment().apply { subcategory = sub }
    }

    override fun onResume() {
        super.onResume()
        adapter.refreshDataList(productList)
    }

    override fun onItemClick(data: Data3) {
        val intent1 = Intent(activity, ProductActivity::class.java).apply {
            putExtra(KEY_Product, data)
        }
        startActivityForResult(intent1, 0)
    }

    override fun changeQuantity(data: Data3simple, operation: Int): Data3simple? {
        return if (operation == 0){
            dbHelper.read(data)
        } else{
            dbHelper.changeQuantity(data, operation)
        }
    }

    override fun insertItem(data: Data3): Data3simple? {
        dbHelper.updateOrInsert(data.getData3Simple())
        return dbHelper.read(data.getData3Simple())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {

            Result_Code_Log_out -> {
                activity?.setResult(Result_Code_Log_out)
                activity?.finish()
            }
            Result_Code_To_Main -> {
                activity?.setResult(Result_Code_To_Main)
                activity?.finish()
            }
            Result_Code_back -> {
                Log.d("abc", "$resultCode")
                //activity?.setResult(2)
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

}