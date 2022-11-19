package com.vfxf.fvxmob;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SelectDepartmentActivity extends AppCompatActivity {

    String[] items ={"Secretary","Marketing","Finance","Project Managment"};
    String Itemname,CurrentUserId,ULastName,ProfilePicture,FirstName,PostKey,PPct;
    AutoCompleteTextView ItemSelector;
    ArrayAdapter<String> arrayAdapter;
    private MaterialButton Start;
    private DatabaseReference CategoryRef,UsersRef,UsersRefTwo;
    private FirebaseAuth mAuth;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_department);
        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();



        CategoryRef = FirebaseDatabase.getInstance().getReference();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("USERS");
        UsersRefTwo = FirebaseDatabase.getInstance().getReference().child("USERS");




        Start = findViewById(R.id.upload2);



        ItemSelector = findViewById(R.id.autocompletetv);
        arrayAdapter = new ArrayAdapter<String>(this,R.layout.departments_dropdown_items,items);
        ItemSelector.setAdapter(arrayAdapter);
        Start.setEnabled(false);



        ItemSelector.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Itemname = item.toString();
                Start.setEnabled(true);
            }
        });



       Start.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               UsersRef.addValueEventListener(new ValueEventListener() {

                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       if (snapshot.exists()){
                       FirstName = snapshot.child(CurrentUserId).child("first name").getValue().toString();
                       ULastName = snapshot.child(CurrentUserId).child("last name").getValue().toString();
                       ProfilePicture = snapshot.child(CurrentUserId).child("profileimage").getValue().toString();

                           HashMap commentsMap = new HashMap();
                           commentsMap.put("uid",CurrentUserId);
                           commentsMap.put("username",FirstName+" "+ULastName);
                           commentsMap.put("profilepc",ProfilePicture);
                           commentsMap.put("property","Actif Member");

                           CategoryRef.child("Category").child(Itemname).child(CurrentUserId).updateChildren(commentsMap).addOnCompleteListener(new OnCompleteListener() {
                               @Override
                               public void onComplete(@NonNull Task task) {
                                   if (task.isSuccessful()){
                                       Toast.makeText(SelectDepartmentActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                                       Intent setupIntent = new Intent(SelectDepartmentActivity.this, MainActivity.class);
                                       setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                       startActivity(setupIntent);
                                       finish();                                   }
                                   else {
                                       Toast.makeText(SelectDepartmentActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                   }

                               }
                           });



                       }}

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });










           }
       });




    }
}