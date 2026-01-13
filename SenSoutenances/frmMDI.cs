using SenSoutenance.Views.Parametre;
using System;
using System.Drawing;
using System.Linq;
using System.Windows.Forms;

namespace SenSoutenance
{
    public partial class frmMDI : Form
    {
        public frmMDI()
        {
            InitializeComponent();
        }

       
        // Méthode : fermer tous les formulaires enfants MDI
       
        private void FermerTousLesForms()
        {
            foreach (Form f in this.MdiChildren)
            {
                f.Close();
            }
        }

        // =====================================================
        // Méthode générique : ouvrir un formulaire dans le MDI
        // =====================================================
        private void OuvrirForm<T>(string titre) where T : Form, new()
        {
            // Vérifier si le formulaire est déjà ouvert
            Form formExistant = this.MdiChildren
                .FirstOrDefault(f => f is T);

            if (formExistant != null)
            {
                formExistant.BringToFront();
                return;
            }

            FermerTousLesForms();

            Form form = new T
            {
                MdiParent = this,
                WindowState = FormWindowState.Maximized
            };

            this.Text = $"Sen Soutenance - [{titre}]";
            form.Show();
        }

        
        // Chargement du formulaire MDI
        
        private void frmMDI_Load(object sender, EventArgs e)
        {
            // Plein écran déjà géré par Designer
            // Gestion des droits

            if (SessionUtilisateur.Role == "Admin")
            {
                securiteToolStripMenuItem.Visible = true;
            }
            else
            {
                securiteToolStripMenuItem.Visible = false;
            }
        }

        
        // MENU PARAMÈTRE
      
        private void anneeAcademiqueToolStripMenuItem_Click(object sender, EventArgs e)
        {
            OuvrirForm<frmAnneeAcademique>("Année Académique");
        }

        private void sessionToolStripMenuItem_Click(object sender, EventArgs e)
        {
            OuvrirForm<frmSession>("Session");
        }

        private void professeurToolStripMenuItem_Click(object sender, EventArgs e)
        {
            OuvrirForm<frmProfesseur>("Professeur");
        }

        private void candidatToolStripMenuItem_Click(object sender, EventArgs e)
        {
            OuvrirForm<frmCandidat>("Candidat");
        }


       
        // MENU ACTION
     
        private void seDeconnecterToolStripMenuItem_Click(object sender, EventArgs e)
        {
            DialogResult rep = MessageBox.Show(
                "Voulez-vous vraiment vous déconnecter ?",
                "Déconnexion",
                MessageBoxButtons.YesNo,
                MessageBoxIcon.Question
            );

            if (rep == DialogResult.Yes)
            {
                new frmConnexion().Show();
                this.Close();
            }
        }

        private void quitterToolStripMenuItem_Click(object sender, EventArgs e)
        {
            DialogResult rep = MessageBox.Show(
                "Voulez-vous vraiment quitter l'application ?",
                "Quitter",
                MessageBoxButtons.YesNo,
                MessageBoxIcon.Warning
            );

            if (rep == DialogResult.Yes)
            {
                Application.Exit();
            }
        }

        private void utilisateurToolStripMenuItem_Click(object sender, EventArgs e)
        {
            FermerTousLesForms();

            frmUtilisateur f = new frmUtilisateur();
            f.MdiParent = this;
            f.WindowState = FormWindowState.Maximized;
            f.Show();
        }

        private void securiteToolStripMenuItem_Click(object sender, EventArgs e)
        {

        }
    }
}
