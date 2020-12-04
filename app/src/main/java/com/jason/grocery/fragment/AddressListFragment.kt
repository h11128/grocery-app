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
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.jason.grocery.R
import com.jason.grocery.activities.ui.PaymentActivity
import com.jason.grocery.adapter.RecyclerAdapterAddress
import com.jason.grocery.model.*
import kotlinx.android.synthetic.main.fragment_address_list.view.*

class AddressListFragment : Fragment(), RecyclerAdapterAddress.OnItemClick {
    // TODO: Rename and change types of parameters

    private lateinit var sessionManager: SessionManager
    lateinit var queue: RequestQueue
    private lateinit var mContext: Context
    lateinit var adapter: RecyclerAdapterAddress
    var orderSummary: OrderSummary? = null
    var dataList: ArrayList<Address> = arrayListOf(
        Address(1,"1","1","1",1,"1","1","1")

    )

    fun refreshDataList(addressList: ArrayList<Address>){
        dataList = addressList
        Log.d("abc", "refreshDataList in fragment")

        adapter.refreshDataList(dataList)
    }


    private fun init(view: View) {
        Log.d("abc", "dataList size pass into adapter ${dataList.size}")
        adapter = RecyclerAdapterAddress(this,  dataList).apply {
            userName = sessionManager.getUserName()
        }
        view.recycler_view_address.adapter = adapter
        Log.d("abc", "item count ${adapter.itemCount}")
        view.recycler_view_address.layoutManager = LinearLayoutManager(mContext)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_address_list, container, false)
        init(view)
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        sessionManager = SessionManager(context)
        queue = Volley.newRequestQueue(context)
        Log.d("abc", "current dataList in AddressList fragment ${dataList.size}")

    }

    companion object {
        @JvmStatic
        fun newInstance(addressList: ArrayList<Address>) =
            AddressListFragment().apply {
                dataList = addressList
                Log.d("abc", "recreate dataList in AddressList fragment ${dataList.size}")
            }

    }

    override fun clickCallback(data: Address) {
        val intent = Intent(activity, PaymentActivity::class.java).apply{
            Log.d("abc","orderSummary $orderSummary when click on address $data" )
            putExtra(KEY_Address, data)

            putExtra(key_orderSummary, orderSummary)
        }
        startActivityForResult(intent, 0)

    }
}