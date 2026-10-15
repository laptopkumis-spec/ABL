# Email Service

Layanan mandiri (`email-service`, port **8085**) yang **mendengarkan event order
melalui RabbitMQ** dan menghasilkan notifikasi email. Terdaftar di Eureka.

## Alur

```
order-service ──publish event──► RabbitMQ (order.exchange)
                                       │ routing: order.created / order.updated / order.deleted
                                       ▼
                             queue: order.email.queue   ← queue SENDIRI
                                       │
                             OrderEmailListener (@RabbitListener)
                                       │
                             EmailService (susun isi email)
                                       │
                                       ▼
                             LOG console  +  outbox (GET /api/emails)
```

> ⚠️ `emailservice` memakai queue **`order.email.queue`** yang **berbeda** dari
> `order.projection.queue` milik CQRS projector. Ini penting: kalau dua konsumen
> berbagi satu queue, pesan akan saling **berebut** (*competing consumers*) dan
> salah satu tidak kebagian.

## Endpoint

| Method | URL | Fungsi |
|---|---|---|
| GET | `/api/emails` | Daftar email yang dihasilkan |
| GET | `/api/emails/count` | Jumlah email |
| DELETE | `/api/emails` | Kosongkan outbox |

## Jenis email

| Event | Subjek |
|---|---|
| `order.created` | "Pesanan #<id> Berhasil Dibuat" |
| `order.updated` | "Pesanan #<id> Telah Diperbarui" |
| `order.deleted` | "Pesanan #<id> Dibatalkan" |

## Menjalankan

```powershell
cd emailservice
.\mvnw.cmd -DskipTests clean package
java -jar target\emailservice-0.0.1-SNAPSHOT.jar
```

Prasyarat: RabbitMQ & Eureka sudah jalan, dan `order-service` (8082) sudah jalan
agar event dipublikasikan.

## Uji

```bash
# buat order lewat command side order-service
curl -X POST http://localhost:8082/api/orders/commands -H "Content-Type: application/json" \
  -d '{"produkId":1,"pelangganId":1,"tglTransaksi":"2026-10-15","jumlah":3,"total":75000}'

# lihat email yang dihasilkan
curl http://localhost:8085/api/emails
```

## Catatan

- Pengiriman email **disimulasikan** lewat log console + outbox in-memory. Untuk
  SMTP asli, tambahkan dependency `spring-boot-starter-mail` dan konfigurasi
  `spring.mail.*`.
