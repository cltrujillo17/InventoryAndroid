# CS 499 Milestone Two — Enhancement One Narrative
## Software Design and Engineering

**Name:** Carlos Trujillo
**Course:** CS 499 — Computer Science Capstone
**Date:** March 22, 2026


---

### Prompt 1: Artifact Description

&nbsp;&nbsp;&nbsp;&nbsp;The artifact selected for this enhancement is an Android Inventory Management
application originally developed during CS 360: Mobile Architecture and Programming.
The application was created to fulfill a mobile development assignment requiring a
fully functional Android app with user authentication, a data-driven inventory screen,
and an SMS notification feature. The app is written in Java, targets Android API 34
and above, and uses Android's Navigation Component to manage transitions between
screens. Its three main screens are a login fragment, an inventory grid fragment, and
an SMS permissions fragment. The supporting data layer uses Android's built-in
SQLiteOpenHelper class to persist user credentials and inventory records locally on
the device.

---

### Prompt 2: Justification, Skills, and Improvements

&nbsp;&nbsp;&nbsp;&nbsp;This artifact was selected for the ePortfolio because it presents a realistic case
study of functional but architecturally flawed code — the kind of situation software
engineers regularly encounter in professional practice. While the app compiled and ran
without crashing, several critical structural problems prevented it from functioning
as intended. The most significant flaw was that the inventory screen, SecondFragment,
stored all data in a plain in-memory ArrayList and never interacted with the database.
This meant all inventory records were permanently lost every time the user closed the
application. The contradiction — a fully written database layer that was never called
by the UI — demonstrates a real-world integration failure that this enhancement
directly addresses. Selecting this artifact allowed me to demonstrate skills in
software architecture, design patterns, object-oriented modeling, defensive
programming, and platform API usage.

&nbsp;&nbsp;&nbsp;&nbsp;The first and most foundational improvement was correcting the Item.java data model.
The original class contained only a title field and a detail field; quantity was
embedded inside the detail string as plain text, for example "Qty: 12." This approach
made any numeric operation on quantity — such as checking for low stock, incrementing
a count, or validating input — fragile and error-prone, requiring string parsing every
time. The revised Item class introduces a properly typed long id field that mirrors the
database primary key and an int qty field for safe arithmetic. A redundant inner class,
AppDbHelper.ItemRow, which previously duplicated the model's purpose, was removed and
replaced by this unified Item class throughout the codebase. This change alone resolved
the type inconsistency that was the root cause of the data model weakness identified
in the code review.

&nbsp;&nbsp;&nbsp;&nbsp;The second major improvement was the introduction of the MVVM
(Model-View-ViewModel) architectural pattern through a new InventoryViewModel class.
In the original implementation, SecondFragment simultaneously served as the UI
controller, business logic handler, and data manager — a known anti-pattern sometimes
described as the "God Fragment." The new InventoryViewModel extends Android's
AndroidViewModel, holds a MutableLiveData wrapped list of Item objects, and delegates
all database operations to AppDbHelper. SecondFragment now observes this LiveData
source and renders whatever the ViewModel provides. No business logic remains in the
Fragment. As a direct result, inventory items are now persisted to SQLite on every add
and delete operation, and the full item list is correctly reloaded from the database on
each app launch through the ViewModel's loadItems method. The use of AndroidViewModel,
rather than a plain ViewModel, ensures the database helper can safely receive the
application context without risking a memory leak by holding a reference to the Fragment
lifecycle.

&nbsp;&nbsp;&nbsp;&nbsp;The third improvement addressed a security vulnerability: two buttons on the login
screen, btnGoToGrid and btnGoToSms, allowed unauthenticated users to bypass the login
flow entirely and access protected screens. These were Debug-era navigation shortcuts
left in the production build. Both buttons were removed from the layout file and the
fragment controller. All navigation to authenticated screens now exclusively passes
through the validated login path, meaning a valid username and password combination
must be confirmed before the inventory screen is accessible.

&nbsp;&nbsp;&nbsp;&nbsp;Additional improvements were made across several supporting files. The GridAdapter was
modernized to extend ListAdapter with a DiffUtil.ItemCallback, replacing the previous
manual notifyItemInserted and notifyItemRemoved calls with efficient, animation-backed
list diffing. Deletion was also corrected from a fragile position-based approach to a
stable ID-based approach using the database row ID from the Item model, preventing
index-out-of-bounds errors during rapid consecutive deletions. In AppDbHelper, all
column name strings were extracted into named constants to eliminate raw string
repetition, Javadoc comments were added to every public method, and defensive
validation was introduced to reject null or empty titles on insert and to enforce a
minimum quantity of zero at the database layer. The destructive onUpgrade method, which
previously dropped all tables and destroyed every user's data on any schema version
bump, was replaced with a safe additive migration using a CREATE INDEX IF NOT EXISTS
statement. The MainActivity floating action button, which displayed a placeholder
"Replace with your own action" message from the Android Studio project template, was
updated to navigate to the SMS Notifications screen. Finally, the deprecated
SmsManager.getDefault() call in SmsFragment was replaced with the modern
requireContext().getSystemService(SmsManager.class) API. This issue was identified
during the code review phase and tracked through to implementation. Since the app
targets a minimum SDK of 34 (Android 14), the modern API can be used unconditionally
without a version guard, and the fix eliminates the compiler deprecation warning that
was present in the original code.

