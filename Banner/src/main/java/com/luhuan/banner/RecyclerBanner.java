package com.luhuan.banner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PointF;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by 鲁欢 on 2017/5/13 0013.
 * Banner由两个recyclerview组成
 * 1 图片  2 指示器(LinearLayout+RecyclerView)
 */

public class RecyclerBanner<T> extends FrameLayout {
    private static final String TAG = "RecyclerBanner";

    //item点击事件监听
    private OnBannerItemClickListener<T> onBannerItemClickListener;

    public RecyclerBanner<T> setOnBannerItemClickListener(OnBannerItemClickListener<T> onBannerItemClickListener) {
        this.onBannerItemClickListener = onBannerItemClickListener;
        return this;
    }

    private RecyclerView recyclerView;
    private BannerAdapter adapter;
    private DotAdapter dotAdapter;
    private Disposable autoDisposable;

    //当前位置
    int currentPosition = 0;
    //指示器dot数量
    int dotCount;

    /**
     * 提供给开发者自己定义dot样式的权力
     * 不设置的情况下默认给出两个默认值
     */
    @DrawableRes
    Integer lightDot;
    @DrawableRes
    Integer normalDot;

    //指示器条背景 仅限颜色资源
    @ColorRes
    private Integer dotParentbackgroud;

    //指示器条透明度
    private Float dotParentAlpha;

    //自动轮播间隔时间 默认2000毫秒
    int interval;

    //指示器左右上下margin
    private int left, right, top, bottom;

    //指示器 dot大小
    private int dotSize;

    boolean isAuto = false; //默认不开启轮播图

    /**
     * @param dotParentbackgroud 指示器条的背景色
     */
    public RecyclerBanner<T> setDotLinebackgroud(@ColorRes Integer dotParentbackgroud) {
        this.dotParentbackgroud = dotParentbackgroud;
        return this;
    }

    /**
     * @param dotSize px  dot 大小
     */
    public RecyclerBanner<T> setDotSize(int dotSize) {
        this.dotSize = dotSize;
        return this;
    }

    /**
     * @param dotParentAlpha 0f-1f  设置指示器背景透明度
     */
    public RecyclerBanner<T> setDotParentAlpha(Float dotParentAlpha) {
        this.dotParentAlpha = dotParentAlpha;
        return this;
    }

    /**
     * @param lightDot 样式资源 设置点亮dot样式
     */
    public RecyclerBanner<T> setLightDot(@NonNull Integer lightDot) {
        this.lightDot = lightDot;
        return this;
    }

    /**
     * @param interval_time 时间 毫秒 轮播间隔时间
     */
    public RecyclerBanner<T> setInterval(int interval_time) {
        interval = interval_time;
        return this;
    }

    /**
     * @param normalDot 样式资源 设置普通dot样式
     */
    public RecyclerBanner<T> setNormalDot(@NonNull Integer normalDot) {
        this.normalDot = normalDot;
        return this;
    }

    /**
     * dot与dot之间上下左右的间距Margin
     */
    public RecyclerBanner<T> setDotMargin(@Px int left_right, @Px int top_bottom) {
        left = left_right;
        right = left_right;
        top = top_bottom;
        bottom = top_bottom;
        return this;
    }

    public RecyclerBanner(@NonNull Context context) {
        this(context, null);
    }

