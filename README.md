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

---
