package com.vfxf.fvxmob;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private Button Loginbtn;
    private TextView SignUpBtn;
    private EditText UserMail, UserPassword;
    private DatabaseReference userRef;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        SignUpBtn = (TextView) findViewById(R.id.sign_up_act);
        Loginbtn = (Button) findViewById(R.id.signInBtn);
        UserMail = (EditText) findViewById(R.id.login_mail);
        UserPassword = (EditText) findViewById(R.id.login_password);

        if (mAuth.getCurrentUser() != null){
            Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        }



        Loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = UserMail.getText().toString().trim();
                String password = UserPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)){
                    UserMail.setError("Email Is Required!");
                    return;

                }
                else if (TextUtils.isEmpty(password)){
                    UserPassword.setError("Password Required!");
                    return;


                }
                else {
                   com.vfxf.fvxmob.ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
                    dialog.show();

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                dialog.dismiss();

                                Toast.makeText(LoginActivity.this, "You Are Logged In Sccessfully...", Toast.LENGTH_SHORT).show();
                                Intent loginIntenttwo = new Intent(LoginActivity.this, MainActivity.class);
                                loginIntenttwo.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(loginIntenttwo);
                                finish();
                            }
                            else {
                                dialog.dismiss();
                                String message = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Error Occured"+ message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

        SignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent PostIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                PostIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(PostIntent);
                finish();
            }
        });




    }


}