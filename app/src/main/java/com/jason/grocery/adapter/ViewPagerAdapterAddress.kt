package com.jason.grocery.adapter

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jason.grocery.fragment.AddressListFragment
import com.jason.grocery.fragment.EditAddressFragment
import com.jason.grocery.model.Address
import com.jason.grocery.model.OrderSummary

class ViewPagerAdapterAddress(private var fm: FragmentManager, lf: Lifecycle) :
    FragmentStateAdapter(fm, lf) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                Log.d("anc", "current addressList size $addressList")
                AddressListFragment.newInstance(addressList)
            }
            else -> {
                EditAddressFragment.newInstance(addressList)
            }
        }
    }

    fun update(orderSummary: OrderSummary?, dataList: ArrayList<Address>) {
        val fragment = fm.findFragmentByTag("f0") as AddressListFragment
        Log.d("abc", "update addressList ${dataList.size}")
        fragment.refreshDataList(dataList)
        Log.d("abc", "pass $orderSummary to fragment")
        fragment.orderSummary = orderSummary
    }


    companion object {
        var addressList: ArrayList<Address> = arrayListOf()


    }

}