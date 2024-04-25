package com.example.pplastic_management_system.ADMIN;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pplastic_management_system.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewUsers extends AppCompatActivity {

    private ListView listViewUsers;
    private ArrayList<String> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);

        listViewUsers = findViewById(R.id.list_view_users);
        usersList = new ArrayList<>();

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item_user, usersList);
        listViewUsers.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        boolean firstItem = true;
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String name = documentSnapshot.getString("name");
                            String email = documentSnapshot.getString("email");
                            String phone = documentSnapshot.getString("phone");

                            // Add a small empty space between each item using a newline character
                            if (!firstItem) {
                                usersList.add("");
                            } else {
                                firstItem = false;
                            }

                            usersList.add("Name: " + name + "\nEmail: " + email + "\nPhone: " + phone);
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ViewUsers.this, "Failed to retrieve users.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
