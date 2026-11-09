CREATE SEQUENCE usuario_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE plan_estudios_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE asignatura_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE requisito_asignatura_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE grupo_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE sesion_horario_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE configuracion_horarios_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE formacion_academica_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE experiencia_docente_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE disponibilidad_docente_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE usuario (
    id                    BIGINT       NOT NULL,
    nombre                VARCHAR(255) NOT NULL,
    apellido              VARCHAR(255) NOT NULL,
    numero_documento      VARCHAR(255) NOT NULL,
    correo_institucional  VARCHAR(255) NOT NULL,
    contrasenia           VARCHAR(255) NOT NULL,
    estado                BOOLEAN      NOT NULL,
    CONSTRAINT pk_usuario PRIMARY KEY (id),
    CONSTRAINT uq_usuario_documento UNIQUE (numero_documento),
    CONSTRAINT uq_usuario_correo UNIQUE (correo_institucional)
);

CREATE TABLE administrador (
    id     BIGINT NOT NULL,
    cargo  VARCHAR(255),
    CONSTRAINT pk_administrador PRIMARY KEY (id),
    CONSTRAINT fk_administrador_usuario FOREIGN KEY (id) REFERENCES usuario (id)
);

CREATE TABLE docente (
    id                              BIGINT       NOT NULL,
    tipo_vinculacion                VARCHAR(255),
    escalafon                       VARCHAR(255),
    especialidad                    VARCHAR(255),
    horas_maximas_docencia_semanal  INTEGER      NOT NULL,
    CONSTRAINT pk_docente PRIMARY KEY (id),
    CONSTRAINT fk_docente_usuario FOREIGN KEY (id) REFERENCES usuario (id),
    CONSTRAINT ck_docente_vinculacion CHECK (tipo_vinculacion IN ('TIEMPO_COMPLETO', 'TRES_CUARTOS_TIEMPO', 'MEDIO_TIEMPO', 'CUARTO_TIEMPO')),
    CONSTRAINT ck_docente_horas CHECK (horas_maximas_docencia_semanal >= 0)
);

CREATE TABLE formacion_academica (
    id_formacion       BIGINT       NOT NULL,
    titulo_obtenido    VARCHAR(255),
    nivel_formacion    VARCHAR(255),
    institucion        VARCHAR(255),
    area_conocimiento  VARCHAR(255),
    anio_graduacion    INTEGER      NOT NULL,
    docente_id         BIGINT       NOT NULL,
    CONSTRAINT pk_formacion_academica PRIMARY KEY (id_formacion),
    CONSTRAINT fk_formacion_docente FOREIGN KEY (docente_id) REFERENCES docente (id),
    CONSTRAINT ck_formacion_nivel CHECK (nivel_formacion IN ('PREGRADO', 'ESPECIALIZACION', 'MAESTRIA', 'DOCTORADO'))
);

CREATE INDEX ix_formacion_docente ON formacion_academica (docente_id);

CREATE TABLE experiencia_docente (
    id_experiencia     BIGINT       NOT NULL,
    tipo_experiencia   VARCHAR(255),
    institucion        VARCHAR(255),
    cargo              VARCHAR(255),
    area_conocimiento  VARCHAR(255),
    descripcion        VARCHAR(255),
    fecha_inicio       DATE,
    fecha_fin          DATE,
    docente_id         BIGINT       NOT NULL,
    CONSTRAINT pk_experiencia_docente PRIMARY KEY (id_experiencia),
    CONSTRAINT fk_experiencia_docente FOREIGN KEY (docente_id) REFERENCES docente (id),
    CONSTRAINT ck_experiencia_tipo CHECK (tipo_experiencia IN ('LABORAL', 'DOCENTE', 'INVESTIGATIVA'))
);

CREATE INDEX ix_experiencia_docente ON experiencia_docente (docente_id);

