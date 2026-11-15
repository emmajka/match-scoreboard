package pl.emmajk.scoreboard

import spock.lang.Specification

class FinishMatchSpec extends Specification {

    Scoreboard scoreboard = new Scoreboard()

    def "finishMatch - dataset tests - invalid names"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")

        when:
        scoreboard.finishMatch(home, away)

        then:
        thrown(exception)

        where:
        [description, home, away, exception] << InvalidTeamNames.REPRESENTATIVE
    }

    def "given an empty scoreboard when calling finishMatch then should throw a MatchNotFoundException"() {
        when:
        scoreboard.finishMatch("Spain", "Brazil")

        then:
        thrown(MatchNotFoundException)
    }

    def "finishMatch - dataset tests - match not found"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")

        when:
        scoreboard.finishMatch(home, away)

        then:
        thrown(MatchNotFoundException)

        where:
        home      | away
        "Brazil"  | "Spain"
        "Spain"   | "Italy"
        "Italy"   | "Brazil"
        "Germany" | "France"
    }

    def "finishMatch - dataset tests - success scenarios"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")

        when:
        scoreboard.finishMatch(home, away)

        then:
        scoreboard.getSummary() == []

        where:
        home    | away
        "Spain" | "Brazil"
        "spain" | "BRAZIL"
    }

    def "given an accented team name when calling finishMatch with the plain spelling then should finish the match"() {
        given:
        scoreboard.startMatch("Côte d'Ivoire", "Cameroon")

        when:
        scoreboard.finishMatch("Cote d'Ivoire", "cameroon")

        then:
        scoreboard.getSummary() == []
    }

    def "given an already finished match when calling finishMatch then should throw a MatchAlreadyFinishedException"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.finishMatch("Spain", "Brazil")

        when:
        scoreboard.finishMatch("Spain", "Brazil")

        then:
        thrown(MatchAlreadyFinishedException)
    }

    def "given a finished pair and another match of its team when calling finishMatch then should throw a MatchAlreadyFinishedException"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.finishMatch("Spain", "Brazil")
        scoreboard.startMatch("Spain", "Italy")

        when:
        scoreboard.finishMatch("Spain", "Brazil")

        then:
        thrown(MatchAlreadyFinishedException)
    }

    def "given two matches when calling finishMatch then should keep the other match in progress"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.startMatch("Germany", "France")

        when:
        scoreboard.finishMatch("Spain", "Brazil")

        then:
        scoreboard.getSummary() == [new MatchSummary("Germany", "France", 0, 0)]
    }

    def "given a finished pair started again with swapped teams when calling finishMatch in the new order then should finish the new match"() {
        given:
        scoreboard.startMatch("Spain", "Germany")
        scoreboard.finishMatch("Spain", "Germany")
        scoreboard.startMatch("Germany", "Spain")

        when:
        scoreboard.finishMatch("Germany", "Spain")

        then:
        scoreboard.getSummary() == []
    }
}
