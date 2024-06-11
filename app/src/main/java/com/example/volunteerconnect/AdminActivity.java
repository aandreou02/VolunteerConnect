package com.example.volunteerconnect;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminActivity extends AppCompatActivity {

    private EditText eventTitle, eventDescription, eventDate, eventCategory;
    private Button submitButton;
    private CircleImageView profileImage;
    private Uri imageURI;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    ImageView imglogout;
    ImageView chatBut,setbut;

    private static final String DEFAULT_IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/volunteerconnect-36739.appspot.com/o/event.png?alt=media&token=2d5ced76-d589-4cfd-8c38-f5bc78f1c29c";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        eventTitle = findViewById(R.id.eventTitle);
        eventDescription = findViewById(R.id.eventDescription);
        eventDate = findViewById(R.id.eventDate);
        eventCategory = findViewById(R.id.eventCategory);
        submitButton = findViewById(R.id.submitButton);
        profileImage = findViewById(R.id.profilerg0);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating Event...");
        progressDialog.setCancelable(false);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("events");
        storageReference = FirebaseStorage.getInstance().getReference().child("event_images");
        chatBut = findViewById(R.id.chatBut);
        setbut = findViewById(R.id.settingBut);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEvent();
            }
        });

        imglogout = findViewById(R.id.logoutimg);

        imglogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(AdminActivity.this,R.style.dialoge);
                dialog.setContentView(R.layout.dialog_layout);
                Button no,yes;
                yes = dialog.findViewById(R.id.yesbnt);
                no = dialog.findViewById(R.id.nobnt);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(AdminActivity.this,login.class);
                        startActivity(intent);
                        finish();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        setbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, setting.class);
                startActivity(intent);
            }
        });

        chatBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK && data != null) {
            imageURI = data.getData();
            profileImage.setImageURI(imageURI);
        }
    }

    private void createEvent() {
        String title = eventTitle.getText().toString();
        String description = eventDescription.getText().toString();
        String date = eventDate.getText().toString();
        String category = eventCategory.getText().toString();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) ||
                TextUtils.isEmpty(date) || TextUtils.isEmpty(category)) {
            Toast.makeText(AdminActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        final String eventId = databaseReference.push().getKey();
        if (imageURI != null) {
            StorageReference imageRef = storageReference.child(eventId + ".jpg");
            imageRef.putFile(imageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                saveEventToDatabase(eventId, title, description, date, category, uri.toString());
                            }
                        });
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(AdminActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            saveEventToDatabase(eventId, title, description, date, category, DEFAULT_IMAGE_URL);
        }
    }

    private void saveEventToDatabase(String eventId, String title, String description, String date, String category, String imageUrl) {
        Event event = new Event(eventId, title, description, date, category, imageUrl);
        databaseReference.child(eventId).setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(AdminActivity.this, "Event created successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                } else {
                    Toast.makeText(AdminActivity.this, "Failed to create event", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void clearFields() {
        eventTitle.setText("");
        eventDescription.setText("");
        eventDate.setText("");
        eventCategory.setText("");
        profileImage.setImageResource(R.drawable.photocamera);
        imageURI = null;
    }
}