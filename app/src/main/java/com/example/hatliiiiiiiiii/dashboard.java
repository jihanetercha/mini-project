package com.example.hatliiiiiiiiii;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

//import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class dashboard extends AppCompatActivity {
    private Button map_btn;
    private Button schedule_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        map_btn = findViewById(R.id.map_btn);
        schedule_btn = findViewById(R.id.schedule_btn);

        map_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String location = "جامعة محمد خيضر بسكرة";

                Uri gmmIntentUri = Uri.parse("geo:" + location + "?q=" + Uri.encode(location));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(dashboard.this, "تطبيق Google Maps غير مثبت على الجهاز.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }public void goTot(View view) {
        Intent intent = new Intent(dashboard.this,MainActivity2.class);

        startActivity(intent);
    }
}