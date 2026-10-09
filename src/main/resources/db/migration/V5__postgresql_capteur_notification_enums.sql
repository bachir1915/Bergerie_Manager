-- ============================================================================
--  Migration V5 — Hiérarchie Capteurs (JOINED), Notifications, PostgreSQL
--  Cible : PostgreSQL (profil prod).
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1) Tables filles pour l'héritage JOINED des Capteurs
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS capteurs_animaux (
    id BIGINT NOT NULL PRIMARY KEY,
    id_animal BIGINT,
    CONSTRAINT fk_capteur_animal_capteur FOREIGN KEY (id) REFERENCES capteurs (id) ON DELETE CASCADE,
    CONSTRAINT fk_capteur_animal_animal FOREIGN KEY (id_animal) REFERENCES animaux (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS capteurs_zones (
    id BIGINT NOT NULL PRIMARY KEY,
    id_zone BIGINT NOT NULL,
    CONSTRAINT fk_capteur_zone_capteur FOREIGN KEY (id) REFERENCES capteurs (id) ON DELETE CASCADE,
    CONSTRAINT fk_capteur_zone_zone FOREIGN KEY (id_zone) REFERENCES zones (id) ON DELETE CASCADE
);

-- Migration des anciens capteurs animal s'il existait une colonne id_animal sur capteurs
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'capteurs' AND column_name = 'id_animal'
    ) THEN
        INSERT INTO capteurs_animaux (id, id_animal)
        SELECT id, id_animal FROM capteurs WHERE id_animal IS NOT NULL
        ON CONFLICT (id) DO NOTHING;

        ALTER TABLE capteurs DROP COLUMN id_animal;
    END IF;
END $$;

-- ----------------------------------------------------------------------------
-- 2) Nouvelle table "notifications" (séparation Alerte / Notification)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    id_alerte BIGINT NOT NULL,
    id_utilisateur_dest BIGINT NOT NULL,
    canal VARCHAR(20) NOT NULL DEFAULT 'PUSH',
    date_envoi TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_lecture TIMESTAMP NULL,
    CONSTRAINT fk_notifications_alerte FOREIGN KEY (id_alerte) REFERENCES alertes (id) ON DELETE CASCADE,
    CONSTRAINT fk_notifications_utilisateur FOREIGN KEY (id_utilisateur_dest) REFERENCES utilisateurs (id) ON DELETE CASCADE
);

-- Migration des alertes existantes vers notifications si id_utilisateur_dest existe sur alertes
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'alertes' AND column_name = 'id_utilisateur_dest'
    ) THEN
        INSERT INTO notifications (id_alerte, id_utilisateur_dest, canal, date_envoi, date_lecture)
        SELECT id, id_utilisateur_dest, 'PUSH', date_heure, CASE WHEN statut_alerte = 'LUE' OR statut_alerte = 'TRAITEE' THEN date_heure ELSE NULL END
        FROM alertes WHERE id_utilisateur_dest IS NOT NULL;

        ALTER TABLE alertes DROP COLUMN id_utilisateur_dest;
    END IF;
END $$;

-- ----------------------------------------------------------------------------
-- 3) Mise à jour de la colonne type_zone sur la table zones
-- ----------------------------------------------------------------------------
ALTER TABLE zones ADD COLUMN IF NOT EXISTS type_zone VARCHAR(30) DEFAULT 'PATURAGE';

-- ----------------------------------------------------------------------------
-- 4) Harmonisation des statuts de santé et d'alertes existants
-- ----------------------------------------------------------------------------
UPDATE animaux SET statut_sante = 'SAIN' WHERE statut_sante = 'BON';
UPDATE animaux SET statut_sante = 'EN_OBSERVATION' WHERE statut_sante = 'MALADE';

UPDATE alertes SET statut_alerte = 'NOUVELLE' WHERE statut_alerte = 'NON_LUE';
UPDATE alertes SET statut_alerte = 'EN_COURS' WHERE statut_alerte = 'LUE';
UPDATE alertes SET niveau = 'CRITIQUE' WHERE niveau = 'URGENCE';
UPDATE alertes SET niveau = 'ELEVE' WHERE niveau = 'WARNING';
UPDATE alertes SET niveau = 'MODERE' WHERE niveau = 'INFO';
