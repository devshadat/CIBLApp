package com.devshadat.ciblapp.utilities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.devshadat.ciblapp.R
import com.devshadat.ciblapp.models.Receipt

class AlertManager(activity: FragmentActivity?) {

    private var context: Context? = activity

    @SuppressLint("MissingInflatedId")
    fun showPrintDialog(
        listener: OnPrintListener, receipt: Receipt
    ) {

        val builder = AlertDialog.Builder(context)
        val customLayout: View = LayoutInflater.from(context).inflate(
            R.layout.layout_dialog, null
        )
        builder.setView(customLayout)

        (customLayout.findViewById<View>(R.id.imgPaymentLogo) as ImageView).setImageResource(receipt.imgId)
        (customLayout.findViewById<View>(R.id.transferTitle) as TextView).text = receipt.title
        (customLayout.findViewById<View>(R.id.transactionNumber) as TextView).text = receipt.number
        (customLayout.findViewById<View>(R.id.transactionName) as TextView).text = receipt.name
        (customLayout.findViewById<View>(R.id.transactionAmount) as TextView).text = receipt.amount
        (customLayout.findViewById<View>(R.id.transactionNarration) as TextView).text =
            receipt.narration
        (customLayout.findViewById<View>(R.id.transationLocation) as TextView).text =
            receipt.location
        (customLayout.findViewById<View>(R.id.transactionTime) as TextView).text = receipt.time
        val alertDialog = builder.create()
        customLayout.findViewById<View>(R.id.cancel)
            .setOnClickListener { alertDialog.dismiss() }

        customLayout.findViewById<Button>(R.id.download).setOnClickListener {
            listener.didPrint(true)
            alertDialog.dismiss()
        }

        customLayout.findViewById<Button>(R.id.share).setOnClickListener {
            listener.didShare(true)
            alertDialog.dismiss()
        }

        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        alertDialog.show()
    }


    interface OnPrintListener {
        fun didPrint(boolean: Boolean)

        fun didShare(boolean: Boolean)
    }

}