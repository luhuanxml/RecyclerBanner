package com.luhuan.banner;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class DesignImageView extends CardView {

    private ImageView imageView;

    public DesignImageView(@NonNull Context context) {
        this(context,null);
    }

    public void setImageResource(@DrawableRes int imageUrl){
        Glide.with(getContext()).load(imageUrl).into(imageView);
    }

    public DesignImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        addView(imageView);
    }
}
