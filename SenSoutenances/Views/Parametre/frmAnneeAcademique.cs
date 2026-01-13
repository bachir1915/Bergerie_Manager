using SenSoutenance.Models;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace SenSoutenance.Views.Parametre
{
    public partial class frmAnneeAcademique : global::System.Windows.Forms.Form
    {
        public frmAnneeAcademique()
        {
            InitializeComponent();
        }

        BdSenSoutenanceContext db;
        private void frmAnneeAcademique_Load(object sender, EventArgs e)
        {
            try
            {
                db = new BdSenSoutenanceContext();
                dgAnneeAcademique.DataSource = db.anneeAcademiques.ToList();
            }
            catch (Exception ex)
            {
                string msg = ex.Message;
                Exception inner = ex.InnerException;
                while (inner != null)
                {
                    msg += "\n\n--- Inner Exception ---\n" + inner.Message;
                    inner = inner.InnerException;
                }
                MessageBox.Show(msg, "Erreur de connexion", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        //pour effacer
        public void Effacer()
        {
            txtLibelleAnneeAcademique.Clear();
            txtAnneeAcademiqueVal.Clear();
            dgAnneeAcademique.DataSource = db.anneeAcademiques.ToList();
            txtLibelleAnneeAcademique.Focus();

        }

        private void btnAjouter_Click(object sender, EventArgs e)
        {
            AnneeAcademique anneeAcademique = new AnneeAcademique()
            {
                LibelleAnneeAcademique = txtLibelleAnneeAcademique.Text,
                AnneeAcademiqueVal = int.Parse(txtAnneeAcademiqueVal.Text),
            };
            db.anneeAcademiques.Add(anneeAcademique); //ca ajoute au niveau de contexte
            db.SaveChanges(); //pour s'incroniger
            Effacer();
        }

        private void btnModifier_Click(object sender, EventArgs e)
        {
            int id = int.Parse(dgAnneeAcademique.CurrentRow.Cells[0].Value.
                ToString());
            AnneeAcademique anneeAcademique = db.anneeAcademiques.Find(id);
            anneeAcademique.LibelleAnneeAcademique = txtLibelleAnneeAcademique.Text;
            anneeAcademique.AnneeAcademiqueVal = int.Parse(txtAnneeAcademiqueVal.Text);
            db.SaveChanges();
            Effacer();
        }

        private void btnSupprimer_Click(object sender, EventArgs e)
        {
            int id = int.Parse(dgAnneeAcademique.CurrentRow.Cells[0].Value.ToString());
            AnneeAcademique anneeAcademique = db.anneeAcademiques.Find(id);
            db.anneeAcademiques.Remove(anneeAcademique);
            db.SaveChanges();
            Effacer();
        }

        private void btnSelectionner_Click(object sender, EventArgs e)
        {
            txtLibelleAnneeAcademique.Text= dgAnneeAcademique.CurrentRow.Cells[1].Value.ToString();
            txtAnneeAcademiqueVal.Text = dgAnneeAcademique.CurrentRow.Cells[2].Value.ToString();
        }
    }
}
