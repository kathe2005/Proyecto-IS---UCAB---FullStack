Proyecto-IS - Estructura de proyecto

Estructura recomendada:

- `backend/` - Spring Boot application (módulo `proyect`)
- `frontend/` - Angular application
- `data/` - Archivos JSON persistentes (`clientes.json`, `puestos.json`, `reservas.json`)
- `logs/` - Archivos de log generados por la aplicación
- `scripts/` - Scripts utilitarios para desarrollo
- `docs/` - Documentación del proyecto

Cambios aplicados:
- Centralizados los archivos JSON en `data/`.
- Código backend actualizado para leer/escribir desde `data/`.
- Tests actualizados para usar `data/clientes.json`.
- Se agregó `scripts/start-dev.ps1` para levantar ambiente de desarrollo.

Siguientes pasos (local):

En PowerShell, desde la raíz del repo:

```powershell
# Iniciar backend (desde backend/proyect)
cd backend\proyect; .\mvnw spring-boot:run

# Iniciar frontend (otra terminal)
cd frontend; npm install; npm start
```

Si quieres conservar el historial de git al mover archivos, usa `git mv` localmente antes de commitear.
