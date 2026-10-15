# Email Service — Notifikasi Email via SMTP Gmail

Layanan mandiri (`email-service`, port **8085**) yang **mendengarkan event order
melalui RabbitMQ** dan **mengirim email sungguhan** ke inbox Gmail Anda via SMTP.
Terdaftar di Eureka.

## Alur

```
order-service ──publish event──► RabbitMQ (order.exchange)
                                       │ routing: order.created / order.updated / order.deleted
                                       ▼
                             queue: order.email.queue   ← queue SENDIRI
                                       │
                             OrderEmailListener (@RabbitListener)
                                       │
                             EmailService (susun email -> JavaMailSender)
                                       │
                                       ▼
                             SMTP Gmail ──► inbox email tujuan ✅
                             + arsip status (GET /api/emails)
```

> ⚠️ `emailservice` memakai queue **`order.email.queue`** yang **berbeda** dari
> `order.projection.queue` milik CQRS projector. Ini penting: kalau dua konsumen
> berbagi satu queue, pesan akan saling **berebut** (*competing consumers*).

## 🔐 Konfigurasi Kredensial (PENTING)

Nilai rahasia **tidak** disimpan di `application.properties` dan **tidak** ikut
ke Git. Semuanya di file terpisah:

```
emailservice/application-local.properties   ← isi kredensial di sini (di-ignore Git)
emailservice/application-local.properties.example  ← contoh (aman di-commit)
```

Isi `application-local.properties`:

```properties
spring.mail.username=akun-anda@gmail.com
spring.mail.password=<APP_PASSWORD_16_KARAKTER>
app.mail.to=email-tujuan@gmail.com
```

### Cara mendapat App Password Gmail
1. Aktifkan **Verifikasi 2 Langkah**: https://myaccount.google.com/security
2. Buka https://myaccount.google.com/apppasswords
3. Buat app password baru → Google beri **16 karakter**
4. Tempel ke `spring.mail.password` (spasi boleh/tanpa spasi)

> 🔒 `application-local.properties` sudah masuk `.gitignore` → tidak akan ter-commit.

**Catatan path:** `application.properties` memuat file lokal lewat
`spring.config.import=optional:file:./application-local.properties`. Karena path
relatif, **jalankan service dari folder `emailservice/`** agar file terbaca.

## Endpoint

| Method | URL | Fungsi |
|---|---|---|
| GET | `/api/emails` | Daftar email + status pengiriman (`SENT`/`FAILED`) |
| GET | `/api/emails/count` | Jumlah email |
| DELETE | `/api/emails` | Kosongkan arsip |

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
# WAJIB dijalankan dari folder ini agar application-local.properties terbaca:
java -jar target\emailservice-0.0.1-SNAPSHOT.jar
```

Prasyarat: RabbitMQ & Eureka sudah jalan, dan `order-service` (8082) sudah jalan.

## Uji

```bash
# buat order lewat command side order-service
curl -X POST http://localhost:8082/api/orders/commands -H "Content-Type: application/json" \
  -d '{"produkId":1,"pelangganId":1,"tglTransaksi":"2026-10-15","jumlah":2,"total":50000}'

# cek status pengiriman
curl http://localhost:8085/api/emails
# -> [{"tipe":"CREATED","status":"SENT","kepadaEmail":"...","subjek":"Pesanan #..."}]
```

Lalu cek **inbox Gmail** tujuan — email akan masuk dalam beberapa detik.
