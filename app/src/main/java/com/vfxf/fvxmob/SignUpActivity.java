package com.vfxf.fvxmob;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private EditText UserName,UserLastName , UserEmail, UserPass, UserConfirmPass, UserPhone;
    private MaterialButton RegisterBtn;
    private ProgressDialog LoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        UsersRef = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        UserName = (EditText) findViewById(R.id.first_name);
        UserLastName = (EditText) findViewById(R.id.last_name);
        UserEmail = (EditText) findViewById(R.id.user_email);
        UserPass = (EditText) findViewById(R.id.user_password);
        UserConfirmPass = (EditText) findViewById(R.id.user_confirm_password);
        UserPhone = (EditText) findViewById(R.id.user_phone_number);
        RegisterBtn = (MaterialButton) findViewById(R.id.regBtn);


        com.vfxf.fvxmob.ProgressDialog dialog = new com.vfxf.fvxmob.ProgressDialog(SignUpActivity.this);






        if (mAuth.getCurrentUser() != null){
            Intent PostIntent = new Intent(SignUpActivity.this, MainActivity.class);
            PostIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(PostIntent);
            finish();

        }




        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = UserEmail.getText().toString().trim();
                String password = UserPass.getText().toString().trim();
                String cnf = UserConfirmPass.getText().toString().trim();
                String name = UserName.getText().toString().trim();
                String lastname = UserLastName.getText().toString().trim();
                String phone = UserPhone.getText().toString().trim();
                if (TextUtils.isEmpty(email)){
                    UserEmail.setError("Email Is Required!");
                    return;

                }
                else if (TextUtils.isEmpty(password)){
                    UserPass.setError("Password Required!");
                    return;


                }
                else if (!password.equals(cnf)){
                    UserConfirmPass.setError("Password not the same please re-conform!");
                    return;

                }
                else if (password.length() < 6 ){
                    UserPass.setError("Password Must be more then 5 characters");
                    return;
                }
                else {
                    dialog.show();








                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){


                                UsersRef.child("USERS").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.hasChild(mAuth.getUid())){
                                            Toast.makeText(SignUpActivity.this, "User Is Already Exist! ", Toast.LENGTH_SHORT).show();
                                            finish();
                                            dialog.dismiss();
                                        }
                                        else {
                                            UsersRef.child("USERS").child(mAuth.getUid()).child("first name").setValue(name);
                                            UsersRef.child("USERS").child(mAuth.getUid()).child("last name").setValue(lastname);
                                            UsersRef.child("USERS").child(mAuth.getUid()).child("phone number").setValue(phone);
                                            UsersRef.child("USERS").child(mAuth.getUid()).child("email").setValue(email);
                                            dialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {


                                    }
                                });






                                Toast.makeText(SignUpActivity.this, "You Are Registred successfully...", Toast.LENGTH_SHORT).show();
                                Intent PostIntent = new Intent(SignUpActivity.this, AddProfilePicActivity.class);
                                PostIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(PostIntent);
                                finish();
                                dialog.dismiss();

                            }
                            else {
                                String message = task.getException().getMessage();
                                Toast.makeText(SignUpActivity.this, "Error Occured"+ message, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

                            }

                        }
                    });
                }

            }
        });
    }


}