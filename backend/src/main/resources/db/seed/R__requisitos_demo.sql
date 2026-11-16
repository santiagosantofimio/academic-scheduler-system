CREATE TEMPORARY TABLE requisitos_demo_asignatura (
    codigo VARCHAR(255) NOT NULL PRIMARY KEY,
    area VARCHAR(255) NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

INSERT INTO requisitos_demo_asignatura (codigo, area) VALUES
    ('IS101', 'Matemáticas'),
    ('IS102', 'Fundamentos de ingeniería'),
    ('IS103', 'Ciencias de la vida'),
    ('IS104', 'Razonamiento lógico'),
    ('IS105', 'Ingeniería de software'),
    ('IS106', 'Sistemas de información'),
    ('IS201', 'Matemáticas'),
    ('IS202', 'Matemáticas'),
    ('IS203', 'Física'),
    ('IS204', 'Ciencias básicas aplicadas'),
    ('IS205', 'Razonamiento lógico'),
    ('IS206', 'Ingeniería de software'),
    ('IS207', 'Formación interdisciplinaria'),
    ('IS301', 'Estadística'),
    ('IS302', 'Física'),
    ('IS303', 'Ciencias básicas aplicadas'),
    ('IS304', 'Idiomas'),
    ('IS305', 'Ingeniería de software'),
    ('IS306', 'Estructuras de datos'),
    ('IS307', 'Formación interdisciplinaria'),
    ('IS401', 'Gestión ambiental y sostenibilidad'),
    ('IS402', 'Idiomas'),
    ('IS403', 'Sistemas operativos'),
    ('IS404', 'Bases de datos'),
    ('IS405', 'Redes e infraestructura'),
    ('IS406', 'Sistemas de información'),
    ('IS407', 'Gestión de proyectos de ingeniería'),
    ('IS501', 'Ecología'),
    ('IS502', 'Idiomas'),
    ('IS503', 'Ingeniería de software'),
    ('IS504', 'Bases de datos'),
    ('IS505', 'Redes e infraestructura'),
    ('IS506', 'Economía y finanzas'),
    ('IS507', 'Formación interdisciplinaria'),
    ('IS601', 'Idiomas'),
    ('IS602', 'Ingeniería de software'),
    ('IS603', 'Algoritmos y complejidad'),
    ('IS604', 'Redes e infraestructura'),
    ('IS605', 'Sistemas de información'),
    ('IS606', 'Gestión de proyectos de ingeniería'),
    ('IS607', 'Gestión de proyectos de ingeniería'),
    ('IS701', 'Ingeniería de software'),
    ('IS702', 'Lenguajes de programación'),
    ('IS703', 'Gestión de proyectos de ingeniería'),
    ('IS704', 'Gestión de proyectos de ingeniería'),
    ('IS705', 'Seguridad de la información'),
    ('IS706', 'Responsabilidad social'),
    ('IS707', 'Ingeniería de sistemas'),
    ('IS801', 'Ciencias sociales'),
    ('IS802', 'Desarrollo profesional'),
    ('IS803', 'Gestión de tecnología e innovación'),
    ('IS804', 'Gestión de proyectos de ingeniería'),
    ('IS805', 'Inteligencia artificial'),
    ('IS806', 'Ingeniería de sistemas'),
    ('IS807', 'Innovación y emprendimiento'),
    ('IS901', 'Investigación'),
    ('IS902', 'Ingeniería de sistemas'),
    ('IS903', 'Formación interdisciplinaria');

INSERT INTO formacion_academica (titulo_obtenido, nivel_formacion, institucion, area_conocimiento, anio_graduacion, docente_id)
SELECT DISTINCT CONCAT('Pregrado afín a ', d.area), 'PREGRADO', 'Institución de demostración', d.area, 2018, u.id
FROM requisitos_demo_asignatura d
JOIN asignatura a ON a.codigo = d.codigo
JOIN plan_estudios p ON p.id_plan = a.plan_estudios_id AND p.id_plan = 900001 AND p.version_plan = '2025'
JOIN grupo g ON g.asignatura_id = a.id_asginatura AND g.estado = 'ACTIVO'
JOIN usuario u ON u.id = g.docente_id AND u.estado = TRUE
WHERE (
    CAST(u.numero_documento AS BINARY) LIKE 'DEMO-SIS-_%'
    OR (u.id, u.numero_documento) IN (
        (900001, '1010101010'),
        (900002, '1020202020'),
        (900003, '1030303030'),
        (900004, '1040404040'),
        (800005, '1050505050'),
        (800006, '1060606060'),
        (800007, '1070707070'),
        (800008, '1080808080'),
        (800009, '1090909090'),
        (800010, '1101010101'),
        (800011, '1111111111'),
        (800012, '1121212121')
    )
)
AND NOT EXISTS (
    SELECT 1
    FROM requisito_asignatura r
    WHERE r.asignatura_id = a.id_asginatura
)
AND NOT EXISTS (
    SELECT 1
    FROM formacion_academica f
    WHERE f.docente_id = u.id
        AND CAST(LOWER(TRIM(f.area_conocimiento)) AS BINARY) = CAST(LOWER(d.area) AS BINARY)
        AND f.nivel_formacion IN ('PREGRADO', 'ESPECIALIZACION', 'MAESTRIA', 'DOCTORADO')
);

INSERT INTO requisito_asignatura (tipo_requisito, descripcion, area_conocimiento, nivel_formacion_minimo, anios_experiencia_minima, asignatura_id)
SELECT 'FORMACION_ACADEMICA', CONCAT('Pregrado en un área afín a ', d.area, ' para orientar ', a.nombre, '.'), d.area, 'PREGRADO', 0, a.id_asginatura
FROM requisitos_demo_asignatura d
JOIN asignatura a ON a.codigo = d.codigo
JOIN plan_estudios p ON p.id_plan = a.plan_estudios_id AND p.id_plan = 900001 AND p.version_plan = '2025'
WHERE NOT EXISTS (
    SELECT 1
    FROM requisito_asignatura r
    WHERE r.asignatura_id = a.id_asginatura
)
AND NOT EXISTS (
    SELECT 1
    FROM grupo g
    WHERE g.asignatura_id = a.id_asginatura
        AND g.estado = 'ACTIVO'
        AND g.docente_id IS NOT NULL
        AND NOT EXISTS (
            SELECT 1
            FROM formacion_academica f
            JOIN usuario u ON u.id = f.docente_id AND u.estado = TRUE
            WHERE f.docente_id = g.docente_id
                AND CAST(LOWER(TRIM(f.area_conocimiento)) AS BINARY) = CAST(LOWER(d.area) AS BINARY)
                AND f.nivel_formacion IN ('PREGRADO', 'ESPECIALIZACION', 'MAESTRIA', 'DOCTORADO')
        )
);

DROP TEMPORARY TABLE requisitos_demo_asignatura;
