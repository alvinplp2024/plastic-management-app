package com.example.pplastic_management_system;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Donate extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private int REQUEST_CODE = 11;
    SupportMapFragment mapFragment;
    EditText mFoodItem, mDescription, mPhone;
    TextView mFullName;
    Button mSubmitBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    public static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        mFullName = findViewById(R.id.donorname);
        mFoodItem = findViewById(R.id.fooditem);
        mPhone = findViewById(R.id.phone);
        mDescription = findViewById(R.id.description);
        mSubmitBtn = findViewById(R.id.submit);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mapFragment.getMapAsync(this);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }

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
                    // Set the retrieved full name to the EditText
                    mFullName.setText(fullName);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        buildGoogleApiClient();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        mLastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You are here");
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        mMap.addMarker(markerOptions).showInfoWindow();

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname = mFullName.getText().toString().trim();
                String fooditem = mFoodItem.getText().toString().trim();
                String description = mDescription.getText().toString().trim();
                String phone = mPhone.getText().toString().trim();

                //convert to string
                String kgString = mPhone.getText().toString();
                //convert input value to numeric value
                float kgValue = Float.parseFloat(kgString);
                //calculating cost of plastics
                float cost = kgValue * 10;

                String type = "Donor";
                String status = "Not paid";

                if (TextUtils.isEmpty(fullname)) {
                    mFullName.setError("Name is Required.");
                    return;
                }

                if (TextUtils.isEmpty(fooditem)) {
                    mFoodItem.setError("Required.");
                    return;
                }

                phone.length();

                userID = fAuth.getCurrentUser().getUid();
                CollectionReference collectionReference = fStore.collection("user data");

                GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                Map<String, Object> user = new HashMap<>();
                user.put("timestamp", FieldValue.serverTimestamp());
                user.put("name", fullname);
                user.put("plastic", fooditem);
                user.put("phone", phone);
                user.put("description", description);
                user.put("location", geoPoint);
                user.put("userid", userID);
                user.put("type", type);
                user.put("amount", cost);
                user.put("status", status);

                collectionReference.add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Success!");
                                // Get the document ID
                                String documentId = documentReference.getId();
                                // Update the document to include the document ID
                                documentReference.update("documentId", documentId)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "Document ID updated successfully!");
                                                // You can perform any additional actions here
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error updating document ID", e);
                                            }
                                        });
                                // Proceed to the MainActivity
                                Intent intent = new Intent(Donate.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                                Log.w(TAG, "Error!", e);
                            }
                        });
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0]  == PackageManager.PERMISSION_GRANTED){
                mapFragment.getMapAsync(this);
            }else{
                Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}