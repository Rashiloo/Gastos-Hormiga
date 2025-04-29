-- Crear y usar la base de datos
DROP DATABASE IF EXISTS gestion_gastos_hormiga;
CREATE DATABASE gestion_gastos_hormiga;
USE gestion_gastos_hormiga;

-- Tabla usuarios: Almacena la información de los usuarios del sistema
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,    -- Identificador único del usuario
    nombre VARCHAR(100) NOT NULL,            -- Nombre completo del usuario
    email VARCHAR(100) NOT NULL UNIQUE,      -- Email para login, debe ser único
    password VARCHAR(255) NOT NULL,          -- Contraseña encriptada
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,  -- Momento en que se registró el usuario
    tiene_presupuesto_inicial BOOLEAN DEFAULT FALSE    -- Indica si ya configuró su presupuesto inicial
);

-- Tabla presupuestos: Registra los presupuestos mensuales de cada usuario
CREATE TABLE presupuestos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,    -- Identificador único del presupuesto
    usuario_id BIGINT NOT NULL,              -- Usuario al que pertenece este presupuesto
    ingreso_mensual DECIMAL(10,2) NOT NULL,  -- Cantidad de dinero que recibe mensualmente
    anio INT NOT NULL,                       -- Año del presupuesto
    mes INT NOT NULL,                        -- Mes del presupuesto (1-12)
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,  -- Cuando se registró el presupuesto
    
    -- Relaciones y restricciones
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    UNIQUE KEY uk_presupuesto_periodo (usuario_id, mes, anio),  -- Un presupuesto por mes/año
    CONSTRAINT chk_ingreso_positivo CHECK (ingreso_mensual > 0),
    CONSTRAINT chk_mes_valido CHECK (mes BETWEEN 1 AND 12),
    CONSTRAINT chk_anio_valido CHECK (anio >= 2024)
);

-- Tabla categorias: Define las categorías principales de gastos
CREATE TABLE categorias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,    -- Identificador único de la categoría
    nombre VARCHAR(50) NOT NULL UNIQUE,      -- Nombre de la categoría (ej: "Alimentación")
    descripcion VARCHAR(100),                -- Descripción detallada de la categoría
    color VARCHAR(7) NOT NULL,               -- Color para visualización en gráficos (#HEXCOD)
    activo BOOLEAN DEFAULT TRUE,             -- Indica si la categoría está disponible
    CONSTRAINT chk_color_formato CHECK (color REGEXP '^#[0-9A-Fa-f]{6}$')  -- Valida formato hex
);

-- Tabla tipos_gasto: Define los tipos específicos de gastos dentro de cada categoría
CREATE TABLE tipos_gasto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,    -- Identificador único del tipo de gasto
    categoria_id BIGINT NOT NULL,            -- Categoría a la que pertenece
    nombre VARCHAR(100) NOT NULL,            -- Nombre del tipo de gasto
    descripcion VARCHAR(255),                -- Descripción detallada
    es_gasto_base BOOLEAN NOT NULL,          -- True: gasto necesario, False: gasto hormiga
    frecuencia ENUM('DIARIO', 'SEMANAL', 'QUINCENAL', 'MENSUAL') NOT NULL DEFAULT 'MENSUAL',
    activo BOOLEAN DEFAULT TRUE,             -- Indica si está disponible para uso
    
    -- Relaciones y restricciones
    FOREIGN KEY (categoria_id) REFERENCES categorias(id),
    UNIQUE KEY uk_nombre_tipo_gasto (nombre)  -- Evita duplicados en nombres
);

-- Tabla gastos: Registra todos los gastos realizados por los usuarios
CREATE TABLE gastos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,    -- Identificador único del gasto
    usuario_id BIGINT NOT NULL,              -- Usuario que realizó el gasto
    tipo_gasto_id BIGINT NOT NULL,           -- Tipo de gasto realizado
    monto DECIMAL(10,2) NOT NULL,            -- Cantidad gastada
    fecha_gasto DATE NOT NULL,               -- Fecha en que se realizó el gasto
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,  -- Cuando se registró en el sistema
    descripcion VARCHAR(100),                -- Descripción opcional del gasto
    periodo_inicio DATE,                     -- Inicio del período (para gastos recurrentes)
    periodo_fin DATE,                        -- Fin del período (para gastos recurrentes)
    
    -- Relaciones y restricciones
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (tipo_gasto_id) REFERENCES tipos_gasto(id),
    CONSTRAINT chk_monto_positivo CHECK (monto > 0)
);

