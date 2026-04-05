# CS 499 — 5-1 Journal: Emerging Trends in Computer Science

**Name:** Carlos Trujillo
**Course:** CS 499 — Computer Science Capstone
**Date:** April 5, 2026

---

> **Formatting note for Word submission:**
> Double spacing | 12-point Times New Roman | 1-inch margins

---

## Part One

&nbsp;&nbsp;&nbsp;&nbsp;The two trends I have chosen to discuss are AI-assisted software development and
on-device artificial intelligence for mobile platforms. Both are directly relevant to
the direction of the field and to my own career interests in Android development and
software engineering.

&nbsp;&nbsp;&nbsp;&nbsp;AI-assisted software development — exemplified by tools such as GitHub Copilot,
Gemini Code Assist, and similar large language model-based tools embedded in
development environments — is reshaping how software is written. The significance of
this trend lies in its potential to increase developer productivity by automating
repetitive boilerplate code, suggesting implementations for common patterns, and
surfacing relevant documentation during the writing process. Rather than eliminating
software engineering as a career, this trend is changing the nature of the work: the
developer's role is increasingly focused on problem framing, architectural decisions,
code review, and verification rather than writing every line from scratch. For
consumers and workers, the effect is accelerated software delivery and, potentially,
a wider population of people who can participate in software creation. For my career,
this trend is directly applicable. During this capstone project I used AI assistance
to support code organization and documentation, and developing the ability to review,
evaluate, and correct AI-generated suggestions is becoming as important as writing
code independently. The core skills I am building in this program — understanding
architecture, identifying security vulnerabilities, and reasoning about algorithmic
efficiency — remain essential for evaluating AI output intelligently.

&nbsp;&nbsp;&nbsp;&nbsp;The second trend is on-device artificial intelligence, sometimes called edge AI or
on-device ML, which refers to running machine learning inference directly on a mobile
or embedded device rather than sending data to a remote server. Tools such as Google's
ML Kit, TensorFlow Lite, and Apple's Core ML make it possible to perform tasks like
image recognition, natural language processing, and anomaly detection within an Android
or iOS application without a network connection. The significance of this trend for
computer science is substantial: it challenges the assumption that powerful computation
requires cloud infrastructure, and it introduces new constraints around model size,
energy efficiency, and hardware acceleration that traditional software engineering does
not encounter. For consumers, on-device AI means faster responses, lower data usage,
and improved privacy because sensitive information never leaves the device. For
software engineers working in mobile development, this trend creates new opportunities
to build intelligent features directly into applications. For my career, this is
particularly relevant because Android is my primary development target, and Google's
ML Kit integrates directly with the Android SDK I am already using in this capstone.

&nbsp;&nbsp;&nbsp;&nbsp;Regarding course outcomes, I have made solid progress on Outcome 3 (designing
computing solutions using algorithmic principles) through the Milestone Three search,
sort, and low-stock detection enhancements; Outcome 4 (applying well-founded
techniques and tools) through the MVVM implementation and SQL-layer query optimization;
and Outcome 5 (developing a security mindset) through parameterized query usage and
authentication improvements. Outcome 2 (professional-quality communication) is being
addressed through the ePortfolio narratives. Outcome 1 (collaborative environments)
will be addressed in the final ePortfolio context section. The Milestone Four database
enhancement remains, after which all five outcomes will have corresponding
demonstrated work.

---

## Part Two — Status Checkpoints Table

| Checkpoint | Software Design and Engineering | Algorithms and Data Structures | Databases |
|---|---|---|---|
| **Name of Artifact Used** | Android Inventory App (CS 360) | Android Inventory App (CS 360) | Android Inventory App (CS 360) |
| **Status of Initial Enhancement** | ✅ Complete. MVVM refactor, database connection, Item model fix, auth bypass removal, GridAdapter upgrade, deprecated API fix. | ✅ Complete. Real-time SQL LIKE search, SQL ORDER BY sort (4 options), O(n) low-stock detection algorithm, per-card visual indicator. | 🔲 Not started. Plan: SHA-256 + salt password hashing, safe onUpgrade migration, off-main-thread DB operations via ExecutorService. |
| **Submission Status** | ✅ Submitted — Milestone Two. | ✅ Ready to submit — Milestone Three. | Not yet submitted. |
| **Status of Final Enhancement** | 🔲 Not started. Plan: low-stock SMS integration and ePortfolio polish. | 🔲 Not started. Plan: refine threshold UI and ePortfolio polish. | 🔲 Not started. Will follow Milestone Four implementation. |
| **Uploaded to ePortfolio** | No — pending final polish. | No — pending instructor feedback. | No |
| **Status of Finalized ePortfolio** | In progress — GitHub Pages structure planned. | In progress. | Not yet started. |

---

## References

GitHub. (2024). *GitHub Copilot documentation.*
&nbsp;&nbsp;&nbsp;&nbsp;https://docs.github.com/en/copilot

Google LLC. (2024). *ML Kit for Firebase — on-device machine learning.*
&nbsp;&nbsp;&nbsp;&nbsp;https://developers.google.com/ml-kit

LeCun, Y., Bengio, Y., & Hinton, G. (2015). Deep learning. *Nature, 521*(7553), 436–444.
&nbsp;&nbsp;&nbsp;&nbsp;https://doi.org/10.1038/nature14539
