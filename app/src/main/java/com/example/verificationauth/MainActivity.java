package com.example.verificationauth;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText inputText;
    private TextView outputText;
    private String lastResult = ""; // Store the last encoded/decoded result

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        inputText = findViewById(R.id.inputText);
        outputText = findViewById(R.id.outputText);
        Button encodeButton = findViewById(R.id.encodeButton);
        Button decodeButton = findViewById(R.id.decodeButton);
        Button moveLetterButton = findViewById(R.id.moveLetterButton);
        ImageView copyButton = findViewById(R.id.imageView);

        // Set click listeners for Base64 encoding/decoding
        encodeButton.setOnClickListener(v -> {
            String input = inputText.getText().toString().trim();
            if (input.isEmpty()) {
                showToast("Please enter text to encode.");
                return;
            }
            String encodedText = encodeBase64(input);
            lastResult = encodedText;
            outputText.setText(encodedText);
        });

        decodeButton.setOnClickListener(v -> {
            String input = inputText.getText().toString().trim();
            if (input.isEmpty()) {
                showToast("Please enter text to decode.");
                return;
            }
            try {
                String decodedText = decodeBase64(input);
                lastResult = decodedText;
                outputText.setText(decodedText);
            } catch (IllegalArgumentException e) {
                showToast("Invalid Base64 input.");
            }
        });

        // Set click listener for "Move Letters"
        moveLetterButton.setOnClickListener(v -> showMoveLetterDialog());

        // Copy result to clipboard
        copyButton.setOnClickListener(v -> {
            if (!lastResult.isEmpty()) {
                copyToClipboard(lastResult);
                showToast("Text copied to clipboard.");
            } else {
                showToast("Nothing to copy.");
            }
        });
    }

    private void showMoveLetterDialog() {
        // Create an EditText for user input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Enter number of characters to move");

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Move Letters")
                .setMessage("How many characters do you want to move?")
                .setView(input)
                .setPositiveButton("OK", (dialog, which) -> {
                    String value = input.getText().toString();
                    if (!value.isEmpty()) {
                        int moveBy = Integer.parseInt(value);
                        String originalText = inputText.getText().toString();
                        if (!originalText.isEmpty()) {
                            String movedText = moveCharacters(originalText, moveBy);
                            outputText.setText(movedText);
                            lastResult = movedText;
                        } else {
                            showToast("Input text is empty!");
                        }
                    } else {
                        showToast("Please enter a valid number!");
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    // Base64 Encoding Method
    private String encodeBase64(String text) {
        return Base64.encodeToString(text.getBytes(), Base64.DEFAULT).trim();
    }

    // Base64 Decoding Method
    private String decodeBase64(String encodedText) {
        byte[] decodedBytes = Base64.decode(encodedText, Base64.DEFAULT);
        return new String(decodedBytes).trim();
    }

    // Move Characters Method
    private String moveCharacters(String text, int moveBy) {
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                int offset = (c - base + moveBy) % 26; // Wrap within alphabet range
                if (offset < 0) offset += 26; // Handle negative wrap
                result.append((char) (base + offset));
            } else {
                result.append(c); // Keep non-letters unchanged
            }
        }
        return result.toString();
    }

    // Copy text to clipboard
    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Encoded/Decoded Text", text);
        clipboard.setPrimaryClip(clip);
    }

    // Show a toast message
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
