package com.example.multiplayerchess.LoginSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.multiplayerchess.GameMenu;
import com.example.multiplayerchess.MainActivity;
import com.example.multiplayerchess.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AccountSettings extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_account_settings);

        ImageView backMenu = (ImageView) findViewById(R.id.back_login_button);

        backMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go back to the game menu
                Intent intent = new Intent(getApplicationContext(), GameMenu.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}