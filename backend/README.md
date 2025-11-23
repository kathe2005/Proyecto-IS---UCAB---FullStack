Backend (Spring Boot)

Desde `backend/proyect` puedes ejecutar la aplicación con Maven Wrapper:

```powershell
cd backend\proyect
.\mvnw spring-boot:run
```

Tests:

```powershell
cd backend\proyect
.\mvnw test
```

Notas:
- Los archivos JSON de persistencia ahora están en `../../data/` desde el módulo `backend/proyect`.
- Verifica permisos de escritura en la carpeta `data/` si la aplicación no puede persistir.
