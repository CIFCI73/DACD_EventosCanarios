# Eventos Canarios

## SPRINT 1

### Módulo Weather

Este es mi trabajo para el primer sprint del proyecto. Me he encargado de 
desarrollar un sistema que descarga automáticamente los datos del tiempo de 
Las Palmas de Gran Canaria y los guarda de forma segura. El objetivo es crear un 
historial real que podamos usar más adelante.

Para organizar bien el proyecto y que el código sea limpio, he seguido los principios de 
Clean Architecture que vimos en clase. He dividido el trabajo en varios paquetes para que 
cada parte tenga una única responsabilidad:

* **El Modelo (`model`)**: He creado un *Record* en Java llamado `Weather`. Es una caja muy simple que solo guarda los datos importantes: temperatura, humedad, probabilidad de lluvia y la hora exacta de la consulta.
* **La Conexión (`feeder`)**: Esta parte es la que sale a internet. Utiliza la API de 
OpenWeatherMap para descargar la información en formato JSON y luego la transforma en 
nuestro objeto Java usando la librería Gson.
* **El Almacenamiento (`store`)**: Es la memoria del programa. He utilizado SQLite para 
crear una base de datos local (`weather.db`). Está programado para añadir siempre una 
fila nueva de forma incremental, garantizando que no se borre el historial antiguo.
* **El Controlador (`control`)**: Funciona como el "cerebro" del módulo. He configurado 
un `Timer` de Java que automatiza todo el proceso, haciendo que el programa consulte la 
API y guarde los datos cada minuto por sí solo.

**Decisiones de diseño**
Una decisión importante que he tomado es utilizar *Interfaces* para conectar estas partes. 
Esto hace que el código sea muy flexible. Por ejemplo, si en el futuro el profesor nos pide
cambiar a otra API del tiempo, solo tendríamos que escribir un archivo nuevo sin necesidad 
de tocar el controlador ni la base de datos.

**Instrucciones para ejecutarlo**
1. Consigue una API Key gratuita en la web de OpenWeatherMap.
2. Pega esa clave en la variable `apiKey` dentro del archivo `OpenWeatherMapFeeder.java`.
3. Ejecuta la clase `Main.java`. El programa creará la base de datos automáticamente y verás en la consola cómo se van guardando los datos.

### Módulo News



## SPRINT 2

En este segundo sprint del proyecto, hemos cambiado por completo la forma en la que se comunican nuestros módulos. Hemos dejado atrás el guardado directo en bases de datos SQLite y hemos implementado una arquitectura basada en eventos (**Publisher/Subscriber**) utilizando **Apache ActiveMQ**.

### 1. La nueva arquitectura y los Publishers
Nuestros módulos originales (`modulo-weather` y `modulo-news`[cite: 5]) ahora funcionan como "estaciones de radio" (Publishers). En lugar de guardar los datos, los empaquetan y los publican en canales específicos (Topics) del broker de ActiveMQ.

### 2. El Suscriptor (EventBuilderSubscriber)
Para capturar estos eventos, hemos creado un módulo completamente nuevo llamado `modulo-eventstore`[cite: 5].

Mi principal aportación en este módulo ha sido desarrollar la clase `EventBuilderSubscriber`[cite: 5]. Sus características principales son:
* **Conexión al Broker:** Se conecta a ActiveMQ (en `tcp://localhost:61616`) y actúa como el "escuchador" central.
* **Suscripción Duradera:** Se suscribe de forma *duradera* a los topics `Weather` y `Events`. Esto es vital porque si el `Event Store` se apaga temporalmente, al volver a encenderse recuperará automáticamente todos los mensajes que los Feeders publicaron mientras estaba desconectado.
* **Delegación:** Cada vez que recibe un mensaje JSON, se lo pasa a la clase encargada de archivarlo.

### 3. Instrucciones de ejecución
Para probar toda la arquitectura del Sprint 2 al mismo tiempo, sigue este orden:
1. Abre una terminal y arranca el broker de **ActiveMQ** (`activemq start`).
2. En tu IDE, ejecuta primero la clase `Main` del `modulo-eventstore`[cite: 5] (para que empiece a escuchar).
3. A continuación, ejecuta la clase `Main` del `modulo-weather`[cite: 5] y la del `modulo-news`[cite: 5].
4. Verás en las diferentes consolas cómo los Feeders envían eventos y el EventStore los recibe en tiempo real.


## SPRINT 3

En este tercer y último sprint, hemos desarrollado la **Business Unit** (`modulo-business-unit`), el núcleo lógico de nuestra aplicación que procesa y unifica toda la información recopilada.

Para este módulo, hemos implementado una arquitectura de procesamiento dual (similar a una arquitectura Lambda/Kappa):
1. **Carga Histórica (Batch):** Al arrancar, el sistema utiliza la clase `EventStoreReader` para procesar de forma masiva todos los archivos `.events` almacenados en el disco por el EventStore.
2. **Tiempo Real (Streaming):** Simultáneamente, la clase `ActiveMQListener` se suscribe a los topics del broker, inyectando los eventos nuevos al instante.

**El Datamart en Memoria**
Toda esta información converge en la clase `InMemoryDatamart`. Hemos diseñado este componente para que actúe como una base de datos en memoria, normalizando las fechas y evitando eventos duplicados, lo que optimiza enormemente el rendimiento de las consultas.

**La Interfaz de Usuario (CLI)**
Finalmente, hemos creado `RecomendadorCLI`, una interfaz interactiva por consola. Esta vista solicita al usuario una fecha específica, consulta el Datamart y aplica nuestra Lógica de Negocio: cruza los datos meteorológicos con la agenda de eventos para emitir recomendaciones personalizadas (por ejemplo, advertir sobre llevar paraguas si la probabilidad de lluvia es alta y priorizar eventos a cubierto).