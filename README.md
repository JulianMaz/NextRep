# NextRep

**Groupe 6 :**  
- EL ADHAL Mohamed  
- ARAGO Paul  
- MAZERAT Julian  

Notre application Android permet de créer des exercices, organiser des séances et enregistrer l’historique des entraînements.  
Le projet respecte le patron architectural **MVVM (Model – View – ViewModel)** vu dans le cours.

---

## Fonctionnalités principales

- **Gestion des exercices**
  - Création d’un exercice (nom, description, séries, répétitions).
  - Ajout d’une **photo** via la **caméra**.
  - Affichage de la photo dans la liste des exercices.

- **Gestion des sessions**
  - Création d’une session et sélection d’exercices existants.
  - Lancement d’un entraînement “Workout Live” basé sur une session.

- **Entraînement**
  - **Workout Live (avec session)** : saisie des sets, ajout de sets, validation, enregistrement en historique.
  - **Free Workout (sans session)** : même logique mais sans session, avec une liste d’exercices sélectionnés.

- **Historique**
  - Historique par exercice : affichage des runs (chaque run == > une session) + sets associés.
  - Aperçu “All Exercises History” : preview du dernier run par exercice.

---

## Structure du projet

### `com.example.nextrep.ui.screens`
Écrans Compose (pages principales) :

- [ExercisesListPage.kt](app/src/main/java/com/example/nextrep/ui/screens/ExercisesListPage.kt) : ici l'affichage des liste d'exos.
- à Noter : cette page fonctionne sous 2 modes:
 - - 1- mode normal affichage,
 - - 2- mode selection (pur ajouter les exos à l'entrainment) cela est géré avec un booléen "selectionMode" [ExercisesListPage.kt – ligne 27](app/src/main/java/com/example/nextrep/ui/screens/ExercisesListPage.kt#L38).
- [ExerciseCreationPage.kt](app/src/main/java/com/example/nextrep/ui/screens/ExerciseCreationPage.kt) : ici la page responsable à la création des exos (input)
- [WorkoutLivePage.kt](app/src/main/java/com/example/nextrep/ui/screens/WorkoutLivePage.kt) : ici la page principale de notre app où se déroule l'entrainement
- [FreeWorkoutPage.kt](app/src/main/java/com/example/nextrep/ui/screens/FreeWorkoutPage.kt) : même logique ici sauf qu'on passe pas par la logique de session on lance directement l'enrainement
- [AllExercicesHistoryPage.kt](app/src/main/java/com/example/nextrep/ui/screens/AllExercicesHistoryPage.kt) : ici l'affichage d'une preview de tt les historiques des exos
- [ExerciceHistoryPage.kt](app/src/main/java/com/example/nextrep/ui/screens/ExerciceHistoryPage.kt): : ici l'affichage en détail de l'historique d'un exo en particulier 

---

### `com.example.nextrep.viewmodels`
ViewModels (gestion de l’état et séparation de la logique métier des exos, sessions et entrainement) :
- [ExercisesViewModel.kt](app/src/main/java/com/example/nextrep/viewmodels/ExercisesViewModel.kt) : rien de spécial
- [SessionsViewModel.kt](app/src/main/java/com/example/nextrep/viewmodels/SessionsViewModel.kt) : rien de spécial
- [WorkoutViewModel.kt](app/src/main/java/com/example/nextrep/viewmodels/WorkoutViewModel.kt) : ce View model à été ajouté apres l'ajout de la fonctionnalité freeWorkout pour qu'on puisse ajouter des exos au fur et à mesure de l'entrainement.


---

### `com.example.nextrep.models.data`
Repositories et base de données :

- [ExercisesRepository.kt](app/src/main/java/com/example/nextrep/models/data/ExercisesRepository.kt)
- [WorkoutHistoryRepository.kt](app/src/main/java/com/example/nextrep/models/data/WorkoutHistoryRepository.kt)
- [NextRepDatabase.kt](app/src/main/java/com/example/nextrep/models/data/NextRepDatabase.kt)

---

### `com.example.nextrep.models.dao`
DAO Room (requêtes SQL) :

- [ExerciseDao.kt](app/src/main/java/com/example/nextrep/models/dao/ExerciseDao.kt)
- [WorkoutSetDao.kt](app/src/main/java/com/example/nextrep/models/dao/WorkoutSetDao.kt)

---

### `com.example.nextrep.models.entity`
Entités Room :

- [ExerciseEntity.kt](app/src/main/java/com/example/nextrep/models/entity/ExerciseEntity.kt)
- [WorkoutSetEntity.kt](app/src/main/java/com/example/nextrep/models/entity/WorkoutSetEntity.kt)

---

## Navigation

Navigation centrale définie dans :

- [NextRepScreen.kt](app/src/main/java/com/example/nextrep/NextRepScreen.kt)

Flow typiques :
- **Créer un exercice** → retour liste
- **Créer une session** → sélection exercices → sauvegarde → lancement possible en workout
- **Workout live** → saisie sets → Finish → sauvegarde historique

---

## Données & persistance (Room)

L’app utilise Room pour stocker :
- Exercices
- Historique des entraînements (sets effectués)

### Entités importantes

- **ExerciseEntity**  
  → [ExerciseEntity.kt](app/src/main/java/com/example/nextrep/models/entity/ExerciseEntity.kt)  
  Contient : nom, description, séries, répétitions, `photoUri`.

- **WorkoutSetEntity**  
  → [WorkoutSetEntity.kt](app/src/main/java/com/example/nextrep/models/entity/WorkoutSetEntity.kt)  
  Contient :
  - `exerciseId`, `exerciseName`
  - `sessionId`, `sessionName`
  - `setIndex`, `weightKg`, `reps`
  - `timestamp` (sert à regrouper et identifier un run (un entrainment))

### Repositories

- **ExercisesRepository**  
  → [ExercisesRepository.kt](app/src/main/java/com/example/nextrep/models/data/ExercisesRepository.kt)

- **WorkoutHistoryRepository**  
  → [WorkoutHistoryRepository.kt](app/src/main/java/com/example/nextrep/models/data/WorkoutHistoryRepository.kt)

---

## Parties “sensibles” ou problèmes majeurs: 

### 1) Problème classique Compose : perte des données en scroll certainement ==> LazyColumn
Sur les pages d’entraînement, les champs (`OutlinedTextField`) peuvent perdre leurs valeurs quand en scrollant tt en bas si l’état est stocké *dans* un item de `LazyColumn`.

**Solution appliquée dans ce projet :**
- L’état des sets est “hoist” (remonté) au parent :
  - `exerciseSets: Map<exerciseId, List<RowState>>`
- Chaque ligne/set est “stateless” et pilotée par la valeur du state parent.

Fichiers à regarder !! :
- `FreeWorkoutPage.kt` ← 
- `WorkoutLivePage.kt` ← 

### 2) Caméra : FileProvider / URI & crash FileUriExposedException
Si on passe `file://...` URI à la caméra, Android peut crasher (`FileUriExposedException`).

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
