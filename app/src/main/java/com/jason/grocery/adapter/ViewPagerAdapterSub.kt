package com.jason.grocery.adapter

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.jason.grocery.fragment.SubFragment
import com.jason.grocery.model.Data2
import java.util.*

class ViewPagerAdapterSub(
    private var mList: ArrayList<Data2>,
    fm: FragmentManager
) : FragmentStatePagerAdapter(fm) {
    private var fragmentList: HashMap<Data2, Fragment> = hashMapOf()

    fun addItem(data: Data2) {
        Log.d("abc", "add sub ${data.subName}")
        mList.add(data)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return mList.size
    }

    override fun getItem(position: Int): Fragment {
        Log.d("abc", "select tab ${getPageTitle(position)}")
        return if (fragmentList.containsKey(mList[position]) && fragmentList[mList[position]] != null) {
            Log.d("abc", "return a existing product")

            fragmentList[mList[position]]!!
        } else {
            fragmentList[mList[position]] = SubFragment.newInstance(mList[position])
            fragmentList[mList[position]]!!
        }
    }

    fun getItemPosition(data: Data2): Int {
        return mList.indexOf(data)
    }

    override fun getPageTitle(position: Int): CharSequence {
        return mList[position].subName
    }
}