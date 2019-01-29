package com.luhuan.banner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by 鲁欢 on 2017/5/13 0013.
 * Banner由两个recyclerview组成
 * 1 图片  2 指示器(LinearLayout+RecyclerView)
 */

public class RecyclerMarquee extends FrameLayout {
    private static final String TAG = "RecyclerMarquee";

    //item点击事件监听
    private OnMarqueeItemClickListener onMarqueeItemClickListener;

    public RecyclerMarquee setOnMarqueeItemClickListener(OnMarqueeItemClickListener onMarqueeItemClickListener) {
        this.onMarqueeItemClickListener = onMarqueeItemClickListener;
        return this;
    }

    private RecyclerView recyclerView;
    private MarqueeAdapter adapter;
    private Disposable autoDisposable;

    //当前位置
    int currentPosition = 0;
    //自动轮播间隔时间 默认2000毫秒
    int interval=3000;

    int size;

    boolean isAuto = false; //默认不开启轮播图

    /**
     * @param interval_time 时间 毫秒 轮播间隔时间
     */
    public RecyclerMarquee setInterval(int interval_time) {
        interval = interval_time;
        return this;
    }

    public RecyclerMarquee(@NonNull Context context) {
        this(context, null);
    }

    public RecyclerMarquee(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecyclerMarquee(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initMarquee();
    }

    /**
     * 初始化轮播recyclerview
     */
    private void initMarquee() {
        recyclerView = new RecyclerView(getContext());
        ScrollSpeedLinearLayoutManger linearLayoutManager = new ScrollSpeedLinearLayoutManger(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setBackgroundColor(Color.WHITE);
        final List<String> texts = new ArrayList<>();
        adapter = new MarqueeAdapter(texts);
        recyclerView.setAdapter(adapter);
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recyclerView);
        LayoutParams recyclerParams = new LayoutParams(LayoutParams.MATCH_PARENT,100);
        addView(recyclerView, recyclerParams);
    }

    public RecyclerMarquee canToLeft(){
        recyclerView.scrollToPosition(10000*size);
        return this;
    }

    public RecyclerMarquee setTextList(List<String> texts) {
        size=texts.size();
        adapter.setTextList(texts);
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
                                recyclerView.smoothScrollToPosition(currentPosition%adapter.getItemCount());
                            }
                        }
                    });
    }

    private class MarqueeAdapter extends RecyclerView.Adapter<MarqueeAdapter.BannerHolder> {
        private List<String> texts;

        MarqueeAdapter(List<String> texts) {
            this.texts = texts;
        }

        void setTextList(List<String> texts) {
            this.texts = texts;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public BannerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(parent.getContext());
            textView.setGravity(Gravity.CENTER);
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
            textView.setLayoutParams(layoutParams);
            return new BannerHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull BannerHolder holder, @SuppressLint("RecyclerView") final int position) {
            if (getContext()!=null){
                holder.textView.setText(texts.get(position));
            }
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onMarqueeItemClickListener != null){
                        //点击的时候停止滑动，点击完了，滑动继续
                        stopAuto();
                        onMarqueeItemClickListener.onBannerItemClick(position);
                        startAuto();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if (texts==null){
                return 0;
            }else if (texts.size()==0){
                return 0;
            }else if (texts.size()==1){
                return 1;
            }else {
                return texts.size();
            }
        }

        class BannerHolder extends RecyclerView.ViewHolder {
            TextView textView;

            BannerHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView;
            }
        }
    }

    /**
     * 控制滑动速度的LinearLayoutManager
     */
    private class ScrollSpeedLinearLayoutManger extends GridLayoutManager {
        private float MILLISECONDS_PER_INCH = 8f;

        public ScrollSpeedLinearLayoutManger(Context context) {
            super(context, 1);
        }


        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
            LinearSmoothScroller linearSmoothScroller =
                    new LinearSmoothScroller(recyclerView.getContext()) {
                        @Override
                        public PointF computeScrollVectorForPosition(int targetPosition) {
                            return RecyclerMarquee.ScrollSpeedLinearLayoutManger.this
                                    .computeScrollVectorForPosition(targetPosition);
                        }

                        //This returns the milliseconds it takes to
                        //scroll one pixel.
                        @Override
                        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                            return MILLISECONDS_PER_INCH / displayMetrics.density;
                            //返回滑动一个pixel需要多少毫秒
                        }

                    };
            linearSmoothScroller.setTargetPosition(position);
            startSmoothScroll(linearSmoothScroller);
        }
    }

    public interface OnMarqueeItemClickListener {
        void onBannerItemClick(int itemPosition);
    }
}
