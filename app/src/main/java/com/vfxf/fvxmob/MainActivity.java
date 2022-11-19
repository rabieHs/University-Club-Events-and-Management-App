package com.vfxf.fvxmob;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private RecyclerView EventsList;
    private DatabaseReference EventsRef, UserREF;
    private FirebaseAuth mAuth;
    private CircleImageView Profile;
    private ImageView GoToEvtadd,GoToForum,Search,Depart;
    String CurrentUserId;
    private EditText Searchbar;
    private ArrayList<EVENTS> arrayList;
    private SwipeRefreshLayout swipeRefreshLayout;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
       getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.TYPE_STATUS_BAR);

        setContentView(R.layout.activity_main);



       /*getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >=19 &&Build.VERSION.SDK_INT <21){
            setWindowsFlag(this,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,true);
        }
        if (Build.VERSION.SDK_INT >=19){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >=21){
            setWindowsFlag(this,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,false
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }*/






        mAuth = FirebaseAuth.getInstance();
        EventsRef = FirebaseDatabase.getInstance().getReference().child("EVENTS");
        CurrentUserId = mAuth.getCurrentUser().getUid();
        UserREF = FirebaseDatabase.getInstance().getReference().child("USERS").child(CurrentUserId).child("profileimage");





        EventsList = (RecyclerView) findViewById(R.id.events_list);
        Profile = (CircleImageView) findViewById(R.id.go_to_profile);
        GoToEvtadd = (ImageView) findViewById(R.id.adminAddpostLayout);
        GoToForum = findViewById(R.id.GoToForum);
        Searchbar = findViewById(R.id.search_box);
        Search = findViewById(R.id.searchbtn);
        swipeRefreshLayout = findViewById(R.id.refreshlayout);
        Depart = findViewById(R.id.Department_act);

        EventsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        EventsList.setLayoutManager(linearLayoutManager);

        arrayList = new ArrayList<>();


        GoToEvtadd.setVisibility(View.INVISIBLE);

        if (CurrentUserId.equals("bpMnij7TSWaqRkL6Ot7Ai87WP1m1")){
            GoToEvtadd.setVisibility(View.VISIBLE);
        }
        DisplayEvents();




        GoToForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ForumActivity.class));
            }
        });
        Depart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),DepartmentsActivity.class));
            }
        });



        GoToEvtadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AdminAddEventAcitivity.class));
            }
        });


        Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),EditProfileActivity.class));
            }
        });
        UserREF.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String profileImage = snapshot.getValue().toString();
                    Picasso.with(MainActivity.this).load(profileImage).into(Profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text= Searchbar.getText().toString();
               SearchVe(text);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DisplayEvents();
                Toast.makeText(MainActivity.this, "Events Refreshed.", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


    }

    private static  void setWindowsFlag(Activity activity,final int Bits,boolean on) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams winparams = window.getAttributes();
        if (on){
            winparams.flags  |=Bits;
        }else {
            winparams.flags &= ~Bits;

        }
        window.setAttributes(winparams);

    }


    private void SearchVe(String text) {
        Query query = EventsRef.orderByChild("eventtitle").startAt(text).endAt(text + "\uf8ff");
        FirebaseRecyclerAdapter<EVENTS, EventViewHolder > firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<EVENTS, EventViewHolder>
                (EVENTS.class, R.layout.event_card, EventViewHolder.class,  query
                ) {
            @Override
            protected void populateViewHolder(EventViewHolder eventViewHolder, EVENTS events, int i) {

               String EventKey = getRef(i).getKey();



                eventViewHolder.setEventtitle(events.getEventtitle());
                eventViewHolder.setEventmonth(events.getEventmonth());
                eventViewHolder.setEventday(events.getEventday());
                eventViewHolder.setEventlocal((events.getEventlocal()));
                eventViewHolder.setEventimage(getApplicationContext(), events.getEventimage());
                eventViewHolder.eventsRef.child(EventKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            eventViewHolder.countINT = (int) snapshot.child("interrsed").getChildrenCount();
                            eventViewHolder. Intertresed.setText(Integer.toString(eventViewHolder.countINT));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



                eventViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent ClickPostIntent = new Intent(MainActivity.this, EventPageActivity.class);
                        ClickPostIntent.putExtra("EventKey", EventKey);
                        startActivity(ClickPostIntent);




                    }
                });


            }

        };



        EventsList.setAdapter(firebaseRecyclerAdapter);



    }
    private void hideSystemBars() {
        WindowInsetsControllerCompat windowInsetsController =
                ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (windowInsetsController == null) {
            return;
        }
        // Configure the behavior of the hidden system bars
        windowInsetsController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
    }


    private void DisplayEvents() {


        FirebaseRecyclerAdapter<EVENTS, EventViewHolder > firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<EVENTS, EventViewHolder>
                (EVENTS.class, R.layout.event_card, EventViewHolder.class,  EventsRef
                ) {
            @Override
            protected void populateViewHolder(EventViewHolder eventViewHolder, EVENTS events, int i) {

                String EventKey = getRef(i).getKey();



                eventViewHolder.setEventtitle(events.getEventtitle());
               eventViewHolder.setEventmonth(events.getEventmonth());
                eventViewHolder.setEventday(events.getEventday());
                eventViewHolder.setEventlocal((events.getEventlocal()));
                eventViewHolder.setEventimage(getApplicationContext(), events.getEventimage());

                eventViewHolder.eventsRef.child(EventKey).child("interresed").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String interresedtoevt  = snapshot.getValue().toString();
                            eventViewHolder.Intertresed.setText(interresedtoevt);
                            Toast.makeText(MainActivity.this, "Events Refreshed.", Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                eventViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent ClickPostIntent = new Intent(MainActivity.this, EventPageActivity.class);
                        ClickPostIntent.putExtra("EventKey", EventKey);
                        startActivity(ClickPostIntent);





                    }
                });

            }
        };

        EventsList.setAdapter(firebaseRecyclerAdapter);

    }
    public static class EventViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        int countINT;
        TextView Intertresed;
        DatabaseReference eventsRef;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            Intertresed = mView.findViewById(R.id.count);
            eventsRef = FirebaseDatabase.getInstance().getReference().child("EVENTS");






        }
        public void setEventtitle(String eventtitle)
        {
            TextView title = (TextView) mView.findViewById(R.id.eventTitle);
            title.setText(eventtitle);
        }
        public void setEventmonth(String eventmonth) {
            TextView monthd = (TextView) mView.findViewById(R.id.month);
            monthd.setText(eventmonth);
        }
        public void setEventimage(Context ctx ,String eventimage)  {
            ImageView EventImage = (ImageView) mView.findViewById(R.id.cardImage);
            Picasso.with(ctx).load(eventimage).fit().centerCrop().into(EventImage);


        }
        public void setEventday(String eventday) {
            TextView monthnm = (TextView) mView.findViewById(R.id.day);
            monthnm.setText(eventday);

        }
        public void setEventlocal(String eventlocal){
            TextView local = (TextView) mView.findViewById(R.id.location);
            local.setText(eventlocal);

        }


    }






}
