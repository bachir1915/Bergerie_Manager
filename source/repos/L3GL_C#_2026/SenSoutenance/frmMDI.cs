using Microsoft.VisualBasic.Devices;
using SenSoutenance.Views;
using SenSoutenance.Views.Parametre;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace SenSoutenance
{
    public partial class frmMDI : Form
    {
        public frmMDI()
        {
            InitializeComponent();
        }

        /// <summary>
        /// methode permettant de fermet tout les from dans le prentMDI
        /// </summary>

        private void fermer()
        {
            Form[] charr = this.MdiChildren;

            //For each child form set the window state to Maximized 
            foreach (Form chform in charr)
            {
                //chform.WindowState = FormWindowState.Maximized;
                chform.Close();
            }
        }

        private void seDeconecterToolStripMenuItem_Click(object sender, EventArgs e)
        {
            frmConnexion f = new frmConnexion();
            f.Show();
            this.Close(); //c'est pour fermer la page actuelle
        }

        private void qToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Application.Exit(); //c'est pour quitter
        }

        private void anneeAcademiqueToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //on appel cette fonction pour fermet le from si on en ouvre un autre
            fermer();

            //ca permet que ca s'affiche a l'interieur 
            frmAnneeAcademique f = new frmAnneeAcademique();
            f.MdiParent = this;
            f.Show(); //pour afficher le from

            //permet d'utiliser toute l'espace disponible au niveau du container
            f.WindowState = FormWindowState.Maximized;
        }

        private void sessionToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //on appel cette fonction pour fermet le from si on en ouvre un autre
            fermer();

            //ca permet que ca s'affiche a l'interieur 
            frmAnneeAcademique f = new frmAnneeAcademique();
            f.MdiParent = this;
            f.Show(); //pour afficher le from

            //permet d'utiliser toute l'espace disponible au niveau du container
            f.WindowState = FormWindowState.Maximized;
        }

        private void professeurToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //on appel cette fonction pour fermet le from si on en ouvre un autre
            fermer();

            //ca permet que ca s'affiche a l'interieur 
            frmAnneeAcademique f = new frmAnneeAcademique();
            f.MdiParent = this;
            f.Show(); //pour afficher le from

            //permet d'utiliser toute l'espace disponible au niveau du container
            f.WindowState = FormWindowState.Maximized;
        }

        //lorsque le formulaire souvre il prend tout l'ecran
        private void frmMDI_Load(object sender, EventArgs e)
        {
            Computer myComputer = new Computer();
            this.Width = myComputer.Screen.Bounds.Width; //prend la taille de mon ordi
            this.Height = myComputer.Screen.Bounds.Height;// prend la hauteur de mon ordi
            this.Location = new Point(0, 0);
        }
    }
}
