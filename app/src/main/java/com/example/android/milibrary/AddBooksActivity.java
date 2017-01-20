package com.example.android.milibrary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddBooksActivity extends AppCompatActivity {

    private final int GALLERY_REQUEST = 1;
    private EditText mBookTitle, mBookAuthor, mBookDescription;
    private ImageButton mSelect;
    private Uri imageUri = null;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_books);

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Books");

        mBookTitle = (EditText) findViewById(R.id.editText_book_title);
        mBookAuthor = (EditText) findViewById(R.id.editText_book_author);
        mBookDescription = (EditText) findViewById(R.id.editText_book_decription);
        mSelect = (ImageButton) findViewById(R.id.imageButton_select);
        mProgress = new ProgressDialog(this);


        /*
        * @Override Method for selecting an image from the device
        * */
        mSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);

                galleryIntent.setType("image/*");

                startActivityForResult(galleryIntent, GALLERY_REQUEST);

            }
        });

    }

    /*
    *@Override Method to get the image after being selected.
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            imageUri = data.getData();

            mSelect.setImageURI(imageUri);

        }

    }


    /*
    * Method used to submit a book after capturing all its credentials.
    * */

    private void submitBook() {

        mProgress.setMessage("Submitting Book Credentials ...");


        final String title_value = mBookTitle.getText().toString().trim();
        final String desc_value = mBookDescription.getText().toString().trim();
        final String author = mBookAuthor.getText().toString().trim();

        if (!TextUtils.isEmpty(title_value) && !TextUtils.isEmpty(desc_value) && imageUri != null) {

            mProgress.show();

            StorageReference filePath = mStorage.child("Book_Images").child(imageUri.getLastPathSegment());

            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    DatabaseReference newPost = mDatabase.push();
                    newPost.child("title").setValue(title_value);
                    newPost.child("author").setValue(author);
                    newPost.child("desc").setValue(desc_value);
                    newPost.child("image").setValue(downloadUrl.toString());

                    mProgress.dismiss();

                    Toast.makeText(AddBooksActivity.this, "Successfully Posted..", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(AddBooksActivity.this, MainActivity.class));

                }
            });

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_done, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_done) {
            submitBook();
        }
        return super.onOptionsItemSelected(item);
    }
}
