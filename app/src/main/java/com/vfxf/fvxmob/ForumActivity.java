package com.vfxf.fvxmob;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ForumActivity extends AppCompatActivity {
    private RecyclerView PostsList;
    private DatabaseReference EventsRef,LikesRef,DeletePostRef;
    private FirebaseAuth mAuth;
    private ImageView AddPostbtn,BackBtn;
    private Boolean LikeChecker=false;
    private String CurrentUID;
    private CircleImageView ProfilePict;
    private SwipeRefreshLayout SwipeLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
        EventsRef = FirebaseDatabase.getInstance().getReference().child("POSTS");
        DeletePostRef = FirebaseDatabase.getInstance().getReference().child("POSTS");


        LikesRef = FirebaseDatabase.getInstance().getReference().child("LIKES");
        mAuth = FirebaseAuth.getInstance();
        CurrentUID = mAuth.getCurrentUser().getUid();

        PostsList =(RecyclerView) findViewById(R.id.postsview);
        AddPostbtn = findViewById(R.id.addpost);
        ProfilePict = findViewById(R.id.profile);
        BackBtn = findViewById(R.id.goBack);
        SwipeLay = findViewById(R.id.swipelayout);


        PostsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        PostsList.setLayoutManager(linearLayoutManager);




        DisplayPosts();

        AddPostbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AddPostActivity.class));
            }
        });

        ProfilePict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),EditProfileActivity.class));
            }
        });
        BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(ForumActivity.this, MainActivity.class);
                backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(backIntent);
                finish();
            }
        });
        SwipeLay.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DisplayPosts();
                SwipeLay.setRefreshing(false);
                Toast.makeText(ForumActivity.this, "Forum Refreshed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void DisplayPosts() {



        FirebaseRecyclerAdapter<POSTS, PostViewHolder> firebaseRecyclerAdaptertwo = new FirebaseRecyclerAdapter<POSTS, PostViewHolder>
                (POSTS.class, R.layout.post_item, PostViewHolder.class,  EventsRef
                ) {
            @Override
            protected void populateViewHolder(PostViewHolder postViewHolder, POSTS posts, int i) {

                String PostKey = getRef(i).getKey();

                postViewHolder.setPostdescription(posts.getPostdescription());
                postViewHolder.setPostimage(getApplicationContext(),posts.getPostimage());
                postViewHolder.setPosttime(posts.getPosttime());
                postViewHolder.setUserprofileimage(getApplicationContext(),posts.getUserprofileimage());
                postViewHolder.setUsername( posts.getUsername());

                postViewHolder.SetLikeStatus(PostKey);







                postViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent ClickPostIntent = new Intent(ForumActivity.this, PostDetailActivity.class);
                        ClickPostIntent.putExtra("PostKey", PostKey);
                        startActivity(ClickPostIntent);

                    }
                });

                postViewHolder.CommentBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent CommentsIntent = new Intent(ForumActivity.this, PostDetailActivity.class);
                        CommentsIntent.putExtra("PostKey", PostKey);
                        startActivity(CommentsIntent);
                    }
                });

                postViewHolder.DeletePost.setVisibility(View.INVISIBLE);
                EventsRef.child(PostKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String UID = snapshot.child("userid").getValue().toString();
                            if (UID.equals(CurrentUID)){
                                postViewHolder.DeletePost.setVisibility(View.VISIBLE);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                postViewHolder.DeletePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        EventsRef.child(PostKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {


                                if (snapshot.exists()){
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("POSTS")
                                            .child(PostKey)
                                            .removeValue();

                                    startActivity(new Intent(getApplicationContext(),MainActivity.class));

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        ;
                    }
                });





                postViewHolder.LikeBtn.setOnClickListener(new View.OnClickListener() {
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










            }
        };

        PostsList.setAdapter(firebaseRecyclerAdaptertwo);

    }
    public static class PostViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        RelativeLayout LikeBtn, CommentBtn;
        TextView LikeNo,CommentNo;
        int CountLikes;
        String currentUserID;
        DatabaseReference LikesRef;
        ImageView LIKEimage,DeletePost;


        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            LikeBtn = (RelativeLayout) mView.findViewById(R.id.likes);
            CommentBtn = (RelativeLayout) mView.findViewById(R.id.comment);
            LikeNo = (TextView) mView.findViewById(R.id.likeNo);
            LIKEimage = (ImageView) mView.findViewById(R.id.like_img);
            DeletePost = mView.findViewById(R.id.cancel2);





            LikesRef = FirebaseDatabase.getInstance().getReference().child("LIKES");
            currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        }
        public void SetLikeStatus(final String PostKey){

            LikesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(PostKey).hasChild(currentUserID)){

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





        public void setPostdescription(String postdescription)
        {
            TextView description = (TextView) mView.findViewById(R.id.textView2);
            description.setText(postdescription);
        }
        public void setPostimage(Context ctx, String postimage) {
            ImageView image = (ImageView) mView.findViewById(R.id.imageView2);
            Picasso.with(ctx).load(postimage).fit().centerCrop().placeholder(R.drawable.not_found).into(image);
        }
        public void setPosttime(String posttime)  {
            TextView time = (TextView) mView.findViewById(R.id.posttime);
            time.setText(posttime);



        }
        public void setUsername(String username){
            TextView name = (TextView) mView.findViewById(R.id.userpostname);
            name.setText(username);

        }
        public void setUserprofileimage( Context ctx1,String userprofileimage){
        CircleImageView image2 = (CircleImageView) mView.findViewById(R.id.circleImageView3);
        Picasso.with(ctx1).load(userprofileimage).fit().centerCrop().into(image2);

        }


    }
}

