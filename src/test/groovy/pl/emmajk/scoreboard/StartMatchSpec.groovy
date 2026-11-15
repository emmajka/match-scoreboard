package pl.emmajk.scoreboard

import spock.lang.Specification

class StartMatchSpec extends Specification {

    Scoreboard scoreboard = new Scoreboard()

    def "startMatch - dataset tests - invalid names"() {
        when:
        scoreboard.startMatch(home, away)

        then:
        thrown(exception)

        where:
        [description, home, away, exception] << InvalidTeamNames.REPRESENTATIVE
    }

    def "startMatch - dataset tests - success scenarios"() {
        when:
        scoreboard.startMatch(home, away)

        then:
        scoreboard.getSummary() == [new MatchSummary(home, away, 0, 0)]

        where:
        home      | away
        "Spain"   | "Brazil"
        "spain"   | "BRAZIL"
        "Czechia" | "Côte d'Ivoire"
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
        "SPAIN"     | "Italy"
        "Spain"     | "Brazil"
        "Brazil"    | "Spain"
        "Spain"     | "Germany"
        "Argentina" | "Brazil"
    }

    def "given a team playing under an accented name when calling startMatch with the plain spelling then should throw a TeamAlreadyPlayingException"() {
        given:
        scoreboard.startMatch("Côte d'Ivoire", "Cameroon")

        when:
        scoreboard.startMatch("Cote d'Ivoire", "Ghana")

        then:
        thrown(TeamAlreadyPlayingException)
    }

    def "given a rejected start when calling startMatch then should not register the other team"() {
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

    def "given a finished match when calling startMatch then should let its teams play other matches"() {
        given:
        scoreboard.startMatch("Spain", "Brazil")
        scoreboard.finishMatch("Spain", "Brazil")

        when:
        scoreboard.startMatch("Spain", "Italy")
        scoreboard.startMatch("Germany", "Brazil")

        then:
        scoreboard.getSummary()*.homeTeam == ["Germany", "Spain"]
    }

    def "startMatch - dataset tests - finished pair started again"() {
        given:
        scoreboard.startMatch("Spain", "Germany")
        scoreboard.updateScore("Spain", "Germany", 3, 0)
        scoreboard.finishMatch("Spain", "Germany")

        when:
        scoreboard.startMatch(home, away)

        then:
        scoreboard.getSummary() == [new MatchSummary(home, away, 0, 0)]

        where:
        home      | away
        "Spain"   | "Germany"
        "Germany" | "Spain"
    }

    def "given both teams already playing when calling startMatch then should report the home team"() {
        given:
        scoreboard.startMatch("Germany", "Argentina")
        scoreboard.startMatch("Spain", "Brazil")

        when:
        scoreboard.startMatch("Spain", "Germany")

        then:
        def exception = thrown(TeamAlreadyPlayingException)
        exception.message.contains("Spain")
    }

    def "given only the away team already playing when calling startMatch then should report the away team"() {
        given:
        scoreboard.startMatch("Germany", "Argentina")

        when:
        scoreboard.startMatch("Spain", "Germany")

        then:
        def exception = thrown(TeamAlreadyPlayingException)
        exception.message.contains("Germany")
    }
}
