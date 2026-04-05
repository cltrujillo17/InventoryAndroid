# CS 499 — 3-1 Journal: Marketing With ePortfolios and Artifact Update

**Name:** Carlos Trujillo
**Course:** CS 499 — Computer Science Capstone
**Date:** March 22, 2026

---

> **Formatting note for Word submission:**
> Double spacing | 12-point Times New Roman | 1-inch margins

---

## Part One

&nbsp;&nbsp;&nbsp;&nbsp;An ePortfolio is one of the most effective tools a software developer can use for
self-promotion because it transforms abstract credentials into visible, working evidence of
skill. Rather than simply claiming proficiency in areas such as database design or software
architecture on a resume, a well-constructed ePortfolio allows a hiring manager or
interviewer to examine actual code, read documented design decisions, and evaluate real
problem-solving. For my own ePortfolio, I plan to use the Android Inventory App from CS
360 as the central artifact, enhanced across three categories — software engineering and
design, algorithms and data structures, and databases — to demonstrate that I can identify
weaknesses in existing code, apply industry-standard architectural patterns such as
Model-View-ViewModel (MVVM), implement efficient search and sort algorithms, and build a
secure data persistence layer. In an interview context, I can walk a potential employer
through the before-and-after state of the project, clearly articulating what I changed and
why, which demonstrates both technical knowledge and professional communication skills
(Andrews & Cole, 2015).

&nbsp;&nbsp;&nbsp;&nbsp;Posting work publicly does carry real risks that must be managed deliberately. The most
significant concern is intellectual property: any code that was written as part of a
course or employer engagement may carry ownership restrictions. To mitigate this, I will
ensure that all work presented in my ePortfolio is original and authored entirely by me as
part of my academic coursework, rather than proprietary business code. A second risk is
exposing security vulnerabilities — for example, early versions of my artifact store user
passwords in plaintext, which is a documented security flaw. Before publishing, I will
ensure that the code shown in the ePortfolio reflects the enhanced, secure version only,
with the vulnerability corrected through password hashing (Stuttard & Pinto, 2011). A
third risk is oversharing personally identifiable information; the ePortfolio will contain
only the artifact, narrative descriptions, and code — no personal contact information
beyond a professional email address. Regarding course outcomes, I have made clear progress
on Outcome 3 (designing computing solutions using algorithmic principles) and Outcome 4
(demonstrating well-founded techniques and tools in computing practices) through my code
review, during which I analyzed existing code and articulated a structured plan for
enhancement. Outcome 5 (developing a security mindset) has been partially addressed
through identifying the plaintext password vulnerability. Outcomes 1 and 2, which relate
to collaborative communication and professional delivery, remain in progress and will be
addressed as I publish the finalized ePortfolio narrative and artifact documentation.

---

## Part Two — Status Checkpoints Table

| Checkpoint | Software Design and Engineering | Algorithms and Data Structures | Databases |
|---|---|---|---|
| **Name of Artifact Used** | Android Inventory App (CS 360) | Android Inventory App (CS 360) | Android Inventory App (CS 360) |
| **Status of Initial Enhancement** | Not yet started. Plan is to refactor `SecondFragment` to connect to `AppDbHelper`, fix the `Item.java` data model to include `id` and `qty` as properly typed fields, and introduce a ViewModel layer to separate UI from business logic. | Not yet started. Plan is to add a search bar with real-time filtering via SQL `LIKE` queries and implement sort options (by name and by quantity) using `ORDER BY` clauses at the database layer. | Not yet started. Plan is to implement password hashing using SHA-256 with a salt before storing credentials, fix the destructive `onUpgrade` method to use `ALTER TABLE` migrations, and add an index on the `items.title` column for search performance. |
| **Submission Status** | Not yet submitted. | Not yet submitted. | Not yet submitted. |
| **Status of Final Enhancement** | Not yet started. Final enhancement will add a low-stock visual indicator and remove all leftover placeholder and debug code (bypass navigation buttons, FAB placeholder). | Not yet started. Final enhancement will add a low-stock detection algorithm that flags items below a user-defined threshold and optionally triggers an SMS alert via `SmsFragment`. | Not yet started. Final enhancement will add non-negative quantity validation at the database layer and move all database operations off the main UI thread using an `ExecutorService`. |
| **Uploaded to ePortfolio** | No | No | No |
| **Status of Finalized ePortfolio** | Not yet started. GitHub Pages will be used to host the ePortfolio with narrative descriptions linking each enhancement to the relevant course outcomes. | Not yet started. | Not yet started. |

---

## Optional Artifact Feedback

No artifact links submitted at this time. The code review video for Milestone One has been
submitted. Feedback is welcomed on the planned enhancement scope — specifically whether the
MVVM refactor for the software engineering category is appropriately scoped for a single
milestone, or whether it should be split across milestones.

---

## References

Andrews, G., & Cole, C. (2015). *The promise of ePortfolios for students in higher education.*
&nbsp;&nbsp;&nbsp;&nbsp;Journal of Learning Development in Higher Education, 7, 1–16.
&nbsp;&nbsp;&nbsp;&nbsp;https://doi.org/10.47408/jldhe.v0i7.215

Stuttard, D., & Pinto, M. (2011). *The web application hacker's handbook: Finding and
&nbsp;&nbsp;&nbsp;&nbsp;exploiting security flaws* (2nd ed.). Wiley.
