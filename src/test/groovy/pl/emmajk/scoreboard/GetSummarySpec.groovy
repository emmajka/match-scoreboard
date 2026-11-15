package pl.emmajk.scoreboard

import spock.lang.Specification

class GetSummarySpec extends Specification {

    Scoreboard scoreboard = new Scoreboard()

    def "given an empty scoreboard when calling getSummary then should return an empty list"() {
        expect:
        scoreboard.getSummary() == []
    }

    def "given the task example scenario when calling getSummary then should return the expected order"() {
        given:
        scoreboard.startMatch("Mexico", "Canada")
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.startMatch("Germany", "France")
        scoreboard.startMatch("Uruguay", "Italy")
        scoreboard.startMatch("Argentina", "Australia")
        scoreboard.updateScore("Mexico", "Canada", 0, 5)
        scoreboard.updateScore("Spain", "Brazil", 10, 2)
        scoreboard.updateScore("Germany", "France", 2, 2)
        scoreboard.updateScore("Uruguay", "Italy", 6, 6)
        scoreboard.updateScore("Argentina", "Australia", 3, 1)

        expect:
        scoreboard.getSummary() == [
                new MatchSummary("Uruguay", "Italy", 6, 6),
                new MatchSummary("Spain", "Brazil", 10, 2),
                new MatchSummary("Mexico", "Canada", 0, 5),
                new MatchSummary("Argentina", "Australia", 3, 1),
                new MatchSummary("Germany", "France", 2, 2)
        ]
    }

    def "given matches with equal scores when calling getSummary then should return the most recently started first"() {
        given:
        scoreboard.startMatch("Mexico", "Canada")
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.startMatch("Germany", "France")

        expect:
        scoreboard.getSummary()*.homeTeam == ["Germany", "Spain", "Mexico"]
    }

    def "given tied matches updated in another order when calling getSummary then should keep the start order"() {
        given:
        scoreboard.startMatch("Mexico", "Canada")
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.updateScore("Spain", "Brazil", 1, 1)
        scoreboard.updateScore("Mexico", "Canada", 1, 1)

        expect:
        scoreboard.getSummary()*.homeTeam == ["Spain", "Mexico"]
    }

    def "given a lowered score when calling getSummary then should order by the new total"() {
        given:
        scoreboard.startMatch("Mexico", "Canada")
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.updateScore("Mexico", "Canada", 2, 1)
        scoreboard.updateScore("Spain", "Brazil", 4, 0)
        scoreboard.updateScore("Spain", "Brazil", 1, 0)

        expect:
        scoreboard.getSummary()*.homeTeam == ["Mexico", "Spain"]
    }

    def "given a finished match when calling getSummary then should not return it"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.startMatch("Mexico", "Canada")
        scoreboard.updateScore("Spain", "Brazil", 10, 2)
        scoreboard.finishMatch("Spain", "Brazil")

        expect:
        scoreboard.getSummary() == [new MatchSummary("Mexico", "Canada", 0, 0)]
    }

    def "given a returned summary when updating a score then should not change that summary"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")
        def summary = scoreboard.getSummary()

        when:
        scoreboard.updateScore("Spain", "Brazil", 2, 0)

        then:
        summary == [new MatchSummary("Spain", "Brazil", 0, 0)]
    }

    def "given a returned summary when calling add then should throw an UnsupportedOperationException"() {
        given:
        scoreboard.startMatch("Mexico", "Canada")

        when:
        scoreboard.getSummary().add(new MatchSummary("Spain", "Brazil", 0, 0))

        then:
        thrown(UnsupportedOperationException)
    }
}
