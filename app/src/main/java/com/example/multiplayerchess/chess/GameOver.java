package com.example.multiplayerchess.chess;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.multiplayerchess.GameMenu;
import com.example.multiplayerchess.R;

public class GameOver extends Activity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game_over);

        String state = getIntent().getExtras().getString("State");
        TextView text = findViewById(R.id.textGameOver);
        text.setText(state);

        Button backMenu = (Button) findViewById(R.id.back_menu_btn);

        backMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameOver.this, GameMenu.class);
                startActivity(intent);
            }
        });
    }
}
