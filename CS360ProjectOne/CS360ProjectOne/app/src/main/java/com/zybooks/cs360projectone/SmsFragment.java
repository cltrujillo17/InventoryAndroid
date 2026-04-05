package com.zybooks.cs360projectone;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class SmsFragment extends Fragment {

    private TextView tvState, tvResult;
    private Button btnCheck, btnRequest, btnSendTest;
    private EditText etPhone, etMessage;

    public SmsFragment() { super(R.layout.fragment_sms); }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                updatePermissionState();
                tvResult.setText(isGranted
                        ? "Permission granted. You can send SMS alerts."
                        : "Permission denied. App continues without SMS.");
            });

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_sms, container, false);

        tvState = root.findViewById(R.id.tvPermissionState);
        tvResult = root.findViewById(R.id.tvSmsResult);
        btnCheck = root.findViewById(R.id.btnCheckPermission);
        btnRequest = root.findViewById(R.id.btnRequestPermission);
        btnSendTest = root.findViewById(R.id.btnSendTest);
        etPhone = root.findViewById(R.id.etPhone);
        etMessage = root.findViewById(R.id.etMessage);

        btnCheck.setOnClickListener(v -> updatePermissionState());
        btnRequest.setOnClickListener(v -> requestSmsPermission());
        btnSendTest.setOnClickListener(v -> sendSms());

        updatePermissionState();
        return root;
    }

    private void updatePermissionState() {
        boolean granted = isSmsPermissionGranted();
        tvState.setText(granted ? "Permission status: GRANTED" : "Permission status: NOT GRANTED");
        btnSendTest.setEnabled(granted);
    }

    private boolean isSmsPermissionGranted() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestSmsPermission() {
        if (isSmsPermissionGranted()) {
            tvResult.setText("Already granted.");
            updatePermissionState();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.SEND_SMS);
        }
    }

    private void sendSms() {
        if (!isSmsPermissionGranted()) {
            tvResult.setText("Cannot send: permission not granted.");
            return;
        }
        String phone = etPhone.getText().toString().trim();
        String msg = etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(msg)) {
            tvResult.setText("Enter phone and message.");
            return;
        }

        try {
            // Enhancement (CS 499 – Software Engineering): SmsManager.getDefault()
            // was deprecated in Android 12 (API 31). Since this app targets minSdk 34,
            // the modern context-aware API is used unconditionally.
            SmsManager sms = requireContext().getSystemService(SmsManager.class);
            sms.sendTextMessage(phone, null, msg, null, null);
            tvResult.setText("SMS sent (emulator may not actually deliver).");
        } catch (SecurityException se) {
            tvResult.setText("SecurityException: missing permission.");
        } catch (Exception e) {
            tvResult.setText("Error sending SMS: " + e.getMessage());
        }
    }
}
