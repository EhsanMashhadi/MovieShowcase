package software.ehsan.movieshowcase.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import software.ehsan.movieshowcase.core.util.DispatcherProvider

@OptIn(ExperimentalCoroutinesApi::class)
class TestDispatcherProvider(private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()) :
    DispatcherProvider {
    override val default: CoroutineDispatcher get() = testDispatcher
    override val io: CoroutineDispatcher get() = testDispatcher
    override val main: CoroutineDispatcher get() = testDispatcher
    override val unconfined: CoroutineDispatcher get() = testDispatcher
}