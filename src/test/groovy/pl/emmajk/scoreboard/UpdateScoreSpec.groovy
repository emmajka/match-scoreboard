package pl.emmajk.scoreboard

import spock.lang.Specification

class UpdateScoreSpec extends Specification {

    Scoreboard scoreboard = new Scoreboard()

    def "updateScore - dataset tests - invalid names"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")

        when:
        scoreboard.updateScore(home, away, 1, 0)

        then:
        thrown(exception)

        where:
        [home, away, exception] << InvalidTeamNames.PAIRS
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
        scoreboard.getSummary() == [new MatchSummary("Spain", "Brazil", homeScore, awayScore)]

        where:
        home    | away     | homeScore | awayScore
        "Spain" | "Brazil" | 1         | 0
        "spain" | "BRAZIL" | 2         | 3
        "SPAIN" | "brazil" | 0         | 0
        "Spain" | "Brazil" | 10        | 2
    }

    def "given an accented team name when calling updateScore with the plain spelling should update the match"() {
        given:
        scoreboard.startMatch("Côte d'Ivoire", "Cameroon")

        when:
        scoreboard.updateScore("Cote d'Ivoire", "cameroon", 2, 1)

        then:
        scoreboard.getSummary() == [new MatchSummary("Côte d'Ivoire", "Cameroon", 2, 1)]
    }

    def "given a lower score when calling updateScore should accept the decrease"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.updateScore("Spain", "Brazil", 2, 1)

        when:
        scoreboard.updateScore("Spain", "Brazil", 1, 1)

        then:
        scoreboard.getSummary() == [new MatchSummary("Spain", "Brazil", 1, 1)]
    }

    def "given several matches when calling updateScore should update each one by its pair"() {
        given:
        scoreboard.startMatch("Mexico", "Canada")
        scoreboard.startMatch("Spain", "Brazil")

        when:
        scoreboard.updateScore("Mexico", "Canada", 0, 5)
        scoreboard.updateScore("Spain", "Brazil", 10, 2)

        then:
        scoreboard.getSummary() == [
                new MatchSummary("Spain", "Brazil", 10, 2),
                new MatchSummary("Mexico", "Canada", 0, 5)
        ]
    }

    def "given a finished match when calling updateScore then should throw a MatchAlreadyFinishedException"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.finishMatch("Spain", "Brazil")

        when:
        scoreboard.updateScore("Spain", "Brazil", 1, 0)

        then:
        thrown(MatchAlreadyFinishedException)
    }

    def "given a finished pair started again when calling updateScore should address the new match"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.finishMatch("Spain", "Brazil")
        scoreboard.startMatch("Spain", "Brazil")

        when:
        scoreboard.updateScore("Spain", "Brazil", 1, 0)
        scoreboard.finishMatch("Spain", "Brazil")
        scoreboard.updateScore("Spain", "Brazil", 2, 0)

        then:
        thrown(MatchAlreadyFinishedException)
    }

    def "given a finished pair started again with swapped teams when calling updateScore in the old order then should throw a MatchAlreadyFinishedException"() {
        given:
        scoreboard.startMatch("Spain", "Germany")
        scoreboard.finishMatch("Spain", "Germany")
        scoreboard.startMatch("Germany", "Spain")

        when:
        scoreboard.updateScore("Spain", "Germany", 1, 2)

        then:
        thrown(MatchAlreadyFinishedException)
    }

    def "given a finished pair started again with swapped teams when calling updateScore in the new order should update the new match"() {
        given:
        scoreboard.startMatch("Spain", "Germany")
        scoreboard.finishMatch("Spain", "Germany")
        scoreboard.startMatch("Germany", "Spain")

        when:
        scoreboard.updateScore("Germany", "Spain", 1, 2)

        then:
        scoreboard.getSummary() == [new MatchSummary("Germany", "Spain", 1, 2)]
    }
}
