INSERT INTO configuracion_horarios (id_configuracion, clave_institucional, hora_inicio_semana, hora_fin_semana, hora_inicio_sabado, hora_fin_sabado, hora_inicio_descanso, hora_fin_descanso, duracion_sesion_minutos, max_sesiones_semanales, evitar_dias_consecutivos, cupo_minimo_grupo, cupo_maximo_grupo, tolerancia_cupo_porcentaje)
VALUES (900001, 'INSTITUCIONAL', '07:00', '22:00', '07:00', '13:00', '12:00', '13:00', 120, 4, FALSE, 10, 40, 10)
ON DUPLICATE KEY UPDATE configuracion_horarios.id_configuracion = configuracion_horarios.id_configuracion;

INSERT INTO plan_estudios (id_plan, nombre_programa, codigo_programa, version_plan, numeros_semestres, estado)
VALUES (900001, 'Ingeniería de Sistemas', 'IS', '2025', 9, TRUE)
ON DUPLICATE KEY UPDATE plan_estudios.id_plan = plan_estudios.id_plan;

INSERT INTO asignatura (id_asginatura, codigo, nombre, semestre, creditos, sesiones_semanales, area_conocimiento, estado, plan_estudios_id)
SELECT d.id, d.codigo, d.nombre, d.semestre, d.creditos, d.sesiones, d.area, TRUE, p.id_plan
FROM (VALUES
    ROW(900001, 'IS101', 'Matemáticas 1', 1, 4, 2, 'Fundamentación'),
    ROW(900002, 'IS102', 'Fundamentos de ingeniería', 1, 2, 1, 'Fundamentación'),
    ROW(900003, 'IS103', 'Ciencias para la vida', 1, 2, 1, 'Fundamentación'),
    ROW(900004, 'IS104', 'Estructuración del pensamiento 1', 1, 2, 1, 'Fundamentación'),
    ROW(900005, 'IS105', 'Desarrollo de sistemas de información 1', 1, 4, 2, 'Disciplinar'),
    ROW(900006, 'IS106', 'Principios de sistemas de información', 1, 2, 1, 'Disciplinar'),
    ROW(900007, 'IS201', 'Matemáticas 2', 2, 4, 2, 'Fundamentación'),
    ROW(900008, 'IS202', 'Álgebra lineal', 2, 2, 1, 'Fundamentación'),
    ROW(900009, 'IS203', 'Física 1', 2, 2, 1, 'Fundamentación'),
    ROW(900010, 'IS204', 'Desafíos desde las ciencias para la ingeniería 1', 2, 2, 1, 'Fundamentación'),
    ROW(900011, 'IS205', 'Estructuración del pensamiento 2', 2, 2, 1, 'Fundamentación'),
    ROW(900012, 'IS206', 'Desarrollo de sistemas de información 2', 2, 4, 2, 'Disciplinar'),
    ROW(900013, 'IS207', 'Electiva libre 1', 2, 2, 1, 'Libre elección'),
    ROW(900014, 'IS301', 'Bioestadística y probabilidad', 3, 2, 1, 'Fundamentación'),
    ROW(900015, 'IS302', 'Física 2', 3, 2, 1, 'Fundamentación'),
    ROW(900016, 'IS303', 'Desafíos desde las ciencias para la ingeniería 2', 3, 2, 1, 'Fundamentación'),
    ROW(900017, 'IS304', 'Idiomas 1', 3, 2, 1, 'Fundamentación'),
    ROW(900018, 'IS305', 'Desarrollo de sistemas de información 3', 3, 4, 2, 'Disciplinar'),
    ROW(900019, 'IS306', 'Estructuras de datos', 3, 2, 1, 'Disciplinar'),
    ROW(900020, 'IS307', 'Electiva libre 2', 3, 2, 1, 'Libre elección'),
    ROW(900021, 'IS401', 'Territorio y sostenibilidad', 4, 2, 1, 'Fundamentación'),
    ROW(900022, 'IS402', 'Idiomas 2', 4, 2, 1, 'Fundamentación'),
    ROW(900023, 'IS403', 'Estructuras de sistemas', 4, 4, 2, 'Disciplinar'),
    ROW(900024, 'IS404', 'Bases de datos 1', 4, 4, 2, 'Disciplinar'),
    ROW(900025, 'IS405', 'Infraestructura TI 1', 4, 2, 1, 'Disciplinar'),
    ROW(900026, 'IS406', 'Modelización informacional', 4, 2, 1, 'Disciplinar'),
    ROW(900027, 'IS407', 'Proyecto núcleo 1', 4, 2, 1, 'Profundización'),
    ROW(900028, 'IS501', 'Fundamentos de ecología', 5, 2, 1, 'Fundamentación'),
    ROW(900029, 'IS502', 'Idiomas 3', 5, 2, 1, 'Fundamentación'),
    ROW(900030, 'IS503', 'Ingeniería de software 1', 5, 2, 1, 'Disciplinar'),
    ROW(900031, 'IS504', 'Bases de datos 2', 5, 2, 1, 'Disciplinar'),
    ROW(900032, 'IS505', 'Infraestructura TI 2', 5, 4, 2, 'Disciplinar'),
    ROW(900033, 'IS506', 'Economía y finanzas', 5, 2, 1, 'Profundización'),
    ROW(900034, 'IS507', 'Electiva libre 3', 5, 2, 1, 'Libre elección'),
    ROW(900035, 'IS601', 'Idiomas 4', 6, 2, 1, 'Fundamentación'),
    ROW(900036, 'IS602', 'Ingeniería de software 2', 6, 2, 1, 'Disciplinar'),
    ROW(900037, 'IS603', 'Complejidad algorítmica', 6, 4, 2, 'Disciplinar'),
    ROW(900038, 'IS604', 'Infraestructura TI 3', 6, 2, 1, 'Disciplinar'),
    ROW(900039, 'IS605', 'Modelos y simulación de sistemas de información', 6, 2, 1, 'Disciplinar'),
    ROW(900040, 'IS606', 'Proyecto núcleo 2', 6, 2, 1, 'Profundización'),
    ROW(900041, 'IS607', 'Formulación de proyectos', 6, 2, 1, 'Profundización'),
    ROW(900042, 'IS701', 'Ingeniería de software 3', 7, 4, 2, 'Disciplinar'),
    ROW(900043, 'IS702', 'Lenguajes de dominio específico', 7, 2, 1, 'Disciplinar'),
    ROW(900044, 'IS703', 'Conceptualización de proyectos de ingeniería', 7, 2, 1, 'Profundización'),
    ROW(900045, 'IS704', 'Gestión de proyectos', 7, 2, 1, 'Profundización'),
    ROW(900046, 'IS705', 'Seguridad de la información', 7, 2, 1, 'Profundización'),
    ROW(900047, 'IS706', 'Labor social', 7, 2, 1, 'Libre elección'),
    ROW(900048, 'IS707', 'Electiva de profundización en ingeniería de sistemas 1', 7, 2, 1, 'Libre elección'),
    ROW(900049, 'IS801', 'Política, sociedad y ciudadanía', 8, 2, 1, 'Fundamentación'),
    ROW(900050, 'IS802', 'Preparación para la vida laboral', 8, 2, 1, 'Fundamentación'),
    ROW(900051, 'IS803', 'Gestión de tecnología e innovación', 8, 4, 2, 'Disciplinar'),
    ROW(900052, 'IS804', 'Diseño de proyectos de ingeniería', 8, 2, 1, 'Profundización'),
    ROW(900053, 'IS805', 'Sistemas inteligentes', 8, 2, 1, 'Profundización'),
    ROW(900054, 'IS806', 'Electiva de profundización en ingeniería de sistemas 2', 8, 2, 1, 'Libre elección'),
    ROW(900055, 'IS807', 'Electiva en pensamiento innovador o pensamiento emprendedor', 8, 2, 1, 'Libre elección'),
    ROW(900056, 'IS901', 'Trabajo de grado', 9, 12, 1, 'Profundización'),
    ROW(900057, 'IS902', 'Electiva de profundización en ingeniería de sistemas 3', 9, 2, 1, 'Libre elección'),
    ROW(900058, 'IS903', 'Electiva libre 4', 9, 2, 1, 'Libre elección')
) AS d (id, codigo, nombre, semestre, creditos, sesiones, area)
JOIN plan_estudios p ON p.codigo_programa = 'IS'
ON DUPLICATE KEY UPDATE asignatura.id_asginatura = asignatura.id_asginatura;

