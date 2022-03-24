/* $Id$ */
package spoiler.blocker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.view.View
import androidx.core.graphics.toRectF
import spoiler.blocker.util.makeGone
import spoiler.blocker.util.makeVisible

/**
 * Created by Muthuraj on 04/03/22.
 */
class OverlayView(context: Context) : View(context) {


    fun addRect(rect: Rect) {
        makeVisible()
        path.addRect(rect.toRectF(), Path.Direction.CW)
        invalidate()
    }

    fun clearAllRect() {
        path.reset()
        makeGone()
    }

    private val paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.RED
            style = Paint.Style.FILL
        }
    }

    private val path = Path()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawPath(path, paint)
    }
}