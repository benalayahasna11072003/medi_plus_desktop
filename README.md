# MediPluse - Application Web de Télémédecine

## 🧠 Overview

**MediPluse** est une application web de télémédecine développée en **JavaFX** avec une base de données relationnelle. Ce projet a été réalisé dans le cadre du module **PIDEV 3A** à **Esprit School of Engineering** durant l’année universitaire 2024–2025. L'objectif principal est de faciliter la gestion des consultations médicales à distance, tout en intégrant des fonctionnalités avancées pour le patient et le médecin.

## 🧩 Fonctionnalités

### 🔒 Gestion Utilisateur
- Deux rôles principaux : **Médecin** et **Patient**
- Connexion et inscription sécurisées
- Liaison avec toutes les autres entités (consultations, prescriptions, rendez-vous, avis…)

### 🗓️ Gestion des Rendez-vous
- Le patient doit avoir un rendez-vous validé pour accéder à la consultation
- Système de planification avec filtrage par date, spécialité et médecin

### 👩‍⚕️ Consultation & Prescription
- Consultation en ligne disponible après validation du rendez-vous
- Création et gestion des **prescriptions médicales**
- Export des prescriptions en **PDF**
- Statistiques et historique des consultations

### ✉️ Mailing
- Envoi automatique de mails pour confirmation de rendez-vous, rappel et notifications diverses

### 🔍 Recherche & Filtrage
- Recherche intelligente avec **auto-complétion**
- Tri dynamique des listes (par date, nom, spécialité…)

### 📢 Avis et Réponses
- Système d’avis et contre-avis sur les professionnels
- Patients peuvent noter les médecins avec **rating (étoiles)**
- Fonction de réponse aux avis

### 🌐 Traduction
- Traduction dynamique des contenus dans différentes langues

### 🚫 Détection de Mots Inappropriés
- **Algorithme de filtrage de "bad words"** dans les champs d’avis, commentaires, etc.

### 📊 Statistiques
- Statistiques sur les  avis
- Rapports graphiques pour le médecin

## 🛠️ Technologies utilisées

### Frontend
- **JavaFX** (interface riche en composants dynamiques)
- **CSS** pour le stylage

### Backend
- **Java** (POO & JavaFX controller)
- **JDBC** pour l’accès aux données
- **SQL** (structure fournie dans `mediplus.sql`)

### Outils additionnels
- **iText** ou équivalent pour la génération de PDF
- **JavaMail API** pour les emails
- **Google Translate API** ou équivalent pour la traduction
- Algorithme maison pour le filtrage des mots offensants

## 🗃️ Structure du projet


## ✅ Pré-requis

- **Java 21**
- **SceneBuilder** (pour JavaFX)
- **MySQL ou MariaDB**
- IDE comme IntelliJ IDEA ou Eclipse
- Bibliothèques externes : iText, JavaMail, JDBC, JSON API, etc.

## 🚀 Lancement du projet

1. Importer le projet dans votre IDE Java.
2. Configurer la base de données avec le script `mediplus.sql`.
3. Mettre à jour les identifiants DB dans le fichier de config.
4. Exécuter `Main.java`.

## 🔖 Topics GitHub

`java` `javafx` `telemedicine` `web-development` `pdf-generation` `rating-system` `i18n` `bad-words-filter` `email-sender` `esprit-school-of-engineering`

## 🙏 Remerciements

Projet réalisé sous la supervision de l’équipe pédagogique de **Esprit School of Engineering**.

---

## 💡 Hébergement (optionnel)

Le projet peut être hébergé sur :
- [GitHub Pages (pour frontend HTML)](https://pages.github.com/) *(non applicable ici directement)*
- [Heroku](https://www.heroku.com/) (avec un backend REST si externalisé)
- Serveur local Tomcat avec API REST future (si évolution envisagée)

---

## 📫 Contact

Pour toute question, suggestion ou collaboration, veuillez contacter :  
`medipluse.support@esprit.tn`

---

