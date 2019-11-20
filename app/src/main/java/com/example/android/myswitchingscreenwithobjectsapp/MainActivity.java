package com.example.android.myswitchingscreenwithobjectsapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import static com.example.android.myswitchingscreenwithobjectsapp.Constants.SECOND_SCREEN_REQUEST_CODE;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Switch destroyTheWorldSwitch = findViewById(R.id.destroyTheWorldSwitch);
        final TextView messageView = findViewById(R.id.messageTextView);

        destroyTheWorldSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    messageView.setText("mmm...Maybe Later");
                }else{
                    messageView.setText("You have saved the world");
                }
            }
        });

    }

    public void buildBomb(View view) {
        //build a bomb, ask to build a new activity(with screen) and send the bomb

        Bomb bomb = new Bomb();
        TextView bombNameTextView = findViewById(R.id.bombNameTextView);

        if(bombNameTextView.getText().toString().trim().isEmpty()) {
            //using a Snackbar, similar to Toast it shows a message to the user, but the former one can be dismissed by the user.
            //it's recommended to use Snackbar.
            Snackbar.make(view, "Please name your bomb", Snackbar.LENGTH_SHORT).show();
            return;
        }

        bomb.setName(bombNameTextView.getText().toString());

        Intent nextScreen = new Intent(this, SecondActivity.class);

        nextScreen.putExtra(Bomb.BOMB_KEY, bomb);

        startActivityForResult(nextScreen, SECOND_SCREEN_REQUEST_CODE);

    }

    // to convert svg files into xml
    //http://inloop.github.io/svg2android/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SECOND_SCREEN_REQUEST_CODE){
            if(resultCode == RESULT_OK){ //if this is true, the secondActivity closed properly, it did not crashed.
                ImageView imageView = findViewById(R.id.currentBombImageView);
                Bomb bomb = (Bomb) data.getSerializableExtra(Bomb.BOMB_KEY);


                imageView.setImageResource(bomb.getResId());

                Switch destroyTheWorldSwitch = findViewById(R.id.destroyTheWorldSwitch);
                destroyTheWorldSwitch.setEnabled(true);

            }
            else if (resultCode == RESULT_CANCELED){ // the app secondActivity crashed or you forgot to use setResult() method before calling finish()
                Toast.makeText(this, "maybe you forgot to set the data back, check your child setResult() ", Toast.LENGTH_SHORT).show();

            }
        }else {
            Toast.makeText(this, "wrong request code come back? requestCode: " + requestCode, Toast.LENGTH_SHORT).show();
        }


    }
}
