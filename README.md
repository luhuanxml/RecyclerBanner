# RecyclerBannner
纯recyclerview实现banner
> 其实就一个类。只不过习惯性的用了rxjava做自动播放操作，你可以把class和 attrs复制过去用。

gradle
```
maven { url 'https://jitpack.io' }
compile 'com.github.luhuanxml:RecyclerBanner:v1.0.0'  //自动轮播图片切换闪跳
 compile 'com.github.luhuanxml:RecyclerBanner:v1.0.1'  //自动轮播图片从左往右平滑移动
```

布局
```
<com.luhuan.banner.RecyclerBanner
        android:id="@+id/banner"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        app:interval="3000"
        app:dot_parentbackground="@android:color/black"
        app:dot_parentalpha="0.8"
        app:dot_size="6dp"
        app:dot_marginbottom="6dp"
        app:dot_margintop="6dp"
        app:dot_marginleft="6dp"
        app:dot_marginright="6dp">
```

使用

```
        banner= (RecyclerBanner<Integer>) findViewById(R.id.banner);
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
        banner.startAuto();
        //item点击事件
        banner.setOnBannerItemClickListener(new RecyclerBanner.OnBannerItemClickListener<Integer>() {
            @Override
            public void onBannerItemClick(int itemPosition, Integer integer) {
                Toast.makeText(MainActivity.this, itemPosition+"", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this,Main2Activity.class));
            }
        });
        
        //或者直接链式调用
        banner.setDotSize(50)
                .setInterval(500)
                .setDotMargin(30, 30)
                .setImages(list)
                .setOnBannerItemClickListener(new RecyclerBanner.OnBannerItemClickListener<Integer>() {
                    @Override
                    public void onBannerItemClick(int itemPosition, Integer integer) {
                        Toast.makeText(MainActivity.this, itemPosition + "", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, Main2Activity.class));
                    }
                })
                .startAuto();
    
    //在相应的生命周期进行轮播的开启和关闭
    //当然 你也可以在onStart()中用。
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


```

        
