package com.example.multiplayerchess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.multiplayerchess.LoginSignup.SignUp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class EnterRoomNumber extends AppCompatActivity {
    private ImageView backMenu;
    private Button joinBtn;
    private EditText editRoomCode;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private String roomCode;
    private String connectionUniqueId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_enter_room_number);

        backMenu = (ImageView) findViewById(R.id.back_signup_button);

        backMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go back to the main menu
                Intent intent = new Intent(getApplicationContext(), GameMenu.class);
                startActivity(intent);
            }
        });

        joinBtn = (Button) findViewById(R.id.join_btn);
        editRoomCode = (EditText) findViewById(R.id.edit_room_code);

        joinBtn.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           roomCode = String.valueOf(editRoomCode.getText());

                                           if (TextUtils.isEmpty(roomCode)) {
                                               Toast.makeText(EnterRoomNumber.this, "Enter room code", Toast.LENGTH_SHORT).show();
                                               return;
                                           }
                                           findAvailableConnection();
                                       }
                                   });

        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("ConnectionsFriends");
    }

    public void findAvailableConnection() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean foundAvailableConnection = false;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    if (childSnapshot.child("status").getValue(String.class).equals("waiting") && childSnapshot.child("roomCode").getValue(String.class).equals(roomCode)) {
                        connectionUniqueId = childSnapshot.getKey();
                        foundAvailableConnection = true;

                        joinRoom();
                    }
                }
                if(!foundAvailableConnection) {
                    Toast.makeText(EnterRoomNumber.this, "There is no available room with that room code!", Toast.LENGTH_SHORT).show();
                }
                //callback.onConnectionFound(foundAvailableConnection);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //callback.onConnectionFound(false);
            }
        });
    }

    public void joinRoom() {
        GameView gameView = new GameView(this, this, connectionUniqueId);
        setContentView(gameView);
    }
}