# NextRep

Groupe 6 :
EL ADHAL Mohamed
ARAGO Paul
MAZERAT Julian

# NextRep

Notre Application Android pour créer des exercices, organiser des séances et aussi enregistrer l’historique des entraînements. Le projet a respécté le patron architectural MVVP pour (Model - View - ViewModel) comme vu dans le cours.

---

## Fonctionnalités principales

- **Gestion des exercices**
  - Création d’un exercice (nom, description, séries, répétitions).
  - Ajout d’une **photo** via la **caméra** (et/ou galerie selon version).
  - Affichage de la photo dans la liste des exercices.

- **Gestion des sessions (séances)**
  - Création d’une session et sélection d’exercices existants.
  - Lancement d’un entraînement “Workout Live” basé sur une session.

- **Entraînement**
  - **Workout Live (avec session)** : saisie des sets (kg, reps), ajout de sets, validation (checkbox Done), enregistrement en historique.
  - **Free Workout (sans session)** : même logique mais avec une liste d’exercices sélectionnés.

- **Historique**
  - Historique par exercice : affichage des runs (chaque run = un timestamp) + sets associés.
  - Aperçu “All Exercises History” : dernier run par exercice.

---

## Structure du projet


- `com.example.nextrep.ui.screens`
  - Écrans Compose (pages principales).
  - Exemples :
    - `ExercisesListPage.kt` : liste + mode sélection
    - `ExerciseCreationPage.kt` : création d’un exercice
    - `WorkoutLivePage.kt` : entraînement d’une session
    - `FreeWorkoutPage.kt` : entraînement libre
    - `AllExercicesHistoryPage.kt` : aperçu historique par exercice
    - `ExerciceHistoryPage.kt` : historique détaillé d’un exercice


- `com.example.nextrep.viewmodels`
  - ViewModels (la partie qui gére la gestion d'etat avec uistate et la separation de la logique) 
  - Exemples :
    - `ExercisesViewModel.kt`
    - `SessionsViewModel.kt`

- `com.example.nextrep.models.data`
  - Repositories + Database 
  - Exemples :
    - `ExercisesRepository.kt`
    - `WorkoutHistoryRepository.kt`
    - `NextRepDatabase.kt`

- `com.example.nextrep.models.dao`
  - DAO Room (requêtes SQL)
  - Exemples :
    - `ExerciseDao.kt`
    - `WorkoutSetDao.kt`

- `com.example.nextrep.models.entity`
  - Entités Room
  - Exemples :
    - `ExerciseEntity.kt`
    - `WorkoutSetEntity.kt`

- `com.example.nextrep.models.data` (ou `models.kt` / `models` selon ton organisation)
  - Modèles “UI/domain” utilisés par l’app (`Exercise`, `Session`, etc.)


## Navigation 

Navigation centrale définie dans :
- `NextRepScreen.kt` (routes / navigation / composables racines)  ← 

Flow typiques :
- **Créer un exercice** → retour liste
- **Créer une session** → sélection exercices → sauvegarde → lancement possible en workout
- **Workout live** → saisie sets → Finish → sauvegarde historique → page de résumé (si existante)

---

## Données & persistance (Room)

L’app utilise Room pour stocker :
- Exercices
- Historique des entraînements (sets effectués)

### Entités importantes
- `ExerciseEntity` ← 
  - Contient (au minimum) : nom, description, séries, répétitions, `photoUri` (ou chemin fichier)
- `WorkoutSetEntity` ← 
  - Contient (au minimum) :
    - `exerciseId`, `exerciseName`
    - `sessionId`, `sessionName`
    - `setIndex`, `weightKg`, `reps`
    - `timestamp` (sert à regrouper un “run”)

### Repositories
- `ExercisesRepository` ← 
  - CRUD exercices
- `WorkoutHistoryRepository` ← 
  - Récupération des sets par exercice, tri, groupBy timestamp, etc.

---

## Parties “sensibles” ou problèmes majeurs: 

### 1) Problème classique Compose : perte des données en scroll certainement ==> LazyColumn
Sur les pages d’entraînement, les champs (`OutlinedTextField`) peuvent perdre leurs valeurs quand en scrollant tt en bas si l’état est stocké *dans* un item de `LazyColumn`.

**Solution appliquée dans ce projet :**
- L’état des sets est “hoist” (remonté) au parent :
  - `exerciseSets: Map<exerciseId, List<RowState>>`
- Chaque ligne/set est “stateless” et pilotée par la valeur du state parent.

Fichiers à regarder :
- `FreeWorkoutPage.kt` ← 
- `WorkoutLivePage.kt` ← 

### 2) Caméra : FileProvider / URI & crash FileUriExposedException
Si tu passes une `file://...` URI à la caméra, Android peut crasher (`FileUriExposedException`).

**Approche recommandée :**
- Utiliser `FileProvider` + `content://...` URI
- Stocker la photo dans un fichier interne (persistant) et sauvegarder le chemin/uri dans l’exercice.

Fichiers à regarder :
- `ExerciseCreationPage.kt` ← 
- `AndroidManifest.xml` (provider + permissions) ← 
- `res/xml/file_paths.xml` (si présent) ← 


### 3) Historique par “run”
Un “run” d’entraînement = sets enregistrés avec un même `timestamp`.
L’historique regroupe :
- `groupBy { timestamp }` (ou clé composite) pour afficher les runs.
- Tri du plus récent au plus ancien.

Fichiers à regarder :
- `ExerciceHistoryPage.kt` 
- `AllExercicesHistoryPage.kt` 
- `WorkoutHistoryRepository.kt` 



## Liens internes
- [NextRepScreen.kt](#)  
- [WorkoutLivePage.kt](#)  
- [FreeWorkoutPage.kt](#)  
- [ExerciseCreationPage.kt](#)  
- [ExercisesRepository.kt](#)  
- [WorkoutHistoryRepository.kt](#)  
- [ExerciseDao.kt](#) / [WorkoutSetDao.kt](#)  
- [ExerciseEntity.kt](#) / [WorkoutSetEntity.kt](#)  
- [Theme.kt](#)  
- [AndroidManifest.xml](#)  

---
