package pl.emmajk.scoreboard

import spock.lang.Specification

class MatchStoreSpec extends Specification {

    MatchStore store = new MatchStore()

    private static Match match(String home, String away, MatchStatus status = MatchStatus.IN_PROGRESS,
                               int homeScore = 0, int awayScore = 0) {
        new Match(home, away, MatchParticipants.of(home, away), homeScore, awayScore, status)
    }

    private static MatchParticipants participants(String home, String away) {
        MatchParticipants.of(home, away)
    }

    def "given an empty store when querying then should find nothing"() {
        expect:
        store.findLatest(participants("Spain", "Germany")).isEmpty()
        !store.isTeamPlaying("spain")
        store.inProgressNewestFirst() == []
    }

    def "given an added match when calling findLatest then should return it"() {
        given:
        def added = match("Spain", "Germany")
        store.add(added)

        expect:
        store.findLatest(participants("Spain", "Germany")).get() == added
    }

    def "findLatest - dataset tests - participants without a match"() {
        given:
        store.add(match("Spain", "Germany"))

        expect:
        store.findLatest(participants(home, away)).isEmpty()

        where:
        home      | away
        "Germany" | "Spain"
        "Spain"   | "Italy"
        "Italy"   | "Germany"
    }

    def "given several matches of a pair when calling findLatest then should return the most recently added"() {
        given:
        def older = match("Spain", "Germany", MatchStatus.FINISHED, 3, 0)
        def newer = match("Spain", "Germany")
        store.add(older)
        store.add(match("Italy", "France"))
        store.add(newer)

        expect:
        store.findLatest(participants("Spain", "Germany")).get() == newer
    }

    def "given a finished latest match when calling findLatest then should return it"() {
        given:
        def finished = match("Spain", "Germany", MatchStatus.FINISHED, 1, 1)
        store.add(finished)

        expect:
        store.findLatest(participants("Spain", "Germany")).get() == finished
    }

    def "isTeamPlaying - dataset tests - in-progress match"() {
        given:
        store.add(match("Spain", "Germany"))

        expect:
        store.isTeamPlaying(teamKey) == playing

        where:
        teamKey   | playing
        "spain"   | true
        "germany" | true
        "italy"   | false
    }

    def "given a finished match when calling isTeamPlaying then should return false for its teams"() {
        given:
        store.add(match("Spain", "Germany", MatchStatus.FINISHED))

        expect:
        !store.isTeamPlaying("spain")
        !store.isTeamPlaying("germany")
    }

    def "given matches added in a sequence when calling inProgressNewestFirst then should return the newest first without finished ones"() {
        given:
        def first = match("Mexico", "Canada")
        def second = match("Spain", "Brazil", MatchStatus.FINISHED)
        def third = match("Germany", "France")
        [first, second, third].each { store.add(it) }

        expect:
        store.inProgressNewestFirst() == [third, first]
    }

    def "given a match when calling replaceLatest then should store the replacement in the same place"() {
        given:
        def first = match("Mexico", "Canada")
        def second = match("Spain", "Brazil")
        def scored = match("Spain", "Brazil", MatchStatus.IN_PROGRESS, 2, 1)
        store.add(first)
        store.add(second)

        when:
        store.replaceLatest(participants("Spain", "Brazil"), scored)

        then:
        store.inProgressNewestFirst() == [scored, first]
        store.findLatest(participants("Spain", "Brazil")).get() == scored
    }

    def "given participants without a match when calling replaceLatest then should throw a NoSuchElementException"() {
        given:
        store.add(match("Spain", "Germany"))

        when:
        store.replaceLatest(participants("Germany", "Spain"), match("Germany", "Spain"))

        then:
        thrown(NoSuchElementException)
    }
}
