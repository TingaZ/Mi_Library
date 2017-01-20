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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BooksActivity extends AppCompatActivity {

    private RecyclerView mBookList;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booklist);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Books");

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
            protected void populateViewHolder(BookViewHolder viewHolder, Book model, int position) {

                viewHolder.setTitle(model.getTitle());
                viewHolder.setAuthor(model.getAuthor());
                viewHolder.setDescription(model.getDesc());
                viewHolder.setImage(getApplicationContext(), model.getImage());

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


    public static class BookViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public BookViewHolder(View itemView) {
            super(itemView);

            mView = itemView ;
        }

        public void setTitle(String title){
            TextView book_title = (TextView) mView.findViewById(R.id.book_title);
            book_title.setText(title);

        }

        public void setAuthor(String author){
            TextView book_author = (TextView) mView.findViewById(R.id.book_author);
            book_author.setText(author);

        }

        public void setDescription(String desc){
            TextView book_desc = (TextView) mView.findViewById(R.id.book_desc);
            book_desc.setText(desc);

        }

        public void setImage(Context c, String image){
            ImageView imageView = (ImageView) mView.findViewById(R.id.book_image);
            Picasso.with(c).load(image).into(imageView);

        }
        /*
        * Methods for setting values for displaying{@ends here}
        * */

    }
}
