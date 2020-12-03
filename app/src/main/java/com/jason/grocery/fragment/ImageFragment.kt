package com.jason.grocery.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jason.grocery.R
import com.jason.grocery.activities.MainActivity
import com.jason.grocery.model.Data1
import com.jason.grocery.model.getImageUrl
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_image.*
import kotlinx.android.synthetic.main.fragment_image.view.*

class ImageFragment(var data: Data1, private var count: Int) : Fragment() {
    private var url: String = data.catImage
    private var catId = data.catId

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("abc", url)
        text_image_fragment.text = url
        Picasso.get().load(getImageUrl(url))
            .error(R.drawable.image_error).into(view.image_view_pager)
        view.image_view_pager.setOnClickListener {
            MainActivity.onItemClick(
                catId,
                count,
                data.catName
            )
        }
    }


}