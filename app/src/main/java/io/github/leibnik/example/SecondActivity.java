package io.github.leibnik.example;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;

import io.github.leibnik.bribeimageview.BribeImageView;

/**
 * Created by Droidroid on 2016/5/1.
 */
public class SecondActivity extends AppCompatActivity {
    BribeImageView imageView;
    Button download,suck;
    ProgressBar progressBar;
    String url = "http://ww4.sinaimg.cn/mw600/90eef340jw1f3f5wqrb2tj20dc0dct9g.jpg";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        imageView = (BribeImageView) findViewById(R.id.imageview);
        download = (Button) findViewById(R.id.download);
        suck = (Button) findViewById(R.id.suck);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        imageView.setOnBlurCompletedListener(new BribeImageView.OnBlurCompletedListener() {
            @Override
            public void blurCompleted() {
                progressBar.setVisibility(View.GONE);
                suck.setVisibility(View.VISIBLE);
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                download.setVisibility(View.GONE);
                Glide.with(getApplicationContext()).load(url).into(imageView);
            }
        });
        suck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setToBlur(false);
                suck.setVisibility(View.GONE);
            }
        });
    }
}
