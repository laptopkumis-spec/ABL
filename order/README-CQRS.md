# Order Service — Implementasi CQRS

Modul `order` pada proyek microservices ini mengimplementasikan **CQRS
(Command and Query Responsibility Segregation)**: memisahkan jalur **tulis
(Command)** dari jalur **baca (Query)**, dihubungkan oleh **event bus RabbitMQ**.

## Konsep

```
        ┌──────────── WRITE SIDE (Command) ────────────┐
POST/PUT/DELETE ─► OrderCommandController ─► OrderCommandService
                        │  tulis ke WRITE MODEL (tabel `orders`)
                        └─► OrderEventPublisher ─► RabbitMQ
                                     exchange : order.exchange
                                     routing  : order.created / order.updated / order.deleted
                                     │
                              queue : order.projection.queue
                                     │
                              OrderEventConsumer (@RabbitListener)
                                     │
                              OrderViewProjector
                                     └─► update READ MODEL (tabel `order_view`)

        ┌──────────── READ SIDE (Query) ────────────────┐
GET ──► OrderQueryController ──► OrderQueryService ──► OrderViewRepository
                              (baca saja, dari read model `order_view`)
```

**Prinsip:**
- Sisi **Command** hanya mengubah state + menerbitkan event (tidak melayani query baca).
- Sisi **Query** hanya membaca read model (tidak mengubah apa pun).
- Read model diperbarui **asinkron** lewat event RabbitMQ → **eventual consistency**.

## Struktur package

| Package | Isi | Peran |
|---|---|---|
| `command` | `OrderCommandController`, `OrderCommandService`, `command.dto.*` | Sisi tulis |
| `query` | `OrderQueryController`, `OrderQueryService`, `OrderView`, `OrderViewRepository` | Sisi baca |
| `event` | `OrderEvent`, `OrderEventPublisher`, `OrderEventConsumer`, `OrderViewProjector` | Event bus + proyeksi |
| `config` | `CqrsRabbitConfig` | Exchange, queue, binding, converter JSON |
| `entity`/`repository` | `Order`, `OrderRepository` | Write model (tabel `orders`) |
| `service`/`controller` | `OrderService`, `OrderController` | Endpoint lama (backward-compatible, tetap mem-publish event) |

## Model data

| Model | Tabel | Keterangan |
|---|---|---|
| Write model | `orders` | Entity `Order` — sumber kebenaran |
| Read model | `order_view` | Entity `OrderView` — hasil proyeksi event (+ `status`, `lastUpdated`) |

## Endpoint

### Command (write)
| Method | URL | Fungsi |
|---|---|---|
| POST | `/api/orders/commands` | Buat order |
| PUT | `/api/orders/commands/{id}` | Ubah order |
| DELETE | `/api/orders/commands/{id}` | Hapus order |
| GET | `/api/orders/commands/write-model` | (demo) lihat isi write model |

### Query (read)
| Method | URL | Fungsi |
|---|---|---|
| GET | `/api/orders/queries` | Semua order (dari read model) |
| GET | `/api/orders/queries/{id}` | Order by id |
| GET | `/api/orders/queries/count` | Jumlah order |

### Endpoint lama (backward-compatible)
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
# 1. build
cd order
.\mvnw.cmd -DskipTests clean package

# 2. jalankan (urut: RabbitMQ -> Eureka -> order)
java -jar target\order-0.0.1-SNAPSHOT.jar
```

### Uji alur CQRS

```bash
# CREATE (command)
curl -X POST http://localhost:8082/api/orders/commands \
  -H "Content-Type: application/json" \
  -d '{"produkId":1,"pelangganId":1,"tglTransaksi":"2026-10-13","jumlah":2,"total":50000}'

# READ (query) -> data muncul dari read model (hasil proyeksi event)
curl http://localhost:8082/api/orders/queries

# UPDATE (command)
curl -X PUT http://localhost:8082/api/orders/commands/1 \
  -H "Content-Type: application/json" \
  -d '{"produkId":1,"pelangganId":1,"tglTransaksi":"2026-10-13","jumlah":5,"total":125000}'

# DELETE (command)
curl -X DELETE http://localhost:8082/api/orders/commands/1
```

**Bukti di log:** setiap event tercatat
```
>> [CQRS] Event diterima dari RabbitMQ: CREATED orderId=1
>> [CQRS] Event diterima dari RabbitMQ: UPDATED orderId=1
>> [CQRS] Event diterima dari RabbitMQ: DELETED orderId=1
```

**Bukti di RabbitMQ UI** (http://localhost:15672):
- Exchange `order.exchange` (topic)
- Queue `order.projection.queue` (consumers: 1, messages: 0)

## Catatan

- Konsistensi antara write & read model bersifat **eventual** (ada jeda ~milidetik
  saat event diproyeksikan).
- Karena event dikirim lewat RabbitMQ (bukan in-process), sisi Query secara
  arsitektur bisa dipisah jadi service tersendiri (distributed CQRS).
