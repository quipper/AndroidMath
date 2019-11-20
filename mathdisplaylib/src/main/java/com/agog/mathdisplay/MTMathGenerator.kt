package com.agog.mathdisplay

import android.graphics.Bitmap
import android.graphics.Canvas
import com.agog.mathdisplay.parse.MTLineStyle
import com.agog.mathdisplay.parse.MTMathListBuilder
import com.agog.mathdisplay.render.MTFont
import com.agog.mathdisplay.render.MTTypesetter
import com.agog.mathdisplay.render.trim

object MTMathGenerator {

    private const val defaultWidth = 640
    private const val defaultHeight = 480
    private const val defaultMargin = 20
    private val defaultFont: MTFont? = MTFontManager.latinModernFontWithSize(40f)

    fun createBitmap(
            latexString: String,
            font: MTFont? = defaultFont,
            bitmapWidth: Int = defaultWidth,
            bitmapHeight: Int = defaultHeight,
            bitmapMargin: Int = defaultMargin
    ): Bitmap? {
        val mathList = MTMathListBuilder.buildFromString(latexString)

        if (mathList != null && font != null) {
            val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.translate(0.0f, bitmapHeight.toFloat())
            canvas.scale(1.0f, -1.0f)
            canvas.translate(100.0f, 100.0f) // We shift this to catch any coordinate system errors

            val display = MTTypesetter.createLineForMathList(mathList, font, MTLineStyle.KMTLineStyleText)
            display.draw(canvas)

            return bitmap.trim(margin = bitmapMargin)
        }

        return null
    }
}
