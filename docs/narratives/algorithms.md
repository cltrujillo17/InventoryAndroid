# CS 499 Milestone Three — Enhancement Two Narrative
## Algorithms and Data Structures

**Name:** Carlos Trujillo
**Course:** CS 499 — Computer Science Capstone
**Date:** April 5, 2026

---

> **Formatting note for Word submission:**
> Double spacing | 12-point Times New Roman | 1-inch margins

---

### Prompt 1: Artifact Description

&nbsp;&nbsp;&nbsp;&nbsp;The artifact selected for this enhancement is the same Android Inventory Management
application used in Milestone Two, originally developed during CS 360: Mobile
Architecture and Programming. The application allows users to log in, manage a
personal inventory of items, and optionally receive low-stock notifications via SMS.
It is written in Java, targets Android API 34, and uses SQLite via the
SQLiteOpenHelper class for local data persistence. The Milestone Two enhancement
refactored the application's architecture to follow the MVVM pattern and, crucially,
connected the inventory screen to the database for the first time. That foundation
made Milestone Three possible: once data was reliably persisted and retrieved,
meaningful search, sort, and analysis algorithms could be added on top of it.

---

### Prompt 2: Justification, Skills, and Improvements

&nbsp;&nbsp;&nbsp;&nbsp;This artifact was selected for the Algorithms and Data Structures category because
the original implementation contained no search, filter, or sort functionality
whatsoever. The inventory was loaded as a flat, unordered list with no way to find
a specific item or identify items that needed restocking. For a real inventory
management tool, this is a critical workflow gap: a warehouse with hundreds of
items and no search capability is effectively unusable. Adding these features
required applying algorithmic thinking at both the database and application layers,
and it demonstrates a meaningful understanding of where different types of
computation are most efficient.

&nbsp;&nbsp;&nbsp;&nbsp;The first algorithmic improvement was a real-time search filter implemented using
a SQL LIKE query. A TextWatcher on the search field calls
InventoryViewModel.setSearch() on every keystroke, which triggers a fresh database
query using a parameterized LIKE clause with wildcard matching on the item title
column. Because the title column is indexed (the index was created in AppDbHelper
during the Milestone Two database enhancement), the LIKE lookup operates sub-linearly
on the stored dataset rather than scanning every row. The use of a parameterized
query — where the user's input is passed as a bound argument rather than
concatenated into the SQL string — also prevents SQL injection, addressing a
potential security vulnerability that would otherwise exist if the search term were
embedded literally in the query string.

&nbsp;&nbsp;&nbsp;&nbsp;The second improvement was a sort capability implemented via SQL ORDER BY. The
inventory screen now includes a Spinner control with four options: newest first,
alphabetical by name, quantity low-to-high, and quantity high-to-low. Each option
maps to a validated string constant defined in AppDbHelper, and the selected constant
is passed to the readAllItems method, which constructs the ORDER BY clause using a
switch statement restricted to known values. This design choice is deliberate: because
the ORDER BY value is assembled from a fixed set of named constants rather than from
user input, there is no risk of injecting arbitrary SQL through the sort control.
Sorting is performed entirely by the SQLite engine rather than by a Java-level
Collections.sort call, which means the sorted result is produced in a single database
pass rather than loading all rows into memory and then sorting them — a more efficient
approach as the dataset grows.

&nbsp;&nbsp;&nbsp;&nbsp;The third improvement is a low-stock detection algorithm in InventoryViewModel. After
every database reload, the countLowStockItems method performs a single O(n) linear
pass over the already-loaded item list and counts items whose quantity is at or below
a defined threshold (currently five units). The result drives two UI elements: a
warning banner at the top of the inventory screen that shows how many items are low
on stock, and a red quantity label on each individual item card — applied by
GridAdapter using the same threshold constant. Because this algorithm runs on the
list that was just loaded from the database, it adds no additional query overhead.
The threshold constant is defined once in InventoryViewModel and referenced from both
the Fragment and the adapter, ensuring the definition is never duplicated.

---

### Prompt 3: Course Outcomes

&nbsp;&nbsp;&nbsp;&nbsp;This enhancement directly addresses Course Outcome 3: designing and evaluating
computing solutions using algorithmic principles and computer science practices while
managing tradeoffs. The tradeoff between in-memory sorting and database-level ORDER BY
was a deliberate design decision that required evaluating the efficiency and scalability
of each approach. The decision to perform search filtering at the SQL layer rather than
filtering a pre-loaded Java list reflects the same reasoning: SQL engines are optimized
for set-based operations on indexed columns, and offloading this work to SQLite avoids
reloading the full dataset into the JVM heap on every keystroke. Course Outcome 4 is
also addressed through the practical implementation of data filtering, sorting, and
scanning techniques using Android's SQLite API, the TextWatcher interface, and the
Spinner component. Course Outcome 5, the security mindset outcome, is supported by the
use of parameterized queries for the search filter, which prevents SQL injection, and
by the controlled construction of ORDER BY clauses from a whitelist of known constants.
No changes to the original outcome-coverage plan are needed.

---

### Prompt 4: Reflection on the Enhancement Process

&nbsp;&nbsp;&nbsp;&nbsp;The most valuable learning from this milestone was developing a clearer sense of
which layer of an application should be responsible for which type of computation.
Early in the program, I would have approached the sort and filter problem by loading
all items from the database into a list and then writing Java code to filter or sort
that list on each user interaction. Working through this enhancement made it concrete
why that approach is inefficient: the database engine is specifically designed to
evaluate WHERE and ORDER BY clauses on indexed data, and asking it to do that work
is almost always faster than pulling everything into memory and processing it in
application code. This principle — that computation should happen as close to the
data as possible — is one that I will carry forward into future work.

&nbsp;&nbsp;&nbsp;&nbsp;The primary challenge in this milestone was maintaining consistency across multiple
components that all needed to know about the same threshold value. The low-stock
threshold appears in the detection algorithm in InventoryViewModel, in the color
logic in GridAdapter, and in the warning banner text in SecondFragment. The solution
was to define the constant once in InventoryViewModel as a public static field and
reference it from all other locations, rather than hardcoding the number in three
separate places. This eliminated the risk of updating the value in one place and
forgetting the others. A secondary challenge was ensuring that search and sort state
survived configuration changes such as screen rotations. Because the state is stored
in the ViewModel rather than in the Fragment, it persists automatically across
configuration changes — a benefit of the MVVM architecture that was first introduced
in Milestone Two and that paid off directly in this milestone.

---

## References

Google LLC. (2024). *SQLite queries in Android (rawQuery).* Android Developers.
&nbsp;&nbsp;&nbsp;&nbsp;https://developer.android.com/training/data-storage/sqlite

Cormen, T. H., Leiserson, C. E., Rivest, R. L., & Stein, C. (2022).
&nbsp;&nbsp;&nbsp;&nbsp;*Introduction to algorithms* (4th ed.). MIT Press.

Google LLC. (2024). *TextWatcher.* Android Developers.
&nbsp;&nbsp;&nbsp;&nbsp;https://developer.android.com/reference/android/text/TextWatcher

OWASP Foundation. (2023). *SQL injection prevention cheat sheet.*
&nbsp;&nbsp;&nbsp;&nbsp;https://cheatsheetseries.owasp.org/cheatsheets/SQL_Injection_Prevention_Cheat_Sheet.html
