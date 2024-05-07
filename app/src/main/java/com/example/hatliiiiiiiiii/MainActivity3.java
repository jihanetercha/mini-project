package com.example.hatliiiiiiiiii;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity3 extends AppCompatActivity {

    private GridLayout gridLayout;
    private String specialtyId,groupId,niveouId,sectionId;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        gridLayout = findViewById(R.id.gridLayout);
        Intent intent = getIntent();
        specialtyId = intent.getStringExtra("specialtyId");
        groupId = intent.getStringExtra("groupId");
        niveouId = intent.getStringExtra("niveouId");
        sectionId = intent.getStringExtra("sectionId");
        new FetchTimetableTask().execute();
    }

    private class FetchTimetableTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                String endpointUrl = "https://num.univ-biskra.dz/psp/emploi/section2_public?select_spec="+specialtyId+"&niveau="+niveouId+"&section="+sectionId+"&groupe="+groupId+"&sg=0&langu=fr&sem=2&id_year=2&key=appmob";

                URL url = new URL(endpointUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if (response != null) {
                try {
                    JsonParser parser = new JsonParser();
                    JsonElement element = parser.parse(response);

                    if (element.isJsonArray()) {
                        JsonArray jsonArray = element.getAsJsonArray();

                        for (JsonElement jsonElement : jsonArray) {
                            JsonArray innerArray = jsonElement.getAsJsonArray();
                            int innerArraySize = innerArray.size();
                            if (innerArraySize >= 3) {
                                String model = innerArray.get(8).getAsString();
                                String section = innerArray.get(1).getAsString();
                                String lastItem = innerArray.get(innerArraySize - 1).getAsString();
                                String linkmeet = innerArray.get(innerArraySize - 2).getAsString();
                                String row1 = innerArray.get(12).getAsString();
                                String column1 = innerArray.get(13).getAsString();
                                int row = Integer.parseInt(row1);
                                int column = Integer.parseInt(column1);
                                View view = gridLayout.getChildAt(row * gridLayout.getColumnCount() + column);
                                if (view instanceof TextView) {
                                    TextView textView = (TextView) view;
                                    textView.setText(model + "\n " + section);

                                    // Add OnClickListener here
                                    if (section.equals("(En ligne)")) {
                                        // Google Meet link
                                        final String meetLink = linkmeet;
                                        textView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent meetIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(meetLink));
                                                if (meetIntent.resolveActivity(getPackageManager()) != null) {
                                                    startActivity(meetIntent);
                                                } else {
                                                    Toast.makeText(MainActivity3.this, "No app found to open Google Meet link", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        // Google Maps coordinates
                                        final String coordinates = lastItem;
                                        textView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Uri gmmIntentUri = Uri.parse("geo:" + coordinates);
                                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                                mapIntent.setPackage("com.google.android.apps.maps");
                                                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                                    startActivity(mapIntent);
                                                } else {
                                                    Toast.makeText(MainActivity3.this, "Google Maps app not found", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    TextView newTextView = new TextView(MainActivity3.this);
                                    newTextView.setText(model + "\n" + section);

                                    GridLayout.Spec rowSpec = GridLayout.spec(row);GridLayout.Spec columnSpec = GridLayout.spec(column);
                                    GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);
                                    newTextView.setLayoutParams(params);
                                    gridLayout.addView(newTextView);
                                }
                            }
                        }
                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
            }
        }
    }
}