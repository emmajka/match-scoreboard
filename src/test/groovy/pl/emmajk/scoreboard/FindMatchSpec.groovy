package pl.emmajk.scoreboard

import spock.lang.Specification

class FindMatchSpec extends Specification {

    Scoreboard scoreboard = new Scoreboard()

    def "findMatch - dataset tests - invalid names"() {
        when:
        scoreboard.findMatch(home, away)

        then:
        thrown(exception)

        where:
        [description, home, away, exception] << InvalidTeamNames.REPRESENTATIVE
    }

    def "given an empty scoreboard when calling findMatch then should return an empty result"() {
        expect:
        scoreboard.findMatch("Spain", "Brazil").isEmpty()
    }

    def "findMatch - dataset tests - no in-progress match for the pair"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")

        expect:
        scoreboard.findMatch(home, away).isEmpty()

        where:
        home      | away
        "Brazil"  | "Spain"
        "Spain"   | "Italy"
        "Italy"   | "Brazil"
        "Germany" | "France"
    }

    def "findMatch - dataset tests - found by pair"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.updateScore("Spain", "Brazil", 2, 1)

        expect:
        scoreboard.findMatch(home, away).get() == new MatchSummary("Spain", "Brazil", 2, 1)

        where:
        home    | away
        "Spain" | "Brazil"
        "spain" | "BRAZIL"
    }

    def "given a started match when calling findMatch then should return it at 0-0 with the names as given"() {
        given:
        scoreboard.startMatch("Spain", "BRAZIL")

        expect:
        scoreboard.findMatch("spain", "brazil").get() == new MatchSummary("Spain", "BRAZIL", 0, 0)
    }

    def "given an accented team name when calling findMatch with the plain spelling then should find the match"() {
        given:
        scoreboard.startMatch("Côte d'Ivoire", "Cameroon")

        expect:
        scoreboard.findMatch("Cote d'Ivoire", "Cameroon").get() == new MatchSummary("Côte d'Ivoire", "Cameroon", 0, 0)
    }

    def "given several matches when calling findMatch then should return the one of the pair"() {
        given:
        scoreboard.startMatch("Mexico", "Canada")
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.updateScore("Mexico", "Canada", 0, 5)
        scoreboard.updateScore("Spain", "Brazil", 10, 2)

        expect:
        scoreboard.findMatch("Mexico", "Canada").get() == new MatchSummary("Mexico", "Canada", 0, 5)
        scoreboard.findMatch("Spain", "Brazil").get() == new MatchSummary("Spain", "Brazil", 10, 2)
    }

    def "given a finished match when calling findMatch then should return an empty result"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.finishMatch("Spain", "Brazil")

        expect:
        scoreboard.findMatch("Spain", "Brazil").isEmpty()
    }

    def "given a finished pair and another match of its team when calling findMatch then should return an empty result"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.finishMatch("Spain", "Brazil")
        scoreboard.startMatch("Spain", "Italy")

        expect:
        scoreboard.findMatch("Spain", "Brazil").isEmpty()
    }

    def "given a finished pair started again when calling findMatch then should return the new match"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.updateScore("Spain", "Brazil", 3, 0)
        scoreboard.finishMatch("Spain", "Brazil")
        scoreboard.startMatch("Spain", "Brazil")

        expect:
        scoreboard.findMatch("Spain", "Brazil").get() == new MatchSummary("Spain", "Brazil", 0, 0)
    }

    def "given a finished pair started again with swapped teams when calling findMatch then should find it only in the new order"() {
        given:
        scoreboard.startMatch("Spain", "Germany")
        scoreboard.finishMatch("Spain", "Germany")
        scoreboard.startMatch("Germany", "Spain")

        expect:
        scoreboard.findMatch("Spain", "Germany").isEmpty()
        scoreboard.findMatch("Germany", "Spain").get() == new MatchSummary("Germany", "Spain", 0, 0)
    }
}
