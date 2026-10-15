# Order Service — Implementasi CQRS + Email Notifikasi

Modul `order` pada proyek microservices ini mengimplementasikan **CQRS
(Command and Query Responsibility Segregation)**: memisahkan jalur **tulis
(Command)** dari jalur **baca (Query)**, dihubungkan oleh **event bus RabbitMQ**.
Selain itu, data pelanggan & produk **diperkaya (denormalisasi)** ke dalam event,
dan event yang sama juga dikonsumsi **emailservice** untuk menghasilkan notifikasi
email.

## Konsep

```
        ┌──────────── WRITE SIDE (Command) ────────────┐
POST/PUT/DELETE ─► OrderCommandController ─► OrderCommandService
                        │  tulis ke WRITE MODEL (tabel `orders`)
                        │  enrich via Eureka: produk-service & pelanggan-service
                        └─► OrderEventPublisher ─► RabbitMQ
                                     exchange : order.exchange (topic)
                                     routing  : order.created / order.updated / order.deleted
                                     │
                     ┌───────────────┴────────────────┐
                     ▼                                ▼
        queue : order.projection.queue      queue : order.email.queue
                     │                                │
        OrderEventConsumer (@RabbitListener)   OrderEmailListener (emailservice)
                     │                                │
        OrderViewProjector                    EmailService (susun email -> log)
                     │                                │
                     ▼                                ▼
        READ MODEL (tabel `order_view`)        outbox email (GET /api/emails)

        ┌──────────── READ SIDE (Query) ────────────────┐
GET ──► OrderQueryController ──► OrderQueryService ──► OrderViewRepository
                              (baca saja, dari read model `order_view`)
```

**Prinsip:**
- Sisi **Command** hanya mengubah state + menerbitkan event (tidak melayani query baca).
- Sisi **Query** hanya membaca read model (tidak mengubah apa pun).
- Read model diperbarui **asinkron** lewat event RabbitMQ → **eventual consistency**.
- Setiap konsumen (projector & emailservice) memakai **queue sendiri** agar tidak
  berebut pesan (*competing consumers*).

## Struktur package (modul `order`)

| Package | Isi | Peran |
|---|---|---|
| `command` | `OrderCommandController`, `OrderCommandService`, `OrderDataEnricher`, `command.dto.*` | Sisi tulis + enrichment |
| `query` | `OrderQueryController`, `OrderQueryService`, `OrderView`, `OrderViewRepository` | Sisi baca |
| `event` | `OrderEvent`, `OrderEventPublisher`, `OrderEventConsumer`, `OrderViewProjector` | Event bus + proyeksi |
| `config` | `CqrsRabbitConfig` | Exchange, queue, binding, converter JSON |
| `entity`/`repository` | `Order`, `OrderRepository` | Write model (tabel `orders`) |
| `service`/`controller` | `OrderService`, `OrderController` | Endpoint lama (backward-compatible) |

## Model data

| Model | Tabel | Keterangan |
|---|---|---|
| Write model | `orders` | Entity `Order` — sumber kebenaran |
| Read model | `order_view` | Entity `OrderView` — hasil proyeksi event, sudah **terdenormalisasi** (nama produk, nama & email pelanggan) |

## Endpoint

### Command (write) — port 8082
| Method | URL | Fungsi |
|---|---|---|
| POST | `/api/orders/commands` | Buat order |
| PUT | `/api/orders/commands/{id}` | Ubah order |
| DELETE | `/api/orders/commands/{id}` | Hapus order |
| GET | `/api/orders/commands/write-model` | (demo) lihat isi write model |

### Query (read) — port 8082
| Method | URL | Fungsi |
|---|---|---|
| GET | `/api/orders/queries` | Semua order (dari read model) |
| GET | `/api/orders/queries/{id}` | Order by id |
| GET | `/api/orders/queries/count` | Jumlah order |

### Email — port 8085
| Method | URL | Fungsi |
|---|---|---|
| GET | `/api/emails` | Daftar email yang dihasilkan dari event order |
| GET | `/api/emails/count` | Jumlah email |
| DELETE | `/api/emails` | Kosongkan outbox email |

### Endpoint lama (backward-compatible) — port 8082
| Method | URL |
|---|---|
| GET | `/api/orders` |
| GET | `/api/orders/{id}` |
| GET | `/api/orders/{id}/with-produk` |
| POST | `/api/orders` |
| DELETE | `/api/orders/{id}` |

## Cara menjalankan & menguji

Prasyarat: RabbitMQ jalan, JDK 21, Eureka jalan.

```powershell
# build
cd order;          .\mvnw.cmd -DskipTests clean package
cd ..\pelanggan;   .\mvnw.cmd -DskipTests clean package
cd ..\emailservice;.\mvnw.cmd -DskipTests clean package

# jalankan (urut: RabbitMQ -> Eureka -> produk/pelanggan -> order -> emailservice)
java -jar order\target\order-0.0.1-SNAPSHOT.jar
java -jar emailservice\target\emailservice-0.0.1-SNAPSHOT.jar
```

### Uji alur CQRS + email

```bash
# siapkan data
curl -X POST http://localhost:8080/api/produk -H "Content-Type: application/json" \
  -d '{"nama":"Kopi Arabika","deskripsi":"Kopi premium","harga":25000,"jenis":"Minuman"}'
curl -X POST http://localhost:8081/api/pelanggan -H "Content-Type: application/json" \
  -d '{"nama":"Aufi","alamat":"Padang","jenisKelamin":"Laki-laki","email":"aufi@mail.com"}'

# CREATE (command)
curl -X POST http://localhost:8082/api/orders/commands -H "Content-Type: application/json" \
  -d '{"produkId":1,"pelangganId":1,"tglTransaksi":"2026-10-15","jumlah":3,"total":75000}'

# READ (query) -> read model terisi + nama produk & pelanggan
curl http://localhost:8082/api/orders/queries

# UPDATE (command)
curl -X PUT http://localhost:8082/api/orders/commands/1 -H "Content-Type: application/json" \
  -d '{"produkId":1,"pelangganId":1,"tglTransaksi":"2026-10-15","jumlah":5,"total":125000}'

# DELETE (command)
curl -X DELETE http://localhost:8082/api/orders/commands/1

# EMAIL yang dihasilkan
curl http://localhost:8085/api/emails
```

**Bukti di log:**
```
>> [CQRS] Event diterima dari RabbitMQ: CREATED orderId=1
>> [EMAIL] Event diterima dari RabbitMQ: CREATED orderId=1
======================================================
  EMAIL TERKIRIM
  Kepada : Aufi <aufi@mail.com>
  Subjek : Pesanan #1 Berhasil Dibuat
======================================================
```

**Bukti di RabbitMQ UI** (http://localhost:15672):
- Exchange `order.exchange` (topic)
- Queue `order.projection.queue` (projector) & `order.email.queue` (emailservice)
  — masing-masing `consumers: 1`

## Catatan

- Konsistensi antara write & read model bersifat **eventual** (ada jeda ~milidetik
  saat event diproyeksikan).
- Karena event dikirim lewat RabbitMQ (bukan in-process), sisi Query secara
  arsitektur bisa dipisah jadi service tersendiri (distributed CQRS).
- **Opsi B (denormalisasi saat proyeksi):** data produk & pelanggan diambil sisi
  Command (via Eureka) dan ditanam ke event, sehingga read model & email siap pakai
  tanpa perlu memanggil service lain saat dibaca.
- Pengiriman email pada tahap ini **disimulasikan** lewat log + outbox REST
  (belum SMTP asli). Untuk produksi tinggal tambahkan `spring-boot-starter-mail`.