INSERT INTO usuario (id, nombre, apellido, numero_documento, correo_institucional, contrasenia, estado) VALUES
    (900001, 'Laura',  'Méndez',  '1010101010', 'lmendez@unbosque.edu.co',  '{bcrypt}$2y$10$LQGBAo/uWk1RIW2N34/yN.vTCl1oFjZpTYCeMM91wO23te4DQsfHq', TRUE),
    (900002, 'Andrés', 'Rojas',   '1020202020', 'arojas@unbosque.edu.co',   '{bcrypt}$2y$10$LQGBAo/uWk1RIW2N34/yN.vTCl1oFjZpTYCeMM91wO23te4DQsfHq', TRUE),
    (900003, 'Camila', 'Torres',  '1030303030', 'ctorres@unbosque.edu.co',  '{bcrypt}$2y$10$LQGBAo/uWk1RIW2N34/yN.vTCl1oFjZpTYCeMM91wO23te4DQsfHq', TRUE),
    (900004, 'Felipe', 'Castaño', '1040404040', 'fcastano@unbosque.edu.co', '{bcrypt}$2y$10$LQGBAo/uWk1RIW2N34/yN.vTCl1oFjZpTYCeMM91wO23te4DQsfHq', TRUE)
ON DUPLICATE KEY UPDATE usuario.id = usuario.id;

