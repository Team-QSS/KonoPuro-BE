package kr.mooner510.konopuro.domain.game.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.mooner510.konopuro.domain.game.component.GoogleSpreadSheetComponent
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Resource", description = "리소스 API")
@RestController
@RequestMapping("/api/resource")
class DataController {
    @Operation(summary = "새로고침", description = "모든 데이터를 새로고칩니다.")
    @GetMapping("/refresh", produces = ["application/json"])
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun refresh() {
        GoogleSpreadSheetComponent.instance.versionRunner.refresh()
        GoogleSpreadSheetComponent.instance.tierDataRunner.refresh()
        GoogleSpreadSheetComponent.instance.passiveDataRunner.refresh()
        GoogleSpreadSheetComponent.instance.defaultCardDataRunner.refresh()
        GoogleSpreadSheetComponent.instance.studentCardDataRunner.refresh()
    }

    @Operation(summary = "버전", description = "버전 데이터를 가져옵니다.")
    @GetMapping("/version", produces = ["application/json"])
    fun getVersion(): String {
        return GoogleSpreadSheetComponent.instance.versionRunner.execute()
    }

    @Operation(summary = "티어 데이터 조회", description = "티어에 대한 데이터를 가져옵니다.")
    @GetMapping("/tier", produces = ["application/json"])
    fun getTierData(): String {
        return GoogleSpreadSheetComponent.instance.tierDataRunner.execute()
    }

    @Operation(summary = "패시브 데이터 조회", description = "패시브에 대한 데이터를 가져옵니다.")
    @GetMapping("/passive", produces = ["application/json"])
    fun getPassiveData(): String {
        return GoogleSpreadSheetComponent.instance.passiveDataRunner.execute()
    }

    @Operation(summary = "기본 카드 데이터 조회", description = "기본 카드에 대한 데이터를 가져옵니다.")
    @GetMapping("/default-card", produces = ["application/json"])
    fun getDefaultCardData(): String {
        return GoogleSpreadSheetComponent.instance.defaultCardDataRunner.execute()
    }

    @Operation(summary = "인물 카드 데이터 조회", description = "인물 카드에 대한 데이터를 가져옵니다.")
    @GetMapping("/student-card", produces = ["application/json"])
    fun getCardData(): String {
        return GoogleSpreadSheetComponent.instance.studentCardDataRunner.execute()
    }
}
