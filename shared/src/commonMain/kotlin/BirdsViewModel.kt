import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import model.BirdImage
import remote.BirdService

data class BirdsUiState(
    val images: List<BirdImage> = emptyList(),
    val selectedCategory: String? = null
){
    val categories = images.map { it.category }.toSet()
    val selectedImages = images.filter { it.category == selectedCategory }
}

class BirdsViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(BirdsUiState())

    val uiState = _uiState.asStateFlow()

    private val service = BirdService.create()

//    private val httpClient = HttpClient {
//        install(ContentNegotiation) {
//            json()
//        }
//    }

    init {
        updateImages()
    }

//    override fun onCleared() {
//        httpClient.close()
//    }

    fun selectCategory(category: String){
        _uiState.update {
            it.copy(selectedCategory = category)
        }
    }

    private fun updateImages(){
        viewModelScope.launch {
            val images = service.getImages() 
            _uiState.update {
                it.copy(images = images)
            }
        }
    }

}