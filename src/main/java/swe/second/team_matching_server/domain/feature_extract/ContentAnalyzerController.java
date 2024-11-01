import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import swe.second.team_matching_server.domain.feature_extract.ContentAnalyzerFacade;

@RestController
@RequestMapping("/api/analyzer")
@RequiredArgsConstructor
public class ContentAnalyzerController {
    private final ContentAnalyzerFacade contentAnalyzerFacade;

    @PostMapping("/meeting/features")
    public ApiResponse<List<String>> analyzeMeetingFeatures(@RequestBody MeetingInfo meeting) {
        return contentAnalyzerFacade.analyzeMeetingFeatures(meeting);
    }

    @PostMapping("/meeting/summary")
    public ApiResponse<String> generateMeetingSummary(@RequestBody MeetingInfo meeting) {
        return contentAnalyzerFacade.generateMeetingSummary(meeting);
    }

    @PostMapping("/member/features")
    public ApiResponse<List<String>> analyzeMemberFeatures(@RequestBody MeetingMemberElement member) {
        return contentAnalyzerFacade.analyzeMemberFeatures(member);
    }

    @PutMapping("/history/meeting/features")
    public ApiResponse<List<String>> updateMeetingFeatures(
            @RequestBody History history,
            @RequestParam List<String> currentFeatures) {
        return contentAnalyzerFacade.updateMeetingFeatures(history, currentFeatures);
    }

    @PutMapping("/history/meeting/introduction")
    public ApiResponse<String> updateMeetingIntroduction(
            @RequestBody History history,
            @RequestParam String currentIntro) {
        return contentAnalyzerFacade.updateMeetingIntroduction(history, currentIntro);
    }

    @PutMapping("/history/user/features")
    public ApiResponse<List<String>> updateUserFeatures(
            @RequestBody History history,
            @RequestParam List<String> currentFeatures) {
        return contentAnalyzerFacade.updateUserFeatures(history, currentFeatures);
    }
}