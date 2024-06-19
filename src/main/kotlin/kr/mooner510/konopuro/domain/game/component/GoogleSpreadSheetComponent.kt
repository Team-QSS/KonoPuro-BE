package kr.mooner510.konopuro.domain.game.component

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import kr.mooner510.konopuro.domain.game._preset.DefaultCardType
import kr.mooner510.konopuro.domain.game._preset.StudentCardType
import kr.mooner510.konopuro.domain.game._preset.TierType
import kr.mooner510.konopuro.domain.game.controller.DataController
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileReader
import java.time.LocalDateTime
import java.util.*


@Suppress("UNCHECKED_CAST")
@Component
class GoogleSpreadSheetComponent {
    companion object {
        private const val CREDENTIALS_FILE_PATH =
            "src/main/resources/client_secret_668641327982-pq7s44jscpcc5b6drkpn9r2lco8s3r45.apps.googleusercontent.com.json"
        private const val APPLICATION_NAME = "konopuro"
        private val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()
        private val SCOPES: List<String> = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY)
        const val SHEET_ID = "12B2zVmVX0SyE38pXY_9IwsZlZ0yOv0slFPn0ZdhdiIs"

        private val logger = LoggerFactory.getLogger(DataController::class.java)

        lateinit var instance: GoogleSpreadSheetComponent
    }

    init {
        @Suppress("LeakingThis")
        instance = this
    }

    class DelayRunner<T : Any>(
        private val delay: Long,
        private val execution: () -> T
    ) {
        private var lastUpdateTime: LocalDateTime = LocalDateTime.now().minusHours(-delay * 2)
        private lateinit var cache: T

        fun execute(): T {
            if (lastUpdateTime.plusHours(delay) > LocalDateTime.now()) return cache
            lastUpdateTime = LocalDateTime.now()
            cache = execution()
            return cache
        }

        fun refresh(): T {
            lastUpdateTime = LocalDateTime.now()
            cache = execution()
            return cache
        }
    }

    private fun getCredentials(transport: NetHttpTransport): Credential {
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, FileReader(File(CREDENTIALS_FILE_PATH)))

        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(transport, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(FileDataStoreFactory(File("tokens")))
            .setAccessType("offline")
            .build()
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }

    val sheets: Sheets =
        Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, getCredentials(GoogleNetHttpTransport.newTrustedTransport()))
            .setApplicationName(APPLICATION_NAME).build()

    val tierRunner = DelayRunner(12) {
        logger.info("Spread Sheet Update Successfully: TierType")

        @Suppress("UNCHECKED_CAST") val values: List<List<String>> = sheets.spreadsheets().values()
            .get(SHEET_ID, "TierData")
            .execute()["values"] as List<List<String>>

        for (value in values) {
            try {
                val type = TierType.valueOf(value[0])
                TierType.setTime(type, value[2].toInt())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val defaultRunner = DelayRunner(12) {
        logger.info("Spread Sheet Update Successfully: DefaultCardType")

        @Suppress("UNCHECKED_CAST") val values: List<List<String>> = sheets.spreadsheets().values()
            .get(SHEET_ID, "DefaultCardData")
            .execute()["values"] as List<List<String>>

        for (value in values) {
            try {
                val type = DefaultCardType.valueOf(value[0])
                DefaultCardType.setTier(type, value[1].toInt())
                DefaultCardType.setTime(type, value[3].toInt())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val versionRunner = DelayRunner(12) {
        logger.info("Spread Sheet Update Successfully: Version")

        val version = (sheets.spreadsheets().values()
            .get(SHEET_ID, "version")
            .execute()["values"] as List<List<String>>)[0][0]
        JSONObject().put("version", version).toString()
    }

    val tierDataRunner = DelayRunner(12) {
        logger.info("Spread Sheet Update Successfully: TierData")

        val json = JSONObject()

        val values: List<List<String>> = sheets.spreadsheets().values()
            .get(SHEET_ID, "TierData")
            .execute()["values"] as List<List<String>>

        for (value in values) {
            json.put(value[0], JSONObject().put("name", value[1]).put("time", value[2].toInt()).put("description", value[3]))
        }

        json.toString()
    }

    val passiveDataRunner = DelayRunner(12) {
        logger.info("Spread Sheet Update Successfully: PassiveData")

        val json = JSONObject()

        val values: List<List<String>> = sheets.spreadsheets().values()
            .get(SHEET_ID, "PassiveData")
            .execute()["values"] as List<List<String>>

        for (value in values) {
            json.put(value[0], JSONObject().put("name", value[1]).put("description", value[2]))
        }

        json.toString()
    }

    val defaultCardDataRunner = DelayRunner(12) {
        logger.info("Spread Sheet Update Successfully: DefaultCardData")

        val json = JSONObject()

        val values: List<List<String>> = sheets.spreadsheets().values()
            .get(SHEET_ID, "DefaultCardData")
            .execute()["values"] as List<List<String>>

        for (value in values) {
            val obj = JSONObject()
                .put("tier", value[1].toInt())
                .put("name", value[2])
                .put("time", value[3].toInt())
                .put("description", value[4])

            try {
                val type = DefaultCardType.valueOf(value[0])
                obj.put("cardType", type.cardType.toString())
            } catch (_: IllegalArgumentException) {
            }

            json.put(value[0], obj)
        }

        json.toString()
    }

    val studentCardDataRunner = DelayRunner(12) {
        logger.info("Spread Sheet Update Successfully: StudentCardData")

        val json = JSONObject()

        val values: List<List<String>> = sheets.spreadsheets().values()
            .get(SHEET_ID, "StudentCardData")
            .execute()["values"] as List<List<String>>

        for (value in values) {
            val obj = JSONObject()
                .put("name", value[1])
                .put("idea", value[2])
                .put("motive", value[3])

            try {
                val type = StudentCardType.valueOf(value[0])
                obj.put("majors", type.major.sortedBy { it.ordinal }.toList())
                    .put("defaultPassives", type.passive.sortedBy { it.ordinal }.toList())
                    .put("defaultTier", type.tier.toString())
                    .put("second", type.secondTier.sortedBy { it.ordinal }.toList())
                    .put("third", type.thirdPassive.sortedBy { it.ordinal }.toList())
                    .put("forth", type.forthTier.sortedBy { it.ordinal }.toList())
            } catch (_: IllegalArgumentException) {
            }

            json.put(value[0], obj)
        }

        json.toString()
    }
}
