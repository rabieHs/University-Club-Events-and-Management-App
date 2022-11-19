package com.vfxf.fvxmob;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddPostActivity extends AppCompatActivity {

    private TextView UserName;
    private EditText PostDescription;
    private CircleImageView ProfileImage;
    private ConstraintLayout AddPostImage;
    private Button PostBtn;
    private Uri ImageUri;
    public static int Gallery_pick = 1;
    private ImageView PostImage,BackBtn;
    private String Description, downloadUrl,UserFirstName,UserLastName,UserID,UserProfilePicture;
    private StorageReference PostRef;
    private DatabaseReference PostReference,UsersRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        PostRef = FirebaseStorage.getInstance().getReference();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("USERS");
        PostReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        UserID = mAuth.getCurrentUser().getUid();

        UserName = findViewById(R.id.user_name);
        PostDescription = findViewById(R.id.post_text);
        ProfileImage = findViewById(R.id.circleImageView3);
        AddPostImage = findViewById(R.id.constraintLayout3);
        PostBtn = findViewById(R.id.post_it);
        PostImage = findViewById(R.id.meme);
        BackBtn = findViewById(R.id.imageView55);
        AddPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGalley();
            }
        });
        PostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Description = PostDescription.getText().toString();


                ValidatePost();

            }
        });
        BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ForumActivity.class));
            }
        });

        PostReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String FirstName = snapshot.child("USERS").child(UserID).child("first name").getValue().toString();
                   String ULastName = snapshot.child("USERS").child(UserID).child("last name").getValue().toString();
                    String ProfilePicture = snapshot.child("USERS").child(UserID).child("profileimage").getValue().toString();
                    UserName.setText(FirstName+""+ULastName);
                    Picasso.with(getApplicationContext()).load(ProfilePicture).into(ProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void ValidatePost() {
        if(ImageUri==null && Description.isEmpty()){

            Toast.makeText(AddPostActivity.this, "Please Write Somthing Or Add Any Image", Toast.LENGTH_SHORT).show();
        }
        else
        {
            StoringPostInfo();
        }
    }

    private void StoringPostInfo() {
        com.vfxf.fvxmob.ProgressDialog dialog = new ProgressDialog(AddPostActivity.this);
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        String saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        String saveCurrentTime = currentTime.format(calForDate.getTime());

        String postRandomName = saveCurrentDate + saveCurrentTime;
        if (ImageUri != null)
        {
        StorageReference filePath = PostRef.child("Users_Posts_Images").child(ImageUri.getLastPathSegment() + postRandomName + ".jpg");

        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    PostRef.child("Users_Posts_Images").child(ImageUri.getLastPathSegment() + postRandomName + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadUrl = uri.toString();


                            PostReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {





                                        UserFirstName = snapshot.child("USERS").child(UserID).child("first name").getValue().toString();
                                        UserLastName = snapshot.child("USERS").child(UserID).child("last name").getValue().toString();
                                        UserProfilePicture = snapshot.child("USERS").child(UserID).child("profileimage").getValue().toString();






                                        PostReference.child("POSTS").child(UserID+postRandomName).child("postdescription").setValue(Description);
                                        PostReference.child("POSTS").child(UserID+postRandomName).child("posttime").setValue(saveCurrentDate+" at "+saveCurrentTime);
                                        PostReference.child("POSTS").child(UserID+postRandomName).child("postimage").setValue(downloadUrl);
                                        PostReference.child("POSTS").child(UserID+postRandomName).child("username").setValue(UserFirstName+""+UserLastName);
                                        PostReference.child("POSTS").child(UserID+postRandomName).child("userprofileimage").setValue(UserProfilePicture);
                                        PostReference.child("POSTS").child(UserID+postRandomName).child("PostKey").setValue(PostReference.child("Posts").child(UserID+postRandomName).getKey().toString());
                                        PostReference.child("POSTS").child(UserID+postRandomName).child("userid").setValue(UserID);









                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });



                        }
                    });




                    Toast.makeText(AddPostActivity.this, "Post Uploaded...", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),ForumActivity.class));
                    dialog.dismiss();
                }
                else {
                    String message = task.getException().getMessage();
                    Toast.makeText(AddPostActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

            }
        });

        }else  {
            PostReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){





                        UserFirstName = snapshot.child("USERS").child(UserID).child("first name").getValue().toString();
                        UserLastName = snapshot.child("USERS").child(UserID).child("last name").getValue().toString();
                        UserProfilePicture = snapshot.child("USERS").child(UserID).child("profileimage").getValue().toString();




                        PostReference.child("POSTS").child(UserID+postRandomName).child("postdescription").setValue(Description);
                        PostReference.child("POSTS").child(UserID+postRandomName).child("posttime").setValue(saveCurrentDate+" at "+saveCurrentTime);
                        PostReference.child("POSTS").child(UserID+postRandomName).child("username").setValue(UserFirstName+""+UserLastName);
                        PostReference.child("POSTS").child(UserID+postRandomName).child("postimage").setValue("https://st3.depositphotos.com/23594922/31822/v/600/depositphotos_318221368-stock-illustration-missing-picture-page-for-website.jpg");
                        PostReference.child("POSTS").child(UserID+postRandomName).child("userprofileimage").setValue(UserProfilePicture);
                        PostReference.child("POSTS").child(UserID+postRandomName).child("PostKey").setValue(PostReference.child("Posts").child(UserID+postRandomName).getKey().toString());
                        PostReference.child("POSTS").child(UserID+postRandomName).child("userid").setValue(UserID);









                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            dialog.dismiss();
            Toast.makeText(AddPostActivity.this, "Post Uploaded... ", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),AddPostActivity.class));


        }



    }

    private void OpenGalley() {
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
            ImageUri = data.getData();


            PostImage.setImageURI(ImageUri);


        }

    }
}