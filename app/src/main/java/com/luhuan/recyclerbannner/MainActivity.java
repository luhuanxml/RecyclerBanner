package com.luhuan.recyclerbannner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerBanner<Integer> banner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        banner= (RecyclerBanner<Integer>) findViewById(R.id.banner);
        banner.setInterval(2000);
        banner.startAuto();
        List<Integer> list=new ArrayList<>();
        list.add(R.mipmap.img01);
        list.add(R.mipmap.img02);
        list.add(R.mipmap.img03);
        list.add(R.mipmap.img04);
        list.add(R.mipmap.img05);
        list.add(R.mipmap.img06);
        list.add(R.mipmap.img07);
        list.add(R.mipmap.img08);
        list.add(R.mipmap.img09);
        banner.setImages(list);
        banner.setOnBannerItemClickListener(new RecyclerBanner.OnBannerItemClickListener<Integer>() {
            @Override
            public void onBannerItemClick(int itemPosition, Integer integer) {
                Toast.makeText(MainActivity.this, itemPosition+"", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this,Main2Activity.class));
            }
        });
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
