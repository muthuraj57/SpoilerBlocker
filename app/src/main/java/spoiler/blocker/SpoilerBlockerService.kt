/* $Id$ */
package spoiler.blocker

import android.accessibilityservice.AccessibilityService
import android.graphics.PixelFormat
import android.graphics.Rect
import android.view.WindowInsets
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.content.getSystemService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import spoiler.blocker.util.children
import spoiler.blocker.util.log
import spoiler.blocker.util.logE
import spoiler.blocker.util.logI
import spoiler.blocker.util.makeGone


/**
 * Created by Muthuraj on 03/03/22.
 */
class SpoilerBlockerService : AccessibilityService() {

    private var scope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        logE { "onCreate() called" }
        scope.cancel()
        scope = CoroutineScope(SupervisorJob())
        keywordsDataStore.data
            .onEach {
                blockList = it.keywords
            }.launchIn(scope)
        super.onCreate()
    }

    override fun onDestroy() {
        logE { "onDestroy() called" }
        scope.cancel()
        super.onDestroy()
    }

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
        windowManager.addView(view, windowManagerLayoutParams)
        view
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        logI { "onAccessibilityEvent() called with: event = [$event]" }
        if (event.className == "android.widget.ProgressBar") {
            //When video/image loads, progress bar will be updated very frequently and we don't want
            //to update overlay view that frequently. Just ignore progress bar events.
            return
        }

        if (event.packageName == "com.android.systemui") {
            //Whenever Status bar updates and heads up notifications are updated, accessibility event
            //from this package will be delivered. We don't want to act on these as this is not from
            //full blown app.
            return
        }

        //TODO split screen app?

        overlayView.clearAllRect()

        if ("reddit.news" in event.packageName) {
            //Reddit app.
            checkForReddit(rootInActiveWindow)
            return
        }
    }

    private fun checkForReddit(nodeInfo: AccessibilityNodeInfo?) {
        nodeInfo ?: return

        val dy =
            windowManager.currentWindowMetrics.windowInsets.getInsets(WindowInsets.Type.statusBars()).top
        val rect = Rect()
        var blockedText: String? = null
        nodeInfo.children.forEachIndexed { index, childNode ->
            log { "child id: ${childNode.viewIdResourceName}, text: ${childNode.text}" }
            when (childNode.viewIdResourceName) {
                "reddit.news:id/title" -> {
                    if (blockedText == null) {
                        blockedText = childNode.getBlockedTextIfFound()
                    }
                    if (blockedText != null) {
                        logE { "Blocking title with text: \"${childNode.text}\" at index: $index" }

                        childNode.getBoundsInScreen(rect)
                        rect.offset(0, -dy)
                        overlayView.addRect(rect, blockedText)
                    } else {
                        log { "Found title with text: \"${childNode.text}\" at index: $index" }
                    }

                    nodeInfo.children.forEachIndexed { childIndex, sibling ->
                        if (blockedText != null) {
                            if (childIndex > index && sibling.viewIdResourceName == "reddit.news:id/imagePreview") {
                                sibling.getBoundsInScreen(rect)
                                logE { "Found imagePreview with text: \"${sibling.text}\" at index: $index for text: \"${childNode.text}\", rect: $rect" }
                                rect.offset(0, -dy)
                                overlayView.addRect(rect, blockedText)
                                logE { "adding overlay view width: ${rect.width()}, height: ${rect.height()}" }
                            }
                        }
                    }
                }
                "reddit.news:id/selftext", "reddit.news:id/imagePreview", "reddit.news:id/video", "reddit.news:id/linkFlair" -> {
                    if (blockedText == null) {
                        blockedText = childNode.getBlockedTextIfFound()
                    }
                    if (blockedText != null) {
                        logE { "Blocking title with self text: \"${childNode.text}\" at index: $index" }
                        childNode.getBoundsInScreen(rect)
                        rect.offset(0, -dy)
                        overlayView.addRect(rect, blockedText)
                    } else {
                        log { "Found title with self text: \"${childNode.text}\" at index: $index" }
                    }
                }
                else -> {
                    checkForReddit(childNode)
                }
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

    companion object {
        private var blockList = emptyList<String>()

        private fun AccessibilityNodeInfo.getBlockedTextIfFound(): String? {
            val text = text ?: return null
            val result = blockList.find { text.contains(it, ignoreCase = true) }
            if (result != null) {
                logE { "shouldBlock() called true $text" }
            } else {
                log { "shouldBlock() called false" }
            }
            return result
//            return blockList.any { it in text }
        }
    }
}