-- Tabla metas_ahorro: Gestiona las metas de ahorro de los usuarios
CREATE TABLE metas_ahorro (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,    -- Identificador único de la meta
    usuario_id BIGINT NOT NULL,              -- Usuario dueño de la meta
    nombre VARCHAR(100) NOT NULL,            -- Nombre descriptivo de la meta
    monto_objetivo DECIMAL(10,2) NOT NULL,   -- Cantidad que se desea ahorrar
    fecha_inicio DATE NOT NULL,              -- Fecha de inicio de la meta
    fecha_fin DATE NOT NULL,                 -- Fecha límite para alcanzar la meta
    estado ENUM('ACTIVA', 'COMPLETADA', 'CANCELADA') DEFAULT 'ACTIVA',  -- Estado actual
    total_gastos_hormiga_periodo DECIMAL(10,2) NOT NULL,  -- Total de gastos hormiga del período
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,     -- Cuando se creó la meta
    
    -- Relaciones y restricciones
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    CONSTRAINT chk_monto_objetivo_positivo CHECK (monto_objetivo > 0),
    CONSTRAINT chk_fechas_validas CHECK (fecha_fin > fecha_inicio),
    CONSTRAINT chk_monto_objetivo_valido CHECK (monto_objetivo <= total_gastos_hormiga_periodo)
);

-- Tabla gastos_evitados: Registra los gastos que el usuario evitó para alcanzar sus metas
CREATE TABLE gastos_evitados (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,    -- Identificador único
    meta_id BIGINT NOT NULL,                 -- Meta de ahorro relacionada
    tipo_gasto_id BIGINT NOT NULL,           -- Tipo de gasto que se evitó
    monto DECIMAL(10,2) NOT NULL,            -- Cantidad que se evitó gastar
    fecha_registro DATE NOT NULL,            -- Cuando se evitó el gasto
    descripcion VARCHAR(100),                -- Cómo se evitó el gasto
    evidencia_ahorro TEXT,                   -- Prueba del ahorro (ej: foto, nota)
    
    -- Relaciones y restricciones
    FOREIGN KEY (meta_id) REFERENCES metas_ahorro(id),
    FOREIGN KEY (tipo_gasto_id) REFERENCES tipos_gasto(id),
    CONSTRAINT chk_monto_evitado_positivo CHECK (monto > 0)
);

-- Tabla analisis_gastos: Almacena análisis periódicos de los gastos
CREATE TABLE analisis_gastos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,    -- Identificador único del análisis
    usuario_id BIGINT NOT NULL,              -- Usuario analizado
    fecha_inicio DATE NOT NULL,              -- Inicio del período analizado
    fecha_fin DATE NOT NULL,                 -- Fin del período analizado
    total_gastos_base DECIMAL(10,2) NOT NULL,      -- Total de gastos necesarios
    total_gastos_hormiga DECIMAL(10,2) NOT NULL,   -- Total de gastos hormiga
    porcentaje_gastos_hormiga DECIMAL(5,2) NOT NULL,  -- Porcentaje que representan
    fecha_analisis DATETIME DEFAULT CURRENT_TIMESTAMP, -- Cuando se realizó el análisis
    
    -- Relaciones
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

DELIMITER //

-- Trigger para validar fecha_gasto antes de INSERT
CREATE TRIGGER tr_validar_fecha_gasto_before_insert
BEFORE INSERT ON gastos
FOR EACH ROW
BEGIN
    IF NEW.fecha_gasto > CURDATE() THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se pueden registrar gastos con fecha futura';
    END IF;
END//

-- Trigger para validar fecha_gasto antes de UPDATE
CREATE TRIGGER tr_validar_fecha_gasto_before_update
BEFORE UPDATE ON gastos
FOR EACH ROW
BEGIN
    IF NEW.fecha_gasto > CURDATE() THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se pueden registrar gastos con fecha futura';
    END IF;
END//

-- Función para calcular el total de gastos hormiga
CREATE FUNCTION fn_calcular_total_gastos_hormiga(
    p_usuario_id BIGINT,
    p_fecha_inicio DATE,
    p_fecha_fin DATE
) 
RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
    DECLARE total DECIMAL(10,2);
    
    SELECT COALESCE(SUM(g.monto), 0)
    INTO total
    FROM gastos g
    INNER JOIN tipos_gasto tg ON g.tipo_gasto_id = tg.id
    WHERE g.usuario_id = p_usuario_id
    AND g.fecha_gasto BETWEEN p_fecha_inicio AND p_fecha_fin
    AND tg.es_gasto_base = FALSE;
    
    RETURN total;
END//

-- Función para calcular el total de gastos base
CREATE FUNCTION fn_calcular_total_gastos_base(
    p_usuario_id BIGINT,
    p_fecha_inicio DATE,
    p_fecha_fin DATE
) 
RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
    DECLARE total DECIMAL(10,2);
    
    SELECT COALESCE(SUM(g.monto), 0)
    INTO total
    FROM gastos g
    INNER JOIN tipos_gasto tg ON g.tipo_gasto_id = tg.id
    WHERE g.usuario_id = p_usuario_id
    AND g.fecha_gasto BETWEEN p_fecha_inicio AND p_fecha_fin
    AND tg.es_gasto_base = TRUE;
    
    RETURN total;
END//

