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

    def "given a null home team when calling finishMatch then should throw a NullPointerException"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")

        when:
        scoreboard.finishMatch(null, "Brazil")

        then:
        thrown(NullPointerException)
    }

    def "given a null away team when calling finishMatch then should throw a NullPointerException"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")

        when:
        scoreboard.finishMatch("Spain", null)

        then:
        thrown(NullPointerException)
    }

    def "finishMatch - dataset tests - blank names"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")

        when:
        scoreboard.finishMatch(home, away)

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

    def "finishMatch - dataset tests - same team"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")

        when:
        scoreboard.finishMatch(home, away)

        then:
        thrown(IllegalArgumentException)

        where:
        home    | away
        "Spain" | "Spain"
        "Spain" | "spain"
        "Spain" | " SPAIN "
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
        noExceptionThrown()

        where:
        home      | away
        "Spain"   | "Brazil"
        "spain"   | "BRAZIL"
        " Spain " | " Brazil "
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

    def "given a finished match when calling updateScore then should throw a MatchAlreadyFinishedException"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.finishMatch("Spain", "Brazil")

        when:
        scoreboard.updateScore("Spain", "Brazil", 1, 0)

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

    def "given a finished match when calling startMatch should let its teams play other matches"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.finishMatch("Spain", "Brazil")

        when:
        scoreboard.startMatch("Spain", "Italy")
        scoreboard.startMatch("Germany", "Brazil")

        then:
        noExceptionThrown()
    }

    def "given a finished match when calling startMatch should let the same pair play again"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.finishMatch("Spain", "Brazil")

        when:
        scoreboard.startMatch("Spain", "Brazil")

        then:
        noExceptionThrown()
    }

    def "given a rematch when calling updateScore and finishMatch should address the new match"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.finishMatch("Spain", "Brazil")
        scoreboard.startMatch("Spain", "Brazil")

        when:
        scoreboard.updateScore("Spain", "Brazil", 1, 0)
        scoreboard.finishMatch("Spain", "Brazil")

        then:
        noExceptionThrown()

        when:
        scoreboard.updateScore("Spain", "Brazil", 2, 0)

        then:
        thrown(MatchAlreadyFinishedException)
    }

    def "given two matches when calling finishMatch should keep the other match in progress"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.startMatch("Germany", "France")
        scoreboard.finishMatch("Spain", "Brazil")

        when:
        scoreboard.updateScore("Germany", "France", 1, 0)
        scoreboard.startMatch("Germany", "Italy")

        then:
        thrown(TeamAlreadyPlayingException)
    }

    def "given an empty scoreboard when calling getSummary should return an empty list"() {
        expect:
        scoreboard.getSummary() == []
    }

    def "given a started match when calling getSummary should return it at 0-0 with the names as first given"() {
        given:
        scoreboard.startMatch(" Spain ", "BRAZIL ")

        expect:
        scoreboard.getSummary() == [new MatchSummary("Spain", "BRAZIL", 0, 0)]
    }

    def "given an updated match when calling getSummary should return the new score and keep the names"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.updateScore(" SPAIN ", "brazil", 3, 1)

        expect:
        scoreboard.getSummary() == [new MatchSummary("Spain", "Brazil", 3, 1)]
    }

    def "given the task example scenario when calling getSummary should return the expected order"() {
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

    def "given matches with equal scores when calling getSummary should return the most recently started first"() {
        given:
        scoreboard.startMatch("Mexico", "Canada")
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.startMatch("Germany", "France")

        expect:
        scoreboard.getSummary()*.homeTeam == ["Germany", "Spain", "Mexico"]
    }

    def "given tied matches updated in another order when calling getSummary should keep the start order"() {
        given:
        scoreboard.startMatch("Mexico", "Canada")
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.updateScore("Spain", "Brazil", 1, 1)
        scoreboard.updateScore("Mexico", "Canada", 1, 1)

        expect:
        scoreboard.getSummary()*.homeTeam == ["Spain", "Mexico"]
    }

    def "given a lowered score when calling getSummary should order by the new total"() {
        given:
        scoreboard.startMatch("Mexico", "Canada")
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.updateScore("Mexico", "Canada", 2, 1)
        scoreboard.updateScore("Spain", "Brazil", 4, 0)
        scoreboard.updateScore("Spain", "Brazil", 1, 0)

        expect:
        scoreboard.getSummary()*.homeTeam == ["Mexico", "Spain"]
    }

    def "given a finished match when calling getSummary should not return it"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.startMatch("Mexico", "Canada")
        scoreboard.updateScore("Spain", "Brazil", 10, 2)
        scoreboard.finishMatch("Spain", "Brazil")

        expect:
        scoreboard.getSummary() == [new MatchSummary("Mexico", "Canada", 0, 0)]
    }

    def "given a finished pair started again when calling getSummary should return only the new match"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.updateScore("Spain", "Brazil", 3, 0)
        scoreboard.finishMatch("Spain", "Brazil")
        scoreboard.startMatch("Spain", "Brazil")

        expect:
        scoreboard.getSummary() == [new MatchSummary("Spain", "Brazil", 0, 0)]
    }

    def "given a returned summary when updating a score should not change that summary"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")
        def summary = scoreboard.getSummary()

        when:
        scoreboard.updateScore("Spain", "Brazil", 2, 0)

        then:
        summary == [new MatchSummary("Spain", "Brazil", 0, 0)]
    }

    def "given a returned summary when calling add then should throw an UnsupportedOperationException"() {
        when:
        scoreboard.getSummary().add(new MatchSummary("Spain", "Brazil", 0, 0))

        then:
        thrown(UnsupportedOperationException)
    }

    def "given a null home team when calling findMatch then should throw a NullPointerException"() {
        when:
        scoreboard.findMatch(null, "Brazil")

        then:
        thrown(NullPointerException)
    }

    def "given a null away team when calling findMatch then should throw a NullPointerException"() {
        when:
        scoreboard.findMatch("Spain", null)

        then:
        thrown(NullPointerException)
    }

    def "findMatch - dataset tests - blank names"() {
        when:
        scoreboard.findMatch(home, away)

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

    def "findMatch - dataset tests - same team"() {
        when:
        scoreboard.findMatch(home, away)

        then:
        thrown(IllegalArgumentException)

        where:
        home    | away
        "Spain" | "Spain"
        "Spain" | "spain"
        "Spain" | " SPAIN "
    }

    def "given an empty scoreboard when calling findMatch should return an empty result"() {
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
        home      | away
        "Spain"   | "Brazil"
        "spain"   | "BRAZIL"
        " Spain " | " Brazil "
    }

    def "given a started match when calling findMatch should return it at 0-0 with the names as first given"() {
        given:
        scoreboard.startMatch(" Spain ", "BRAZIL ")

        expect:
        scoreboard.findMatch("spain", "brazil").get() == new MatchSummary("Spain", "BRAZIL", 0, 0)
    }

    def "given several matches when calling findMatch should return the one of the pair"() {
        given:
        scoreboard.startMatch("Mexico", "Canada")
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.updateScore("Mexico", "Canada", 0, 5)
        scoreboard.updateScore("Spain", "Brazil", 10, 2)

        expect:
        scoreboard.findMatch("Mexico", "Canada").get() == new MatchSummary("Mexico", "Canada", 0, 5)
        scoreboard.findMatch("Spain", "Brazil").get() == new MatchSummary("Spain", "Brazil", 10, 2)
    }

    def "given a finished match when calling findMatch should return an empty result"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.finishMatch("Spain", "Brazil")

        expect:
        scoreboard.findMatch("Spain", "Brazil").isEmpty()
    }

    def "given a finished pair and another match of its team when calling findMatch should return an empty result"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.finishMatch("Spain", "Brazil")
        scoreboard.startMatch("Spain", "Italy")

        expect:
        scoreboard.findMatch("Spain", "Brazil").isEmpty()
    }

    def "given a finished pair started again when calling findMatch should return the new match"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.updateScore("Spain", "Brazil", 3, 0)
        scoreboard.finishMatch("Spain", "Brazil")
        scoreboard.startMatch("Spain", "Brazil")

        expect:
        scoreboard.findMatch("Spain", "Brazil").get() == new MatchSummary("Spain", "Brazil", 0, 0)
    }
}
