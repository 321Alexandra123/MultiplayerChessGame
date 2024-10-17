package com.example.multiplayerchess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.multiplayerchess.LoginSignup.AccountSettings;

public class GameMenu extends AppCompatActivity {
    ImageView accountSettingsBtn;
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game_menu);

        accountSettingsBtn = (ImageView) findViewById(R.id.account_settings_btn);

        accountSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open account settings activity
                Intent intent = new Intent(getApplicationContext(), AccountSettings.class);
                startActivity(intent);
            }
        });

        Log.d("GameOver", "ON CREATE GAME MENU");
    }

    public void playOnline(View view) {
        gameView = new GameView(this, this, false);
        setContentView(gameView);
    }

    public void playFriends(View view) {
        Intent intent = new Intent(getApplicationContext(), FriendsMenu.class);
        startActivity(intent);
    }
}