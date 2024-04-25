package com.example.pplastic_management_system.ADMIN.MPESA;

import static com.example.pplastic_management_system.ADMIN.MPESA.Constants.BUSINESS_SHORT_CODE;
import static com.example.pplastic_management_system.ADMIN.MPESA.Constants.CALLBACKURL;
import static com.example.pplastic_management_system.ADMIN.MPESA.Constants.PARTYB;
import static com.example.pplastic_management_system.ADMIN.MPESA.Constants.PASSKEY;
import static com.example.pplastic_management_system.ADMIN.MPESA.Constants.TRANSACTION_TYPE;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pplastic_management_system.ADMIN.AdminDashboard;
import com.example.pplastic_management_system.ADMIN.MPESA.model.AccessToken;
import com.example.pplastic_management_system.ADMIN.MPESA.model.STKPush;
import com.example.pplastic_management_system.ADMIN.MPESA.services.DarajaApiClient;
import com.example.pplastic_management_system.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class Transaction extends AppCompatActivity implements View.OnClickListener {

    private DarajaApiClient mApiClient;
    private ProgressDialog mProgressDialog;
    private EditText mAmount;
    private Button mPay;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        ButterKnife.bind(this);

        // Initialize views
        mAmount = findViewById(R.id.etAmounts);
        mPay = findViewById(R.id.btnPays);
        mProgressDialog = new ProgressDialog(this);
        mApiClient = new DarajaApiClient();
        mApiClient.setIsDebug(true); // Set True to enable logging, false to disable.

        // Receive the amount from the intent
        double amounts = getIntent().getDoubleExtra("amount", 0.0);
        userId = getIntent().getStringExtra("documentId");
        //change amount to int
        int amount = (int) amounts;
        // Set the received amount to the EditText
        mAmount.setText(String.valueOf(amount));
        // Set OnClickListener for the Pay button
        mPay.setOnClickListener(this);
        getAccessToken();
    }

    public void getAccessToken() {
        mApiClient.setGetAccessToken(true);
        mApiClient.mpesaService().getAccessToken().enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(@NonNull Call<AccessToken> call, @NonNull Response<AccessToken> response) {
                if (response.isSuccessful()) {
                    mApiClient.setAuthToken(response.body().accessToken);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessToken> call, @NonNull Throwable t) {
                Timber.e(t);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == mPay) {
            String amount = mAmount.getText().toString();
            performSTKPush(amount);
        }
    }

    public void performSTKPush(String amount) {
        mProgressDialog.setMessage("Processing your request");
        mProgressDialog.setTitle("Please Wait...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
        String phone_number = "0746329548";
        String timestamp = Utils.getTimestamp();
        STKPush stkPush = new STKPush(
                BUSINESS_SHORT_CODE,
                Utils.getPassword(BUSINESS_SHORT_CODE, PASSKEY, timestamp),
                timestamp,
                TRANSACTION_TYPE,
                String.valueOf(amount),
                Utils.sanitizePhoneNumber(phone_number),
                PARTYB,
                Utils.sanitizePhoneNumber(phone_number),
                CALLBACKURL,
                "PLASTIC", // Account reference
                "PLASTIC MANAGEMENTS"  // Transaction description
        );

        mApiClient.setGetAccessToken(false);

        // Sending the data to the Mpesa API, remember to remove the logging when in production.
        mApiClient.mpesaService().sendPush(stkPush).enqueue(new Callback<STKPush>() {

            @Override
            public void onResponse(@NonNull Call<STKPush> call, @NonNull Response<STKPush> response) {
                mProgressDialog.dismiss();
                try {
                    if (response.isSuccessful()) {
                        Timber.d("post submitted to API. %s", response.body());

                        // Update the status to "paid" in Firestore
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference docRef = db.collection("user data").document(userId); // Use your collection name and document ID
                        docRef.update("status", "paid")
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Timber.d("Status updated successfully to paid.");
                                        startActivity(new Intent(Transaction.this, AdminDashboard.class)
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Timber.e(e, "Error updating status to paid.");
                                    }
                                });

                    } else {
                        Timber.e("Response %s", response.errorBody().string());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(@NonNull Call<STKPush> call, @NonNull Throwable t) {
                mProgressDialog.dismiss();
                Timber.e(t);
            }
        });
    }
}
