package com.jason.grocery.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jason.grocery.R
import com.jason.grocery.activities.MainActivity
import com.jason.grocery.model.Data1
import com.jason.grocery.model.getImageUrl
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_adapter_category.view.*

class RecyclerAdapterMain(var mList: ArrayList<Data1>) :
    RecyclerView.Adapter<RecyclerAdapterMain.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: Data1) {
            Picasso.get().load(getImageUrl(data.catImage)).error(R.drawable.image_error)
                .into(itemView.image_view_category)
            itemView.text_category.text = data.catName
            itemView.setOnClickListener {
                Log.d("abc", "${mList.size}")
                MainActivity.onItemClick(data.catId, mList.size, data.catName)
            }
            Log.d("abc", "bind ${data.catName}")
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerAdapterMain.MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_adapter_category, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(mList[position])
    }
}