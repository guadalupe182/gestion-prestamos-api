-- DDL para la Evaluación de Gestión de Préstamos (Oracle Dialect)

CREATE TABLE clientes (
                          id VARCHAR2(36) PRIMARY KEY,
                          nombre VARCHAR2(100) NOT NULL,
                          email VARCHAR2(100) UNIQUE NOT NULL,
                          edad NUMBER(3) NOT NULL,
                          tipo_cliente VARCHAR2(20) NOT NULL CHECK (tipo_cliente IN ('REGULAR', 'VIP'))
);

CREATE TABLE prestamos (
                           id VARCHAR2(36) PRIMARY KEY,
                           monto NUMBER(10, 2) NOT NULL,
                           cliente_id VARCHAR2(36) NOT NULL,
                           fecha DATE NOT NULL,
                           estado VARCHAR2(20) NOT NULL CHECK (estado IN ('PENDIENTE', 'PAGADO')),
                           CONSTRAINT fk_prestamo_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);