INSERT INTO docente (id, tipo_vinculacion, escalafon, especialidad, horas_maximas_docencia_semanal)
SELECT u.id, d.vinculacion, d.escalafon, d.especialidad, d.horas
FROM (VALUES
    ROW(900001, 'TIEMPO_COMPLETO',     'Asociado',  'Ingeniería de software', 20),
    ROW(900002, 'TIEMPO_COMPLETO',     'Titular',   'Matemáticas',            20),
    ROW(900003, 'MEDIO_TIEMPO',        'Asistente', 'Redes e infraestructura', 12),
    ROW(900004, 'TRES_CUARTOS_TIEMPO', 'Asistente', 'Sistemas operativos',    16)
) AS d (id, vinculacion, escalafon, especialidad, horas)
JOIN usuario u ON u.id = d.id
ON DUPLICATE KEY UPDATE docente.id = docente.id;

INSERT INTO disponibilidad_docente (id_disponibilidad, dia_semana, hora_inicio, hora_fin, docente_id)
SELECT d.id, d.dia, CAST(d.inicio AS TIME), CAST(d.fin AS TIME), u.id
FROM (VALUES
    ROW(900001, 'MONDAY',    '18:00', '22:00', '1030303030'),
    ROW(900002, 'WEDNESDAY', '18:00', '22:00', '1030303030'),
    ROW(900003, 'TUESDAY',   '16:00', '22:00', '1040404040'),
    ROW(900004, 'THURSDAY',  '16:00', '22:00', '1040404040'),
    ROW(900005, 'SATURDAY',  '07:00', '12:00', '1040404040')
) AS d (id, dia, inicio, fin, documento)
JOIN usuario u ON u.numero_documento = d.documento
JOIN docente o ON o.id = u.id
ON DUPLICATE KEY UPDATE disponibilidad_docente.id_disponibilidad = disponibilidad_docente.id_disponibilidad;

