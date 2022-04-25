/* $Id$ */
package spoiler.blocker

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.view.View
import androidx.core.graphics.toRectF
import spoiler.blocker.util.log
import spoiler.blocker.util.logE
import spoiler.blocker.util.makeGone
import spoiler.blocker.util.makeVisible
import java.util.*

/**
 * Created by Muthuraj on 04/03/22.
 */
class OverlayView(context: Context) : View(context) {

    private data class TextData(val text: String, val x: Float, val y: Float)

    //To draw the text that caused the element to be blocked in that element itself as overlay.
    private val textDataList = mutableListOf<TextData>()

    fun addRect(rect: Rect, text: String?) {
        logE { "addRect() called with: rect = [$rect], text = [$text]" }
        makeVisible()
        path.addRect(rect.toRectF(), Path.Direction.CW)
        if (text != null) {
            val finalText = text.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.ROOT)
                else it.toString()
            }
            val textWidth = textPaint.measureText(finalText)
            textDataList.add(
                TextData(
                    text.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.ROOT
                        ) else it.toString()
                    },
                    x = rect.centerX() - (textWidth / 2f),
                    y = rect.centerY().toFloat()
                )
            )
        }
        invalidate()
    }

    fun clearAllRect() {
        logE { "clearAllRect() called" }
        path.reset()
        textDataList.clear()
        makeGone()
        invalidate()
    }

    private val paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.RED
            style = Paint.Style.FILL
        }
    }

    private val textPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            textSize = Resources.getSystem().displayMetrics.scaledDensity * 12
        }
    }

    private val path = Path()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawPath(path, paint)
        textDataList.forEach {
            canvas.drawText(it.text, it.x, it.y, textPaint)
        }
    }
}