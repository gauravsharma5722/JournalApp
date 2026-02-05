package com.example.journalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class JournalListActivity extends AppCompatActivity {

    //FirebaseAuth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //Firebase Firestore
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=db.collection("Journal");

    //Recycler view
    private RecyclerView recyclerView;

    //Adaptor
    private MyAdaptor myAdaptor;

    //Firebase Storage
    private List<Journal> journalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);

        //firebase Auth

        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();

        //wigets
        recyclerView=findViewById(R.id.recyclerView);
//        recyclerView.setAdapter(myAdaptor);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //post Arraylist
        journalList=new ArrayList<>();

    }

    //2-Adding a menu


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menue,menu);
//        return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId=item.getItemId();
        if(itemId==R.id.action_add) {

            if (user != null && firebaseAuth != null) {
                Intent i = new Intent(JournalListActivity.this,
                        AddJournalActivity.class);
                startActivity(i);
            }
            return true;
        } else if (itemId==R.id.action_signout) {
            if(user!=null && firebaseAuth !=null)
            {
                firebaseAuth.signOut();
                Intent i=new Intent(JournalListActivity.this,MainActivity.class);
                startActivity(i);  // Start MainActivity after sign out
                finish();
            }
            return true;

        }
            return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onStart() {
        super.onStart();
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                //Query snapshot represents a single documt
                //retrived from fireStore Query
                for(QueryDocumentSnapshot journals:queryDocumentSnapshots){
                    Journal journal=journals.toObject(Journal.class);
                    journalList.add(journal);
                }
                //recycler view
                myAdaptor=new MyAdaptor(JournalListActivity.this,journalList);
                recyclerView.setAdapter(myAdaptor);
                myAdaptor.notifyDataSetChanged();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(JournalListActivity.this, "Oop's Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });


    }
}

