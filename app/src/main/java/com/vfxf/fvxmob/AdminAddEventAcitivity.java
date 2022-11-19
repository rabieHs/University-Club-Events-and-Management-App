package com.vfxf.fvxmob;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AdminAddEventAcitivity extends AppCompatActivity {
    private ImageView EVTimage;
    private EditText EVTtitle, EVTdate, EVTtime, EVTdescription, EVTmonth, EVTdAY, EVTlocation,EVTurl;
    private Button UploadEvt;
    private Uri imageUri;
    private static int Gallery_pick = 1;
    private String Description, Title, Time, Date,downloadUrl, current_user_id, Month, Day, localisation,url;
    private StorageReference EVTref;
    private DatabaseReference UsersRef, EVENTreference;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_event_acitivity);

        EVTref = FirebaseStorage.getInstance().getReference();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("USERS");
        EVENTreference = FirebaseDatabase.getInstance().getReference();


        EVTimage = (ImageView) findViewById(R.id.add_evt_image);
        EVTtitle = (EditText) findViewById(R.id.evadmintTitle);
        EVTtime = (EditText) findViewById(R.id.evadmintime);
        EVTdate = (EditText) findViewById(R.id.evadmintdate);
        EVTmonth = (EditText) findViewById(R.id.evadmintmonth);
        EVTdescription = (EditText) findViewById(R.id.evtadmindescription);
        UploadEvt = (Button) findViewById(R.id.UploadEvt);
        EVTdAY = (EditText) findViewById(R.id.evadminday);
        EVTlocation = (EditText) findViewById(R.id.evadminlocation) ;
        EVTurl = (EditText) findViewById(R.id.evadminURL);
        EVTimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();

            }
        });
        UploadEvt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Description = EVTdescription.getText().toString();
                Time = EVTtime.getText().toString();
                Title= EVTtitle.getText().toString();
                Date = EVTdate.getText().toString();
                Month = EVTmonth.getText().toString();
                Day = EVTdAY.getText().toString();
                localisation = EVTlocation.getText().toString();
                url = EVTurl.getText().toString();


                ValidateEVTinfo();


            }
        });

    }

    private void ValidateEVTinfo() {


        if(imageUri==null)
        {
            Toast.makeText(this, "Image Not Selected !", Toast.LENGTH_SHORT).show();
        }

        else {

            StoringImageToStorage();

        }


    }

    private void StoringImageToStorage()
    {
        com.vfxf.fvxmob.ProgressDialog dialog = new ProgressDialog(AdminAddEventAcitivity.this);
        dialog.show();
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
       String saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:SS");
        String saveCurrentTime = currentTime.format(calForDate.getTime());

        String postRandomName = saveCurrentDate + saveCurrentTime;
        StorageReference filePath = EVTref.child("Post Images").child(Title + ".jpg");

        filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    EVTref.child("Post Images").child(Title + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadUrl = uri.toString();
                           

                            EVENTreference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){

                                        EVENTreference.child("EVENTS").child(postRandomName).child("eventtitle").setValue(Title);
                                        EVENTreference.child("EVENTS").child(postRandomName).child("eventtime").setValue(Time);
                                        EVENTreference.child("EVENTS").child(postRandomName).child("eventdate").setValue(Date);
                                        EVENTreference.child("EVENTS").child(postRandomName).child("eventdescription").setValue(Description);
                                        EVENTreference.child("EVENTS").child(postRandomName).child("eventimage").setValue(downloadUrl);
                                        EVENTreference.child("EVENTS").child(postRandomName).child("eventmonth").setValue(Month);
                                        EVENTreference.child("EVENTS").child(postRandomName).child("eventday").setValue(Day);
                                        EVENTreference.child("EVENTS").child(postRandomName).child("eventlocal").setValue(localisation);
                                        EVENTreference.child("EVENTS").child(postRandomName).child("eventurl").setValue(url);





                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });



                        }
                    });




                    Toast.makeText(AdminAddEventAcitivity.this, "Event Uploaded...", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                else {
                    String message = task.getException().getMessage();
                    Toast.makeText(AdminAddEventAcitivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

            }
        });
    }

    private void SavingPostInformationToDatabase() {


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


            EVTimage.setImageURI(imageUri);


        }
    }
}