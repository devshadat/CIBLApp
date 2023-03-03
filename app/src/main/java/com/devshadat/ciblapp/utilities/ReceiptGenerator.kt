package com.devshadat.ciblapp.utilities

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import com.devshadat.ciblapp.R
import com.devshadat.ciblapp.models.Receipt
import java.io.File
import java.io.FileOutputStream
import java.security.AccessController.getContext

class ReceiptGenerator {

    private fun createBitmapFromView(
        context: Context, view: View, pdfDetails: Receipt, activity: Activity
    ): Bitmap {
        val binding = com.devshadat.ciblapp.databinding.LayoutReceiptBinding.bind(view)
        //binding.lifecycleOwner = binding.root.findViewTreeLifecycleOwner()    --   To be used when working with lifecycle components
        binding.pdfDetails = pdfDetails
        binding.executePendingBindings()
        return createBitmap(context, binding.root, activity)
    }

    private fun createBitmap(
        context: Context,
        view: View,
        activity: Activity,
    ): Bitmap {
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display?.getRealMetrics(displayMetrics)
            displayMetrics.densityDpi
        } else {
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        }
        view.measure(
            View.MeasureSpec.makeMeasureSpec(
                displayMetrics.widthPixels, View.MeasureSpec.EXACTLY
            ), View.MeasureSpec.makeMeasureSpec(
                displayMetrics.heightPixels, View.MeasureSpec.EXACTLY
            )
        )
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return Bitmap.createScaledBitmap(bitmap, 595, 842, true)
    }

    private fun convertBitmapToPdf(bitmap: Bitmap, context: Context, code: Int) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        page.canvas.drawBitmap(bitmap, 0F, 0F, null)
        pdfDocument.finishPage(page)
        val filePath = File(context.getExternalFilesDir(null), "receipt.pdf")
        pdfDocument.writeTo(FileOutputStream(filePath))
        pdfDocument.close()

        when (code) {
            101 -> renderPdf(context, filePath)
            102 -> sharePDF(context, filePath)
        }
    }

    fun createPdf(
        context: Context, pdfDetails: Receipt, activity: Activity, code: Int
    ) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.layout_receipt, null)

        val bitmap = createBitmapFromView(context, view, pdfDetails, activity)
        convertBitmapToPdf(bitmap, activity, code)
    }


    private fun renderPdf(context: Context, filePath: File) {
        val uri = FileProvider.getUriForFile(
            context, context.applicationContext.packageName + ".provider", filePath
        )
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(uri, "application/pdf")

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {

        }
    }

    private fun sharePDF(context: Context, filePath: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context.applicationContext,
            context.applicationContext.packageName + ".provider",
            filePath
        )

        val intent: Intent = ShareCompat.IntentBuilder.from(context as Activity)
            .setType("application/pdf")
            .setStream(uri)
            .setChooserTitle("Choose bar")
            .createChooserIntent()
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        context.startActivity(intent)
    }
}