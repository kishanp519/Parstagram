package com.example.parstagram.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.parstagram.LoginActivity;
import com.example.parstagram.Post;
import com.example.parstagram.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class ComposeFragment extends Fragment {

    private final String TAG = "ComposeFragment";
    private final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private final String photoFileName = "photo.jpg";
    private EditText etDescription;
    private ImageView ivImage;
    private Button btnCapture, btnPost, btnLogout;
    private File photoFile;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ComposeFragment() {
    }


    public static ComposeFragment newInstance(String param1, String param2) {
        ComposeFragment fragment = new ComposeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        etDescription = view.findViewById(R.id.etDescription);
        btnCapture = view.findViewById(R.id.btnCapture);
        ivImage = view.findViewById(R.id.ivImage);
        btnPost = view.findViewById(R.id.btnPost);
        btnLogout = view.findViewById(R.id.btnLogout);

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

        btnPost.setBackgroundColor(getResources().getColor(R.color.light_green));
        btnPost.setTextColor(getResources().getColor(R.color.black));
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = etDescription.getText().toString();

                if (description.isEmpty()) {
                    Toast.makeText(getContext(), "Description cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (photoFile == null || ivImage.getDrawable() == null) {
                    Toast.makeText(getContext(), "Image not found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                ParseUser user = ParseUser.getCurrentUser();
                savePost(user, description, photoFile);
            }
        });
    }

    private void onLaunchCamera(View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    private void goLoginActivity() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
    }

    private File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "Failed to create directory.");
        }

        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                ivImage.setImageBitmap(takenImage);
            } else {
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), "Unable to save post!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i(TAG, "Post saved successfully.");
                etDescription.setText("");
                ivImage.setImageResource(0);
                Toast.makeText(getContext(), "Post published!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}