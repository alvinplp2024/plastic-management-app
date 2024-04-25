package com.example.pplastic_management_system.ADMIN;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pplastic_management_system.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Complains extends AppCompatActivity {

    private ListView listViewComplaints;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> complaintsList;
    private List<DocumentSnapshot> documentSnapshots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complains);

        listViewComplaints = findViewById(R.id.list_view_complaints);
        complaintsList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, R.layout.item_complains, R.id.text_view_user, complaintsList) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textViewStatus = view.findViewById(R.id.text_view_status);

                if (documentSnapshots != null && position < documentSnapshots.size()) {
                    String status = documentSnapshots.get(position).getString("status");
                    if (status != null) {
                        textViewStatus.setText(status);
                    }
                }
                return view;
            }
        };
        listViewComplaints.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("contact data")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        documentSnapshots = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                            String name = documentSnapshot.getString("name");
                            String email = documentSnapshot.getString("email");
                            String message = documentSnapshot.getString("message");
                            String status = documentSnapshot.getString("status");
                            String complaintInfo = "Name: " + name + "\nEmail: " + email + "\nMessage: " + message;
                            complaintsList.add(complaintInfo);
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Complains.this, "Failed to retrieve complaints.", Toast.LENGTH_SHORT).show();
                        Log.e("ComplaintsActivity", "Failed to retrieve complaints", e);
                    }
                });

        // Set click listener for the respond button
        listViewComplaints.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                if (documentSnapshots != null && position < documentSnapshots.size()) {
                    String docId = documentSnapshots.get(position).getId();
                    DocumentReference docRef = db.collection("contact data").document(docId);

                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String currentStatus = documentSnapshot.getString("status");
                            if ("In progress".equals(currentStatus)) {
                                // Update the status to "Noted" only if the current status is "In progress"
                                docRef.update("status", "Noted")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(Complains.this, "Status updated to 'Noted'", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Complains.this, "Failed to update status.", Toast.LENGTH_SHORT).show();
                                                Log.e("ComplaintsActivity", "Failed to update status", e);
                                            }
                                        });
                            } else {
                                Toast.makeText(Complains.this, "Status is not 'In progress'. Cannot update.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Complains.this, "Failed to retrieve document.", Toast.LENGTH_SHORT).show();
                            Log.e("ComplaintsActivity", "Failed to retrieve document", e);
                        }
                    });
                } else {
                    Log.e("ComplaintsActivity", "Error: documentSnapshots is null or position out of bounds");
                }
            }
        });
    }
}
