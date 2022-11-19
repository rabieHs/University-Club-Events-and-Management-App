package com.vfxf.fvxmob;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private TextInputEditText FirstName, LastName, PhoneNumber, EmailAdress;
    private TextView UserFirstNamelabel, UserLastNamelabel;
    private DatabaseReference UserRef,UpdateRef;
    private FirebaseAuth mAuth;
    private MaterialButton UpdateBtn;
    String CurrentUserID;
    private ImageView logOutbtn;
    private CircleImageView ProfilePic;
    private static int Gallery_pick=1;
    private Uri imageUri;
    private StorageReference ProfileRef;
    private String downloadUrl;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);



        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("USERS").child(CurrentUserID);
        UpdateRef = FirebaseDatabase.getInstance().getReference();
        ProfileRef = FirebaseStorage.getInstance().getReference();


        FirstName = findViewById(R.id.edit_name);
        LastName = findViewById(R.id.edit_last_name);
        EmailAdress = findViewById(R.id.edit_email);
        PhoneNumber = findViewById(R.id.edit_phone_number);
        UserFirstNamelabel = findViewById(R.id.edit_nom);
        UserLastNamelabel = findViewById(R.id.edit_prenom);
        logOutbtn = findViewById(R.id.log_out);
        UpdateBtn = findViewById(R.id.update_btn);
        ProfilePic = findViewById(R.id.profile_image);

        ProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });



        logOutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });



        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String firstname = snapshot.child("first name").getValue().toString();
                    String lastname = snapshot.child("last name").getValue().toString();
                    String email = snapshot.child("email").getValue().toString();
                    String phone = snapshot.child("phone number").getValue().toString();
                    String image = snapshot.child("profileimage").getValue().toString();


                    UserFirstNamelabel.setText(firstname);
                    UserLastNamelabel.setText(lastname);
                    EmailAdress.setText(email);
                    PhoneNumber.setText(phone);
                    FirstName.setText(firstname);
                    LastName.setText(lastname);
                    Picasso.with(EditProfileActivity.this).load(image).into(ProfilePic);

                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        UpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.vfxf.fvxmob.ProgressDialog dialog = new ProgressDialog(EditProfileActivity.this);
                dialog.show();

                String firstnamee = FirstName.getText().toString();
                String lastnamee = LastName.getText().toString();
                String emaila = EmailAdress.getText().toString();
                String phonen = PhoneNumber.getText().toString();


                Calendar calForDate = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                String saveCurrentDate = currentDate.format(calForDate.getTime());

                Calendar calForTime = Calendar.getInstance();
                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
                String saveCurrentTime = currentTime.format(calForDate.getTime());

                String postRandomName = saveCurrentDate + saveCurrentTime;
                StorageReference filePath = ProfileRef.child("Post Images").child(imageUri.getLastPathSegment() + postRandomName + ".jpg");

                filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            ProfileRef.child("Post Images").child(imageUri.getLastPathSegment() + postRandomName + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl = uri.toString();

                                    UpdateRef.child("USERS").child(mAuth.getUid()).child("first name").setValue(firstnamee);
                                    UpdateRef.child("USERS").child(mAuth.getUid()).child("last name").setValue(lastnamee);
                                    UpdateRef.child("USERS").child(mAuth.getUid()).child("phone number").setValue(phonen);
                                    UpdateRef.child("USERS").child(mAuth.getUid()).child("email").setValue(emaila);
                                    UpdateRef.child("USERS").child(mAuth.getUid()).child("profileimage").setValue(downloadUrl);

                                    Toast.makeText(EditProfileActivity.this, "Informations Updated Successfully", Toast.LENGTH_SHORT).show();






                                }
                            });




                            Toast.makeText(EditProfileActivity.this, "Informations Updated Successfully", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                        else {
                            String message = task.getException().getMessage();
                            Toast.makeText(EditProfileActivity.this, "Error Try again", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                    }
                });




















            }
        });











    }

    private void OpenGallery() {
        Intent GalleryIntent = new Intent();
        GalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        GalleryIntent.setType("image/*");
        startActivityForResult(GalleryIntent, Gallery_pick);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Gallery_pick && resultCode==RESULT_OK && data!=null)
        {
            imageUri = data.getData();


            ProfilePic.setImageURI(imageUri);


        }
    }


}

