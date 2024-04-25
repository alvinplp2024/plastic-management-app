package com.example.pplastic_management_system.ADMIN.REPORT;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.example.pplastic_management_system.ADMIN.AdminDashboard;
import com.example.pplastic_management_system.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import timber.log.Timber;

public class ReportActivity extends AppCompatActivity {

    private final String reportFileName = "report.csv";
    private List<ReportItem> reportData;  // Declare reportData at class level

    private List<ReportItem> report = new ArrayList<>();
    private static final String CHANNEL_IDS = "ReportDownloadNotification";

    private FirebaseFirestore db;
    private TableLayout reportTableLayout;
    private Button downloadButton;
    public static final String TAG = "TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        reportTableLayout = findViewById(R.id.reportTableLayout);
        downloadButton = findViewById(R.id.downloadButton);

        // Query data from Firestore
        db.collection("user data")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Initialize reportData list
                        reportData = new ArrayList<>();

                        // Add header row
                        addHeaderRow();

                        // Iterate through documents
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            ReportItem reportItem = document.toObject(ReportItem.class);
                            reportData.add(reportItem);  // Add item to reportData list
                            addRowToTable(reportItem);
                        }

                        // Log the size of reportData for debugging
                        if (reportData != null) {
                            Timber.tag("ReportActivity").d("Report data size: %s", reportData.size());
                        } else {
                            Timber.tag("ReportActivity").d("Report data is null");
                        }
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle errors
                        Timber.tag("ReportActivity").e(e, "Error fetching data from Firestore");
                    }
                });

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if there is data available for download
                if (reportData != null && !reportData.isEmpty()) {
                    // Generate and save the report file before downloading
                    File reportFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), reportFileName);
                    writeReportToFile(reportFileName);

                    // Initiate the download
                    downloadFile(reportFileName);
                } else {
                    Toast.makeText(ReportActivity.this, "No data available for download", Toast.LENGTH_LONG).show();
                }
            }
        });

        //getReport();
    }

    private void addHeaderRow() {
        TableRow headerRow = new TableRow(this);
        headerRow.setBackgroundColor(Color.LTGRAY);

        addHeaderCell(headerRow, "Document Id");
        addHeaderCell(headerRow, "Name");
        addHeaderCell(headerRow, "Plastic");
        addHeaderCell(headerRow, "Description");
        addHeaderCell(headerRow, "Kilograms");
        addHeaderCell(headerRow, "Amount");
        addHeaderCell(headerRow, "Status");

        reportTableLayout.addView(headerRow);
    }

    private void addHeaderCell(TableRow row, String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(20, 20, 20, 20);
        textView.setGravity(Gravity.CENTER);
        row.addView(textView);

        // Add column divider
        View divider = new View(this);
        divider.setBackgroundColor(Color.BLACK);
        divider.setLayoutParams(new TableRow.LayoutParams(1, TableRow.LayoutParams.MATCH_PARENT));
        row.addView(divider);
    }

    private void addRowToTable(ReportItem reportItem) {
        TableRow row = new TableRow(this);

        addDataCell(row, reportItem.getDocumentId());
        addDataCell(row, reportItem.getName());
        addDataCell(row, reportItem.getPlastic());
        addDataCell(row, reportItem.getDescription());
        addDataCell(row, reportItem.getPhone());
        addDataCell(row, String.valueOf(reportItem.getAmount()));
        addDataCell(row, reportItem.getStatus());

        reportTableLayout.addView(row);

        // Add row divider
        View divider = new View(this);
        divider.setBackgroundColor(Color.BLACK);
        divider.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
        reportTableLayout.addView(divider);
    }

    private void addDataCell(TableRow row, String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(20, 20, 20, 20);
        textView.setGravity(Gravity.CENTER);
        row.addView(textView);

        // Add column divider
        View divider = new View(this);
        divider.setBackgroundColor(Color.BLACK);
        divider.setLayoutParams(new TableRow.LayoutParams(1, TableRow.LayoutParams.MATCH_PARENT));
        row.addView(divider);
    }

    private void writeReportToFile(String fileName) {
        ContentResolver contentResolver = getContentResolver();

        // Define the content values for the new file
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.Files.FileColumns.MIME_TYPE, "text/csv");

        // Insert the new file into MediaStore
        Uri uri = contentResolver.insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), contentValues);

        try {
            if (uri != null) {
                // Open an OutputStream to the newly created file
                OutputStream outputStream = contentResolver.openOutputStream(uri);

                if (outputStream != null) {
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

                    // Write header
                    writer.write("Document Id,Name,Plastic,Description,Phone,Amount,Status");
                    writer.newLine();

                    // Write data
                    for (ReportItem item : reportData) {
                        writer.write(item.getDocumentId() + ",");
                        writer.write(item.getName() + ",");
                        writer.write(item.getPlastic() + ",");
                        writer.write(item.getDescription() + ",");
                        writer.write(item.getPhone() + ",");
                        writer.write(item.getAmount() + ",");
                        writer.write(item.getStatus() + ",");
                        writer.newLine();
                    }

                    // Close the writer
                    writer.close();


                    // Inform the user that the report has been saved
                    Toast.makeText(this, "Report saved successfully", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Inform the user that there was an error creating the file
                Toast.makeText(this, "Failed to create report file", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Inform the user that there was an error writing to the file
            Toast.makeText(this, "Error writing report file", Toast.LENGTH_SHORT).show();
        }
    }




    //NOTIFICATIONS
    void getReport() {
        CollectionReference reportsRef = FirebaseFirestore.getInstance().collection("user data");

        ListenerRegistration listenerRegistration = reportsRef.addSnapshotListener(new com.google.firebase.firestore.EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Timber.tag(TAG).w(error, "Listen failed.");
                    return;
                }

                if (querySnapshot != null) {
                    for (DocumentChange dc : querySnapshot.getDocumentChanges()) {
                        ReportItem reportnotify = dc.getDocument().toObject(ReportItem.class);
                        switch (dc.getType()) {
                            case ADDED:
                                report.add(reportnotify);
                                showNotifications(reportnotify.getContent());
                                break;
                            case MODIFIED:
                                // Find the corresponding report and update it
                                for (int i = 0; i < report.size(); i++) {
                                    if (report.get(i).getId().equals(reportnotify.getId())) {
                                        report.set(i, reportnotify);
                                        // Handle modification
                                        break;
                                    }
                                }
                                break;
                            case REMOVED:
                                // Find the corresponding report and remove it
                                for (int i = 0; i < report.size(); i++) {
                                    if (report.get(i).getId().equals(reportnotify.getId())) {
                                        report.remove(i);
                                        // Handle removal
                                        break;
                                    }
                                }
                                break;
                        }
                    }
                }
            }
        });
    }
