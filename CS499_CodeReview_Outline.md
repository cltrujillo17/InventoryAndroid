# CS 499 Milestone One — Code Review Video Outline
**Artifact:** Android Inventory App (CS 360)  
**Video Length:** ~30 minutes  
**Format:** Screen recording with mic (show code in Android Studio)

> **How to use this:** Don't read this word-for-word. Use it as a talking guide.  
> Open the file in each section before you start talking about it.  
> Speak naturally — like you're explaining to a teammate.

---

## INTRO (~2 minutes)

> Open Android Studio. Have the project loaded and ready.

**Say something like:**
"Hi, my name is Carlos, and this is my CS 499 code review for Milestone One.  
The artifact I'm reviewing is an Android Inventory App I built in CS 360.  
It's written in Java, uses Android's Navigation component, SQLite for the database,  
and it covers three features: user login, inventory management, and SMS alerts.  

Today I'm going to walk through the existing code, point out its weaknesses,  
and explain exactly how I plan to improve it across three categories:  
Software Engineering and Design, Algorithms and Data Structures, and Databases.  

Let's start with the project structure."

> **Show the file tree in Android Studio** — point to each file as you name it.

"The app has six Java files:
- `MainActivity` — the shell that hosts navigation
- `FirstFragment` — the login screen
- `SecondFragment` — the inventory grid  
- `GridAdapter` — the RecyclerView adapter
- `Item` — the data model
- `AppDbHelper` — the SQLite database helper
- And one more Fragment, `SmsFragment`, for SMS permission handling"

---

## CATEGORY ONE: Software Engineering and Design (~10 minutes)

### Part A — What the Code Does (2 min)

> Open `FirstFragment.java`

"The login screen is handled by `FirstFragment`. When a user taps Login, it reads  
the username and password fields, calls `AppDbHelper.checkUser()`, and if valid,  
navigates to the inventory screen. There's also a Create Account button that calls  
`AppDbHelper.createUser()`. So the login flow is actually connected to the database  
correctly — users are persisted."

> Open `SecondFragment.java`

"The inventory screen is handled by `SecondFragment`. It displays items in a two-column  
grid using a RecyclerView and GridAdapter. The user can add a new item by typing  
a name and detail, and delete items by pressing a delete button on each card."

> Open `GridAdapter.java`

"The adapter is a standard RecyclerView adapter. It holds a list of `Item` objects,  
binds title and detail text to each card, and handles the delete callback."

> Open `MainActivity.java`

"MainActivity is just the host — it sets up the toolbar and navigation. 
There's not much logic here."

---

### Part B — Code Review Analysis (5 min)

> Keep `SecondFragment.java` open while talking about this section.

#### 🔴 Structure Issues

**1. SecondFragment is disconnected from the database**

"This is the most critical structural problem in the entire app.  
`AppDbHelper` has fully working `insertItem()`, `deleteItem()`, and `readAllItems()` methods —  
but `SecondFragment` never imports or calls it.  
All inventory data is stored in a plain Java `ArrayList` that only exists in memory.  
The moment the user closes the app, every item they added is gone.  
The database exists, but it's never used for inventory. That defeats the whole purpose."

> Point to line 19: `private final ArrayList<Item> items = new ArrayList<>();`
> Point to lines 37–41: the hardcoded seed data

**2. Unauthenticated bypass button**

"There are two buttons on the login screen that skip authentication entirely —  
`btnGoToGrid` and `btnGoToSms` navigate directly to the inventory and SMS screen  
without any login check. This is leftover debug code that should not exist in a  
finished product."

> Open `FirstFragment.java`, point to lines 62–68

**3. Unfinished placeholder in MainActivity**

"The floating action button in MainActivity still has the template placeholder text:  
'Replace with your own action.' This is scaffolding left over from the Android Studio  
project template — it was never implemented."

> Open `MainActivity.java`, point to lines 42–44

**4. Two separate data model classes doing the same job**

"There's an `Item.java` class and an `AppDbHelper.ItemRow` inner class.  
Both are attempting to represent an inventory item, but neither is complete.  
`Item.java` only has `title` and `detail`. `ItemRow` inside `AppDbHelper` has  
`id`, `title`, `detail`, and `qty`. These should be one unified model — having two  
creates confusion and inconsistency."

> Open `Item.java` then `AppDbHelper.java` and scroll to the `ItemRow` class

**5. No MVVM architecture — Fragments handle everything**

