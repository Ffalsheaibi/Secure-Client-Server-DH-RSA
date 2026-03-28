# Secure Client-Server using Diffie-Hellman and RSA

## Overview
This project demonstrates a simple secure communication system between a client and a server using Java.

It uses:
- Diffie-Hellman (DH) for key exchange
- RSA for encryption and decryption

## How it works
1. The server starts and listens on port 5002
2. The client connects to the server
3. Both exchange DH public keys
4. Both generate a shared secret
5. The server generates an RSA key pair
6. The client encrypts a message using RSA public key
7. The server decrypts the message using RSA private key

## Output
- The program prints:
  - Prime number (p)
  - Generator (g)
  - Decrypted message: Hello from client

## Technologies Used
- Java
- Socket Programming
- Java Cryptography (JCE)

## Files
- SimpleServer.java
- SimpleClient.java

## How to Run
1. Run SimpleServer.java
2. Run SimpleClient.java

## Example Output
Server:

[Server] Listening on 5002
p = ...
g = ...
Decrypted: Hello from client


Client:

[Client] Connected
p = ...
g = ...
[Client] Sent


## Notes
- DH and RSA use 2048-bit keys
- X.509 format is used for public key encoding
- Base64 is used to encode keys and messages
