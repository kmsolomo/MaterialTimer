package com.example.admin.materialtimer;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity{

    private FloatingActionButton controlButton;
    private TextView timerView;
    private int clickControl;
    private Animator growAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        controlButton = findViewById(R.id.floatingActionButton);
        timerView = findViewById(R.id.timerTextView);
        clickControl = 0;


        //initialize default values
        timerView.setText("30");
        controlButton.setImageResource(R.drawable.ic_play_arrow_44dp);
        growAnimation = AnimatorInflater.loadAnimator(getApplicationContext(),R.animator.image_transition);
        growAnimation.setTarget(controlButton);


        //Create Work Timer
        final CountDownTimer firstTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {
                long timeInSeconds = l / 1000;
                String currentTime = String.valueOf(timeInSeconds);
                timerView.setText(currentTime);
            }

            @Override
            public void onFinish() {
                timerView.setText("0");
            }
        };

        //Create Break Timer
        final CountDownTimer secondTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                long timeInSeconds = l / 1000;
                String currentTime = String.valueOf(timeInSeconds);
                timerView.setText(currentTime);
            }

            @Override
            public void onFinish() {
                timerView.setText("0");
            }
        };

        //Listen for user interaction
        controlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                growAnimation.start();

                if(clickControl == 1){
                    controlButton.setImageResource(R.drawable.ic_play_arrow_44dp);
                    clickControl = 0;

                } else {
                    controlButton.setImageResource(R.drawable.ic_pause_44dp);
                    clickControl = 1;
                }

                //firstTimer.start();
                //secondTimer.start();
            }
        });

    }
}