-- Función para calcular el ahorro diario necesario
CREATE FUNCTION fn_calcular_ahorro_diario_necesario(
    p_meta_id BIGINT
) 
RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
    DECLARE v_monto_objetivo DECIMAL(10,2);
    DECLARE v_fecha_inicio DATE;
    DECLARE v_fecha_fin DATE;
    DECLARE v_dias_restantes INT;
    DECLARE v_total_ahorrado DECIMAL(10,2);
    DECLARE v_ahorro_diario DECIMAL(10,2);
    
    -- Obtener datos de la meta
    SELECT monto_objetivo, fecha_inicio, fecha_fin
    INTO v_monto_objetivo, v_fecha_inicio, v_fecha_fin
    FROM metas_ahorro
    WHERE id = p_meta_id;
    
    -- Calcular días restantes
    SET v_dias_restantes = DATEDIFF(v_fecha_fin, GREATEST(CURDATE(), v_fecha_inicio));
    
    -- Obtener total ahorrado
    SELECT COALESCE(SUM(monto), 0)
    INTO v_total_ahorrado
    FROM gastos_evitados
    WHERE meta_id = p_meta_id;
    
    -- Calcular ahorro diario necesario
    IF v_dias_restantes > 0 THEN
        SET v_ahorro_diario = (v_monto_objetivo - v_total_ahorrado) / v_dias_restantes;
    ELSE
        SET v_ahorro_diario = 0;
    END IF;
    
    RETURN GREATEST(v_ahorro_diario, 0);
END//

-- Trigger para validar meta de ahorro antes de INSERT
CREATE TRIGGER tr_validar_meta_ahorro_before_insert
BEFORE INSERT ON metas_ahorro
FOR EACH ROW
BEGIN
    DECLARE v_total_gastos_hormiga DECIMAL(10,2);
    
    -- Calcular total de gastos hormiga para el período
    SET v_total_gastos_hormiga = fn_calcular_total_gastos_hormiga(
        NEW.usuario_id,
        DATE_SUB(NEW.fecha_inicio, INTERVAL 1 MONTH),
        NEW.fecha_inicio
    );
    
    -- Actualizar el campo total_gastos_hormiga_periodo
    SET NEW.total_gastos_hormiga_periodo = v_total_gastos_hormiga;
    
    -- Validar que el monto objetivo no exceda el total de gastos hormiga
    IF NEW.monto_objetivo > v_total_gastos_hormiga THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El monto objetivo no puede ser mayor que el total de gastos hormiga del período anterior';
    END IF;
END//

-- Trigger para validar meta de ahorro antes de UPDATE
CREATE TRIGGER tr_validar_meta_ahorro_before_update
BEFORE UPDATE ON metas_ahorro
FOR EACH ROW
BEGIN
    DECLARE v_total_gastos_hormiga DECIMAL(10,2);
    
    -- Solo recalcular si cambian las fechas o el monto objetivo
    IF NEW.fecha_inicio != OLD.fecha_inicio 
    OR NEW.fecha_fin != OLD.fecha_fin 
    OR NEW.monto_objetivo != OLD.monto_objetivo THEN
        
        -- Calcular total de gastos hormiga para el período
        SET v_total_gastos_hormiga = fn_calcular_total_gastos_hormiga(
            NEW.usuario_id,
            DATE_SUB(NEW.fecha_inicio, INTERVAL 1 MONTH),
            NEW.fecha_inicio
        );
        
        -- Actualizar el campo total_gastos_hormiga_periodo
        SET NEW.total_gastos_hormiga_periodo = v_total_gastos_hormiga;
        
        -- Validar que el monto objetivo no exceda el total de gastos hormiga
        IF NEW.monto_objetivo > v_total_gastos_hormiga THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'El monto objetivo no puede ser mayor que el total de gastos hormiga del período anterior';
        END IF;
    END IF;
END//

DELIMITER ;

DELIMITER //

