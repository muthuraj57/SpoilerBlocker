package spoiler.blocker.apps

import android.view.WindowManager
import spoiler.blocker.OverlayView

/**
 * Created by Muthuraj on 26/04/22.
 */
enum class Blockers {
    Reddit {
        override fun create(windowManager: WindowManager, overlayView: OverlayView): RedditBlocker {
            return RedditBlocker(windowManager, overlayView)
        }
    },
    YouTube {
        override fun create(
            windowManager: WindowManager,
            overlayView: OverlayView
        ): YouTubeBlocker {
            return YouTubeBlocker(windowManager, overlayView)
        }
    },
    WhatsApp {
        override fun create(
            windowManager: WindowManager,
            overlayView: OverlayView
        ): WhatsAppBlocker {
            return WhatsAppBlocker(windowManager, overlayView)
        }
    },
    Facebook() {
        override fun create(
            windowManager: WindowManager,
            overlayView: OverlayView
        ): FacebookBlocker {
            return FacebookBlocker(windowManager, overlayView)
        }
    };

    abstract fun create(windowManager: WindowManager, overlayView: OverlayView): Blocker
}