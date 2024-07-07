package com.example.smartdustbin;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ProgressBar fillProgress;
    private TextView fillText, ee;
    private static final String URL_STRING = "http://192.168.4.1/";
    private static final int UPDATE_INTERVAL = 5000; // 5 seconds

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private final Handler updateHandler = new Handler(Looper.getMainLooper());
    public Float lastvalue=8.1f;
    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            fetchValue();
            updateHandler.postDelayed(this, UPDATE_INTERVAL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fillProgress = findViewById(R.id.fillProgress);
        fillText = findViewById(R.id.fillText);
        ee = findViewById(R.id.ee);

        // Start the periodic update
        updateHandler.post(updateRunnable);
    }

    private void fetchValue() {
        executorService.execute(() -> {
            Float value = getValueFromUrl(URL_STRING);
            mainThreadHandler.post(() -> updateUI(value));
        });
    }

    private Float getValueFromUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                String response = in.readLine();
                if(response==null){return lastvalue;}
                return Float.parseFloat(response);
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void updateUI(Float value) {
        if (value != null) {
            lastvalue=value;
            int percentage = (int) Math.floor((value / 8.2f) * 100);

            int fillpercentage = 100 - percentage;
            fillProgress.setProgress(fillpercentage);
            fillText.setText(fillpercentage + "%");

            // Set the color of the progress bar based on the fillpercentage
            if (fillpercentage < 40) {
                fillProgress.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            } else if (fillpercentage < 70) {
                fillProgress.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
            } else {
                fillProgress.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            }
        } else {
            fillText.setText("Error fetching value");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop the periodic update when the activity is destroyed
        updateHandler.removeCallbacks(updateRunnable);
        executorService.shutdown();
    }
}

