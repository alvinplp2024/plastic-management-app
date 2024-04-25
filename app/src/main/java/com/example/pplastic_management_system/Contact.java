package com.example.pplastic_management_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class Contact extends AppCompatActivity {
    EditText message;
    TextView name, email;
    Button submit;
    boolean isNameValid, isEmailValid, isMessageValid;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    public static final String TAG = "TAG";
    TextInputLayout nameError, emailError, messageError;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);
        message = (EditText) findViewById(R.id.message);
        submit = (Button) findViewById(R.id.submit);
        nameError = (TextInputLayout) findViewById(R.id.nameError);
        emailError = (TextInputLayout) findViewById(R.id.emailError);
        messageError = (TextInputLayout) findViewById(R.id.messageError);

        fAuth=FirebaseAuth.getInstance();
        fStore= FirebaseFirestore.getInstance();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetValidation();
            }
        });


        // Fetch user data from Firestore and set full name to EditText
        fetchAndSetFullName();

        
    }
    private void fetchAndSetFullName() {
        // Fetch user data from Firestore
        userID = fAuth.getCurrentUser().getUid();
        DocumentReference userDocument = fStore.collection("users").document(userID);
        userDocument.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String fullName = documentSnapshot.getString("name");
                    String fullEmail = documentSnapshot.getString("email");
                    // Set the retrieved full name and email to the EditText
                    name.setText(fullName);
                    email.setText(fullEmail);
                } else {
                    Log.d(TAG, "No such document");
                    Toast.makeText(getApplicationContext(), "User data does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error retrieving user data: " + e.getMessage());
                Toast.makeText(getApplicationContext(), "Error retrieving user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void SetValidation() {

        // Check for a valid name.
        if (name.getText().toString().isEmpty()) {
            nameError.setError(getResources().getString(R.string.name_error));
            isNameValid = false;
        } else  {
            isNameValid = true;
            nameError.setErrorEnabled(false);
        }

        // Check for a valid email address.
        if (email.getText().toString().isEmpty()) {
            emailError.setError(getResources().getString(R.string.email_error));
            isEmailValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            emailError.setError(getResources().getString(R.string.error_invalid_email));
            isEmailValid = false;
        } else  {
            isEmailValid = true;
            emailError.setErrorEnabled(false);
        }

        // Check for a valid phone number.
        if (message.getText().toString().isEmpty()) {
            messageError.setError(getResources().getString(R.string.phone_error));
            isMessageValid = false;
        } else  {
            isMessageValid = true;
            messageError.setErrorEnabled(false);
        }

        if (isNameValid && isEmailValid && isMessageValid ) {

            String Name = name.getText().toString().trim();
            String Email= email.getText().toString().trim();
            String Message= message.getText().toString().trim();
            // Add status field with value "in progress"
            final String STATUS_IN_PROGRESS = "In progress";
            userID = fAuth.getCurrentUser().getUid();

            //DocumentReference documentReference = fStore.collection("donate").document(userID);
            CollectionReference collectionReference = fStore.collection("contact data");

            Map<String,Object> user = new HashMap<>();
            user.put("timestamp", FieldValue.serverTimestamp());
            user.put("name",Name);
            user.put("email",Email);
            user.put("message",Message);
            user.put("status" ,STATUS_IN_PROGRESS);
            user.put("userid",userID);

            collectionReference.add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getApplicationContext(),"Success!",Toast.LENGTH_SHORT).show();
                            Log.d(TAG,"Successfully! We will shortly revert you back.");
                            //startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            Intent intent = new Intent(Contact.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Error!",Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Error!", e);
                        }
                    });
        }

    }
}
