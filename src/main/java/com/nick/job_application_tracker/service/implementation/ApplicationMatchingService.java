package com.nick.job_application_tracker.service.implementation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.response.ApplicationMatchResponseDTO;
import com.nick.job_application_tracker.dto.response.DocumentMatchResponseDTO;
import com.nick.job_application_tracker.model.CoverLetter;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.Resume;
import com.nick.job_application_tracker.model.SkillTracker;

@Service
public class ApplicationMatchingService {
    private static final Pattern TOKEN_SPLIT = Pattern.compile("[^a-z0-9+#.]+");
    private static final Set<String> STOP_WORDS = Set.of(
        "a", "an", "and", "are", "as", "at", "be", "by", "for", "from", "in", "into", "is", "of",
        "on", "or", "our", "the", "to", "with", "you", "your", "we", "will", "this", "that", "role",
        "team", "job", "work", "experience", "using", "have", "has", "must", "should", "can"
    );

    private final JobApplicationService jobApplicationService;
    private final WorkspaceReadService workspaceReadService;

    public ApplicationMatchingService(
        JobApplicationService jobApplicationService,
        WorkspaceReadService workspaceReadService
    ) {
        this.jobApplicationService = jobApplicationService;
        this.workspaceReadService = workspaceReadService;
    }

    public ApplicationMatchResponseDTO matchApplication(UUID applicationId) {
        JobApplication application = jobApplicationService.getModelById(applicationId);

        Set<String> applicationKeywords = extractKeywords(String.join(
            " ",
            safe(application.getJobTitle()),
            safe(application.getCompany()),
            safe(application.getJobDescription()),
            safe(application.getNotes())
        ));

        List<SkillTracker> skills = workspaceReadService.getSkills();
        List<String> skillNames = skills.stream()
            .map(SkillTracker::getSkillName)
            .filter(name -> name != null && !name.isBlank())
            .distinct()
            .sorted(String.CASE_INSENSITIVE_ORDER)
            .toList();

        List<String> matchedSkills = skillNames.stream()
            .filter(skill -> containsPhrase(application.getJobDescription(), skill) || hasKeywordOverlap(applicationKeywords, extractKeywords(skill)))
            .toList();

        List<Resume> resumes = workspaceReadService.getResumes();
        List<CoverLetter> coverLetters = workspaceReadService.getCoverLetters();

        DocumentMatchResponseDTO recommendedResume = resumes.stream()
            .map(resume -> scoreResume(applicationKeywords, resume))
            .max(Comparator.comparingDouble(DocumentMatchResponseDTO::score))
            .orElse(null);

        DocumentMatchResponseDTO recommendedCoverLetter = coverLetters.stream()
            .map(coverLetter -> scoreCoverLetter(applicationKeywords, coverLetter))
            .max(Comparator.comparingDouble(DocumentMatchResponseDTO::score))
            .orElse(null);

        Set<String> assetKeywords = new LinkedHashSet<>();
        skillNames.forEach(skill -> assetKeywords.addAll(extractKeywords(skill)));
        resumes.forEach(resume -> assetKeywords.addAll(extractKeywords(safe(resume.getTitle()) + " " + safe(resume.getFilePath()))));
        coverLetters.forEach(coverLetter -> assetKeywords.addAll(extractKeywords(safe(coverLetter.getTitle()) + " " + safe(coverLetter.getFilePath()) + " " + safe(coverLetter.getContent()))));

        List<String> keywordMatches = applicationKeywords.stream()
            .filter(assetKeywords::contains)
            .limit(10)
            .toList();

        List<String> keywordGaps = applicationKeywords.stream()
            .filter(keyword -> !assetKeywords.contains(keyword))
            .limit(10)
            .toList();

        List<String> missingSkills = keywordGaps.stream()
            .limit(8)
            .toList();

        double skillCoverage = toPercent(keywordMatches.size(), applicationKeywords.size());
        double resumeScore = recommendedResume == null ? 0.0 : recommendedResume.score();
        double coverLetterScore = recommendedCoverLetter == null ? 0.0 : recommendedCoverLetter.score();
        double overallScore = roundOneDecimal((skillCoverage * 0.5) + (resumeScore * 0.25) + (coverLetterScore * 0.25));

        List<String> recommendations = buildRecommendations(application, recommendedResume, recommendedCoverLetter, missingSkills, overallScore);

        return new ApplicationMatchResponseDTO(
            application.getId(),
            application.getCompany(),
            application.getJobTitle(),
            overallScore,
            matchedSkills,
            missingSkills,
            keywordMatches,
            keywordGaps,
            recommendedResume,
            recommendedCoverLetter,
            recommendations
        );
    }

