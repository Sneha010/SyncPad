package com.nearby.syncpad.util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewGothamBook extends TextView {
	private static Typeface defaultTypeface;

	public TextViewGothamBook(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		// TODO Auto-generated constructor stub
	}

	public TextViewGothamBook(Context context) {
		super(context);
		init();
		// TODO Auto-generated constructor stub
	}

	public TextViewGothamBook(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
		// TODO Auto-generated constructor stub
	}

	private void init() {
		if (!isInEditMode()) {
			setTypeface(getDefaultTypeface());
		}
	}

	private Typeface getDefaultTypeface() {
		if (defaultTypeface == null)
			defaultTypeface =FontCache.getTypeface(getContext(), "fonts/gothambook.ttf");
		return defaultTypeface;
	}
}