"There's no separation of concerns. Each Fragment is doing UI work,  
business logic, and database interaction all in one place.  
This is sometimes called the 'God Fragment' anti-pattern.  
Android's recommended architecture is MVVM — Model, View, ViewModel —  
where the Fragment only handles display and user input, and a ViewModel  
manages the data and business logic."

---

#### 🔴 Documentation Issues

**6. No Javadoc comments on public methods**

"None of the public methods in `AppDbHelper`, `GridAdapter`, or the Fragments  
have Javadoc comments. A maintainer reading this code has no documented explanation  
of what parameters are expected, what the return values mean, or what side effects  
to expect."

**7. The security comment acknowledges but doesn't fix the problem**

> Open `AppDbHelper.java`, point to line 46

"There's a comment on line 46 that says 'storing plain text is OK for demo.'  
Acknowledging a vulnerability in a comment is not the same as addressing it.  
For a production-quality app, this needs to actually be fixed."

---

#### 🔴 Variables Issues

**8. `Item.java` is missing `id` and `qty` fields**

> Open `Item.java`

"The `Item` class only has two fields: `title` and `detail`.  
There's no `id` field to uniquely identify items in the database,  
and there's no `qty` field typed as an integer.  
Instead, quantity is stuffed into the `detail` string — for example: 'Qty: 12'.  
That makes the data model incomplete and incorrect for an inventory application."

**9. Magic number for grid column count**

> Open `SecondFragment.java`, point to line 44

"The grid column count is hardcoded as `2` directly in the code with no named constant.  
This is called a 'magic number' — if someone wanted to change it, they'd have to  
know to look here. It should be a named constant or a resource value."

---

#### 🔴 Defensive Programming Issues

**10. No input length validation**

"The username and password fields in `FirstFragment` accept unlimited input  
with no length restrictions. A malformed or extremely long input could cause  
unexpected behavior in the database layer."

**11. No phone number format validation in SmsFragment**

> Open `SmsFragment.java`, point to lines 81–86

"The `sendSms()` method checks that the phone field is not empty,  
but it doesn't validate that the input is actually a valid phone number.  
Any arbitrary string is passed directly to `SmsManager`, which could crash  
or silently fail."

**12. Deprecated SmsManager API**

> Point to line 89: `SmsManager sms = SmsManager.getDefault();`

"`SmsManager.getDefault()` was deprecated in Android 12.  
The modern approach is to call `context.getSystemService(SmsManager.class)`.  
This won't crash today, but it signals the code hasn't been kept current  
with platform changes."

**13. `onUpgrade` destroys all data**

> Open `AppDbHelper.java`, point to lines 37–40

"If the database version number is ever incremented — for example to add a new table —  
`onUpgrade` immediately drops both tables, destroying all users and inventory data.  
A proper migration should use `ALTER TABLE` to add columns without data loss."

---

### Part C — Planned Enhancement: Software Engineering (3 min)

"Here's what I plan to do to address these issues:

**First**, I will unify the data model. I'll rewrite `Item.java` to include  
`id` as a long, `title` as a String, `detail` as a String, and `qty` as an int.  
One class, correctly typed.

**Second**, I will connect `SecondFragment` to `AppDbHelper` — every add, delete,  
and update operation will be persisted to the SQLite database,  
and the list will be loaded from the database on startup.

**Third**, I will refactor toward an MVVM structure by introducing an `InventoryViewModel`  
that holds the LiveData list of items and handles all database interactions.  
The Fragment will only observe and display — no business logic.

**Fourth**, I will remove the bypass navigation buttons from the login screen  
and clean up the placeholder FAB.

These enhancements demonstrate **Course Outcome 3** — designing and evaluating  
computing solutions using established software engineering practices —  
and **Course Outcome 4** — implementing well-founded techniques and standards  
appropriate to the field."

---

## CATEGORY TWO: Algorithms and Data Structures (~8 minutes)

### Part A — What the Code Does (2 min)

> Open `SecondFragment.java` and `GridAdapter.java`

"From an algorithms and data structure standpoint, the inventory list  
is currently managed with a plain `ArrayList<Item>`.
Items are added to the end with `items.add()`, removed by index with `items.remove()`,  
and the adapter is notified manually with `notifyItemInserted` and `notifyItemRemoved`.

There is no searching, no filtering, and no sorting.  
If a user has 100 items and wants to find 'Apples', they have to scroll through  
every item manually.

The database query in `AppDbHelper.readAllItems()` uses a basic  
`SELECT * FROM items ORDER BY id DESC` — it returns everything, every time,  
with no filtering capability."