CREATE TABLE disponibilidad_docente (
    id_disponibilidad  BIGINT       NOT NULL,
    dia_semana         VARCHAR(255) NOT NULL,
    hora_inicio        TIME(0)      NOT NULL,
    hora_fin           TIME(0)      NOT NULL,
    docente_id         BIGINT       NOT NULL,
    CONSTRAINT pk_disponibilidad_docente PRIMARY KEY (id_disponibilidad),
    CONSTRAINT fk_disponibilidad_docente FOREIGN KEY (docente_id) REFERENCES docente (id),
    CONSTRAINT ck_disponibilidad_dia CHECK (dia_semana IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY')),
    CONSTRAINT ck_disponibilidad_horas CHECK (hora_inicio < hora_fin)
);

CREATE INDEX ix_disponibilidad_docente ON disponibilidad_docente (docente_id);

CREATE TABLE plan_estudios (
    id_plan            BIGINT       NOT NULL,
    nombre_programa    VARCHAR(255),
    codigo_programa    VARCHAR(255) NOT NULL,
    version_plan       VARCHAR(255),
    numeros_semestres  INTEGER      NOT NULL,
    estado             BOOLEAN      NOT NULL,
    CONSTRAINT pk_plan_estudios PRIMARY KEY (id_plan),
    CONSTRAINT uq_plan_codigo UNIQUE (codigo_programa)
);

CREATE TABLE asignatura (
    id_asginatura       BIGINT       NOT NULL,
    codigo              VARCHAR(255) NOT NULL,
    nombre              VARCHAR(255),
    semestre            INTEGER      NOT NULL,
    creditos            INTEGER      NOT NULL,
    sesiones_semanales  INTEGER      NOT NULL,
    area_conocimiento   VARCHAR(255),
    estado              BOOLEAN      NOT NULL,
    plan_estudios_id    BIGINT       NOT NULL,
    CONSTRAINT pk_asignatura PRIMARY KEY (id_asginatura),
    CONSTRAINT uq_asignatura_codigo UNIQUE (codigo),
    CONSTRAINT fk_asignatura_plan FOREIGN KEY (plan_estudios_id) REFERENCES plan_estudios (id_plan)
);

CREATE INDEX ix_asignatura_plan ON asignatura (plan_estudios_id);

CREATE TABLE requisito_asignatura (
    id_requisito              BIGINT       NOT NULL,
    tipo_requisito            VARCHAR(255),
    descripcion               VARCHAR(255),
    area_conocimiento         VARCHAR(255),
    nivel_formacion_minimo    VARCHAR(255),
    anios_experiencia_minima  INTEGER      NOT NULL,
    asignatura_id             BIGINT       NOT NULL,
    CONSTRAINT pk_requisito_asignatura PRIMARY KEY (id_requisito),
    CONSTRAINT fk_requisito_asignatura FOREIGN KEY (asignatura_id) REFERENCES asignatura (id_asginatura),
    CONSTRAINT ck_requisito_tipo CHECK (tipo_requisito IN ('FORMACION_ACADEMICA', 'EXPERIENCIA_LABORAL', 'EXPERIENCIA_DOCENTE', 'EXPERIENCIA_INVESTIGATIVA')),
    CONSTRAINT ck_requisito_nivel CHECK (nivel_formacion_minimo IN ('PREGRADO', 'ESPECIALIZACION', 'MAESTRIA', 'DOCTORADO'))
);

CREATE INDEX ix_requisito_asignatura ON requisito_asignatura (asignatura_id);

CREATE TABLE grupo (
    id_grupo       BIGINT       NOT NULL,
    numero_grupo   VARCHAR(255) NOT NULL,
    estado         VARCHAR(255) NOT NULL,
    cupo_esperado  INTEGER      NOT NULL,
    asignatura_id  BIGINT       NOT NULL,
    docente_id     BIGINT,
    CONSTRAINT pk_grupo PRIMARY KEY (id_grupo),
    CONSTRAINT uq_grupo_numero UNIQUE (asignatura_id, numero_grupo),
    CONSTRAINT fk_grupo_asignatura FOREIGN KEY (asignatura_id) REFERENCES asignatura (id_asginatura),
    CONSTRAINT fk_grupo_docente FOREIGN KEY (docente_id) REFERENCES docente (id),
    CONSTRAINT ck_grupo_estado CHECK (estado IN ('ACTIVO', 'INACTIVO')),
    CONSTRAINT ck_grupo_cupo CHECK (cupo_esperado > 0)
);

CREATE INDEX ix_grupo_docente ON grupo (docente_id);

CREATE TABLE sesion_horario (
    id_sesion    BIGINT       NOT NULL,
    dia_semana   VARCHAR(255) NOT NULL,
    hora_inicio  TIME(0)      NOT NULL,
    hora_fin     TIME(0)      NOT NULL,
    estado       VARCHAR(255) NOT NULL,
    grupo_id     BIGINT       NOT NULL,
    CONSTRAINT pk_sesion_horario PRIMARY KEY (id_sesion),
    CONSTRAINT fk_sesion_grupo FOREIGN KEY (grupo_id) REFERENCES grupo (id_grupo),
    CONSTRAINT ck_sesion_dia CHECK (dia_semana IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY')),
    CONSTRAINT ck_sesion_estado CHECK (estado IN ('PROGRAMADA', 'CANCELADA')),
    CONSTRAINT ck_sesion_horas CHECK (hora_inicio < hora_fin)
);

CREATE INDEX ix_sesion_grupo ON sesion_horario (grupo_id);
CREATE INDEX ix_sesion_dia ON sesion_horario (dia_semana);
CREATE INDEX ix_sesion_estado ON sesion_horario (estado);

CREATE TABLE configuracion_horarios (
    id_configuracion            BIGINT      NOT NULL,
    clave_institucional         VARCHAR(20) NOT NULL,
    hora_inicio_semana          TIME(0)     NOT NULL,
    hora_fin_semana             TIME(0)     NOT NULL,
    hora_inicio_sabado          TIME(0)     NOT NULL,
    hora_fin_sabado             TIME(0)     NOT NULL,
    hora_inicio_descanso        TIME(0),
    hora_fin_descanso           TIME(0),
    duracion_sesion_minutos     INTEGER     NOT NULL,
    max_sesiones_semanales      INTEGER     NOT NULL,
    evitar_dias_consecutivos    BOOLEAN     NOT NULL,
    cupo_minimo_grupo           INTEGER     NOT NULL,
    cupo_maximo_grupo           INTEGER     NOT NULL,
    tolerancia_cupo_porcentaje  INTEGER     NOT NULL,
    CONSTRAINT pk_configuracion_horarios PRIMARY KEY (id_configuracion),
    CONSTRAINT uq_configuracion_clave UNIQUE (clave_institucional),
    CONSTRAINT ck_configuracion_semana CHECK (hora_inicio_semana < hora_fin_semana),
    CONSTRAINT ck_configuracion_sabado CHECK (hora_inicio_sabado < hora_fin_sabado),
    CONSTRAINT ck_configuracion_descanso CHECK (hora_inicio_descanso < hora_fin_descanso),
    CONSTRAINT ck_configuracion_duracion CHECK (duracion_sesion_minutos > 0),
    CONSTRAINT ck_configuracion_sesiones CHECK (max_sesiones_semanales > 0),
    CONSTRAINT ck_configuracion_cupo CHECK (cupo_minimo_grupo > 0 AND cupo_minimo_grupo <= cupo_maximo_grupo),
    CONSTRAINT ck_configuracion_tolerancia CHECK (tolerancia_cupo_porcentaje BETWEEN 0 AND 100)
);
