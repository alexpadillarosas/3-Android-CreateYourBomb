package com.example.android.myswitchingscreenwithobjectsapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    Bomb bomb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Intent activityThatCalled = getIntent();

        bomb = (Bomb) activityThatCalled.getSerializableExtra(Bomb.BOMB_KEY);

        TextView bombNameTextView = findViewById(R.id.bombNameSecondTextView);
        bombNameTextView.setText(bomb.getName());
    }

    public void selectBomb(View view) {
//        ImageView imageView;

        switch (view.getId()){
            case R.id.bomb1imageView:
                bomb.setResId(R.drawable.bomb);
                break;
            case R.id.bomb2imageView:
                bomb.setResId(R.drawable.dynamite);
                break;
            case R.id.bomb3imageView:
                bomb.setResId(R.drawable.nuclear);
                break;
        }

        Intent goingBack = new Intent();
        goingBack.putExtra(Bomb.BOMB_KEY, bomb);
        //gives result code and intent with data back to the activity that called this one (main)
        setResult(RESULT_OK, goingBack);

        //kills this activity
        finish();
    }

}
