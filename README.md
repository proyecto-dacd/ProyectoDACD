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

    - Correlación de Pearson (Sentimiento vs. Precio): A diferencia de los monitores convencionales, el sistema implementa el Índice de Pearson (r) en tiempo real. Esto permite al usuario cuantificar matemáticamente la fuerza de la relación entre el sentimiento de las noticias y la fluctuación del mercado, identificando de forma objetiva anomalías o divergencias de precio.

    - Análisis de Sentimiento Automatizado: Mediante un motor de procesamiento de lenguaje natural basado en heurística de palabras clave, la plataforma categoriza el impacto emocional del flujo de noticias. Esto permite filtrar el "ruido" del mercado y centrarse en eventos con una carga sentimental alta (alcista o bajista).

    - Resiliencia y Sincronización: El sistema garantiza la integridad absoluta del historial de mercado. Gracias al uso de suscriptores duraderos en el middleware (ActiveMQ), la plataforma es capaz de realizar una lógica de catch-up automático. Si el sistema se desconecta, al reiniciarse procesa instantáneamente todos los eventos acumulados, eliminando cualquier brecha de información en el Datamart.

    - Implementación de Arquitectura Lambda: El diseño combina un flujo de procesamiento por lotes (Batch) para el análisis de datos históricos masivos con un flujo de velocidad (Speed Layer) para la respuesta inmediata. El resultado es una transición fluida donde el usuario puede analizar tendencias pasadas y movimientos en vivo bajo un mismo modelo de datos unificado.

    - Alertas de Volatilidad Contextualizadas: El sistema no solo avisa cuando el precio cambia, ofrece el porqué. Al detectar variaciones superiores al umbral configurado, la plataforma vincula automáticamente el movimiento con la última noticia relevante y su puntuación de sentimiento, reduciendo drásticamente el tiempo de reacción del analista.

    - Visualización Analítica de Alta Densidad (Dashboard Real-Time): El sistema traslada la complejidad del procesamiento de datos a una interfaz web intuitiva y dinámica. Mediante el uso de WebSockets y actualizaciones reactivas, el Dashboard ofrece una supervisión manos libres donde widgets especializados (velocímetros de Pearson, indicadores de sentimiento y gráficos de tendencia) se sincronizan al milisegundo con los eventos del mercado. Esta capacidad permite al usuario detectar patrones visuales de forma inmediata sin necesidad de interactuar manualmente con la plataforma.

## 3. Justificación de Tecnologías y Estructura de Datamart

### 3.1. Elección de Fuentes de Datos (APIs y Scraping)

    - CoinGecko API: Se seleccionó por ser una de las fuentes más fiables y completas en el sector cripto. Su campo last_updated permite una sincronización precisa con el mercado, evitando procesar datos obsoletos si la cotización no ha variado.

    - Scraping de Decrypt: Se eligió esta fuente de noticias por su enfoque técnico y la estructura clara de sus artículos (título, cuerpo y fecha de publicación). Esto facilita la extracción de metadatos críticos para el análisis de sentimiento y la vinculación con activos específicos

### 3.2. Estructura del Datamart (SQLite)

El Datamart ha sido diseñado siguiendo principios de normalización para garantizar la consistencia entre el flujo de precios y el de noticias, permitiendo una analítica cruzada eficiente.

#### Modelado de Datos

    - Tabla de Precios (prices): Almacena el histórico de cotizaciones, incluyendo el timestamp preciso y la métrica de sentimiento calculada en el momento de la ingesta. Esto permite reconstruir la evolución del mercado con contexto emocional.

    - Tabla de Noticias (news): Almacena el contenido informativo. Se utiliza una Clave Primaria Compuesta para asegurar la unicidad, evitando que una misma noticia procesada por diferentes fuentes duplique el ruido en el análisis.

#### Estrategias de Optimización e Integridad

    - Persistencia Robusta: El uso de SQLite garantiza que el estado de la Business Unit sea persistente. Tras un reinicio, el sistema puede recuperar instantáneamente los últimos estados para reanudar el cálculo del Índice de Pearson sin tiempos de espera.

    - Resolución de Conflictos (INSERT OR IGNORE): Se implementa una lógica de inserción no bloqueante. El sistema descarta automáticamente registros duplicados a nivel de base de datos, lo que optimiza el ciclo de escritura y garantiza que el Datalake y el Datamart mantengan una sincronía limpia.

    - Eficiencia en Consultas de Ventana: La estructura de índices permite realizar consultas de ventana deslizante con latencia mínima. Esto es crítico para que el Dashboard recalcule la volatilidad y los promedios móviles en tiempo real cada vez que el broadcastUpdate() se activa.

#### Relación Semántica

    - El diseño permite realizar vínculos temporales: el sistema busca noticias cuya publicación coincida con ventanas de volatilidad detectadas. Esto permite la contextualización automática en la interfaz web, explicando al usuario el "porqué" de un movimiento de mercado mediante el cruce de datos de ambas tablas.

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


    
