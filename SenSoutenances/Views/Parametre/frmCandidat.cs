using SenSoutenance.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.RegularExpressions;
using System.Windows.Forms;

namespace SenSoutenance.Views.Parametre
{
    public partial class frmCandidat : Form
    {
        
        // MODE SIMULATION (SANS DB)
       
        private List<Candidat> candidats = new List<Candidat>();

        public frmCandidat()
        {
            InitializeComponent();
        }

        
        // Chargement du formulaire
        
        private void frmCandidat_Load(object sender, EventArgs e)
        {
            ChargerCandidats();
            Effacer();
        }

        
        // Charger DataGridView
       
        private void ChargerCandidats()
        {
            dgCandidat.DataSource = null;
            dgCandidat.DataSource = candidats;

            // Masquer mot de passe
            if (dgCandidat.Columns["MotDePasse"] != null)
                dgCandidat.Columns["MotDePasse"].Visible = false;

            dgCandidat.ClearSelection();
        }

        
        // Nettoyage des champs

        private void Effacer()
        {
            txtNom.Clear();
            txtPrenom.Clear();
            txtTelephone.Clear();
            txtEmail.Clear();
            txtNom.Focus();
        }

       
        // Ajouter
        
        private void btnAjouter_Click(object sender, EventArgs e)
        {
            if (!VerifierChamps()) return;

            // Anti-doublon email
            

            int newId = candidats.Count == 0
                ? 1
                : candidats.Max(x => x.IdUtilisateur) + 1;

            Candidat c = new Candidat
            {
                IdUtilisateur = newId,
                NomUtilisateur = txtNom.Text.Trim(),
                PrenomUtilisateur = txtPrenom.Text.Trim(),
                TelUtilisateur = txtTelephone.Text.Trim(),
                EmailUtilisateur = txtEmail.Text.Trim(),
                MotDePasse = "1234" // simulation
            };

            candidats.Add(c);
            ChargerCandidats();
            Effacer();
        }

        
        // Sélectionner
      
        private void btnSelectionner_Click(object sender, EventArgs e)
        {
            if (dgCandidat.CurrentRow == null) return;

            Candidat c =
                (Candidat)dgCandidat.CurrentRow.DataBoundItem;

            txtNom.Text = c.NomUtilisateur;
            txtPrenom.Text = c.PrenomUtilisateur;
            txtTelephone.Text = c.TelUtilisateur;
            txtEmail.Text = c.EmailUtilisateur;
        }

        
        // Modifier
    
        private void btnModifier_Click(object sender, EventArgs e)
        {
            if (dgCandidat.CurrentRow == null)
            {
                MessageBox.Show("Veuillez sélectionner un candidat.");
                return;
            }

            if (!VerifierChamps()) return;

            Candidat c =
                (Candidat)dgCandidat.CurrentRow.DataBoundItem;

            // Anti-doublon email (sauf lui-même)
            if (candidats.Any(x =>
                x.EmailUtilisateur.Equals(txtEmail.Text.Trim(), StringComparison.OrdinalIgnoreCase)
                && x.IdUtilisateur != c.IdUtilisateur))
            {
                MessageBox.Show(
                    "Cet email existe déjà.",
                    "Doublon",
                    MessageBoxButtons.OK,
                    MessageBoxIcon.Warning
                );
                return;
            }

            c.NomUtilisateur = txtNom.Text.Trim();
            c.PrenomUtilisateur = txtPrenom.Text.Trim();
            c.TelUtilisateur = txtTelephone.Text.Trim();
            c.EmailUtilisateur = txtEmail.Text.Trim();

            ChargerCandidats();
            Effacer();
        }

    
        // Supprimer
       
        
        private void btnSupprimer_Click(object sender, EventArgs e)
        {
            if (dgCandidat.CurrentRow == null)
            {
                MessageBox.Show("Veuillez sélectionner un candidat.");
                return;
            }

            DialogResult rep = MessageBox.Show(
                "Voulez-vous vraiment supprimer ce candidat ?",
                "Confirmation",
                MessageBoxButtons.YesNo,
                MessageBoxIcon.Question
            );

            if (rep != DialogResult.Yes) return;

            Candidat c =
                (Candidat)dgCandidat.CurrentRow.DataBoundItem;

            candidats.Remove(c);
            ChargerCandidats();
            Effacer();
        }

       
        // Vérification des champs
        
        private bool VerifierChamps()
        {
            if (string.IsNullOrWhiteSpace(txtNom.Text) ||
                string.IsNullOrWhiteSpace(txtPrenom.Text) ||
                string.IsNullOrWhiteSpace(txtTelephone.Text) ||
                string.IsNullOrWhiteSpace(txtEmail.Text))
            {
                MessageBox.Show(
                    "Veuillez remplir tous les champs.",
                    "Validation",
                    MessageBoxButtons.OK,
                    MessageBoxIcon.Warning
                );
                return false;
            }

            // Validation email simple
            if (!Regex.IsMatch(txtEmail.Text.Trim(), @"@"))
            {
                MessageBox.Show(
                    "Email invalide.",
                    "Validation",
                    MessageBoxButtons.OK,
                    MessageBoxIcon.Warning
                );
                return false;
            }

            return true;
        }
    }
}
