# Introducción a Red Hat OpenShift para CI y Desarrollo

## Objetivo
Entender los conceptos clave de Red Hat OpenShift para usarlo como plataforma de CI/CD y soporte al desarrollo de aplicaciones Java Spring Boot y PHP Symfony.

---

## ¿Qué es Red Hat OpenShift?
- **Plataforma de contenedores** basada en Kubernetes.
- Simplifica el despliegue, gestión y escalado de aplicaciones.
- Ideal para CI/CD, desarrollo y producción.
- Soporta múltiples lenguajes y frameworks (Java Spring Boot, PHP Symfony, etc.).

---

## Conceptos Generales

### 1. **Componentes Principales**
- **Pod**: Unidad básica que ejecuta contenedores.
- **Deployment**: Gestiona la actualización y escalado de Pods.
- **Service**: Expone Pods internamente para comunicación.
- **Route**: Expone servicios al exterior (similar a un dominio público).
- **BuildConfig**: Define cómo construir imágenes de contenedores.
- **ImageStream**: Gestiona versiones de imágenes.

### 2. **Topología en OpenShift**
- Usa la vista de **Topología** en la consola web para visualizar:
  - Pods, Deployments, Services y Routes.
  - Relaciones entre componentes.
- Ejemplo: Un proyecto Spring Boot tendrá un Deployment, un Service para comunicación interna y una Route para acceso externo.

---

## Variables de Entorno

- **Qué son**: Configuraciones clave-valor que las aplicaciones consumen en tiempo de ejecución.
- **Uso en OpenShift**:
  - Definidas en el **Deployment** o **Pod**.
  - Ejemplo para Spring Boot:
    - `SPRING_DATASOURCE_URL`: URL de la base de datos.
    - `SPRING_PROFILES_ACTIVE`: Perfil activo (dev, prod).
  - Ejemplo para Symfony:
    - `DATABASE_URL`: Conexión a la base de datos.
    - `APP_ENV`: Entorno (dev, prod).
- **Cómo configurar**:
  - En la consola: Deployment → Environment.
  - En CLI: `oc set env deployment/<nombre> KEY=VALUE`.
- **Buenas prácticas**:
  - Usar **ConfigMaps** para configuraciones comunes.
  - Usar **Secrets** para datos sensibles (contraseñas, claves API).

---

## Creación del Dockerfile

- **Consideraciones generales**:
  - Usar imágenes base oficiales y ligeras:
    - Para **Spring Boot**: `openjdk:17-jdk-slim` o `eclipse-temurin:17-jre`.
    - Para **Symfony**: `php:8.1-fpm` o `php:8.2-fpm`.
  - Minimizar capas para optimizar el tamaño de la imagen.
  - Exponer el puerto correcto (8080 para Spring Boot, 9000 para PHP-FPM).
  - Configurar un usuario no root para seguridad.

- **Ejemplo de Dockerfile para Spring Boot**:
```dockerfile
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

- **Ejemplo de Dockerfile para Symfony**:
```dockerfile
FROM php:8.1-fpm
WORKDIR /var/www
COPY . .
RUN apt-get update && apt-get install -y \
    libpq-dev \
    && docker-php-ext-install pdo pdo_pgsql
EXPOSE 9000
CMD ["php-fpm"]
```

- **Consideraciones**:
  - Incluir solo dependencias necesarias.
  - Usar `.dockerignore` para excluir archivos innecesarios (ej. `target/`, `vendor/`).
  - Probar la imagen localmente antes de subir a OpenShift.

---

## BuildConfig

- **Qué es**: Define cómo OpenShift construye una imagen a partir del código fuente.
- **Configuración**:
  - **Source**: Repositorio Git (URL y rama).
  - **Strategy**: Tipo de construcción (Docker, Source-to-Image).
  - **Output**: ImageStream donde se guarda la imagen resultante.
- **Ejemplo para Spring Boot**:
```yaml
apiVersion: build.openshift.io/v1
kind: BuildConfig
metadata:
  name: spring-boot-app
