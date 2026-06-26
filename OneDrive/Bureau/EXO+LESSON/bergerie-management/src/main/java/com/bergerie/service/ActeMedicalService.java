package com.bergerie.service;

import com.bergerie.entity.ActeMedical;
import com.bergerie.enums.TypeSoin;
import com.bergerie.repository.ActeMedicalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActeMedicalService {

    private final ActeMedicalRepository acteMedicalRepository;

    public List<ActeMedical> findAll() {
        return acteMedicalRepository.findAll();
    }

    public List<ActeMedical> findByAnimal(Long animalId) {
        return acteMedicalRepository.findByAnimalIdOrderByDateSoinDesc(animalId);
    }

    public List<ActeMedical> findByVeterinaire(Long vetId) {
        return acteMedicalRepository.findByUtilisateurIdOrderByDateSoinDesc(vetId);
    }

    public ActeMedical findById(Long id) {
        return acteMedicalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Acte médical non trouvé avec id: " + id));
    }

    public ActeMedical save(ActeMedical acteMedical) {
        return acteMedicalRepository.save(acteMedical);
    }

    public void delete(Long id) {
        acteMedicalRepository.deleteById(id);
    }

    public List<ActeMedical> rappelsProchains() {
        LocalDate today = LocalDate.now();
        LocalDate inSevenDays = today.plusDays(7);
        return acteMedicalRepository.findByProchainRappelBetween(today, inSevenDays);
    }

    public long countByType(TypeSoin type) {
        return acteMedicalRepository.countByTypeSoin(type);
    }
}
