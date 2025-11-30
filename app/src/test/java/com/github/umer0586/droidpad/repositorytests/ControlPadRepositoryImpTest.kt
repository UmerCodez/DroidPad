package com.github.umer0586.droidpad.repositorytests

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.github.umer0586.droidpad.MainDispatcherRule
import com.github.umer0586.droidpad.data.database.AppDatabase
import com.github.umer0586.droidpad.data.database.entities.ControlPad
import com.github.umer0586.droidpad.data.database.entities.ControlPadItem
import com.github.umer0586.droidpad.data.database.entities.ItemType
import com.github.umer0586.droidpad.data.database.entities.Orientation
import com.github.umer0586.droidpad.data.repositories.ControlPadItemRepository
import com.github.umer0586.droidpad.data.repositories.ControlPadRepository
import com.github.umer0586.droidpad.data.repositoriesimp.ControlPadItemRepositoryImp
import com.github.umer0586.droidpad.data.repositoriesimp.ControlPadRepositoryImp
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException


@RunWith(RobolectricTestRunner::class)
class ControlPadRepositoryImpTest {


    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var db: AppDatabase
    private lateinit var controlPadRepository: ControlPadRepository
    private lateinit var controlPadItemRepository: ControlPadItemRepository

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()

        controlPadRepository = ControlPadRepositoryImp(db)
        controlPadItemRepository = ControlPadItemRepositoryImp(db)

    }

    @After
    @Throws(IOException::class)
    fun teardown() {
        db.close()
    }

    @Test
    fun `test retrieval of controlPadItems of specific controlPad`() = runTest {

        val controlPadItemDao = db.controlPadItemDao()

        val controlPadSample = ControlPad(
            name = "test controller",
            orientation = Orientation.LANDSCAPE
        )

        val newId = controlPadRepository.saveControlPad(controlPadSample)

        val controlPadItemsSamples = listOf(
            ControlPadItem(
                id = 100,
                controlPadId = newId,
                itemIdentifier = "btn",
                itemType = ItemType.BUTTON,
            ),
            ControlPadItem(
                id = 101,
                controlPadId = newId,
                itemIdentifier = "switch",
                itemType = ItemType.SWITCH,
            ),
            ControlPadItem(
                id = 102,
                controlPadId = newId,
                itemIdentifier = "slider",
                itemType = ItemType.SLIDER,
            )
        )

        controlPadItemsSamples.forEach {
            controlPadItemRepository.save(it)
        }

        controlPadRepository.getControlPadById(newId)?.also { controlPad ->
            val controlPadItems = controlPadRepository.getControlPadItemsOf(controlPad)
            
            assertEquals(controlPadItemsSamples,controlPadItems )
        }

    }


}