package com.example.homeserv.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.homeserv.data.BookingItem
import com.example.homeserv.data.BookingStatus
import com.example.homeserv.data.DashboardCounts
import com.example.homeserv.data.Offer
import com.example.homeserv.data.Provider
import com.example.homeserv.data.Roles
import com.example.homeserv.data.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE users(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                phone TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                role TEXT NOT NULL
            )
        """.trimIndent())
        db.execSQL("""
            CREATE TABLE providers(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                phone TEXT NOT NULL,
                service_type TEXT NOT NULL,
                user_id INTEGER,
                FOREIGN KEY(user_id) REFERENCES users(id)
            )
        """.trimIndent())
        db.execSQL("""
            CREATE TABLE offers(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                provider_id INTEGER NOT NULL,
                title TEXT NOT NULL,
                description TEXT NOT NULL,
                price REAL NOT NULL,
                duration TEXT NOT NULL,
                created_at TEXT NOT NULL,
                FOREIGN KEY(provider_id) REFERENCES providers(id)
            )
        """.trimIndent())
        db.execSQL("""
            CREATE TABLE bookings(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                offer_id INTEGER NOT NULL,
                customer_id INTEGER NOT NULL,
                notes TEXT,
                date_time TEXT NOT NULL,
                status TEXT NOT NULL,
                FOREIGN KEY(offer_id) REFERENCES offers(id),
                FOREIGN KEY(customer_id) REFERENCES users(id)
            )
        """.trimIndent())
        seed(db)
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        if (!db.isReadOnly) {
            syncServiceProviderUsers(db)
            ensureDefaultOffersForProviders(db)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS bookings")
        db.execSQL("DROP TABLE IF EXISTS offers")
        db.execSQL("DROP TABLE IF EXISTS providers")
        db.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }

    private fun seed(db: SQLiteDatabase) {
        insertUser(db, "Admin", "0000", "admin123", Roles.ADMIN)
        val customerId = insertUser(db, "Ali Customer", "03000000001", "123456", Roles.CUSTOMER).toInt()
        val providerUserId = insertUser(db, "Usman Provider", "03000000002", "123456", Roles.PROVIDER).toInt()
        val plumber = insertProvider(db, "Usman Provider", "03000000002", "Plumber", providerUserId).toInt()
        val electrician = insertProvider(db, "Bright Electric", "03003334444", "Electrician", null).toInt()
        val cleaner = insertProvider(db, "CleanPro Team", "03005556666", "Cleaner", null).toInt()
        val carpenter = insertProvider(db, "WoodWorks PK", "03007778888", "Carpenter", null).toInt()
        val painter = insertProvider(db, "Green Paint Masters", "03009990000", "Painter", null).toInt()
        val firstOffer = insertOffer(db, plumber, "Emergency Pipe Repair", "Leak fixing, pipe replacement, and bathroom plumbing support.", 2500.0, "2 hours").toInt()
        insertOffer(db, electrician, "Fan & Switch Installation", "Safe installation of fans, switches, boards, and wiring checks.", 1800.0, "1.5 hours")
        insertOffer(db, cleaner, "Deep Home Cleaning", "Complete room, kitchen, washroom, and floor cleaning service.", 5000.0, "4 hours")
        insertOffer(db, carpenter, "Furniture Repair", "Door, cabinet, chair, and small furniture repair service.", 3000.0, "3 hours")
        insertOffer(db, painter, "Room Paint Service", "Single room paint service with wall preparation and finishing.", 8000.0, "1 day")
        insertBooking(db, firstOffer, customerId, "Please come after 4 PM.")
    }

    fun registerUser(name: String, phone: String, password: String, role: String, serviceType: String? = null): Long {
        val db = writableDatabase
        db.beginTransaction()
        return try {
            val userId = insertUser(db, name, phone, password, role)
            if (userId > 0 && role == Roles.PROVIDER) {
                val providerServiceType = serviceType ?: DEFAULT_PROVIDER_SERVICE
                val providerId = insertProvider(db, name, phone, providerServiceType, userId.toInt())
                if (providerId > 0) insertDefaultOfferForProvider(db, providerId.toInt(), providerServiceType)
            }
            db.setTransactionSuccessful()
            userId
        } finally {
            db.endTransaction()
        }
    }

    private fun insertUser(db: SQLiteDatabase, name: String, phone: String, password: String, role: String): Long {
        val values = ContentValues().apply {
            put("name", name); put("phone", phone); put("password", password); put("role", role)
        }
        return db.insert("users", null, values)
    }

    fun loginUser(phone: String, password: String): User? {
        val c = readableDatabase.rawQuery(
            "SELECT id,name,phone,role FROM users WHERE phone=? AND password=? LIMIT 1",
            arrayOf(phone, password)
        )
        c.use { return if (it.moveToFirst()) it.toUser() else null }
    }

    fun getUserById(userId: Int): User? {
        val c = readableDatabase.rawQuery("SELECT id,name,phone,role FROM users WHERE id=?", arrayOf(userId.toString()))
        c.use { return if (it.moveToFirst()) it.toUser() else null }
    }

    fun getCustomers(): List<User> {
        val list = mutableListOf<User>()
        val c = readableDatabase.rawQuery("SELECT id,name,phone,role FROM users WHERE role=? ORDER BY name", arrayOf(Roles.CUSTOMER))
        c.use { while (it.moveToNext()) list.add(it.toUser()) }
        return list
    }

    fun addProvider(name: String, phone: String, serviceType: String, userId: Int?): Long {
        val db = writableDatabase
        db.beginTransaction()
        return try {
            val providerId = insertProvider(db, name, phone, serviceType, userId)
            if (providerId > 0) insertDefaultOfferForProvider(db, providerId.toInt(), serviceType)
            db.setTransactionSuccessful()
            providerId
        } finally {
            db.endTransaction()
        }
    }

    private fun insertProvider(db: SQLiteDatabase, name: String, phone: String, serviceType: String, userId: Int?): Long {
        val values = ContentValues().apply {
            put("name", name); put("phone", phone); put("service_type", serviceType)
            if (userId == null) putNull("user_id") else put("user_id", userId)
        }
        return db.insert("providers", null, values)
    }

    private fun syncServiceProviderUsers(db: SQLiteDatabase) {
        val c = db.rawQuery("""
            SELECT u.id,u.name,u.phone
            FROM users u
            LEFT JOIN providers p ON p.user_id = u.id
            WHERE u.role = ? AND p.id IS NULL
        """.trimIndent(), arrayOf(Roles.PROVIDER))
        c.use {
            while (it.moveToNext()) {
                val providerId = insertProvider(
                    db,
                    it.getString(it.getColumnIndexOrThrow("name")),
                    it.getString(it.getColumnIndexOrThrow("phone")),
                    DEFAULT_PROVIDER_SERVICE,
                    it.getInt(it.getColumnIndexOrThrow("id"))
                )
                if (providerId > 0) insertDefaultOfferForProvider(db, providerId.toInt(), DEFAULT_PROVIDER_SERVICE)
            }
        }
    }

    fun getProviders(): List<Provider> {
        val list = mutableListOf<Provider>()
        val c = readableDatabase.rawQuery("SELECT id,name,phone,service_type,user_id FROM providers ORDER BY name", null)
        c.use { while (it.moveToNext()) list.add(it.toProvider()) }
        return list
    }

    fun getProvidersForUser(userId: Int, role: String): List<Provider> {
        if (role == Roles.ADMIN) return getProviders()
        val list = mutableListOf<Provider>()
        val c = readableDatabase.rawQuery("SELECT id,name,phone,service_type,user_id FROM providers WHERE user_id=? ORDER BY name", arrayOf(userId.toString()))
        c.use { while (it.moveToNext()) list.add(it.toProvider()) }
        return list
    }

    fun addOffer(providerId: Int, title: String, description: String, price: Double, duration: String): Long =
        insertOffer(writableDatabase, providerId, title, description, price, duration)

    private fun insertOffer(db: SQLiteDatabase, providerId: Int, title: String, description: String, price: Double, duration: String): Long {
        val values = ContentValues().apply {
            put("provider_id", providerId); put("title", title); put("description", description)
            put("price", price); put("duration", duration); put("created_at", now())
        }
        return db.insert("offers", null, values)
    }

    private fun insertDefaultOfferForProvider(db: SQLiteDatabase, providerId: Int, serviceType: String): Long {
        val cleanServiceType = if (serviceType.isBlank()) DEFAULT_PROVIDER_SERVICE else serviceType
        return insertOffer(
            db = db,
            providerId = providerId,
            title = "$cleanServiceType Service",
            description = "Professional $cleanServiceType service provider is available for booking. Contact provider for complete details.",
            price = 0.0,
            duration = "Contact provider"
        )
    }

    private fun ensureDefaultOffersForProviders(db: SQLiteDatabase) {
        val c = db.rawQuery("""
            SELECT p.id,p.service_type
            FROM providers p
            LEFT JOIN offers o ON o.provider_id = p.id
            WHERE o.id IS NULL
            ORDER BY p.id
        """.trimIndent(), null)
        c.use {
            while (it.moveToNext()) {
                insertDefaultOfferForProvider(
                    db,
                    it.getInt(it.getColumnIndexOrThrow("id")),
                    it.getString(it.getColumnIndexOrThrow("service_type"))
                )
            }
        }
    }

    fun getOffers(): List<Offer> {
        val list = mutableListOf<Offer>()
        val c = readableDatabase.rawQuery("""
            SELECT o.id,o.provider_id,p.name AS provider_name,p.service_type,o.title,o.description,o.price,o.duration
            FROM offers o INNER JOIN providers p ON p.id=o.provider_id ORDER BY o.id DESC
        """.trimIndent(), null)
        c.use { while (it.moveToNext()) list.add(it.toOffer()) }
        return list
    }

    fun getOfferById(offerId: Int): Offer? {
        val c = readableDatabase.rawQuery("""
            SELECT o.id,o.provider_id,p.name AS provider_name,p.service_type,o.title,o.description,o.price,o.duration
            FROM offers o INNER JOIN providers p ON p.id=o.provider_id WHERE o.id=?
        """.trimIndent(), arrayOf(offerId.toString()))
        c.use { return if (it.moveToFirst()) it.toOffer() else null }
    }

    fun addBooking(offerId: Int, customerId: Int, notes: String): Long =
        insertBooking(writableDatabase, offerId, customerId, notes)

    private fun insertBooking(db: SQLiteDatabase, offerId: Int, customerId: Int, notes: String): Long {
        val values = ContentValues().apply {
            put("offer_id", offerId); put("customer_id", customerId); put("notes", notes)
            put("date_time", now()); put("status", BookingStatus.ACTIVE)
        }
        return db.insert("bookings", null, values)
    }

    fun getBookingsForUser(user: User): List<BookingItem> = when (user.role) {
        Roles.ADMIN -> queryBookings("ORDER BY b.id DESC", emptyArray())
        Roles.PROVIDER -> queryBookings("WHERE p.user_id=? ORDER BY b.id DESC", arrayOf(user.id.toString()))
        else -> queryBookings("WHERE b.customer_id=? ORDER BY b.id DESC", arrayOf(user.id.toString()))
    }

    private fun queryBookings(whereClause: String, args: Array<String>): List<BookingItem> {
        val list = mutableListOf<BookingItem>()
        val c = readableDatabase.rawQuery("""
            SELECT b.id,b.offer_id,o.title AS service_title,u.name AS customer_name,p.name AS provider_name,
                   o.price,b.date_time,b.status,IFNULL(b.notes,'') AS notes
            FROM bookings b
            INNER JOIN offers o ON o.id=b.offer_id
            INNER JOIN providers p ON p.id=o.provider_id
            INNER JOIN users u ON u.id=b.customer_id
            $whereClause
        """.trimIndent(), args)
        c.use {
            while (it.moveToNext()) {
                list.add(BookingItem(
                    id = it.getInt(it.getColumnIndexOrThrow("id")),
                    offerId = it.getInt(it.getColumnIndexOrThrow("offer_id")),
                    serviceTitle = it.getString(it.getColumnIndexOrThrow("service_title")),
                    customerName = it.getString(it.getColumnIndexOrThrow("customer_name")),
                    providerName = it.getString(it.getColumnIndexOrThrow("provider_name")),
                    price = it.getDouble(it.getColumnIndexOrThrow("price")),
                    dateTime = it.getString(it.getColumnIndexOrThrow("date_time")),
                    status = it.getString(it.getColumnIndexOrThrow("status")),
                    notes = it.getString(it.getColumnIndexOrThrow("notes"))
                ))
            }
        }
        return list
    }

    fun markBookingCompleted(bookingId: Int): Int {
        val values = ContentValues().apply { put("status", BookingStatus.COMPLETED) }
        return writableDatabase.update("bookings", values, "id=?", arrayOf(bookingId.toString()))
    }

    fun getDashboardCounts(): DashboardCounts = DashboardCounts(count("offers"), count("providers"), count("bookings"))

    private fun count(table: String): Int {
        val c = readableDatabase.rawQuery("SELECT COUNT(*) FROM $table", null)
        c.use { return if (it.moveToFirst()) it.getInt(0) else 0 }
    }

    private fun now(): String = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())

    private fun Cursor.toUser(): User = User(
        getInt(getColumnIndexOrThrow("id")), getString(getColumnIndexOrThrow("name")),
        getString(getColumnIndexOrThrow("phone")), getString(getColumnIndexOrThrow("role"))
    )

    private fun Cursor.toProvider(): Provider {
        val idx = getColumnIndexOrThrow("user_id")
        return Provider(
            getInt(getColumnIndexOrThrow("id")), getString(getColumnIndexOrThrow("name")),
            getString(getColumnIndexOrThrow("phone")), getString(getColumnIndexOrThrow("service_type")),
            if (isNull(idx)) null else getInt(idx)
        )
    }

    private fun Cursor.toOffer(): Offer = Offer(
        getInt(getColumnIndexOrThrow("id")), getInt(getColumnIndexOrThrow("provider_id")),
        getString(getColumnIndexOrThrow("provider_name")), getString(getColumnIndexOrThrow("service_type")),
        getString(getColumnIndexOrThrow("title")), getString(getColumnIndexOrThrow("description")),
        getDouble(getColumnIndexOrThrow("price")), getString(getColumnIndexOrThrow("duration"))
    )

    companion object {
        private const val DB_NAME = "homeserv.db"
        private const val DB_VERSION = 1
        private const val DEFAULT_PROVIDER_SERVICE = "General Service"
    }
}
