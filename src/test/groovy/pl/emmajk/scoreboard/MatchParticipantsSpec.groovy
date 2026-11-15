package pl.emmajk.scoreboard

import spock.lang.Specification

class MatchParticipantsSpec extends Specification {

    def "of - dataset tests - invalid names"() {
        when:
        MatchParticipants.of(home, away)

        then:
        thrown(exception)

        where:
        [home, away, exception] << InvalidTeamNames.PAIRS
    }

    def "of - dataset tests - normalised keys"() {
        expect:
        MatchParticipants.of(name, "Other").homeKey() == key

        where:
        name              | key
        "Spain"           | "spain"
        "SPAIN"           | "spain"
        "gErmany"         | "germany"
        "Côte d'Ivoire"   | "cote d'ivoire"
        "Côte"      | "cote"
        "Türkiye"         | "turkiye"
        "Curaçao"         | "curacao"
        "İstanbul"        | "istanbul"
        "Łódź"            | "łodz"
    }

    def "given the away name when calling of should normalise it as well"() {
        expect:
        MatchParticipants.of("Spain", "BRAZİL").awayKey() == "brazil"
    }

    def "given the same teams in another spelling when comparing then should be equal"() {
        expect:
        MatchParticipants.of("Spain", "Côte d'Ivoire") == MatchParticipants.of("SPAIN", "Cote d'Ivoire")
    }

    def "given the same teams in swapped order when comparing then should not be equal"() {
        expect:
        MatchParticipants.of("Spain", "Germany") != MatchParticipants.of("Germany", "Spain")
    }

    def "isPlayedBy - dataset tests - home and away teams"() {
        given:
        def participants = MatchParticipants.of("Spain", "Germany")

        expect:
        participants.isPlayedBy(teamKey) == playing

        where:
        teamKey   | playing
        "spain"   | true
        "germany" | true
        "italy"   | false
        "Spain"   | false
    }
}
