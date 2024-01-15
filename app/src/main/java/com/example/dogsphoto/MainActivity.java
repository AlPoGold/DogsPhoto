package com.example.dogsphoto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends AppCompatActivity {
    public static final String BASE_URL = "https://dog.ceo/api/breeds/image/random";
    public static final String ERROR_MSG = "It is impossible to show data";
    private MainViewModel mainViewModel;
    private static final String LOG_MAIN = "MainActivity";

    Button nextImageBtn;
    ImageView ivImageDog;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.loadImage();

        mainViewModel.getDogImage().observe(this, new Observer<DogImage>() {
            @Override
            public void onChanged(DogImage image) {
                Glide.with(MainActivity.this)
                        .load(image.getMessage())
                        .into(ivImageDog);

                Log.d("mainviewmodel", "set image");
            }
        });

        mainViewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loading) {
                if(loading){
                    progressBar.setVisibility(View.VISIBLE);
                }else{
                    progressBar.setVisibility(View.INVISIBLE);
                }

            }
        });

        mainViewModel.getIsHavingNet().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isHavingNet) {
                if(!isHavingNet){
                    Toast.makeText(MainActivity.this, ERROR_MSG, Toast.LENGTH_SHORT).show();
                }
            }
        });

        nextImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("mainactivity", "button is pressed");
                mainViewModel.loadImage();
            }
        });
    }

    private void initViews(){
        nextImageBtn = findViewById(R.id.btnNextImage);
        ivImageDog = findViewById(R.id.imageDog);
        progressBar = findViewById(R.id.progressBar);
    }


}