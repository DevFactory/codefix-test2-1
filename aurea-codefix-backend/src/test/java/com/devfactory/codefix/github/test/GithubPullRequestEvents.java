package com.devfactory.codefix.github.test;

import java.time.Instant;
import java.util.Calendar;
import java.util.TimeZone;
import javax.json.Json;
import javax.json.JsonObject;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GithubPullRequestEvents {

    public static final long PULL_REQUEST_ID = 191568743L;
    public static final String ACTION = "closed";
    public static final String BODY = "pull request body";
    public static final String TITLE = "pull request title";
    public static final boolean MERGED = true;
    public static final Instant MERGED_TIME = createInstant(2018, Calendar.MAY, 30, 20, 18, 48);

    public static String jsonPullEvent() {
        return Json.createObjectBuilder()
                .add("action", ACTION)
                .add("pull_request", createPullRequest())
                .build()
                .toString();
    }

    private JsonObject createPullRequest() {
        return Json.createObjectBuilder()
                .add("id", PULL_REQUEST_ID)
                .add("body", BODY)
                .add("title", TITLE)
                .add("merged", MERGED)
                .add("merged_at", "2018-05-30T20:18:48Z")
                .build();
    }

    private static Instant createInstant(int year, int month, int day, int hours, int minutes, int seconds) {
        return new Calendar.Builder()
                .setDate(year, month, day)
                .setTimeOfDay(hours, minutes, seconds)
                .setTimeZone(TimeZone.getTimeZone("UTC"))
                .build()
                .toInstant();
    }
}
