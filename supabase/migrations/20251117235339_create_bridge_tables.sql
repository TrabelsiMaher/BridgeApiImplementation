/*
  # Bridge API Microservice Database Schema

  ## Overview
  Creates database tables to store Bridge API data including users, items, accounts, and transactions.

  ## Tables Created

  ### 1. bridge_users
  Stores Bridge user information
  - `id` (uuid, primary key) - Internal identifier
  - `bridge_uuid` (text, unique, not null) - Bridge API user UUID
  - `email` (text, not null) - User email address
  - `external_user_id` (text) - Optional external reference ID
  - `created_at` (timestamptz) - Creation timestamp
  - `updated_at` (timestamptz) - Last update timestamp

  ### 2. bridge_items
  Stores bank connections (items) for users
  - `id` (uuid, primary key) - Internal identifier
  - `item_id` (text, unique, not null) - Bridge API item ID
  - `user_uuid` (text, not null) - Reference to Bridge user UUID
  - `provider_id` (integer) - Bank provider ID
  - `provider_name` (text) - Bank provider name
  - `status` (text, not null) - Connection status
  - `status_code_info` (text) - Status code information
  - `status_code_description` (text) - Status description
  - `created_at` (timestamptz) - Creation timestamp
  - `updated_at` (timestamptz) - Last update timestamp

  ### 3. bridge_accounts
  Stores bank account information
  - `id` (uuid, primary key) - Internal identifier
  - `account_id` (text, unique, not null) - Bridge API account ID
  - `item_id` (text, not null) - Reference to item ID
  - `name` (text, not null) - Account name
  - `balance` (numeric) - Account balance
  - `currency` (text) - Currency code
  - `type` (text) - Account type
  - `status` (text) - Account status
  - `iban` (text) - IBAN if available
  - `created_at` (timestamptz) - Creation timestamp
  - `updated_at` (timestamptz) - Last update timestamp

  ### 4. bridge_transactions
  Stores financial transactions
  - `id` (uuid, primary key) - Internal identifier
  - `transaction_id` (text, unique, not null) - Bridge API transaction ID
  - `account_id` (text, not null) - Reference to account ID
  - `description` (text, not null) - Transaction description
  - `amount` (numeric, not null) - Transaction amount
  - `currency` (text) - Currency code
  - `date` (date) - Transaction date
  - `operation_type` (text) - Type of operation
  - `category_id` (integer) - Category ID
  - `category_name` (text) - Category name
  - `is_deleted` (boolean) - Deletion flag
  - `created_at` (timestamptz) - Creation timestamp
  - `updated_at` (timestamptz) - Last update timestamp

  ## Security
  - RLS enabled on all tables
  - Policies allow authenticated users to manage their own data
  - Users can only access data linked to their Bridge UUID

  ## Indexes
  - Created on foreign key columns for performance
  - Created on frequently queried columns
*/

CREATE TABLE IF NOT EXISTS bridge_users (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    bridge_uuid text UNIQUE NOT NULL,
    email text NOT NULL,
    external_user_id text,
    created_at timestamptz DEFAULT now(),
    updated_at timestamptz DEFAULT now()
);

ALTER TABLE bridge_users ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can view own data"
    ON bridge_users FOR SELECT
    TO authenticated
    USING (auth.uid()::text = external_user_id);

CREATE POLICY "Users can insert own data"
    ON bridge_users FOR INSERT
    TO authenticated
    WITH CHECK (auth.uid()::text = external_user_id);

CREATE POLICY "Users can update own data"
    ON bridge_users FOR UPDATE
    TO authenticated
    USING (auth.uid()::text = external_user_id)
    WITH CHECK (auth.uid()::text = external_user_id);

CREATE INDEX IF NOT EXISTS idx_bridge_users_email ON bridge_users(email);
CREATE INDEX IF NOT EXISTS idx_bridge_users_bridge_uuid ON bridge_users(bridge_uuid);

CREATE TABLE IF NOT EXISTS bridge_items (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    item_id text UNIQUE NOT NULL,
    user_uuid text NOT NULL,
    provider_id integer,
    provider_name text,
    status text NOT NULL,
    status_code_info text,
    status_code_description text,
    created_at timestamptz DEFAULT now(),
    updated_at timestamptz DEFAULT now()
);

ALTER TABLE bridge_items ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can view own items"
    ON bridge_items FOR SELECT
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM bridge_users
            WHERE bridge_users.bridge_uuid = bridge_items.user_uuid
            AND bridge_users.external_user_id = auth.uid()::text
        )
    );

CREATE POLICY "Users can insert own items"
    ON bridge_items FOR INSERT
    TO authenticated
    WITH CHECK (
        EXISTS (
            SELECT 1 FROM bridge_users
            WHERE bridge_users.bridge_uuid = bridge_items.user_uuid
            AND bridge_users.external_user_id = auth.uid()::text
        )
    );

CREATE POLICY "Users can update own items"
    ON bridge_items FOR UPDATE
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM bridge_users
            WHERE bridge_users.bridge_uuid = bridge_items.user_uuid
            AND bridge_users.external_user_id = auth.uid()::text
        )
    )
    WITH CHECK (
        EXISTS (
            SELECT 1 FROM bridge_users
            WHERE bridge_users.bridge_uuid = bridge_items.user_uuid
            AND bridge_users.external_user_id = auth.uid()::text
        )
    );

