-- ============================================================================
--  Migration V3 — Code d'identification des bergeries (format AA-76-VI-77)
--  Cible : MySQL (profil prod). À appliquer après V2.
--
--     mysql -u root -p bergerie_db < V3__code_bergerie.sql
-- ============================================================================

-- 1) Nouvelle colonne, unique. D'abord nullable : les bergeries existantes n'ont pas de code.
ALTER TABLE bergeries
    ADD COLUMN IF NOT EXISTS code VARCHAR(20) NULL,
    ADD CONSTRAINT uk_bergeries_code UNIQUE (code);

-- 2) Les bergeries existantes reçoivent automatiquement un code au démarrage de l'application
--    (voir BergerieCodeInitializer). Aucune action SQL supplémentaire n'est requise.
--
--    Si vous préférez remplir les codes manuellement, faites-le ici en respectant le format
--    (2 lettres MAJ - 2 chiffres - chiffre romain - 2 chiffres), par exemple :
--        UPDATE bergeries SET code = 'AB-12-IV-34' WHERE id = 1;

-- 3) Optionnel : rendre la colonne obligatoire une fois tous les codes renseignés.
--    ALTER TABLE bergeries MODIFY COLUMN code VARCHAR(20) NOT NULL;