INSERT INTO grupo (id_grupo, numero_grupo, estado, cupo_esperado, asignatura_id, docente_id)
SELECT d.id, '01', 'ACTIVO', d.cupo, a.id_asginatura, o.id
FROM (VALUES
    ROW(900001, 30, 'IS105', '1010101010'),
    ROW(900002, 28, 'IS503', '1010101010'),
    ROW(900003, 40, 'IS101', '1020202020'),
    ROW(900004, 35, 'IS202', '1020202020'),
    ROW(900005, 22, 'IS405', '1030303030'),
    ROW(900006, 30, 'IS403', '1040404040')
) AS d (id, cupo, codigo, documento)
JOIN asignatura a ON a.codigo = d.codigo
JOIN usuario u ON u.numero_documento = d.documento
JOIN docente o ON o.id = u.id
ON DUPLICATE KEY UPDATE grupo.id_grupo = grupo.id_grupo;

INSERT INTO grupo (id_grupo, numero_grupo, estado, cupo_esperado, asignatura_id, docente_id)
SELECT 900007, '01', 'ACTIVO', 25, a.id_asginatura, NULL
FROM asignatura a WHERE a.codigo = 'IS407'
ON DUPLICATE KEY UPDATE grupo.id_grupo = grupo.id_grupo;

INSERT INTO usuario (id, nombre, apellido, numero_documento, correo_institucional, contrasenia, estado) VALUES
    (800005, 'Mariana', 'Gómez', '1050505050', 'mgomez@unbosque.edu.co', '{bcrypt}$2y$10$LQGBAo/uWk1RIW2N34/yN.vTCl1oFjZpTYCeMM91wO23te4DQsfHq', TRUE),
    (800006, 'Julián', 'Herrera', '1060606060', 'jherrera@unbosque.edu.co', '{bcrypt}$2y$10$LQGBAo/uWk1RIW2N34/yN.vTCl1oFjZpTYCeMM91wO23te4DQsfHq', TRUE),
    (800007, 'Natalia', 'Pérez', '1070707070', 'nperez@unbosque.edu.co', '{bcrypt}$2y$10$LQGBAo/uWk1RIW2N34/yN.vTCl1oFjZpTYCeMM91wO23te4DQsfHq', TRUE),
    (800008, 'Sergio', 'Ramírez', '1080808080', 'sramirez@unbosque.edu.co', '{bcrypt}$2y$10$LQGBAo/uWk1RIW2N34/yN.vTCl1oFjZpTYCeMM91wO23te4DQsfHq', TRUE),
    (800009, 'Paola', 'Vargas', '1090909090', 'pvargas@unbosque.edu.co', '{bcrypt}$2y$10$LQGBAo/uWk1RIW2N34/yN.vTCl1oFjZpTYCeMM91wO23te4DQsfHq', TRUE),
    (800010, 'Diego', 'Moreno', '1101010101', 'dmoreno@unbosque.edu.co', '{bcrypt}$2y$10$LQGBAo/uWk1RIW2N34/yN.vTCl1oFjZpTYCeMM91wO23te4DQsfHq', TRUE),
    (800011, 'Carolina', 'Suárez', '1111111111', 'csuarez@unbosque.edu.co', '{bcrypt}$2y$10$LQGBAo/uWk1RIW2N34/yN.vTCl1oFjZpTYCeMM91wO23te4DQsfHq', TRUE),
    (800012, 'Ricardo', 'León', '1121212121', 'rleon@unbosque.edu.co', '{bcrypt}$2y$10$LQGBAo/uWk1RIW2N34/yN.vTCl1oFjZpTYCeMM91wO23te4DQsfHq', TRUE)
ON DUPLICATE KEY UPDATE usuario.id = usuario.id;

