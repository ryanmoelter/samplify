package co.moelten.samplify.model

import co.moelten.samplify.model.Loadable.Error
import co.moelten.samplify.model.Loadable.Loading
import co.moelten.samplify.model.Loadable.Success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

sealed class Loadable<out Subject> {
  data class Success<out Subject>(val value: Subject) : Loadable<Subject>()
  class Loading<Subject> : Loadable<Subject>()
  data class Error<Subject>(val throwable: Throwable) : Loadable<Subject>()
}

fun <OriginalSubject, NewSubject> Flow<Loadable<OriginalSubject>>.mapLoadableValue(
  map: suspend (OriginalSubject) -> NewSubject
): Flow<Loadable<NewSubject>> = map { loadable ->
  when (loadable) {
    is Success -> Success<NewSubject>(map(loadable.value))
    is Loading -> Loading<NewSubject>()
    is Error -> Error<NewSubject>(loadable.throwable)
  }
}
