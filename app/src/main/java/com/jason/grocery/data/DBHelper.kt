package com.jason.grocery.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import com.jason.grocery.model.ProductX
import java.io.Serializable

data class Data3simple(
    val catId: Int,
    val image: String,
    val productName: String,
    var quantity: Int,
    val price: String,
    var priceNumber: Double? = 0.0,
    var mrpNumber: Double? = 0.0
) : Serializable {
    fun getRealPrice() {
        val ar = price.split("#")
        if (ar.size > 1) {
            priceNumber = ar[0].toDoubleOrNull()
            mrpNumber = ar[1].toDoubleOrNull()
        }

    }

    fun toProductX(): ProductX?{
        return if (mrpNumber != null && priceNumber != null){
            val p = ProductX(catId.toString(), image, mrpNumber!!.toInt(), priceNumber!!.toInt(), productName, quantity)
            p
        } else{
            null
        }
    }
}

class DBHelper(var context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        var DATABASE_VERSION = 1
        const val DATABASE_NAME = "product_database"
        private const val DATABASE_TABLE_NAME = "product"
        var DATABASE_new_TABLE_NAME = "product"
        private const val COLUMN_NAME_CATID = "catId"
        private const val COLUMN_NAME_NAME = "productName"
        private const val COLUMN_NAME_Image = "image"
        private const val COLUMN_NAME_QUANTITY = "quantity"
        private const val COLUMN_NAME_Price = "price"

        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $DATABASE_TABLE_NAME"
        val SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS $DATABASE_new_TABLE_NAME ($COLUMN_NAME_CATID INTEGER,$COLUMN_NAME_NAME TEXT,$COLUMN_NAME_Image TEXT,$COLUMN_NAME_QUANTITY INTEGER,$COLUMN_NAME_Price TEXT)"
        var SQL_CREATE_NEW_TABLE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS $DATABASE_new_TABLE_NAME ($COLUMN_NAME_CATID INTEGER,$COLUMN_NAME_NAME TEXT,$COLUMN_NAME_Image TEXT,$COLUMN_NAME_QUANTITY INTEGER,$COLUMN_NAME_Price TEXT)"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)

    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
        DATABASE_VERSION += 1

    }

    override fun onDowngrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        onUpgrade(db, oldVersion, newVersion)
        DATABASE_VERSION -= 1

    }

    fun createNewTable(newTableName: String) {
        val db = writableDatabase
        DATABASE_new_TABLE_NAME = newTableName
        try {
            db.execSQL(SQL_CREATE_NEW_TABLE_ENTRIES)
            Log.d("database", "table $DATABASE_new_TABLE_NAME create successfully")
            onUpgrade(db, DATABASE_VERSION, DATABASE_VERSION + 1)
        } catch (e: Exception) {
            Log.d("database", "table $DATABASE_new_TABLE_NAME create fail error $e")
        }


    }

    fun insert(product: Data3simple): Long {
        val db = writableDatabase
        Log.d("data", "insert data ${product.catId}")

        val data = ContentValues().apply {
            put(COLUMN_NAME_CATID, product.catId)
            put(COLUMN_NAME_NAME, product.productName)
            put(COLUMN_NAME_Image, product.image)
            put(COLUMN_NAME_QUANTITY, product.quantity)
            put(COLUMN_NAME_Price, product.price)

        }

        return db.insert(DATABASE_new_TABLE_NAME, null, data)
    }

    fun insertIfNotExist(product: Data3simple): Long? {
        return if (read(product) != null) {
            Log.d("database", "data already exist")
            null
        } else {
            insert(product)
        }
    }

    @Synchronized
    fun changeQuantity(data: Data3simple, operation: Int): Data3simple? {
        var currentData = read(data)
        if (currentData == null) {
            if (operation > 0) {
                data.quantity = 1
                insert(data)
            }

        } else {
            if (currentData.quantity <= 0 || (operation < 0 && currentData.quantity <= 1)) {
                delete(currentData)
            } else {
                val newData = currentData.copy(quantity = currentData.quantity + operation)
                update(newData, currentData)
            }
        }
        currentData = read(data)
        return currentData

    }

    fun updateOrInsert(product: Data3simple): Int {
        val oldData = read(product)
        return if (oldData != null) {
            //Log.d("abc", "old quantity ${oldData.quantity}")

            update(product, oldData)
            oldData.quantity + 1
        } else {
            //Log.d("abc", "insert product")

            insert(product)
            product.quantity
        }
    }


    fun read(data3simple: Data3simple): Data3simple? {
        val db = readableDatabase
        val projection = null
        val selection = "$COLUMN_NAME_NAME = ?"
        val selectionArg = arrayOf(data3simple.productName)
        val sortOrder = "$COLUMN_NAME_CATID ASC"
        val cursor = db.query(
            DATABASE_new_TABLE_NAME,
            projection,
            selection,
            selectionArg,
            null,
            null,
            sortOrder
        )
        if (cursor != null) {
            with(cursor) {
                while (moveToNext()) {
                    val id1 = getInt(getColumnIndexOrThrow(COLUMN_NAME_CATID))
                    val name = getString(getColumnIndexOrThrow(COLUMN_NAME_NAME))
                    val image = getString(getColumnIndexOrThrow(COLUMN_NAME_Image))
                    val quantity = getInt(getColumnIndexOrThrow(COLUMN_NAME_QUANTITY))
                    val price = getString(getColumnIndexOrThrow(COLUMN_NAME_Price))
                    return Data3simple(
                        catId = id1,
                        productName = name,
                        image = image,
                        quantity = quantity,
                        price = price
                    )
                }
            }
        } else {
            Toast.makeText(
                context,
                "not find data with name ${data3simple.productName}",
                Toast.LENGTH_SHORT
            )
                .show()
            return null
        }
        return null

    }

    fun readAll(): ArrayList<Data3simple> {
        val db = readableDatabase
        val projection = null
        val sortOrder = "$COLUMN_NAME_CATID ASC"
        val result: ArrayList<Data3simple> = arrayListOf()
        val cursor =
            db.query(DATABASE_new_TABLE_NAME, projection, null, null, null, null, sortOrder)
        with(cursor) {
            while (moveToNext()) {
                val id1 = getInt(getColumnIndexOrThrow(COLUMN_NAME_CATID))
                val name = getString(getColumnIndexOrThrow(COLUMN_NAME_NAME))
                val image = getString(getColumnIndexOrThrow(COLUMN_NAME_Image))
                val quantity = getInt(getColumnIndexOrThrow(COLUMN_NAME_QUANTITY))
                val price = getString(getColumnIndexOrThrow(COLUMN_NAME_Price))

                val product = Data3simple(
                    catId = id1,
                    productName = name,
                    image = image,
                    quantity = quantity,
                    price = price
                )
                product.getRealPrice()
                result.add(product)
            }
        }
        return result
    }

    fun countAll(): Int {
        val db = readableDatabase
        val projection = null
        val sortOrder = "$COLUMN_NAME_CATID ASC"
        var result: Int = 0
        val cursor =
            db.query(DATABASE_new_TABLE_NAME, projection, null, null, null, null, sortOrder)
        with(cursor) {
            while (moveToNext()) {
                val quantity = getInt(getColumnIndexOrThrow(COLUMN_NAME_QUANTITY))
                if (quantity > 0){
                    result += quantity
                }
            }
        }
        return result
    }

    fun delete(product: Data3simple): Int {
        val db = writableDatabase
        val selection = "$COLUMN_NAME_NAME LIKE ?"
        val selectionArgs = arrayOf(product.productName)
        return db.delete(DATABASE_new_TABLE_NAME, selection, selectionArgs)
    }

    fun deleteAll(): Int {
        val db = writableDatabase
        return db.delete(DATABASE_new_TABLE_NAME, null, null)
    }

    fun update(
        newProduct: Data3simple,
        oldProduct: Data3simple
    ): Int {
        val db = writableDatabase
        return if (read(oldProduct) != null) {
            val data = ContentValues().apply {
                put(COLUMN_NAME_NAME, newProduct.productName)
                put(COLUMN_NAME_Image, newProduct.image)
                put(COLUMN_NAME_QUANTITY, newProduct.quantity)
                put(COLUMN_NAME_CATID, newProduct.catId)
            }
            Log.d(
                "abc",
                "update ${newProduct.productName} quantity to ${newProduct.quantity} from ${oldProduct.quantity}"
            )

            val selection = "$COLUMN_NAME_NAME LIKE ?"
            val selectionArgs = arrayOf(oldProduct.productName)
            db.update(DATABASE_new_TABLE_NAME, data, selection, selectionArgs)
        } else {
            insert(newProduct)
            -1
        }

    }


}