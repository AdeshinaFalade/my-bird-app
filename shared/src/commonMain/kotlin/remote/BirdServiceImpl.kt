package remote

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject
import model.BirdImage
import toModel

class BirdServiceImpl(
    private val client: HttpClient
): BirdService {

    override suspend fun getImages(): List<BirdImage> {
        return try {
            val response: HttpResponse = client.get("${HttpRoutes.BASE_URL}pictures.json")
            val jsonArray: JsonArray = response.body()
            val images: List<BirdImage> = jsonArray.map { it.jsonObject.toModel() }
            images
        } catch (e: RedirectResponseException){
            // 3xx - responses
            println("Error: ${ e.response.status.description}")
            emptyList()
        } catch (e: ClientRequestException){
            // 4xx - responses
            println("Error: ${ e.response.status.description}")
            emptyList()
        } catch (e: ServerResponseException){
            // 5xx - responses
            println("Error: ${ e.response.status.description}")
            emptyList()
        } catch (e: Exception){
            // General exception
            println("Error: ${ e.message}")
            emptyList()
        }
    }
}