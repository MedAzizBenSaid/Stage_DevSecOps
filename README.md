# Student Management System — Application 3-Tiers

Application de gestion des étudiants, structurée selon une architecture **3-tiers** classique.

---

## 1. Identification des trois couches

| Couche | Technologie | Dossier | Rôle |
|---|---|---|---|
| **Frontend (présentation)** | Angular 17 (standalone components) + Bootstrap | `frontend/` | Interface utilisateur, formulaires, consommation de l'API REST |
| **Backend (métier)** | Spring Boot 3 (Java 17) | `backend/` | API REST, logique métier, calculs de moyennes, validations |
| **Base de données (persistance)** | MySQL 8 | externe (service local ou serveur dédié) | Stockage des classes, étudiants, matières, notes |

Ces trois couches sont **totalement indépendantes** :
- Le frontend ne parle jamais directement à la base de données.
- Le backend ne connaît rien de l'interface graphique.
- La base de données ne connaît rien de la logique métier (tout est dans les Services Spring Boot).

Cette séparation est ce qui permettra, à l'étape suivante du projet, de conteneuriser
chaque couche indépendamment (un conteneur frontend, un conteneur backend, un conteneur
ou volume dédié à la base de données).

---

## 2. Récupérer le code source

Le code source complet est fourni dans ce zip. Structure :

```
student-management-system/
├── backend/                         <- Couche Backend
│   ├── pom.xml
│   └── src/main/java/com/studentmanagement/sms/
│       ├── entity/       (AcademicClass, Student, Subject, StudentGrade + enums)
│       ├── dto/
│       ├── mapper/
│       ├── repository/
│       ├── service/      (toute la logique métier)
│       ├── controller/   (endpoints REST)
│       ├── exception/
│       └── config/       (CORS)
│
├── frontend/                        <- Couche Frontend
│   └── src/app/
│       ├── models/
│       ├── services/     (appels HTTP vers le backend)
│       └── components/   (Dashboard, Classes, Students, Subjects, Grades, Results)
│
└── README.md
```

---

## 3. Tester l'application en local

### Prérequis

- Java 17
- Maven 3.8+
- Node.js 18+ et npm
- MySQL 8 installé et démarré en local (port 3306)

### 3.1 — Démarrer la base de données

Assure-toi que MySQL tourne en local. Aucune création manuelle de base n'est nécessaire :
Spring Boot la crée automatiquement au démarrage (`createDatabaseIfNotExist=true`).

Vérifie les identifiants dans `backend/src/main/resources/application.properties` :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/student_management_db?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=
```

Adapte `username` / `password` à ton installation locale si besoin.

### 3.2 — Démarrer le backend

```bash
cd backend
mvn spring-boot:run
```

Le backend démarre sur **http://localhost:8081**.

Vérifie qu'il répond correctement :

```bash
curl http://localhost:8081/api/classes
```

Tu dois recevoir une réponse JSON (probablement `[]` si la base est vide).

### 3.3 — Démarrer le frontend

Dans un **second terminal** :

```bash
cd frontend
npm install
npm start
```

Le frontend démarre sur **http://localhost:4200** et communique avec le backend
sur le port 8081 (CORS déjà configuré dans `CorsConfig.java`).

### 3.4 — Vérification de bout en bout

1. Ouvre `http://localhost:4200` dans le navigateur.
2. Va dans **Gestion des classes** → crée une classe (ex: `GL2`, niveau `2ème année`).
3. Va dans **Gestion des étudiants** → ajoute un étudiant dans cette classe.
4. Va dans **Gestion des matières** → ajoute des matières pour S1 et S2.
5. Va dans **Notes (Session Principale)** → sélectionne la classe, l'étudiant, saisis des notes, enregistre.
6. Va dans **Résultats** → vérifie que la moyenne et le statut s'affichent correctement.

Si toutes ces étapes fonctionnent, les **trois couches communiquent correctement** :
Frontend ↔ Backend ↔ Base de données.

---

## Logique métier clé (rappel)

- Chaque note est liée à un `sessionType` (`MAIN` ou `CONTROL`), ce qui permet à un
  étudiant d'avoir une note principale et une note de contrôle pour la même matière.
- Aucun résultat (moyenne, statut) n'est stocké en base : tout est recalculé à la
  volée par `StudentResultService` à partir des notes existantes.
- Un étudiant n'est jamais déclaré "Ajourné" directement après la session principale :
  s'il est en dessous de 10, il passe en `CONTROL_SESSION` avant un éventuel `FAILED`.

## Points d'API principaux

| Ressource | Endpoint |
|---|---|
| Classes | `GET/POST/PUT/DELETE /api/classes` |
| Étudiants | `GET/POST/PUT/DELETE /api/students`, `/api/students/class/{id}`, `/api/students/search?query=` |
| Matières | `GET/POST/PUT/DELETE /api/subjects`, `/api/subjects/class/{id}/semester/{semester}` |
| Notes | `GET /api/grades/student/{id}`, `POST /api/grades/batch` |
| Résultats | `GET /api/results/{studentId}`, `GET /api/results/control-session-students` |
| Dashboard | `GET /api/dashboard/stats` |

---

## Prochaine étape (hors périmètre de ce livrable)

Une fois le test local validé, l'étape suivante du projet (dockerisation,
docker-compose, déploiement Kubernetes) pourra s'appuyer directement sur cette
séparation en 3 dossiers (`backend/`, `frontend/`, DB externe).
