package com.jason.grocery.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jason.grocery.fragment.ImageFragment
import com.jason.grocery.model.Data1
import java.util.*

class ViewPagerAdapterMain(
    private var mList: ArrayList<Data1>,
    fm: FragmentManager,
    lf: Lifecycle
) : FragmentStateAdapter(fm, lf) {

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun createFragment(position: Int): Fragment {
        return ImageFragment(mList[position], mList.size)
    }

    fun updateItem(position: Int, data: Data1) {
        if (position + 1 > mList.size) {
            mList.add(data)
        } else {
            mList[position] = data
        }
        notifyDataSetChanged()
    }
}