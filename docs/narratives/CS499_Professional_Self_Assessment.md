# Professional Self-Assessment
## Carlos Trujillo — B.S. Computer Science, SNHU


---

&nbsp;&nbsp;&nbsp;&nbsp;Completing the Bachelor of Science in Computer Science at Southern New Hampshire
University has been a process of progressive, cumulative learning that I did not fully
appreciate until reaching this capstone. Early courses gave me foundational vocabulary
and syntax. Later courses gave me design principles and engineering judgment. The
capstone forced me to apply both simultaneously to a real artifact — a mobile
application I built in CS 360 — and to evaluate it honestly against professional
standards. That evaluation revealed something important: a program that compiled and
ran without errors was nonetheless architecturally broken in ways that would have made
it unmaintainable, insecure, and incorrect in production. Recognizing that gap, and
knowing how to close it, is the most concrete measure of what this program has given
me.

**Collaborating in a Team Environment**

&nbsp;&nbsp;&nbsp;&nbsp;Collaborative work in software engineering is not only about writing code with others —
it is about writing code that others can understand, review, and extend. CS 250
(Software Development Lifecycle) introduced me to Agile methodology and the discipline
of sprint planning, retrospectives, and iterative delivery. CS 320 (Software Testing
and Quality Assurance) reinforced that readable, testable code is a prerequisite for
any collaborative environment. Throughout this capstone, I practiced what those courses
taught: every method I wrote or rewrote includes Javadoc comments explaining its
contract, its parameters, and the reasoning behind non-obvious decisions. The code
review I produced for Milestone One was written and recorded for an audience of peers
and a hypothetical manager — not just to satisfy a rubric, but to simulate the real
industry practice of presenting work for group scrutiny. That exercise made me
articulate decisions I might otherwise have left implicit, which is exactly what
collaborative environments require.

**Communicating With Stakeholders**

&nbsp;&nbsp;&nbsp;&nbsp;The three milestone narratives I wrote for this ePortfolio were each addressed to a
different audience simultaneously: a technical instructor who needed to see evidence
of engineering depth, and a general reader who needed to understand why the choices
mattered. Explaining why plain-text password storage is a vulnerability, what a rainbow
table attack is, and why a per-user salt prevents it — in clear prose without assuming
prior cryptography knowledge — required the same translation skill that a software
engineer uses when presenting a security proposal to a non-technical product team.
CS 340 (Advanced Programming Concepts) and IT 315 (Object Oriented Analysis and Design)
both emphasized the importance of communicating design decisions before writing a line
of code. This ePortfolio reflects that discipline: each enhancement was planned in the
code review, explained in a narrative, and implemented in a way that the code itself
documented through comments and naming.

**Data Structures and Algorithms**

&nbsp;&nbsp;&nbsp;&nbsp;The Milestone Three enhancement demonstrated that algorithmic improvement is not always
about inventing a new data structure — it is often about knowing which layer of the
stack should perform which computation. I replaced an in-memory ArrayList with no
search capability with a SQL-backed query system that uses an indexed LIKE clause for
keyword filtering and a validated ORDER BY clause for sorting. Both operations offload
work to the SQLite engine, which is optimized for set-based operations on indexed data.
The low-stock detection algorithm — an O(n) linear scan over the loaded item list —
was designed to add zero extra database queries by reusing data already in memory.
CS 260 (Data Structures and Algorithms) gave me the vocabulary to reason about
algorithmic complexity and make those tradeoffs deliberately rather than accidentally.

**Software Engineering and Databases**

&nbsp;&nbsp;&nbsp;&nbsp;The Milestone Two enhancement is where the program's software engineering coursework
became most tangible. The Android Inventory App I built in CS 360 was functional but
architecturally flawed: all inventory data was stored in an in-memory ArrayList that
was never connected to the database, the data model was missing typed fields, and
authentication bypass buttons were left in the production build. The MVVM refactor I
performed in Milestone Two reflects principles introduced in CS 250 and reinforced in
CS 360: separation of concerns, single responsibility, and the value of a observable
data layer that the UI reacts to rather than manages. The Milestone Four database
enhancement built on this by introducing SHA-256 password hashing with per-user
salting, structured schema migrations, and background threading via ExecutorService —
each of which reflects professional-grade database engineering rather than prototype-
level code.

**Security Mindset**

&nbsp;&nbsp;&nbsp;&nbsp;CS 405 (Secure Coding) was the course that most directly shifted my perspective on
software quality. Before it, I thought of security as a separate concern — something
added after the core logic was working. After it, I understand that security is a
property of design decisions made before a line of code is written. The most practical
demonstration of this in my ePortfolio is the password hashing implementation: the
decision to use SecureRandom rather than java.util.Random, to salt each password
individually rather than using a fixed salt, and to drop and recreate the users table
during the schema migration rather than leaving old plain-text credentials in place —
each of these choices reflects the adversarial thinking that secure coding requires.
The use of parameterized SQL queries throughout AppDbHelper similarly reflects the
principle that user input should never be concatenated into executable code.

**How the Artifacts Fit Together**

&nbsp;&nbsp;&nbsp;&nbsp;All three enhancements were applied to a single artifact: the Android Inventory
Management application from CS 360. This was a deliberate choice. Using a single
artifact allowed me to demonstrate how the three areas of computer science — software
engineering, algorithms, and databases — are not independent disciplines but
interconnected layers of the same system. The MVVM architecture established in
Milestone Two was the prerequisite for the search and sort algorithms in Milestone
Three: only once data was flowing through a ViewModel and LiveData could real-time
filtering be wired to a TextWatcher without touching the database layer from a
Fragment. The database security improvements in Milestone Four were only meaningful
because Milestone Two had connected the UI to the database in the first place. A
visitor to this portfolio can read the three narratives and the code review in sequence
and trace a single engineering story: a prototype-quality app transformed into a
production-quality one through deliberate, documented, and reasoned enhancements.

&nbsp;&nbsp;&nbsp;&nbsp;The career direction this program has given me is mobile software engineering, with a
particular focus on Android platform development and the backend systems that support
it. The skills I have developed — architectural design, database security, algorithmic
efficiency, and professional technical communication — are directly applicable to the
roles I am pursuing. This ePortfolio is the most honest representation I can offer
of where I am as a computer scientist and where I intend to go.