//END OF NOTIFICATIONS


    private void downloadFile(String fileName) {
        File reportFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);

        // Check if the file exists before proceeding with the download
        if (reportFile.exists()) {
            // Get the content URI for the file using FileProvider
            Uri fileUri = FileProvider.getUriForFile(this, "com.example.fixeridetest.fileprovider", reportFile);

            try {
                // Insert the file into the MediaStore Downloads directory
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.Downloads.MIME_TYPE, "text/csv");
                contentValues.put(MediaStore.Downloads.IS_PENDING, 1);

                ContentResolver contentResolver = getContentResolver();
                Uri contentUri = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    contentUri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                }

                if (contentUri != null) {
                    // Open an OutputStream to the file content URI
                    OutputStream outputStream = contentResolver.openOutputStream(contentUri);

                    if (outputStream != null) {
                        // Copy the file to the OutputStream
                        FileInputStream inputStream = new FileInputStream(reportFile);
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = inputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }

                        // Close streams
                        inputStream.close();
                        outputStream.close();

                        // Update the file status to complete
                        contentValues.clear();
                        contentValues.put(MediaStore.Downloads.IS_PENDING, 0);
                        contentResolver.update(contentUri, contentValues, null, null);

                        Toast.makeText(this, "File saved to Downloads", Toast.LENGTH_SHORT).show();

                        // Show notification
                        //showNotifications(fileName);

                        // Start AdminDashboard activity
                        Intent exit = new Intent(ReportActivity.this, AdminDashboard.class);
                        startActivity(exit);
                    } else {
                        Toast.makeText(this, "Failed to open output stream", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Failed to insert file into MediaStore", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error saving file to Downloads", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Report Saved To Downloads", Toast.LENGTH_SHORT).show();
        }
    }




    //NOTIFICATIONS
    private void showNotifications(
            String content
    ) {
        // Create an intent to open the downloads directory
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
        intent.setDataAndType(uri, "*/*");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_IDS)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle("REPORT DOWNLOADED")
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Create a notification manager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Check if Android version is Oreo or higher, as notification channels are required from Oreo onward
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Report Notifications";
            String description = "Notifications for report alerts";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_IDS, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        // Show the notification
        notificationManager.notify(0, builder.build());
    }


}
