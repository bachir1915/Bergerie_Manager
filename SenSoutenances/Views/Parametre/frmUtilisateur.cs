using SenSoutenance.Models;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Windows.Forms;

namespace SenSoutenance.Views.Parametre
{
    public partial class frmUtilisateur : Form
    {
       
        // LISTES TEMPORAIRES (SIMULATION)
       
        private List<Candidat> candidats = new List<Candidat>();
        private List<Professeur> professeurs = new List<Professeur>();
        private List<ChefDepartement> chefs = new List<ChefDepartement>();

        public frmUtilisateur()
        {
            InitializeComponent();
        }

        
        // LOAD FORM
      
        private void frmUtilisateur_Load(object sender, EventArgs e)
        {
            ChargerCandidats();
            ChargerProfesseurs();
            ChargerChefs();
        }

        
        // CANDIDAT 
       

        private void ChargerCandidats()
        {
            dgCandidat.DataSource = null;
            dgCandidat.DataSource = candidats.Select(c => new
            {
                c.IdUtilisateur,
                c.NomUtilisateur,
                c.PrenomUtilisateur,
                c.TelUtilisateur,
                c.EmailUtilisateur,
                c.MatriculeCandidat
            }).ToList();

            dgCandidat.ClearSelection();
        }

        private void btnAjouterCandidat_Click(object sender, EventArgs e)
        {
            if (!VerifierChampsCandidat()) return;

            int newId = candidats.Count == 0 ? 1 : candidats.Max(x => x.IdUtilisateur) + 1;

            Candidat c = new Candidat
            {
                IdUtilisateur = newId,
                NomUtilisateur = txtNomCandidat.Text.Trim(),
                PrenomUtilisateur = txtPrenomCandidat.Text.Trim(),
                TelUtilisateur = txtTelephoneCandidat.Text.Trim(),
                EmailUtilisateur = txtEmailCandidat.Text.Trim(),
                MatriculeCandidat = txtMatriculeCandidat.Text.Trim()
            };

            candidats.Add(c);
            ChargerCandidats();
            EffacerCandidat();
        }

        private void btnSelectionnerCandidat_Click(object sender, EventArgs e)
        {
            if (dgCandidat.CurrentRow == null) return;

            txtNomCandidat.Text = dgCandidat.CurrentRow.Cells["NomUtilisateur"].Value.ToString();
            txtPrenomCandidat.Text = dgCandidat.CurrentRow.Cells["PrenomUtilisateur"].Value.ToString();
            txtTelephoneCandidat.Text = dgCandidat.CurrentRow.Cells["TelUtilisateur"].Value.ToString();
            txtEmailCandidat.Text = dgCandidat.CurrentRow.Cells["EmailUtilisateur"].Value.ToString();
            txtMatriculeCandidat.Text = dgCandidat.CurrentRow.Cells["MatriculeCandidat"].Value.ToString();
        }

        private void btnModifierCandidat_Click(object sender, EventArgs e)
        {
            if (dgCandidat.CurrentRow == null) return;
            if (!VerifierChampsCandidat()) return;

            int id = Convert.ToInt32(dgCandidat.CurrentRow.Cells["IdUtilisateur"].Value);
            Candidat c = candidats.FirstOrDefault(x => x.IdUtilisateur == id);

            if (c == null) return;

            c.NomUtilisateur = txtNomCandidat.Text.Trim();
            c.PrenomUtilisateur = txtPrenomCandidat.Text.Trim();
            c.TelUtilisateur = txtTelephoneCandidat.Text.Trim();
            c.EmailUtilisateur = txtEmailCandidat.Text.Trim();
            c.MatriculeCandidat = txtMatriculeCandidat.Text.Trim();

            ChargerCandidats();
            EffacerCandidat();
        }

