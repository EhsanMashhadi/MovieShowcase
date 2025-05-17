package software.ehsan.movieshowcase.core.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DefaultDispatcherProvider: DispatcherProvider {
    override val default: CoroutineDispatcher get() = Dispatchers.Default
    override val io: CoroutineDispatcher get() = Dispatchers.IO
    override val main: CoroutineDispatcher get() = Dispatchers.Main
    override val unconfined: CoroutineDispatcher get() = Dispatchers.Unconfined
}