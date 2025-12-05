package com.mentalhealthhub.service;

import com.mentalhealthhub.dto.AssessmentQuestionDTO;
import com.mentalhealthhub.dto.AssessmentResultDTO;
import com.mentalhealthhub.model.Assessment;
import com.mentalhealthhub.model.User;
import com.mentalhealthhub.repository.AssessmentRepository;
import com.mentalhealthhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssessmentService {

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private UserRepository userRepository;

    // Define all 8 questions
    public List<AssessmentQuestionDTO> getAllQuestions() {
        List<AssessmentQuestionDTO> questions = new ArrayList<>();

        questions.add(new AssessmentQuestionDTO(1,
                "How often have you felt overwhelmed in the past week?", "STRESS"));

        questions.add(new AssessmentQuestionDTO(2,
                "How often have you experienced difficulty concentrating?", "STRESS"));

        questions.add(new AssessmentQuestionDTO(3,
                "How often have you felt nervous, anxious, or on edge?", "ANXIETY"));

        questions.add(new AssessmentQuestionDTO(4,
                "How often have you felt down, depressed, or hopeless?", "DEPRESSION"));

        questions.add(new AssessmentQuestionDTO(5,
                "How often have you had trouble falling or staying asleep?", "SLEEP"));

        questions.add(new AssessmentQuestionDTO(6,
                "How often have you felt tired or had little energy?", "DEPRESSION"));

        questions.add(new AssessmentQuestionDTO(7,
                "How often have you felt bad about yourself or that you are a failure?", "DEPRESSION"));

        questions.add(new AssessmentQuestionDTO(8,
                "How often have you had thoughts that you would be better off dead or hurting yourself?",
                "DEPRESSION"));

        return questions;
    }

    public AssessmentQuestionDTO getQuestion(int questionNumber) {
        List<AssessmentQuestionDTO> questions = getAllQuestions();
        if (questionNumber > 0 && questionNumber <= questions.size()) {
            return questions.get(questionNumber - 1);
        }
        return null;
    }

    @Transactional
    public Assessment saveAssessment(User user, List<Integer> answers) {
        Assessment assessment = new Assessment();
        assessment.setUser(user);
        assessment.setTitle("Mental Health Self-Assessment");
        assessment.setDescription("Self-assessment completed on "
                + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")));

        // Store individual scores
        assessment.setQ1Score(answers.get(0));
        assessment.setQ2Score(answers.get(1));
        assessment.setQ3Score(answers.get(2));
        assessment.setQ4Score(answers.get(3));
        assessment.setQ5Score(answers.get(4));
        assessment.setQ6Score(answers.get(5));
        assessment.setQ7Score(answers.get(6));
        assessment.setQ8Score(answers.get(7));

        // Calculate total score
        int totalScore = answers.stream().mapToInt(Integer::intValue).sum();
        assessment.setTotalScore(totalScore);
        assessment.setScore(totalScore); // Legacy field

        // Store answers as string
        assessment.setAnswers(String.join(",", answers.stream().map(String::valueOf).toArray(String[]::new)));

        // Categorize based on total score (0-32 possible)
        String category;
        String resultMessage;
        if (totalScore <= 8) {
            category = "LOW";
            resultMessage = "Low levels of mental health concerns";
        } else if (totalScore <= 16) {
            category = "MILD";
            resultMessage = "Mild mental health symptoms";
        } else if (totalScore <= 24) {
            category = "MODERATE";
            resultMessage = "Moderate mental health symptoms";
        } else {
            category = "SEVERE";
            resultMessage = "Severe mental health symptoms";
        }
        assessment.setCategory(category);
        assessment.setResult(resultMessage); // Legacy field

        // Generate content summary
        StringBuilder content = new StringBuilder();
        content.append("Assessment Results:\n\n");
        content.append("Total Score: ").append(totalScore).append("/32\n");
        content.append("Category: ").append(category).append("\n\n");
        content.append("Question Responses:\n");
        List<AssessmentQuestionDTO> questions = getAllQuestions();
        for (int i = 0; i < answers.size(); i++) {
            content.append((i + 1)).append(". ").append(questions.get(i).getQuestionText())
                    .append(" - Score: ").append(answers.get(i)).append("\n");
        }
        assessment.setContent(content.toString());

        // Save the assessment
        Assessment savedAssessment = assessmentRepository.save(assessment);

        // Update user's mental health scores based on assessment results
        int stressScore = (answers.get(0) != null ? answers.get(0) : 0) +
                (answers.get(1) != null ? answers.get(1) : 0); // Q1 + Q2
        int anxietyScore = (answers.get(2) != null ? answers.get(2) : 0) * 2; // Q3 * 2
        int wellbeingScore = 100 - totalScore; // Higher total score = lower wellbeing

        user.setStressLevel((stressScore * 100) / 8); // Convert to 0-100
        user.setAnxietyLevel((anxietyScore * 100) / 8); // Convert to 0-100
        user.setWellbeingScore(wellbeingScore);
        user.setLastAssessmentDate(java.time.LocalDateTime.now());

        userRepository.save(user);

        return savedAssessment;
    }

    public AssessmentResultDTO getAssessmentResult(Assessment assessment) {
        String message;
        String recommendation;

        switch (assessment.getCategory()) {
            case "LOW":
                message = "Your responses indicate low levels of mental health concerns.";
                recommendation = "Continue practicing self-care and maintaining healthy habits.";
                break;
            case "MILD":
                message = "Your responses indicate mild mental health symptoms.";
                recommendation = "Consider exploring our self-care tools and educational modules. Monitor your wellbeing regularly.";
                break;
            case "MODERATE":
                message = "Your responses indicate moderate mental health symptoms.";
                recommendation = "We recommend booking an appointment with a counselor and exploring our support resources.";
                break;
            case "SEVERE":
                message = "Your responses indicate severe mental health symptoms.";
                recommendation = "We strongly recommend scheduling an immediate appointment with a mental health professional. If you're in crisis, please contact emergency services.";
                break;
            default:
                message = "Assessment completed.";
                recommendation = "Review your results with a counselor.";
        }

        // Calculate category levels (Stress, Anxiety, Depression)
        int stressLevel = (assessment.getQ1Score() != null ? assessment.getQ1Score() : 0) +
                (assessment.getQ2Score() != null ? assessment.getQ2Score() : 0); // Max 8
        int anxietyLevel = (assessment.getQ3Score() != null ? assessment.getQ3Score() : 0) * 2; // Max 8
        int depressionLevel = (assessment.getQ4Score() != null ? assessment.getQ4Score() : 0) +
                (assessment.getQ6Score() != null ? assessment.getQ6Score() : 0) +
                (assessment.getQ7Score() != null ? assessment.getQ7Score() : 0) +
                (assessment.getQ8Score() != null ? assessment.getQ8Score() : 0); // Max 16

        return new AssessmentResultDTO(
                assessment.getTotalScore(),
                assessment.getCategory(),
                message,
                recommendation,
                (stressLevel * 100) / 8, // Convert to percentage
                (anxietyLevel * 100) / 8,
                (depressionLevel * 100) / 16);
    }

    public Assessment getLatestAssessment(User user) {
        return assessmentRepository.findFirstByUserOrderByCompletedAtDesc(user).orElse(null);
    }

    public List<Assessment> getUserAssessments(User user) {
        return assessmentRepository.findByUserOrderByCompletedAtDesc(user);
    }

    public Long getAssessmentCount(User user) {
        return assessmentRepository.countByUser(user);
    }
}
