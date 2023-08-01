import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.call.receive
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import model.BirdImage

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


    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    init {
        updateImages()
    }

    override fun onCleared() {
        httpClient.close()
    }

    fun selectCategory(category: String){
        _uiState.update {
            it.copy(selectedCategory = category)
        }
    }

    fun updateImages(){
        viewModelScope.launch {
            val images = getImages()
            _uiState.update {
                it.copy(images = images)
            }
        }
    }


    private suspend fun getImages(): List<BirdImage> {
//        val images: List<BirdImage> = httpClient
//            .get("https://sebastianaigner.github.io/demo-image-api/pictures.json")
//            .body()
//
//        return images

        return try {
            val response: HttpResponse = httpClient.get("https://sebastianaigner.github.io/demo-image-api/pictures.json")
            if (response.status.isSuccess()) {
                val jsonArray: JsonArray = response.body()
                val images: List<BirdImage> = jsonArray.map { it.jsonObject.toModel() }
                images
            } else {
                // Handle error cases here, such as returning an empty list or throwing an exception.
                throw Exception("Failed to fetch images. Status code: ${response.status}")
            }
        } catch (e: Exception) {
            // Handle the exception here, e.g., logging the error or returning an empty list.
            e.printStackTrace()
            emptyList()
        }
    }

    private fun JsonObject.toModel(): BirdImage {
        val category = this["category"]?.jsonPrimitive?.content ?: ""
        val path = this["path"]?.jsonPrimitive?.content ?: ""
        val author = this["author"]?.jsonPrimitive?.content ?: ""
        return BirdImage(author, category, path)
    }


}