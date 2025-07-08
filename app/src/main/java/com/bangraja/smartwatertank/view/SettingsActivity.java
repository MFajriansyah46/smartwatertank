package com.bangraja.smartwatertank.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.text.InputType;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bangraja.smartwatertank.R;
import com.bangraja.smartwatertank.controller.AuthController;
import com.bangraja.smartwatertank.controller.CommandController;
import com.bangraja.smartwatertank.model.AuthModel;
import com.bangraja.smartwatertank.model.CommandModel;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private TextView emailView, nameView;
    private ImageView backIcon;
    private CircleImageView imageView;
    private Switch bukaKeranOtomatis;
    private Button logoutBtn;
    private LinearLayout bgProfile, editBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.bread));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.meat));

        emailView = findViewById(R.id.emailView);
        nameView = findViewById(R.id.nameView);
        imageView = findViewById(R.id.imageView);
        bukaKeranOtomatis = findViewById(R.id.bukaKeranOtomatis);
        logoutBtn = findViewById(R.id.logoutBtn);
        backIcon = findViewById(R.id.backIcon);
        bgProfile = findViewById(R.id.bgprofile);
        editBtn = findViewById(R.id.editBtn);

        backIcon.setOnClickListener(v -> onBackPressed());
        logoutBtn.setOnClickListener(view -> new AuthController(new AuthModel()).logout(SettingsActivity.this));

        new AuthController(new AuthModel()).displayUserProfile(emailView, nameView, imageView);
        new CommandController(new CommandModel(), this).autoSwitch(bukaKeranOtomatis, bgProfile);

        editBtn.setOnClickListener(v -> showEditNameDialog());
        loadUserName();
    }

    private void showEditNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ubah Nama");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(nameView.getText().toString()); // prefill nama lama
        builder.setView(input);

        builder.setPositiveButton("Simpan", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                saveUserName(newName);
                nameView.setText(newName);
            }
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void saveUserName(String name) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user_name", name);
        editor.apply();
    }

    private void loadUserName() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String name = prefs.getString("user_name", "Anonim");
        nameView.setText(name);
    }
}
