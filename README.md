# Crypto Monitor

## 1. Descripción del Proyecto

Este proyecto consiste en el desarrollo de una plataforma avanzada de monitorización
de criptomonedas en tiempo real, diseñada bajo una Arquitectura Lambda para el procesamiento 
masivo de datos. El sistema integra tres flujos de información críticos:

    1. Ingesta de Precios: Captura automática de cotizaciones de las principales criptomonedas a través de la API de CoinGecko.
    
    2. Web Scraping de Noticias: Recolección de artículos de prensa especializada (Decrypt) para identificar eventos que impacten el mercado.

    3. Persistencia y Análisis: Un motor de almacenamiento de eventos (Datalake) y una unidad de negocio que unifica datos históricos y en tiempo real en un Datamart persistente (SQLite).

    4. Correlación Estadística: Implementación del Índice de Pearson para medir la relación entre el sentimiento de las noticias y el movimiento del precio.

## 2. Propuesta de Valor

La principal ventaja competitiva de esta herramienta no es solo la visualización de datos, sino la generación de 
conocimiento accionable para el usuario:

    - Detección de Volatilidad Contextual: El sistema identifica variaciones de precio superiores al 1% y, de forma inmediata, busca noticias relacionadas en el datamart para explicar el movimiento.
    
    - Análisis de Sentimiento: Mediante un motor de procesamiento de lenguaje natural basado en palabras clave, la plataforma asigna un sentimiento (Positivo, Negativo o Neutro) a las causas de la volatilidad, permitiendo al inversor tomar decisiones informadas rápidamente.

    - Integridad y Resiliencia: Gracias al uso de suscriptores duraderos, garantizamos que no se pierda ni un solo dato de mercado durante caídas del sistema o paradas de mantenimiento, asegurando un histórico continuo y fiable.

    - Arquitectura de Datos Unificada: Combinamos el almacenamiento masivo de eventos históricos con el procesamiento en tiempo real, ofreciendo al usuario una transición fluida entre los datos del pasado y la actividad en vivo del mercado.

    - Versatilidad de Interfaz: El sistema ofrece flexibilidad de visualización mediante una interfaz gráfica detallada para análisis visual y una interfaz de consola (CLI) para una monitorización técnica y ligera.

## 3. Justificación de Tecnologías y Estructura de Datamart

### 3.1. Elección de Fuentes de Datos (APIs y Scraping)

    - CoinGecko API: Se seleccionó por ser una de las fuentes más fiables y completas en el sector cripto. Su campo last_updated permite una sincronización precisa con el mercado, evitando procesar datos obsoletos si la cotización no ha variado.

    - Scraping de Decrypt: Se eligió esta fuente de noticias por su enfoque técnico y la estructura clara de sus artículos (título, cuerpo y fecha de publicación). Esto facilita la extracción de metadatos críticos para el análisis de sentimiento y la vinculación con activos específicos

### 3.2. Estructura del Datamart (SQLite)

    - Persistencia y Relación: A diferencia de una solución en memoria, SQLite permite que los datos sobrevivan a reinicios de la Business Unit y facilita consultas complejas para unir precios y noticias mediante el identificador único de la criptomoneda.

    - Integridad de Datos: Se han definido claves primarias compuestas en la tabla de noticias para evitar la duplicidad de información. El uso de la instrucción INSERT OR IGNORE optimiza el rendimiento del sistema al descartar automáticamente eventos ya procesados, reduciendo el consumo de recursos en disco.

    - Eficiencia en Consultas: La estructura permite recuperar rápidamente los 10 precios más recientes para las alertas de volatilidad, garantizando una respuesta fluida de la interfaz de usuario.

## 4. Instrucciones claras para compilar y ejecutar cada módulo

### 4.1. Requisitos Previos

    - Java 21 o superior instalado.

    - Maven instalado para la gestión de dependencias y construcción de los JARs.

    - Apache ActiveMQ en ejecución (por defecto en tcp://localhost:61616).

    - API Key de CoinGecko: Es necesario disponer de una clave de la API de CoinGecko.

### 4.2. Configuración Inicial

Antes de ejecutar los módulos, es obligatorio crear un archivo llamado **config.properties** en la raíz del proyecto
con el siguiente contenido:

    # ProyectoDACD/config.properties
    api_key=TU_API_KEY_AQUI

Debes definir una ruta en tu sistema de archivos (**datalake**) donde se almacenarán los eventos en formato NDJSON.

    Esta misma ruta debe ser proporcionada como argumento tanto al EventStoreBuilder para la escritura, como a la BusinessUnit para la carga del histórico.

    El sistema creará automáticamente la estructura interna de carpetas (/events/topic/source/) dentro de esta ruta al recibir los primeros mensajes.

### 4.3. Orden de Ejecución

Para asegurar la correcta persistencia y visualización de los datos, se recomienda seguir estrictamente este orden:

    1. Event Store Builder (MainEvent): Debe ser el primero en arrancar para que el Datalake esté listo para guardar los eventos que generen los siguientes módulos.

        Argumento: Ruta absoluta o relativa de la carpeta del datalake.

    2. Business Unit (MainBusinessUnit): Al arrancar, realizará la carga histórica del datalake y activará la interfaz gráfica y la escucha en tiempo real.

        Argumento: Ruta de la carpeta del datalake (debe coincidir con la del MainEvent).

    3. Decrypt Scraper (MainDecrypt): Ejecutarlo a continuación para que capture las noticias más recientes 

    4. CoinGecko Feeder (MainFeeder): Una vez que todo el sistema de persistencia y análisis está activo, iniciamos el feeder para empezar a recibir las cotizaciones en vivo.


    