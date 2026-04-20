# CS 499 Milestone Four — Enhancement Three Narrative
## Databases

**Name:** Carlos Trujillo
**Course:** CS 499 — Computer Science Capstone
**Date:** April 5, 2026

---

> **Formatting note for Word submission:**
> Double spacing | 12-point Times New Roman | 1-inch margins

---

### Prompt 1: Artifact Description

&nbsp;&nbsp;&nbsp;&nbsp;The artifact for this enhancement is the Android Inventory Management application
originally developed in CS 360: Mobile Architecture and Programming, and progressively
enhanced throughout CS 499. The application manages user accounts and inventory records
through a local SQLite database, accessed via Android's SQLiteOpenHelper class. Over
the course of this capstone, the application has been refactored to follow MVVM
architecture (Milestone Two), extended with search, sort, and low-stock detection
algorithms (Milestone Three), and is now enhanced with improved database security and
reliability practices in this milestone.

---

### Prompt 2: Justification, Skills, and Improvements

&nbsp;&nbsp;&nbsp;&nbsp;This artifact was selected for the Databases category because the original
implementation had a critical and easily exploitable security flaw at the data layer:
user passwords were stored in the database as plain text. Any developer, database
administrator, or attacker with access to the SQLite file could read every user's
password directly. In many real-world cases, people reuse passwords across services,
meaning a compromised password in one application becomes a credential risk in others.
Addressing this vulnerability required applying specific knowledge of cryptographic
hashing, salting, and secure random number generation — database security practices
that are standard in any professional application handling user credentials.

&nbsp;&nbsp;&nbsp;&nbsp;The first and most significant improvement is the replacement of plain-text password
storage with SHA-256 hashing and per-user salting. When a user creates an account,
the application now generates a 16-byte cryptographically random salt using Java's
SecureRandom class, which draws from the operating system's entropy pool. The salt
is prepended to the password bytes and the combined value is hashed using the SHA-256
algorithm via Java's MessageDigest class. Both the resulting hash and the salt are
Base64-encoded and stored as text columns in the users table. On login, the stored
salt for the given username is retrieved, the entered password is hashed with it, and
the result is compared against the stored hash. The plain-text password is never stored,
logged, or transmitted. The use of a unique per-user salt is critical: without it, two
users with the same password would produce the same hash, making the database
vulnerable to pre-computed lookup tables (rainbow table attacks).

&nbsp;&nbsp;&nbsp;&nbsp;The second improvement is the addition of a structured database schema migration.
Introducing the salt column required a new schema version (version 3). The onUpgrade
method was updated to handle the specific step from version 2 to version 3: the users
table is dropped and recreated with the new schema, while the items table, which holds
the actual inventory data, is left intact. Dropping the users table in this case is
the correct security decision — existing rows contain plain-text passwords that cannot
be retroactively hashed without knowing the originals. Requiring existing users to
re-register is preferable to leaving compromised credentials in place.

&nbsp;&nbsp;&nbsp;&nbsp;The third improvement moves all database operations off the Android main thread using
a single-threaded ExecutorService. In the original application, every login check,
account creation, item read, and item write ran synchronously on the UI thread. While
SQLite operations are typically fast, blocking the main thread with any I/O is
considered an antipattern in Android development and can cause Application Not
Responding (ANR) errors on slower devices or when the database grows large. The
InventoryViewModel and FirstFragment now submit each database call to a background
executor thread and use LiveData.postValue() and Activity.runOnUiThread() to safely
return results to the UI thread. The executor is shut down in onCleared() and
onDestroyView() respectively, preventing thread pool leaks.

---

### Prompt 3: Course Outcomes

&nbsp;&nbsp;&nbsp;&nbsp;This enhancement primarily addresses Course Outcome 5 — developing a security mindset
that anticipates adversarial exploits in software architecture and designs to expose
potential vulnerabilities, mitigate design flaws, and ensure privacy and enhanced
security of data and resources. The password hashing implementation directly mitigates
a credential exposure vulnerability that was identified during the code review in
Milestone One. The decision to use a random per-user salt, rather than a fixed salt
or no salt at all, reflects adversarial thinking about how an attacker might use a
leaked database dump. Course Outcome 4 — applying well-founded techniques and tools —
is addressed through the use of Java's MessageDigest and SecureRandom APIs as
cryptographic primitives and through the ExecutorService threading pattern as a
platform-appropriate background processing technique. No changes to the original
outcome-coverage plan are needed. All five course outcomes are now addressed across
the three milestones.

---

### Prompt 4: Reflection on the Enhancement Process

&nbsp;&nbsp;&nbsp;&nbsp;The most significant learning from this milestone came from working through what
happens to a database when a security change requires a schema migration. The instinct
when changing a table's schema might be to add the column with a default value and
leave existing rows untouched, but thinking through the security implications revealed
why that approach is wrong in this context: rows with the old plain-text password and
an empty salt value would still be vulnerable, undermining the entire purpose of the
upgrade. Arriving at the decision to drop and recreate the users table — and
articulating why that is the correct behavior from a security standpoint — was the
clearest moment of applying a security mindset to a real design choice rather than
simply following a recipe.

&nbsp;&nbsp;&nbsp;&nbsp;The background threading work presented a different kind of challenge: understanding
the threading model imposed by Android's LiveData. The key insight was that setValue()
can only be called from the main thread, while postValue() can be called from any
thread. Failing to make this distinction would cause a silent crash at runtime that
would not appear at compile time. This is exactly the type of bug that is difficult
to catch in code review but straightforward to prevent once the threading model is
understood. Working through this made the value of consistent threading discipline
very concrete: knowing which operations belong on which thread, and having a systematic
approach (all DB calls through ExecutorService, all UI updates through the main thread),
eliminates an entire class of concurrency bugs before they can occur.

---

## References

Google LLC. (2024). *Keep sensitive data out of logs.* Android Developers.
&nbsp;&nbsp;&nbsp;&nbsp;https://developer.android.com/privacy-and-security/risks/sensitive-data-console

Menezes, A., van Oorschot, P., & Vanstone, S. (2018). *Handbook of applied cryptography.*
&nbsp;&nbsp;&nbsp;&nbsp;CRC Press. https://cacr.uwaterloo.ca/hac/

OWASP Foundation. (2023). *Password storage cheat sheet.*
&nbsp;&nbsp;&nbsp;&nbsp;https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html

Google LLC. (2024). *Threading in ViewModel.* Android Developers.
&nbsp;&nbsp;&nbsp;&nbsp;https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories
