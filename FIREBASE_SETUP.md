# Configuración de Firebase Realtime Database

## Paso 1: Habilitar Firebase Realtime Database

1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Selecciona tu proyecto: **tarea-3-3-firebase-crud**
3. En el menú lateral izquierdo, busca y haz clic en **"Realtime Database"** (bajo "Build")
4. Haz clic en **"Crear base de datos"** (si no está creada ya)
5. Selecciona ubicación (ej: us-central1)
6. Selecciona modo: **"Comenzar en modo de prueba"** (para desarrollo/pruebas)
7. Haz clic en **"Crear"**

Ahora tu Realtime Database está habilitada. Deberías ver una URL como:
```
https://tarea-3-3-firebase-crud-default-rtdb.firebaseio.com/
```

## Paso 2: Configurar Reglas de Seguridad (IMPORTANTE)

Después de crear la base de datos:

1. En la consola de Firebase, ve a **"Realtime Database"**
2. Haz clic en la pestaña **"Reglas"** (junto a "Datos")
3. Reemplaza el contenido con estas reglas de desarrollo (SOLO PARA DESARROLLO/PRUEBAS):

```json
{
  "rules": {
    "personas": {
      ".read": true,
      ".write": true,
      "$uid": {
        ".read": true,
        ".write": true
      }
    }
  }
}
```

4. Haz clic en **"Publicar"**

⚠️ **ADVERTENCIA**: Estas reglas permiten lectura y escritura sin autenticación. 
Son SOLO para desarrollo/pruebas. En producción, implementa autenticación y reglas más restrictivas.

## Paso 3: Verificar que Funciona

1. En la pestaña **"Datos"** de Realtime Database, deberías ver:
   ```
   (root)
   └── personas (rama vacía inicialmente)
   ```

2. Ahora abre tu app en Android:
   - Abre "Gestionar usuarios"
   - Rellena los datos (Nombres, Apellidos, Correo, Fecha, Foto)
   - Pulsa "Crear"
   - Deberías ver un diálogo confirmando "Registro exitoso" con el ID

3. Luego ve a "Ver Tabla":
   - Deberías ver la persona creada en la lista

4. Verifica en Firebase Console > Realtime Database > Datos:
   - Deberías ver los datos guardados bajo `personas/{id}`

## Troubleshooting

Si no funciona:

1. **Revisa los Logs en Android Studio**:
   - Abre: View > Tool Windows > Logcat (o Alt+6)
   - Busca logs que contengan "FirebaseHelper"
   - Muestra cualquier error de la forma: "Error al crear persona: ..."

2. **Verifica que Firebase esté inicializado**:
   - En Android Studio, abre la terminal
   - Ejecuta: `adb logcat | grep Firebase`
   - Busca mensajes como "Firebase initialized successfully" o errores

3. **Comprueba la conexión a internet**:
   - El dispositivo debe tener conexión WiFi o datos activados

4. **Revisa que google-services.json esté presente**:
   - Debe estar en: `app/google-services.json`

5. **Limpia el build si todo lo anterior está bien**:
   ```powershell
   .\gradlew.bat clean
   .\gradlew.bat assembleDebug
   ```

## Reglas de Seguridad por Ambiente

### Desarrollo (LO QUE ESTAMOS USANDO):
```json
{
  "rules": {
    "personas": {
      ".read": true,
      ".write": true
    }
  }
}
```

### Producción (Con Autenticación):
```json
{
  "rules": {
    "personas": {
      ".read": "auth != null",
      ".write": "auth != null"
    }
  }
}
```

### Producción (Restricción por Usuario):
```json
{
  "rules": {
    "personas": {
      ".read": "auth != null",
      ".write": "auth != null",
      "$persona_id": {
        ".write": "root.child('personas').child($persona_id).child('uid').val() === auth.uid"
      }
    }
  }
}
```

---

Si tras hacer todos estos pasos sigue sin funcionar, comparte los logs de Android Studio (Logcat) 
y te ayudaré a identificar el problema específico.

