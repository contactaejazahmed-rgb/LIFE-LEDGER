package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.InventoryDao
import com.example.data.local.dao.NotebookDao
import com.example.data.local.dao.SchoolDao
import com.example.data.local.entity.NotebookEntity
import com.example.data.local.entity.PurchaseEntryEntity
import com.example.data.local.entity.SalesEntryEntity
import com.example.data.local.entity.SchoolEntity
import com.example.data.local.entity.SchoolPriceOverrideEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        NotebookEntity::class,
        SchoolEntity::class,
        SchoolPriceOverrideEntity::class,
        PurchaseEntryEntity::class,
        SalesEntryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notebookDao(): NotebookDao
    abstract fun schoolDao(): SchoolDao
    abstract fun inventoryDao(): InventoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "edubooks_database"
                )
                .addCallback(DatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        DatabaseSeeder.seedDatabase(
                            database.notebookDao(),
                            database.schoolDao(),
                            database.inventoryDao()
                        )
                    }
                }
            }
        }
    }
}
