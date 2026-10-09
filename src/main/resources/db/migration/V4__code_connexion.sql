-- ============================================================================
--  Migration V4 — Connexion par email + code (au lieu du mot de passe)
--  Cible : MySQL (profil prod). À appliquer après V3.
--
--     mysql -u root -p bergerie_db < V4__code_connexion.sql
-- ============================================================================

-- Code personnel de connexion :
--   ADMIN       -> ADM-XXXX-XXXX
--   ELEVEUR     -> ELV-000000
--   VETERINAIRE -> NULL (le vétérinaire se connecte avec le code d'une bergerie)
ALTER TABLE utilisateurs
    ADD COLUMN IF NOT EXISTS code_connexion VARCHAR(20) NULL,
    ADD CONSTRAINT uk_utilisateurs_code_connexion UNIQUE (code_connexion);

-- Attribuer un code aux comptes ADMIN / ELEVEUR existants.
-- Adaptez les valeurs ; elles doivent respecter le format et rester uniques.
--   UPDATE utilisateurs SET code_connexion = 'ADM-ROOT-0001' WHERE role = 'ADMIN'   AND code_connexion IS NULL;
--   UPDATE utilisateurs SET code_connexion = CONCAT('ELV-', LPAD(id, 6, '0'))       WHERE role = 'ELEVEUR' AND code_connexion IS NULL;

-- Le mot de passe (mot_de_passe) n'est plus utilisé pour la connexion applicative :
-- la colonne reste NOT NULL, on y stocke désormais une valeur aléatoire inutilisable.
