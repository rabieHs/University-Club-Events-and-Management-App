package com.vfxf.fvxmob;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ProjectMangmentActivity extends AppCompatActivity {
    private RecyclerView Recycle;
    private DatabaseReference Cat1Ref;
    private String CUID;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_mangment);
        mAuth = FirebaseAuth.getInstance();
        CUID = mAuth.getCurrentUser().getUid();


        Cat1Ref = FirebaseDatabase.getInstance().getReference().child("Category").child("Project Managment");




        Recycle = findViewById(R.id.rv4);


        Recycle.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        Recycle.setLayoutManager(linearLayoutManager);


        Desplay();

    }

    private void Desplay() {
        FirebaseRecyclerAdapter<Category,CategoryViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(Category.class,R.layout.userinfodp_item,CategoryViewHolder.class, Cat1Ref) {
            @Override
            protected void populateViewHolder(CategoryViewHolder categoryViewHolder, Category category, int i) {
                categoryViewHolder.setProfilepc(getApplicationContext(),category.getProfilepc());
                categoryViewHolder.setUsername(category.getUsername());
                categoryViewHolder.setProperty(category.getProperty());

            }
        };Recycle.setAdapter(firebaseRecyclerAdapter);


    }
    public static class CategoryViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

        }
        public void setProfilepc(Context ctx, String profilepc){
            ImageView image = (ImageView) mView.findViewById(R.id.dppfpc);
            Picasso.with(ctx).load(profilepc).into(image);


        }
        public void setUsername(String username) {
            TextView name = (TextView) mView.findViewById(R.id.dpun);
            name.setText(username);

        }
        public void setProperty(String property){
            TextView prop = (TextView) mView.findViewById(R.id.Prop);
            prop.setText(property);

        }


    }
}