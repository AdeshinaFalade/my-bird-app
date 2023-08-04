package remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import model.BirdImage

interface BirdService {
    suspend fun getImages(): List<BirdImage>

    companion object {
        fun create(): BirdService {
            return BirdServiceImpl(
                client = HttpClient{
                    install(ContentNegotiation) {
                        json(Json {
                            ignoreUnknownKeys = true
                            prettyPrint = true
                            isLenient = true

                        })
                    }

                    install(Logging){
                        level = LogLevel.ALL
                    }


                }
            )
        }
    }
}