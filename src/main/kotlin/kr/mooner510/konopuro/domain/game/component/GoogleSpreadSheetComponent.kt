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

    @Bean
    fun getService(): Sheets {
        val transport = GoogleNetHttpTransport.newTrustedTransport()
        return Sheets.Builder(transport, JSON_FACTORY, getCredentials(transport)).setApplicationName(APPLICATION_NAME).build()
    }
}
