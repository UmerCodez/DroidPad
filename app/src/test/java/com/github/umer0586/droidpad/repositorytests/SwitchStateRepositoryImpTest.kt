package com.github.umer0586.droidpad.repositorytests

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.github.umer0586.droidpad.MainDispatcherRule
import com.github.umer0586.droidpad.data.database.AppDatabase
import com.github.umer0586.droidpad.data.database.entities.ControlPad
import com.github.umer0586.droidpad.data.database.entities.ControlPadItem
import com.github.umer0586.droidpad.data.database.entities.ItemType
import com.github.umer0586.droidpad.data.database.entities.Orientation
import com.github.umer0586.droidpad.data.database.entities.SwitchState
import com.github.umer0586.droidpad.data.repositories.ControlPadItemRepository
import com.github.umer0586.droidpad.data.repositories.ControlPadRepository
import com.github.umer0586.droidpad.data.repositories.SwitchStateRepository
import com.github.umer0586.droidpad.data.repositoriesimp.ControlPadItemRepositoryImp
import com.github.umer0586.droidpad.data.repositoriesimp.ControlPadRepositoryImp
import com.github.umer0586.droidpad.data.repositoriesimp.SwitchStateRepositoryImp
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
class SwitchStateRepositoryImpTest {


    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var db: AppDatabase
    private lateinit var switchStateRepository: SwitchStateRepository
    private lateinit var controlPadRepository: ControlPadRepository
    private lateinit var controlPadItemRepository: ControlPadItemRepository

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()

        switchStateRepository = SwitchStateRepositoryImp(db)
        controlPadRepository = ControlPadRepositoryImp(db)
        controlPadItemRepository = ControlPadItemRepositoryImp(db)

    }

    @After
    @Throws(IOException::class)
    fun teardown() {
        db.close()
    }

    @Test
    fun `get all switch states of control pad`() = runTest {

        val controlPad = ControlPad(
            id = 10,
            name = "test controller",
            orientation = Orientation.LANDSCAPE
        )

        val controlPadItems = listOf(
            ControlPadItem(
                id = 1,
                controlPadId = controlPad.id,
                itemIdentifier = "s1",
                itemType = ItemType.SWITCH,
            ),
            ControlPadItem(
                id = 2,
                controlPadId = controlPad.id,
                itemIdentifier = "s2",
                itemType = ItemType.SWITCH,
            ),
            ControlPadItem(
                id = 3,
                controlPadId = controlPad.id,
                itemIdentifier = "s3",
                itemType = ItemType.SWITCH,
            )
        )

        val switchStates = listOf(
            SwitchState(
                id = 1,
                controlPadId = controlPad.id,
                controlPadItemId = controlPadItems[0].id,
                checked = true
            ),
            SwitchState(
                id = 2,
                controlPadId = controlPad.id,
                controlPadItemId = controlPadItems[1].id,
                checked = false
            ),
            SwitchState(
                id = 3,
                controlPadId = controlPad.id,
                controlPadItemId = controlPadItems[2].id,
                checked = true
            )
        )

        controlPadRepository.saveControlPad(controlPad)

        controlPadItems.forEach {
            controlPadItemRepository.save(it)
        }

        switchStates.forEach {
            switchStateRepository.saveSwitchState(it)
        }

        val controlPadSwitchStates = switchStateRepository.getAllSwitchStatesForControlPad(controlPad.id)
        Assert.assertEquals(switchStates, controlPadSwitchStates)
    }
}