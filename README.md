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