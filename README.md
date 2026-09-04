# AgriConnect Niger 🌾

Marketplace agricole qui connecte **directement les agriculteurs et les acheteurs** du Niger :
grossistes, marchés urbains, transporteurs. Avec prix du marché en temps réel, météo agricole
et logistique intégrée — pensé pour fonctionner **même à faible bande passante** (PWA + SMS/USSD).

> Projet MVP complet (full-stack) : backend Spring Boot, frontend React (PWA),
> PostgreSQL + PostGIS, notifications SMS/USSD.

---

## 🧩 Fonctionnalités

| Domaine | Détails |
|---|---|
| 🛒 **Marketplace** | Publication d'offres de vente, recherche/filtres (produit, région, prix, mot-clé), commande en ligne |
| 📊 **Prix du marché** | Cotations FCFA par produit/région (source SIMA), alertes de prix |
| 🌤️ **Météo agricole** | Prévisions + conseils aux cultures par région |
| 🚚 **Logistique** | Itinéraires, distance, durée, coût/kg, transporteur attribué automatiquement |
| 📦 **Workflow commande** | `EN ATTENTE → CONFIRMÉE → PRÉPARÉE → EN TRANSIT → LIVRÉE` (ou `ANNULÉE`), rôles contrôlés |
| 🔐 **Comptes & rôles** | Agriculteur, Acheteur, Transporteur, Admin — JWT |
| ⭐ **Confiance** | Évaluations/notes entre utilisateurs |
| 🔔 **Alertes** | In-app, SMS (simulateur) ; menu USSD de démonstration |
| 📱 **PWA** | Installation sur mobile, cache hors-ligne, JS gzip ≈ 63 KB |

---

## 🏗️ Architecture

```
┌─────────────────┐      REST /api      ┌──────────────────────────┐
│  Frontend React │ ───────────────────▶│   Backend Spring Boot 3  │
│  (Vite + PWA)   │◀─────────────────── │   Security + JWT          │
└─────────────────┘                     │   Workflow commande       │
        │                               │   Services métier         │
        └── SMS / USSD (zones sans      └────────────┬─────────────┘
            smartphone)                              │ JPA
                                         ┌───────────▼─────────────┐
                                         │  PostgreSQL + PostGIS   │
                                         │  (H2 en dev)            │
                                         └─────────────────────────┘
```

### Stack
- **Backend** : Java 17, Spring Boot 3.3, Spring Security (JWT jjwt), Spring Data JPA, Validation, Lombok
- **Frontend** : React 18, Vite 5, React Router, CSS pur (léger), Service Worker
- **Base de données** : PostgreSQL 16 + extension **PostGIS** (profil `postgres`), H2 en mémoire (profil `dev`)
- **SMS/USSD** : interface `SmsService` + simulateur (`MockSmsService`) prêt à être branché sur Africa's Talking

### Workflow de commande (équivalent BPM)
Implémenté comme machine à états dans `OrderService` (transitions validées par rôle) :

```
PENDING ──confirmé──▶ CONFIRMED ──préparée──▶ PACKED ──en transit──▶ IN_TRANSIT ──livrée──▶ DELIVERED
   │                    │                        │
   └───▶ CANCELLED ◀────┴────────────────────────┘
```
Chaque transition notifie l'acheteur (SMS), le vendeur et le transporteur.
Une offre devient `RÉSERVÉE` à la commande (anti-double-book) puis `CLÔTURÉE` à la livraison.

---

## 🚀 Démarrage rapide

### Prérequis
- Java 17+ et Maven 3.9+
- Node.js 18+
- Docker (optionnel, pour PostgreSQL)

### 1. Backend (profil dev — H2 en mémoire, données pré-chargées)

```bash
cd backend
mvn spring-boot:run
# API : http://localhost:8080  ·  console H2 : /h2-console (jdbc:h2:mem:agriconnect, user sa)
```

### 2. Frontend

