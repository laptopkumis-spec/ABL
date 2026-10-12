# stop-all.ps1 - Hentikan seluruh service yang dijalankan oleh start-all.ps1

$root = $PSScriptRoot
foreach ($svc in @("gateway","consumer","producer","order","pelanggan","produk","eureka")) {
    $pidFile = "$root\logs\$svc.pid"
    if (Test-Path $pidFile) {
        $procId = Get-Content $pidFile -ErrorAction SilentlyContinue
        if ($procId -and (Get-Process -Id $procId -ErrorAction SilentlyContinue)) {
            Stop-Process -Id $procId -Force -ErrorAction SilentlyContinue
            Write-Host "STOP $svc (PID $procId)" -ForegroundColor Yellow
        } else {
            Write-Host "SKIP $svc (tidak berjalan)" -ForegroundColor DarkGray
        }
    }
}
Write-Host "`nSelesai." -ForegroundColor Cyan
