package com.example.projectfruit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import androidx.fragment.app.FragmentManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.projectfruit.common.Constant
import com.example.projectfruit.dialog.CustomDialogCategory
import com.example.projectfruit.dialog.CustomDialogFruit
import com.example.projectfruit.model.FruitCategoryAndFruits
import com.google.android.material.appbar.MaterialToolbar
import com.example.projectfruit.viewmodel.MainViewModel
import com.google.firebase.database.*
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), FruitCategoryAdapter.FruitCategoryListener {

    private var rcvFruitCategory: RecyclerView? = null
    private var edtSearch: SearchView? = null
    private val listFruitCategory: MutableList<FruitCategoryAndFruits> = mutableListOf()
    private var mAdapter: FruitCategoryAdapter? = null
    private var topAppBar: MaterialToolbar? = null
    private var mPullToRefresh: SwipeRefreshLayout? = null


    private val dialog: LoadingDialogFragment by lazy {
        LoadingDialogFragment()
    }

    private val fManager: FragmentManager by lazy {
        this.supportFragmentManager
    }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        initData()
        initAction()
    }

    private fun initViews() {
        dialog.show(fManager, "")
        rcvFruitCategory = findViewById(R.id.rcv_fruit_category)
        edtSearch = findViewById(R.id.menu_search)
        topAppBar = findViewById(R.id.top_app_bar)
        mPullToRefresh = findViewById(R.id.mRefreshMain)
        mPullToRefresh?.setOnRefreshListener {
            viewModel.getDataFromFirebase()
            mPullToRefresh?.isRefreshing = false
        }
    }

    private fun initData() {
        mAdapter = FruitCategoryAdapter(applicationContext, this)
        rcvFruitCategory?.layoutManager = LinearLayoutManager(applicationContext)
        rcvFruitCategory?.adapter = mAdapter
        viewModel.getMCategory().observe(this, {
            listFruitCategory.clear()
            listFruitCategory.addAll(it)
            mAdapter?.setListFruitCategory(it)
            if (!it.isNullOrEmpty()) {
                dialog.dismiss()
            }
        })
        viewModel.getDataFromFirebase()
    }

    private fun initAction() {
        edtSearch?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(name: String?): Boolean {
                if (!name.isNullOrEmpty()) {
                    filter(name.toString())
                } else {
                    listFruitCategory.forEach {
                        it.fruitCategory.expanded = false
                    }
                    mAdapter?.setListFruitCategory(listFruitCategory)
                }
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

    private fun filter(name: String) {
        val filterList: ArrayList<FruitCategoryAndFruits> = ArrayList()
        for (category in listFruitCategory) {
            category.fruits?.let { listFruit ->
                val fruits: MutableList<Fruit> = arrayListOf()
                listFruit.forEachIndexed { index, fruit ->
                    if (fruit.name?.lowercase(Locale.getDefault())
                            ?.contains(name.lowercase(Locale.getDefault())) == true
                    ) {
                        fruits.add(fruit)
                    }
                    if (index == listFruit.size - 1 && fruits.isNotEmpty()) {
                        val fruitCategory = category.fruitCategory.apply {
                            this.expanded = true
                        }
                        filterList.add(FruitCategoryAndFruits(fruitCategory, fruits))
                    }
                }
            }
        }
        mAdapter?.setListFruitCategory(filterList)
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
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.save_success),
                            Toast.LENGTH_LONG
                        )
                            .show()
                        viewModel.addNewFruitOnFirebase(
                            categoryName, viewModel.getTheLastFruitItem()
                        )
                    }
                }
            }
        val dialog = CustomDialogFruit(this, fruitListener, null)
        dialog.show()
    }

    private fun openDialogAddCategory() {
        val categoryListener: CustomDialogCategory.DialogCategoryListener =
            object : CustomDialogCategory.DialogCategoryListener {

                override fun nameEntered(name: String) {
                    viewModel.insertCategory(FruitCategory(nameCategory = name))
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.save_success),
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.addNewCategory(name)
                }

            }
        val dialog = CustomDialogCategory(this, categoryListener)
        dialog.show()
    }

    override fun onClickListener(id: Int?, name: String) {
        openDialogAddFruit(id, name)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(triple: Triple<Fruit, Constant.KeyEvent, FruitCategory>) {
        when (triple.second) {
            Constant.KeyEvent.UPDATE_FRUIT -> {
                openDialogEditFruit(triple.first, triple.third)
            }
            Constant.KeyEvent.DELETE_FRUIT -> {
                viewModel.deleteFruit(triple.first, triple.third)
            }
        }
    }

    private fun openDialogEditFruit(fruit: Fruit, fruitCategory: FruitCategory) {
        val fruitListener: CustomDialogFruit.DialogFruitListener =
            object : CustomDialogFruit.DialogFruitListener {

                override fun nameEntered(name: String, price: Int) {
                    viewModel.updateFruit(name = name, price = price, id = fruit.id)
                    viewModel.updateDataForFirebase(fruitCategory.nameCategory ?: "", fruit)
                    Toast.makeText(this@MainActivity, "Lưu thành công", Toast.LENGTH_LONG).show()
                }
            }
        val dialog = CustomDialogFruit(this, fruitListener, fruit)
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.getMCategory().removeObserver { }
    }
}