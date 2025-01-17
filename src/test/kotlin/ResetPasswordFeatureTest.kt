import io.cucumber.java8.En
import io.cucumber.junit.platform.engine.Cucumber
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.jsonObject
import kotlin.test.assertEquals

@Cucumber
class ResetPasswordFeatureTest : En {
    private val client = HttpClient() {
        // Устанавливаем ContentNegotiation для JSON
        install(ContentNegotiation) {
            json(Json { prettyPrint = true; isLenient = true })
        }
    }
    private lateinit var response: HttpResponse

    init {
        // Шаг: Отправка запроса на сброс пароля
        Given("[RESET] User sends reset password {string} request to {string}") { method: String, endpoint: String ->
            runBlocking {
                val requestBody = mapOf(
                    "email" to "dmd234567@gmail.com",
                    "oldPassword" to "123123"
                )

                // Преобразуем тело запроса в JSON
                val jsonBody = Json.encodeToString(requestBody)

                response = when (method.uppercase()) {
                    "POST" -> client.post(endpoint) {
                        contentType(ContentType.Application.Json)
                        setBody(jsonBody)
                    }
                    else -> throw IllegalArgumentException("Unsupported method: $method")
                }
            }
        }

        // Шаг: Проверка кода ответа
        Then("[RESET] The response on {string} code should be {int}") { endpoint: String, statusCode: Int ->
            assertEquals(statusCode, response.status.value, "Unexpected status code for $endpoint")
        }

        // Шаг: Проверка содержимого JSON-ответа
        And("[RESET] The response on {string} should match JSON:") { endpoint: String, expectedJson: String ->
            runBlocking {
                // Получаем тело ответа в виде строки и преобразуем его в JsonObject
                val actualJson = Json.parseToJsonElement(response.bodyAsText()).jsonObject
                val expectedJsonObject = Json.parseToJsonElement(expectedJson).jsonObject

                // Сравниваем реальные и ожидаемые JSON объекты
                assertEquals(
                    actualJson,
                    expectedJsonObject,
                    "Response JSON on $endpoint does not match expected. Actual: $actualJson, Expected: $expectedJsonObject"
                )
            }
        }

        // Шаг: Отправка запроса для подтверждения сброса пароля
        Given("[RESET] User sends reset password verification {string} request to {string}") { method: String, endpoint: String ->
            runBlocking {
                val requestBody = mapOf(
                    "email" to "dmd234567@gmail.com",
                    "code" to "123456",
                    "newPassword" to "123"
                )

                // Преобразуем тело запроса в JSON
                val jsonBody = Json.encodeToString(requestBody)

                response = when (method.uppercase()) {
                    "POST" -> client.post(endpoint) {
                        contentType(ContentType.Application.Json)
                        setBody(jsonBody)
                    }
                    else -> throw IllegalArgumentException("Unsupported method: $method")
                }
            }
        }
    }
}
