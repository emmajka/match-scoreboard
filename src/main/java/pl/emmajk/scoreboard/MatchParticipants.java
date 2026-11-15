package pl.emmajk.scoreboard;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * The home and away teams of a match as normalised keys. Create it with {@link #of(String, String)}.
 */
record MatchParticipants(String homeKey, String awayKey) {

    /**
     * Combining marks left after splitting accented letters, for example the accent of "é".
     */
    private static final Pattern COMBINING_MARKS = Pattern.compile("\\p{M}+");

    /**
     * Validates the team names and returns their participants.
     *
     * @throws NullPointerException     if a name is null
     * @throws IllegalArgumentException if a name is blank or has surrounding whitespace, or both teams are the same
     */
    static MatchParticipants of(String homeTeam, String awayTeam) {
        Objects.requireNonNull(homeTeam, "home team must not be null");
        Objects.requireNonNull(awayTeam, "away team must not be null");
        String homeKey = normalise(homeTeam);
        String awayKey = normalise(awayTeam);
        if (homeKey.isBlank() || awayKey.isBlank()) {
            throw new IllegalArgumentException("Team names must not be blank");
        }
        if (hasSurroundingWhitespace(homeTeam) || hasSurroundingWhitespace(awayTeam)) {
            throw new IllegalArgumentException("Team names must not have surrounding whitespace");
        }
        if (homeKey.equals(awayKey)) {
            throw new IllegalArgumentException("A team cannot play itself");
        }
        return new MatchParticipants(homeKey, awayKey);
    }

    /**
     * Tells whether the team, given as a normalised key, is the home or the away team.
     */
    boolean isPlayedBy(String teamKey) {
        return homeKey.equals(teamKey) || awayKey.equals(teamKey);
    }

    /**
     * Lower-cases a team name and removes the accents from its letters.
     */
    private static String normalise(String teamName) {
        String lowerCase = teamName.toLowerCase(Locale.ROOT);
        String decomposed = Normalizer.normalize(lowerCase, Normalizer.Form.NFD);
        return COMBINING_MARKS.matcher(decomposed).replaceAll("");
    }

    /**
     * Tells whether the name starts or ends with whitespace, as {@link String#strip()} defines it.
     */
    private static boolean hasSurroundingWhitespace(String teamName) {
        return !teamName.equals(teamName.strip());
    }
}
