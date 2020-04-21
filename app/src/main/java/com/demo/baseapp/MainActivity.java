package com.demo.baseapp;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1587464830619&di=42078e8bd04556f069b7446e8bce1a63&imgtype=0&src=http%3A%2F%2Fimage.biaobaiju.com%2Fuploads%2F20191102%2F15%2F1572678473-ipvOHQGmES.jpeg";
        Glide.with(this)
                .load(url)
                .into((ImageView) findViewById(R.id.iv_zan));
    }
}
