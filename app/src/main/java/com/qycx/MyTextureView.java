package com.qycx;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

public class MyTextureView extends TextureView {
    public MyTextureView(Context context)
    {
        super(context);
    }

    public MyTextureView(Context context, AttributeSet attrs)
    {
        super(context,attrs,0);
    }

    public MyTextureView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void SetAspectRatio(int width, int height) {

    }
}
