
$basePath = "c:\Users\LENOVO\source\repos\L3GL_C#_2026\SenSoutenance\SenSoutenances\packages"

# List of DLLs to load in order
$dlls = @(
    "Google.Protobuf.3.32.0\lib\net45\Google.Protobuf.dll",
    "ZstdSharp.Port.0.8.6\lib\net462\ZstdSharp.dll",
    "K4os.Compression.LZ4.1.3.8\lib\net462\K4os.Compression.LZ4.dll",
    "System.Runtime.CompilerServices.Unsafe.6.0.0\lib\net461\System.Runtime.CompilerServices.Unsafe.dll",
    "System.Buffers.4.5.1\lib\net461\System.Buffers.dll",
    "System.Memory.4.5.5\lib\net461\System.Memory.dll",
    "MySql.Data.9.5.0\lib\net48\MySql.Data.dll"
)

foreach ($dll in $dlls) {
    $path = Join-Path $basePath $dll
    if (Test-Path $path) {
        try {
            [System.Reflection.Assembly]::LoadFrom($path) | Out-Null
        } catch {
            Write-Host "Warning: Could not load $dll"
        }
    } else {
        Write-Host "Warning: Path not found $path"
    }
}

try {
    Write-Host "--- Starting connection test ---"
    $connString = "server=127.0.0.1;port=3306;user id=root;password=P@sser123"
    $conn = New-Object MySql.Data.MySqlClient.MySqlConnection($connString)
    $conn.Open()
    Write-Host "SUCCESS: Connection to MySQL server (127.0.0.1) works!" -ForegroundColor Green
    
    $dbName = "Bdsensoutenances"
    $cmd = $conn.CreateCommand()
    $cmd.CommandText = "SHOW DATABASES"
    $reader = $cmd.ExecuteReader()
    $found = $false
    Write-Host "Available databases:"
    while ($reader.Read()) {
        $name = $reader[0].ToString()
        Write-Host " - $name"
        if ($name -ieq $dbName) { $found = $true }
    }
    $reader.Close()
    
    if ($found) {
        Write-Host "SUCCESS: Database '$dbName' found!" -ForegroundColor Green
    } else {
        Write-Host "ERROR: Database '$dbName' NOT found! Check the name carefully." -ForegroundColor Red
    }
    $conn.Close()
} catch {
    Write-Host "ERROR: Connection failed!" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Yellow
    if ($_.Exception.InnerException) {
        Write-Host ("Inner Error: " + $_.Exception.InnerException.Message) -ForegroundColor Yellow
    }
}
