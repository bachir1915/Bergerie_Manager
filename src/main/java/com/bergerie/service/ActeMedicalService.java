package com.bergerie.service;

import com.bergerie.entity.ActeMedical;
import com.bergerie.enums.TypeSoin;
import com.bergerie.repository.ActeMedicalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

// @Service : Indique à Spring que cette classe contient la LOGIQUE MÉTIER de l'application.
//             Spring la détecte automatiquement et la gère dans son conteneur.
//             Elle fait le lien entre le Controller (qui reçoit les requêtes) 
//             et le Repository (qui accède à la base de données).
//             Sémantiquement, @Service = "Je suis un service qui traite des règles métier".
@Service

// @RequiredArgsConstructor (Lombok) : Génère un constructeur avec le champ "final" acteMedicalRepository.
//                                      Spring injecte automatiquement le repository via ce constructeur.
//                                      C'est l'injection de dépendances par constructeur (recommandée).
@RequiredArgsConstructor
public class ActeMedicalService {

    // Le repository est injecté automatiquement par Spring grâce à @RequiredArgsConstructor.
    // "final" garantit que ce champ ne sera jamais null ni réassigné.
    private final ActeMedicalRepository acteMedicalRepository;

    // Récupère TOUS les actes médicaux en base de données.
    // findAll() est une méthode héritée de JpaRepository, pas besoin de l'écrire.
    public List<ActeMedical> findAll() {
        return acteMedicalRepository.findAll();
    }

    // Récupère les actes médicaux d'un animal spécifique, triés du plus récent au plus ancien.
    public List<ActeMedical> findByAnimal(Long animalId) {
        return acteMedicalRepository.findByAnimalIdOrderByDateSoinDesc(animalId);
    }

    // Récupère les actes médicaux effectués par un vétérinaire spécifique.
    public List<ActeMedical> findByVeterinaire(Long vetId) {
        return acteMedicalRepository.findByUtilisateurIdOrderByDateSoinDesc(vetId);
    }

    // Cherche un acte médical par son ID.
    // orElseThrow() : Si l'acte n'existe pas, une exception est levée avec un message clair.
    public ActeMedical findById(Long id) {
        return acteMedicalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Acte médical non trouvé avec id: " + id));
    }

    // Sauvegarde ou met à jour un acte médical en base de données.
    // Si l'objet a déjà un ID → UPDATE. S'il n'en a pas → INSERT.
    public ActeMedical save(ActeMedical acteMedical) {
        return acteMedicalRepository.save(acteMedical);
    }

    // Supprime un acte médical de la base de données par son ID.
    public void delete(Long id) {
        acteMedicalRepository.deleteById(id);
    }

    // Récupère les rappels médicaux prévus dans les 7 prochains jours.
    // Utile pour alerter les vétérinaires des soins à venir.
    public List<ActeMedical> rappelsProchains() {
        LocalDate today = LocalDate.now();
        LocalDate inSevenDays = today.plusDays(7);
        return acteMedicalRepository.findByProchainRappelBetween(today, inSevenDays);
    }

    // Compte le nombre d'actes médicaux d'un type donné (ex: VACCINATION, TRAITEMENT...).
    public long countByType(TypeSoin type) {
        return acteMedicalRepository.countByTypeSoin(type);
    }
}
