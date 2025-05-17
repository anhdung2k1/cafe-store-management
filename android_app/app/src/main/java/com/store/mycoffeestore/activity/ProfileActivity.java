package com.store.mycoffeestore.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.store.mycoffeestore.R;
import com.store.mycoffeestore.helper.NavigationHelper;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    // Text views
    private TextView usernameText, emailText, addressText, birthdayText, genderText, passwordText;

    // Edit texts
    private EditText addressEdit, birthdayEdit, genderEdit, passwordEdit;

    // Image buttons
    private ImageView editAddressBtn, editBirthdayBtn, editGenderBtn, editPasswordBtn, togglePasswordBtn;
    private ImageView avatarImageView, editAvatarBtn; // Avatar image and edit button

    // Editing states
    private boolean isEditingAddress = false;
    private boolean isEditingBirthday = false;
    private boolean isEditingGender = false;
    private boolean isEditingPassword = false;

    // Password visibility
    private boolean isPasswordHidden = true;
    private String actualPassword = "my_real_password"; // Simulated password

    private static final int PICK_IMAGE_REQUEST = 1; // Request code for image picker

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        initViews();
        initListeners();
        populateSampleData();

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        NavigationHelper.setupBottomNavigation(this, nav, R.id.profile_btn);
    }

    private void initViews() {
        // Mapping Views
        usernameText = findViewById(R.id.username);
        emailText = findViewById(R.id.email);
        addressText = findViewById(R.id.address);
        birthdayText = findViewById(R.id.birthday);
        genderText = findViewById(R.id.gender);
        passwordText = findViewById(R.id.password);

        addressEdit = findViewById(R.id.address_edit);
        birthdayEdit = findViewById(R.id.birthday_edit);
        genderEdit = findViewById(R.id.gender_edit);
        passwordEdit = findViewById(R.id.password_edit);

        editAddressBtn = findViewById(R.id.edit_address_btn);
        editBirthdayBtn = findViewById(R.id.edit_birthday_btn);
        editGenderBtn = findViewById(R.id.edit_gender_btn);
        editPasswordBtn = findViewById(R.id.edit_password_btn);
        togglePasswordBtn = findViewById(R.id.view_hide_btn);

        // Avatar Views
        avatarImageView = findViewById(R.id.avatar_image);
        editAvatarBtn = findViewById(R.id.edit_avatar_btn);
    }

    private void initListeners() {
        setupEditField(editAddressBtn, addressText, addressEdit, () -> isEditingAddress = !isEditingAddress);
        setupEditField(editBirthdayBtn, birthdayText, birthdayEdit, () -> isEditingBirthday = !isEditingBirthday);
        setupEditField(editGenderBtn, genderText, genderEdit, () -> isEditingGender = !isEditingGender);

        editPasswordBtn.setOnClickListener(v -> togglePasswordEdit());
        togglePasswordBtn.setOnClickListener(v -> togglePasswordVisibility());

        // Handle avatar edit click
        editAvatarBtn.setOnClickListener(v -> openImagePicker());
    }

    private void populateSampleData() {
        usernameText.setText("Quang Nho");
        emailText.setText("123@gmail.com");
        addressText.setText("123 Street, ABC city");
        birthdayText.setText("01 Jan 1990");
        genderText.setText("Male");
        passwordText.setText("********");

        // Set default avatar
        avatarImageView.setImageResource(R.drawable.profile_avatar);
    }

    private void setupEditField(ImageView editBtn, TextView textView, EditText editText, Runnable toggleEditingState) {
        editBtn.setOnClickListener(v -> {
            boolean isEditing = editText.getVisibility() == View.VISIBLE;
            if (!isEditing) {
                editText.setText(textView.getText().toString());
                textView.setVisibility(View.GONE);
                editText.setVisibility(View.VISIBLE);
                editBtn.setImageResource(R.drawable.ic_done);
                editText.requestFocus();
            } else {
                updateTextViewFromEditText(textView, editText);
                textView.setVisibility(View.VISIBLE);
                editText.setVisibility(View.GONE);
                editBtn.setImageResource(R.drawable.ic_edit);
            }
            toggleEditingState.run();
        });
    }

    private void updateTextViewFromEditText(TextView textView, EditText editText) {
        String updatedText = editText.getText().toString().trim();
        textView.setText(updatedText);
        Toast.makeText(this, "Saved: " + updatedText, Toast.LENGTH_SHORT).show();
    }

    private void togglePasswordVisibility() {
        if (!isEditingPassword) {
            if (isPasswordHidden) {
                passwordText.setText(actualPassword);
                togglePasswordBtn.setImageResource(R.drawable.ic_view);
            } else {
                passwordText.setText("********");
                togglePasswordBtn.setImageResource(R.drawable.ic_hide);
            }
            isPasswordHidden = !isPasswordHidden;
        }
    }

    private void togglePasswordEdit() {
        if (!isEditingPassword) {
            passwordEdit.setText(actualPassword);
            passwordText.setVisibility(View.GONE);
            passwordEdit.setVisibility(View.VISIBLE);
            editPasswordBtn.setImageResource(R.drawable.ic_done);
            togglePasswordBtn.setVisibility(View.GONE);
            passwordEdit.requestFocus();
        } else {
            actualPassword = passwordEdit.getText().toString().trim();
            passwordText.setText("********");
            passwordText.setVisibility(View.VISIBLE);
            passwordEdit.setVisibility(View.GONE);
            editPasswordBtn.setImageResource(R.drawable.ic_edit);
            togglePasswordBtn.setVisibility(View.VISIBLE);
            isPasswordHidden = true;
            Toast.makeText(this, "Password changed", Toast.LENGTH_SHORT).show();
        }
        isEditingPassword = !isEditingPassword;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                avatarImageView.setImageBitmap(bitmap); // Set selected image as avatar
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
