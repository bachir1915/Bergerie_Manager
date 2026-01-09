using AppSenSoutenance.Models;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace AppSenSoutenance.View.parametre
{
    public partial class frmAnneeAcademique : Form
    {
        public frmAnneeAcademique()
        {
            InitializeComponent();
        }
        BdSensoutenanceContext db= new BdSensoutenanceContext();

        private void dgAnneeAcademique_CellContentClick(object sender, DataGridViewCellEventArgs e)
        {

        }
        public void EffacerChamps()
        {
            txtLibelleAnneeAcademique.Clear();
            txtAnneeAcademiqueVal.Clear();
            // Recharger la datagridview
            dgAnneeAcademique.DataSource= db.AnneeAcademiques.ToList();
            // Positionner le focus sur le premier champ
            txtLibelleAnneeAcademique.Focus();
        }

        private void button3_Click(object sender, EventArgs e)
        {
            AnneeAcademique annee = new AnneeAcademique
            {
                LibelleAnneeAcademique= txtLibelleAnneeAcademique.Text,
                AnneeAcademiqueVal= int.Parse( txtAnneeAcademiqueVal.Text)
                
                
            };
            //Ajouter l'objet annee dans la base de donnees(context
            db.AnneeAcademiques.Add(annee);
            db.SaveChanges();
            EffacerChamps();
        }

        private void frmAnneeAcademique_Load(object sender, EventArgs e)
        {
            //charger les donnees de la base dans la datagridview
            dgAnneeAcademique.DataSource = db.AnneeAcademiques.ToList();
        }

        private void btnModifier_Click(object sender, EventArgs e)
        {
            //Récupérer l'id de la ligne selectionnée
            int? id = int.Parse(dgAnneeAcademique.CurrentRow.Cells[0].Value.ToString());
            AnneeAcademique anneeAcademique = db.AnneeAcademiques.Find(id);
            anneeAcademique.LibelleAnneeAcademique = txtLibelleAnneeAcademique.Text;
            anneeAcademique.AnneeAcademiqueVal = int.Parse(txtAnneeAcademiqueVal.Text);
            db.SaveChanges();
            EffacerChamps();


        }

        private void btnSupprimer_Click(object sender, EventArgs e)
        {
            int? id = int.Parse(dgAnneeAcademique.CurrentRow.Cells[0].Value.ToString());
            AnneeAcademique anneeAcademique = db.AnneeAcademiques.Find(id);
            db.AnneeAcademiques.Remove(anneeAcademique);
            db.SaveChanges();
            EffacerChamps();
        }

        private void btnSelectionner_Click(object sender, EventArgs e)
        {
            //Remplir les champs avec les valeurs de la ligne selectionnée
            txtLibelleAnneeAcademique.Text= dgAnneeAcademique.CurrentRow.Cells[1].Value.ToString();
              txtAnneeAcademiqueVal.Text= dgAnneeAcademique.CurrentRow.Cells[2].Value.ToString();
        }
    }
}
