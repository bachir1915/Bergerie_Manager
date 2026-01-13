using SenSoutenance.Models;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace SenSoutenance
{
    public partial class frmConnexion : global::System.Windows.Forms.Form
    {
        BdSenSoutenanceContext db = new BdSenSoutenanceContext();

        public frmConnexion()
        {
            InitializeComponent();
        }

        private void btnSeConnecter_Click(object sender, EventArgs e)
        {
            //// Récupérer le hash du mot de passe stocké en base pour cet utilisateur
            //var utilisateur = db.utilisateurs
            //    .Where(u => u.EmailUtilisateur == txtIdentifiant.Text) // Utilisation de l'Email comme identifiant
            //    .FirstOrDefault();

            //if (utilisateur == null)
            //{
            //    MessageBox.Show("Identifiant incorrect !", "Erreur", MessageBoxButtons.OK, MessageBoxIcon.Error);
            //    return;
            //}

            //// Vérifier le mot de passe avec le Md5Helper
            //if (Shared.Md5Helper.VerifyMd5Hash(txtMotDePasse.Text, utilisateur.MotDePasse))
            //{
            //    // Connexion réussie
              
            //}
            //else
            //{
            //    MessageBox.Show("Mot de passe incorrect !", "Erreur", MessageBoxButtons.OK, MessageBoxIcon.Error);
            //}
            frmMDI f = new frmMDI();
            f.Show();
            this.Hide();
        }

        private void btnQuitter_Click(object sender, EventArgs e)
        {
            Application.Exit();
        }
    }
}

