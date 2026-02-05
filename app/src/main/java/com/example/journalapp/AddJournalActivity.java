package com.example.journalapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

public class AddJournalActivity extends AppCompatActivity {

    //Widgets
    private Button saveButton;
    private ImageView addPhotoBtn;
    private ProgressBar progressBar;
    private EditText titleEditText;
    private EditText thoughtsEditTest;
    private ImageView imageView;

    //Firebase(FireStore)
    private FirebaseFirestore db=FirebaseFirestore.getInstance();

    private CollectionReference collectionReference=
            db.collection("Journal");
    //Firebase(Storage)
    private StorageReference storageReference;
    //FirebaseAuth
    private String currentUserId;
    private String currentUserName;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //Using Activity Result Launcher

    ActivityResultLauncher<String> mTakePhoto;
    Uri imageUri;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal);


        progressBar=findViewById(R.id.post_progressBar);
        titleEditText=findViewById(R.id.post_title_et);
        thoughtsEditTest=findViewById(R.id.post_Description_et);
        imageView=findViewById(R.id.post_imageView);
        saveButton=findViewById(R.id.post_save_Button);
        addPhotoBtn=findViewById(R.id.postCameraButton);
        progressBar.setVisibility(View.INVISIBLE);




    }

    private void SaveJournal() {

        String title=titleEditText.getText().toString().trim();
        String thought=thoughtsEditTest.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);
        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(thought) && imageUri !=null){


            //the saving path of the image in firebase Storage:
            //................../ journal_images/my_image_202311126789.png

            final StorageReference filePath=storageReference.child("journal_images").child("my_image_"+ Timestamp.now().getSeconds());

            //uploading the image
            filePath.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl=uri.toString();

                                    //Creating a journal object

                                    Journal journal=new Journal();
                                    journal.setTitle(title);
                                    journal.setThoughts(thought);
                                    journal.setUserId(currentUserId);
                                    journal.setUserName(currentUserName);
                                    journal.setImageUrl(imageUrl);
                                    journal.setTimeAdded(new Timestamp(new Date()));


                                    collectionReference.add(journal)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    Intent i =new Intent(AddJournalActivity.this,JournalListActivity.class);
                                                    startActivity(i);
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(AddJournalActivity.this, "Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(AddJournalActivity.this, "Failed!!!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


        //Firebase
        storageReference= FirebaseStorage.getInstance().getReference();
        //Auth
        firebaseAuth=FirebaseAuth.getInstance();

        user=firebaseAuth.getCurrentUser();

        if(user!=null){
            currentUserId=user.getUid();
            db.collection("User").document(user.getUid().toString().trim()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    currentUserName=documentSnapshot.get("user name").toString();
                }
            });


        }


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveJournal();
            }
        });

        mTakePhoto=registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri o) {
                        imageView.setImageURI(o);
                        imageUri=o;
                    }
                }
        );
        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getting image from gallary
                mTakePhoto.launch("image/*");
            }
        });



    }
}