spec:
  source:
    git:
      uri: https://github.com/equipo/spring-boot-app.git
      ref: main
  strategy:
    dockerStrategy:
      dockerfilePath: Dockerfile
  output:
    to:
      kind: ImageStreamTag
      name: spring-boot-app:latest
```
- **Ejemplo para Symfony**:
  - Similar, pero apuntando al repositorio y Dockerfile de Symfony.
- **Buenas prácticas**:
  - Usar **webhooks** para disparar builds automáticamente al hacer push a Git.
  - Especificar la rama correcta (ej. `main`, `develop`).

---

## Tags de Git

- **Uso en CI**:
  - Usar tags para versiones estables (ej. `v1.0.0`).
  - Usar ramas para desarrollo (ej. `develop`, `feature/xyz`).
- **Estrategia**:
  - Configurar el BuildConfig para escuchar una rama o tag específico.
  - Ejemplo: `ref: v1.0.0` en el BuildConfig para construir una versión específica.
- **Recomendación**:
  - Automatizar con pipelines (Jenkins, Tekton) para crear tags al aprobar releases.

---

## Visualización de Logs

- **Cómo acceder**:
  - **Consola web**: Pods → Logs.
  - **CLI**: `oc logs pod/<nombre-pod>`.
- **Buenas prácticas**:
  - Configurar logging en la aplicación (Spring Boot: Logback, Symfony: Monolog).
  - Usar `oc logs --tail=100` para ver las últimas líneas.
  - Habilitar logs centralizados (ej. EFK stack en OpenShift).

---

## Acceso a la Terminal

- **Cómo entrar**:
  - **Consola web**: Pods → Terminal.
  - **CLI**: `oc rsh pod/<nombre-pod>`.
- **Usos**:
  - Depurar problemas en la aplicación.
  - Ejecutar comandos (ej. `php artisan` para Symfony, `java -version` para Spring Boot).
- **Consideraciones**:
  - Solo disponible si el contenedor tiene un shell (ej. `/bin/sh`).
  - Evitar cambios manuales en producción.

---

## NodePort

- **Qué es**: Un tipo de Service que expone la aplicación en un puerto del nodo.
- **Uso en desarrollo**:
  - Útil para pruebas internas, pero menos común que Routes para acceso externo.
- **Configuración**:
```yaml
apiVersion: v1
kind: Service
metadata:
  name: spring-boot-service
spec:
  type: NodePort
  ports:
  - port: 8080
    targetPort: 8080
    nodePort: 30001
  selector:
    app: spring-boot-app
```
- **Consideraciones**:
  - Los puertos NodePort están en el rango 30000-32767.
  - Usar Routes para acceso público en lugar de NodePort en producción.

---

## Lo Mínimo para CI y Desarrollo

### **Flujo Básico de CI**:
1. Crear un repositorio Git con el código (Spring Boot o Symfony).
2. Escribir un Dockerfile optimizado.
3. Crear un BuildConfig para construir la imagen desde Git.
4. Configurar un Deployment para ejecutar la aplicación.
5. Exponer la aplicación con un Service y una Route.
6. Configurar variables de entorno (ConfigMaps/Secrets).
7. Usar webhooks para automatizar builds al hacer push.

### **Soporte al Desarrollo**:
- Usar OpenShift para entornos efímeros (dev, staging).
- Acceder a logs y terminal para depuración.
- Configurar pipelines (Tekton, Jenkins) para automatizar pruebas y despliegues.
- Usar ImageStreams para gestionar versiones específicas.

---

## Buenas Prácticas
- **Automatización**: Usar pipelines para CI/CD.
- **Seguridad**: Usar Secrets para datos sensibles, no root en contenedores.
- **Monitoreo**: Habilitar métricas y alertas en OpenShift.
- **Documentación**: Mantener README con instrucciones para desarrolladores.

---

## Recursos Adicionales
- Documentación oficial: [OpenShift Documentation](https://docs.openshift.com)
- Tutoriales: [OpenShift Learning Portal](https://learn.openshift.com)
- CLI: Descargar `oc` desde la consola de OpenShift.
