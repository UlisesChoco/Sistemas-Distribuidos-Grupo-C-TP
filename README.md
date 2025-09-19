# Sistemas Distribuidos - Grupo C - TP
Este proyecto implementa un sistema con **gRPC (Java/Spring Boot)**, **API Gateway (Node/Express)**, **MySQL** y **Mailhog** para pruebas de envío de correos.

---

## 🚀 Tecnologías usadas
- **Java 17 + Spring Boot** → Servidor gRPC
- **Node.js + Express** → API Gateway (REST → gRPC)
- **MySQL 8** → Base de datos
- **Mailhog** → Captura de correos (testing)
- **Docker + Docker Compose** → Orquestación de servicios

---
## Integrantes
- Leandro Aranda  
- Guido Huidobro  
- Ulises Justo Saucedo  
- Mateo Rivas
---
## 📋 Prerrequisitos

Antes de comenzar, se debe tener instalado:
- Docker 🐳
- Git 📦

## 1. Clonar el repositorio

```bash
git clone https://github.com/UlisesChoco/Sistemas-Distribuidos-Grupo-C-TP.git
cd Sistemas-Distribuidos-Grupo-C-TP
```

## 2. Configurar variables de entorno
Copiar el archivo de ejemplo y completar según sea necesario:
```bash
cp .env.template .env
```
En el archivo .env se debe definir:

Credenciales de MySQL

Configuración de la datasource de Spring Boot

JWT Key

Configuración de Mailhog

Ejemplo mínimo:
```python
MYSQL_ROOT_PASSWORD=root
MYSQL_DB_NAME=sist_distribuidos
MYSQL_DB_USERNAME=admin
MYSQL_DB_PASSWORD=admin

SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/sist_distribuidos?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=admin
SPRING_DATASOURCE_PASSWORD=admin

JWT_PRIVATE_KEY=secret
SPRING_SECURITY_JWT_USER_GENERATOR=AUTH-SPRING-BACKEND
```
## 3. Levantar los servicios

```bash
docker compose up --build
```
Esto levanta:

* db: MySQL con la base definida en .env

* grpc-server: aplicación Spring Boot

* mailhog: servidor SMTP falso para pruebas de correo
## 4. Verificar servicios

* Servidor (gRPC server):
Corre en http://localhost:9090 (ajustar según config del proyecto).

* Cliente (gRPC client):
Corre en http://localhost:8080 (ajustar según config del proyecto).

* MySQL:
Puerto 3306. Usuario/clave los definidos en .env.

* Mailhog (UI web):
http://localhost:8025

## 🧪 Probar Mailhog

Mailhog captura todos los mails enviados desde la app.

* Entrar a http://localhost:8025

* Hacer una petición que envíe mail (ejemplo: endpoint /send-test-mail).

* El mail aparecerá en la UI de Mailhog.

