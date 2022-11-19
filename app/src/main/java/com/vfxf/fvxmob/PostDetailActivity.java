package com.vfxf.fvxmob;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetailActivity extends AppCompatActivity {

    private RecyclerView CommentList;
    private EditText WriteCommentBox;
    private ImageView PostCommentBtn,LIKEimage,PostImage,CloseBtn,deletePostButton;
    private RelativeLayout LikeBtn;
    private String PostKey,CurrentUID,UserFirstName,UserLastName,UserProfilePicture,CommentKey;
    private DatabaseReference UsersRef,PostRef,LikesRef,ClickpostRef,DeletePostRef;
    private FirebaseAuth mAuth;
    private TextView LikeNo, UserName, Time, Description;
    private Boolean LikeChecker=false;
    private CircleImageView ProfilePic;
    int CountLikes;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        mAuth = FirebaseAuth.getInstance();
        CurrentUID = mAuth.getCurrentUser().getUid();

        PostKey = getIntent().getExtras().get("PostKey").toString();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("USERS");
        PostRef = FirebaseDatabase.getInstance().getReference().child("POSTS").child(PostKey).child("COMMENTS");
        LikesRef = FirebaseDatabase.getInstance().getReference().child("LIKES");
        ClickpostRef = FirebaseDatabase.getInstance().getReference().child("POSTS").child(PostKey);
        DeletePostRef = FirebaseDatabase.getInstance().getReference().child("POSTS");

        CommentList = findViewById(R.id.commentsView);
        WriteCommentBox = findViewById(R.id.textBox);
        PostCommentBtn = findViewById(R.id.imageView10);
        LikeNo = (TextView) findViewById(R.id.likeNo1);
        LIKEimage = (ImageView) findViewById(R.id.like_img1);
        CloseBtn = findViewById(R.id.imageView3);

        UserName = findViewById(R.id.name2);
        ProfilePic = findViewById(R.id.circleImageView3);
        PostImage = findViewById(R.id.imageView2);
        Time = findViewById(R.id.timedate);
        Description = findViewById(R.id.textView2);

        LikeBtn = (RelativeLayout) findViewById(R.id.like2);
        LikeNo = (TextView) findViewById(R.id.likeNo1);
        LIKEimage = (ImageView) findViewById(R.id.like_img1);










        CloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ForumActivity.class));
            }
        });

        ClickpostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String postdescription = snapshot.child("postdescription").getValue().toString();
                String postimage = snapshot.child("postimage").getValue().toString();
                String posttime = snapshot.child("posttime").getValue().toString();
                String username = snapshot.child("username").getValue().toString();
                String profileimage = snapshot.child("userprofileimage").getValue().toString();

                UserName.setText(username);
                Description.setText(postdescription);
                Picasso.with(PostDetailActivity.this).load(postimage).into(PostImage);
                Time.setText(posttime);
                Picasso.with(PostDetailActivity.this).load(profileimage).placeholder(R.drawable.avatar).into(ProfilePic);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        LikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LikeChecker = true;
                LikesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (LikeChecker.equals(true)) {
                            if (snapshot.child(PostKey).hasChild(CurrentUID)){

                                LikesRef.child(PostKey).child(CurrentUID).removeValue();
                                LikeChecker = false;
                            }
                            else {

                                LikesRef.child(PostKey).child(CurrentUID).setValue(true);
                                LikeChecker = false;

                            }

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


        CommentList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        CommentList.setLayoutManager(linearLayoutManager);

        SetLikeStatus(PostKey);

        PostCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UsersRef.child(CurrentUID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            UserFirstName = snapshot.child("first name").getValue().toString();
                            UserLastName = snapshot.child("last name").getValue().toString();
                            UserProfilePicture = snapshot.child("profileimage").getValue().toString();
                            ValidateComment(UserLastName,UserLastName,UserProfilePicture);
                            WriteCommentBox.setText("");

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }




    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<COMMENTS, CommentsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<COMMENTS, CommentsViewHolder>(COMMENTS.class, R.layout.comment_item,CommentsViewHolder.class,PostRef) {
            @Override
            protected void populateViewHolder(CommentsViewHolder commentsViewHolder, COMMENTS comments, int i) {

                 CommentKey = getRef(i).getKey();
                commentsViewHolder.setUsername(comments.getUsername());
                commentsViewHolder.setComment(comments.getComment());
                commentsViewHolder.setDatetime(comments.getDatetime());
                commentsViewHolder.setProfilepic(getApplicationContext(), comments.getProfilepic());

                commentsViewHolder.Delecomment.setVisibility(View.INVISIBLE);

                PostRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String UID = snapshot.child(CommentKey).child("uid").getValue().toString();
                            if (UID.equals(CurrentUID)) {
                                commentsViewHolder.Delecomment.setVisibility(View.VISIBLE);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                commentsViewHolder.Delecomment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PostRef.child(CommentKey).removeValue();
                        Toast.makeText(PostDetailActivity.this, "Comment Has Benn Deleted...", Toast.LENGTH_SHORT).show();





                    }
                });

            }

        };
        CommentList.setAdapter(firebaseRecyclerAdapter);
    }



    public static class CommentsViewHolder extends RecyclerView.ViewHolder{
        ImageView Delecomment;
        View mView;

        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            Delecomment = mView.findViewById(R.id.cancel2);
        }
        public void setComment(String comment){
            TextView description = (TextView) mView.findViewById(R.id.comment);
            description.setText(comment);

        }

        public void setDatetime(String datetime) {
            TextView time = (TextView) mView.findViewById(R.id.textView12);
            time.setText(datetime);
        }
        public void setUsername(String username){
            TextView un = (TextView) mView.findViewById(R.id.rec_msg);
            un.setText(username);
        }
        public void setProfilepic(Context ctx, String profilepic){
            CircleImageView profilepicture = (CircleImageView) mView.findViewById(R.id.dp);
            Picasso.with(ctx).load(profilepic).into(profilepicture);
        }
    }



    private void ValidateComment(String userLastName, String userLastName1, String userProfilePicture) {

        String comment = WriteCommentBox.getText().toString();
        if (TextUtils.isEmpty(comment)){
            WriteCommentBox.setError("Empty!");
        }else {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            String saveCurrentDate = currentDate.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            String saveCurrentTime = currentTime.format(calForDate.getTime());

            String RandomName = CurrentUID+saveCurrentDate+saveCurrentTime;

            HashMap commentsMap = new HashMap();
            commentsMap.put("uid",CurrentUID);
            commentsMap.put("comment",comment);
            commentsMap.put("profilepic",UserProfilePicture);
            commentsMap.put("datetime",saveCurrentDate+" at "+saveCurrentTime);
            commentsMap.put("username",UserFirstName+" "+UserLastName);
            PostRef.child(RandomName).updateChildren(commentsMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Toast.makeText(PostDetailActivity.this, "Comment Added", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(PostDetailActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }

                }
            });




        }
    }
    private void SetLikeStatus(String postKey)
    {

        LikesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(PostKey).hasChild(CurrentUID)){

                    CountLikes = (int) snapshot.child(PostKey).getChildrenCount();
                    LIKEimage.setImageResource(R.drawable.ic_liked);
                    LikeNo.setText(Integer.toString(CountLikes)+(" Like"));
                }
                else {
                    CountLikes = (int) snapshot.child(PostKey).getChildrenCount();
                    LIKEimage.setImageResource(R.drawable.like);
                    LikeNo.setText(Integer.toString(CountLikes)+(" Like"));


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}


