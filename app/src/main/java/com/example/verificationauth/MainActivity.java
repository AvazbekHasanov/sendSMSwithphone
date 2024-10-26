package com.example.verificationauth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.net.Uri;



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
            String emails = email_address.getText().toString(); // Single string with comma-separated emails

            // Log for debugging
            Log.d("EmailApp", "Emails: " + emails + ", Code: " + codeValue);

            if (!emails.isEmpty()) {
                confirm_button.setText("Confirming");
                email_address.setAlpha(0.5f);
                email_address.setEnabled(false);
                email_address.setFocusable(false);
                code.setVisibility(View.VISIBLE);
            }

            if (!codeValue.isEmpty()) {
                header_title.setText("Confirmation code: " + codeValue);
                code.setVisibility(View.GONE);

                // Split the comma-separated emails and send them
                sendEmail(emails.split(","), generateEmailMessage(codeValue));
            }
        });
    }

    // Method to generate a beautiful HTML email message
    private String generateEmailMessage(String discountCode) {
        return "<html>" +
                "<body style=\"font-family: Arial, sans-serif; color: #333;\">" +
                "<h2 style=\"color: #4CAF50;\">Exclusive Discount Just for You!</h2>" +
                "<p>Hello,</p>" +
                "<p>We're excited to offer you an exclusive <strong>20% discount</strong> on your next purchase!</p>" +
                "<p>Use the following discount code at checkout to save:</p>" +
                "<h3 style=\"color: #d91616; font-size: 24px;\">" + discountCode + "</h3>" +
                "<p style=\"font-size: 16px;\">Hurry up! This offer is valid for a limited time only.</p>" +
                "<p>Thanks for being a valued customer!</p>" +
                "<p><em>Best regards,</em></p>" +
                "<p><strong>Your Company Name</strong></p>" +
                "</body>" +
                "</html>";
    }


    private void sendEmail(String[] emailAddresses, String message) {
        // Create the intent with ACTION_SENDTO for sending an email
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:")); // Only email apps should handle this

        // Add the recipients and subject
        emailIntent.putExtra(Intent.EXTRA_EMAIL, emailAddresses);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Exclusive Discount Just for You!");

        // Use HTML for the email body content
        emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY)); // Converts the HTML string to a format the email client can understand

        try {
            // Open the email client chooser
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        } catch (android.content.ActivityNotFoundException ex) {
            // Handle error if no email client is installed
        }
    }
}