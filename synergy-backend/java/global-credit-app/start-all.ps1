$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-21.0.11.10-hotspot"
$env:PATH      = "$env:JAVA_HOME\bin;$env:PATH"

$java  = "$env:JAVA_HOME\bin\java.exe"
$base  = $PSScriptRoot
$db    = "jdbc:postgresql://localhost:5432/gcp"
$dbArg = @(
    "--spring.datasource.url=$db",
    "--spring.datasource.username=gcp",
    "--spring.datasource.password=gcp"
)

New-Item -ItemType Directory -Force -Path "$base\logs" | Out-Null

function Start-Svc($name, $port, $jar, $extraArgs = @()) {
    Write-Host "Starting $name  (port $port)..." -ForegroundColor Cyan
    Start-Process -FilePath $java `
        -ArgumentList (@("-jar", $jar) + $extraArgs) `
        -RedirectStandardOutput "$base\logs\$name.log" `
        -RedirectStandardError  "$base\logs\$name-err.log" `
        -NoNewWindow
}

Start-Svc "audit-service"    8091 "$base\audit-service\target\audit-service-0.0.1-SNAPSHOT.jar"    $dbArg
Start-Sleep 5
Start-Svc "auth-service"     8088 "$base\auth-service\target\auth-service-0.0.1-SNAPSHOT.jar"     $dbArg
Start-Svc "borrower-service" 8089 "$base\borrower-service\target\borrower-service-0.0.1-SNAPSHOT.jar" $dbArg
Start-Svc "passport-service" 8087 "$base\passport-service\target\passport-service-0.0.1-SNAPSHOT.jar" $dbArg
Start-Svc "lender-service"   8090 "$base\lender-service\target\lender-service-0.0.1-SNAPSHOT.jar"   $dbArg
Start-Sleep 10
Start-Svc "api-gateway"      8080 "$base\api-gateway\target\api-gateway-0.0.1-SNAPSHOT.jar"

Write-Host ""
Write-Host "All services launching. Logs are in .\logs\" -ForegroundColor Green
Write-Host "Wait ~30s then open: http://localhost:8080/swagger-ui.html" -ForegroundColor Yellow
