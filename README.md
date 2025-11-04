# ♻️ TOTT – Basurero Inteligente

**TOTT (Take Out The Trash)** es una aplicación móvil desarrollada en **Android Studio** como parte del curso **Aplicaciones Móviles para IoT (TI3042)**.
Su propósito es facilitar la **gestión y organización de los recordatorios para sacar la basura**, permitiendo asignar tareas a diferentes usuarios dentro de un grupo familiar, utilizando un sistema de colores y notificaciones inteligentes.

---

## 👥 Integrantes

* **Valeska Aguirre**
* **Nicolás Espejo**

**Carrera:** Ingeniería en Informática
**Profesor:** Pablo Eliseo Hernández
**Institución:** INACAP
**Fecha:** 04/11/2025

---

## 🚀 Características principales

* 🔐 **Login y registro de usuarios**
* 🔁 **Recuperación de contraseña**
* 🗓️ **Calendario interactivo** con colores asignados a cada usuario
* 🔔 **Recordatorios automáticos** de días y horarios para sacar la basura
* 👨‍👩‍👧‍👦 **Panel principal colaborativo**, donde se visualizan las tareas del grupo familiar
* 🎨 **Diseño intuitivo y moderno** implementado con **Jetpack Compose**
* ☁️ **Integración con servicios IoT (simulados)** para conexión con un basurero inteligente

---

## 🧩 Requisitos

Asegúrate de contar con lo siguiente antes de ejecutar la aplicación:

* **Android Studio Giraffe o superior**
* **JDK 17+**
* **Gradle actualizado**
* **Dispositivo o emulador Android 8.0 (API 26)** o superior

---

## ⚙️ Instalación y ejecución

1. **Clona el repositorio:**

   ```bash
   git clone https://github.com/tuusuario/TOTT.git
   ```
2. **Abre el proyecto en Android Studio**
   Selecciona *“Open an Existing Project”* y elige la carpeta clonada.
3. **Sincroniza dependencias:**

   ```
   File > Sync Project with Gradle Files
   ```
4. **Ejecuta la app** en un emulador o dispositivo físico.

---

## 🧱 Estructura del proyecto

```
app/
 ├── java/com.example.tott/
 │   ├── ui/                # Interfaces y pantallas principales
 │   ├── data/              # Modelos, usuarios y recordatorios
 │   ├── viewmodel/         # Lógica de negocio y gestión de estado
 │   └── MainActivity.kt    # Punto de entrada de la app
 ├── res/                   # Layouts, íconos y recursos gráficos
 └── AndroidManifest.xml
```

---


---

## 🧑‍💻 Ayuda para contribuidores
1. Haz un *fork* del proyecto
2. Crea una nueva rama:

   ```bash
   git checkout -b feature/nueva-funcionalidad
   ```
3. Realiza tus cambios y haz un commit
4. Envía un *pull request* con una breve descripción

---

## 🪪 Licencia

Este proyecto fue desarrollado con fines **académicos y educativos** en INACAP.
Su código puede ser reutilizado con fines de aprendizaje citando a los autores.

---

### ✨ Extra

Si te gustó el proyecto, deja una ⭐ en el repositorio.
¡Gracias por apoyar nuestro trabajo!
