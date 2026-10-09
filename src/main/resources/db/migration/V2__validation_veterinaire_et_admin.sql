-- ============================================================================
--  Migration V2 — Rôle ADMIN + validation des comptes vétérinaires
--  Cible : MySQL (profil prod). Idempotente autant que possible.
--  À appliquer sur une base EXISTANTE sans perdre de données.
--
--  Application manuelle :
--     mysql -u root -p bergerie_db < V2__validation_veterinaire_et_admin.sql
--  (Le projet n'utilise pas encore Flyway ; le nommage Vx__ est une convention.)
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1) Table des administrateurs (héritage JOINED depuis "utilisateurs")
--    Le rôle est stocké dans utilisateurs.role / utilisateurs.dtype.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS admins (
    id BIGINT NOT NULL PRIMARY KEY,
    CONSTRAINT fk_admins_utilisateur FOREIGN KEY (id) REFERENCES utilisateurs (id)
);

-- ----------------------------------------------------------------------------
-- 2) Nouvelles colonnes sur "veterinaires"
--    NB : MySQL < 8.0.29 ne supporte pas "ADD COLUMN IF NOT EXISTS".
--    Si votre version ne le supporte pas, retirez "IF NOT EXISTS" ci-dessous.
-- ----------------------------------------------------------------------------
ALTER TABLE veterinaires
    ADD COLUMN IF NOT EXISTS specialite VARCHAR(255) NULL,
    ADD COLUMN IF NOT EXISTS statut_validation VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
    ADD COLUMN IF NOT EXISTS motif_suspension TEXT NULL,
    ADD COLUMN IF NOT EXISTS justificatif_nom_fichier VARCHAR(255) NULL,
    ADD COLUMN IF NOT EXISTS justificatif_type_contenu VARCHAR(255) NULL,
    ADD COLUMN IF NOT EXISTS justificatif_chemin VARCHAR(500) NULL;

-- Le vétérinaire se connecte avec (email + code de bergerie) : plus de numéro d'ordre.
ALTER TABLE veterinaires DROP COLUMN IF EXISTS numero_ordre;

-- ----------------------------------------------------------------------------
-- 3) Données existantes : les vétérinaires déjà en base sont considérés comme
--    VALIDE, pour ne pas les bloquer rétroactivement sur la plateforme.
--    (Les nouvelles inscriptions arriveront bien en EN_ATTENTE via l'application.)
-- ----------------------------------------------------------------------------
UPDATE veterinaires
   SET statut_validation = 'VALIDE'
 WHERE statut_validation IS NULL
    OR statut_validation = ''
    OR statut_validation = 'EN_ATTENTE';

-- ----------------------------------------------------------------------------
-- 4) Compte ADMIN créé manuellement (aucune inscription publique pour ce rôle).
--
--    Option A (recommandée) : laisser l'application créer le compte au premier
--    démarrage via AdminInitializer, configurable dans application-prod.properties :
--        app.admin.email=admin@bergerie.com
--        app.admin.password=...            (encodé en BCrypt automatiquement)
--
--    Option B : insertion manuelle ci-dessous. Remplacez d'abord le hash par le
--    vôtre :  new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
--                 .encode("votre_mot_de_passe")
-- ----------------------------------------------------------------------------
-- INSERT INTO utilisateurs (nom, prenom, email, telephone, mot_de_passe, role, statut_compte, date_creation, dtype)
-- SELECT 'Admin', 'Système', 'admin@bergerie.com', '0600000000',
--        '<VOTRE_HASH_BCRYPT>',
--        'ADMIN', 'ACTIF', NOW(), 'ADMIN'
--   FROM DUAL
--  WHERE NOT EXISTS (SELECT 1 FROM utilisateurs WHERE email = 'admin@bergerie.com');
--
-- INSERT INTO admins (id)
-- SELECT u.id FROM utilisateurs u
--  WHERE u.email = 'admin@bergerie.com'
--    AND NOT EXISTS (SELECT 1 FROM admins a WHERE a.id = u.id);
