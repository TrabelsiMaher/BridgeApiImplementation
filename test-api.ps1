# PowerShell script to test Bridge API Microservice
$BASE_URL = "http://localhost:8080"
$EMAIL = "mahtrabelsi@gmail.com"

Write-Host "🧪 Starting API Tests for Bridge Microservice" -ForegroundColor Cyan
Write-Host "==============================================" -ForegroundColor Cyan
Write-Host ""

# Test 1: Health check
Write-Host "📋 Test 1: Health Check" -ForegroundColor Yellow
Write-Host "GET $BASE_URL/actuator/health"
$response = Invoke-RestMethod -Uri "$BASE_URL/actuator/health" -Method Get
$response | ConvertTo-Json -Depth 10
Write-Host "`n"

# Test 2: Create Bridge User
Write-Host "📋 Test 2: Create Bridge User" -ForegroundColor Yellow
Write-Host "POST $BASE_URL/api/bridge/users"
$userBody = @{
    email = $EMAIL
} | ConvertTo-Json

try {
    $userResponse = Invoke-RestMethod -Uri "$BASE_URL/api/bridge/users" -Method Post -Body $userBody -ContentType "application/json"
    $userResponse | ConvertTo-Json -Depth 10
    $USER_UUID = $userResponse.uuid
    Write-Host "User UUID: $USER_UUID" -ForegroundColor Green
    Write-Host "`n"
} catch {
    Write-Host "❌ Failed to create user. Exiting." -ForegroundColor Red
    exit 1
}

if ([string]::IsNullOrEmpty($USER_UUID)) {
    Write-Host "❌ Failed to create user. Exiting." -ForegroundColor Red
    exit 1
}

# Test 3: Generate Auth Token
Write-Host "📋 Test 3: Generate Auth Token" -ForegroundColor Yellow
Write-Host "POST $BASE_URL/api/bridge/users/$USER_UUID/token"
$tokenBody = @{
    userUuid = $USER_UUID
} | ConvertTo-Json

try {
    $tokenResponse = Invoke-RestMethod -Uri "$BASE_URL/api/bridge/users/$USER_UUID/token" -Method Post -Body $tokenBody -ContentType "application/json"
    $tokenResponse | ConvertTo-Json -Depth 10
    $ACCESS_TOKEN = $tokenResponse.access_token
    Write-Host "Access Token: $($ACCESS_TOKEN.Substring(0, [Math]::Min(50, $ACCESS_TOKEN.Length)))..." -ForegroundColor Green
    Write-Host "`n"
} catch {
    Write-Host "❌ Failed to generate token. Exiting." -ForegroundColor Red
    exit 1
}

if ([string]::IsNullOrEmpty($ACCESS_TOKEN)) {
    Write-Host "❌ Failed to generate token. Exiting." -ForegroundColor Red
    exit 1
}

# Test 4: Create Connect Session
Write-Host "📋 Test 4: Create Connect Session" -ForegroundColor Yellow
Write-Host "POST $BASE_URL/api/bridge/users/$USER_UUID/connect"
$connectBody = @{
    userUuid = $USER_UUID
    prefillEmail = $EMAIL
} | ConvertTo-Json

try {
    $connectResponse = Invoke-RestMethod -Uri "$BASE_URL/api/bridge/users/$USER_UUID/connect" -Method Post -Body $connectBody -ContentType "application/json"
    $connectResponse | ConvertTo-Json -Depth 10
    $CONNECT_URL = $connectResponse.redirect_url
    Write-Host "Connect URL: $CONNECT_URL" -ForegroundColor Green
    Write-Host "`n"
} catch {
    Write-Host "⚠️ Failed to create connect session." -ForegroundColor Yellow
    Write-Host "`n"
}

# Test 5: Get User Info
Write-Host "📋 Test 5: Get User Info" -ForegroundColor Yellow
Write-Host "GET $BASE_URL/api/bridge/users/$USER_UUID"
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/api/bridge/users/$USER_UUID" -Method Get
    $response | ConvertTo-Json -Depth 10
} catch {
    Write-Host "Error getting user info" -ForegroundColor Red
}
Write-Host "`n"

# Test 6: Get Accounts (will be empty initially)
Write-Host "📋 Test 6: Get Accounts for User" -ForegroundColor Yellow
Write-Host "GET $BASE_URL/api/bridge/data/accounts?userUuid=$USER_UUID"
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/api/bridge/data/accounts?userUuid=$USER_UUID" -Method Get
    $response | ConvertTo-Json -Depth 10
} catch {
    Write-Host "Error getting accounts" -ForegroundColor Red
}
Write-Host "`n"

# Test 7: Get Transactions (will be empty initially)
Write-Host "📋 Test 7: Get Transactions for User" -ForegroundColor Yellow
Write-Host "GET $BASE_URL/api/bridge/data/transactions?userUuid=$USER_UUID"
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/api/bridge/data/transactions?userUuid=$USER_UUID" -Method Get
    $response | ConvertTo-Json -Depth 10
} catch {
    Write-Host "Error getting transactions" -ForegroundColor Red
}
Write-Host "`n"

Write-Host "==============================================" -ForegroundColor Cyan
Write-Host "✅ All tests completed!" -ForegroundColor Green
Write-Host ""
Write-Host "🔗 Next Steps:" -ForegroundColor Cyan
Write-Host "1. Open this URL in your browser to connect a bank:"
Write-Host "   $CONNECT_URL" -ForegroundColor Blue
Write-Host ""
Write-Host "2. After connecting your bank, run these commands to see data:" -ForegroundColor Cyan
Write-Host "   Invoke-RestMethod -Uri 'http://localhost:8080/api/bridge/data/accounts?userUuid=$USER_UUID' | ConvertTo-Json -Depth 10"
Write-Host "   Invoke-RestMethod -Uri 'http://localhost:8080/api/bridge/data/transactions?userUuid=$USER_UUID' | ConvertTo-Json -Depth 10"
