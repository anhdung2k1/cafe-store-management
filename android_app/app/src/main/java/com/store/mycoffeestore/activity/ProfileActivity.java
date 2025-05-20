package com.store.mycoffeestore.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.store.mycoffeestore.R;
import com.store.mycoffeestore.adapter.ProfileAdapter;
import com.store.mycoffeestore.api.ApiClient;
import com.store.mycoffeestore.api.ApiService;
import com.store.mycoffeestore.helper.NavigationHelper;
import com.store.mycoffeestore.helper.TokenManager;
import com.store.mycoffeestore.model.Users;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private RecyclerView profileRecycler;
    private final List<Object[]> profileItems = new ArrayList<>();
    private Long userId;
    private Users currentUser;

    private ImageView avatarImage;

    // ✅ ActivityResultLauncher để chọn ảnh
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        avatarImage.setImageURI(selectedImageUri);
                        Toast.makeText(this, "Avatar updated locally", Toast.LENGTH_SHORT).show();
                        // TODO: Gửi URI này lên server nếu cần
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileRecycler = findViewById(R.id.profileRecycler);
        avatarImage = findViewById(R.id.avatar_image);

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        NavigationHelper.setupBottomNavigation(this, nav, R.id.profile_btn);

        getUserIdAndLoadProfile();

        // ✅ Gắn sự kiện click vào nút edit avatar
        findViewById(R.id.edit_avatar_btn).setOnClickListener(v -> openImagePicker());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void getUserIdAndLoadProfile() {
        TokenManager tokenManager = new TokenManager(this);
        String userName = tokenManager.getUserNameFromToken();

        if (userName == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = ApiClient.getSecuredApiService(this);
        api.getUserIdByUserName(userName).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Long>> call, @NonNull Response<Map<String, Long>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userId = response.body().get("id");
                    loadUserProfile(userId);
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to get user ID", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Long>> call, @NonNull Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserProfile(Long userId) {
        ApiService api = ApiClient.getSecuredApiService(this);
        api.getUserById(userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> user = response.body();

                    String userName = (String) user.getOrDefault("userName", "Unknown");
                    String address = (String) user.getOrDefault("address", "");
                    String birthDay = (String) user.getOrDefault("birthDay", "");
                    String gender = (String) user.getOrDefault("gender", "");

                    currentUser = new Users();
                    currentUser.setUserName(userName);
                    currentUser.setAddress(address);
                    currentUser.setBirthDay(birthDay);
                    currentUser.setGender(gender);
                    currentUser.setImageUrl((String) user.getOrDefault("imageUrl", ""));

                    profileItems.clear();
                    profileItems.add(new Object[]{"Username", userName, R.drawable.ic_person, false});
                    profileItems.add(new Object[]{"Address", address, R.drawable.ic_home, false});
                    profileItems.add(new Object[]{"Birthday", birthDay, R.drawable.ic_calendar, false});
                    profileItems.add(new Object[]{"Gender", gender, R.drawable.ic_person, false});
                    profileItems.add(new Object[]{"Logout", "", R.drawable.ic_logout, true});

                    setupProfileAdapter();
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error loading profile: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupProfileAdapter() {
        ProfileAdapter adapter = new ProfileAdapter(this, profileItems, (title, newValue) -> {
            if ("Logout".equals(title)) {
                new TokenManager(ProfileActivity.this).clearSession();
                Intent intent = new Intent(ProfileActivity.this, SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                updateLocalUser(title, newValue);
                updateUserOnServer();
            }
        });

        profileRecycler.setLayoutManager(new LinearLayoutManager(this));
        profileRecycler.setAdapter(adapter);
    }

    private void updateLocalUser(String field, String value) {
        if (currentUser == null) return;

        switch (field) {
            case "Address":
                currentUser.setAddress(value);
                break;
            case "Birthday":
                currentUser.setBirthDay(value);
                break;
            case "Gender":
                currentUser.setGender(value);
                break;
            default:
                break;
        }
    }

    private void updateUserOnServer() {
        ApiService api = ApiClient.getSecuredApiService(this);
        api.updateUser(userId, currentUser).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Users> call, @NonNull Response<Users> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Users> call, @NonNull Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error updating user: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}