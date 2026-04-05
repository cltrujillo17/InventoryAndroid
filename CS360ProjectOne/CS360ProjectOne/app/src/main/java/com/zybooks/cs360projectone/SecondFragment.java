package com.zybooks.cs360projectone;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Inventory grid screen — displays all inventory items in a two-column card grid.
 *
 * <p>Enhancement (CS 499 – Software Engineering): Refactored to MVVM pattern;
 * all data operations are delegated to {@link InventoryViewModel}.</p>
 *
 * <p>Enhancement (CS 499 – Algorithms and Data Structures): Added real-time
 * search filtering and sort controls:
 * <ul>
 *   <li>A {@link TextWatcher} on the search field calls
 *       {@link InventoryViewModel#setSearch(String)} on every keystroke,
 *       driving a SQL {@code LIKE} query at the database layer.</li>
 *   <li>A {@link Spinner} offers four sort options (newest, A-Z, qty low→high,
 *       qty high→low), each mapping to a validated {@code ORDER BY} constant
 *       in {@link AppDbHelper}.</li>
 *   <li>After every LiveData update the low-stock count is recomputed and
 *       displayed in a warning banner, giving users immediate visibility into
 *       items that need restocking.</li>
 * </ul>
 * </p>
 */
public class SecondFragment extends Fragment {

    /** Number of columns in the inventory grid. Named constant avoids magic numbers. */
    private static final int GRID_COLUMNS = 2;

    /** Sort option labels shown in the Spinner — order must match SORT_KEYS. */
    private static final String[] SORT_LABELS = {
            "Newest First",
            "Name (A \u2192 Z)",
            "Qty: Low \u2192 High",
            "Qty: High \u2192 Low"
    };

    /** AppDbHelper sort constants parallel to SORT_LABELS. */
    private static final String[] SORT_KEYS = {
            AppDbHelper.SORT_DEFAULT,
            AppDbHelper.SORT_NAME_ASC,
            AppDbHelper.SORT_QTY_ASC,
            AppDbHelper.SORT_QTY_DESC
    };

    private InventoryViewModel viewModel;
    private GridAdapter adapter;

    public SecondFragment() {
        super(R.layout.fragment_second);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ---- ViewModel ----
        viewModel = new ViewModelProvider(this).get(InventoryViewModel.class);

        // ---- Bind views ----
        EditText  searchBox    = view.findViewById(R.id.inputSearch);
        Spinner   sortSpinner  = view.findViewById(R.id.spinnerSort);
        TextView  lowStockBanner = view.findViewById(R.id.tvLowStockWarning);
        RecyclerView recycler  = view.findViewById(R.id.recyclerGrid);
        EditText  inputTitle   = view.findViewById(R.id.inputNewTitle);
        EditText  inputDetail  = view.findViewById(R.id.inputNewDetail);
        EditText  inputQty     = view.findViewById(R.id.inputNewQty);
        Button    btnAdd       = view.findViewById(R.id.btnAddRow);

        // ---- RecyclerView ----
        recycler.setLayoutManager(new GridLayoutManager(requireContext(), GRID_COLUMNS));
        adapter = new GridAdapter(position -> { /* positional deletion unused */ });
        adapter.setOnDeleteById(itemId -> viewModel.deleteItem(itemId));
        recycler.setAdapter(adapter);

        // ---- Search — TextWatcher drives SQL LIKE on every keystroke ----
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSearch(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // ---- Sort spinner ----
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, SORT_LABELS);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(spinnerAdapter);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
                viewModel.setSort(SORT_KEYS[pos]);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // ---- Observe LiveData — update grid and low-stock banner ----
        viewModel.getItems().observe(getViewLifecycleOwner(), items -> {
            adapter.submitList(items);

            // Low-stock detection algorithm result drives the warning banner
            int lowCount = viewModel.countLowStockItems();
            if (lowCount > 0) {
                lowStockBanner.setText(
                        "\u26a0\ufe0f " + lowCount + " item(s) low on stock (\u2264 "
                        + InventoryViewModel.LOW_STOCK_THRESHOLD + ")");
                lowStockBanner.setVisibility(View.VISIBLE);
            } else {
                lowStockBanner.setVisibility(View.GONE);
            }
        });

        // ---- Add button: validate, persist, clear fields ----
        btnAdd.setOnClickListener(v -> {
            String title  = inputTitle.getText().toString().trim();
            String detail = inputDetail.getText().toString().trim();
            String qtyStr = inputQty.getText().toString().trim();

            if (TextUtils.isEmpty(title)) {
                Toast.makeText(requireContext(),
                        "Item name is required.", Toast.LENGTH_SHORT).show();
                return;
            }

            int qty = 0;
            if (!TextUtils.isEmpty(qtyStr)) {
                try {
                    qty = Integer.parseInt(qtyStr);
                    if (qty < 0) {
                        Toast.makeText(requireContext(),
                                "Quantity cannot be negative.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(requireContext(),
                            "Please enter a valid number for quantity.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            viewModel.addItem(title, detail, qty);
            inputTitle.setText("");
            inputDetail.setText("");
            inputQty.setText("");
        });
    }
}
