package com.github.umer0586.droidpad

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

// see : https://developer.android.com/kotlin/coroutines/test#setting-main-dispatcher

//If the Main dispatcher has been replaced with a TestDispatcher,
// any newly-created TestDispatchers will automatically use the scheduler from the Main dispatcher,
// including the StandardTestDispatcher created by runTest if no other dispatcher is passed to it.
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher(),
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}