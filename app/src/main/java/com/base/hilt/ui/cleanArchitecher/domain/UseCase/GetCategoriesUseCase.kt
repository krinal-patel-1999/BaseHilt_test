import com.base.hilt.ui.cleanArchitecher.domain.model.Category
import com.base.hilt.ui.cleanArchitecher.domain.repository.CategoryRepository
import com.base.hilt.utils.ResponseHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    operator fun invoke(): Flow<ResponseHandler<List<Category>>> = flow {
        try {
            emit(ResponseHandler.Loading())

            val response: Response<Category> = repository.getCategories()

            if (response.isSuccessful) {
                val categoryList: List<Category> = listOfNotNull(response.body()) // Convert single item to list
                emit(ResponseHandler.Success(categoryList))
            } else {
                emit(ResponseHandler.Error("Failed to fetch categories"))
            }

        } catch (e: Exception) {
            emit(ResponseHandler.Error("An unexpected error occurred"))
        }
    }
}