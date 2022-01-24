package com.example.projectfruit.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.projectfruit.R
import com.example.projectfruit.model.Fruit

class CustomDialogFruit(
    context: Context,
    private val dialogFruitListener: DialogFruitListener,
    private val fruit: Fruit?
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_custom_dialog)
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val edtName: EditText = findViewById(R.id.edt_name)
        val edtPrice: EditText = findViewById(R.id.edt_price)
        val btnCancel = findViewById<Button>(R.id.btn_cancel)
        val btnSubmit = findViewById<Button>(R.id.btn_submit)

        btnSubmit.setOnClickListener {
            if (edtName.text.toString().isNullOrEmpty() || edtPrice.text.toString()
                    .isNullOrEmpty()
            ) {
                Toast.makeText(context, "Vui lòng nhập lại!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val name: String = edtName.text.toString()
            val price: Int = edtPrice.text.toString().toInt()
            dialogFruitListener.nameEntered(name, price)
            dismiss()
        }

        fruit?.let {
            edtName.setText(it.name)
            edtPrice.setText(it.price.toString())
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    interface DialogFruitListener {
        fun nameEntered(name: String, price: Int)
    }
}