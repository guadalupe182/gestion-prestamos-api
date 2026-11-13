# API de Gestión de Préstamos 💰

Solución desarrollada para la evaluación técnica de **Desarrollador Senior Java**. Es una API RESTful que permite gestionar clientes y préstamos almacenados en memoria, aplicando reglas de negocio específicas para el cálculo de intereses.

---

## 🚀 Tecnologías y Herramientas

- **Java 17** — Uso de `Records`, `var` y *Pattern Matching*.
- **Spring Boot 3.3.5**
- **Maven**
- **Lombok**
- **JUnit 5 & Mockito** — Pruebas unitarias.
- **Docker**

---

## 📋 Características Principales

- **Gestión en Memoria:** Uso de `ConcurrentHashMap` para garantizar operaciones *thread-safe*, simulando una base de datos.
- **Cálculo de Intereses Dinámico:** Aplicación de reglas de negocio para determinar tasas de interés basadas en el tipo de cliente:
  - 👑 **VIP:** 5%
  - 👤 **REGULAR:** 10%
- **Documentación DDL:** Incluye el script `schema-oracle.sql` como referencia para un posible almacenamiento persistente utilizando Oracle Database.
- **OpenAPI:** Diseño y documentación de los endpoints disponible en `openapi.yaml`.
- **Arquitectura Orientada a Capas:** Separación clara de responsabilidades entre controladores, servicios y lógica de negocio.
- **Pruebas Unitarias:** Cobertura de la lógica principal del negocio mediante JUnit 5 y Mockito.

---

## 🔐 Integración con GDEV Auth

La API ahora soporta registro de usuarios validando su `licenseKey` contra el CRM de GDEV.
Antes de acceder a los endpoints de clientes y préstamos, el cliente debe obtener un JWT mediante:

```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "username": "contador",
  "password": "passwordSegura123",
  "licenseKey": "licencia-gdev"
}
```

El token devuelto debe enviarse en el header:

```http
Authorization: Bearer <token>
```

Variables de entorno relevantes:

```text
GDEV_LICENSE_VALIDATION_URL=https://api.gdevsoftware.com/api/v1/licenses/validate/{key}
JWT_SECRET=<secreto-de-al-menos-32-bytes>
JWT_EXPIRATION_MS=3600000
DB_URL=jdbc:h2:mem:gdevloans;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
```

---

## 📈 Cálculo de intereses

La API soporta dos flujos de préstamos:

- **Préstamos en memoria** (`/api/v1/prestamos`): la tasa de interés depende del tipo de cliente:
  - 👑 **VIP:** 5%
  - 👤 **REGULAR:** 10%

  El total a pagar se calcula como `monto × (1 + tasa)` y se consulta mediante:

  ```http
  PATCH /api/v1/prestamos/{id}/total-pagar
  ```

- **Préstamos JPA** (`/api/loans`): interés simple con tasa anual por defecto del 10% (configurable por préstamo), calculado como:

  ```text
  interés = monto × tasa × (días transcurridos / 365)
  ```

  El cálculo se limita a la fecha de fin (`endDate`) cuando existe y devuelve `0` para préstamos pagados. Se consulta mediante:

  ```http
  GET /api/loans/{id}/calculate-interest
  ```

  Respuesta de ejemplo:

  ```json
  {
    "loanId": "123e4567-e89b-12d3-a456-426614174000",
    "interest": 100.00
  }
  ```

---

## ⚙️ Cómo ejecutar el proyecto

### Opción 1: Ejecución local con Maven

Clona el repositorio:

```bash
git clone <URL_DEL_REPOSITORIO>
cd <NOMBRE_DEL_PROYECTO>
```

Ejecuta el siguiente comando desde la raíz del proyecto:

```bash
./mvnw spring-boot:run
```

> En Windows puedes utilizar:

```bash
mvnw.cmd spring-boot:run
```

Una vez iniciado, la API estará disponible en:

```text
http://localhost:8080/api/v1
```

---

### Opción 2: Ejecución con Docker 🐳

Construye la imagen:

```bash
docker build -t gestion-prestamos-api .
```

Posteriormente, levanta el contenedor:

```bash
docker run -p 8080:8080 gestion-prestamos-api
```

La API estará disponible en:

```text
http://localhost:8080/api/v1
```

---

## 🧪 Pruebas Unitarias

El proyecto incluye pruebas unitarias diseñadas para aislar y validar la lógica de negocio, especialmente el cálculo de intereses y los montos totales a pagar.

Para ejecutar las pruebas:

```bash
./mvnw test
```

En Windows:

```bash
mvnw.cmd test
```

---

## 📚 Documentación de la API

El contrato de la API se encuentra documentado en el archivo:

```text
openapi.yaml
```

Este archivo describe los recursos disponibles, operaciones HTTP, estructuras de solicitud y respuesta, así como los posibles códigos de estado.

Puedes utilizar herramientas compatibles con **OpenAPI**, como Swagger Editor o Postman, para visualizar e importar la definición de la API.

---

## 🗄️ Persistencia y DDL

Actualmente, la aplicación utiliza almacenamiento **en memoria** mediante estructuras `ConcurrentHashMap`, lo que permite mantener el proyecto ligero y enfocado en la lógica de negocio para fines de la evaluación técnica.

Como referencia para una futura implementación con persistencia, el proyecto incluye:

```text
schema-oracle.sql
```

Este archivo contiene una propuesta de estructura DDL compatible con **Oracle Database**.

---

## 🏗️ Consideraciones Técnicas

El proyecto fue diseñado considerando algunos principios importantes para aplicaciones empresariales:

- **Separación de responsabilidades** entre las diferentes capas de la aplicación.
- **Thread Safety** para las operaciones realizadas sobre los datos almacenados en memoria.
- **Código moderno de Java 17**, aprovechando características del lenguaje para mejorar la legibilidad y expresividad.
- **Lógica de negocio desacoplada** y validada mediante pruebas unitarias.
- **Preparación para evolución futura**, permitiendo reemplazar fácilmente el almacenamiento en memoria por una solución persistente.

---

## 📁 Estructura de Recursos

```text
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
│       └── java/
├── Dockerfile
├── openapi.yaml
├── schema-oracle.sql
├── pom.xml
└── README.md
```

---

## 🛠️ Requisitos Previos

Para ejecutar el proyecto necesitas tener instalado:

- **Java 17 o superior**
- **Docker** *(opcional, para ejecución mediante contenedor)*
- No es necesario instalar Maven globalmente si utilizas el **Maven Wrapper (`mvnw`)** incluido en el proyecto.

Puedes verificar tu versión de Java con:

```bash
java -version
```

---

## 👨‍💻 Autor

Desarrollado como parte de una **evaluación técnica para la posición de Desarrollador Senior Java**.

---

⭐ **Proyecto desarrollado priorizando código limpio, buenas prácticas y una arquitectura preparada para evolucionar.**
