package com.example.demofruit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demofruit.adapter.FruitCategoryAdapter
import com.example.demofruit.model.Fruit
import com.example.demofruit.model.FruitCategory
import android.graphics.ColorSpace.Model
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private var rcvFruitCategory: RecyclerView? = null
    private var edtSearch: EditText? = null
    private val listFruitCategory: ArrayList<FruitCategory> = ArrayList()
    private var mAdapter: FruitCategoryAdapter = FruitCategoryAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rcvFruitCategory = findViewById(R.id.rcv_fruit_category)
        edtSearch = findViewById(R.id.edt_search)
        initData()
        initAction()
    }

    private fun initData() {
        val listFruit1: ArrayList<Fruit> = ArrayList()
        listFruit1.add(Fruit("Banana"))
        listFruit1.add(Fruit("Orange"))
        listFruit1.add(Fruit("Apple"))

        listFruitCategory.add(FruitCategory("Frozen Fruits", listFruit1))

        val listFruit2: ArrayList<Fruit> = ArrayList()
        listFruit2.add(Fruit("Grape"))
        listFruit2.add(Fruit("Strawberry"))
        listFruit2.add(Fruit("Cherry"))
        listFruitCategory.add(FruitCategory("Nice Fruits", listFruit2))

        rcvFruitCategory?.layoutManager = LinearLayoutManager(applicationContext)
        mAdapter.setContext(applicationContext)
        mAdapter.setListFruitCategory(listFruitCategory)
        rcvFruitCategory?.adapter = mAdapter
    }

    private fun initAction() {
        edtSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //suppress
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //suppress
            }

            override fun afterTextChanged(text: Editable?) {
                filter(text.toString())
            }

        })
    }

    private fun filter(text: String) {
        val filterList: ArrayList<FruitCategory> = ArrayList()
        for (item in listFruitCategory) {
            if (item.nameCategory.lowercase(Locale.getDefault())
                    .contains(text.lowercase(Locale.getDefault()))
            ) {
                filterList.add(item)
            }
        }
        mAdapter.filterList(filterList)
    }


}