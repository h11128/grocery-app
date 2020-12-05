package com.jason.grocery.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jason.grocery.R
import com.jason.grocery.activities.CartActivity
import com.jason.grocery.data.Data3simple
import com.jason.grocery.model.getImageUrl
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_adapter_product2.view.*
import kotlinx.android.synthetic.main.relative_add_cart.view.*

class RecyclerAdapterCart(
    mContext: Context,
    private var mList: ArrayList<Data3simple>
) :
    RecyclerView.Adapter<RecyclerAdapterCart.MyViewHolder>() {
    var callback: CallBack? = mContext as? CartActivity

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: Data3simple) {
            Picasso.get().load(getImageUrl(data.image))
                .error(R.drawable.image_error).into(itemView.image_product1)
            itemView.text_product2_title.text = data.productName
            data.getRealPrice()
            val priText = ((data.priceNumber)?.times(1)).toString() + " $"
            val mrpText =
                (data.mrpNumber).toString() + "$" + "  save ${(data.mrpNumber as Double - data.priceNumber!!)}$"
            val ss2 = SpannableString(mrpText)
            val index1 = mrpText.indexOf("save")
            ss2.setSpan(StrikethroughSpan(), 0, index1 - 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            ss2.setSpan(ForegroundColorSpan(Color.RED), index1, ss2.length, 0)
            ss2.setSpan(StyleSpan(Typeface.ITALIC), index1, ss2.length, 0)
            ss2.setSpan(RelativeSizeSpan(0.8f), index1, ss2.length, 0)
            itemView.text_product2_mrp_price.text = ss2
            itemView.text_product2_price.text = priText

            itemView.text_Inside.text = data.quantity.toString()
            itemView.button_add_quantity.setOnClickListener {
                callback?.changeQuantity(data, 1)
            }

            itemView.button_minus_quantity.setOnClickListener {
                callback?.changeQuantity(data, -1)
            }

        }
    }

    fun refreshDataList(newList: ArrayList<Data3simple>) {
        mList = newList
        notifyDataSetChanged()
    }

    interface CallBack {
        fun changeQuantity(data: Data3simple, operation: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_adapter_product2, parent, false)
        return MyViewHolder(view)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun getItem(position: Int): Data3simple {
        return mList[position]
    }
}