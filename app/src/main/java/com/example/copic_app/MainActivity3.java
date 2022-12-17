package com.example.copic_app;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.copic_app.ml.Model17;


import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MainActivity3 extends AppCompatActivity {
    TextView result, demoTxt, classified, clickHere, confidence1, confidence2, says1;
    ImageView imageView, phones1,cpic1,cman1;
    Button picture;
    int imageSize = 224;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main3);
        result = findViewById(R.id.result);
        cman1 = findViewById(R.id.cman);
        cpic1 = findViewById(R.id.cpic);
        imageView = findViewById(R.id.imageView);
        picture = findViewById(R.id.button);

        phones1 = findViewById(R.id.phones);
        demoTxt = findViewById(R.id.demoText);
        says1 = findViewById(R.id.says);
        clickHere = findViewById(R.id.click_here);
        classified = findViewById(R.id.classified);
        confidence1 = findViewById(R.id.confidences);
        confidence2 = findViewById(R.id.confi);

        phones1.setVisibility(View.VISIBLE);
        demoTxt.setVisibility(View.VISIBLE);
        says1.setVisibility(View.VISIBLE);
        clickHere.setVisibility(View.GONE);
        classified.setVisibility(View.GONE);
        result.setVisibility(View.GONE);
        cman1.setVisibility(View.GONE);
        cpic1.setVisibility(View.GONE);
        confidence1.setVisibility(View.GONE);

        picture.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view){
                if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent,1);
                } else{
                    requestPermissions(new String[]{Manifest.permission.CAMERA},100);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK){
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min (image.getWidth(),image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image,dimension,dimension);
            imageView.setImageBitmap(image);

            phones1.setVisibility(View.GONE);
            demoTxt.setVisibility(View.GONE);
            says1.setVisibility(View.GONE);
            clickHere.setVisibility(View.VISIBLE);
            classified.setVisibility(View.VISIBLE);
            result.setVisibility(View.VISIBLE);
            cman1.setVisibility(View.VISIBLE);
            cpic1.setVisibility(View.VISIBLE);
            confidence1.setVisibility(View.VISIBLE);

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage (image);


        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @SuppressLint("SetTextI18n")
    private void classifyImage(Bitmap image){
        try {
            Model17 model = Model17.newInstance(getApplicationContext());

            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValue = new int[imageSize * imageSize];
            image.getPixels(intValue, 0,image.getWidth(), 0,0,image.getWidth(),image.getHeight());

            int pixel = 0;
            for (int i =0; i < imageSize; i++){
                for (int j = 0; j < imageSize; j++){
                    int val = intValue[pixel++];
                    byteBuffer.putFloat(((val >> 16) & 0xFF)* (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF)* (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF)* (1.f / 255.f));
                }
            }
            inputFeature0.loadBuffer(byteBuffer);

            Model17.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeatures0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidence = outputFeatures0.getFloatArray();

            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidence.length; i++){
                if (confidence[i] > maxConfidence){
                    maxConfidence = confidence[i];
                    maxPos = i;
                }
            }
            String[] classes = {"Crater Coffee Bean","Invalid Input","No Defect Coffee Bean","Non-Coffee Bean Object","Quaker Coffee Bean", "Tipped Coffee Bean"};
            String s = "";
            for(int i = 0; i < classes.length; i++){
                s += String.format("%s: %.1f%%\n", classes[i], confidence[i] * 100);
            }
            confidence2.setText(s);
            result.setText(classes[maxPos]);
            result.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.google.com/search?q="+result.getText())));

                }
            });

            model.close();

        }catch (IOException e){

        }
    }

    public void clickexit(View view) {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

}