-- Procedimiento para registrar un nuevo gasto
CREATE PROCEDURE sp_registrar_gasto(
    IN p_usuario_id BIGINT,
    IN p_tipo_gasto_id BIGINT,
    IN p_monto DECIMAL(10,2),
    IN p_fecha_gasto DATE,
    IN p_descripcion VARCHAR(100)
)
BEGIN
    -- Validar que el usuario existe
    IF NOT EXISTS (SELECT 1 FROM usuarios WHERE id = p_usuario_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Usuario no encontrado';
    END IF;
    
    -- Validar que el tipo de gasto existe y está activo
    IF NOT EXISTS (SELECT 1 FROM tipos_gasto WHERE id = p_tipo_gasto_id AND activo = TRUE) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Tipo de gasto no válido o inactivo';
    END IF;
    
    -- Insertar el gasto
    INSERT INTO gastos (usuario_id, tipo_gasto_id, monto, fecha_gasto, descripcion)
    VALUES (p_usuario_id, p_tipo_gasto_id, p_monto, p_fecha_gasto, p_descripcion);
END//

-- Procedimiento para crear una meta de ahorro
CREATE PROCEDURE sp_crear_meta_ahorro(
    IN p_usuario_id BIGINT,
    IN p_nombre VARCHAR(100),
    IN p_monto_objetivo DECIMAL(10,2),
    IN p_fecha_inicio DATE,
    IN p_fecha_fin DATE
)
BEGIN
    DECLARE v_total_gastos_hormiga DECIMAL(10,2);
    
    -- Validar que el usuario existe
    IF NOT EXISTS (SELECT 1 FROM usuarios WHERE id = p_usuario_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Usuario no encontrado';
    END IF;
    
    -- Validar fechas
    IF p_fecha_inicio < CURDATE() THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La fecha de inicio no puede ser anterior a hoy';
    END IF;
    
    IF p_fecha_fin <= p_fecha_inicio THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La fecha fin debe ser posterior a la fecha de inicio';
    END IF;
    
    -- Calcular total de gastos hormiga del último mes
    SET v_total_gastos_hormiga = fn_calcular_total_gastos_hormiga(
        p_usuario_id,
        DATE_SUB(CURDATE(), INTERVAL 1 MONTH),
        CURDATE()
    );
    
    -- Insertar la meta de ahorro (el trigger validará el monto objetivo)
    INSERT INTO metas_ahorro (
        usuario_id, 
        nombre, 
        monto_objetivo, 
        fecha_inicio, 
        fecha_fin,
        total_gastos_hormiga_periodo
    )
    VALUES (
        p_usuario_id, 
        p_nombre, 
        p_monto_objetivo, 
        p_fecha_inicio, 
        p_fecha_fin,
        v_total_gastos_hormiga
    );
END//

-- Procedimiento para registrar un gasto evitado
CREATE PROCEDURE sp_registrar_gasto_evitado(
    IN p_meta_id BIGINT,
    IN p_tipo_gasto_id BIGINT,
    IN p_monto DECIMAL(10,2),
    IN p_descripcion VARCHAR(100),
    IN p_evidencia_ahorro TEXT
)
BEGIN
    DECLARE v_usuario_id BIGINT;
    DECLARE v_estado VARCHAR(20);
    
    -- Obtener información de la meta
    SELECT usuario_id, estado 
    INTO v_usuario_id, v_estado
    FROM metas_ahorro 
    WHERE id = p_meta_id;
    
    -- Validar que la meta existe y está activa
    IF v_usuario_id IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Meta de ahorro no encontrada';
    END IF;
    
    IF v_estado != 'ACTIVA' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La meta de ahorro no está activa';
    END IF;
    
    -- Validar que el tipo de gasto es un gasto hormiga
    IF NOT EXISTS (
        SELECT 1 
        FROM tipos_gasto 
        WHERE id = p_tipo_gasto_id 
        AND es_gasto_base = FALSE 
        AND activo = TRUE
    ) THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'El tipo de gasto debe ser un gasto hormiga válido y activo';
    END IF;
    
    -- Registrar el gasto evitado
    INSERT INTO gastos_evitados (
        meta_id,
        tipo_gasto_id,
        monto,
        fecha_registro,
        descripcion,
        evidencia_ahorro
    )
    VALUES (
        p_meta_id,
        p_tipo_gasto_id,
        p_monto,
        CURDATE(),
        p_descripcion,
        p_evidencia_ahorro
    );
    
    -- Verificar si se alcanzó la meta
    CALL sp_verificar_meta_completada(p_meta_id);
END//

-- Procedimiento para verificar si una meta se completó
CREATE PROCEDURE sp_verificar_meta_completada(
    IN p_meta_id BIGINT
)
BEGIN
    DECLARE v_monto_objetivo DECIMAL(10,2);
    DECLARE v_total_ahorrado DECIMAL(10,2);
    
    -- Obtener el monto objetivo de la meta
    SELECT monto_objetivo
    INTO v_monto_objetivo
    FROM metas_ahorro
    WHERE id = p_meta_id;
    
    -- Calcular el total ahorrado
    SELECT COALESCE(SUM(monto), 0)
    INTO v_total_ahorrado
    FROM gastos_evitados
    WHERE meta_id = p_meta_id;
    
    -- Actualizar estado si se alcanzó la meta
    IF v_total_ahorrado >= v_monto_objetivo THEN
        UPDATE metas_ahorro
        SET estado = 'COMPLETADA'
        WHERE id = p_meta_id;
    END IF;
END//

-- Procedimiento para obtener el resumen de gastos por categoría
CREATE PROCEDURE sp_obtener_resumen_gastos_categoria(
    IN p_usuario_id BIGINT,
    IN p_fecha_inicio DATE,
    IN p_fecha_fin DATE
)
BEGIN
    SELECT 
        c.nombre AS categoria,
        c.color,
        SUM(g.monto) AS total_gastado,
        COUNT(*) AS cantidad_gastos,
        tg.es_gasto_base
    FROM gastos g
    INNER JOIN tipos_gasto tg ON g.tipo_gasto_id = tg.id
    INNER JOIN categorias c ON tg.categoria_id = c.id
    WHERE g.usuario_id = p_usuario_id
    AND g.fecha_gasto BETWEEN p_fecha_inicio AND p_fecha_fin
    GROUP BY c.nombre, c.color, tg.es_gasto_base
    ORDER BY total_gastado DESC;
END//

-- Procedimiento para obtener el progreso de una meta de ahorro
CREATE PROCEDURE sp_obtener_progreso_meta_ahorro(
    IN p_meta_id BIGINT
)
BEGIN
    SELECT 
        m.nombre AS nombre_meta,
        m.monto_objetivo,
        m.fecha_inicio,
        m.fecha_fin,
        m.estado,
        COALESCE(SUM(ge.monto), 0) AS total_ahorrado,
        (COALESCE(SUM(ge.monto), 0) / m.monto_objetivo * 100) AS porcentaje_completado,
        fn_calcular_ahorro_diario_necesario(m.id) AS ahorro_diario_necesario,
        DATEDIFF(m.fecha_fin, GREATEST(CURDATE(), m.fecha_inicio)) AS dias_restantes
    FROM metas_ahorro m
    LEFT JOIN gastos_evitados ge ON m.id = ge.meta_id
    WHERE m.id = p_meta_id
    GROUP BY m.id;
END//

DELIMITER ;

DELIMITER //

-- Procedimiento para análisis semanal de gastos
CREATE PROCEDURE sp_analisis_semanal_gastos(
    IN p_usuario_id BIGINT
)
BEGIN
    DECLARE v_fecha_inicio DATE;
    DECLARE v_fecha_fin DATE;
    
    -- Establecer período de análisis (última semana)
    SET v_fecha_inicio = DATE_SUB(CURDATE(), INTERVAL 7 DAY);
    SET v_fecha_fin = CURDATE();
    
    -- Análisis detallado de gastos por día
    SELECT 
        DATE(g.fecha_gasto) AS fecha,
        c.nombre AS categoria,
        tg.nombre AS tipo_gasto,
        SUM(g.monto) AS total_diario,
        COUNT(*) AS frecuencia,
        tg.es_gasto_base
    FROM gastos g
    INNER JOIN tipos_gasto tg ON g.tipo_gasto_id = tg.id
    INNER JOIN categorias c ON tg.categoria_id = c.id
    WHERE g.usuario_id = p_usuario_id
    AND g.fecha_gasto BETWEEN v_fecha_inicio AND v_fecha_fin
    GROUP BY DATE(g.fecha_gasto), c.nombre, tg.nombre, tg.es_gasto_base
    ORDER BY fecha DESC, total_diario DESC;
    
    -- Resumen semanal
    SELECT 
        fn_calcular_total_gastos_base(p_usuario_id, v_fecha_inicio, v_fecha_fin) AS total_gastos_base,
        fn_calcular_total_gastos_hormiga(p_usuario_id, v_fecha_inicio, v_fecha_fin) AS total_gastos_hormiga,
        (SELECT COUNT(DISTINCT DATE(fecha_gasto)) 
         FROM gastos 
         WHERE usuario_id = p_usuario_id 
         AND fecha_gasto BETWEEN v_fecha_inicio AND v_fecha_fin) AS dias_con_gastos;
END//

-- Procedimiento para análisis mensual de gastos
CREATE PROCEDURE sp_analisis_mensual_gastos(
    IN p_usuario_id BIGINT
)
BEGIN
    DECLARE v_fecha_inicio DATE;
    DECLARE v_fecha_fin DATE;
    DECLARE v_presupuesto_mensual DECIMAL(10,2);
    
    -- Establecer período de análisis (último mes)
    SET v_fecha_inicio = DATE_SUB(CURDATE(), INTERVAL 1 MONTH);
    SET v_fecha_fin = CURDATE();
    
    -- Obtener presupuesto del mes actual
    SELECT ingreso_mensual 
    INTO v_presupuesto_mensual
    FROM presupuestos
    WHERE usuario_id = p_usuario_id
    AND anio = YEAR(CURDATE())
    AND mes = MONTH(CURDATE())
    LIMIT 1;
    
    -- Análisis por categoría
    SELECT 
        c.nombre AS categoria,
        c.color,
        SUM(CASE WHEN tg.es_gasto_base = TRUE THEN g.monto ELSE 0 END) AS total_gastos_base,
        SUM(CASE WHEN tg.es_gasto_base = FALSE THEN g.monto ELSE 0 END) AS total_gastos_hormiga,
        COUNT(*) AS cantidad_gastos,
        ROUND((SUM(g.monto) / v_presupuesto_mensual) * 100, 2) AS porcentaje_presupuesto
    FROM gastos g
    INNER JOIN tipos_gasto tg ON g.tipo_gasto_id = tg.id
    INNER JOIN categorias c ON tg.categoria_id = c.id
    WHERE g.usuario_id = p_usuario_id
    AND g.fecha_gasto BETWEEN v_fecha_inicio AND v_fecha_fin
    GROUP BY c.nombre, c.color
    ORDER BY total_gastos_hormiga DESC;
    
    -- Análisis de tendencias
    SELECT 
        WEEK(fecha_gasto) AS semana,
        SUM(CASE WHEN tg.es_gasto_base = TRUE THEN g.monto ELSE 0 END) AS total_gastos_base,
        SUM(CASE WHEN tg.es_gasto_base = FALSE THEN g.monto ELSE 0 END) AS total_gastos_hormiga
    FROM gastos g
    INNER JOIN tipos_gasto tg ON g.tipo_gasto_id = tg.id
    WHERE g.usuario_id = p_usuario_id
    AND g.fecha_gasto BETWEEN v_fecha_inicio AND v_fecha_fin
    GROUP BY WEEK(fecha_gasto)
    ORDER BY semana;
END//

-- Procedimiento para análisis de tendencias de gastos hormiga
CREATE PROCEDURE sp_analisis_tendencias_gastos_hormiga(
    IN p_usuario_id BIGINT
)
BEGIN
    DECLARE v_fecha_inicio DATE;
    DECLARE v_fecha_fin DATE;
    
    SET v_fecha_inicio = DATE_SUB(CURDATE(), INTERVAL 3 MONTH);
    SET v_fecha_fin = CURDATE();
    
    -- Patrones de gasto por día de la semana
    SELECT 
        DAYNAME(fecha_gasto) AS dia_semana,
        COUNT(*) AS frecuencia,
        AVG(monto) AS promedio_gasto,
        SUM(monto) AS total_gasto
    FROM gastos g
    INNER JOIN tipos_gasto tg ON g.tipo_gasto_id = tg.id
    WHERE g.usuario_id = p_usuario_id
    AND g.fecha_gasto BETWEEN v_fecha_inicio AND v_fecha_fin
    AND tg.es_gasto_base = FALSE
    GROUP BY DAYNAME(fecha_gasto)
    ORDER BY DAYOFWEEK(fecha_gasto);
    
    -- Gastos hormiga más frecuentes
    SELECT 
        tg.nombre AS tipo_gasto,
        c.nombre AS categoria,
        COUNT(*) AS frecuencia,
        AVG(g.monto) AS promedio_gasto,
        SUM(g.monto) AS total_gastado
    FROM gastos g
    INNER JOIN tipos_gasto tg ON g.tipo_gasto_id = tg.id
    INNER JOIN categorias c ON tg.categoria_id = c.id
    WHERE g.usuario_id = p_usuario_id
    AND g.fecha_gasto BETWEEN v_fecha_inicio AND v_fecha_fin
    AND tg.es_gasto_base = FALSE
    GROUP BY tg.id, tg.nombre, c.nombre
    HAVING COUNT(*) > 5
    ORDER BY frecuencia DESC;
END//

-- Procedimiento para generar recomendaciones de ahorro
CREATE PROCEDURE sp_generar_recomendaciones_ahorro(
    IN p_usuario_id BIGINT
)
BEGIN
    DECLARE v_total_gastos_hormiga DECIMAL(10,2);
    DECLARE v_ingreso_mensual DECIMAL(10,2);
    
    -- Obtener datos relevantes
    SELECT ingreso_mensual 
    INTO v_ingreso_mensual
    FROM presupuestos
    WHERE usuario_id = p_usuario_id
    AND anio = YEAR(CURDATE())
    AND mes = MONTH(CURDATE())
    LIMIT 1;
    
    SET v_total_gastos_hormiga = fn_calcular_total_gastos_hormiga(
        p_usuario_id,
        DATE_SUB(CURDATE(), INTERVAL 1 MONTH),
        CURDATE()
    );
    
    -- Gastos hormiga más significativos
    SELECT 
        tg.nombre AS tipo_gasto,
        c.nombre AS categoria,
        COUNT(*) AS frecuencia,
        AVG(g.monto) AS gasto_promedio,
        SUM(g.monto) AS total_mes,
        ROUND((SUM(g.monto) / v_ingreso_mensual) * 100, 2) AS porcentaje_ingreso,
        CASE 
            WHEN COUNT(*) > 15 THEN 'Alta'
            WHEN COUNT(*) > 8 THEN 'Media'
            ELSE 'Baja'
        END AS prioridad_ahorro
    FROM gastos g
    INNER JOIN tipos_gasto tg ON g.tipo_gasto_id = tg.id
    INNER JOIN categorias c ON tg.categoria_id = c.id
    WHERE g.usuario_id = p_usuario_id
    AND g.fecha_gasto >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)
    AND tg.es_gasto_base = FALSE
    GROUP BY tg.id, tg.nombre, c.nombre
    HAVING SUM(g.monto) > (v_total_gastos_hormiga * 0.1)
    ORDER BY total_mes DESC;
    
    -- Días con mayor gasto hormiga
    SELECT 
        DAYNAME(fecha_gasto) AS dia_semana,
        COUNT(*) AS frecuencia,
        AVG(monto) AS promedio_gasto
    FROM gastos g
    INNER JOIN tipos_gasto tg ON g.tipo_gasto_id = tg.id
    WHERE g.usuario_id = p_usuario_id
    AND g.fecha_gasto >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)
    AND tg.es_gasto_base = FALSE
    GROUP BY DAYNAME(fecha_gasto)
    ORDER BY promedio_gasto DESC;