    public RecyclerBanner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getArrs(context, attrs);
        init();
    }

    public RecyclerBanner(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getArrs(context, attrs);
        init();
    }

    private void init() {
        initBanner();
        initDots();
    }

    /**
     * 自定义控件加属性
     */
    private void getArrs(@NonNull Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RecyclerBanner);
        left = typedArray.getDimensionPixelSize(R.styleable.RecyclerBanner_dot_marginleft, 5);
        right = typedArray.getDimensionPixelSize(R.styleable.RecyclerBanner_dot_marginright, 5);
        top = typedArray.getDimensionPixelSize(R.styleable.RecyclerBanner_dot_margintop, 15);
        bottom = typedArray.getDimensionPixelSize(R.styleable.RecyclerBanner_dot_marginbottom, 15);
        dotSize = typedArray.getDimensionPixelSize(R.styleable.RecyclerBanner_dot_size, 12);
        dotParentbackgroud = typedArray.getResourceId(R.styleable.RecyclerBanner_dot_parentbackground, android.R.color.black);
        dotParentAlpha = typedArray.getFloat(R.styleable.RecyclerBanner_dot_parentalpha, 0.5f);
        interval = typedArray.getInteger(R.styleable.RecyclerBanner_interval, 2000);
        lightDot = typedArray.getResourceId(R.styleable.RecyclerBanner_light_dot_src, R.drawable.dot_light);
        normalDot = typedArray.getResourceId(R.styleable.RecyclerBanner_normal_dot_src, R.drawable.dot_normal);
        typedArray.recycle();
    }

    /**
     * 初始化轮播recyclerview
     */
    private void initBanner() {
        recyclerView = new RecyclerView(getContext());
        ScrollSpeedLinearLayoutManger linearLayoutManager = new ScrollSpeedLinearLayoutManger(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setBackgroundColor(Color.WHITE);
        List<T> images = new ArrayList<>();
        adapter = new BannerAdapter(images);
        recyclerView.setAdapter(adapter);
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recyclerView);
        LayoutParams recyclerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                dotAdapter.setIndex(currentPosition);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) stopAuto();/* 手放上去开始滑动 */
                else if (newState == RecyclerView.SCROLL_STATE_IDLE) startAuto();/* 滑动停止 */
            }
        });
        addView(recyclerView, recyclerParams);
    }

    /**
     * 初始化指示器recyclerview
     */
    private void initDots() {
        RecyclerView dotRecycler = new RecyclerView(getContext());
        dotRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        LayoutParams dotParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        dotParams.gravity = Gravity.CENTER;
        dotAdapter = new DotAdapter();
        dotRecycler.setAdapter(dotAdapter);
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.addView(dotRecycler, dotParams);
        LayoutParams linearParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        linearParams.gravity = Gravity.BOTTOM;
        linearLayout.setBackgroundColor(getResources().getColor(dotParentbackgroud));
        linearLayout.setAlpha(dotParentAlpha);
        addView(linearLayout, linearParams);
    }

    public RecyclerBanner<T> setImages(List<T> images) {
        adapter.setImgUrls(images);
        dotCount = images.size();
        return this;
    }

    /**
     * 开启轮播图
     */
    public void startAuto() {
        isAuto = true;
        if (autoDisposable != null && !autoDisposable.isDisposed()) {
            autoDisposable.dispose();
        }
        startPlaying();
    }

    /**
     * 关闭轮播
     */
    public void stopAuto() {
        isAuto = false;
        if (autoDisposable != null && !autoDisposable.isDisposed()) {
            autoDisposable.dispose();
        }
    }

    /**
     * 开启自动轮播
     */
    private void startPlaying() {
        if (isAuto)
            autoDisposable = Observable.interval(interval, interval, TimeUnit.MILLISECONDS)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(@io.reactivex.annotations.NonNull Long aLong) throws Exception {
                            if ((++currentPosition) >= adapter.getItemCount()) {
                                currentPosition = 0;
                                recyclerView.scrollToPosition(currentPosition);
                            } else {
                                recyclerView.smoothScrollToPosition(currentPosition);
                            }
                            dotAdapter.setIndex(currentPosition);
                        }
                    });
    }

    private class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerHolder> {
        private List<T> imgUrls;

        BannerAdapter(List<T> imgUrls) {
            this.imgUrls = imgUrls;
        }

        void setImgUrls(List<T> imgUrls) {
            this.imgUrls = imgUrls;
            notifyDataSetChanged();
        }

        @Override
        public BannerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(parent.getContext());
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(layoutParams);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return new BannerHolder(imageView);
        }

        @Override
        public void onBindViewHolder(BannerHolder holder, @SuppressLint("RecyclerView") final int position) {
            if (getContext()!=null){
                Glide.with(getContext()).load(imgUrls.get(position)).into(holder.imgview);
            }
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onBannerItemClickListener != null)
                        onBannerItemClickListener.onBannerItemClick(position, imgUrls.get(position));
                }
            });
        }

        @Override
        public int getItemCount() {
            return imgUrls == null ? 0 : imgUrls.size();
        }

        class BannerHolder extends RecyclerView.ViewHolder {
            ImageView imgview;

            BannerHolder(View itemView) {
                super(itemView);
                imgview = (ImageView) itemView;
            }
        }
    }

    private class DotAdapter extends RecyclerView.Adapter<DotAdapter.DotHolder> {
        int index;

        @Override
        public DotHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View dotView = new View(getContext());
            LayoutParams layoutParams = new LayoutParams(dotSize, dotSize);
            layoutParams.setMargins(left, top, right, bottom);
            dotView.setLayoutParams(layoutParams);
            return new DotHolder(dotView);
        }

        //获得位置点亮对应的指示器
        void setIndex(int position) {
            index = position;
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(DotHolder holder, int position) {
            holder.itemView.setBackgroundResource(position == index ? lightDot : normalDot);
        }

        @Override
        public int getItemCount() {
            return dotCount;
        }

        class DotHolder extends RecyclerView.ViewHolder {
            DotHolder(View itemView) {
                super(itemView);
            }
        }
    }

    /**
     * 控制滑动速度的LinearLayoutManager
     */
    private class ScrollSpeedLinearLayoutManger extends LinearLayoutManager {
        private float MILLISECONDS_PER_INCH = 1f;

        ScrollSpeedLinearLayoutManger(Context context) {
            super(context, HORIZONTAL, false);
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
            LinearSmoothScroller linearSmoothScroller =
                    new LinearSmoothScroller(recyclerView.getContext()) {
                        @Override
                        public PointF computeScrollVectorForPosition(int targetPosition) {
                            return ScrollSpeedLinearLayoutManger.this
                                    .computeScrollVectorForPosition(targetPosition);
                        }

                        //This returns the milliseconds it takes to
                        //scroll one pixel.
                        @Override
                        protected float calculateSpeedPerPixel
                        (DisplayMetrics displayMetrics) {
                            return MILLISECONDS_PER_INCH / displayMetrics.density;
                            //返回滑动一个pixel需要多少毫秒
                        }

                    };
            linearSmoothScroller.setTargetPosition(position);
            startSmoothScroll(linearSmoothScroller);
        }
    }

    public interface OnBannerItemClickListener<T> {
        void onBannerItemClick(int itemPosition, T t);
    }
}
