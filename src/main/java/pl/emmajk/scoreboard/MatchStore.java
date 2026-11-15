package pl.emmajk.scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * In-memory storage of all matches in start order, finished ones included.
 */
final class MatchStore {

    /**
     * All matches, the most recently started last.
     */
    private final List<Match> matches = new ArrayList<>();

    /**
     * Appends a match as the most recently started one.
     */
    void add(Match match) {
        matches.add(match);
    }

    /**
     * Tells whether the team, given as a normalised key, is in an in-progress match as home or away team.
     */
    boolean isTeamPlaying(String teamKey) {
        return matches.stream()
                .anyMatch(match -> match.isInProgress() && match.participants().isPlayedBy(teamKey));
    }

    /**
     * Returns the latest match of the participants whatever its status, or empty if there is none.
     */
    Optional<Match> findLatest(MatchParticipants participants) {
        int index = indexOfLatest(participants);

        return index < 0 ? Optional.empty() : Optional.of(matches.get(index));
    }

    /**
     * Replaces the latest match of the participants.
     *
     * @throws NoSuchElementException if the participants have no match
     */
    void replaceLatest(MatchParticipants participants, Match replacement) {
        int index = indexOfLatest(participants);
        if (index < 0) {
            throw new NoSuchElementException("No match for " + participants);
        }

        matches.set(index, replacement);
    }

    /**
     * Returns the in-progress matches, the most recently started first.
     */
    List<Match> inProgressNewestFirst() {
        return matches.reversed().stream()
                .filter(Match::isInProgress)
                .toList();
    }

    /**
     * Returns the index of the latest match of the participants, or -1 if there is none.
     */
    private int indexOfLatest(MatchParticipants participants) {
        for (int i = matches.size() - 1; i >= 0; i--) {
            if (matches.get(i).participants().equals(participants)) {
                return i;
            }
        }

        return -1;
    }
}