END//

-- Procedimiento para seguimiento de metas de ahorro
CREATE PROCEDURE sp_seguimiento_metas_ahorro(
    IN p_usuario_id BIGINT
)
BEGIN
    -- Resumen de todas las metas activas
    SELECT 
        m.nombre AS meta,
        m.monto_objetivo,
        m.fecha_inicio,
        m.fecha_fin,
        COALESCE(SUM(ge.monto), 0) AS total_ahorrado,
        (COALESCE(SUM(ge.monto), 0) / m.monto_objetivo * 100) AS porcentaje_completado,
        fn_calcular_ahorro_diario_necesario(m.id) AS ahorro_diario_necesario,
        DATEDIFF(m.fecha_fin, GREATEST(CURDATE(), m.fecha_inicio)) AS dias_restantes,
        m.total_gastos_hormiga_periodo
    FROM metas_ahorro m
    LEFT JOIN gastos_evitados ge ON m.id = ge.meta_id
    WHERE m.usuario_id = p_usuario_id
    AND m.estado = 'ACTIVA'
    GROUP BY m.id;
    
    -- Detalle de ahorros recientes
    SELECT 
        ge.fecha_registro,
        tg.nombre AS tipo_gasto_evitado,
        ge.monto,
        ge.descripcion
    FROM gastos_evitados ge
    INNER JOIN metas_ahorro m ON ge.meta_id = m.id
    INNER JOIN tipos_gasto tg ON ge.tipo_gasto_id = tg.id
    WHERE m.usuario_id = p_usuario_id
    AND ge.fecha_registro >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)
    ORDER BY ge.fecha_registro DESC;
