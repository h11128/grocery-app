package com.jason.grocery.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jason.grocery.R
import com.jason.grocery.data.Data3simple
import com.jason.grocery.model.Data3
import com.jason.grocery.model.getImageUrl
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_adapter_product2.view.*
import kotlinx.android.synthetic.main.relative_add_cart.view.*


class RecyclerAdapterSub(
    private var mContext: Callback,
    private var mList: ArrayList<Data3>,

    ) :
    RecyclerView.Adapter<RecyclerAdapterSub.MyViewHolder>() {
    private var callback: Callback? = null

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: Data3) {
            Picasso.get().load(getImageUrl(data.image))
                .error(R.drawable.image_error).into(itemView.image_product1)
            itemView.text_product2_title.text = data.productName
            val priText = (data.price).toString() + "$"
            val mrpText = (data.mrp).toString() + "$" + "  save ${(data.mrp as Double - data.price)}$"
            val ss2 = SpannableString(mrpText)
            val index1 = mrpText.indexOf("save")
            ss2.setSpan(StrikethroughSpan(), 0, index1 - 2 , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            ss2.setSpan(ForegroundColorSpan(Color.RED), index1, ss2.length, 0)
            ss2.setSpan(StyleSpan(Typeface.ITALIC), index1, ss2.length, 0)
            ss2.setSpan(RelativeSizeSpan(0.8f), index1, ss2.length, 0)
            itemView.text_product2_mrp_price.text = ss2

            itemView.text_product2_price.text = priText

            itemView.button_add_cart.setOnClickListener {
                callback?.insertItem(data)
                refreshCartUI(data, itemView)
            }

            itemView.button_add_quantity.setOnClickListener {
                callback?.changeQuantity(data.getData3Simple(), 1)
                refreshCartUI(data, itemView)
            }

            itemView.button_minus_quantity.setOnClickListener {
                callback?.changeQuantity(data.getData3Simple(), -1)
                refreshCartUI(data, itemView)
            }
            refreshCartUI(data, itemView)

        }
    }

    private fun checkStatus(simpleData: Data3simple?): Boolean {
        return simpleData != null && simpleData.quantity > 0
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerAdapterSub.MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_adapter_product2, parent, false)
        val holder = MyViewHolder(view)
        callback = mContext
        return holder
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    interface Callback {
        fun onItemClick(data: Data3)

        fun changeQuantity(data: Data3simple, operation: Int): Data3simple?

        fun insertItem(data: Data3): Data3simple?
    }

    fun refreshDataList(newList: ArrayList<Data3>) {
        mList = newList
        notifyDataSetChanged()
    }

    fun refreshCartUI(data: Data3, itemView: View){
        val simpleData1 = callback?.changeQuantity(data.getData3Simple(), 0)
        val status = checkStatus(simpleData1)
        switchCartUI(simpleData1, status, itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(mList[position])
        holder.itemView.setOnClickListener {
            callback?.onItemClick(mList[position])
        }
    }


    private fun switchCartUI(simpleData: Data3simple?, newStatus: Boolean, itemView: View) {
        Log.d(
            "abc",
            "switch Cart UI for product ${simpleData?.productName} to $newStatus with quantity ${simpleData?.quantity}"
        )
        if (newStatus) {
            itemView.button_add_cart.visibility = View.GONE
            itemView.button_add_quantity.visibility = View.VISIBLE
            itemView.button_minus_quantity.visibility = View.VISIBLE
            itemView.text_Inside.visibility = View.VISIBLE

        } else {
            itemView.button_add_cart.visibility = View.VISIBLE
            itemView.button_add_quantity.visibility = View.GONE
            itemView.button_minus_quantity.visibility = View.GONE
            itemView.text_Inside.visibility = View.GONE
        }
        itemView.text_Inside.text = simpleData?.quantity.toString()


    }


}