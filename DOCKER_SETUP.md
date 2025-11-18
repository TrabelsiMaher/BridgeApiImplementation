# Docker Setup Guide

## Prerequisites

- Docker installed on your machine
- Docker Compose installed
- Supabase database password

## Setup Instructions

### 1. Configure Database Password

Edit the `.env` file and replace `YOUR_DB_PASSWORD` with your actual Supabase database password:

```bash
DB_PASSWORD=your_actual_password_here
```

You can find your database password in the Supabase dashboard:
https://supabase.com/dashboard/project/jqicherasceexeaxbzxv/settings/database

### 2. Build and Start the Application

```bash
docker-compose up --build
```

This will:
- Build the Docker image with Maven and Java 21
- Start the Spring Boot application on port 8080
- Connect to your Supabase database

### 3. Run API Tests

In a new terminal, make the test script executable and run it:

```bash
chmod +x test-api.sh
./test-api.sh
```

The test script will:
1. Create a Bridge user with email `mahtrabelsi@gmail.com`
2. Generate an authentication token
3. Create a connect session
4. Fetch user information
5. Try to fetch accounts and transactions (empty until you connect a bank)

### 4. Connect a Bank Account

After running the tests, you'll get a URL like:
```
https://connect.bridgeapi.io/...
```

Open this URL in your browser to connect a bank account through Bridge.

### 5. Verify Data Synchronization

After connecting your bank, wait a few moments for Bridge to sync, then fetch data:

```bash
# Get accounts
curl "http://localhost:8080/api/bridge/data/accounts?userUuid=<YOUR_USER_UUID>" | jq '.'

# Get transactions
curl "http://localhost:8080/api/bridge/data/transactions?userUuid=<YOUR_USER_UUID>" | jq '.'
```

## Available Endpoints

### User Management
- `POST /api/bridge/users` - Create a Bridge user
- `GET /api/bridge/users/{uuid}` - Get user info
- `POST /api/bridge/users/{uuid}/token` - Generate auth token
- `POST /api/bridge/users/{uuid}/connect` - Create connect session

### Data Fetching
- `GET /api/bridge/data/accounts?userUuid={uuid}` - Get user accounts
- `GET /api/bridge/data/transactions?userUuid={uuid}` - Get user transactions
- `POST /api/bridge/data/sync/{itemId}` - Force sync an item

### Account Selection
- `GET /api/bridge/accounts/{userUuid}/selected` - Get selected account
- `POST /api/bridge/accounts/{userUuid}/select/{accountId}` - Select an account

### Webhooks
- `POST /api/bridge/webhook` - Receive Bridge webhooks

## Troubleshooting

### Application won't start
- Check that port 8080 is not already in use
- Verify your database password is correct in `.env`
- Check Docker logs: `docker-compose logs -f`

### Database connection error
- Ensure your Supabase database password is correct
- Check that your IP is allowed in Supabase settings

### Bridge API errors
- Verify your Bridge API credentials in `.env`
- Check Bridge API status

## Stopping the Application

```bash
docker-compose down
```

To also remove the built image:
```bash
docker-compose down --rmi all
```
