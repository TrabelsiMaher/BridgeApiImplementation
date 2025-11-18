# Bridge API Microservice

Microservice Spring Boot 3.2.x implémentant le workflow complet de Bridge API pour l'agrégation bancaire.

## Architecture

Le microservice implémente tous les endpoints nécessaires pour:
- Gestion des utilisateurs Bridge
- Authentification et génération de tokens
- Connexion aux comptes bancaires (Connect Sessions)
- Synchronisation des comptes et transactions
- Réception et traitement des webhooks

## Prérequis

- Java 17+
- Maven 3.8+
- PostgreSQL (Supabase configuré)
- Compte Bridge API

## Configuration

1. Récupérez vos credentials Bridge API depuis [Bridge Dashboard](https://dashboard.bridgeapi.io)

2. Configurez les variables d'environnement dans `.env`:

```env
SUPABASE_DB_URL=postgresql://postgres.jqicherasceexeaxbzxv:YOUR_DB_PASSWORD@aws-0-eu-west-1.pooler.supabase.com:6543/postgres
DB_PASSWORD=YOUR_DB_PASSWORD

BRIDGE_CLIENT_ID=YOUR_BRIDGE_CLIENT_ID
BRIDGE_CLIENT_SECRET=YOUR_BRIDGE_CLIENT_SECRET
```

## Installation

```bash
mvn clean install
```

## Démarrage

```bash
mvn spring-boot:run
```

Le microservice démarre sur `http://localhost:8080`

## API Endpoints

### Users

**Créer un utilisateur**
```http
POST /api/bridge/users
Content-Type: application/json

{
  "email": "user@example.com",
  "external_user_id": "optional-external-id"
}
```

**Obtenir un utilisateur par UUID**
```http
GET /api/bridge/users/{uuid}
```

**Générer un token d'authentification**
```http
POST /api/bridge/users/{uuid}/auth-token
```

**Créer une session de connexion bancaire**
```http
POST /api/bridge/users/connect-session
Authorization: Bearer {access_token}
Content-Type: application/json

{
  "user_uuid": "user-uuid",
  "user_email": "user@example.com",
  "redirect_url": "https://yourapp.com/callback"
}
```

### Data Synchronization

**Synchroniser toutes les données d'un utilisateur**
```http
POST /api/bridge/data/sync/{userUuid}
Authorization: Bearer {access_token}
```

**Synchroniser les comptes**
```http
POST /api/bridge/data/sync/accounts
Authorization: Bearer {access_token}
```

**Synchroniser les transactions**
```http
POST /api/bridge/data/sync/transactions?since=2025-01-01
Authorization: Bearer {access_token}
```

**Récupérer les items d'un utilisateur**
```http
GET /api/bridge/data/items/{userUuid}
```

**Récupérer les comptes d'un item**
```http
GET /api/bridge/data/accounts/{itemId}
```

**Récupérer les transactions d'un compte**
```http
GET /api/bridge/data/transactions/{accountId}
```

### Webhooks

**Endpoint webhook Bridge**
```http
POST /api/bridge/webhooks
Content-Type: application/json

{
  "type": "item.status.updated",
  "item_id": 123,
  "user_uuid": "user-uuid",
  "status": "ok"
}
```

## Workflow Bridge API

### 1. Créer un utilisateur
```bash
curl -X POST http://localhost:8080/api/bridge/users \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com"}'
```

### 2. Générer un token d'accès
```bash
curl -X POST http://localhost:8080/api/bridge/users/{uuid}/auth-token
```

### 3. Créer une session de connexion
```bash
curl -X POST http://localhost:8080/api/bridge/users/connect-session \
  -H "Authorization: Bearer {access_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "user_uuid": "{uuid}",
    "user_email": "user@example.com",
    "redirect_url": "https://yourapp.com/callback"
  }'
```

### 4. Synchroniser les données
```bash
curl -X POST http://localhost:8080/api/bridge/data/sync/{userUuid} \
  -H "Authorization: Bearer {access_token}"
```

## Sécurité

- Les webhooks sont validés contre les IPs Bridge autorisées
- RLS activé sur toutes les tables Supabase
- Les utilisateurs ne peuvent accéder qu'à leurs propres données
- Authentification Bearer token requise pour toutes les opérations sensibles

## Base de données

Le schéma Supabase inclut:
- `bridge_users` - Utilisateurs Bridge
- `bridge_items` - Connexions bancaires
- `bridge_accounts` - Comptes bancaires
- `bridge_transactions` - Transactions

Toutes les tables ont RLS activé avec des politiques restrictives.

## Logs

Les logs sont configurés au niveau DEBUG pour `com.bridgeapi` et INFO pour Spring.

## Build Production

```bash
mvn clean package
java -jar target/bridge-microservice-1.0.0.jar
```

## Documentation Bridge API

- [Documentation officielle](https://docs.bridgeapi.io)
- [Guide de migration 2025](https://docs.bridgeapi.io/docs/migration-guide-from-2021-to-2025-version)
- [Dashboard Bridge](https://dashboard.bridgeapi.io)
