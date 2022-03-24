/* $Id$ */
package spoiler.blocker

import android.accessibilityservice.AccessibilityService
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.content.getSystemService
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import spoiler.blocker.util.children
import spoiler.blocker.util.log
import spoiler.blocker.util.logE
import spoiler.blocker.util.makeGone


/**
 * Created by Muthuraj on 03/03/22.
 */
class SpoilerBlockerService : AccessibilityService() {

    private val windowManager by lazy {
        getSystemService<WindowManager>()!!
    }
    private val windowManagerLayoutParams by lazy {
        WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )
    }
    private val overlayView by lazy {
        val view = OverlayView(this).apply {
            layoutParams = windowManagerLayoutParams
        }
        view.makeGone()
//        val view = FrameLayout(this).apply {
//            background = ColorDrawable(Color.RED)
//        }
        windowManager.addView(view, windowManagerLayoutParams)
        view
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        log { "onAccessibilityEvent() called with: event = [$event]" }

//        logE { "overlay views size before removing: ${overlayViews.size}" }
//        if (overlayView.isAttachedToWindow) {
//            windowManager.removeView(overlayView)
//        }
        overlayView.clearAllRect()
//        overlayViews.forEach {
//            overlayView.removeView(it)
//        }
//        overlayViews.clear()

        if ("reddit.news" in event.packageName) {
            //Reddit app.
            checkForReddit(rootInActiveWindow)
            return
        }
//        mDebugDepth = 0
//        val list = mutableListOf<String>()
//        printAllViews(rootInActiveWindow, list)
//        if (list.any { it.contains("Console") }) {
//            log { "Reddit view detected." }
//        }
    }

//    private val overlayViews = mutableListOf<View>()

    private fun checkForReddit(nodeInfo: AccessibilityNodeInfo?) {
        nodeInfo ?: return

        val dy = windowManager.currentWindowMetrics.windowInsets.getInsets(WindowInsets.Type.statusBars()).top
        val rect = Rect()
        nodeInfo.children.forEachIndexed { index, childNode ->
            if (childNode.viewIdResourceName == "reddit.news:id/title") {
                logE { "Found title with text: \"${childNode.text}\" at index: $index" }

                childNode.getBoundsInScreen(rect)
                rect.offset(0, -dy)
                overlayView.addRect(rect)

                nodeInfo.children.forEachIndexed { childIndex, sibling ->
                    if (childIndex > index && sibling.viewIdResourceName == "reddit.news:id/imagePreview") {
                        sibling.getBoundsInScreen(rect)
                        logE { "Found imagePreview with text: \"${sibling.text}\" at index: $index for text: \"${childNode.text}\", rect: $rect" }
                        rect.offset(0, -dy)
                        overlayView.addRect(rect)
//                        val params = FrameLayout.LayoutParams(rect.width(), rect.height())
//                        val view = View(this).apply {
//                            background = ColorDrawable(Color.RED)
//                            layoutParams = params
//                        }
//                        view.x = rect.left.toFloat()
//                        view.y = rect.top.toFloat()
//                        overlayView.addView(view, rect.width(), rect.height())
//                        if (overlayView.isAttachedToWindow.not()) {
//                            windowManager.addView(overlayView, windowManagerLayoutParams)
//                        }
                        logE { "adding overlay view width: ${rect.width()}, height: ${rect.height()}" }
//                        overlayViews += view

                        /*val windowManagerLayoutParams = WindowManager.LayoutParams().apply {
                            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                            format = PixelFormat.TRANSLUCENT
                            flags = flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            width = rect.width()
                            height = rect.height()
                            x = rect.left
                            y = rect.top
                        }
                        val view = View(this).apply {
                            background = ColorDrawable(Color.RED)
                        }
                        windowManager.addView(view, windowManagerLayoutParams)
                        logE { "adding overlay view count: ${overlayViews.size + 1}" }
                        overlayViews += view*/
                    }
                }
            } else {
                checkForReddit(childNode)
            }
        }
    }

    private var mDebugDepth = 0
    private fun printAllViews(mNodeInfo: AccessibilityNodeInfo?, list: MutableList<String>) {
        if (mNodeInfo == null) return
        var log = ""
        for (i in 0 until mDebugDepth) {
            log += "."
        }
        log += "(" + mNodeInfo.text + " <-- " +
                mNodeInfo.viewIdResourceName + ")"
        list += log
        log { log }
        if (mNodeInfo.childCount < 1) return
        mDebugDepth++
        for (i in 0 until mNodeInfo.childCount) {
            printAllViews(mNodeInfo.getChild(i), list)
        }
        mDebugDepth--
    }

    override fun onInterrupt() {
        log { "onInterrupt() called" }
    }
}