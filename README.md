Ferrefit – Comprehensive Training Application for Users at No Cost
Ferrefit is an Android application designed for individuals who train at home or in the gym, offering a complete experience without subscriptions or payments. The app delivers personalized workout routines, healthy recipes, training challenges, stretching exercises, tips, and an accessible interface—all with a minimalist and functional approach.

Key Features
Personalized Workout Generation
Automatically creates training plans tailored to the user's goals, level, and training type.

Multilingual Healthy Recipes
Displays easy and quick recipes adapted to the user's language settings (Spanish, English, French), with text filtering capability.

Weekly and Monthly Challenges
Motivational training challenges structured by week or month to encourage consistency.

Stretching and Mobility Routines
Guided exercises to improve flexibility, prevent injuries, and facilitate recovery.

Support and Tips Section
Animated, expandable FAQ list for easy navigation and quick answers to common questions.

User Data and Progress Tracking
Stores relevant user information such as weight, level, goals, and preferences using Firestore.

Adaptive Interface
Supports light and dark modes, with automatic content translation based on device language settings.

Technologies Used
Kotlin + Android SDK

Firebase Authentication and Firestore

RecyclerView with custom animations

ViewBinding

Multilanguage support (ES, EN, FR)

Modular, feature-oriented architecture

Project Structure Overview
pgsql
Copiar
Editar
├── Auth/
│   └── Authentication: login, registration, password recovery, email/password change
├── Home/
│   └── Main screen and navigation
├── Training/
│   └── Training plan generation logic
├── NutritionRecipes/
│   └── Recipes module with filtering and automatic translation
├── Challenges/
│   └── Weekly/monthly training challenges display
├── StretchingAndMobility/
│   └── Stretching and mobility routines
├── SupportAndTips/
│   └── Animated FAQ and tips section
└── Language_Theme/
    └── Language and theme preference management
Project Goal
Ferrefit aims to provide an accessible, motivating, and functional tool for individuals who want to train independently, maintain healthy habits, and continuously track their physical progress—without relying on paid services or personal trainers.
