package kr.mooner510.konopuro.domain.game.controller

import com.google.api.services.sheets.v4.Sheets
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.mooner510.konopuro.domain.game._preset.PassiveType
import kr.mooner510.konopuro.domain.game._preset.TierType
import kr.mooner510.konopuro.domain.game.component.GoogleSpreadSheetComponent
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Resource", description = "리소스 API")
@RestController
@RequestMapping("/api/resource")
class DataController(
    private val sheets: Sheets
) {
    companion object {
        private val logger = LoggerFactory.getLogger(DataController::class.java)
    }

    @Suppress("UNCHECKED_CAST")
    @Operation(summary = "티어 데이터 조회", description = "티어에 대한 데이터를 가져옵니다.")
    @GetMapping("/tier")
    fun getTierData(): String {
        val json = JSONObject()

        val values: List<List<Any>> = sheets.spreadsheets().values()
            .get(GoogleSpreadSheetComponent.SHEET_ID, "TierData")
            .execute()["values"] as List<List<Any>>

        for (value in values) {
            json.put(value[0] as String, JSONObject().put("name", values[1]).put("time", value[2]).put("description", values[3]))
        }

        return json.toString()
    }

    @Suppress("UNCHECKED_CAST")
    @Operation(summary = "패시브 데이터 조회", description = "패시브에 대한 데이터를 가져옵니다.")
    @GetMapping("/passive")
    fun getPassiveData(): String {
        val json = JSONObject()

        val values: List<List<Any>> = sheets.spreadsheets().values()
            .get(GoogleSpreadSheetComponent.SHEET_ID, "PassiveData")
            .execute()["values"] as List<List<Any>>

        for (value in values) {
            json.put(value[0] as String, JSONObject().put("name", values[1]).put("description", values[2]))
        }

        return json.toString()
    }
}
