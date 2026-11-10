package com.example.data.local

import com.example.data.local.dao.InventoryDao
import com.example.data.local.dao.NotebookDao
import com.example.data.local.dao.SchoolDao
import com.example.data.local.entity.NotebookEntity
import com.example.data.local.entity.PurchaseEntryEntity
import com.example.data.local.entity.SalesEntryEntity
import com.example.data.local.entity.SchoolEntity
import com.example.data.local.entity.SchoolPriceOverrideEntity

object DatabaseSeeder {
    suspend fun seedDatabase(
        notebookDao: NotebookDao,
        schoolDao: SchoolDao,
        inventoryDao: InventoryDao
    ) {
        val initialNotebooks = listOf(
            NotebookEntity(
                sku = "NB-SL-172-REG",
                name = "Single-Line Standard Notebook",
                rulingType = "Single-line",
                size = "Regular (24x18 cm)",
                pageCount = 172,
                bindingType = "Center Stapled",
                paperGsm = 64,
                wholesalePrice = 28.50,
                mrpPrice = 45.00,
                unitsPerCarton = 60,
                currentStock = 1450,
                minStockThreshold = 200,
                description = "Standard single-line ruled exercise book for middle & high school. High brightness 64 GSM paper with margin."
            ),
            NotebookEntity(
                sku = "NB-SL-240-REG",
                name = "Single-Line Jumbo Exercise Book",
                rulingType = "Single-line",
                size = "Regular (24x18 cm)",
                pageCount = 240,
                bindingType = "Soft Bound",
                paperGsm = 64,
                wholesalePrice = 38.00,
                mrpPrice = 60.00,
                unitsPerCarton = 48,
                currentStock = 820,
                minStockThreshold = 150,
                description = "240 pages thick ruled notebook with index, date & page numbering header. Perfect for comprehensive subjects."
            ),
            NotebookEntity(
                sku = "NB-FL-120-REG",
                name = "Four-Line English Notebook",
                rulingType = "Four-line",
                size = "Regular (24x18 cm)",
                pageCount = 120,
                bindingType = "Center Stapled",
                paperGsm = 64,
                wholesalePrice = 22.00,
                mrpPrice = 35.00,
                unitsPerCarton = 72,
                currentStock = 85, // Low Stock intentionally for notification
                minStockThreshold = 150,
                description = "Specially calibrated 4-line rulings (red & blue guide lines) for primary grade handwriting and cursive practice."
            ),
            NotebookEntity(
                sku = "NB-FL-80-REG",
                name = "Four-Line Junior Primer Book",
                rulingType = "Four-line",
                size = "Small (19x15 cm)",
                pageCount = 80,
                bindingType = "Center Stapled",
                paperGsm = 58,
                wholesalePrice = 16.50,
                mrpPrice = 25.00,
                unitsPerCarton = 96,
                currentStock = 540,
                minStockThreshold = 100,
                description = "Lightweight four-line format for pre-primary and Grade 1 students with colorful instructional cover."
            ),
            NotebookEntity(
                sku = "NB-SQ-120-REG",
                name = "Square Grid Mathematics Notebook",
                rulingType = "Ruled (Square Grid)",
                size = "Regular (24x18 cm)",
                pageCount = 120,
                bindingType = "Center Stapled",
                paperGsm = 64,
                wholesalePrice = 23.50,
                mrpPrice = 38.00,
                unitsPerCarton = 72,
                currentStock = 620,
                minStockThreshold = 120,
                description = "10mm arithmetic square grid ruling for arithmetic, tables, and primary school geometry."
            ),
            NotebookEntity(
                sku = "NB-DL-120-REG",
                name = "Double-Line Language Notebook",
                rulingType = "Ruled (Double-line)",
                size = "Regular (24x18 cm)",
                pageCount = 120,
                bindingType = "Center Stapled",
                paperGsm = 64,
                wholesalePrice = 22.50,
                mrpPrice = 35.00,
                unitsPerCarton = 72,
                currentStock = 410,
                minStockThreshold = 100,
                description = "Double-line ruling ideal for regional and Hindi script alignment and calligraphy."
            ),
            NotebookEntity(
                sku = "NB-LB-A4-240",
                name = "A4 Long Book (Single-Line Hard Bound)",
                rulingType = "Long Book",
                size = "A4 (29.7x21 cm)",
                pageCount = 240,
                bindingType = "Hard Bound",
                paperGsm = 70,
                wholesalePrice = 62.00,
                mrpPrice = 95.00,
                unitsPerCarton = 36,
                currentStock = 950,
                minStockThreshold = 150,
                description = "Heavy-duty hardbound full-scape A4 long book. 70 GSM super-smooth opaque paper for senior secondary and college."
            ),
            NotebookEntity(
                sku = "NB-LB-CR-172",
                name = "Crown Long Book (Single-Line)",
                rulingType = "Long Book",
                size = "Crown Long (28x17 cm)",
                pageCount = 172,
                bindingType = "Soft Bound",
                paperGsm = 64,
                wholesalePrice = 36.00,
                mrpPrice = 55.00,
                unitsPerCarton = 48,
                currentStock = 70, // Low Stock for testing
                minStockThreshold = 100,
                description = "Standard crown long notebook. Preferred across secondary schools for daily class lecture notes."
            ),
            NotebookEntity(
                sku = "NB-PR-SCI-172",
                name = "Science Practical Lab Journal (Interleaf)",
                rulingType = "Practical Book",
                size = "A4 (29.7x21 cm)",
                pageCount = 172,
                bindingType = "Hard Bound",
                paperGsm = 70,
                wholesalePrice = 68.00,
                mrpPrice = 110.00,
                unitsPerCarton = 30,
                currentStock = 380,
                minStockThreshold = 100,
                description = "Interleaved format: One side single-line ruled for procedure/theory, facing side high-opacity unruled for diagrams & apparatus sketches."
            ),
            NotebookEntity(
                sku = "NB-PR-GRP-80",
                name = "School Graph Practical Book (1mm/1cm)",
                rulingType = "Practical Book",
                size = "A4 (29.7x21 cm)",
                pageCount = 80,
                bindingType = "Spiral",
                paperGsm = 70,
                wholesalePrice = 34.00,
                mrpPrice = 55.00,
                unitsPerCarton = 60,
                currentStock = 510,
                minStockThreshold = 100,
                description = "Accurate millimeter grid with observation tables, index page, and conversion reference sheets for physics/math labs."
            ),
            NotebookEntity(
                sku = "NB-PR-DRW-80",
                name = "Art & Drawing Sketch Book",
                rulingType = "Practical Book",
                size = "A4 (29.7x21 cm)",
                pageCount = 80,
                bindingType = "Spiral",
                paperGsm = 100,
                wholesalePrice = 42.00,
                mrpPrice = 70.00,
                unitsPerCarton = 40,
                currentStock = 290,
                minStockThreshold = 80,
                description = "Thick 100 GSM cartridge drawing paper suited for pencil sketching, crayons, and water-color artwork."
            ),
            NotebookEntity(
                sku = "NB-UN-300-A4",
                name = "Unruled / Plain Note Register",
                rulingType = "Plain (Unruled)",
                size = "A4 (29.7x21 cm)",
                pageCount = 300,
                bindingType = "Hard Bound",
                paperGsm = 70,
                wholesalePrice = 74.00,
                mrpPrice = 120.00,
                unitsPerCarton = 24,
                currentStock = 45, // Low Stock
                minStockThreshold = 60,
                description = "300 pages unruled ledger/register for administrative records, mind mapping, and rough calculation notebooks."
            )
        )
        notebookDao.insertAll(initialNotebooks)

        val initialSchools = listOf(
            SchoolEntity(
                schoolCode = "SCH-DX-0101",
                name = "St. Xavier's Senior Secondary School",
                affiliation = "ICSE",
                address = "42 Cathedral Road, St. Thomas Enclave",
                city = "New Delhi",
                state = "Delhi NCR",
                pincode = "110001",
                principalName = "Rev. Dr. Thomas Fernandez",
                purchaseManagerName = "Mr. Rajesh K. Sharma",
                contactPhone = "+91 98112 34567",
                contactEmail = "purchase@stxaviers-school.org",
                verificationStatus = "Verified",
                contractedDiscountPercent = 12.0,
                notes = "Annual supply contract renewed. Requires custom school crest hot-foil stamping on long books."
            ),
            SchoolEntity(
                schoolCode = "SCH-DP-0205",
                name = "Delhi Public International Academy",
                affiliation = "CBSE",
                address = "Sector 14, Institutional Area, Expressway",
                city = "Gurugram",
                state = "Haryana",
                pincode = "122001",
                principalName = "Mrs. Sunita Kapoor (M.Ed)",
                purchaseManagerName = "Mr. Anil Verma",
                contactPhone = "+91 98765 43210",
                contactEmail = "procurement@dpia-edu.in",
                verificationStatus = "Premium Partner",
                contractedDiscountPercent = 15.0,
                notes = "Tier 1 school account. Net 30 days payment terms with monthly consolidated batch delivery."
            ),
            SchoolEntity(
                schoolCode = "SCH-OW-0312",
                name = "Oakridge Global Cambridge School",
                affiliation = "Cambridge / IB",
                address = "Hill View Greens, Phase 2, Near Tech Park",
                city = "Bengaluru",
                state = "Karnataka",
                pincode = "560066",
                principalName = "Dr. Michael Davies",
                purchaseManagerName = "Ms. Priya Menon",
                contactPhone = "+91 94480 12890",
                contactEmail = "admin.purchase@oakridgeglobal.edu",
                verificationStatus = "Verified",
                contractedDiscountPercent = 10.0,
                notes = "Primary consumer of A4 Long Books and 100 GSM Art sketch books."
            ),
            SchoolEntity(
                schoolCode = "SCH-MV-0418",
                name = "Modern Vidya Mandir High School",
                affiliation = "State Board",
                address = "Station Road, Near Town Hall",
                city = "Pune",
                state = "Maharashtra",
                pincode = "411002",
                principalName = "Prof. Ramakant Joshi",
                purchaseManagerName = "Mr. Suresh Patil",
                contactPhone = "+91 99220 87654",
                contactEmail = "office@mvm-schoolpune.edu",
                verificationStatus = "Pending Verification",
                contractedDiscountPercent = 8.0,
                notes = "Registration submitted with affiliation certificate. Verification visit pending."
            )
        )
        schoolDao.insertAllSchools(initialSchools)

        // Seed custom price override for St. Xavier's on A4 Long books
        schoolDao.insertOrUpdatePriceOverride(
            SchoolPriceOverrideEntity(
                schoolId = 1,
                notebookId = 7, // A4 Long Book
                customPrice = 52.00 // Special negotiated rate
            )
        )

        val currentTime = System.currentTimeMillis()
        val dayMillis = 86400000L

        val initialPurchases = listOf(
            PurchaseEntryEntity(
                entryNumber = "PUR-2026-0042",
                dateEpoch = currentTime - (dayMillis * 3),
                supplierName = "JK Paper & Stationery Mills Ltd.",
                notebookId = 1,
                notebookName = "Single-Line Standard Notebook",
                quantity = 2000,
                unitCost = 21.00,
                totalCost = 42000.00,
                batchNumber = "B-SL172-NOV26",
                notes = "Received 33 cartons in prime condition. Moisture level inspected 4.5%."
            ),
            PurchaseEntryEntity(
                entryNumber = "PUR-2026-0041",
                dateEpoch = currentTime - (dayMillis * 7),
                supplierName = "Ballarpur Pulp & Print Works",
                notebookId = 7,
                notebookName = "A4 Long Book (Single-Line Hard Bound)",
                quantity = 1200,
                unitCost = 46.50,
                totalCost = 55800.00,
                batchNumber = "B-LBA4-NOV03",
                notes = "Hard bound spine durability tested. UV gloss lamination approved."
            ),
            PurchaseEntryEntity(
                entryNumber = "PUR-2026-0040",
                dateEpoch = currentTime - (dayMillis * 12),
                supplierName = "National Graphic Paper Corp",
                notebookId = 9,
                notebookName = "Science Practical Lab Journal (Interleaf)",
                quantity = 600,
                unitCost = 51.00,
                totalCost = 30600.00,
                batchNumber = "B-LAB172-OCT28",
                notes = "Batch for term-2 school science laboratories."
            )
        )
        inventoryDao.insertAllPurchaseEntries(initialPurchases)

        val initialSales = listOf(
            SalesEntryEntity(
                invoiceNumber = "INV-SCH-2026-089",
                dateEpoch = currentTime - (dayMillis * 1),
                schoolId = 2,
                schoolName = "Delhi Public International Academy",
                totalAmount = 38760.00,
                totalUnits = 1200,
                status = "Dispatched",
                paymentTerms = "Net 30 Days",
                itemsSummary = "Single-Line 172p (x800), A4 Long Book 240p (x400)",
                itemsJson = """[{"name":"Single-Line Standard Notebook","qty":800,"price":24.22,"total":19376.0},{"name":"A4 Long Book (Single-Line Hard Bound)","qty":400,"price":48.46,"total":19384.0}]""",
                notes = "Truck dispatched with consignment #DEL-8821. Gate pass attached."
            ),
            SalesEntryEntity(
                invoiceNumber = "INV-SCH-2026-088",
                dateEpoch = currentTime - (dayMillis * 4),
                schoolId = 1,
                schoolName = "St. Xavier's Senior Secondary School",
                totalAmount = 26520.00,
                totalUnits = 600,
                status = "Delivered",
                paymentTerms = "Net 15 Days",
                itemsSummary = "Science Practical Lab (x300), A4 Long Book (x300)",
                itemsJson = """[{"name":"Science Practical Lab Journal","qty":300,"price":59.84,"total":17952.0},{"name":"A4 Long Book (Custom Xavier's Rate)","qty":300,"price":28.56,"total":8568.0}]""",
                notes = "Received and signed by Purchase Manager Mr. Sharma. Stamp verified."
            ),
            SalesEntryEntity(
                invoiceNumber = "INV-SCH-2026-090",
                dateEpoch = currentTime - (dayMillis * 0),
                schoolId = 3,
                schoolName = "Oakridge Global Cambridge School",
                totalAmount = 18900.00,
                totalUnits = 450,
                status = "Pending Dispatch",
                paymentTerms = "Advance",
                itemsSummary = "Art Sketch Book (x200), Four-Line English (x250)",
                itemsJson = """[{"name":"Art & Drawing Sketch Book","qty":200,"price":37.80,"total":7560.0},{"name":"Four-Line English Notebook","qty":250,"price":19.80,"total":4950.0}]""",
                notes = "Packing in progress in warehouse bay 3. Awaiting dispatch clearance."
            )
        )
        inventoryDao.insertAllSalesEntries(initialSales)
    }
}
