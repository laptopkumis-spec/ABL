# start-all.ps1 - Jalankan seluruh service Spring Boot
# Pemakaian:  .\start-all.ps1
# Hentikan:   .\stop-all.ps1
# Pastikan RabbitMQ service sudah jalan (Get-Service RabbitMQ)

$root = $PSScriptRoot
$java = Join-Path $env:JAVA_HOME "bin\java.exe"

if (-not (Test-Path $java)) {
    Write-Host "JAVA_HOME tidak valid: $env:JAVA_HOME" -ForegroundColor Red
    exit 1
}

New-Item -ItemType Directory -Force -Path "$root\logs" | Out-Null

# Cek RabbitMQ
$rmq = Get-Service RabbitMQ -ErrorAction SilentlyContinue
if ($rmq) {
    Write-Host "RabbitMQ: $($rmq.Status)" -ForegroundColor Cyan
} else {
    Write-Host "RabbitMQ service tidak ditemukan!" -ForegroundColor Yellow
}

# Urutan start: eureka dulu, lalu service, terakhir gateway
$services = @(
    @{ name = "eureka";   port = 8761; wait = 25 },
    @{ name = "produk";   port = 8080; wait = 3 },
    @{ name = "pelanggan";port = 8081; wait = 3 },
    @{ name = "order";    port = 8082; wait = 3 },
    @{ name = "producer"; port = 8083; wait = 3 },
    @{ name = "consumer"; port = 8084; wait = 3 },
    @{ name = "gateway";  port = 9001; wait = 3 }
)

foreach ($svc in $services) {
    $jar = Join-Path $root "$($svc.name)\target\$($svc.name)-0.0.1-SNAPSHOT.jar"
    if (-not (Test-Path $jar)) {
        Write-Host "SKIP $($svc.name): jar tidak ditemukan. Build dulu." -ForegroundColor Yellow
        continue
    }
    $p = Start-Process -FilePath $java -ArgumentList "-jar", "`"$jar`"" `
        -RedirectStandardOutput "$root\logs\$($svc.name).out.log" `
        -RedirectStandardError  "$root\logs\$($svc.name).err.log" `
        -PassThru -WindowStyle Hidden
    $p.Id | Out-File "$root\logs\$($svc.name).pid"
    Write-Host "START $($svc.name) (port $($svc.port)) PID=$($p.Id)" -ForegroundColor Green
    Start-Sleep -Seconds $svc.wait
}

Write-Host "`nSemua service dijalankan. Eureka: http://localhost:8761" -ForegroundColor Cyan