INSERT INTO docente (id, tipo_vinculacion, escalafon, especialidad, horas_maximas_docencia_semanal)
SELECT u.id, d.vinculacion, d.escalafon, d.especialidad, d.horas
FROM (VALUES
    ROW(800005, 'TIEMPO_COMPLETO', 'Asociado', 'Ciencias básicas', 20),
    ROW(800006, 'TIEMPO_COMPLETO', 'Asistente', 'Física', 20),
    ROW(800007, 'TIEMPO_COMPLETO', 'Asociado', 'Humanidades e idiomas', 20),
    ROW(800008, 'TIEMPO_COMPLETO', 'Titular', 'Ingeniería de software', 20),
    ROW(800009, 'TIEMPO_COMPLETO', 'Asociado', 'Bases de datos', 20),
    ROW(800010, 'TIEMPO_COMPLETO', 'Asistente', 'Infraestructura y seguridad', 20),
    ROW(800011, 'TIEMPO_COMPLETO', 'Asociado', 'Gestión de proyectos', 20),
    ROW(800012, 'MEDIO_TIEMPO', 'Asistente', 'Electivas y emprendimiento', 12)
) AS d (id, vinculacion, escalafon, especialidad, horas)
JOIN usuario u ON u.id = d.id
ON DUPLICATE KEY UPDATE docente.id = docente.id;

INSERT INTO grupo (id_grupo, numero_grupo, estado, cupo_esperado, asignatura_id, docente_id)
SELECT d.id, '01', 'ACTIVO', 30, a.id_asginatura, o.id
FROM (VALUES
    ROW(800008, 'IS106', '1080808080'),
    ROW(800009, 'IS206', '1080808080'),
    ROW(800010, 'IS305', '1080808080'),
    ROW(800011, 'IS306', '1080808080'),
    ROW(800012, 'IS404', '1080808080'),
    ROW(800013, 'IS406', '1080808080'),
    ROW(800014, 'IS504', '1080808080'),
    ROW(800015, 'IS505', '1090909090'),
    ROW(800016, 'IS602', '1090909090'),
    ROW(800017, 'IS603', '1090909090'),
    ROW(800018, 'IS604', '1090909090'),
    ROW(800019, 'IS605', '1090909090'),
    ROW(800020, 'IS701', '1090909090'),
    ROW(800021, 'IS702', '1090909090'),
    ROW(800022, 'IS803', '1101010101'),
    ROW(800023, 'IS102', '1050505050'),
    ROW(800024, 'IS103', '1050505050'),
    ROW(800025, 'IS104', '1050505050'),
    ROW(800026, 'IS201', '1050505050'),
    ROW(800027, 'IS203', '1050505050'),
    ROW(800028, 'IS204', '1050505050'),
    ROW(800029, 'IS205', '1050505050'),
    ROW(800030, 'IS301', '1050505050'),
    ROW(800031, 'IS302', '1050505050'),
    ROW(800032, 'IS303', '1060606060'),
    ROW(800033, 'IS304', '1060606060'),
    ROW(800034, 'IS401', '1060606060'),
    ROW(800035, 'IS402', '1060606060'),
    ROW(800036, 'IS501', '1060606060'),
    ROW(800037, 'IS502', '1060606060'),
    ROW(800038, 'IS601', '1060606060'),
    ROW(800039, 'IS801', '1060606060'),
    ROW(800040, 'IS802', '1060606060'),
    ROW(800041, 'IS207', '1121212121'),
    ROW(800042, 'IS307', '1121212121'),
    ROW(800043, 'IS507', '1121212121'),
    ROW(800044, 'IS706', '1121212121'),
    ROW(800045, 'IS707', '1121212121'),
    ROW(800046, 'IS806', '1121212121'),
    ROW(800047, 'IS807', '1060606060'),
    ROW(800048, 'IS902', '1070707070'),
    ROW(800049, 'IS903', '1070707070'),
    ROW(800050, 'IS506', '1111111111'),
    ROW(800051, 'IS606', '1111111111'),
    ROW(800052, 'IS607', '1111111111'),
    ROW(800053, 'IS703', '1111111111'),
    ROW(800054, 'IS704', '1111111111'),
    ROW(800055, 'IS705', '1111111111'),
    ROW(800056, 'IS804', '1111111111'),
    ROW(800057, 'IS805', '1111111111'),
    ROW(800058, 'IS901', '1111111111')
) AS d (id, codigo, documento)
JOIN asignatura a ON a.codigo = d.codigo
JOIN usuario u ON u.numero_documento = d.documento
JOIN docente o ON o.id = u.id
ON DUPLICATE KEY UPDATE grupo.id_grupo = grupo.id_grupo;
