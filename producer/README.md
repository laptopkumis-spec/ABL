# Producer Service + RabbitMQ (Native Windows)

Service **producer-service** (port **8083**) mengirim pesan ke **RabbitMQ** yang
terpasang secara native di Windows. Service ini juga terdaftar di **Eureka** dan
bisa diakses lewat **API Gateway**.

---

## Status Instalasi (sudah terpasang di mesin ini)

| Komponen | Versi | Lokasi |
|---|---|---|
| Erlang/OTP | **27.3.4.18** | `C:\Program Files\Erlang OTP` |
| RabbitMQ Server | **4.3.6** | `C:\Program Files\RabbitMQ Server\rabbitmq_server-4.3.6` |
| RabbitMQ Service | Running (Automatic) | Windows Service `RabbitMQ` |

> Erlang **27.x** dipilih karena RabbitMQ 4.x hanya mendukung Erlang 27.x.
> Erlang 28/29 **tidak didukung** dan akan membuat node gagal start.

---

## 1. Menjalankan RabbitMQ

RabbitMQ terpasang sebagai **Windows Service** (`RabbitMQ`) dan otomatis berjalan
saat Windows menyala. Kelola lewat **Command Prompt / PowerShell sebagai Administrator**:

```powershell
Get-Service RabbitMQ          # cek status
Start-Service RabbitMQ        # jalankan
Stop-Service  RabbitMQ        # hentikan
Restart-Service RabbitMQ      # restart (perlu restart saat ubah plugin)
```

Atau lewat `sbin` (Administrator), agar tampil di layar/foreground:
```cmd
cd "C:\Program Files\RabbitMQ Server\rabbitmq_server-4.3.6\sbin"
rabbitmq-server.bat
```

### Environment variable yang diperlukan (sudah di-set)

Agar CLI (`rabbitmqctl`, `rabbitmq-plugins`) berfungsi:
```
ERLANG_HOME = C:\Program Files\Erlang OTP
PATH        = ...; C:\Program Files\Erlang OTP\bin
```

---

## 2. Management UI (browser)

Aktifkan plugin sekali saja (Administrator):
```cmd
cd "C:\Program Files\RabbitMQ Server\rabbitmq_server-4.3.6\sbin"
rabbitmq-plugins enable rabbitmq_management
```
Lalu restart service. Buka: **http://localhost:15672**
- Username: `guest`
- Password: `guest`

---

## 3. Port yang Dipakai

| Port | Fungsi |
|---|---|
| `5672` | AMQP (dipakai Spring Boot / producer-service) |
| `15672` | Management UI (browser) |
| `4369` | epmd (Erlang Port Mapper) |

---

## 4. Jalankan Eureka, Gateway, dan Producer

Urutan: **RabbitMQ → Eureka → Gateway → Producer**.

```powershell
java -jar eureka\target\eureka-0.0.1-SNAPSHOT.jar
java -jar gateway\target\gateway-0.0.1-SNAPSHOT.jar
java -jar producer\target\producer-0.0.1-SNAPSHOT.jar
```

Build producer (jika belum):
```powershell
cd producer
.\mvnw.cmd -DskipTests clean package
```

---

## 5. Test Kirim Pesan

### Lewat Gateway (port 9001)

**POST** `http://localhost:9001/api/pesan`
Body (JSON):
```json
{
  "pengirim": "admin",
  "isi": "Halo RabbitMQ dari producer-service"
}
```

Respons:
```json
{
  "status": "terkirim",
  "exchange": "pesan.exchange",
  "routingKey": "pesan.routing",
  "queue": "pesan.queue",
  "waktu": "2026-10-01T14:55:59",
  "pesan": { "pengirim": "admin", "isi": "Halo RabbitMQ dari producer-service" }
}
```

### Lewat Producer langsung (port 8083)

**POST** `http://localhost:8083/api/pesan` — body sama seperti di atas.

**GET** `http://localhost:8083/api/pesan` — info konfigurasi queue/exchange.

---

## 6. Verifikasi Pesan Masuk ke RabbitMQ

1. **Log producer** — listener internal mencatat pesan yang diterima:
   ```
   >> Pesan diterima dari RabbitMQ [pesan.queue]: PesanDto{pengirim='admin', isi='...'}
   ```

2. **RabbitMQ Management UI** → tab **Queues** → `pesan.queue` → lihat grafik
   *Message rates* (ada aktivitas publish/deliver).

---

## 7. Konfigurasi yang Dipakai

| Item | Nilai |
|---|---|
| Exchange | `pesan.exchange` (Topic) |
| Queue | `pesan.queue` (durable) |
| Routing key | `pesan.routing` |
| Host RabbitMQ | `localhost:5672` |
| User/Pass | `guest` / `guest` |
| Port producer-service | `8083` |
| Nama service di Eureka | `producer-service` |
| Route gateway | `/api/pesan/**` → `lb://producer-service` |

Konfigurasi RabbitMQ di `producer/src/main/resources/application.properties`:
```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

---

## 8. Struktur Modul Producer

```
producer/
├── pom.xml                          # dependency spring-boot-starter-amqp + eureka-client
├── src/main/java/com/aufi/producer/
│   ├── ProducerApplication.java     # @SpringBootApplication + @EnableDiscoveryClient
│   ├── config/RabbitMQConfig.java   # definisi exchange, queue, binding, converter JSON
│   ├── controller/ProducerController.java  # endpoint POST/GET /api/pesan
│   ├── dto/PesanDto.java            # model pesan
│   ├── service/ProducerService.java # kirim pesan ke RabbitMQ
│   └── listener/PesanListener.java  # (opsional) terima pesan untuk verifikasi
└── src/main/resources/application.properties
```
