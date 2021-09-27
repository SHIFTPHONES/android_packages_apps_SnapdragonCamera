package com.android.camera;

import com.android.camera.util.CameraUtil;

import java.util.Vector;

public class NamedImages {
    /**
     * This class is just a thread-safe queue for name, date holder objects.
     */
    private final Vector<NamedEntity> mQueue;

    public NamedImages() {
        mQueue = new Vector<>();
    }

    public void nameNewImage(long date) {
        NamedEntity r = new NamedEntity();
        r.title = CameraUtil.createJpegName(date);
        r.date = date;
        mQueue.add(r);
    }

    public NamedEntity nameNewImage(long date, boolean refocus) {
        NamedEntity r = new NamedEntity();
        r.title = CameraUtil.createJpegName(date, refocus);
        r.date = date;
        mQueue.add(r);
        return r;
    }

    public NamedEntity getNextNameEntity() {
        synchronized (mQueue) {
            if (!mQueue.isEmpty()) {
                return mQueue.remove(0);
            }
        }
        return null;
    }

    public static class NamedEntity {
        public String title;
        public long date;
    }
}
