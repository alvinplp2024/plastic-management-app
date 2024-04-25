package com.example.pplastic_management_system.ADMIN;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.pplastic_management_system.ADMIN.MPESA.Transaction;
import com.example.pplastic_management_system.ADMIN.REPORT.ReportActivity;
import com.example.pplastic_management_system.Landingpage;
import com.example.pplastic_management_system.R;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboard extends AppCompatActivity {


    CardView donate, logout, foodmap, about, contact, history, report;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);


        donate = findViewById(R.id.cardDonate);
        logout = findViewById(R.id.cardLogout);
        foodmap = findViewById(R.id.cardFoodmap);
        //history = findViewById(R.id.cardHistory);
        about = findViewById(R.id.cardAboutus);
        contact = findViewById(R.id.cardContact);
        report =  findViewById(R.id.cardReport);



        donate.setOnClickListener(new View.OnClickListener ()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ViewUsers.class);
                startActivity(intent);
            }
        });

/*
        history.setOnClickListener(new View.OnClickListener ()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Transaction.class);
                startActivity(intent);
            }
        });
*/


        foodmap.setOnClickListener(new View.OnClickListener ()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SellerLocation.class);
                startActivity(intent);
            }
        });
        about.setOnClickListener(new View.OnClickListener ()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoryAdmin.class);
                startActivity(intent);
            }
        });



        contact.setOnClickListener(new View.OnClickListener ()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Complains.class);
                startActivity(intent);
            }
        });


        report.setOnClickListener(new View.OnClickListener ()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
                startActivity(intent);
            }
        });


        logout.setOnClickListener(new View.OnClickListener ()
        {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(AdminDashboard.this, Landingpage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}