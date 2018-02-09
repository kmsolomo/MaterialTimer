package com.example.admin.materialtimer;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ImageButton controlButton;
    private TextView timerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        controlButton = findViewById(R.id.controlButton);
        timerView = findViewById(R.id.timerTextView);

        //set timer based on settings

        //placeholder
        timerView.setText("30");


        //
        final CountDownTimer firstTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {
                long timeInSeconds = l / 1000;
                String currentTime = String.valueOf(timeInSeconds);
                timerView.setText(currentTime);
            }

            @Override
            public void onFinish() {
                timerView.setText("00");
            }
        };

        final CountDownTimer secondTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                long timeInSeconds = l / 1000;
                String currentTime = String.valueOf(timeInSeconds);
                timerView.setText(currentTime);
            }

            @Override
            public void onFinish() {
                timerView.setText("00");
            }
        };

        controlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstTimer.start();
                //secondTimer.start();
            }
        });

    }
}
