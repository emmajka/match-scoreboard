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

    def "given a null home team when calling updateScore then should throw a NullPointerException"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")

        when:
        scoreboard.updateScore(null, "Brazil", 1, 0)

        then:
        thrown(NullPointerException)
    }

    def "given a null away team when calling updateScore then should throw a NullPointerException"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")

        when:
        scoreboard.updateScore("Spain", null, 1, 0)

        then:
        thrown(NullPointerException)
    }

    def "updateScore - dataset tests - blank names"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")

        when:
        scoreboard.updateScore(home, away, 1, 0)

        then:
        thrown(IllegalArgumentException)

        where:
        home    | away
        ""      | "Brazil"
        "   "   | "Brazil"
        "Spain" | ""
        "Spain" | "   "
        ""      | ""
    }

    def "updateScore - dataset tests - same team"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")

        when:
        scoreboard.updateScore(home, away, 1, 0)

        then:
        thrown(IllegalArgumentException)

        where:
        home    | away
        "Spain" | "Spain"
        "Spain" | "spain"
        "Spain" | " SPAIN "
    }

    def "updateScore - dataset tests - negative scores"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")

        when:
        scoreboard.updateScore("Spain", "Brazil", homeScore, awayScore)

        then:
        thrown(IllegalArgumentException)

        where:
        homeScore | awayScore
        -1        | 0
        0         | -1
        -1        | -1
        -5        | 3
    }

    def "given a negative score for an unknown pair when calling updateScore then should throw an IllegalArgumentException"() {
        when:
        scoreboard.updateScore("Spain", "Brazil", -1, 0)

        then:
        thrown(IllegalArgumentException)
    }

    def "given an empty scoreboard when calling updateScore then should throw a MatchNotFoundException"() {
        when:
        scoreboard.updateScore("Spain", "Brazil", 1, 0)

        then:
        thrown(MatchNotFoundException)
    }

    def "updateScore - dataset tests - match not found"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")

        when:
        scoreboard.updateScore(home, away, 1, 0)

        then:
        thrown(MatchNotFoundException)

        where:
        home      | away
        "Brazil"  | "Spain"
        "Spain"   | "Italy"
        "Italy"   | "Brazil"
        "Germany" | "France"
    }

    def "updateScore - dataset tests - success scenarios"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")

        when:
        scoreboard.updateScore(home, away, homeScore, awayScore)

        then:
        noExceptionThrown()

        where:
        home      | away       | homeScore | awayScore
        "Spain"   | "Brazil"   | 1         | 0
        "spain"   | "BRAZIL"   | 2         | 3
        " Spain " | " Brazil " | 0         | 0
        "Spain"   | "Brazil"   | 10        | 2
    }

    def "given a lower score when calling updateScore should accept the decrease"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.updateScore("Spain", "Brazil", 2, 1)

        when:
        scoreboard.updateScore("Spain", "Brazil", 1, 1)

        then:
        noExceptionThrown()
    }

    def "given several matches when calling updateScore should update each one by its pair"() {
        given:
        scoreboard.startMatch("Mexico", "Canada")
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.startMatch("Germany", "France")

        when:
        scoreboard.updateScore("Mexico", "Canada", 0, 5)
        scoreboard.updateScore("Spain", "Brazil", 10, 2)
        scoreboard.updateScore("Germany", "France", 2, 2)

        then:
        noExceptionThrown()
    }
}