        private void btnSupprimerCandidat_Click(object sender, EventArgs e)
        {
            if (dgCandidat.CurrentRow == null) return;

            int id = Convert.ToInt32(dgCandidat.CurrentRow.Cells["IdUtilisateur"].Value);
            Candidat c = candidats.FirstOrDefault(x => x.IdUtilisateur == id);

            if (c == null) return;

            if (MessageBox.Show("Supprimer ce candidat ?", "Confirmation",
                MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
            {
                candidats.Remove(c);
                ChargerCandidats();
                EffacerCandidat();
            }
        }

        private bool VerifierChampsCandidat()
        {
            if (string.IsNullOrWhiteSpace(txtNomCandidat.Text) ||
                string.IsNullOrWhiteSpace(txtPrenomCandidat.Text) ||
                string.IsNullOrWhiteSpace(txtTelephoneCandidat.Text) ||
                string.IsNullOrWhiteSpace(txtEmailCandidat.Text) ||
                string.IsNullOrWhiteSpace(txtMatriculeCandidat.Text))
            {
                MessageBox.Show("Veuillez remplir tous les champs.");
                return false;
            }

            if (!txtEmailCandidat.Text.Contains("@"))
            {
                MessageBox.Show("Email invalide.");
                return false;
            }

            return true;
        }

        private void EffacerCandidat()
        {
            txtNomCandidat.Clear();
            txtPrenomCandidat.Clear();
            txtTelephoneCandidat.Clear();
            txtEmailCandidat.Clear();
            txtMatriculeCandidat.Clear();
        }

      
        // PROFESSEUR 
       
        private void ChargerProfesseurs()
        {
            dgProfesseur.DataSource = null;
            dgProfesseur.DataSource = professeurs.Select(p => new
            {
                p.IdUtilisateur,
                p.NomUtilisateur,
                p.PrenomUtilisateur,
                p.TelUtilisateur,
                p.EmailUtilisateur,
                p.SpecialiteProfesseur
            }).ToList();

            dgProfesseur.ClearSelection();
        }

        private void btnAjouterProfesseur_Click(object sender, EventArgs e)
        {
            if (!VerifierChampsProfesseur()) return;

            int newId = professeurs.Count == 0 ? 1 : professeurs.Max(x => x.IdUtilisateur) + 1;

            Professeur p = new Professeur
            {
                IdUtilisateur = newId,
                NomUtilisateur = txtProfNom.Text.Trim(),
                PrenomUtilisateur = txtProfPrenom.Text.Trim(),
                TelUtilisateur = txtProfTelephone.Text.Trim(),
                EmailUtilisateur = txtProfEmail.Text.Trim(),
                SpecialiteProfesseur = txtProfSpecialite.Text.Trim(),
                MotDePasse = "1234"
            };

            professeurs.Add(p);
            ChargerProfesseurs();
            EffacerProfesseur();
        }

        private void btnSelectionnerProfesseur_Click(object sender, EventArgs e)
        {
            if (dgProfesseur.CurrentRow == null) return;

            txtProfNom.Text = dgProfesseur.CurrentRow.Cells["NomUtilisateur"].Value.ToString();
            txtProfPrenom.Text = dgProfesseur.CurrentRow.Cells["PrenomUtilisateur"].Value.ToString();
            txtProfTelephone.Text = dgProfesseur.CurrentRow.Cells["TelUtilisateur"].Value.ToString();
            txtProfEmail.Text = dgProfesseur.CurrentRow.Cells["EmailUtilisateur"].Value.ToString();
            txtProfSpecialite.Text = dgProfesseur.CurrentRow.Cells["SpecialiteProfesseur"].Value.ToString();
        }

        private void btnModifierProfesseur_Click(object sender, EventArgs e)
        {
            if (dgProfesseur.CurrentRow == null) return;
            if (!VerifierChampsProfesseur()) return;

            int id = Convert.ToInt32(dgProfesseur.CurrentRow.Cells["IdUtilisateur"].Value);
            Professeur p = professeurs.FirstOrDefault(x => x.IdUtilisateur == id);

            if (p == null) return;

            p.NomUtilisateur = txtProfNom.Text.Trim();
            p.PrenomUtilisateur = txtProfPrenom.Text.Trim();
            p.TelUtilisateur = txtProfTelephone.Text.Trim();
            p.EmailUtilisateur = txtProfEmail.Text.Trim();
            p.SpecialiteProfesseur = txtProfSpecialite.Text.Trim();

            ChargerProfesseurs();
            EffacerProfesseur();
        }

        private void btnSupprimerProfesseur_Click(object sender, EventArgs e)
        {
            if (dgProfesseur.CurrentRow == null) return;

            int id = Convert.ToInt32(dgProfesseur.CurrentRow.Cells["IdUtilisateur"].Value);
            Professeur p = professeurs.FirstOrDefault(x => x.IdUtilisateur == id);

            if (p == null) return;

            if (MessageBox.Show("Supprimer ce professeur ?", "Confirmation",
                MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
            {
                professeurs.Remove(p);
                ChargerProfesseurs();
                EffacerProfesseur();
            }
        }

        private bool VerifierChampsProfesseur()
        {
            if (string.IsNullOrWhiteSpace(txtProfNom.Text) ||
                string.IsNullOrWhiteSpace(txtProfPrenom.Text) ||
                string.IsNullOrWhiteSpace(txtProfTelephone.Text) ||
                string.IsNullOrWhiteSpace(txtProfEmail.Text) ||
                string.IsNullOrWhiteSpace(txtProfSpecialite.Text))
            {
                MessageBox.Show("Veuillez remplir tous les champs.");
                return false;
            }

            if (!txtProfEmail.Text.Contains("@"))
            {
                MessageBox.Show("Email invalide.");
                return false;
            }

            return true;
        }

        private void EffacerProfesseur()
        {
            txtProfNom.Clear();
            txtProfPrenom.Clear();
            txtProfTelephone.Clear();
            txtProfEmail.Clear();
            txtProfSpecialite.Clear();
        }

       
        // CHEF DE DEPARTEMENT 
        

        private void ChargerChefs()
        {
            dgChefDepartement.DataSource = null;
            dgChefDepartement.DataSource = chefs.Select(c => new
            {
                c.IdUtilisateur,
                c.NomUtilisateur,
                c.PrenomUtilisateur,
                c.TelUtilisateur,
                c.EmailUtilisateur
            }).ToList();

            dgChefDepartement.ClearSelection();
            dgCandidat.AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill;
            dgCandidat.SelectionMode = DataGridViewSelectionMode.FullRowSelect;
            dgCandidat.MultiSelect = false;
            dgCandidat.ReadOnly = true;
            dgCandidat.AllowUserToAddRows = false;
            dgCandidat.RowHeadersVisible = false;
            dgCandidat.BackgroundColor = Color.White;
            dgCandidat.BorderStyle = BorderStyle.None;

        }

        private void btnAjouterChef_Click(object sender, EventArgs e)
        {
            if (!VerifierChampsChef()) return;

            int newId = chefs.Count == 0 ? 1 : chefs.Max(x => x.IdUtilisateur) + 1;

            ChefDepartement c = new ChefDepartement
            {
                IdUtilisateur = newId,
                NomUtilisateur = txtChefNom.Text.Trim(),
                PrenomUtilisateur = txtChefPrenom.Text.Trim(),
                TelUtilisateur = txtChefTelephone.Text.Trim(),
                EmailUtilisateur = txtChefEmail.Text.Trim(),
                MotDePasse = "1234"
            };

            chefs.Add(c);
            ChargerChefs();
            EffacerChef();
        }

        private void btnSelectionnerChef_Click(object sender, EventArgs e)
        {
            if (dgChefDepartement.CurrentRow == null) return;

            txtChefNom.Text = dgChefDepartement.CurrentRow.Cells["NomUtilisateur"].Value.ToString();
            txtChefPrenom.Text = dgChefDepartement.CurrentRow.Cells["PrenomUtilisateur"].Value.ToString();
            txtChefTelephone.Text = dgChefDepartement.CurrentRow.Cells["TelUtilisateur"].Value.ToString();
            txtChefEmail.Text = dgChefDepartement.CurrentRow.Cells["EmailUtilisateur"].Value.ToString();
        }

        private void btnModifierChef_Click(object sender, EventArgs e)
        {
            if (dgChefDepartement.CurrentRow == null) return;
            if (!VerifierChampsChef()) return;

            int id = Convert.ToInt32(dgChefDepartement.CurrentRow.Cells["IdUtilisateur"].Value);
            ChefDepartement c = chefs.FirstOrDefault(x => x.IdUtilisateur == id);

            if (c == null) return;

            c.NomUtilisateur = txtChefNom.Text.Trim();
            c.PrenomUtilisateur = txtChefPrenom.Text.Trim();
            c.TelUtilisateur = txtChefTelephone.Text.Trim();
            c.EmailUtilisateur = txtChefEmail.Text.Trim();

            ChargerChefs();
            EffacerChef();
        }

        private void btnSupprimerChef_Click(object sender, EventArgs e)
        {
            if (dgChefDepartement.CurrentRow == null) return;

            int id = Convert.ToInt32(dgChefDepartement.CurrentRow.Cells["IdUtilisateur"].Value);
            ChefDepartement c = chefs.FirstOrDefault(x => x.IdUtilisateur == id);

            if (c == null) return;

            if (MessageBox.Show("Supprimer ce chef ?", "Confirmation",
                MessageBoxButtons.YesNo, MessageBoxIcon.Question) == DialogResult.Yes)
            {
                chefs.Remove(c);
                ChargerChefs();
                EffacerChef();
            }
        }

        private bool VerifierChampsChef()
        {
            if (string.IsNullOrWhiteSpace(txtChefNom.Text) ||
                string.IsNullOrWhiteSpace(txtChefPrenom.Text) ||
                string.IsNullOrWhiteSpace(txtChefTelephone.Text) ||
                string.IsNullOrWhiteSpace(txtChefEmail.Text))
            {
                MessageBox.Show("Veuillez remplir tous les champs.");
                return false;
            }

            if (!txtChefEmail.Text.Contains("@"))
            {
                MessageBox.Show("Email invalide.");
                return false;
            }

            return true;
        }

        private void EffacerChef()
        {
            txtChefNom.Clear();
            txtChefPrenom.Clear();
            txtChefTelephone.Clear();
            txtChefEmail.Clear();
        }

        private void txtChefPrenom_TextChanged(object sender, EventArgs e)
        {

        }

        private void tabChefDepartement_Click(object sender, EventArgs e)
        {

        }

        private void dgCandidat_CellContentClick(object sender, DataGridViewCellEventArgs e)
        {

        }
    }
}
