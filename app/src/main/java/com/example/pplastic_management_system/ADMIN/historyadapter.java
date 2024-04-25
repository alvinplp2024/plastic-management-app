package com.example.pplastic_management_system.ADMIN;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pplastic_management_system.ADMIN.MPESA.Transaction;
import com.example.pplastic_management_system.R;

import java.util.ArrayList;

public class historyadapter extends RecyclerView.Adapter<historyadapter.myviewholder> {
    private ArrayList<modelhistory> datalist;
    private ArrayList<String> documentIds;
    private Context context;


    public historyadapter(Context context, ArrayList<modelhistory> datalist) {
        this.context = context;
        this.datalist = datalist;
        this.documentIds = documentIds;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adminhistory, parent, false);
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        modelhistory currentItem = datalist.get(position);

        holder.tname.setText(currentItem.getName());
        holder.tplastic.setText(currentItem.getPlastic());
        holder.ttype.setText(currentItem.getPhone());
        holder.tdescription.setText(currentItem.getDescription());
        holder.tamount.setText(String.valueOf(currentItem.getAmount())); // Convert Double to String

        // Check if status is "Paid"
        if (currentItem.getStatus().equalsIgnoreCase("paid")) {
            // If status is "Paid", set button visibility to INVISIBLE
            holder.viewTransactionButton.setVisibility(View.INVISIBLE);
        } else {
            // Otherwise, set button visibility to VISIBLE
            holder.viewTransactionButton.setVisibility(View.VISIBLE);
            // Set OnClickListener for the button
            holder.viewTransactionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Navigate to Transaction.java activity
                    Intent intent = new Intent(context, Transaction.class);
                    // Pass the amount and document ID via intent extras
                    intent.putExtra("amount", currentItem.getAmount());
                    intent.putExtra("documentId", currentItem.getDocumentId());
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    class myviewholder extends RecyclerView.ViewHolder {
        TextView tname, ttype, tdescription, tamount, tplastic;
        Button viewTransactionButton; // Add Button variable

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            tname = itemView.findViewById(R.id.name);
            tplastic = itemView.findViewById(R.id.plastictype);
            ttype = itemView.findViewById(R.id.type);
            tdescription = itemView.findViewById(R.id.description);
            tamount = itemView.findViewById(R.id.amount);
            viewTransactionButton = itemView.findViewById(R.id.pay); // Initialize Button
        }
    }
}
