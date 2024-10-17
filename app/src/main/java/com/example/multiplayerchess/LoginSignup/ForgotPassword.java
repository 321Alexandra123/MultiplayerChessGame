package com.example.multiplayerchess.LoginSignup;

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

import com.example.multiplayerchess.MainActivity;
import com.example.multiplayerchess.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    ImageView backMenu;
    EditText editTextEmail;
    Button resetPasswordBtn;
    FirebaseAuth mAuth;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_forgot_password);

        backMenu = (ImageView) findViewById(R.id.back_button);

        backMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go back to the main menu
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });

        editTextEmail = findViewById(R.id.email);
        mAuth = FirebaseAuth.getInstance();

        resetPasswordBtn = (Button) findViewById(R.id.reset_password_btn);

        resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = String.valueOf(editTextEmail.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(ForgotPassword.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                resetPassword();
            }
        });
    }

    private void resetPassword() {
        resetPasswordBtn.setVisibility(View.INVISIBLE);

        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ForgotPassword.this, "Reset Password link has been sent to your email.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ForgotPassword.this, Login.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ForgotPassword.this, "There was an error and the link wasn't sent.", Toast.LENGTH_SHORT).show();
                resetPasswordBtn.setVisibility(View.VISIBLE);
            }
        });
    }
}