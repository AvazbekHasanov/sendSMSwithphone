package com.example.verificationauth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private TextView header_title;
    private Button confirm_button;
    private TextView code;
    private EditText email_address;

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
        email_address = findViewById(R.id.email_address);

        confirm_button.setOnClickListener(v -> {
            String codeValue = code.getText().toString();
            String email = email_address.getText().toString();

            // Log for debugging
            Log.d("EmailApp", "Email: " + email + ", Code: " + codeValue);

            if (!email.isEmpty()) {
                confirm_button.setText("Confirming");
                email_address.setAlpha(0.5f);
                email_address.setEnabled(false);
                email_address.setFocusable(false);
                code.setVisibility(View.VISIBLE);
            }

            if (!codeValue.isEmpty()) {
                header_title.setText("Confirmation code: " + codeValue);
                code.setVisibility(View.GONE);
                sendEmail(email, "Your confirmation code is: " + codeValue);
            }
        });
    }

    private void sendEmail(String emailAddress, String message) {
        // Create an email intent with ACTION_SEND
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822"); // This ensures that only email clients handle this intent
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Confirmation Code");
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            // Start the email client
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        } catch (android.content.ActivityNotFoundException ex) {
            header_title.setText("No email client installed.");
        }
    }
}
