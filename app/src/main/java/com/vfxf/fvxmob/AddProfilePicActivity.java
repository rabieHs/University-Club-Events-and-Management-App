package com.vfxf.fvxmob;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddProfilePicActivity extends AppCompatActivity {
    private CircleImageView ProfilePic;
    private Uri imageUri;
    private String downloadUrl;
    private StorageReference ProfileRef;
    String CurrentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef,UpdateRef;
    private MaterialButton UpdateBtn;
    private static int Gallery_pick=1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile_pic);


        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("USERS").child(CurrentUserID);
        UpdateRef = FirebaseDatabase.getInstance().getReference();
        ProfileRef = FirebaseStorage.getInstance().getReference();


        UpdateBtn = findViewById(R.id.upload_profile_image);
        ProfilePic = findViewById(R.id.select_profile_image);

        UpdateBtn.setEnabled(false);

        ProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });
        UpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.vfxf.fvxmob.ProgressDialog dialog = new ProgressDialog(AddProfilePicActivity.this);
                dialog.show();


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



                                    UpdateRef.child("USERS").child(mAuth.getUid()).child("profileimage").setValue(downloadUrl);







                                }
                            });




                            Toast.makeText(AddProfilePicActivity.this, "Image  Uploaded Successfully", Toast.LENGTH_SHORT).show();


                            dialog.dismiss();
                            Intent setupIntent = new Intent(AddProfilePicActivity.this, SelectDepartmentActivity.class);
                            setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(setupIntent);
                            finish();
                        }
                        else {
                            String message = task.getException().getMessage();
                            Toast.makeText(AddProfilePicActivity.this, "Error Try again", Toast.LENGTH_SHORT).show();
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
            UpdateBtn.setEnabled(true);


        }
    }

}
