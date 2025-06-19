# Order Processing API

[![Java Version](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Una API RESTful para el procesamiento asíncrono de órdenes con validaciones robustas y métricas de rendimiento.

## Características Principales

- 🚀 Procesamiento asíncrono de órdenes
- ✅ Validaciones de datos integradas
- 📊 Métricas de rendimiento con Micrometer
- 🧪 Cobertura completa de pruebas
- 🔄 Manejo de concurrencia
- 🛡️ Manejo centralizado de errores

## Requisitos Previos

- Java 17 o superior
- Maven 3.6+
- Spring Boot 3.x

## Instalación

1. Clona el repositorio:
   ```bash
   git clone [URL_DEL_REPOSITORIO]
   cd challenge
   ```

2. Compila el proyecto:
   ```bash
   ./mvnw clean install
   ```

3. Ejecuta la aplicación:
   ```bash
   ./mvnw spring-boot:run
   ```

## Uso

### Procesar una Orden

**Endpoint:** `POST /processOrder`

**Cuerpo de la solicitud:**
```json
{
  "customer": "Juan Pérez",
  "amount": 150.50,
  "items": [
    {
      "productId": 1,
      "name": "Producto 1",
      "quantity": 2,
      "unitPrice": 50.00
    },
    {
      "productId": 2,
      "name": "Producto 2",
      "quantity": 1,
      "unitPrice": 50.50
    }
  ]
}
```

**Respuesta exitosa (200 OK):**
```
Orden procesada exitosamente. ID: 12345
```

### Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/adrian/challenge/
│   │   ├── configs/         # Configuraciones
│   │   ├── controllers/     # Controladores REST
│   │   ├── exceptions/      # Excepciones personalizadas
│   │   ├── models/          # Modelos de dominio
│   │   ├── services/        # Lógica de negocio
│   │   └── validators/      # Validadores personalizados
│   └── resources/           # Archivos de configuración
└── test/                    # Pruebas
```

## Ejecutando las Pruebas

Para ejecutar las pruebas unitarias:
```bash
./mvnw test
```

Para ejecutar las pruebas de integración:
```bash
./mvnw verify
```

## Métricas

La aplicación expone métricas de rendimiento en `/actuator/metrics`. Las métricas incluyen:
- Tiempo de procesamiento de órdenes
- Conteo de órdenes procesadas
- Tasa de errores


## Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.
