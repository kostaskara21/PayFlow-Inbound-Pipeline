# PayFlow Inbound Pipeline

A Spring Boot app that watches an SFTP server for CSV files of transactions.
Every 10 seconds it checks the server's `upload` folder, copies each new `.csv`
file into a local `downloads` folder, reads it, and prints its transactions.
Each file is processed only once, even after the app restarts.

## What you need

- Java 17 or newer
- Maven
- Docker

## First-time setup

Run these from the project folder.

### 1. Generate the SFTP server keys

The keys are not stored in the repository, so create your own:

```bash
mkdir sftp_host_keys
ssh-keygen -t ed25519 -f sftp_host_keys/ssh_host_ed25519_key -N ""
ssh-keygen -t rsa -b 4096 -f sftp_host_keys/ssh_host_rsa_key -N ""
```

### 2. Start the SFTP server

```bash
docker compose up -d
```

This starts a local SFTP server with:

| Setting  | Value       |
|----------|-------------|
| Host     | `localhost` |
| Port     | `2222`      |
| Username | `user`      |
| Password | `password`  |
| Folder   | `upload`    |

### 3. Run the app

```bash
mvn spring-boot:run
```

When it is ready, the console shows `PayFlow Inbound Pipeline initialized successfully!`.

## Upload a file to test it

Create a file called `Test1.csv` with this content:

```
transactionId,amount,currency,status
TXN-2026-001,250.50,USD,COMPLETED
TXN-2026-002,1500.00,EUR,PENDING
```

Connect to the server (the password is `password`):

```bash
sftp -P 2222 user@localhost
```

Then, at the `sftp>` prompt:

```
cd upload
put Test1.csv
bye
```

Within 10 seconds the app console prints `Processing New File: Test1.csv`
followed by the transactions in the file.

## Running the tests

The tests connect to the real SFTP server, so start it first
(`docker compose up -d`). Then run:

```bash
mvn test
```

They check that the app can log in, that the `upload` folder exists, and that
a file can be written to the server and read back.

## Folders created while running

Both are created automatically and are not stored in the repository.

- `downloads/` holds the local copies of the files taken from the SFTP server.
- `sftp-metadata/` holds the list of files that were already processed.
  Delete this folder and restart the app to process every file again.

## Stopping

```bash
docker compose down
```

## Note

The username and password are written in the code and in `docker-compose.yaml`.
This is only for the local test server. Never do this with a real server.
