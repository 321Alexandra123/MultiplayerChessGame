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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {
    EditText editTextEmail, editTextUsername, editTextPassword;
    ImageView backMenu;
    Button registerBtn;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextUsername = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);
        reference = FirebaseDatabase.getInstance().getReference("Users");

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");

        backMenu = (ImageView) findViewById(R.id.back_signup_button);

        backMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go back to the main menu
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        registerBtn = (Button) findViewById(R.id.register_btn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, username, password;
                email = String.valueOf(editTextEmail.getText());
                username = String.valueOf(editTextUsername.getText());
                password = String.valueOf(editTextPassword.getText());

                if(TextUtils.isEmpty(email)) {
                    Toast.makeText(SignUp.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(username)) {
                    Toast.makeText(SignUp.this, "Enter username", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)) {
                    Toast.makeText(SignUp.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    User user = new User(email, username);

                                    reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(SignUp.this, "Account created.",
                                                            Toast.LENGTH_SHORT).show();
                                                    sendVerificationEmail();
                                                }
                                            });
                                } else {
                                    // If sign in fails, display a message to the user.
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthWeakPasswordException weakPassword) {
                                        Toast.makeText(SignUp.this, "The password has to be at least 6 letters long.",
                                                Toast.LENGTH_SHORT).show();
                                    } catch (FirebaseAuthInvalidCredentialsException invalidEmail) {
                                        Toast.makeText(SignUp.this, "The email is invalid.",
                                                Toast.LENGTH_SHORT).show();
                                    } catch (FirebaseAuthException emailExists) {
                                        String errorCode = emailExists.getErrorCode();
                                        if (errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE")) {
                                            Toast.makeText(SignUp.this, "The email already exists.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        Toast.makeText(SignUp.this, "Registration failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });

            }
        });
    }

    private void sendVerificationEmail()
    {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                user.sendEmailVerification();
                                FirebaseAuth.getInstance().signOut();

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                FirebaseAuth.getInstance().signOut();

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
        }
    }
}