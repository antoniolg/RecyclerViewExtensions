package com.antonioleiva.recyclerviewextensions.example;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;
import android.util.LruCache;

public class PaletteManager {
    private LruCache<String, Palette> cache = new LruCache<String, Palette>(100);

    public interface Callback {
        void onPaletteReady(Palette palette);
    }

    public void getPalette(final String key, Bitmap bitmap, final Callback callback) {
        Palette palette = cache.get(key);
        if (palette != null)
            callback.onPaletteReady(palette);
        else
            Palette.generateAsync(bitmap, 24, new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    cache.put(key, palette);
                    callback.onPaletteReady(palette);
                }
            });
    }
}
