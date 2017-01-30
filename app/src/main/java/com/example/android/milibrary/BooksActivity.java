package com.example.android.milibrary;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BooksActivity extends AppCompatActivity {

    private RecyclerView mBookList;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseLike;

    private FirebaseAuth mAuth;

    private boolean mProcessLike = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booklist);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Books");
        mDatabase.keepSynced(true);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);

        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseLike.keepSynced(true);

        mBookList = (RecyclerView) findViewById(R.id.booklist);
        mBookList.setHasFixedSize(true);
        mBookList.setLayoutManager(new LinearLayoutManager(this));


    }


    @Override
    protected void onStart() {
        super.onStart();

        //Creating an adapter to hold values sequential.
        FirebaseRecyclerAdapter<Book, BookViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Book, BookViewHolder>(

                Book.class,
                R.layout.book_row,
                BookViewHolder.class,
                mDatabase

        ) {
            @Override
            protected void populateViewHolder(BookViewHolder viewHolder, Book model, final int position) {

                final String book_key = getRef(position).getKey();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setAuthor(model.getAuthor());
                viewHolder.setDescription(model.getDesc());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setImage(getApplicationContext(), model.getImage());

                viewHolder.setmLikeBtn(book_key);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

//                        Toast.makeText(BooksActivity.this, "You clicked a toast", Toast.LENGTH_SHORT).show();



                    }
                });

                viewHolder.mLikeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mProcessLike = true;

                        mDatabaseLike.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (mProcessLike) {

                                    if (dataSnapshot.child(book_key).hasChild(mAuth.getCurrentUser().getUid())) {

                                        mDatabaseLike.child(book_key).child(mAuth.getCurrentUser().getUid()).removeValue();

                                        mProcessLike = false;

                                    } else {

                                        mDatabaseLike.child(book_key).child(mAuth.getCurrentUser().getUid()).setValue("RandomValue");

                                        mProcessLike = false;

                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                });

            }
        };

        //Set the Booklist{@RecylerView} to firebaseRecyclerviewAdapter.
        mBookList.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add:
                gotoAdd();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void gotoAdd() {

        startActivity(new Intent(this, AddBooksActivity.class));

    }


    public static class BookViewHolder extends RecyclerView.ViewHolder {

        DatabaseReference mDattabaseLike;
        FirebaseAuth mAuth;

        TextView book_title;

        ImageButton mLikeBtn;

        View mView;

        public BookViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            book_title = (TextView) mView.findViewById(R.id.book_title);
            mLikeBtn = (ImageButton) mView.findViewById(R.id.like_button);

            mDattabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
            mAuth = FirebaseAuth.getInstance();
            mDattabaseLike.keepSynced(true);


            book_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("Books " + "My Title");
                }
            });
        }

        public void setmLikeBtn(final String book_key) {

            mDattabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(book_key).hasChild(mAuth.getCurrentUser().getUid())){

                        mLikeBtn.setImageResource(R.drawable.pink_thumb);

                    }else{

                        mLikeBtn.setImageResource(R.drawable.thumb);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        public void setTitle(String title) {
//            TextView book_title = (TextView) mView.findViewById(R.id.book_title);
            book_title.setText(title);

        }

        public void setAuthor(String author) {
            TextView book_author = (TextView) mView.findViewById(R.id.book_author);
            book_author.setText(author);

        }

        public void setDescription(String desc) {
            TextView book_desc = (TextView) mView.findViewById(R.id.book_desc);
            book_desc.setText(desc);

        }

        public void setUsername(String username) {

            TextView book_username = (TextView) mView.findViewById(R.id.book_username);
            book_username.setText(username);

        }

        public void setImage(final Context c, final String image) {
            final ImageView imageView = (ImageView) mView.findViewById(R.id.book_image);
            Picasso.with(c).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                    Picasso.with(c).load(image).into(imageView);

                }
            });

        }
        /*
        * Methods for setting values for displaying{@ends here}
        * */
    }


}
