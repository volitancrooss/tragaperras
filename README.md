# Aplicación de Tragaperras

## Descripción General
La Aplicación de Tragaperras es un juego de tragaperras creativo e interactivo desarrollado en Java. Cuenta con una interfaz gráfica de usuario (GUI) que permite a los jugadores experimentar la emoción de girar los carretes y ganar premios.

## Estructura del Proyecto
```
SlotMachineApp
├── src
│   ├── Main.java
│   ├── ui
│   │   └── SlotMachineUI.java
│   ├── logic
│   │   └── SlotMachineLogic.java
│   └── assets
│       └── images
├── lib
├── build.gradle
└── README.md
```

## Características
- **GUI Interactiva**: La aplicación proporciona una interfaz fácil de usar para que los jugadores interactúen con la máquina tragaperras.
- **Lógica del Juego**: La lógica central para girar los carretes y determinar las ganancias está encapsulada en la clase `SlotMachineLogic`.
- **Recursos de Imágenes**: La aplicación incluye varias imágenes para los símbolos de la máquina tragaperras, mejorando el atractivo visual del juego.

## Comenzando

### Requisitos Previos
- Kit de Desarrollo de Java (JDK) instalado en tu máquina.
- Gradle para construir el proyecto.

### Ejecutar la Aplicación
1. Clone the repository:
   ```
   git clone <repository-url>
   ```
2. Navigate to the project directory:
   ```
   cd SlotMachineApp
   ```
3. Build the project using Gradle:
   ```
   ./gradlew build
   ```
4. Run the application:
   ```
   java -cp build/libs/SlotMachineApp.jar Main
   ```

## Contribuir
¡Las contribuciones son bienvenidas! No dudes en enviar una solicitud de extracción o abrir un problema para cualquier mejora o corrección de errores.

## Licencia
Este proyecto está licenciado bajo la Licencia MIT. Consulta el archivo LICENSE para más detalles.