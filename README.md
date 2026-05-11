# Crypto Monitor

## 1. Descripción del Proyecto

Este proyecto consiste en el desarrollo de una plataforma avanzada de monitorización
de criptomonedas en tiempo real, diseñada bajo una Arquitectura Lambda para el procesamiento 
masivo de datos. El sistema integra tres flujos de información críticos:

    1. Ingesta de Precios: Captura automática de cotizaciones de las principales criptomonedas a través de la API de CoinGecko.
    
    2. Web Scraping de Noticias: Recolección de artículos de prensa especializada (Decrypt) para identificar eventos que impacten el mercado.

    3. Persistencia y Análisis: Un motor de almacenamiento de eventos (Datalake) y una unidad de negocio que unifica datos históricos y en tiempo real en un Datamart persistente (SQLite).

## 2. Propuesta de Valor

La principal ventaja competitiva de esta herramienta no es solo la visualización de datos, sino la generación de 
conocimiento accionable para el usuario:

    Detección de Volatilidad Contextual: El sistema identifica variaciones de precio superiores al 1% y, de forma inmediata, busca noticias relacionadas en el datamart para explicar el movimiento.
    
    Análisis de Sentimiento: Mediante un motor de procesamiento de lenguaje natural basado en palabras clave, la plataforma asigna un sentimiento (Positivo, Negativo o Neutro) a las causas de la volatilidad, permitiendo al inversor tomar decisiones informadas rápidamente.

    Integridad y Resiliencia: Gracias al uso de suscriptores duraderos, garantizamos que no se pierda ni un solo dato de mercado durante caídas del sistema o paradas de mantenimiento, asegurando un histórico continuo y fiable.

    Arquitectura de Datos Unificada: Combinamos el almacenamiento masivo de eventos históricos con el procesamiento en tiempo real, ofreciendo al usuario una transición fluida entre los datos del pasado y la actividad en vivo del mercado.

    Versatilidad de Interfaz: El sistema ofrece flexibilidad de visualización mediante una interfaz gráfica detallada para análisis visual y una interfaz de consola (CLI) para una monitorización técnica y ligera.

## 3. Justificación de Tecnologías y Estructura de Datamart

### 3.1. Elección de Fuentes de Datos (APIs y Scraping)

    CoinGecko API: Se seleccionó por ser una de las fuentes más fiables y completas en el sector cripto. Su campo last_updated permite una sincronización precisa con el mercado, evitando procesar datos obsoletos si la cotización no ha variado.

    Scraping de Decrypt: Se eligió esta fuente de noticias por su enfoque técnico y la estructura clara de sus artículos (título, cuerpo y fecha de publicación). Esto facilita la extracción de metadatos críticos para el análisis de sentimiento y la vinculación con activos específicos

### 3.2. Estructura del Datamart (SQLite)

    Persistencia y Relación: A diferencia de una solución en memoria, SQLite permite que los datos sobrevivan a reinicios de la Business Unit y facilita consultas complejas para unir precios y noticias mediante el identificador único de la criptomoneda.

    Integridad de Datos: Se han definido claves primarias compuestas en la tabla de noticias para evitar la duplicidad de información. El uso de la instrucción INSERT OR IGNORE optimiza el rendimiento del sistema al descartar automáticamente eventos ya procesados, reduciendo el consumo de recursos en disco.

    Eficiencia en Consultas: La estructura permite recuperar rápidamente los 10 precios más recientes para las alertas de volatilidad, garantizando una respuesta fluida de la interfaz de usuario.