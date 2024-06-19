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
import kr.mooner510.konopuro.domain.game._preset.TierType
import kr.mooner510.konopuro.domain.game.controller.DataController
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileReader
import java.util.*


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

    private val tierRunner = DataController.DelayRunner(12) {
        logger.info("Spread Sheet Update Successfully: TierData")

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

    private val defaultRunner = DataController.DelayRunner(12) {
        logger.info("Spread Sheet Update Successfully: DefaultCardData")

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

    private val versionRunner =
}
