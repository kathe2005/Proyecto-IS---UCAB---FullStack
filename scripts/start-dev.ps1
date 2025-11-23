# Script de ayuda para iniciar backend + frontend (PowerShell)

Write-Host "Iniciando backend (módulo Spring Boot) en background..."
Push-Location "backend\proyect"
Start-Process -NoNewWindow -FilePath "cmd.exe" -ArgumentList "/c mvnw spring-boot:run"
Pop-Location

Write-Host "Iniciando frontend (Angular) en una nueva terminal..."
Push-Location "frontend"
Start-Process -NoNewWindow -FilePath "cmd.exe" -ArgumentList "/c npm install && npm start"
Pop-Location

Write-Host "Frontend y backend iniciados (verifica las consolas)."