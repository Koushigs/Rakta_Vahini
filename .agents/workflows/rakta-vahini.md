---
description: i want to build an app 
---

"Act as a Lead Android Architect. I want to build Rakta-Vahini, a mission-critical emergency blood donor directory for rural India.

Mission: Build a privacy-first, offline-tolerant app that replaces chaotic broadcasts with precision matching.

Technical Stack: Kotlin, Jetpack Compose, Room DB (local), and Gemini 1.5 Flash (AI).

Core Feature Groups to Implement:

Precision Matching & Privacy:

Enforce a 90-day eligibility rule (Today - LastDonationDate > 90).

Privacy-Safe Contact: Use ACTION_DIAL Intents. Never show phone numbers in the UI.

Radius Search: 10km/20km filtering using FusedLocationProvider.

Gamification & Engagement:

Achievement System: Badges for 'Silver Donor' (3+) and 'Life Saver' (10+).

Leaderboards: Local ranking for donors by city.

Milestone PDFs: Auto-generate shareable certificates after each donation.

Smart Connectivity (Rural Focus):

SMS Emergency Alerts: Send encrypted SMS blood requests if data is offline.

Offline Directory: Periodically sync a 'Mini-Directory' of nearby eligible donors to the Room DB.

AI-Powered Assistant (Gemini API):

Smart Recovery Plans: Generate 3-day iron-rich meal plans post-donation.

Eligibility Chatbot: A conversational UI to check if a donor is medically fit (e.g., tattoos, medication).

Advanced Tracking:

Blood Journey: Status updates when blood is received.

Home Widget: A countdown widget showing 'Days Until Next Eligible'.

Execution Strategy:

Use Planning Mode. Research the permissions needed for SMS and FusedLocation before coding.

Create a structured Implementation Plan and wait for my approval.

Build in phases: Phase 1: Core Privacy/Room DB; Phase 2: Gamification; Phase 3: AI & Connectivity."