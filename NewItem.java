package com.example.closetifiy_finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewItem extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 100;
    private static final int GALLERY_REQUEST = 200;
    private static final String API_KEY = "twcK8Qj8G6DzGNDWcvSqyHX7"; // Your remove.bg API key

    private ImageView imageView;
    private Uri imageUri; // Store the selected image URI

    private static final Map<String, List<String>> categoryMap = new HashMap<>();
    private static final List<String> defaultSizes = new ArrayList<>();
    private static final List<String> shoeSizes = new ArrayList<>();
    private static final List<String> colors = new ArrayList<>();

    private String selectedCategory = "Not available";
    private String selectedSubcategory = "Not available";
    private String selectedColor = "Not available";
    private String selectedSize = "Not available";

    private static final ArrayList<Uri> imageUris = new ArrayList<>(); // List to store image URIs
    private static final ArrayList<String> categories = new ArrayList<>(); // List to store categories
    private static final ArrayList<String> subcategories = new ArrayList<>(); // List to store subcategories
    private static final ArrayList<String> itemColors = new ArrayList<>(); // List to store colors
    private static final ArrayList<String> sizes = new ArrayList<>(); // List to store sizes

    static {
        // Initialize category map
        categoryMap.put("Tops", List.of("Croptops", "Oneshoulders", "Off-shoulders", "T-Shirt", "Hoodies",
                "Strapless", "Sweatshirt", "Sport", "Shirts", "Suit", "Polo",
                "Classy", "Street", "Cardigans", "Blazer"));
        categoryMap.put("Bottoms", List.of("Skirt", "Pants", "Jeans", "Shorts", "Leggings", "Sweatpants",
                "Suit", "Sport", "Classy"));
        categoryMap.put("One Pieces", List.of("Jumpsuits", "Playsuits", "Overalls"));
        categoryMap.put("Accessories", List.of("Watch", "Jewelry", "Belt", "Hat", "Sunglasses", "Bags",
                "Purse", "Scarfs", "Perfume", "Earrings", "Necklace",
                "Ring", "Bracelet", "Cap", "Tote bag"));
        categoryMap.put("Outwear", List.of("Jacket", "Coat", "Vest", "Cardigans", "Blazer"));
        categoryMap.put("Shoes", List.of("Boots", "Sandals", "High heels", "Sneakers", "Rubberboots",
                "Loafers", "Sport"));
        categoryMap.put("Others", new ArrayList<>());

        // Initialize default sizes
        defaultSizes.addAll(List.of("xxxs", "xxs", "xs", "s", "m", "l", "xl", "xxl", "xxxl"));

        // Initialize shoe sizes
        for (int i = 30; i <= 50; i++) {
            shoeSizes.add(String.valueOf(i));
        }

        // Initialize colors
        colors.addAll(List.of("Red", "Orange", "Yellow", "Green", "Blue", "Purple", "Pink", "Brown",
                "Grey", "Gold", "Silver", "White", "Black", "Beige"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);

        // Find views
        Button backButton = findViewById(R.id.back);
        imageView = findViewById(R.id.imageView);
        Button btnCategory = findViewById(R.id.btncategory);
        Button btnSubcategory = findViewById(R.id.btnsubcategory);
        Button btnColors = findViewById(R.id.btncolors);
        Button btnSize = findViewById(R.id.btnsize);
        Button createItemButton = findViewById(R.id.createItemButton);

        // Back button
        backButton.setOnClickListener(v -> finish());

        // Set click listeners for Buttons
        btnCategory.setOnClickListener(v -> showBottomSheet("Category", new ArrayList<>(categoryMap.keySet()), selected -> {
            selectedCategory = selected;
            btnCategory.setText(selectedCategory);
            btnSubcategory.setText("Add");
            selectedSubcategory = "Not available";
        }));
        btnSubcategory.setOnClickListener(v -> {
            if (!selectedCategory.equals("Not available") && categoryMap.containsKey(selectedCategory)) {
                showBottomSheet("Subcategory", categoryMap.get(selectedCategory), selected -> {
                    selectedSubcategory = selected;
                    btnSubcategory.setText(selectedSubcategory);
                });
            } else {
                Toast.makeText(this, "Please select a category first", Toast.LENGTH_SHORT).show();
            }
        });
        btnColors.setOnClickListener(v -> showBottomSheet("Colors", colors, selected -> {
            selectedColor = selected;
            btnColors.setText(selectedColor);
        }));
        btnSize.setOnClickListener(v -> {
            if ("Shoes".equals(selectedCategory)) {
                showBottomSheet("Size", shoeSizes, selected -> {
                    selectedSize = selected;
                    btnSize.setText(selectedSize);
                });
            } else {
                showBottomSheet("Size", defaultSizes, selected -> {
                    selectedSize = selected;
                    btnSize.setText(selectedSize);
                });
            }
        });

        // Get the image URI passed from the Home activity
        Intent intent = getIntent();
        String imageUriString = intent.getStringExtra("imageUri");
        if (imageUriString != null) {
            imageUri = Uri.parse(imageUriString);
            loadImage(imageUri);
        }

        // Handle create item button
        createItemButton.setOnClickListener(v -> {
            if (imageUri != null) {
                // Add details to the lists
                imageUris.add(imageUri);
                categories.add(selectedCategory);
                subcategories.add(selectedSubcategory);
                itemColors.add(selectedColor);
                sizes.add(selectedSize);

                Toast.makeText(NewItem.this, "Item created successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(NewItem.this, "Please select an image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadImage(Uri uri) {
        try {
            // Load the image from the URI and set it to the ImageView
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
        }
    }

    private void showBottomSheet(String title, List<String> items, BottomSheetAdapter.OnItemClickListener listener) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(sheetView);

        TextView tvTitle = sheetView.findViewById(R.id.tvTitle);
        RecyclerView recyclerView = sheetView.findViewById(R.id.recyclerView);
        Button btnAddYourOwn = sheetView.findViewById(R.id.btnAddYourOwn);

        tvTitle.setText("Select " + title);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        BottomSheetAdapter adapter = new BottomSheetAdapter(items, item -> {
            bottomSheetDialog.dismiss();
            listener.onItemClick(item);
        });
        recyclerView.setAdapter(adapter);

        btnAddYourOwn.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            // Handle add your own functionality
        });

        bottomSheetDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Bitmap originalBitmap = null;

            if (requestCode == CAMERA_REQUEST) {
                originalBitmap = (Bitmap) data.getExtras().get("data");
                imageUri = data.getData();
            } else if (requestCode == GALLERY_REQUEST) {
                try {
                    imageUri = data.getData();
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    originalBitmap = BitmapFactory.decodeStream(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (originalBitmap != null) {
                // Set the image directly to ImageView before background removal
                imageView.setImageBitmap(originalBitmap);

                // Optionally, you can still call removeBackground if you want to process the image further
                removeBackground(originalBitmap);
            } else {
                Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void removeBackground(Bitmap bitmap) {
        new Thread(() -> {
            try {
                // Save image to temporary file
                File imageFile = new File(getCacheDir(), "temp_image.jpg");
                FileOutputStream fos = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();

                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("image_file", "temp_image.jpg",
                                RequestBody.create(MediaType.parse("image/jpeg"), imageFile))
                        .addFormDataPart("size", "auto")
                        .build();

                Request request = new Request.Builder()
                        .url("https://api.remove.bg/v1.0/removebg")
                        .addHeader("X-Api-Key", API_KEY)
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    byte[] imageBytes = response.body().bytes();
                    runOnUiThread(() -> {
                        Bitmap resultBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        imageView.setImageBitmap(resultBitmap); // Update ImageView with result
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(NewItem.this, "Background removal failed", Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(NewItem.this, "Error removing background", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    public static ArrayList<Uri> getImageUris() {
        return imageUris;
    }

    public static String getCategory(int position) {
        if (position >= 0 && position < categories.size()) {
            return categories.get(position);
        } else {
            return "Not available";
        }
    }

    public static String getSubcategory(int position) {
        if (position >= 0 && position < subcategories.size()) {
            return subcategories.get(position);
        } else {
            return "Not available";
        }
    }

    public static String getItemColor(int position) {
        if (position >= 0 && position < itemColors.size()) {
            return itemColors.get(position);
        } else {
            return "Not available";
        }
    }

    public static String getSize(int position) {
        if (position >= 0 && position < sizes.size()) {
            return sizes.get(position);
        } else {
            return "Not available";
        }
    }
}