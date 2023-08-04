import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import model.BirdImage

fun JsonObject.toModel(): BirdImage {
    val category = this["category"]?.jsonPrimitive?.content ?: ""
    val path = this["path"]?.jsonPrimitive?.content ?: ""
    val author = this["author"]?.jsonPrimitive?.content ?: ""
    return BirdImage(author, category, path)
}