package com.github.umer0586.droidpad.daotests

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.github.umer0586.droidpad.data.database.AppDatabase
import com.github.umer0586.droidpad.MainDispatcherRule
import com.github.umer0586.droidpad.data.database.dao.ControlPadDao
import com.github.umer0586.droidpad.data.database.dao.ControlPadItemDao
import com.github.umer0586.droidpad.data.database.entities.ControlPad
import com.github.umer0586.droidpad.data.database.entities.ControlPadItem
import com.github.umer0586.droidpad.data.database.entities.ItemType
import com.github.umer0586.droidpad.data.database.entities.Orientation

import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException


@RunWith(RobolectricTestRunner::class)
class ControlPadItemDaoTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var controlPadDao: ControlPadDao
    private lateinit var controlPadItemDao: ControlPadItemDao
    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()

        controlPadDao = db.controlPadDao()
        controlPadItemDao = db.controlPadItemDao()
    }

    @After
    @Throws(IOException::class)
    fun teardown() {
        db.close()
    }

    @Test
    fun `test insert and retrieve control pad item`() = runTest{

        var controlPad = ControlPad(
            name = "test controller",
            orientation = Orientation.LANDSCAPE
        )
        val newControlPadId = controlPadDao.insert(controlPad)

        controlPadDao.getById(newControlPadId)?.let { retrievedControlPad ->
            // controlPad with auto generated id
            controlPad = retrievedControlPad

        } ?: fail("ControlPad should not be null here")

        var controlPadItem = ControlPadItem(
            itemIdentifier = "testItem",
            controlPadId = controlPad.id,
            offsetX = 0f,
            offsetY = 0f,
            scale = 100f,
            rotation = 0f,
            itemType = ItemType.SWITCH,
            properties = "{}"
        )

        val newControlPadItemId = controlPadItemDao.insert(controlPadItem)
        controlPadItemDao.getById(newControlPadItemId)?.let { retrievedControlPadItem ->
            // controlPadItem with auto generated id
            controlPadItem = retrievedControlPadItem

        } ?: fail("ControlPadItem should not be null here")

        controlPadItemDao.getById(newControlPadItemId)?.let { retrievedControlPadItem ->
            assertEquals(controlPadItem, retrievedControlPadItem)
        }


    }

    @Test
    fun `ControlPadItems should be deleted when controlPad is deleted`() = runTest{

        var controlPad = ControlPad(
            name = "test controller",
            orientation = Orientation.LANDSCAPE
        )
        val newControlPadId = controlPadDao.insert(controlPad)

        controlPadDao.getById(newControlPadId)?.let { retrievedControlPad ->
            // controlPad with auto generated id
            controlPad = retrievedControlPad

        } ?: fail("ControlPad should not be null here")

        var controlPadItem = ControlPadItem(
            itemIdentifier = "testItem",
            controlPadId = controlPad.id,
            offsetX = 0f,
            offsetY = 0f,
            scale = 100f,
            rotation = 0f,
            itemType = ItemType.SWITCH,
            properties = "{}"
        )

        val newControlPadItemId = controlPadItemDao.insert(controlPadItem)

        controlPadItemDao.getById(newControlPadItemId)?.let { retrievedControlPadItem ->
            // controlPadItem with auto generated id
            controlPadItem = retrievedControlPadItem

        } ?: fail("ControlPadItem should not be null here")


        var controlPadItem2 = ControlPadItem(
            itemIdentifier = "testItem",
            controlPadId = controlPad.id,
            offsetX = 0f,
            offsetY = 0f,
            scale = 100f,
            rotation = 0f,
            itemType = ItemType.SWITCH,
            properties = "{}"
        )

        val newControlPadItemId2 = controlPadItemDao.insert(controlPadItem2)
        controlPadItemDao.getById(newControlPadItemId2)?.let { retrievedControlPadItem2 ->
            // controlPadItem with auto generated id
            controlPadItem2 = retrievedControlPadItem2

        } ?: fail("ControlPadItem should not be null here")



        // Delete a controlPad
        // This should also delete the controlPadItem associated with it
        controlPadDao.delete(controlPad)

        // ControlPadItems should be deleted when its parent controlPad is deleted
        assertNull(controlPadItemDao.getById(controlPadItem.id))
        assertNull(controlPadItemDao.getById(controlPadItem2.id))


    }

    @Test
    fun `test delete`() = runTest{

        var controlPadItem = ControlPadItem(
            itemIdentifier = "testItem",
            controlPadId = controlPadDao.insert(
                ControlPad(
                    name = "test controller",
                    orientation = Orientation.LANDSCAPE
                )
            ),
            offsetX = 0f,
            offsetY = 0f,
            scale = 100f,
            rotation = 0f,
            itemType = ItemType.SWITCH,
            properties = "{}"
        )

        val newControlPadItemId = controlPadItemDao.insert(controlPadItem)

        controlPadItemDao.getById(newControlPadItemId)?.let { retrievedControlPadItem ->
            // controlPadItem with auto generated id
            controlPadItem = retrievedControlPadItem

        } ?: fail("ControlPadItem should not be null here")

        controlPadItemDao.delete(controlPadItem)

        assertNull(controlPadItemDao.getById(newControlPadItemId))


    }

    @Test
    fun `test update`() = runTest{

        var controlPadItem = ControlPadItem(
            itemIdentifier = "testItem",
            controlPadId = controlPadDao.insert(
                ControlPad(
                    name = "test controller",
                    orientation = Orientation.LANDSCAPE
                )
            ),
            offsetX = 0f,
            offsetY = 0f,
            scale = 100f,
            rotation = 0f,
            itemType = ItemType.SWITCH,
            properties = "{}"
        )

        val newControlPadItemId = controlPadItemDao.insert(controlPadItem)

        controlPadItemDao.getById(newControlPadItemId)?.let { retrievedControlPadItem ->
            // controlPadItem with auto generated id
            controlPadItem = retrievedControlPadItem

        } ?: fail("ControlPadItem should not be null here")

        val updatedControlPadItem = controlPadItem.copy(
            itemIdentifier = "updatedTestItem",
            offsetX = 100f,
            offsetY = 100f,
            scale = 200f,
            rotation = 90f,
            properties = "{}"
        )

        controlPadItemDao.update(updatedControlPadItem)

        controlPadItemDao.getById(updatedControlPadItem.id)?.let { retrievedUpdatedControlPadItem ->

            assertEquals(updatedControlPadItem, retrievedUpdatedControlPadItem)

        } ?: fail("ControlPadItem should not be null here")


    }

}