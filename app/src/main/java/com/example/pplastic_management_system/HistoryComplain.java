package com.example.pplastic_management_system;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HistoryComplain extends AppCompatActivity {

    private ListView listViewComplaints;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> complaintsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_complain);

        listViewComplaints = findViewById(R.id.list_view_complaints);
        complaintsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, R.layout.item_my_complain, R.id.text_view_user, complaintsList);
        listViewComplaints.setAdapter(adapter);

        // Initialize Firebase Authentication
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // User is logged in
            String userEmail = currentUser.getEmail();

            // Retrieve complaints only for the logged-in user
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("contact data")
                    .whereEqualTo("email", userEmail)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                //String name = documentSnapshot.getString("name");
                                //String email = documentSnapshot.getString("email");
                                String message = documentSnapshot.getString("message");
                                String status = documentSnapshot.getString("status");
                                String complaintInfo = /* "Name: " + name + "\nEmail: " + email + "\n */"Message: " + message + "\nStatus: " + status;
                                complaintsList.add(complaintInfo);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(HistoryComplain.this, "Failed to retrieve complaints.", Toast.LENGTH_SHORT).show();
                            // Log the error for debugging purposes
                            Log.e("ComplaintsActivity", "Failed to retrieve complaints", e);
                        }
                    });
        } else {
            // User is not logged in
            Toast.makeText(HistoryComplain.this, "User not logged in !!", Toast.LENGTH_SHORT).show();
            // Handle this case according to your application's logic
        }
    }
}
