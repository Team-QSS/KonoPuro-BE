package kr.mooner510.konopuro.domain.game._preset

import kr.mooner510.konopuro.domain.game.controller.DataController
import kr.mooner510.konopuro.domain.game.data.card.response.DefaultDataResponse
import kr.mooner510.konopuro.domain.game.data.card.types.CardType
import java.util.*

enum class DefaultCardType(
    val cardType: CardType,
    val passives: EnumSet<PassiveType> = EnumSet.noneOf(PassiveType::class.java)
) {
    OnlyPower(CardType.Field),
    UltimatePower(CardType.Field),
    Music(CardType.Field),
    DawnCoding(CardType.Field),
    Passion(CardType.Field),

    CleanCode(CardType.Tool, EnumSet.of(PassiveType.CleanCode)),
    Refectoring(CardType.Event, EnumSet.of(PassiveType.Refectoring)),
    JustRealize(CardType.Activity, EnumSet.of(PassiveType.JustRealize)),
    IndustrialSpy(CardType.Activity, EnumSet.of(PassiveType.IndustrialSpy)),
    ;

    companion object {
        private var tierOtherListImpl: List<DefaultCardType>? = null
        private var tier3ListImpl: List<DefaultCardType>? = null

        val tierOtherList: List<DefaultCardType>
            get() {
                if (tierOtherListImpl == null) tierOtherListImpl = DefaultCardType.entries.filter { it.tier != 3 && it.cardType != CardType.Field }
                return tierOtherListImpl!!
            }

        val tier3List: List<DefaultCardType>
            get() {
                if (tier3ListImpl == null) tier3ListImpl = DefaultCardType.entries.filter { it.tier == 3 && it.cardType != CardType.Field }
                return tierOtherListImpl!!
            }


        private val timeMap = hashMapOf<DefaultCardType, Int>()
        private val tierMap = hashMapOf<DefaultCardType, Int>()

        fun setTime(type: DefaultCardType, time: Int) {
            timeMap[type] = time
        }

        fun getTime(type: DefaultCardType): Int {
            DataController.defaultUpdater()
            return timeMap.getOrDefault(type, 0)
        }

        fun setTier(type: DefaultCardType, tier: Int) {
            tierMap[type] = tier
        }

        fun getTier(type: DefaultCardType): Int {
            DataController.defaultUpdater()
            return tierMap.getOrDefault(type, 1)
        }
    }

    val time: Int
        get() {
            return getTime(this)
        }

    val tier: Int
        get() {
            return getTier(this)
        }

    fun toResponse(): DefaultDataResponse {
        return DefaultDataResponse(
            toString(),
            cardType,
            tier
        )
    }
}
