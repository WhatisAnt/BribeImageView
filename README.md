#BribeImageView
仿2016年春节微信朋友圈红包照片效果
#效果图
![](http://ww4.sinaimg.cn/bmiddle/b5405c76gw1f3g5h9a10kj20dc0m83zm.jpg)
![](http://ww4.sinaimg.cn/bmiddle/b5405c76gw1f3m6bd6j7dg20bd0gqdkl.jpg)
#用法
##xml
```xml
<io.github.leibnik.bribeimageview.BribeImageView
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:id="@+id/imageview"
        android:layout_centerInParent="true"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@mipmap/picture"
        app:blur_radius="25"
        app:circle_radius="50dp"
        app:period="1000"
        app:scale_factor="8"
        />
```

* `app:blur_radius`：模糊半径，可以理解为模糊程度
* `app:circle_radius`：圆圈半径
* `app:period`：圆圈显示的时间间隔
* `app:scale_factor`：缩放因子，为了提高模糊处理效率，将原图按倍数缩小后再进行模糊处理最后还原为原图大小

## java
可以给BribeImageView设置监听事件，在模糊处理完成后执行相应操作
```java
imageView.setOnBlurCompletedListener(new BribeImageView.OnBlurCompletedListener() {
    @Override
    public void blurCompleted() {
        progressBar.setVisibility(View.GONE);
        suck.setVisibility(View.VISIBLE);
    }
});
```
## License
[Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0)