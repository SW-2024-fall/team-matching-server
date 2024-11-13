package swe.second.team_matching_server.domain.meeting.FeatureExtract;


import java.util.*;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.stream.Collectors;
import swe.second.team_matching_server.domain.meeting.model.entity.Meeting;

public class ContentRecommender {
    // Existing weights
    private static final double CATEGORY_WEIGHT = 0.25;
    private static final double FEATURE_WEIGHT = 0.25;
    private static final double TIME_WEIGHT = 0.15;
    private static final double SIZE_WEIGHT = 0.1;
    private static final double LOCATION_WEIGHT = 0.1;
    // New weights
    private static final double USER_FEATURE_WEIGHT = 0.15;

    public static class UserProfile {
        private String id;
        private String username;
        private String major;
        private String studentId;
        private int attendanceScore;
        private List<String> features;
        private List<UserMeeting> meetings;

        // Getters and constructor
    }

    public static class UserMeeting {
        private String meetingId;
        private List<String> features;

        // Getters and constructor
    }

    // Recommendation data class
    private static class RecommendationScore implements Comparable<RecommendationScore> {
        Meeting meeting;
        double score;

        RecommendationScore(Meeting meeting, double score) {
            this.meeting = meeting;
            this.score = score;
        }

        @Override
        public int compareTo(RecommendationScore other) {
            return Double.compare(other.score, this.score);
        }
    }

    public List<Meeting> recommendMeetings(List<Meeting> availableMeetings, 
                                         List<Meeting> userPastMeetings,
                                         UserProfile userProfile,
                                         int numRecommendations) {
        
        // Existing preference calculations
        Map<String, Integer> categoryPreferences = new HashMap<>();
        Map<String, Integer> featurePreferences = new HashMap<>();
        Set<String> preferredLocations = new HashSet<>();
        List<LocalTime> preferredTimes = new ArrayList<>();

        // Add user profile features to preferences
        for (String userFeature : userProfile.features) {
            featurePreferences.merge(userFeature, 2, Integer::sum); // Weight user features higher
        }

        // Add features from user's past meetings in profile
        for (UserMeeting userMeeting : userProfile.meetings) {
            for (String feature : userMeeting.features) {
                featurePreferences.merge(feature, 1, Integer::sum);
            }
        }

        // Process past meetings as before
        for (Meeting pastMeeting : userPastMeetings) {
            processPastMeeting(pastMeeting, categoryPreferences, featurePreferences, 
                             preferredLocations, preferredTimes);
        }

        // Score meetings with enhanced criteria
        List<RecommendationScore> scoredMeetings = new ArrayList<>();
        
        for (Meeting meeting : availableMeetings) {
            double score = calculateMeetingScore(meeting, userProfile, categoryPreferences, 
                                               featurePreferences, preferredLocations, 
                                               preferredTimes);
            scoredMeetings.add(new RecommendationScore(meeting, score));
        }

        return scoredMeetings.stream()
                .sorted()
                .limit(numRecommendations)
                .map(rs -> rs.meeting)
                .collect(Collectors.toList());
    }

    private void processPastMeeting(Meeting meeting, 
                                  Map<String, Integer> categoryPreferences,
                                  Map<String, Integer> featurePreferences,
                                  Set<String> preferredLocations,
                                  List<LocalTime> preferredTimes) {
        for (String category : meeting.getInfo().getCategories()) {
            categoryPreferences.merge(category, 1, Integer::sum);
        }
        
        for (String feature : meeting.getInfo().getFeatures()) {
            featurePreferences.merge(feature, 1, Integer::sum);
        }

        preferredLocations.add(meeting.getInfo().getLocation());
        preferredTimes.add(meeting.getInfo().getStartTime());
    }

    private double calculateMeetingScore(Meeting meeting, 
                                       UserProfile userProfile,
                                       Map<String, Integer> categoryPreferences,
                                       Map<String, Integer> featurePreferences,
                                       Set<String> preferredLocations,
                                       List<LocalTime> preferredTimes) {
        double score = 0.0;
        
        // Basic scoring as before
        score += CATEGORY_WEIGHT * calculateCategoryScore(meeting, categoryPreferences);
        score += FEATURE_WEIGHT * calculateFeatureScore(meeting, featurePreferences);
        score += TIME_WEIGHT * calculateTimeScore(meeting, preferredTimes);
        score += LOCATION_WEIGHT * (preferredLocations.contains(meeting.getInfo().getLocation()) ? 1.0 : 0.0);
        score += SIZE_WEIGHT * calculateSizeScore(meeting, userProfile);
        
        // Add user profile specific scoring
        score += USER_FEATURE_WEIGHT * calculateUserFeatureMatch(meeting, userProfile);
        
        return score;
    }

    private double calculateUserFeatureMatch(Meeting meeting, UserProfile userProfile) {
        Set<String> meetingFeatures = new HashSet<>(meeting.getInfo().getFeatures());
        meetingFeatures.addAll(meeting.getInfo().getAnalyzedFeatures());
        
        long matchingFeatures = userProfile.features.stream()
                .filter(meetingFeatures::contains)
                .count();
        
        return (double) matchingFeatures / Math.max(1, userProfile.features.size());
    }

    private double calculateCategoryScore(Meeting meeting, Map<String, Integer> categoryPreferences) {
        return meeting.getInfo().getCategories().stream()
                .mapToDouble(category -> categoryPreferences.getOrDefault(category, 0))
                .sum() / Math.max(1, categoryPreferences.size());
    }

    private double calculateFeatureScore(Meeting meeting, Map<String, Integer> featurePreferences) {
        return meeting.getInfo().getFeatures().stream()
                .mapToDouble(feature -> featurePreferences.getOrDefault(feature, 0))
                .sum() / Math.max(1, featurePreferences.size());
    }

    private double calculateTimeScore(Meeting meeting, List<LocalTime> preferredTimes) {
        if (preferredTimes.isEmpty()) return 0.5;
        
        LocalTime meetingTime = meeting.getInfo().getStartTime();
        return preferredTimes.stream()
                .mapToDouble(prefTime -> 1.0 / (1 + Math.abs(prefTime.getHour() - meetingTime.getHour())))
                .average()
                .orElse(0.5);
    }

    private double calculateSizeScore(Meeting meeting, UserProfile userProfile) {
        // Adjust size score based on attendance score
        double baseScore = 1.0 / (1 + Math.abs(meeting.getInfo().getCurrentParticipant() - 
                                 meeting.getInfo().getMaxParticipant() / 2));
        
        // Prefer smaller groups for lower attendance scores
        if (userProfile.attendanceScore < 70) {
            return baseScore * (1.0 - (double)meeting.getInfo().getCurrentParticipant() / 
                              meeting.getInfo().getMaxParticipant());
        }
        
        return baseScore;
    }
}
