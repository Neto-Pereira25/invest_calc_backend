package br.edu.ifpe.pdsc.investCalc.investCalc.services.userFinancialProfile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.userFinancialProfile.FinancialProfileAnswerRequestDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.userFinancialProfile.FinancialProfileHistoryDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.userFinancialProfile.FinancialProfileRequestDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.userFinancialProfile.FinancialProfileResponseDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.userFinancialProfile.FinancialProfileAnswer;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.userFinancialProfile.FinancialProfileResult;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.userFinancialProfile.FinancialProfile;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.userFinancialProfile.FinancialProfileResultRepository;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.userFinancialProfile.mapper.FinancialProfileMapper;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.userFinancialProfile.rules.FinancialProfileRuleEngine;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.userFinancialProfile.rules.FinancialProfileScore;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FinancialProfileServiceImpl implements FinancialProfileService {

    private final FinancialProfileResultRepository repository;
    private final FinancialProfileRuleEngine ruleEngine;
    private final FinancialProfileMapper mapper;

    @Override
    public FinancialProfileResponseDTO submitAssessment(
            FinancialProfileRequestDTO request,
            User authenticatedUser) {

        validateQuestionnaire(request);

        FinancialProfileScore score = ruleEngine.calculate(
                request.getAnswers());

        FinancialProfileResult result = buildResult(
                authenticatedUser,
                request,
                score);

        FinancialProfileResult savedResult = repository.save(result);

        return mapper.toResponseDTO(savedResult);
    }

    @Override
    @Transactional(readOnly = true)
    public FinancialProfileResponseDTO getCurrentProfile(
            User authenticatedUser) {

        FinancialProfileResult result = repository
                .findTopByUserOrderByAssessedAtDesc(
                        authenticatedUser)
                .orElseThrow(() -> new RuntimeException(
                        "Financial profile not found"));

        return mapper.toResponseDTO(result);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialProfileHistoryDTO> getHistory(
            User authenticatedUser) {

        return repository
                .findByUserOrderByAssessedAtDesc(
                        authenticatedUser)
                .stream()
                .map(mapper::toHistoryDTO)
                .toList();
    }

    private void validateQuestionnaire(
            FinancialProfileRequestDTO request) {

        if (request.getAnswers() == null ||
                request.getAnswers().size() != 10) {

            throw new IllegalArgumentException(
                    "The questionnaire must contain exactly 10 answers.");
        }
    }

    private FinancialProfileResult buildResult(
            User user,
            FinancialProfileRequestDTO request,
            FinancialProfileScore score) {

        FinancialProfileResult result = FinancialProfileResult.builder()
                .user(user)
                .profile(score.getMainProfile())
                .devedorScore(
                        score.get(FinancialProfile.DEVEDOR))
                .gastadorScore(
                        score.get(FinancialProfile.GASTADOR))
                .desligadoScore(
                        score.get(FinancialProfile.DESLIGADO))
                .poupadorScore(
                        score.get(FinancialProfile.POUPADOR))
                .investidorScore(
                        score.get(FinancialProfile.INVESTIDOR))
                .assessedAt(LocalDateTime.now())
                .build();

        List<FinancialProfileAnswer> answers = buildAnswers(result, request.getAnswers());

        result.setAnswers(answers);

        return result;
    }

    private List<FinancialProfileAnswer> buildAnswers(
            FinancialProfileResult result,
            List<FinancialProfileAnswerRequestDTO> requestAnswers) {

        return requestAnswers
                .stream()
                .map(answer -> FinancialProfileAnswer.builder()
                        .questionNumber(
                                answer.getQuestionNumber())
                        .selectedOption(
                                answer.getSelectedOption())
                        .result(result)
                        .build())
                .collect(Collectors.toList());
    }

}
