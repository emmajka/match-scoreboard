package pl.emmajk.scoreboard

import spock.lang.Specification

class ScoreboardSpec extends Specification {

    Scoreboard scoreboard = new Scoreboard()

    def "given a null home team when calling startMatch then should throw a NullPointerException"() {
        when:
        scoreboard.startMatch(null, "Brazil")

        then:
        thrown(NullPointerException)
    }

    def "given a null away team when calling startMatch then should throw a NullPointerException"() {
        when:
        scoreboard.startMatch("Spain", null)

        then:
        thrown(NullPointerException)
    }

    def "startMatch - dataset tests - blank names"() {
        when:
        scoreboard.startMatch(home, away)

        then:
        thrown(IllegalArgumentException)

        where:
        home    | away
        ""      | "Brazil"
        "   "   | "Brazil"
        "\t"    | "Brazil"
        "Spain" | ""
        "Spain" | "   "
        ""      | ""
    }

    def "startMatch - dataset tests - same team"() {
        when:
        scoreboard.startMatch(home, away)

        then:
        thrown(IllegalArgumentException)

        where:
        home    | away
        "Spain" | "Spain"
        "Spain" | "spain"
        "Spain" | " SPAIN "
    }

    def "startMatch - dataset tests - team already playing"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.startMatch("Germany", "Argentina")

        when:
        scoreboard.startMatch(home, away)

        then:
        thrown(TeamAlreadyPlayingException)

        where:
        home        | away
        "Spain"     | "Italy"
        "Italy"     | "Spain"
        "Brazil"    | "Italy"
        "Italy"     | "Brazil"
        "spain"     | "Italy"
        " SPAIN "   | "Italy"
        "Spain"     | "Brazil"
        "Brazil"    | "Spain"
        "Spain"     | "Germany"
        "Argentina" | "Brazil"
    }

    def "startMatch - dataset tests - success scenarios"() {
        when:
        scoreboard.startMatch(home, away)

        then:
        noExceptionThrown()

        where:
        home      | away
        "Spain"   | "Brazil"
        " Spain " | "Brazil "
        "spain"   | "BRAZIL"
        "Czechia" | "Côte d'Ivoire"
    }

    def "given several matches with distinct teams when calling startMatch should start them all"() {
        when:
        scoreboard.startMatch("Mexico", "Canada")
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.startMatch("Germany", "France")

        then:
        noExceptionThrown()
    }

    def "given a rejected start when calling startMatch should not register the other team"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")

        when:
        scoreboard.startMatch("Italy", "Spain")

        then:
        thrown(TeamAlreadyPlayingException)

        when:
        scoreboard.startMatch("Italy", "Germany")

        then:
        noExceptionThrown()
    }
}
