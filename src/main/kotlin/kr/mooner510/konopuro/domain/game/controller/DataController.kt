package kr.mooner510.konopuro.domain.game.controller

import com.google.api.services.sheets.v4.Sheets
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.mooner510.konopuro.domain.game._preset.DefaultCardType
import kr.mooner510.konopuro.domain.game._preset.StudentCardType
import kr.mooner510.konopuro.domain.game._preset.TierType
import kr.mooner510.konopuro.domain.game.component.GoogleSpreadSheetComponent
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@Tag(name = "Resource", description = "리소스 API")
@RestController
@RequestMapping("/api/resource")
class DataController(
    private val sheets: Sheets
) {
    companion object {
        private val logger = LoggerFactory.getLogger(DataController::class.java)

        private lateinit var lastTierUpdateTime: LocalDateTime
        private lateinit var lastDefaultUpdateTime: LocalDateTime

        lateinit var tierUpdater: () -> Unit
            private set

        lateinit var defaultUpdater: () -> Unit
            private set
    }

    init {
        lastTierUpdateTime = LocalDateTime.now().minusHours(1)
        lastDefaultUpdateTime = LocalDateTime.now().minusHours(1)

        tierUpdater = {
            if (lastTierUpdateTime.plusMinutes(10) <= LocalDateTime.now()) {
                logger.info("Spread Sheet Update Successfully: TierData")

                @Suppress("UNCHECKED_CAST") val values: List<List<String>> = sheets.spreadsheets().values()
                    .get(GoogleSpreadSheetComponent.SHEET_ID, "TierData")
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
        }
        tierUpdater()

        defaultUpdater = {
            if (lastDefaultUpdateTime.plusMinutes(10) <= LocalDateTime.now()) {
                logger.info("Spread Sheet Update Successfully: DefaultCardData")

                @Suppress("UNCHECKED_CAST") val values: List<List<String>> = sheets.spreadsheets().values()
                    .get(GoogleSpreadSheetComponent.SHEET_ID, "DefaultCardData")
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
        }
        defaultUpdater()
    }

    @Suppress("UNCHECKED_CAST")
    @Operation(summary = "버전", description = "버전 데이터를 가져옵니다.")
    @GetMapping("/version", produces = ["application/json"])
    fun getVersion(): String {
        val version = (sheets.spreadsheets().values()
            .get(GoogleSpreadSheetComponent.SHEET_ID, "version")
            .execute()["values"] as List<List<String>>)[0][0]
        return JSONObject().put("version", version).toString()
    }

    @Suppress("UNCHECKED_CAST")
    @Operation(summary = "티어 데이터 조회", description = "티어에 대한 데이터를 가져옵니다.")
    @GetMapping("/tier", produces = ["application/json"])
    fun getTierData(): String {
        val json = JSONObject()

        val values: List<List<String>> = sheets.spreadsheets().values()
            .get(GoogleSpreadSheetComponent.SHEET_ID, "TierData")
            .execute()["values"] as List<List<String>>

        for (value in values) {
            json.put(value[0], JSONObject().put("name", value[1]).put("time", value[2].toInt()).put("description", value[3]))
        }

        return json.toString()
    }

    @Suppress("UNCHECKED_CAST")
    @Operation(summary = "패시브 데이터 조회", description = "패시브에 대한 데이터를 가져옵니다.")
    @GetMapping("/passive", produces = ["application/json"])
    fun getPassiveData(): String {
        val json = JSONObject()

        val values: List<List<String>> = sheets.spreadsheets().values()
            .get(GoogleSpreadSheetComponent.SHEET_ID, "PassiveData")
            .execute()["values"] as List<List<String>>

        for (value in values) {
            json.put(value[0], JSONObject().put("name", value[1]).put("description", value[2]))
        }

        return json.toString()
    }

    @Suppress("UNCHECKED_CAST")
    @Operation(summary = "기본 카드 데이터 조회", description = "기본 카드에 대한 데이터를 가져옵니다.")
    @GetMapping("/default-card", produces = ["application/json"])
    fun getDefaultCardData(): String {
        val json = JSONObject()

        val values: List<List<String>> = sheets.spreadsheets().values()
            .get(GoogleSpreadSheetComponent.SHEET_ID, "DefaultCardData")
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

        return json.toString()
    }

    @Suppress("UNCHECKED_CAST")
    @Operation(summary = "인물 카드 데이터 조회", description = "인물 카드에 대한 데이터를 가져옵니다.")
    @GetMapping("/student-card", produces = ["application/json"])
    fun getCardData(): String {
        val json = JSONObject()

        val values: List<List<String>> = sheets.spreadsheets().values()
            .get(GoogleSpreadSheetComponent.SHEET_ID, "StudentCardData")
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

        return json.toString()
    }
}
