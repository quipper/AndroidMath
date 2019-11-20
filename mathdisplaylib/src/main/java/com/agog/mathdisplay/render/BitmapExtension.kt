package com.agog.mathdisplay.render

import android.graphics.Bitmap
import android.graphics.Color
import android.support.annotation.ColorInt
import java.util.*

/**
 * Got this code from here: https://stackoverflow.com/a/49281542
 * add a margin value to it
 */
fun Bitmap.trim(@ColorInt color: Int = Color.TRANSPARENT, margin: Int = 0): Bitmap {

    var top = height
    var bottom = 0
    var right = width
    var left = 0

    var colored = IntArray(width, { color })
    var buffer = IntArray(width)

    for (y in bottom until top) {
        getPixels(buffer, 0, width, 0, y, width, 1)
        if (!Arrays.equals(colored, buffer)) {
            bottom = y
            break
        }
    }

    for (y in top - 1 downTo bottom) {
        getPixels(buffer, 0, width, 0, y, width, 1)
        if (!Arrays.equals(colored, buffer)) {
            top = y
            break
        }
    }

    val heightRemaining = top - bottom
    colored = IntArray(heightRemaining, { color })
    buffer = IntArray(heightRemaining)

    for (x in left until right) {
        getPixels(buffer, 0, 1, x, bottom, 1, heightRemaining)
        if (!Arrays.equals(colored, buffer)) {
            left = x
            break
        }
    }

    for (x in right - 1 downTo left) {
        getPixels(buffer, 0, 1, x, bottom, 1, heightRemaining)
        if (!Arrays.equals(colored, buffer)) {
            right = x
            break
        }
    }

    // add margin if any, and normalize the result
    if (margin != 0) {
        top += margin
        top = kotlin.math.min(top, height)

        bottom -= margin
        bottom = kotlin.math.max(bottom, 0)

        left -= margin
        left = kotlin.math.max(left, 0)

        right += margin
        right = kotlin.math.min(right, width)
    }

    return Bitmap.createBitmap(this, left, bottom, right - left, top - bottom)
}
