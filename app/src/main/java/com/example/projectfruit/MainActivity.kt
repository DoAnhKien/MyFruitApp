package com.example.projectfruit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfruit.adapter.FruitCategoryAdapter
import com.example.projectfruit.model.Fruit
import com.example.projectfruit.model.FruitCategory
import java.util.*
import kotlin.collections.ArrayList
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import com.example.projectfruit.dao.FruitDao
import com.example.projectfruit.dialog.CustomDialogCategory
import com.example.projectfruit.dialog.CustomDialogFruit
import com.google.android.material.appbar.MaterialToolbar
import com.example.projectfruit.database.FruitDatabase
import com.example.projectfruit.viewmodel.MainViewModel
import com.google.firebase.database.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), FruitCategoryAdapter.FruitCategoryListener {

    private var rcvFruitCategory: RecyclerView? = null
    private var edtSearch: SearchView? = null
    private val listFruitCategory: ArrayList<FruitCategory> = ArrayList()
    private var mAdapter: FruitCategoryAdapter? = null
    private var topAppBar: MaterialToolbar? = null


    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rcvFruitCategory = findViewById(R.id.rcv_fruit_category)
        edtSearch = findViewById(R.id.menu_search)
        topAppBar = findViewById(R.id.top_app_bar)
        initData()
        initAction()
    }

    private fun initData() {
        mAdapter = FruitCategoryAdapter(applicationContext, this)
        rcvFruitCategory?.layoutManager = LinearLayoutManager(applicationContext)
        rcvFruitCategory?.adapter = mAdapter
        viewModel.insertCategory(listFruitCategory)
        viewModel.getMCategory().observe(this, {
            mAdapter?.setListFruitCategory(it)
        })
        viewModel.getDataFromFirebase()
    }

    private fun initAction() {
        edtSearch?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                filter(p0.toString())
                return true
            }

        })

        topAppBar?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_more -> {
                    openDialogAddCategory()
                    true
                }
                else -> false
            }
        }
    }

    private fun filter(text: String) {
        val filterList: ArrayList<FruitCategory> = ArrayList()
        for (item in listFruitCategory) {
            if (item.nameCategory?.lowercase(Locale.getDefault())
                    ?.contains(text.lowercase(Locale.getDefault())) == true
            ) {
                filterList.add(item)
            }
        }
        mAdapter?.filterList(filterList)
    }

    private fun openDialogAddFruit(id: Int?, categoryName: String) {
        val fruitListener: CustomDialogFruit.DialogFruitListener =
            object : CustomDialogFruit.DialogFruitListener {

                override fun nameEntered(name: String, price: Int) {
                    id?.let {
                        viewModel.insertFruit(
                            Fruit(
                                name = name,
                                price = price,
                                idFruitCategory = it
                            )
                        )
                        Toast.makeText(this@MainActivity, "Lưu thành công", Toast.LENGTH_LONG)
                            .show()
                        viewModel.addNewFruitOnFirebase(
                            name, fruit = Fruit(
                                name = categoryName,
                                price = price,
                                idFruitCategory = it
                            )
                        )
                        viewModel.getDataFromFirebase()
                    }
                }
            }
        val dialog = CustomDialogFruit(this, fruitListener)
        dialog.show()
    }

    private fun openDialogAddCategory() {
        val categoryListener: CustomDialogCategory.DialogCategoryListener =
            object : CustomDialogCategory.DialogCategoryListener {

                override fun nameEntered(name: String) {
                    Toast.makeText(this@MainActivity, "Tên danh : $name", Toast.LENGTH_LONG).show()
                    viewModel.addNewCategory(name)
                    viewModel.getDataFromFirebase()
                }

            }
        val dialog = CustomDialogCategory(this, categoryListener)
        dialog.show()
    }

    override fun onClickListener(id: Int?, name: String) {
        openDialogAddFruit(id, name)
    }


}