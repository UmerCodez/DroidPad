package com.github.umer0586.droidpad.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/*
 * Room database migration behavior:
 *
 * - Existing users (app update):
 *   When the database version is incremented (e.g., from version 4 to 5), Room will apply the
 *   corresponding Migration object (e.g., Migration(4, 5)) to migrate the user's existing schema.
 *   This ensures their data remains intact while updating the structure.
 *
 * - New users (fresh install):
 *   For users installing the app for the first time, Room creates the database using the latest
 *   schema based on the current @Entity definitions. In this case, Room does not run any migrations.
 *
 * Therefore, migrations are only executed for users who are upgrading from an older version.
 */


// New table added in version 5
val MIGRATION_4_TO_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
             CREATE TABLE IF NOT EXISTS `ControlPadSensor` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `controlPadId` INTEGER NOT NULL,
                `sensorType` INTEGER NOT NULL,
                FOREIGN KEY(`controlPadId`) REFERENCES `ControlPad`(`id`) ON DELETE CASCADE
            )
            """.trimIndent()
        )
    }
}
