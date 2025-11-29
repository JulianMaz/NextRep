import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nextrep.data.exercise.ExerciseDao
import com.example.nextrep.viewmodels.ExercisesViewModel

class ExercisesViewModelFactory(private val dao: ExerciseDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExercisesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExercisesViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
