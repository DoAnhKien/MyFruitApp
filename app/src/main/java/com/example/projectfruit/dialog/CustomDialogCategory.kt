package com.example.projectfruit.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.projectfruit.R

class CustomDialogCategory(
    context: Context,
    private val dialogListener: DialogCategoryListener
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
        val tvTitle: TextView = findViewById(R.id.tv_title)
        val edtPrice: EditText = findViewById(R.id.edt_price)
        val btnCancel = findViewById<Button>(R.id.btn_cancel)
        val btnSubmit = findViewById<Button>(R.id.btn_submit)

        edtPrice.visibility = View.GONE
        tvTitle.text = "Nhập tên danh  mục:"
        edtName.hint = "Tên danh  mục"

        btnSubmit.setOnClickListener {
            val name: String = edtName.text.toString()

            if (name.isEmpty()) {
                Toast.makeText(context, "Vui lòng nhập lại!", Toast.LENGTH_LONG).show()
            } else {
                dialogListener.nameEntered(name)
                dismiss()
            }
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    interface DialogCategoryListener {
        fun nameEntered(name: String)
    }
}