    private DocumentMatchResponseDTO scoreResume(Set<String> applicationKeywords, Resume resume) {
        Set<String> resumeKeywords = extractKeywords(safe(resume.getTitle()) + " " + safe(resume.getFilePath()));
        List<String> matches = applicationKeywords.stream().filter(resumeKeywords::contains).toList();
        double score = toPercent(matches.size(), applicationKeywords.size());
        String rationale = matches.isEmpty()
            ? "No strong keyword overlap detected."
            : "Matched keywords: " + String.join(", ", matches.stream().limit(5).toList());
        return new DocumentMatchResponseDTO(resume.getId(), resume.getTitle(), roundOneDecimal(score), rationale);
    }

    private DocumentMatchResponseDTO scoreCoverLetter(Set<String> applicationKeywords, CoverLetter coverLetter) {
        Set<String> coverLetterKeywords = extractKeywords(safe(coverLetter.getTitle()) + " " + safe(coverLetter.getFilePath()) + " " + safe(coverLetter.getContent()));
        List<String> matches = applicationKeywords.stream().filter(coverLetterKeywords::contains).toList();
        double score = toPercent(matches.size(), applicationKeywords.size());
        String rationale = matches.isEmpty()
            ? "No strong keyword overlap detected."
            : "Matched keywords: " + String.join(", ", matches.stream().limit(5).toList());
        return new DocumentMatchResponseDTO(coverLetter.getId(), coverLetter.getTitle(), roundOneDecimal(score), rationale);
    }

    private List<String> buildRecommendations(
        JobApplication application,
        DocumentMatchResponseDTO recommendedResume,
        DocumentMatchResponseDTO recommendedCoverLetter,
        List<String> missingSkills,
        double overallScore
    ) {
        List<String> recommendations = new ArrayList<>();
        if (!missingSkills.isEmpty()) {
            recommendations.add("Strengthen or highlight these likely gaps: " + String.join(", ", missingSkills.stream().limit(5).toList()) + ".");
        }
        if (application.getResume() == null && recommendedResume != null) {
            recommendations.add("Attach the best available resume match: " + recommendedResume.title() + ".");
        }
        if (application.getCoverLetter() == null && recommendedCoverLetter != null) {
            recommendations.add("Attach or tailor the best available cover letter: " + recommendedCoverLetter.title() + ".");
        }
        if (recommendedCoverLetter == null || recommendedCoverLetter.score() < 20.0) {
            recommendations.add("Tailor the cover letter content more closely to the role language.");
        }
        if (overallScore >= 70.0) {
            recommendations.add("Stored assets show a strong baseline fit for this application.");
        }
        if (recommendations.isEmpty()) {
            recommendations.add("No major gaps detected from stored documents and tracked skills.");
        }
        return recommendations;
    }

    private boolean containsPhrase(String haystack, String phrase) {
        if (haystack == null || phrase == null) {
            return false;
        }
        return haystack.toLowerCase(Locale.ROOT).contains(phrase.toLowerCase(Locale.ROOT));
    }

    private boolean hasKeywordOverlap(Set<String> left, Set<String> right) {
        return right.stream().anyMatch(left::contains);
    }

    private Set<String> extractKeywords(String text) {
        if (text == null || text.isBlank()) {
            return Set.of();
        }
        return TOKEN_SPLIT.splitAsStream(text.toLowerCase(Locale.ROOT))
            .map(String::trim)
            .filter(token -> token.length() > 1)
            .filter(token -> !STOP_WORDS.contains(token))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private double toPercent(int numerator, int denominator) {
        if (denominator <= 0) {
            return 0.0;
        }
        return numerator * 100.0 / denominator;
    }

    private double roundOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
