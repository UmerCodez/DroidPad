package com.github.umer0586.droidpad

import android.content.ContentValues
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import com.github.umer0586.droidpad.data.database.AppDatabase
import com.github.umer0586.droidpad.data.database.MIGRATION_4_TO_5
import com.github.umer0586.droidpad.data.database.MIGRATION_5_TO_6
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.io.IOException
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MigrationTest {

    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java,
        emptyList(), // No AutoMigrationSpec needed, we are not using auto migration
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun testMigration4to5(){

        // Create the database at version 4
        val db = helper.createDatabase(TEST_DB, 4)

        // Insert test data in ControlPad table
        val controlPadId = 13L
        val controlPadName = "Test Control Pad"
        val orientation = "LANDSCAPE"
        val backgroundColor = 2342132L
        val width = 720
        val height = 1280

        db.apply {
            val values = ContentValues().apply {
                put("id", controlPadId)
                put("name", controlPadName)
                put("orientation", orientation)
                put("backgroundColor", backgroundColor)
                put("width", width)
                put("height", height)
            }
            insert("ControlPad", 0, values)
        }

        db.close()

        // Run the migration
        val db2 = helper.runMigrationsAndValidate(TEST_DB, 5, true, MIGRATION_4_TO_5)

        // Verify that the data in the ControlPad table is still available after migration
        val controlPadCursor = db2.query("SELECT * FROM ControlPad WHERE id = ?", arrayOf(controlPadId.toString()))
        assertTrue("ControlPad record should exist after migration", controlPadCursor.moveToFirst())

        // Verify that all fields contain the expected values
        assertEquals(controlPadId, controlPadCursor.getLong(controlPadCursor.getColumnIndex("id")))
        assertEquals(controlPadName, controlPadCursor.getString(controlPadCursor.getColumnIndex("name")))
        assertEquals(orientation, controlPadCursor.getString(controlPadCursor.getColumnIndex("orientation")))
        assertEquals(backgroundColor, controlPadCursor.getLong(controlPadCursor.getColumnIndex("backgroundColor")))
        assertEquals(width, controlPadCursor.getInt(controlPadCursor.getColumnIndex("width")))
        assertEquals(height, controlPadCursor.getInt(controlPadCursor.getColumnIndex("height")))

        controlPadCursor.close()

        // The new table added in version 5 (ControlPadSensor) should exist
        val sensorResult = db2.query("SELECT * FROM ControlPadSensor")
        assertEquals(0, sensorResult.count) // Table should exist and be empty
        sensorResult.close()

        db2.apply {
            val sensorTypeAccelerometer = 1  // Integer value for ACCELEROMETER sensor type

            val sensorValues = ContentValues().apply {
                put("controlPadId", controlPadId)  // Foreign key referencing the ControlPad
                put("sensorType", sensorTypeAccelerometer)
            }

            // Insert should succeed as the foreign key constraint is satisfied
            val sensorId = insert("ControlPadSensor", 0, sensorValues)
            assertTrue("Insert with valid foreign key should succeed", sensorId > 0)

            // Verify the record was inserted correctly
            val verifyResult = query("SELECT * FROM ControlPadSensor WHERE controlPadId = ?",
                arrayOf(controlPadId.toString()))
            assertTrue("ControlPadSensor record should exist", verifyResult.moveToFirst())
            assertEquals(controlPadId, verifyResult.getLong(verifyResult.getColumnIndex("controlPadId")))
            assertEquals(sensorTypeAccelerometer, verifyResult.getInt(verifyResult.getColumnIndex("sensorType")))
            verifyResult.close()
        }

        db2.close()
    }

    @Test
    fun testMigration5to6() {
        // Create the database at version 5
        val db = helper.createDatabase(TEST_DB, 5)

        // Insert test data in ControlPad table (without logging column)
        val controlPadId = 42L
        val controlPadName = "Test Control Pad v5"
        val orientation = "PORTRAIT"
        val backgroundColor = 987654321L
        val width = 1080
        val height = 1920

        db.apply {
            val values = ContentValues().apply {
                put("id", controlPadId)
                put("name", controlPadName)
                put("orientation", orientation)
                put("backgroundColor", backgroundColor)
                put("width", width)
                put("height", height)
            }
            insert("ControlPad", 0, values)
        }

        db.close()

        // Run the migration to version 6
        val db2 = helper.runMigrationsAndValidate(TEST_DB, 6, true, MIGRATION_5_TO_6)

        // Verify that the data in the ControlPad table is still available after migration
        val controlPadCursor = db2.query(
            "SELECT * FROM ControlPad WHERE id = ?",
            arrayOf(controlPadId.toString())
        )
        assertTrue("ControlPad record should exist after migration", controlPadCursor.moveToFirst())

        // Verify all pre-existing fields
        assertEquals(controlPadId, controlPadCursor.getLong(controlPadCursor.getColumnIndex("id")))
        assertEquals(controlPadName, controlPadCursor.getString(controlPadCursor.getColumnIndex("name")))
        assertEquals(orientation, controlPadCursor.getString(controlPadCursor.getColumnIndex("orientation")))
        assertEquals(backgroundColor, controlPadCursor.getLong(controlPadCursor.getColumnIndex("backgroundColor")))
        assertEquals(width, controlPadCursor.getInt(controlPadCursor.getColumnIndex("width")))
        assertEquals(height, controlPadCursor.getInt(controlPadCursor.getColumnIndex("height")))

        // ✅ Verify the new logging column exists and defaults to 0 (false)
        assertEquals(0, controlPadCursor.getInt(controlPadCursor.getColumnIndex("logging")))

        controlPadCursor.close()
        db2.close()
    }




}