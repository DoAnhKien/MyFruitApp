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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.FragmentManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.projectfruit.common.Constant
import com.example.projectfruit.model.FruitCategoryAndFruits
import com.google.android.material.appbar.MaterialToolbar
import com.example.projectfruit.viewmodel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import android.text.TextUtils
import android.text.Editable
import android.text.Html
import android.view.View
import android.view.WindowManager
import com.example.projectfruit.customer.CustomTextWatcher

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
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.purple_500)
        dialog.show(fManager, "")
        rcvFruitCategory = findViewById(R.id.rcv_fruit_category)
        edtSearch = findViewById(R.id.menu_search)
        edtSearch?.queryHint = Html.fromHtml("<font color = #ffffff>" + resources.getString(R.string.search_text) + "</font>");
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
                R.id.mn_add_category -> {
                    openDialogAddCategory()
                    true
                }
                R.id.menu_search -> {

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
        var name = ""
        var price = 0
        val build = MaterialAlertDialogBuilder(this)
        build.setTitle(resources.getString(R.string.input_product_info))
        build.setView(R.layout.layout_custom_dialog)
        build.setNegativeButton(resources.getString(R.string.submit)) { dialog, _ ->
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
                dialog.dismiss()
            }
        }
        build.setPositiveButton(resources.getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        build.setView(R.layout.layout_custom_dialog)
        val dialog: AlertDialog = build.create()
        dialog.show()

        val edtName = (dialog as? AlertDialog)?.findViewById<TextInputEditText>(R.id.edt_name)
        val edtPrice = (dialog as? AlertDialog)?.findViewById<TextInputEditText>(R.id.edt_price)
        edtPrice?.visibility = View.VISIBLE
        var isPrice = false
        var isName = false
        (dialog).getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = false

        edtName?.addTextChangedListener(object : CustomTextWatcher(){
            override fun afterTextChanged(p0: Editable?) {
                if (TextUtils.isEmpty(p0)){
                    isName = false
                } else {
                    isName = true
                    name = p0.toString()
                }
                (dialog).getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = isName && isPrice
            }
        })

        edtPrice?.addTextChangedListener(object : CustomTextWatcher(){
            override fun afterTextChanged(p0: Editable?) {
                if (TextUtils.isEmpty(p0)){
                    isPrice = false
                } else {
                    isPrice = true
                    price = p0.toString().toInt()
                }
                (dialog).getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = isName && isPrice
            }
        })
    }

    private fun openDialogAddCategory() {
        var name = ""
        val build = MaterialAlertDialogBuilder(this)
        build.setTitle(resources.getString(R.string.add_category_info))
        build.setView(R.layout.layout_custom_dialog)
        build.setNegativeButton(resources.getString(R.string.submit)) { dialog, _ ->
            viewModel.insertCategory(FruitCategory(nameCategory = name))
            Toast.makeText(
                this@MainActivity,
                getString(R.string.save_success),
                Toast.LENGTH_LONG
            ).show()
            viewModel.addNewCategory(name)
            dialog.dismiss()
        }
        build.setPositiveButton(resources.getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        build.setView(R.layout.layout_custom_dialog)
        val dialog: AlertDialog = build.create()
        dialog.show()

        val edtName = (dialog as? AlertDialog)?.findViewById<TextInputEditText>(R.id.edt_name)
        edtName?.hint = getString(R.string.hint_text_name_category)
        (dialog).getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = false

        edtName?.addTextChangedListener(object : CustomTextWatcher(){
            override fun afterTextChanged(p0: Editable?) {
                if (TextUtils.isEmpty(p0)){
                    (dialog).getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = false
                } else {
                    (dialog).getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = true
                    name = p0.toString()
                }
            }
        })
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
        val build = MaterialAlertDialogBuilder(this)
        build.setTitle(resources.getString(R.string.input_product_info))
        build.setView(R.layout.layout_custom_dialog)
        build.setNegativeButton(resources.getString(R.string.submit)) { dialog, _ ->
            viewModel.updateFruit(
                name = fruit.name,
                price = fruit.price,
                id = fruit.id
            )
            viewModel.updateDataForFirebase(fruitCategory.nameCategory ?: "", fruit)
            Toast.makeText(
                this, getString(R.string.save_success),
                Toast.LENGTH_LONG
            ).show()
            dialog.dismiss()
        }
        build.setPositiveButton(resources.getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        build.setView(R.layout.layout_custom_dialog)
        val dialog: AlertDialog = build.create()
        dialog.show()

        val edtName = (dialog as? AlertDialog)?.findViewById<TextInputEditText>(R.id.edt_name)
        val edtPrice = (dialog as? AlertDialog)?.findViewById<TextInputEditText>(R.id.edt_price)
        edtPrice?.visibility = View.VISIBLE
        var isPrice = true
        var isName = true
        edtName?.setText(fruit.name)
        edtPrice?.setText(fruit.price.toString())
        (dialog).getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = true

        edtName?.addTextChangedListener(object : CustomTextWatcher(){
            override fun afterTextChanged(p0: Editable?) {
                if (TextUtils.isEmpty(p0)){
                    isName = false
                } else {
                    isName = true
                    fruit.name = p0.toString()
                }
                (dialog).getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = isName && isPrice
            }
        })

        edtPrice?.addTextChangedListener(object : CustomTextWatcher(){
            override fun afterTextChanged(p0: Editable?) {
                if (TextUtils.isEmpty(p0)){
                    isPrice = false
                } else {
                    isPrice = true
                    fruit.price = p0.toString().toInt()
                }
                (dialog).getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = isName && isPrice
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.getMCategory().removeObserver { }
    }
}