CREATE INDEX IF NOT EXISTS idx_bridge_items_user_uuid ON bridge_items(user_uuid);
CREATE INDEX IF NOT EXISTS idx_bridge_items_item_id ON bridge_items(item_id);

CREATE TABLE IF NOT EXISTS bridge_accounts (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id text UNIQUE NOT NULL,
    item_id text NOT NULL,
    name text NOT NULL,
    balance numeric,
    currency text,
    type text,
    status text,
    iban text,
    created_at timestamptz DEFAULT now(),
    updated_at timestamptz DEFAULT now()
);

ALTER TABLE bridge_accounts ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can view own accounts"
    ON bridge_accounts FOR SELECT
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM bridge_items
            JOIN bridge_users ON bridge_users.bridge_uuid = bridge_items.user_uuid
            WHERE bridge_items.item_id = bridge_accounts.item_id
            AND bridge_users.external_user_id = auth.uid()::text
        )
    );

CREATE POLICY "Users can insert own accounts"
    ON bridge_accounts FOR INSERT
    TO authenticated
    WITH CHECK (
        EXISTS (
            SELECT 1 FROM bridge_items
            JOIN bridge_users ON bridge_users.bridge_uuid = bridge_items.user_uuid
            WHERE bridge_items.item_id = bridge_accounts.item_id
            AND bridge_users.external_user_id = auth.uid()::text
        )
    );

CREATE POLICY "Users can update own accounts"
    ON bridge_accounts FOR UPDATE
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM bridge_items
            JOIN bridge_users ON bridge_users.bridge_uuid = bridge_items.user_uuid
            WHERE bridge_items.item_id = bridge_accounts.item_id
            AND bridge_users.external_user_id = auth.uid()::text
        )
    )
    WITH CHECK (
        EXISTS (
            SELECT 1 FROM bridge_items
            JOIN bridge_users ON bridge_users.bridge_uuid = bridge_items.user_uuid
            WHERE bridge_items.item_id = bridge_accounts.item_id
            AND bridge_users.external_user_id = auth.uid()::text
        )
    );

CREATE INDEX IF NOT EXISTS idx_bridge_accounts_item_id ON bridge_accounts(item_id);
CREATE INDEX IF NOT EXISTS idx_bridge_accounts_account_id ON bridge_accounts(account_id);

CREATE TABLE IF NOT EXISTS bridge_transactions (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    transaction_id text UNIQUE NOT NULL,
    account_id text NOT NULL,
    description text NOT NULL,
    amount numeric NOT NULL,
    currency text,
    date date,
    operation_type text,
    category_id integer,
    category_name text,
    is_deleted boolean DEFAULT false,
    created_at timestamptz DEFAULT now(),
    updated_at timestamptz DEFAULT now()
);

ALTER TABLE bridge_transactions ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can view own transactions"
    ON bridge_transactions FOR SELECT
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM bridge_accounts
            JOIN bridge_items ON bridge_items.item_id = bridge_accounts.item_id
            JOIN bridge_users ON bridge_users.bridge_uuid = bridge_items.user_uuid
            WHERE bridge_accounts.account_id = bridge_transactions.account_id
            AND bridge_users.external_user_id = auth.uid()::text
        )
    );

CREATE POLICY "Users can insert own transactions"
    ON bridge_transactions FOR INSERT
    TO authenticated
    WITH CHECK (
        EXISTS (
            SELECT 1 FROM bridge_accounts
            JOIN bridge_items ON bridge_items.item_id = bridge_accounts.item_id
            JOIN bridge_users ON bridge_users.bridge_uuid = bridge_items.user_uuid
            WHERE bridge_accounts.account_id = bridge_transactions.account_id
            AND bridge_users.external_user_id = auth.uid()::text
        )
    );

CREATE POLICY "Users can update own transactions"
    ON bridge_transactions FOR UPDATE
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM bridge_accounts
            JOIN bridge_items ON bridge_items.item_id = bridge_accounts.item_id
            JOIN bridge_users ON bridge_users.bridge_uuid = bridge_items.user_uuid
            WHERE bridge_accounts.account_id = bridge_transactions.account_id
            AND bridge_users.external_user_id = auth.uid()::text
        )
    )
    WITH CHECK (
        EXISTS (
            SELECT 1 FROM bridge_accounts
            JOIN bridge_items ON bridge_items.item_id = bridge_accounts.item_id
            JOIN bridge_users ON bridge_users.bridge_uuid = bridge_items.user_uuid
            WHERE bridge_accounts.account_id = bridge_transactions.account_id
            AND bridge_users.external_user_id = auth.uid()::text
        )
    );

CREATE INDEX IF NOT EXISTS idx_bridge_transactions_account_id ON bridge_transactions(account_id);
CREATE INDEX IF NOT EXISTS idx_bridge_transactions_date ON bridge_transactions(date);
CREATE INDEX IF NOT EXISTS idx_bridge_transactions_transaction_id ON bridge_transactions(transaction_id);