> Open `AppDbHelper.java`, point to lines 106–119

---

### Part B — Code Review Analysis (4 min)

#### 🔴 Structure / Efficiency Issues

**1. No search or filter capability**

"There is no search bar and no filter logic anywhere in the app.  
For an inventory app, this is a significant missing feature.  
As the item list grows, the user has no way to quickly find a specific item.  
Every refresh loads the entire table — no pagination, no filtering at the query level."

**2. Quantity is not a computable field**

"Because quantity is stored as part of a plain text string like 'Qty: 6',  
there is no way to perform numeric operations on it.  
For example, you cannot answer questions like:  
- 'Which items are running low on stock?'  
- 'What is the total unit count across all items?'  
You'd have to parse, split, and cast the string every time — which is fragile  
and computationally wasteful."

**3. No sorting**

"Items are always returned in reverse insertion order (`ORDER BY id DESC`).  
There's no ability to sort by name alphabetically, or by quantity  
from lowest to highest — which would be extremely useful for identifying  
items that need to be restocked."

**4. No low-stock detection algorithm**

"The app has no logic to detect when an item's quantity drops to a critical threshold  
and alert the user. This would be the primary business value of an inventory app  
and it's entirely absent."

**5. ArrayList not the right structure for this use case**

"Using a plain ArrayList is fine for a small list, but there's no consideration  
for how the data structure scales. Using a LiveData-backed list from the database  
would be more appropriate and reactive to changes."

---

### Part C — Planned Enhancement: Algorithms and Data Structures (2 min)

"Here's what I plan to build:

**First**, I will add a `SearchView` or search bar above the inventory grid.  
As the user types, the list will filter in real time using a SQL `LIKE` query  
on the `title` column — so only matching items are shown.

**Second**, I will add sort functionality — the user can sort by item name (A to Z)  
or by quantity (low to high or high to low).  
This will be implemented with ORDER BY clauses in the SQL query,  
not by re-sorting the Java list — that's the efficient approach.

**Third**, I will add a low-stock detection algorithm.  
When a user sets a threshold — say, anything with qty below 5 —  
the app will scan the list and visually flag those items,  
and optionally trigger an SMS alert via `SmsFragment`.

These enhancements demonstrate **Course Outcome 3** — evaluating and designing  
computing solutions — and **Course Outcome 4** — using well-founded algorithms  
appropriate to the problem."

---

## CATEGORY THREE: Databases (~8 minutes)

### Part A — What the Code Does (2 min)

> Open `AppDbHelper.java`

"The database layer is handled entirely by `AppDbHelper`, which extends  
Android's `SQLiteOpenHelper`.  

It creates two tables: `users` with a primary key of `username`,  
and `items` with an auto-incrementing integer primary key.  

The class provides full CRUD — create, read, update, delete —  
for both users and items.  
For users: `createUser`, `checkUser`, and `userExists`.  
For items: `insertItem`, `deleteItem`, `updateItemQty`, `updateItem`, and `readAllItems`.  

The login system is connected to this database correctly.  
The inventory system is not — as we discussed."

---

### Part B — Code Review Analysis (4 min)

#### 🔴 Security Issues (Critical)

**1. Passwords stored in plaintext**

> Point to `createUser()`, line 45–52

"This is the most serious security vulnerability in the application.  
Passwords are stored as plain text in the `users` table.  
If the database file is ever extracted from the device —  
which is possible on a rooted phone or in development mode —  
every user's password is exposed in plain readable text.  

The correct approach is to hash the password before storing it,  
using a one-way hashing algorithm like bcrypt or Argon2.  
When the user logs in, you hash what they typed and compare the hashes —  
you never store or compare the raw password."

**2. No SQL injection protection on user inputs... actually, it does use parameterization**

> Point to `checkUser()`, lines 55–62

"I want to give credit where it's due — the queries do use parameterized  
placeholders with `?` arguments, which correctly prevents SQL injection.  
That's a good secure coding practice that's already in place."

---

#### 🔴 Structure / Design Issues

**3. `onUpgrade` is destructive**

> Point to lines 37–40

"When the database version is incremented, `onUpgrade` drops both tables immediately.  
This means every deployed user loses all their data on any app update that  
requires a schema change. A production database migration should use  
`ALTER TABLE` to add new columns without destroying existing data."

**4. No indexes on searchable columns**

