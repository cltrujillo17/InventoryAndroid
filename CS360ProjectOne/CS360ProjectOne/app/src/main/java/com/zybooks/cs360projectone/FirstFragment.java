package com.zybooks.cs360projectone;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.zybooks.cs360projectone.databinding.FragmentFirstBinding;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Login screen fragment.
 *
 * <p><b>Enhancement (CS 499 – Software Engineering):</b> Removed unauthenticated
 * bypass navigation buttons. All access to protected screens requires login.</p>
 *
 * <p><b>Enhancement (CS 499 – Databases):</b> Database calls (login check and
 * account creation) are now executed on a background thread via
 * {@link ExecutorService}. Previously these ran on the Android main thread,
 * which can trigger Application Not Responding (ANR) errors if the database
 * is slow. UI updates after the background operation use
 * {@link android.app.Activity#runOnUiThread} to safely return to the main
 * thread before modifying views.</p>
 */
public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private AppDbHelper db;

    /**
     * Single-threaded executor for login and registration DB calls.
     * Shut down in {@link #onDestroyView()} to avoid leaking the thread.
     */
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        db = new AppDbHelper(requireContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        // ---- Login ----
        binding.btnLogin.setOnClickListener(v -> {
            String u = binding.inputUsername.getText().toString().trim();
            String p = binding.inputPassword.getText().toString().trim();

            if (u.isEmpty() || p.isEmpty()) {
                toast("Enter username and password");
                return;
            }

            // Disable button while processing to prevent double-tap
            binding.btnLogin.setEnabled(false);

            // Enhancement (CS 499 – Databases): DB check runs on background thread
            executor.execute(() -> {
                boolean ok = db.checkUser(u, p);
                requireActivity().runOnUiThread(() -> {
                    binding.btnLogin.setEnabled(true);
                    if (ok) {
                        toast("Welcome back, " + u + "!");
                        NavHostFragment.findNavController(this)
                                .navigate(R.id.action_FirstFragment_to_SecondFragment);
                    } else {
                        toast("Invalid credentials");
                    }
                });
            });
        });

        // ---- Create Account ----
        binding.btnCreateAccount.setOnClickListener(v -> {
            String u = binding.inputUsername.getText().toString().trim();
            String p = binding.inputPassword.getText().toString().trim();

            if (u.isEmpty() || p.isEmpty()) {
                toast("Choose a username and password");
                return;
            }

            binding.btnCreateAccount.setEnabled(false);

            // Enhancement (CS 499 – Databases): DB write runs on background thread
            executor.execute(() -> {
                boolean exists = db.userExists(u);
                if (exists) {
                    requireActivity().runOnUiThread(() -> {
                        binding.btnCreateAccount.setEnabled(true);
                        toast("Username already taken. Try logging in.");
                    });
                    return;
                }
                boolean created = db.createUser(u, p);
                requireActivity().runOnUiThread(() -> {
                    binding.btnCreateAccount.setEnabled(true);
                    toast(created
                            ? "Account created. You can log in now."
                            : "Could not create account.");
                });
            });
        });

        // Enhancement (CS 499 – Software Engineering): bypass buttons removed —
        // all navigation to authenticated screens requires a valid login.
    }

    private void toast(String msg) {
        android.widget.Toast.makeText(requireContext(), msg,
                android.widget.Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executor.shutdown(); // prevent thread leak
        binding = null;
    }
}
