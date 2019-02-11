package com.luhuan.simple;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luhuan.banner.RecyclerBanner;
import com.luhuan.banner.RecyclerMarquee;
import com.luhuanxml.viewpagerbanner.banner.Banner;
import com.luhuanxml.viewpagerbanner.banner.Transformer;
import com.luhuanxml.viewpagerbanner.banner.holder.BannerViewHolder;
import com.luhuanxml.viewpagerbanner.banner.holder.HolderCreator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    LinearLayout linearParent;
    RecyclerBanner<Integer> banner;
    RecyclerMarquee marquee;
    Banner viewPagerBanner;

    //提供一个对象,用于处理颜色的渐变过程
    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linearParent = findViewById(R.id.linearParent);
        banner = findViewById(R.id.banner);
        viewPagerBanner = findViewById(R.id.viewPagerBanner);
        marquee = findViewById(R.id.marquee);
        List<String> marquees = new ArrayList<>();
        marquees.add("1");
        marquees.add("2");
        marquees.add("3");
        marquees.add("4");
        marquees.add("5");
        final List<Integer> list = new ArrayList<>();
        list.add(R.mipmap.img01);
        list.add(R.mipmap.img02);
        list.add(R.mipmap.img03);
        list.add(R.mipmap.img04);
        list.add(R.mipmap.img05);
        list.add(R.mipmap.img06);
        list.add(R.mipmap.img07);
        list.add(R.mipmap.img08);
        list.add(R.mipmap.img09);
        final List<Integer> colorList = new ArrayList<>();
        colorList.add(Color.RED);
        colorList.add(Color.RED - 10);
        colorList.add(Color.RED - 20);
        colorList.add(Color.RED - 30);
        colorList.add(Color.RED - 40);
        colorList.add(Color.RED - 50);
        colorList.add(Color.RED - 60);
        colorList.add(Color.RED - 70);
        colorList.add(Color.RED - 80);
        banner.setDotSize(20)
                .setInterval(2000)
                .setDotMargin(20, 20)
                .setImages(list)
                .canToLeft()
                .setOnBannerItemClickListener(new RecyclerBanner.OnBannerItemClickListener() {
                    @Override
                    public void onBannerItemClick(int itemPosition) {
                        Toast.makeText(MainActivity.this, "" + itemPosition, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onBannerScroll(int position, int sumX) {
                        Log.d("SUMX", "onScrolled: " + sumX);
                        int bgColor;
                        if (sumX == 0) {
                            //开始色值
                            Log.d("AAAAAAA", "onBannerScroll: sumX=0走了");
                            Log.d("AAAAAAA", "onBannerScroll: " + position % list.size());
                            bgColor = colorList.get(position % list.size());
                        } else if (sumX >= 1050 || sumX <= -1050) {
                            Log.d("AAAAAAA", "onBannerScroll: sumX >= 1050走了");
                            Log.d("AAAAAAA", "onBannerScroll: " + position % list.size());
                            //最终色值
                            bgColor = colorList.get(position % list.size());
                        } else if (sumX > 0) {
                            Log.d("AAAAAAA", "onBannerScroll: sumX > 0走了");
                            Log.d("AAAAAAA", "onBannerScroll: " + position % list.size());
                            //渐变色值,伴随手指移动,移动的越多颜色变化的就越多
                            bgColor = (int) argbEvaluator.evaluate(sumX / 1050.0f, colorList.get(position % list.size()), colorList.get((position + 1) % list.size()));
                        } else {
                            Log.d("AAAAAAA", "onBannerScroll: sumX<0走了");
                            Log.d("AAAAAAA", "onBannerScroll: " + position % list.size());
                            //渐变色值,伴随手指移动,移动的越多颜色变化的就越多
                            bgColor = (int) argbEvaluator.evaluate(Math.abs(sumX) / 1050.0f, colorList.get(position % list.size()), colorList.get((position - 1) % list.size()));
                        }
                        //     linearParent.setBackgroundColor(bgColor);
                    }
                }).startAuto();
        marquee.setTextList(marquees)//.canToLeft()
                .setOnMarqueeItemClickListener(new RecyclerMarquee.OnMarqueeItemClickListener() {
                    @Override
                    public void onBannerItemClick(int itemPosition) {
                        Toast.makeText(MainActivity.this, "" + itemPosition, Toast.LENGTH_SHORT).show();
                    }
                }).startAuto();

        viewPagerBanner
                .setBannerAnimation(Transformer.FlipHorizontal)
                .setAutoPlay(true)
                .setPages(list, new HolderCreator<BannerViewHolder>() {
                    @Override
                    public BannerViewHolder createViewHolder() {
                        return new CustomViewHolder();
                    }
                }).start();
        viewPagerBanner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int bgColor=0xFF00000;
                switch (position) {
                    case 0:
                        bgColor= (int) argbEvaluator.evaluate(positionOffset,Color.WHITE,Color.YELLOW);
                        break;
                    case 1:
                        bgColor= (int) argbEvaluator.evaluate(positionOffset,Color.YELLOW,Color.BLUE);
                        break;
                    case 2:
                        bgColor= (int) argbEvaluator.evaluate(positionOffset,Color.BLUE,Color.GREEN);
                        break;
                    case 3:
                        bgColor= (int) argbEvaluator.evaluate(positionOffset,Color.GREEN,Color.GRAY);
                        break;
                    case 4:
                        bgColor= (int) argbEvaluator.evaluate(positionOffset,Color.GRAY,Color.RED);
                        break;
                    case 5:
                        bgColor= (int) argbEvaluator.evaluate(positionOffset,Color.RED,Color.LTGRAY);
                        break;
                    case 6:
                        bgColor= (int) argbEvaluator.evaluate(positionOffset,Color.LTGRAY,Color.DKGRAY);
                        break;
                    case 7:
                        bgColor= (int) argbEvaluator.evaluate(positionOffset,Color.DKGRAY,Color.BLACK);
                        break;
                    case 8:
                        bgColor= (int) argbEvaluator.evaluate(positionOffset,Color.BLACK,Color.WHITE);
                        break;
                }
                linearParent.setBackgroundColor(bgColor);
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        banner.startAuto();
        viewPagerBanner.startAutoPlay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        banner.stopAuto();
        viewPagerBanner.stopAutoPlay();
    }

    class CustomViewHolder implements BannerViewHolder<Integer> {

        private LinearLayout parent;
        private TextView mTextView;

        @Override
        public View createView(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.banner_item, null);
            parent = view.findViewById(R.id.parent);
            mTextView = view.findViewById(R.id.position);
            return view;
        }

        @Override
        public void onBind(Context context, int position, Integer data) {
            // 数据绑定
            parent.setBackgroundResource(data);
            mTextView.setText(position + "");
        }
    }
}
