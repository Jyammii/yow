package com.example.closetifiy_finalproject;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class ItemDetailed extends AppCompatActivity {

    private ImageView imageView;
    private Button btnCategory, btnSubcategory, btnColors, btnSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.itemdetail);

        // Find views
        imageView = findViewById(R.id.imageView);
        btnCategory = findViewById(R.id.btncategory);
        btnSubcategory = findViewById(R.id.btnsubcategory);
        btnColors = findViewById(R.id.btncolors);
        btnSize = findViewById(R.id.btnsize);
        Button backButton = findViewById(R.id.back);

        // Back button
        backButton.setOnClickListener(v -> finish());

        // Get the data passed from FragmentItems
        Uri imageUri = Uri.parse(getIntent().getStringExtra("imageUri"));
        int position = getIntent().getIntExtra("position", -1);

        // Load the image and details
        imageView.setImageURI(imageUri);
        loadDetails(position);
    }

    private void loadDetails(int position) {
        // Load the actual details based on the position
        btnCategory.setText(NewItem.getCategory(position));
        btnSubcategory.setText( NewItem.getSubcategory(position));
        btnColors.setText( NewItem.getItemColor(position));
        btnSize.setText( NewItem.getSize(position));

        // Disable buttons to make them non-clickable
        btnCategory.setClickable(false);
        btnSubcategory.setClickable(false);
        btnColors.setClickable(false);
        btnSize.setClickable(false);
    }
}