END//

DELIMITER ;

-- Inserción de categorías predefinidas
INSERT INTO categorias (nombre, descripcion, color) VALUES
('Alimentación', 'Gastos relacionados con comida y bebida', '#FF5733'),
('Transporte', 'Gastos de movilidad y transporte', '#33FF57'),
('Entretenimiento', 'Gastos en actividades recreativas', '#3357FF'),
('Compras', 'Gastos en artículos varios', '#FF33F5'),
('Servicios', 'Gastos en servicios varios', '#33FFF5'),
('Salud', 'Gastos relacionados con la salud', '#FF3333'),
('Educación', 'Gastos en formación y educación', '#FFB533'),
('Vivienda', 'Gastos relacionados con la vivienda', '#33FFBB'),
('Personal', 'Gastos de cuidado personal', '#8033FF'),
('Otros', 'Otros gastos no categorizados', '#808080');

-- Inserción de tipos de gastos base (necesarios)
INSERT INTO tipos_gasto (categoria_id, nombre, descripcion, es_gasto_base, frecuencia) VALUES
-- Alimentación básica
((SELECT id FROM categorias WHERE nombre = 'Alimentación'), 
 'Compras de supermercado', 
 'Compras de alimentos básicos y productos de primera necesidad',
 TRUE, 'SEMANAL'),

