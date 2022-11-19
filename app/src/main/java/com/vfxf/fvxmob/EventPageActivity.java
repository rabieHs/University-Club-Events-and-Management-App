package com.vfxf.fvxmob;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class EventPageActivity extends AppCompatActivity {
    private TextView EventDate, EventTime, EventDescription,EventName,EventLocation,interrsedCount;
    private ImageView EventImage, CancelButton;
    private String EventKey,CurrentUserId;
    private DatabaseReference ClickEvtRef;
    private FirebaseAuth mAuth;
    private MaterialButton JoinBtn;
    int countInterrsed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);
        getWindow().setStatusBarColor(Color.GRAY);

        EventKey = getIntent().getExtras().get("EventKey").toString();
        ClickEvtRef = FirebaseDatabase.getInstance().getReference().child("EVENTS").child(EventKey);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();

        EventDate = (TextView) findViewById(R.id.date);
        EventName = (TextView) findViewById(R.id.event_name);
        EventTime = (TextView) findViewById(R.id.time1);
        EventDescription = (TextView) findViewById(R.id.description);
        EventImage = (ImageView) findViewById(R.id.evImage);
        EventLocation = (TextView) findViewById(R.id.locationp);
        CancelButton = (ImageView) findViewById(R.id.Cancelbtn);
        JoinBtn = (MaterialButton) findViewById(R.id.join_btn);
        interrsedCount = findViewById(R.id.count1);





        ClickEvtRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Evttitle = snapshot.child("eventtitle").getValue().toString();
                String Evtimage = snapshot.child("eventimage").getValue().toString();
                String Evtdate = snapshot.child("eventdate").getValue().toString();
                String Evttime = snapshot.child("eventtime").getValue().toString();
                String Evtlocation = snapshot.child("eventlocal").getValue().toString();
                String Evtdescription = snapshot.child("eventdescription").getValue().toString();



                EventName.setText(Evttitle);
                EventDescription.setText(Evtdescription);
                Picasso.with(EventPageActivity.this).load(Evtimage).fit().centerCrop().into(EventImage);
                EventDate.setText(Evtdate);
                EventTime.setText(Evttime);
                EventLocation.setText(Evtlocation);




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ClickEvtRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    countInterrsed = (int) snapshot.child("interrsed").getChildrenCount();
                    ClickEvtRef.child("interresed").setValue(countInterrsed);

                    interrsedCount.setText(Integer.toString(countInterrsed));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        JoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ClickEvtRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String EvtUrl = snapshot.child("eventurl").getValue().toString();
                        ClickEvtRef.child("interrsed").child(CurrentUserId).setValue("interrsed");

                        GotoUrl(EvtUrl);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });


    }

    private void GotoUrl(String evtUrl) {
        Uri uri = Uri.parse(evtUrl);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }


}