```bash
cd frontend
npm install
npm run dev
# Application : http://localhost:5173  (proxy /api → :8080)
```

### 3. Backend avec PostgreSQL + PostGIS (production locale)

```bash
docker compose up -d          # démarre postgresql + postgis sur :5432
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

---

## 👤 Comptes de démonstration

| Rôle | Téléphone | Mot de passe |
|---|---|---|
| Administrateur | 97000000 | admin123 |
| Agriculteur (Zinder) | 97000001 | farmer123 |
| Acheteur / Grossiste | 97000010 | buyer123 |
| Transporteur | 97000020 | transport123 |

---

## 📡 API (extrait)

| Méthode | Route | Accès | Description |
|---|---|---|---|
| POST | `/api/auth/register` | public | Inscription |
| POST | `/api/auth/login` | public | Connexion (téléphone + mot de passe) |
| GET | `/api/offers` | public | Offres actives + filtres (`productId`, `region`, `maxPrice`, `q`) |
| POST | `/api/offers` | Agriculteur/Admin | Publier une offre |
| PATCH | `/api/offers/{id}/status` | Propriétaire | ACTIVE / CLOSED |
| POST | `/api/orders` | Acheteur/Admin | Créer une commande (panier multi-offres) |
| PATCH | `/api/orders/{id}/status` | par rôle | Transition du workflow |
| GET | `/api/prices` | public | Prix du marché (`productId`, `region`) |
| GET | `/api/weather` | public | Météo (`region`) |
| GET | `/api/transport` | public | Itinéraires (`fromRegion`, `toRegion`) |
| GET | `/api/stats/dashboard` | connecté | Statistiques tableau de bord |
| GET/PATCH | `/api/notifications*` | connecté | Alertes |
| POST | `/api/reviews/{id}` | connecté | Évaluer un utilisateur |
| POST | `/api/ussd` | public | Menu USSD (simulation SMS/feature phone) |

---

## 📱 Mobile & zones sans smartphone

- **App légère** : la PWA s'installe sur Android/iOS, cache les pages, et le bundle ne pèse que **63 KB gzip**.
- **SMS** : `MockSmsService` journalise chaque envoi. Pour la production, implémentez
  `SmsService` avec le SDK **Africa's Talking** (clés dans `application.yml` → `app.sms.africa-talking`).
- **USSD** : endpoint `/api/ussd` simule le menu de consultation (prix, commandes, météo)
  compatible passerelle USSD (`sessionId`, `text`, réponse `CON`/`END`).

---

## 🗺️ Géolocalisation (PostGIS)

Les entités stockent `latitude`/`longitude` + région. En production, l'extension **PostGIS**
permet d'ajouter les requêtes spatiales : recherche d'offres "à moins de X km",
calcul de distance, rayon de livraison — via `ST_Distance` / `ST_DWithin` sur un index `GIST`.
Les coordonnées des lieux de vente sont pré-chargées dans le seed.

---

## 🚦 Roadmap / améliorations proposées

- **Paiement mobile** : Orange Money / Moov Money (abstraction `PaymentService`, statut de règlement dans le workflow)
- **Achat groupé / coopératives** : commandes mutualisées par organisation
- **Alertes prix personnalisées** : seuils par produit (SMS/email)
- **Entrepôts & qualité** : certification des lots, photos, traçabilité
- **Multi-langue** : FR + Haoussa / Zarma (i18n)
- **Statistiques export** : rapports SIMA, tendances saisonnières
- **App native** : wrapper Capacitor pour publication Store
- **OpenAPI/Swagger** : documentation automatique des endpoints

---

## 🛡️ Notes de production

- Remplacez `app.jwt.secret` et les mots de passe de base de données.
- Utilisez le profil `postgres` (le dev utilise H2 en mémoire, données perdues au redémarrage).
- HTTPS obligatoire pour le service worker (PWA) et les identifiants.
- `GlobalExceptionHandler` centralise les erreurs API (format JSON uniforme).
