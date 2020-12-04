package com.jason.grocery.model


const val url_image = "http://rjtmobile.com/grocery/images/"
const val url_api = "https://grocery-second-app.herokuapp.com/api/"
const val url_subcategory = "${url_api}subcategory/"
const val url_product = "${url_api}products/sub/"
const val url_cate = "${url_api}category"
const val url_register = "${url_api}auth/register"
const val url_login = "${url_api}auth/login"
const val url_address = "${url_api}address"
const val url_user_address = "${url_api}address/"
const val url_order = "${url_api}orders"
const val KEY_Product = "product"
const val KEY_Category = "catId"
const val KEY_Count = "count"
const val KEY_OrderSummary = "order_summary"
const val KEY_Address = "address"
const val Name_Category = "name_cate"
const val Result_Code_Log_out = 2
const val Result_Code_To_Main = 1
const val Result_Code_back = 0

val getImageUrl = fun(ImageId: String): String {
    return "$url_image${ImageId}"
}
val getProductUrl = fun(subcategoryId: Int): String {
    return "$url_product$subcategoryId"
}

val getSubCategoryUrl = fun(categoryId: Int): String {
    return url_subcategory + categoryId
}

