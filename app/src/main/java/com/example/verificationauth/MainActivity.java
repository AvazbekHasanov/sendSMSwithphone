package com.example.verificationauth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private TextView header_title;
    private Button confirm_button;
    private TextView code;
    private EditText phone_number;
    private static final int SMS_PERMISSION_CODE = 101; // Request code for SMS permission

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        header_title = findViewById(R.id.header_title);
        confirm_button = findViewById(R.id.confirm_button);
        code = findViewById(R.id.code);
        phone_number = findViewById(R.id.phone_number);

        // Check SMS permission at startup
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        }

        confirm_button.setOnClickListener(v -> {
            String codeValue = code.getText().toString();
            String phone = phone_number.getText().toString();

            // Log for debugging
            Log.d("SMSApp", "Phone: " + phone + ", Code: " + codeValue);

            if (!phone.isEmpty()) {
                confirm_button.setText("Tasdqilash");
                phone_number.setAlpha(0.5f);
                phone_number.setEnabled(false);
                phone_number.setFocusable(false);
                code.setVisibility(View.VISIBLE);
            }

            if (!codeValue.isEmpty()) {
                header_title.setText("Tasdiqlash codi: " + codeValue);
                code.setVisibility(View.GONE);
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED) {
                    sendSms(phone, "Your confirmation code is: " + codeValue);
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
                }
            }
        });
    }


    private void sendSms(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String codeValue = code.getText().toString();
                String phone = phone_number.getText().toString();
                sendSms(phone, "Your confirmation code is: " + codeValue);
            } else {
                // Permission denied
                header_title.setText("SMS permission denied.");
            }
        }
    }
}