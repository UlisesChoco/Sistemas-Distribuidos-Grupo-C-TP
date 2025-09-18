# Instrucciones para levantar el cliente

## 1. Instalar dependencias del cliente gRPC
```bash
cd "./grpc client"
npm install
```

## 2. Configurar las variables de entorno (archivo .env-example)
- Creá una copia de .env-example y modificá su nombre a: .env
- Configurá la variable de entorno JWT_PRIVATE_KEY (debe coincidir con la key JWT que usamos en el server)

## 3. Ejecutar Express
```bash
cd express/
node --env-file=../.env app.js
```

