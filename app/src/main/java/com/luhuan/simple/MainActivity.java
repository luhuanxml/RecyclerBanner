package com.luhuan.simple;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.luhuan.banner.RecyclerBanner;
import com.luhuan.banner.RecyclerMarquee;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    LinearLayout linearParent;
    RecyclerBanner<Integer> banner;
    RecyclerMarquee marquee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linearParent = findViewById(R.id.linearParent);
        banner = findViewById(R.id.banner);
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
                    public void onBannerScroll(int itemPosition) {
                        int index = itemPosition % list.size();
                        int color=Color.WHITE;
                        switch (index) {
                            case 0:
                                color=Color.RED;
                                break;
                            case 1:
                                color=Color.YELLOW;
                                break;
                            case 2:
                                color=Color.GREEN;
                                break;
                            case 3:
                                color=Color.BLUE;
                                break;
                            case 4:
                                color=Color.DKGRAY;
                                break;
                            case 5:
                                color=Color.GRAY;
                                break;
                            case 6:
                                color=Color.BLACK;
                                break;
                        }
                        linearParent.setBackgroundColor(color);
                    }
                }).startAuto();
        marquee.setTextList(marquees)//.canToLeft()
                .setOnMarqueeItemClickListener(new RecyclerMarquee.OnMarqueeItemClickListener() {
                    @Override
                    public void onBannerItemClick(int itemPosition) {
                        Toast.makeText(MainActivity.this, "" + itemPosition, Toast.LENGTH_SHORT).show();
                    }
                }).startAuto();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        banner.startAuto();
    }

    @Override
    protected void onPause() {
        super.onPause();
        banner.stopAuto();
    }
}
