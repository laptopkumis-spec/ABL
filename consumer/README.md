# Consumer Service (RabbitMQ)

Service **consumer-service** (port **8084**) menerima pesan dari **RabbitMQ** pada
queue `pesan.queue`. Service ini terdaftar di **Eureka** dan bisa diakses lewat
**API Gateway**.

---

## Alur Sistem

```
POST/GET /send  ──►  producer-service  ──►  RabbitMQ  ──►  consumer-service
   (client)            (kirim pesan)      (pesan.queue)     (terima pesan)
```

- **producer-service** (8083) → **mengirim** pesan ke queue `pesan.queue`
- **consumer-service** (8084) → **menerima** pesan dari queue yang sama

> ⚠️ **Penting:** Listener di `producer-service` sudah **dinonaktifkan**.
> Jika producer dan consumer sama-sama mendengarkan queue yang sama, keduanya
> akan **berebut pesan** (competing consumers) sehingga sebagian pesan tidak
> sampai ke consumer.

---

## 1. Prasyarat

- RabbitMQ berjalan (service `RabbitMQ` di Windows) → port `5672` & `15672`
- Eureka (8761) berjalan
- Gateway (9001) berjalan
- JDK 21

---

## 2. Build & Jalankan

```powershell
cd consumer
.\mvnw.cmd -DskipTests clean package
java -jar target\consumer-0.0.1-SNAPSHOT.jar
```

Urutan start: **RabbitMQ → Eureka → Gateway → Producer → Consumer**.

---

## 3. Test End-to-End

### Langkah 1 — Kirim pesan dari producer
```powershell
# Lewat producer langsung
curl "http://localhost:8083/send?pesan=Halo%20Consumer"
```

### Langkah 2 — Lihat pesan yang diterima consumer
```powershell
curl "http://localhost:8084/api/consumer/pesan"
```

Contoh hasil:
```json
[
  { "isi": "Halo Consumer", "waktu": "2026-10-01T15:31:28.723802" }
]
```

---

## 4. Endpoint Consumer

| Method | URL | Fungsi |
|---|---|---|
| GET | `/api/consumer` | Info service & jumlah pesan diterima |
| GET | `/api/consumer/pesan` | Lihat semua pesan yang diterima |
| GET | `/api/consumer/pesan/count` | Jumlah pesan diterima |
| DELETE | `/api/consumer/pesan` | Kosongkan daftar pesan (di memori) |

Lewat gateway (port 9001): ganti `8084` menjadi `9001`.

---

## 5. Verifikasi Pesan Masuk

### Lewat log consumer
```
>> [CONSUMER] Pesan diterima dari RabbitMQ [pesan.queue]: Halo Consumer
```

### Lewat RabbitMQ Management UI
Buka `http://localhost:15672` (guest/guest) → tab **Queues** → `pesan.queue`.
Karena consumer aktif, `messages` akan bernilai **0** (pesan langsung diproses),
dan **Consumers: 1**.

---

## 6. Konfigurasi

| Item | Nilai |
|---|---|
| Queue | `pesan.queue` (sama dengan producer) |
| Host RabbitMQ | `localhost:5672` |
| User/Pass | `guest` / `guest` |
| Port consumer-service | `8084` |
| Nama service di Eureka | `consumer-service` |
| Route gateway | `/api/consumer/**` → `lb://consumer-service` |

`consumer/src/main/resources/application.properties`:
```properties
server.port=8084
spring.application.name=consumer-service
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

---

## 7. Struktur Modul

```
consumer/
├── pom.xml                          # spring-boot-starter-amqp + eureka-client
├── src/main/java/com/aufi/consumer/
│   ├── ConsumerApplication.java     # @SpringBootApplication + @EnableDiscoveryClient
│   ├── config/RabbitMQConfig.java   # deklarasi queue + JSON converter
│   ├── listener/PesanListener.java  # @RabbitListener → terima pesan dari queue
│   ├── service/ConsumerService.java # simpan pesan di memori
│   └── controller/ConsumerController.java  # endpoint /api/consumer/**
└── src/main/resources/application.properties
```
