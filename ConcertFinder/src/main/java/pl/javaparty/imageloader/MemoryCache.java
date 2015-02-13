package pl.javaparty.imageloader;

import android.graphics.Bitmap;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MemoryCache {
	private final String TAG = "MemoryCache";
	private Map<String, Bitmap> cache;//Last argument true for LRU ordering
	private long size = 0;//current allocated size
	private long limit = 1000000;//max memory in bytes

	public MemoryCache() {
		cache = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(10, 1.5f, true));
		//uzywa 25% ramu
		setLimit(Runtime.getRuntime().maxMemory() / 4);
	}

	public void setLimit(long new_limit) {
		limit = new_limit;
		//Log.i(TAG, "MemoryCache will use up to "+limit/1024./1024.+"MB");
	}

	public boolean contains(String s) {
		return cache.containsKey(s);
	}

	public Bitmap get(String id) {
		try {
			if (!cache.containsKey(id))
				return null;
			//NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78
			return cache.get(id);
		} catch (NullPointerException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void put(String id, Bitmap bitmap) {
		try {
			if (cache.containsKey(id))
				size -= getSizeInBytes(cache.get(id));
			cache.put(id, bitmap);
			size += getSizeInBytes(bitmap);
			checkSize();
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}

	long getSizeInBytes(Bitmap bitmap) {
		if (bitmap == null)
			return 0;
		return bitmap.getRowBytes() * bitmap.getHeight();
	}

	private void checkSize() {
		//Log.i(TAG, "cache size="+size+" length="+cache.size());
		if (size > limit) {
			Iterator<Entry<String, Bitmap>> iter = cache.entrySet().iterator();//least recently accessed item will be the first one iterated
			while (iter.hasNext() && size <= limit) {
				Entry<String, Bitmap> entry = iter.next();
				size -= getSizeInBytes(entry.getValue());
				iter.remove();
			}
			//Log.i(TAG, "Clean cache. New size " + cache.size());
		}
	}

	public void clear() {
		try {
			//NullPointerException sometimes happen here http://code.google.com/p/osmdroid/issues/detail?id=78
			cache.clear();
			size = 0;
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
	}
}
