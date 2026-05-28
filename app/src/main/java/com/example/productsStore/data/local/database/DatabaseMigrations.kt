package com.example.productsStore.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    val MIGRATION_1_2 = object : Migration(
        startVersion = 1,
        endVersion = 2,
    ) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                    ALTER TABLE cart_products
                    ADD COLUMN reminderEnabled INTEGER NOT NULL DEFAULT 0
                """.trimIndent()
            )
        }
    }
}