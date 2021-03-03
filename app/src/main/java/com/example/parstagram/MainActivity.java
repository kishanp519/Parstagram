package com.example.parstagram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private EditText etDescription;
    private ImageView ivImage;
    private Button btnCapture, btnPost, btnLogout;
    private final String photoFileName = "photo.jpg";
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etDescription = findViewById(R.id.etDescription);
        btnCapture = findViewById(R.id.btnCapture);
        ivImage = findViewById(R.id.ivImage);
        btnPost = findViewById(R.id.btnPost);
        btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setBackgroundColor(getResources().getColor(R.color.light_red));
        btnLogout.setTextColor(getResources().getColor(R.color.black));
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                goLoginActivity();
            }
        });

        btnCapture.setBackgroundColor(getResources().getColor(R.color.light_blue));
        btnCapture.setTextColor(getResources().getColor(R.color.black));
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchCamera(v);
            }
        });

        // queryPosts();

        btnPost.setBackgroundColor(getResources().getColor(R.color.light_green));
        btnPost.setTextColor(getResources().getColor(R.color.black));
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = etDescription.getText().toString();

                if (description.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Description cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (photoFile == null || ivImage.getDrawable() == null) {
                    Toast.makeText(MainActivity.this, "Image not found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                ParseUser user = ParseUser.getCurrentUser();
                savePost(user, description, photoFile);
            }
        });
    }

    private void goLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "Failed to create directory.");
        }

        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    private void onLaunchCamera(View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);
        Uri fileProvider = FileProvider.getUriForFile(MainActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                ivImage.setImageBitmap(takenImage);
            } else {
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void savePost(ParseUser user, String description, File photoFile) {
        Post post = new Post();
        post.setUser(user);
        post.setDescription(description);
        post.setImage(new ParseFile(photoFile));
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while attempting to save post.", e);
                    Toast.makeText(MainActivity.this, "Unable to save post!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i(TAG, "Post saved successfully.");
                etDescription.setText("");
                ivImage.setImageResource(0);
                Toast.makeText(MainActivity.this, "Post published!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error occurred while getting posts.", e);
                    return;
                }

                for (Post post : posts) {
                    Log.i(TAG, "Post Details: [Username: " + post.getUser().getUsername() + ", Description: " + post.getDescription() + "]");
                }
            }
        });
    }
}