-- Transporte necesario
((SELECT id FROM categorias WHERE nombre = 'Transporte'),
 'Transporte público mensual',
 'Abono o tarjeta de transporte público mensual',
 TRUE, 'MENSUAL'),

-- Vivienda
((SELECT id FROM categorias WHERE nombre = 'Vivienda'),
 'Alquiler',
 'Pago mensual de alquiler o hipoteca',
 TRUE, 'MENSUAL'),

((SELECT id FROM categorias WHERE nombre = 'Vivienda'),
 'Servicios básicos',
 'Agua, luz, gas',
 TRUE, 'MENSUAL'),

-- Servicios esenciales
((SELECT id FROM categorias WHERE nombre = 'Servicios'),
 'Internet y telefonía',
 'Servicios de conectividad básicos',
 TRUE, 'MENSUAL'),

-- Salud básica
((SELECT id FROM categorias WHERE nombre = 'Salud'),
 'Medicamentos recurrentes',
 'Medicamentos de uso regular necesario',
 TRUE, 'MENSUAL');

-- Inserción de tipos de gastos hormiga (no esenciales)
INSERT INTO tipos_gasto (categoria_id, nombre, descripcion, es_gasto_base, frecuencia) VALUES
-- Alimentación no esencial
((SELECT id FROM categorias WHERE nombre = 'Alimentación'),
 'Café de máquina',
 'Café comprado en máquinas expendedoras',
 FALSE, 'DIARIO'),

