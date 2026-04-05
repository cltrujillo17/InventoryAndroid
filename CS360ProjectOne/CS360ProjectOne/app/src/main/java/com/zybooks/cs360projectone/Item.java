package com.zybooks.cs360projectone;

/**
 * Unified data model representing a single inventory item.
 *
 * <p>Enhancement (CS 499 – Software Engineering): The original {@code Item} class only
 * contained {@code title} and {@code detail} as plain strings. Quantity was
 * incorrectly embedded in the detail string (e.g., "Qty: 12"), making any
 * numeric operation on it fragile and impossible without string parsing.
 * This class corrects that by introducing a proper {@code int qty} field
 * and a {@code long id} field that mirrors the database primary key,
 * enabling safe arithmetic operations and full CRUD via the database.</p>
 *
 * <p>This class also replaces the redundant {@code AppDbHelper.ItemRow} inner
 * class that previously served as a duplicate DTO.</p>
 */
public class Item {

    /** Database primary key. -1 indicates the item has not yet been persisted. */
    public long id;

    /** Human-readable name of the inventory item. */
    public String title;

    /** Optional description or notes for the item. */
    public String detail;

    /** Current stock count. Must be >= 0. */
    public int qty;

    /**
     * Full constructor used when loading an existing item from the database.
     *
     * @param id     The database row ID.
     * @param title  The item name.
     * @param detail Optional description.
     * @param qty    Current stock quantity.
     */
    public Item(long id, String title, String detail, int qty) {
        this.id     = id;
        this.title  = title;
        this.detail = detail;
        this.qty    = qty;
    }

    /**
     * Constructor for a new item that has not yet been inserted into the database.
     * The {@code id} field is set to -1 as a sentinel value.
     *
     * @param title  The item name.
     * @param detail Optional description.
     * @param qty    Initial stock quantity.
     */
    public Item(String title, String detail, int qty) {
        this(-1, title, detail, qty);
    }
}
