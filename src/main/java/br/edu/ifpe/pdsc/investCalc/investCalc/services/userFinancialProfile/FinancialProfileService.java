package br.edu.ifpe.pdsc.investCalc.investCalc.services.userFinancialProfile;

import java.util.List;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.userFinancialProfile.FinancialProfileHistoryDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.userFinancialProfile.FinancialProfileRequestDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.userFinancialProfile.FinancialProfileResponseDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;

public interface FinancialProfileService {

    FinancialProfileResponseDTO submitAssessment(FinancialProfileRequestDTO request, User authenticatedUser);

    FinancialProfileResponseDTO getCurrentProfile(User authenticatedUser);

    List<FinancialProfileHistoryDTO> getHistory(User authenticatedUser);
}
