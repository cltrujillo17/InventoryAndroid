package com.zybooks.cs360projectone;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

/**
 * RecyclerView adapter for the inventory grid.
 *
 * <p>Enhancement (CS 499 – Software Engineering): The original adapter held a
 * plain {@code List<Item>} where {@code Item} only had {@code title} and
 * {@code detail} fields — with quantity embedded as a raw string in {@code detail}.
 * Deletion was also driven by list position, which is fragile because positions
 * shift as items are removed.</p>
 *
 * <p>This class has been improved in the following ways:
 * <ul>
 *   <li>Extends {@link ListAdapter} with {@link DiffUtil} instead of plain
 *       {@code RecyclerView.Adapter}, which calculates the minimal set of
 *       changes needed and animates them correctly — more efficient and correct.</li>
 *   <li>Deletion is now driven by the item's stable database {@code id} rather
 *       than its list position, preventing index-out-of-bounds errors when
 *       multiple rapid deletes occur.</li>
 *   <li>Quantity ({@code qty}) is now displayed as a proper integer field,
 *       separate from the description text.</li>
 * </ul>
 * </p>
 */
public class GridAdapter extends ListAdapter<Item, GridAdapter.RowVH> {

    /** Callback fired when the user taps Delete, passing the item's row ID. */
    public interface OnDeleteById {
        void onDelete(long itemId);
    }

    /** Legacy positional delete callback — kept briefly for compatibility. */
    public interface OnDeleteClick {
        void onDelete(int position);
    }

    private OnDeleteById onDeleteById;

    /**
     * {@link DiffUtil} implementation that tells the adapter when two list items
     * represent the same database row and whether their visible content has changed.
     */
    private static final DiffUtil.ItemCallback<Item> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Item>() {
                @Override
                public boolean areItemsTheSame(@NonNull Item a, @NonNull Item b) {
                    // Same database row = same logical item
                    return a.id == b.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull Item a, @NonNull Item b) {
                    // All visible fields must match for the view to be considered unchanged
                    return a.qty == b.qty
                            && a.title.equals(b.title)
                            && a.detail.equals(b.detail);
                }
            };

    /**
     * Creates the adapter. A positional-delete callback is accepted for backward
     * compatibility but is superseded by {@link #setOnDeleteById(OnDeleteById)}.
     *
     * @param ignored Legacy positional delete — not used; deletion is now ID-based.
     */
    public GridAdapter(@NonNull OnDeleteClick ignored) {
        super(DIFF_CALLBACK);
    }

    /**
     * Registers the ID-based delete handler.
     * This must be called before the adapter is attached to a RecyclerView.
     *
     * @param cb Callback receiving the database row ID of the item to delete.
     */
    public void setOnDeleteById(OnDeleteById cb) {
        this.onDeleteById = cb;
    }

    @NonNull
    @Override
    public RowVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);
        return new RowVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RowVH holder, int position) {
        Item item = getItem(position);
        holder.title.setText(item.title);
        holder.subtitle.setText(item.detail);
        holder.qty.setText("Qty: " + item.qty);

        // Enhancement (CS 499 – Algorithms and Data Structures):
        // Low-stock detection — apply a red warning color when the item's quantity
        // is at or below the threshold defined in InventoryViewModel.
        // This gives users an immediate, per-card visual signal without any extra query.
        if (item.qty <= InventoryViewModel.LOW_STOCK_THRESHOLD) {
            holder.qty.setTextColor(0xFFD32F2F); // Material Red 700
        } else {
            holder.qty.setTextColor(0xFF1565C0); // Material Blue 800 (healthy stock)
        }

        holder.delete.setOnClickListener(v -> {
            if (onDeleteById != null) {
                onDeleteById.onDelete(item.id);
            }
        });
    }

    /** ViewHolder caching references to the three visible fields in each card. */
    static class RowVH extends RecyclerView.ViewHolder {
        TextView title, subtitle, qty;
        Button   delete;

        RowVH(@NonNull View itemView) {
            super(itemView);
            title    = itemView.findViewById(R.id.labelTitle);
            subtitle = itemView.findViewById(R.id.labelSubtitle);
            qty      = itemView.findViewById(R.id.labelQty);
            delete   = itemView.findViewById(R.id.btnDeleteRow);
        }
    }
}
