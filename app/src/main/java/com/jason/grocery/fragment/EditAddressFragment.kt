package com.jason.grocery.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jason.grocery.R
import com.jason.grocery.activities.AddressActivity
import com.jason.grocery.model.Address
import com.jason.grocery.model.SessionManager
import kotlinx.android.synthetic.main.fragment_edit_address.*


class EditAddressFragment : Fragment() {
    var dataList: ArrayList<Address> = arrayListOf()
    private lateinit var mContext: Context
    private var callback: PassAddress? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

    }

    private fun init() {
        button_edit_add_address.setOnClickListener {
            val city = edit_address_city.text.toString()
            val houseNo = edit_address_houseNo.text.toString()
            val pincode = edit_address_Pincode.text.toString().toInt()
            val type = edit_address_type.text.toString()
            val streetName = edit_address_streetName.text.toString()
            val username = SessionManager(mContext).getUserId()
            val address = Address(dataList.size,dataList.size.toString(),city,houseNo, pincode, streetName,type,username)
            dataList.add(address)
            callback?.editAddressCallback(address)
        }
    }

    interface PassAddress{
        fun editAddressCallback(data: Address)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_address, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        callback = mContext as? AddressActivity
    }

    companion object {
        @JvmStatic
        fun newInstance(addressList: ArrayList<Address>) =
            EditAddressFragment().apply {
                dataList = addressList
            }

    }
}