((SELECT id FROM categorias WHERE nombre = 'Alimentación'),
 'Snacks y golosinas',
 'Compras impulsivas de snacks y dulces',
 FALSE, 'DIARIO'),

((SELECT id FROM categorias WHERE nombre = 'Alimentación'),
 'Comida rápida',
 'Compras en establecimientos de comida rápida',
 FALSE, 'DIARIO'),

-- Transporte no esencial
((SELECT id FROM categorias WHERE nombre = 'Transporte'),
 'Taxi por pereza',
 'Uso de taxi cuando hay alternativas más económicas',
 FALSE, 'DIARIO'),

-- Entretenimiento
((SELECT id FROM categorias WHERE nombre = 'Entretenimiento'),
 'Aplicaciones y juegos',
 'Compras impulsivas de apps o juegos',
 FALSE, 'DIARIO'),

((SELECT id FROM categorias WHERE nombre = 'Entretenimiento'),
 'Suscripciones no utilizadas',
 'Servicios de streaming o suscripciones poco utilizadas',
 FALSE, 'MENSUAL'),

-- Compras impulsivas
((SELECT id FROM categorias WHERE nombre = 'Compras'),
 'Compras impulsivas',
 'Artículos comprados por impulso',
 FALSE, 'DIARIO'),

-- Personal
((SELECT id FROM categorias WHERE nombre = 'Personal'),
 'Productos innecesarios',
 'Productos de cuidado personal no esenciales',
 FALSE, 'DIARIO'),

-- Otros gastos hormiga
((SELECT id FROM categorias WHERE nombre = 'Otros'),
 'Gastos evitables',
 'Otros gastos pequeños que podrían evitarse',
 FALSE, 'DIARIO');

-- Procedimiento para crear datos de ejemplo
DELIMITER //

CREATE PROCEDURE sp_crear_datos_ejemplo()
BEGIN
    DECLARE v_usuario_id BIGINT;
    DECLARE v_tipo_gasto_cafe_id BIGINT;
    DECLARE v_tipo_gasto_snacks_id BIGINT;
    
    -- Crear usuario de ejemplo
    INSERT INTO usuarios (nombre, email, password)
    VALUES ('Usuario Demo', 'demo@example.com', SHA2('password123', 256));
    
    SET v_usuario_id = LAST_INSERT_ID();
    
    -- Crear presupuesto inicial
    INSERT INTO presupuestos (usuario_id, ingreso_mensual, anio, mes)
    VALUES (v_usuario_id, 3000.00, YEAR(CURDATE()), MONTH(CURDATE()));
    
    -- Obtener IDs de tipos de gasto específicos
    SELECT id INTO v_tipo_gasto_cafe_id 
    FROM tipos_gasto 
    WHERE nombre = 'Café de máquina' 
    LIMIT 1;
    
    SELECT id INTO v_tipo_gasto_snacks_id
    FROM tipos_gasto 
    WHERE nombre = 'Snacks y golosinas'
    LIMIT 1;
    
    -- Insertar gastos de ejemplo del último mes
    INSERT INTO gastos (usuario_id, tipo_gasto_id, monto, fecha_gasto, descripcion)
    SELECT 
        v_usuario_id,
        CASE 
            WHEN MOD(dia, 2) = 0 THEN v_tipo_gasto_cafe_id
            ELSE v_tipo_gasto_snacks_id
        END,
        CASE 
            WHEN MOD(dia, 2) = 0 THEN 2.50
            ELSE 3.00
        END,
        DATE_SUB(CURDATE(), INTERVAL dia DAY),
        CASE 
            WHEN MOD(dia, 2) = 0 THEN 'Café de la mañana'
            ELSE 'Snacks de media tarde'
        END
    FROM (
        SELECT a.N + b.N * 10 + 1 as dia
        FROM (SELECT 0 as N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
             (SELECT 0 as N UNION SELECT 1 UNION SELECT 2) b
        WHERE a.N + b.N * 10 < 30
    ) nums;
    
    -- Crear una meta de ahorro de ejemplo
    INSERT INTO metas_ahorro (
        usuario_id,
        nombre,
        monto_objetivo,
        fecha_inicio,
        fecha_fin,
        total_gastos_hormiga_periodo
    )
    VALUES (
        v_usuario_id,
        'Reducir gastos en café y snacks',
        100.00,
        CURDATE(),
        DATE_ADD(CURDATE(), INTERVAL 1 MONTH),
        (SELECT COALESCE(SUM(monto), 0)
         FROM gastos
         WHERE usuario_id = v_usuario_id
         AND tipo_gasto_id IN (v_tipo_gasto_cafe_id, v_tipo_gasto_snacks_id)
         AND fecha_gasto >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH))
    );
END //

DELIMITER ;

-- Ejecutar el procedimiento para crear datos de ejemplo
CALL sp_crear_datos_ejemplo();

