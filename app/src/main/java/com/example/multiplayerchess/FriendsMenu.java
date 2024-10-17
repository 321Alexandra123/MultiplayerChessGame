package com.example.multiplayerchess;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.multiplayerchess.LoginSignup.Login;
import com.example.multiplayerchess.LoginSignup.SignUp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FriendsMenu extends AppCompatActivity {
    ImageView backMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friends_menu);

        backMenu = (ImageView) findViewById(R.id.back_menu_button);

        backMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go back to the main menu
                Intent intent = new Intent(getApplicationContext(), GameMenu.class);
                startActivity(intent);
            }
        });
    }

    public void openHost(View view) {
        GameView gameView = new GameView(this, this, true);
        setContentView(gameView);
    }

    public void openJoin(View view) {
        Intent intent = new Intent(FriendsMenu.this, EnterRoomNumber.class);
        startActivity(intent);
    }
}