"The `items` table has no indexes beyond the primary key.  
When we add search functionality, a `WHERE title LIKE '%query%'` without an index  
will scan every row in the table — which is a full table scan.  
Adding an index on `title` will dramatically improve search performance  
as the data set grows."

**5. No data validation at the database layer**

"There's no validation that a `title` isn't empty before inserting,  
no max length enforcement, and no check that `qty` is non-negative.  
A user could insert an item with a qty of -50 and there's nothing  
in the database layer to reject it. Validation should happen both  
at the UI layer and at the data layer as a safety net."

**6. Database opened on the main thread**

"Every call to `getWritableDatabase()` and `getReadableDatabase()` runs  
synchronously on the main UI thread. For a small local database this usually  
won't cause visible lag, but it's bad practice and can trigger  
'Application Not Responding' warnings on slower devices.  
Proper implementation uses background threads or Room's built-in async support."

**7. The `ItemRow` inner class should not live inside the DB helper**

"The `ItemRow` class at the bottom of `AppDbHelper` is a data transfer object —  
it's a model, not a database concern. Putting model classes inside the database  
helper class violates the single responsibility principle.  
It belongs in its own file as part of the data model layer."

---

### Part C — Planned Enhancement: Databases (2 min)

"Here's what I plan to implement:

**First**, I will add password hashing before storing credentials.  
I'll use Android's MessageDigest with SHA-256 as a minimum,  
with a randomly generated salt per user to prevent rainbow table attacks.  
This directly addresses the security vulnerability.

**Second**, I will fix `onUpgrade` to use proper migration logic —  
adding columns with `ALTER TABLE` and preserving existing user data.

**Third**, I will add a `CREATE INDEX` statement on `items.title`  
to support efficient search queries as the data scales.

**Fourth**, I will add non-negative quantity validation at the database  
insert and update layer — rejecting any operation that would  
set qty below zero.

**Fifth**, I will move database operations to a background thread  
using Android's `AsyncTask` replacement or a simple `ExecutorService`,  
keeping the main thread free for UI rendering.

These enhancements demonstrate **Course Outcome 4** — implementing industry-standard  
techniques and tools — and **Course Outcome 5** — developing a security mindset  
that anticipates vulnerabilities and designs for mitigation."

---

## CLOSING (~2 minutes)

"To summarize what I've covered today:

The existing app has a solid foundation — the navigation structure works,  
the database helper has proper CRUD methods, and the SMS permission flow  
is implemented correctly.

However, there are significant gaps:

- The inventory screen is completely disconnected from the database,  
  and all data is lost on app restart.
- The data model is incomplete — quantity is stored as a string,  
  not an integer, making it impossible to do numeric operations.
- Passwords are stored in plaintext, which is a critical security vulnerability.
- There's no search, filter, or sort capability.
- The architecture has no separation of concerns — everything is crammed  
  into the Fragments.

My planned enhancements will address all of these issues across all three categories,  
and I'm confident they will demonstrate meaningful growth in software engineering,  
algorithm design, and secure database practices.

Thank you."

---

## 📋 Screen Flow — What to Have Open When

| Segment | File to Show |
|---|---|
| Intro | Project file tree in Android Studio |
| SE — What it does | `FirstFragment.java` → `SecondFragment.java` → `MainActivity.java` |
| SE — Big bug | `SecondFragment.java` line 19, then `AppDbHelper.java` methods |
| SE — Bypass button | `FirstFragment.java` lines 62–68 |
| SE — Two models | `Item.java` side by side with `AppDbHelper.java` ItemRow |
| Algorithms — Data structure | `SecondFragment.java` ArrayList + `AppDbHelper.java` readAllItems |
| DB — What it does | `AppDbHelper.java` full file walk |
| DB — Plaintext password | `AppDbHelper.java` createUser and checkUser methods |
| DB — onUpgrade | `AppDbHelper.java` lines 37–40 |

---

## ⏱️ Timing Guide

| Section | Target Time |
|---|---|
| Intro + project overview | 2 min |
| Category 1: SE — Functionality | 2 min |
| Category 1: SE — Analysis | 5 min |
| Category 1: SE — Enhancement plan | 3 min |
| Category 2: Algorithms — Functionality | 2 min |
| Category 2: Algorithms — Analysis | 4 min |
| Category 2: Algorithms — Enhancement plan | 2 min |
| Category 3: DB — Functionality | 2 min |
| Category 3: DB — Analysis | 4 min |
| Category 3: DB — Enhancement plan | 2 min |
| Closing | 2 min |
| **Total** | **~30 min** |
