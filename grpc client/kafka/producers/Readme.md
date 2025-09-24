# Productor Kafka

Este script en Python permite enviar mensajes a distintos topics de Kafka. Los datos se leen desde archivos `.json` y se publican en los siguientes topics:

- `solicitud-donaciones`
- `oferta-donaciones`
- `eventos-solidarios`

## Requisitos

- Python 3.10+

Instalar la librería necesaria con:

```bash
pip install kafka-python
```

Para ejecutar el script, abrí una terminal en el directorio del productor y ejecutá el siguiente comando:

```bash
python producer.py
```

Seleccioná la opción deseada para enviar los mensajes al topic correspondiente.
