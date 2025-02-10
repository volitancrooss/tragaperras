Tragaperras/Jackpot


◆ Descripción General
La Aplicación de Tragaperras es un juego de temática recreativa y lúdica. Nuestro tragaperras está inspirado en los comunes Jackpot y en las clásicas tragamonedas de los bares.
Si nos adentramamos más en detalle y miramos las características del juego veremos como juenta con una estética creativa e interactiva. Desarrollado en Java y con una interfaz gráfica de usuario (GUI) que permite a los jugadores experimentar la emoción y tensión de girar los carretes y ganar premios.


◆ Estructura del Proyecto
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


◆ ✨ Características
   - 🎨 →  GUI Interactiva: La aplicación proporciona una interfaz fácil de usar para que los jugadores interactúen con la máquina tragaperras.
   - 🎮 →  Intuitiva: Su manejo y jugabilidad será de carácter fácil e intuitiva para que los jugadores no requieran de conocimientos o formación previa.
   - 🧠 →  Lógica del Juego: La lógica central para girar los carretes y determinar las ganancias está encapsulada en la clase `SlotMachineLogic`.
   - 🖼️ →  Recursos de Imágenes: La aplicación incluye varias imágenes para los símbolos de la máquina tragaperras, mejorando el atractivo visual del juego y hacerlo así más adictivo y atractivo al público.
     

◆ 📌 Inicio

   ◉ 🚀 Comenzando el juego
   
   |   ● 📋 Requisitos Previos
         - Kit de Desarrollo de Java (JDK) instalado en tu máquina.
         - Gradle para construir el proyecto.
      
   |   ○ 🏃 Ejecutar la Aplicación
         1. Abre la terminal.
         2. Clona el repositorio:
            ```
            git clone <repository-url>
            ```
         3. Navega al directorio del proyecto:
            ```
            cd tragaperras
            ```
         3. Construye el proyecto:
            ```
            javac -d bin src/logic/SlotMachineLogic.java src/ui/SlotMachineUI.java src/Main.java
            ```
         4. Ejecuta la aplicación:
            ```
            java -cp bin SlotMachineApp.Main
            ```

   |   ● 🤝 Contribuir
         ¡Las contribuciones son bienvenidas! No dudes en enviar una solicitud de extracción o abrir un problema para cualquier mejora o corrección de errores.

   |   ● 📄 Licencia
         Este proyecto está licenciado bajo la Licencia MIT. Consulta el archivo LICENSE para más detalles →  https://es.wikipedia.org/wiki/Licencia_MIT
