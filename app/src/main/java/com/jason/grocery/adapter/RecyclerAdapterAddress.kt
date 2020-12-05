package com.jason.grocery.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jason.grocery.R

import com.jason.grocery.model.Address
import kotlinx.android.synthetic.main.recycler_adapter_address.view.*

class RecyclerAdapterAddress(
    private var mContext: OnItemClick,
    private var mList: ArrayList<Address>
) :
    RecyclerView.Adapter<RecyclerAdapterAddress.MyViewHolder>() {
    private var callback: OnItemClick? = null
    var userName = ""

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: Address) {
            Log.d("bind", "$data")
            itemView.text_user.text = "User: $userName"
            itemView.text_city.text = "City: " + data.city
            itemView.text_houseNo.text = "HouseNo: " + data.houseNo
            itemView.text_pincode.text = "Pincode: " + data.pincode.toString()
            itemView.text_address_type.text = "Type: " + data.type
            itemView.setOnClickListener {
                callback?.clickCallback(data)
            }
        }
    }


    interface OnItemClick {
        fun clickCallback(data: Address)
    }

    fun refreshDataList(newList: ArrayList<Address>) {
        Log.d("abc", "refreshDataList in adapter")
        mList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_adapter_address, parent, false)
        Log.d("abc", "onCreateViewHolder ${mList.size}")
        callback = mContext
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun getItem(position: Int): Address {
        return mList[position]
    }
}