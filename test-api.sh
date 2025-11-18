#!/bin/bash

BASE_URL="http://localhost:8080"
EMAIL="mahtrabelsi@gmail.com"

echo "🧪 Starting API Tests for Bridge Microservice"
echo "=============================================="
echo ""

# Test 1: Health check
echo "📋 Test 1: Health Check"
echo "GET $BASE_URL/actuator/health"
curl -s -X GET "$BASE_URL/actuator/health" | jq '.'
echo -e "\n"

# Test 2: Create Bridge User
echo "📋 Test 2: Create Bridge User"
echo "POST $BASE_URL/api/bridge/users"
USER_RESPONSE=$(curl -s -X POST "$BASE_URL/api/bridge/users" \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"$EMAIL\"}")

echo "$USER_RESPONSE" | jq '.'

USER_UUID=$(echo "$USER_RESPONSE" | jq -r '.uuid')
echo "User UUID: $USER_UUID"
echo -e "\n"

if [ "$USER_UUID" == "null" ] || [ -z "$USER_UUID" ]; then
    echo "❌ Failed to create user. Exiting."
    exit 1
fi

# Test 3: Generate Auth Token
echo "📋 Test 3: Generate Auth Token"
echo "POST $BASE_URL/api/bridge/users/$USER_UUID/token"
TOKEN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/bridge/users/$USER_UUID/token" \
  -H "Content-Type: application/json" \
  -d "{\"userUuid\": \"$USER_UUID\"}")

echo "$TOKEN_RESPONSE" | jq '.'

ACCESS_TOKEN=$(echo "$TOKEN_RESPONSE" | jq -r '.access_token')
echo "Access Token: ${ACCESS_TOKEN:0:50}..."
echo -e "\n"

if [ "$ACCESS_TOKEN" == "null" ] || [ -z "$ACCESS_TOKEN" ]; then
    echo "❌ Failed to generate token. Exiting."
    exit 1
fi

# Test 4: Create Connect Session
echo "📋 Test 4: Create Connect Session"
echo "POST $BASE_URL/api/bridge/users/$USER_UUID/connect"
CONNECT_RESPONSE=$(curl -s -X POST "$BASE_URL/api/bridge/users/$USER_UUID/connect" \
  -H "Content-Type: application/json" \
  -d "{
    \"userUuid\": \"$USER_UUID\",
    \"prefillEmail\": \"$EMAIL\"
  }")

echo "$CONNECT_RESPONSE" | jq '.'

CONNECT_URL=$(echo "$CONNECT_RESPONSE" | jq -r '.redirect_url')
echo "Connect URL: $CONNECT_URL"
echo -e "\n"

# Test 5: Get User Info
echo "📋 Test 5: Get User Info"
echo "GET $BASE_URL/api/bridge/users/$USER_UUID"
curl -s -X GET "$BASE_URL/api/bridge/users/$USER_UUID" | jq '.'
echo -e "\n"

# Test 6: Get Accounts (will be empty initially)
echo "📋 Test 6: Get Accounts for User"
echo "GET $BASE_URL/api/bridge/data/accounts?userUuid=$USER_UUID"
curl -s -X GET "$BASE_URL/api/bridge/data/accounts?userUuid=$USER_UUID" | jq '.'
echo -e "\n"

# Test 7: Get Transactions (will be empty initially)
echo "📋 Test 7: Get Transactions for User"
echo "GET $BASE_URL/api/bridge/data/transactions?userUuid=$USER_UUID"
curl -s -X GET "$BASE_URL/api/bridge/data/transactions?userUuid=$USER_UUID" | jq '.'
echo -e "\n"

echo "=============================================="
echo "✅ All tests completed!"
echo ""
echo "🔗 Next Steps:"
echo "1. Open this URL in your browser to connect a bank:"
echo "   $CONNECT_URL"
echo ""
echo "2. After connecting your bank, run these commands to see data:"
echo "   curl http://localhost:8080/api/bridge/data/accounts?userUuid=$USER_UUID | jq '.'"
echo "   curl http://localhost:8080/api/bridge/data/transactions?userUuid=$USER_UUID | jq '.'"
