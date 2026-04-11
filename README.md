# MascotaSocial 🐾

MascotaSocial es una aplicación móvil nativa para Android diseñada para los amantes de las mascotas. Permite a los usuarios compartir fotos, videos y momentos especiales de sus compañeros peludos en una comunidad vibrante y moderna.

## 🚀 Características

- **Inicio (Feed):** Explora las últimas novedades, historias y publicaciones de la comunidad.
- **Galería de Fotos:** Visualiza fotos en una cuadrícula de estilo "staggered" (bento) con filtros por categorías (Perros, Gatos, Aves, etc.).
- **Feed de Videos:** Disfruta de videos verticales a pantalla completa con navegación por gestos (estilo Reels/TikTok).
- **Perfil de Usuario:** Gestiona tu información, estadísticas de publicaciones y visualiza tu propia galería de contenido.
- **Navegación Lateral:** Menú de navegación moderno (Navigation Rail) para un acceso rápido y eficiente a todas las secciones.

## 🛠️ Tecnologías Utilizadas

- **Lenguaje:** [Kotlin](https://kotlinlang.org/)
- **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Diseño:** Material 3 Design System
- **Navegación:** Jetpack Navigation Compose
- **Carga de Imágenes:** [Coil](https://coil-kt.github.io/coil/)
- **Build System:** Gradle 8.7
- **JVM:** 21

## 📋 Requisitos Previos

- **Android Studio:** Jellyfish | 2023.3.1 o superior.
- **JDK:** Versión 21.
- **SDK de Android:** API 34 (Android 14) como Target SDK.

## ⚙️ Configuración e Instalación

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/tu-usuario/MascotaSocial.git
   ```
2. **Abrir en Android Studio:**
   Selecciona la carpeta del proyecto y espera a que Gradle sincronice todas las dependencias.
3. **Configurar Memoria (Opcional):**
   Si experimentas errores de memoria durante la compilación, el archivo `gradle.properties` ya está configurado con:
   ```properties
   org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=1024m
   ```

## 📱 Cómo Ejecutar en tu Dispositivo

### Vía USB
1. Habilita las **Opciones de Desarrollador** en tu teléfono (Toca 7 veces el "Número de compilación").
2. Activa la **Depuración por USB**.
3. Conecta el teléfono a tu PC y acepta el mensaje de confianza.
4. En Android Studio, selecciona tu dispositivo en la barra superior y haz clic en **Run** (▶️).

### Vía Wi-Fi (Android 11+)
1. Asegúrate de que tu PC y tu teléfono estén en la misma red Wi-Fi.
2. En Android Studio, selecciona **"Pair Devices Using Wi-Fi"**.
3. Escanea el código QR desde **Ajustes > Opciones de desarrollador > Depuración inalámbrica**.

## 🎨 Vistas Previas (Previews)

El proyecto incluye funciones de `@Preview` en `MainActivity.kt` para visualizar componentes individuales como `PostCard`, `HomeScreen` y la interfaz principal sin necesidad de ejecutar la app en un dispositivo.

---
Desarrollado con ❤️ para todos los amigos de cuatro patas.
