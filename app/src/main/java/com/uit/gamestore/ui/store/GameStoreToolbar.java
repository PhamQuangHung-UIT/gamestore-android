package com.uit.gamestore.ui.store;

import android.content.Context;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.uit.gamestore.R;

public class GameStoreToolbar extends Toolbar {

    public GameStoreToolbar(@NonNull Context context) {
        super(context);
        init(context);
    }

    public GameStoreToolbar(@NonNull Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameStoreToolbar(@NonNull Context context, @NonNull AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.toolbar_store, this, true);

        applyBlur();
    }

    private void applyBlur() {
        RenderEffect blurEffect = RenderEffect.createBlurEffect(
                50f,   // radiusX
                50f,   // radiusY
                Shader.TileMode.CLAMP
        );

        // Apply blur to everything behind this view
        //((View)getParent()).setRenderEffect(blurEffect);

        // Optional frosted glass tint
        //setBackgroundColor(Color.argb(150, 255, 255, 255));
    }
}
