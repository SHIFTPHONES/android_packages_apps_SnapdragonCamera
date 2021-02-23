package com.android.camera.util;

import android.view.View;

/**
 * AccessibilityUtils provides functions needed in accessibility mode. All the functions
 * in this class are made compatible with gingerbread and later API's
 */
public class AccessibilityUtils {
    public static void makeAnnouncement(View view, CharSequence announcement) {
        if (view == null) {
            return;
        }
        view.announceForAccessibility(announcement);
    }
}
