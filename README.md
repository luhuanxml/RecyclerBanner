# RecyclerBannner
纯recyclerview实现banner

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


    
> 就一个类，就不搞什么引用了，如果是一个简单的banner，可以直接用这个。
> 当然也可以使用网络引用

        
