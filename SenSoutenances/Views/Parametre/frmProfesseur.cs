using SenSoutenance.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;

namespace SenSoutenance.Views.Parametre
{
    public partial class frmProfesseur : Form
    {
        
        // MODE SIMULATION (PAS DE DB)
        
        private List<Professeur> professeurs = new List<Professeur>();

        public frmProfesseur()
        {
            InitializeComponent();
        }

        
        // Chargement du formulaire
       
        private void frmProfesseur_Load(object sender, EventArgs e)
        {
            ChargerProfesseurs();
            Effacer();
        }

        
        // Charger DataGridView
       
        private void ChargerProfesseurs()
        {
            dgProfesseur.DataSource = null;
            dgProfesseur.DataSource = professeurs;
            dgProfesseur.ClearSelection();
        }

       
        // Réinitialiser les champs
        
        private void Effacer()
        {
            txtNom.Clear();
            txtPrenom.Clear();
            txtTelephone.Clear();
            txtEmail.Clear();
            txtSpecialite.Clear();
            txtNom.Focus();
        }

        
        // Ajouter
       
        private void btnAjouter_Click(object sender, EventArgs e)
        {
            if (!VerifierChamps()) return;

            int newId = professeurs.Count == 0
                ? 1
                : professeurs.Max(p => p.IdUtilisateur) + 1;

            Professeur prof = new Professeur
            {
                IdUtilisateur = newId,
                NomUtilisateur = txtNom.Text.Trim(),
                PrenomUtilisateur = txtPrenom.Text.Trim(),
                TelUtilisateur = txtTelephone.Text.Trim(),
                EmailUtilisateur = txtEmail.Text.Trim(),
                SpecialiteProfesseur = txtSpecialite.Text.Trim(),
                MotDePasse = "1234"
            };

            professeurs.Add(prof);
            ChargerProfesseurs();
            Effacer();
        }

       
        // Sélectionner
        
        private void btnSelectionner_Click(object sender, EventArgs e)
        {
            if (dgProfesseur.CurrentRow == null) return;

            Professeur prof =
                (Professeur)dgProfesseur.CurrentRow.DataBoundItem;

            txtNom.Text = prof.NomUtilisateur;
            txtPrenom.Text = prof.PrenomUtilisateur;
            txtTelephone.Text = prof.TelUtilisateur;
            txtEmail.Text = prof.EmailUtilisateur;
            txtSpecialite.Text = prof.SpecialiteProfesseur;
        }

        
        // Modifier
        
        private void btnModifier_Click(object sender, EventArgs e)
        {
            if (dgProfesseur.CurrentRow == null)
            {
                MessageBox.Show("Veuillez sélectionner un professeur.");
                return;
            }

            if (!VerifierChamps()) return;

            Professeur prof =
                (Professeur)dgProfesseur.CurrentRow.DataBoundItem;

            prof.NomUtilisateur = txtNom.Text.Trim();
            prof.PrenomUtilisateur = txtPrenom.Text.Trim();
            prof.TelUtilisateur = txtTelephone.Text.Trim();
            prof.EmailUtilisateur = txtEmail.Text.Trim();
            prof.SpecialiteProfesseur = txtSpecialite.Text.Trim();

            ChargerProfesseurs();
            Effacer();
        }

      
        // Supprimer
        
        private void btnSupprimer_Click(object sender, EventArgs e)
        {
            if (dgProfesseur.CurrentRow == null)
            {
                MessageBox.Show("Veuillez sélectionner un professeur.");
                return;
            }

            DialogResult rep = MessageBox.Show(
                "Voulez-vous vraiment supprimer ce professeur ?",
                "Confirmation",
                MessageBoxButtons.YesNo,
                MessageBoxIcon.Question
            );

            if (rep != DialogResult.Yes) return;

            Professeur prof =
                (Professeur)dgProfesseur.CurrentRow.DataBoundItem;

            professeurs.Remove(prof);
            ChargerProfesseurs();
            Effacer();
        }

        
        // Vérification des champs
     
        private bool VerifierChamps()
        {
            if (string.IsNullOrWhiteSpace(txtNom.Text) ||
                string.IsNullOrWhiteSpace(txtPrenom.Text) ||
                string.IsNullOrWhiteSpace(txtTelephone.Text) ||
                string.IsNullOrWhiteSpace(txtEmail.Text) ||
                string.IsNullOrWhiteSpace(txtSpecialite.Text))
            {
                MessageBox.Show(
                    "Veuillez remplir tous les champs.",
                    "Validation",
                    MessageBoxButtons.OK,
                    MessageBoxIcon.Warning
                );
                return false;
            }

            return true;
        }

        private void frmProfesseur_Load_1(object sender, EventArgs e)
        {

        }
    }
}
