package com.zybooks.cs360projectone;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel for the inventory screen.
 *
 * <p><b>Enhancement (CS 499 – Software Engineering):</b> Introduced MVVM pattern,
 * connecting SecondFragment to AppDbHelper via LiveData.</p>
 *
 * <p><b>Enhancement (CS 499 – Algorithms):</b> Added search/sort state driving
 * SQL-level filtering and ordering; added O(n) low-stock detection.</p>
 *
 * <p><b>Enhancement (CS 499 – Databases):</b> All database operations are now
 * executed on a dedicated background thread via a single-threaded
 * {@link ExecutorService}. Previously, every call to {@code readAllItems},
 * {@code insertItem}, and {@code deleteItem} blocked the Android main
 * (UI) thread. SQLite I/O, while fast for small datasets, can cause
 * Application Not Responding (ANR) errors on slower devices or when the
 * database grows. Offloading these calls to a background thread and using
 * {@link MutableLiveData#postValue} — which safely crosses thread boundaries
 * — keeps the UI responsive at all times.</p>
 */
public class InventoryViewModel extends AndroidViewModel {

    /**
     * Items with a quantity at or below this value are considered low on stock.
     * Single definition used by the ViewModel, Fragment, and GridAdapter.
     */
    public static final int LOW_STOCK_THRESHOLD = 5;

    /** Database access layer. */
    private final AppDbHelper db;

    /**
     * Single-threaded executor for all database operations.
     * Using one thread prevents concurrent writes from racing each other.
     * Shut down in {@link #onCleared()} to avoid leaking the thread.
     */
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /** Observable stream of inventory items exposed to the Fragment. */
    private final MutableLiveData<List<Item>> itemsLiveData = new MutableLiveData<>();

    /** Current keyword filter. Empty string = no filter. */
    private String currentQuery = "";

    /** Current sort order. One of the SORT_* constants in AppDbHelper. */
    private String currentSort = AppDbHelper.SORT_DEFAULT;

    public InventoryViewModel(Application application) {
        super(application);
        db = new AppDbHelper(application);
        loadItems();
    }

    /**
     * Returns the observable list of inventory items.
     *
     * @return LiveData wrapping the current filtered and sorted item list.
     */
    public LiveData<List<Item>> getItems() {
        return itemsLiveData;
    }

    /**
     * Reloads items from the database on a background thread.
     *
     * <p><b>Enhancement (CS 499 – Databases):</b> The database read is
     * submitted to the {@link ExecutorService} so the main thread is never
     * blocked. {@code postValue} is used instead of {@code setValue} because
     * this method may be called from the background executor thread.</p>
     */
    public void loadItems() {
        executor.execute(() -> {
            List<Item> items = db.readAllItems(currentQuery, currentSort);
            itemsLiveData.postValue(items); // thread-safe LiveData update
        });
    }

    /**
     * Updates the keyword search filter and reloads the item list.
     *
     * @param query The search keyword; pass empty string to clear the filter.
     */
    public void setSearch(String query) {
        currentQuery = (query != null) ? query.trim() : "";
        loadItems();
    }

    /**
     * Updates the sort order and reloads the item list.
     *
     * @param sortOrder One of the SORT_* constants in AppDbHelper.
     */
    public void setSort(String sortOrder) {
        currentSort = (sortOrder != null) ? sortOrder : AppDbHelper.SORT_DEFAULT;
        loadItems();
    }

    /**
     * Low-stock detection — O(n) linear pass over the current loaded list.
     *
     * @return Count of items at or below LOW_STOCK_THRESHOLD.
     */
    public int countLowStockItems() {
        List<Item> current = itemsLiveData.getValue();
        if (current == null) return 0;
        int count = 0;
        for (Item item : current) {
            if (item.qty <= LOW_STOCK_THRESHOLD) count++;
        }
        return count;
    }

    /**
     * Inserts a new item on a background thread, then reloads.
     *
     * @param title  Item name.
     * @param detail Optional description.
     * @param qty    Initial quantity.
     */
    public void addItem(String title, String detail, int qty) {
        executor.execute(() -> {
            db.insertItem(title, detail, qty);
            List<Item> items = db.readAllItems(currentQuery, currentSort);
            itemsLiveData.postValue(items);
        });
    }

    /**
     * Deletes an item by ID on a background thread, then reloads.
     *
     * @param id The item's database row ID.
     */
    public void deleteItem(long id) {
        executor.execute(() -> {
            db.deleteItem(id);
            List<Item> items = db.readAllItems(currentQuery, currentSort);
            itemsLiveData.postValue(items);
        });
    }

    /**
     * Called when the ViewModel is about to be destroyed.
     * Shuts down the executor to prevent thread leaks.
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