---

### Prompt 3: Course Outcomes

&nbsp;&nbsp;&nbsp;&nbsp;The enhancements completed in this milestone demonstrate substantial progress toward
three course outcomes. Course Outcome 3 — designing and evaluating computing solutions
using algorithmic principles and computer science practices while managing tradeoffs —
is reflected in the architectural decision to adopt MVVM. The choice to use LiveData
over simple callbacks, and AndroidViewModel over plain ViewModel, represents a
deliberate tradeoff: slightly more structural complexity upfront in exchange for
lifecycle-safe data observation, configuration-change resilience, and testability.
Course Outcome 4 — applying well-founded and innovative techniques and tools in
computing practices for the purpose of delivering value and accomplishing
industry-specific goals — is demonstrated through the correct application of
established Android architecture components including ViewModel and LiveData, the
ListAdapter and DiffUtil pattern for efficient RecyclerView updates, and the use of
parameterized SQL queries throughout the database layer. Course Outcome 5 — developing
a security mindset that anticipates adversarial exploits in software architecture and
designs to expose potential vulnerabilities — is addressed through the removal of the
unauthenticated navigation bypass, the introduction of defensive data validation at
the database layer, and the correction of the deprecated platform API in SmsFragment.
Full coverage of Outcome 5 will be expanded in Milestone Three with the implementation
of password hashing for stored credentials. No updates to the original outcome-coverage
plan are required at this time.

---

### Prompt 4: Reflection on the Enhancement Process

&nbsp;&nbsp;&nbsp;&nbsp;The most significant learning that occurred during this enhancement was developing a
clearer understanding of why architectural patterns like MVVM exist, rather than simply
knowing that they are recommended practice. Before this milestone, the concept of
separating concerns between a Fragment and a ViewModel was abstract. Working through
the actual process of extracting database calls out of SecondFragment and placing them
inside InventoryViewModel made the value concrete: without the ViewModel, a screen
rotation would previously have re-instantiated the Fragment and lost all in-memory
data, or — as happened in the original app — simply never persisted it in the first
place. The LiveData observer pattern also clarified how reactive programming reduces
manual state management: instead of explicitly calling a refresh method after every
add or delete, the Fragment declares what it wants to observe and the framework handles
the rest automatically.

&nbsp;&nbsp;&nbsp;&nbsp;The primary challenge encountered was coordinating changes across multiple
interdependent files simultaneously. Changing the Item data model had cascading effects
on AppDbHelper, GridAdapter, SecondFragment, and the XML layout files for both the
grid screen and the item card. Each file had to be updated consistently, and the order
of changes mattered to avoid intermediate compilation failures. A secondary challenge
arose during the Gradle build configuration, where adding the lifecycle-viewmodel and
lifecycle-livedata dependencies via the version catalog required understanding how
Gradle's type-safe accessor generation works and when cached accessors must be
invalidated. This experience reinforced the importance of planning a complete
dependency map before making changes, rather than modifying files individually and
expecting the surrounding system to adapt. One additional improvement made during
implementation was updating the deprecated SmsManager.getDefault() call in SmsFragment
to use the modern context-aware API. This issue was identified and noted during the
code review phase, tracked deliberately, and addressed as part of this milestone —
demonstrating that the code review process produced actionable, follow-through results
rather than observations alone.

---

## References

Google LLC. (2024). *Guide to app architecture.* Android Developers.
&nbsp;&nbsp;&nbsp;&nbsp;https://developer.android.com/topic/architecture

Google LLC. (2024). *ViewModel overview.* Android Developers.
&nbsp;&nbsp;&nbsp;&nbsp;https://developer.android.com/topic/libraries/architecture/viewmodel

Google LLC. (2024). *LiveData overview.* Android Developers.
&nbsp;&nbsp;&nbsp;&nbsp;https://developer.android.com/topic/libraries/architecture/livedata

Martin, R. C. (2008). *Clean code: A handbook of agile software craftsmanship.* Prentice Hall.

Stuttard, D., & Pinto, M. (2011). *The web application hacker's handbook: Finding and
&nbsp;&nbsp;&nbsp;&nbsp;exploiting security flaws* (2nd ed.). Wiley.
