CREATE TABLE version_horario (
    id_version     BIGINT       NOT NULL AUTO_INCREMENT,
    nombre         VARCHAR(120) NOT NULL,
    estado         VARCHAR(20)  NOT NULL,
    creada_en      DATETIME(0)  NOT NULL,
    creada_por     VARCHAR(255) NOT NULL,
    publicada_en   DATETIME(0),
    publicada_por  VARCHAR(255),
    origen_id      BIGINT,
    CONSTRAINT pk_version_horario PRIMARY KEY (id_version),
    CONSTRAINT fk_version_origen FOREIGN KEY (origen_id) REFERENCES version_horario (id_version) ON DELETE SET NULL,
    CONSTRAINT ck_version_estado CHECK (estado IN ('BORRADOR', 'PUBLICADA', 'HISTORICA'))
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE INDEX ix_version_estado ON version_horario (estado);

CREATE TABLE version_sesion (
    id_version_sesion  BIGINT       NOT NULL AUTO_INCREMENT,
    version_id         BIGINT       NOT NULL,
    grupo_id           BIGINT,
    dia_semana         VARCHAR(255) NOT NULL,
    hora_inicio        TIME(0)      NOT NULL,
    hora_fin           TIME(0)      NOT NULL,
    CONSTRAINT pk_version_sesion PRIMARY KEY (id_version_sesion),
    CONSTRAINT fk_version_sesion_version FOREIGN KEY (version_id) REFERENCES version_horario (id_version) ON DELETE CASCADE,
    CONSTRAINT fk_version_sesion_grupo FOREIGN KEY (grupo_id) REFERENCES grupo (id_grupo) ON DELETE SET NULL,
    CONSTRAINT ck_version_sesion_dia CHECK (dia_semana IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY')),
    CONSTRAINT ck_version_sesion_horas CHECK (hora_inicio < hora_fin)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE INDEX ix_version_sesion_version ON version_sesion (version_id);

CREATE TABLE novedad_docente (
    id_novedad      BIGINT       NOT NULL AUTO_INCREMENT,
    docente_id      BIGINT       NOT NULL,
    tipo            VARCHAR(30)  NOT NULL,
    dia_semana      VARCHAR(255) NOT NULL,
    hora_inicio     TIME(0),
    hora_fin        TIME(0),
    descripcion     VARCHAR(500),
    estado          VARCHAR(20)  NOT NULL,
    registrada_en   DATETIME(0)  NOT NULL,
    registrada_por  VARCHAR(255) NOT NULL,
    CONSTRAINT pk_novedad_docente PRIMARY KEY (id_novedad),
    CONSTRAINT fk_novedad_docente FOREIGN KEY (docente_id) REFERENCES docente (id) ON DELETE CASCADE,
    CONSTRAINT ck_novedad_tipo CHECK (tipo IN ('INCAPACIDAD', 'CAMBIO_DISPONIBILIDAD', 'OTRA')),
    CONSTRAINT ck_novedad_estado CHECK (estado IN ('ABIERTA', 'CERRADA')),
    CONSTRAINT ck_novedad_dia CHECK (dia_semana IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY')),
    CONSTRAINT ck_novedad_horas CHECK ((hora_inicio IS NULL AND hora_fin IS NULL) OR hora_inicio < hora_fin)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE INDEX ix_novedad_docente ON novedad_docente (docente_id, estado);

CREATE TABLE auditoria_cambio (
    id_auditoria  BIGINT       NOT NULL AUTO_INCREMENT,
    fecha         DATETIME(0)  NOT NULL,
    usuario       VARCHAR(255) NOT NULL,
    accion        VARCHAR(40)  NOT NULL,
    detalle       VARCHAR(500) NOT NULL,
    CONSTRAINT pk_auditoria_cambio PRIMARY KEY (id_auditoria)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE INDEX ix_auditoria_fecha ON auditoria_cambio (fecha);
