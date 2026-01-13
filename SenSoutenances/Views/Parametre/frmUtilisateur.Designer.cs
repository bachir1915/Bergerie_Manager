namespace SenSoutenance.Views.Parametre
{
    partial class frmUtilisateur
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.tabUtilisateur = new System.Windows.Forms.TabControl();
            this.tabChefDepartement = new System.Windows.Forms.TabPage();
            this.btnSelectionnerChef = new System.Windows.Forms.Button();
            this.btnModifierChef = new System.Windows.Forms.Button();
            this.btnSupprimerChef = new System.Windows.Forms.Button();
            this.btnAjouterChef = new System.Windows.Forms.Button();
            this.dgChefDepartement = new System.Windows.Forms.DataGridView();
            this.txtChefEmail = new System.Windows.Forms.TextBox();
            this.label11 = new System.Windows.Forms.Label();
            this.txtChefTelephone = new System.Windows.Forms.TextBox();
            this.label12 = new System.Windows.Forms.Label();
            this.txtChefPrenom = new System.Windows.Forms.TextBox();
            this.txtChefNom = new System.Windows.Forms.TextBox();
            this.label13 = new System.Windows.Forms.Label();
            this.label15 = new System.Windows.Forms.Label();
            this.tabProfesseur = new System.Windows.Forms.TabPage();
            this.btnProfSelectionner = new System.Windows.Forms.Button();
            this.btnProfModifier = new System.Windows.Forms.Button();
            this.btnProfSupprimer = new System.Windows.Forms.Button();
            this.btnProfAjouter = new System.Windows.Forms.Button();
            this.dgProfesseur = new System.Windows.Forms.DataGridView();
            this.txtProfEmail = new System.Windows.Forms.TextBox();
            this.label6 = new System.Windows.Forms.Label();
            this.txtProfTelephone = new System.Windows.Forms.TextBox();
            this.label7 = new System.Windows.Forms.Label();
            this.txtProfSpecialite = new System.Windows.Forms.TextBox();
            this.txtProfPrenom = new System.Windows.Forms.TextBox();
            this.txtProfNom = new System.Windows.Forms.TextBox();
            this.label8 = new System.Windows.Forms.Label();
            this.label9 = new System.Windows.Forms.Label();
            this.label10 = new System.Windows.Forms.Label();
            this.tabCandidat = new System.Windows.Forms.TabPage();
            this.panelCandidatGrid = new System.Windows.Forms.Panel();
            this.dgCandidat = new System.Windows.Forms.DataGridView();
            this.panelCandidatForm = new System.Windows.Forms.Panel();
            this.txtMatriculeCandidat = new System.Windows.Forms.TextBox();
            this.label5 = new System.Windows.Forms.Label();
            this.btnSelectionnerCandidat = new System.Windows.Forms.Button();
            this.btnModifierCandidat = new System.Windows.Forms.Button();
            this.btnSupprimerCandidat = new System.Windows.Forms.Button();
            this.btnAjouterCandidat = new System.Windows.Forms.Button();
            this.txtEmailCandidat = new System.Windows.Forms.TextBox();
            this.label4 = new System.Windows.Forms.Label();
            this.txtTelephoneCandidat = new System.Windows.Forms.TextBox();
            this.txtPrenomCandidat = new System.Windows.Forms.TextBox();
            this.txtNomCandidat = new System.Windows.Forms.TextBox();
            this.label3 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.label1 = new System.Windows.Forms.Label();
            this.tabUtilisateur.SuspendLayout();
            this.tabChefDepartement.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dgChefDepartement)).BeginInit();
            this.tabProfesseur.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dgProfesseur)).BeginInit();
            this.tabCandidat.SuspendLayout();
            this.panelCandidatGrid.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dgCandidat)).BeginInit();
            this.panelCandidatForm.SuspendLayout();
            this.SuspendLayout();
            // 
            // tabUtilisateur
            // 
            this.tabUtilisateur.Controls.Add(this.tabChefDepartement);
            this.tabUtilisateur.Controls.Add(this.tabProfesseur);
            this.tabUtilisateur.Controls.Add(this.tabCandidat);
            this.tabUtilisateur.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tabUtilisateur.Location = new System.Drawing.Point(0, 0);
            this.tabUtilisateur.Multiline = true;
            this.tabUtilisateur.Name = "tabUtilisateur";
            this.tabUtilisateur.SelectedIndex = 0;
            this.tabUtilisateur.Size = new System.Drawing.Size(776, 450);
            this.tabUtilisateur.SizeMode = System.Windows.Forms.TabSizeMode.Fixed;
            this.tabUtilisateur.TabIndex = 0;
            // 
            // tabChefDepartement
            // 
            this.tabChefDepartement.BackColor = System.Drawing.SystemColors.ActiveCaption;
            this.tabChefDepartement.Controls.Add(this.btnSelectionnerChef);
            this.tabChefDepartement.Controls.Add(this.btnModifierChef);
            this.tabChefDepartement.Controls.Add(this.btnSupprimerChef);
            this.tabChefDepartement.Controls.Add(this.btnAjouterChef);
            this.tabChefDepartement.Controls.Add(this.dgChefDepartement);
            this.tabChefDepartement.Controls.Add(this.txtChefEmail);
            this.tabChefDepartement.Controls.Add(this.label11);
            this.tabChefDepartement.Controls.Add(this.txtChefTelephone);
            this.tabChefDepartement.Controls.Add(this.label12);
            this.tabChefDepartement.Controls.Add(this.txtChefPrenom);
            this.tabChefDepartement.Controls.Add(this.txtChefNom);
            this.tabChefDepartement.Controls.Add(this.label13);
            this.tabChefDepartement.Controls.Add(this.label15);
            this.tabChefDepartement.Location = new System.Drawing.Point(4, 22);
            this.tabChefDepartement.Name = "tabChefDepartement";
            this.tabChefDepartement.Padding = new System.Windows.Forms.Padding(3);
            this.tabChefDepartement.Size = new System.Drawing.Size(768, 424);
            this.tabChefDepartement.TabIndex = 0;
            this.tabChefDepartement.Text = "Chef Département";
            this.tabChefDepartement.Click += new System.EventHandler(this.tabChefDepartement_Click);
            // 
            // btnSelectionnerChef
            // 
            this.btnSelectionnerChef.BackColor = System.Drawing.Color.DarkSeaGreen;
            this.btnSelectionnerChef.FlatAppearance.BorderSize = 0;
            this.btnSelectionnerChef.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btnSelectionnerChef.Location = new System.Drawing.Point(91, 255);
            this.btnSelectionnerChef.Name = "btnSelectionnerChef";
            this.btnSelectionnerChef.Size = new System.Drawing.Size(74, 34);
            this.btnSelectionnerChef.TabIndex = 52;
            this.btnSelectionnerChef.Text = "Selectionner";
            this.btnSelectionnerChef.UseVisualStyleBackColor = false;
            this.btnSelectionnerChef.Click += new System.EventHandler(this.btnSelectionnerChef_Click);
            // 
            // btnModifierChef
            // 
            this.btnModifierChef.BackColor = System.Drawing.Color.DarkOrange;
            this.btnModifierChef.FlatAppearance.BorderSize = 0;
            this.btnModifierChef.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btnModifierChef.Location = new System.Drawing.Point(171, 255);
            this.btnModifierChef.Name = "btnModifierChef";
            this.btnModifierChef.Size = new System.Drawing.Size(74, 34);
            this.btnModifierChef.TabIndex = 51;
            this.btnModifierChef.Text = "Modifier";
            this.btnModifierChef.UseVisualStyleBackColor = false;
            this.btnModifierChef.Click += new System.EventHandler(this.btnModifierChef_Click);
            // 
            // btnSupprimerChef
            // 
            this.btnSupprimerChef.BackColor = System.Drawing.Color.Red;
            this.btnSupprimerChef.FlatAppearance.BorderSize = 0;
            this.btnSupprimerChef.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btnSupprimerChef.Location = new System.Drawing.Point(251, 255);
            this.btnSupprimerChef.Name = "btnSupprimerChef";
            this.btnSupprimerChef.Size = new System.Drawing.Size(82, 34);
            this.btnSupprimerChef.TabIndex = 50;
            this.btnSupprimerChef.Text = "Supprimer";
            this.btnSupprimerChef.UseVisualStyleBackColor = false;
            this.btnSupprimerChef.Click += new System.EventHandler(this.btnSupprimerChef_Click);
            // 
            // btnAjouterChef
            // 
            this.btnAjouterChef.BackColor = System.Drawing.Color.GreenYellow;
            this.btnAjouterChef.FlatAppearance.BorderSize = 0;
            this.btnAjouterChef.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btnAjouterChef.Location = new System.Drawing.Point(8, 255);
            this.btnAjouterChef.Name = "btnAjouterChef";
            this.btnAjouterChef.Size = new System.Drawing.Size(77, 34);
            this.btnAjouterChef.TabIndex = 49;
            this.btnAjouterChef.Text = "Ajouter";
            this.btnAjouterChef.UseVisualStyleBackColor = false;
            this.btnAjouterChef.Click += new System.EventHandler(this.btnAjouterChef_Click);
            // 
            // dgChefDepartement
            // 
            this.dgChefDepartement.BackgroundColor = System.Drawing.SystemColors.ButtonFace;
            this.dgChefDepartement.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dgChefDepartement.Location = new System.Drawing.Point(361, 0);
            this.dgChefDepartement.Name = "dgChefDepartement";
            this.dgChefDepartement.ReadOnly = true;
            this.dgChefDepartement.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
            this.dgChefDepartement.Size = new System.Drawing.Size(407, 289);
            this.dgChefDepartement.TabIndex = 48;
            // 
            // txtChefEmail
            // 
            this.txtChefEmail.Location = new System.Drawing.Point(81, 197);
            this.txtChefEmail.Name = "txtChefEmail";
            this.txtChefEmail.Size = new System.Drawing.Size(252, 20);
            this.txtChefEmail.TabIndex = 47;
            // 
            // label11
            // 
            this.label11.AutoSize = true;
            this.label11.Location = new System.Drawing.Point(8, 204);
            this.label11.Name = "label11";
            this.label11.Size = new System.Drawing.Size(32, 13);
            this.label11.TabIndex = 46;
            this.label11.Text = "Email";
            // 
            // txtChefTelephone
            // 
            this.txtChefTelephone.Location = new System.Drawing.Point(81, 132);
            this.txtChefTelephone.Name = "txtChefTelephone";
            this.txtChefTelephone.Size = new System.Drawing.Size(252, 20);
            this.txtChefTelephone.TabIndex = 45;
            // 
            // label12
            // 
            this.label12.AutoSize = true;
            this.label12.Location = new System.Drawing.Point(8, 139);
            this.label12.Name = "label12";
            this.label12.Size = new System.Drawing.Size(58, 13);
            this.label12.TabIndex = 44;
            this.label12.Text = "Téléphone";
            // 
            // txtChefPrenom
            // 
            this.txtChefPrenom.Location = new System.Drawing.Point(81, 78);
            this.txtChefPrenom.Name = "txtChefPrenom";
            this.txtChefPrenom.Size = new System.Drawing.Size(252, 20);
            this.txtChefPrenom.TabIndex = 42;
            this.txtChefPrenom.TextChanged += new System.EventHandler(this.txtChefPrenom_TextChanged);
            // 
            // txtChefNom
            // 
            this.txtChefNom.Location = new System.Drawing.Point(81, 28);
            this.txtChefNom.Name = "txtChefNom";
            this.txtChefNom.Size = new System.Drawing.Size(252, 20);
            this.txtChefNom.TabIndex = 41;
            // 
            // label13
            // 
            this.label13.AutoSize = true;
            this.label13.Location = new System.Drawing.Point(8, 85);
            this.label13.Name = "label13";
            this.label13.Size = new System.Drawing.Size(46, 13);
            this.label13.TabIndex = 40;
            this.label13.Text = "Prénom ";
            // 
            // label15
            // 
            this.label15.AutoSize = true;
            this.label15.Location = new System.Drawing.Point(8, 35);
            this.label15.Name = "label15";
            this.label15.Size = new System.Drawing.Size(32, 13);
            this.label15.TabIndex = 38;
            this.label15.Text = "Nom ";
            // 
            // tabProfesseur
            // 
            this.tabProfesseur.BackColor = System.Drawing.SystemColors.ActiveCaption;
            this.tabProfesseur.Controls.Add(this.btnProfSelectionner);
            this.tabProfesseur.Controls.Add(this.btnProfModifier);
            this.tabProfesseur.Controls.Add(this.btnProfSupprimer);
            this.tabProfesseur.Controls.Add(this.btnProfAjouter);
            this.tabProfesseur.Controls.Add(this.dgProfesseur);
            this.tabProfesseur.Controls.Add(this.txtProfEmail);
            this.tabProfesseur.Controls.Add(this.label6);
            this.tabProfesseur.Controls.Add(this.txtProfTelephone);
            this.tabProfesseur.Controls.Add(this.label7);
            this.tabProfesseur.Controls.Add(this.txtProfSpecialite);
            this.tabProfesseur.Controls.Add(this.txtProfPrenom);
            this.tabProfesseur.Controls.Add(this.txtProfNom);
            this.tabProfesseur.Controls.Add(this.label8);
            this.tabProfesseur.Controls.Add(this.label9);
            this.tabProfesseur.Controls.Add(this.label10);
            this.tabProfesseur.Location = new System.Drawing.Point(4, 22);
            this.tabProfesseur.Name = "tabProfesseur";
            this.tabProfesseur.Padding = new System.Windows.Forms.Padding(3);
            this.tabProfesseur.Size = new System.Drawing.Size(768, 424);
            this.tabProfesseur.TabIndex = 1;
            this.tabProfesseur.Text = "Professeur";
            // 
            // btnProfSelectionner
            // 
            this.btnProfSelectionner.BackColor = System.Drawing.Color.DarkSeaGreen;
            this.btnProfSelectionner.FlatAppearance.BorderSize = 0;
            this.btnProfSelectionner.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btnProfSelectionner.Location = new System.Drawing.Point(95, 291);
            this.btnProfSelectionner.Name = "btnProfSelectionner";
            this.btnProfSelectionner.Size = new System.Drawing.Size(77, 34);
            this.btnProfSelectionner.TabIndex = 37;
            this.btnProfSelectionner.Text = "Selectionner";
            this.btnProfSelectionner.UseVisualStyleBackColor = false;
            this.btnProfSelectionner.Click += new System.EventHandler(this.btnSelectionnerProfesseur_Click);
            // 
            // btnProfModifier
            // 
            this.btnProfModifier.BackColor = System.Drawing.Color.DarkOrange;
            this.btnProfModifier.FlatAppearance.BorderSize = 0;
            this.btnProfModifier.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btnProfModifier.Location = new System.Drawing.Point(187, 291);
            this.btnProfModifier.Name = "btnProfModifier";
            this.btnProfModifier.Size = new System.Drawing.Size(72, 34);
            this.btnProfModifier.TabIndex = 36;
            this.btnProfModifier.Text = "Modifier";
            this.btnProfModifier.UseVisualStyleBackColor = false;
            this.btnProfModifier.Click += new System.EventHandler(this.btnModifierProfesseur_Click);
            // 
            // btnProfSupprimer
            // 
            this.btnProfSupprimer.BackColor = System.Drawing.Color.Red;
            this.btnProfSupprimer.FlatAppearance.BorderSize = 0;
            this.btnProfSupprimer.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btnProfSupprimer.Location = new System.Drawing.Point(281, 291);
            this.btnProfSupprimer.Name = "btnProfSupprimer";
            this.btnProfSupprimer.Size = new System.Drawing.Size(72, 34);
            this.btnProfSupprimer.TabIndex = 35;
            this.btnProfSupprimer.Text = "Supprimer";
            this.btnProfSupprimer.UseVisualStyleBackColor = false;
            this.btnProfSupprimer.Click += new System.EventHandler(this.btnSupprimerProfesseur_Click);
            // 
            // btnProfAjouter
            // 
            this.btnProfAjouter.BackColor = System.Drawing.Color.GreenYellow;
            this.btnProfAjouter.FlatAppearance.BorderSize = 0;
            this.btnProfAjouter.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btnProfAjouter.Location = new System.Drawing.Point(8, 291);
            this.btnProfAjouter.Name = "btnProfAjouter";
            this.btnProfAjouter.Size = new System.Drawing.Size(72, 34);
            this.btnProfAjouter.TabIndex = 34;
            this.btnProfAjouter.Text = "Ajouter";
            this.btnProfAjouter.UseVisualStyleBackColor = false;
            this.btnProfAjouter.Click += new System.EventHandler(this.btnAjouterProfesseur_Click);
            // 
            // dgProfesseur
            // 
            this.dgProfesseur.BackgroundColor = System.Drawing.Color.White;
            this.dgProfesseur.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dgProfesseur.Location = new System.Drawing.Point(399, 3);
            this.dgProfesseur.Name = "dgProfesseur";
            this.dgProfesseur.ReadOnly = true;
            this.dgProfesseur.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
            this.dgProfesseur.Size = new System.Drawing.Size(369, 322);
            this.dgProfesseur.TabIndex = 24;
            // 
            // txtProfEmail
            // 
            this.txtProfEmail.Location = new System.Drawing.Point(150, 235);
            this.txtProfEmail.Name = "txtProfEmail";
            this.txtProfEmail.Size = new System.Drawing.Size(203, 20);
            this.txtProfEmail.TabIndex = 23;
            // 
            // label6
            // 
            this.label6.AutoSize = true;
            this.label6.Location = new System.Drawing.Point(8, 242);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(32, 13);
            this.label6.TabIndex = 22;
            this.label6.Text = "Email";
            // 
            // txtProfTelephone
            // 
            this.txtProfTelephone.Location = new System.Drawing.Point(150, 178);
            this.txtProfTelephone.Name = "txtProfTelephone";
            this.txtProfTelephone.Size = new System.Drawing.Size(203, 20);
            this.txtProfTelephone.TabIndex = 21;
            // 
            // label7
            // 
            this.label7.AutoSize = true;
            this.label7.Location = new System.Drawing.Point(8, 185);
            this.label7.Name = "label7";
            this.label7.Size = new System.Drawing.Size(58, 13);
            this.label7.TabIndex = 20;
            this.label7.Text = "Téléphone";
            // 
            // txtProfSpecialite
            // 
            this.txtProfSpecialite.Location = new System.Drawing.Point(150, 129);
            this.txtProfSpecialite.Name = "txtProfSpecialite";
            this.txtProfSpecialite.Size = new System.Drawing.Size(203, 20);
            this.txtProfSpecialite.TabIndex = 19;
            // 
            // txtProfPrenom
            // 
            this.txtProfPrenom.Location = new System.Drawing.Point(150, 81);
            this.txtProfPrenom.Name = "txtProfPrenom";
            this.txtProfPrenom.Size = new System.Drawing.Size(203, 20);
            this.txtProfPrenom.TabIndex = 18;
            // 
            // txtProfNom
            // 
            this.txtProfNom.Location = new System.Drawing.Point(150, 33);
            this.txtProfNom.Name = "txtProfNom";
            this.txtProfNom.Size = new System.Drawing.Size(203, 20);
            this.txtProfNom.TabIndex = 17;
            // 
            // label8
            // 
            this.label8.AutoSize = true;
            this.label8.Location = new System.Drawing.Point(8, 88);
            this.label8.Name = "label8";
            this.label8.Size = new System.Drawing.Size(113, 13);
            this.label8.TabIndex = 16;
            this.label8.Text = "Prénom du professeur ";
            // 
            // label9
            // 
            this.label9.AutoSize = true;
            this.label9.Location = new System.Drawing.Point(8, 136);
            this.label9.Name = "label9";
            this.label9.Size = new System.Drawing.Size(59, 13);
            this.label9.TabIndex = 15;
            this.label9.Text = " Spécialité ";
            // 
            // label10
            // 
            this.label10.AutoSize = true;
            this.label10.Location = new System.Drawing.Point(8, 40);
            this.label10.Name = "label10";
            this.label10.Size = new System.Drawing.Size(96, 13);
            this.label10.TabIndex = 14;
            this.label10.Text = "Nom du professeur";
            // 
            // tabCandidat
            // 
            this.tabCandidat.Controls.Add(this.panelCandidatGrid);
            this.tabCandidat.Controls.Add(this.panelCandidatForm);
            this.tabCandidat.Location = new System.Drawing.Point(4, 22);
            this.tabCandidat.Name = "tabCandidat";
            this.tabCandidat.Padding = new System.Windows.Forms.Padding(3);
            this.tabCandidat.Size = new System.Drawing.Size(768, 424);
            this.tabCandidat.TabIndex = 2;
            this.tabCandidat.Text = "Candidat";
            this.tabCandidat.UseVisualStyleBackColor = true;
            // 
            // panelCandidatGrid
            // 
            this.panelCandidatGrid.Controls.Add(this.dgCandidat);
            this.panelCandidatGrid.Dock = System.Windows.Forms.DockStyle.Fill;
            this.panelCandidatGrid.Location = new System.Drawing.Point(388, 3);
            this.panelCandidatGrid.Name = "panelCandidatGrid";
            this.panelCandidatGrid.Size = new System.Drawing.Size(377, 418);
            this.panelCandidatGrid.TabIndex = 1;
            // 
            // dgCandidat
            // 
            this.dgCandidat.BackgroundColor = System.Drawing.Color.White;
            this.dgCandidat.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dgCandidat.Dock = System.Windows.Forms.DockStyle.Right;
            this.dgCandidat.Location = new System.Drawing.Point(0, 0);
            this.dgCandidat.Name = "dgCandidat";
            this.dgCandidat.ReadOnly = true;
            this.dgCandidat.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
            this.dgCandidat.Size = new System.Drawing.Size(377, 418);
            this.dgCandidat.TabIndex = 0;
            this.dgCandidat.CellContentClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgCandidat_CellContentClick);
            // 
            // panelCandidatForm
            // 
            this.panelCandidatForm.BackColor = System.Drawing.SystemColors.ActiveCaption;
            this.panelCandidatForm.Controls.Add(this.txtMatriculeCandidat);
            this.panelCandidatForm.Controls.Add(this.label5);
            this.panelCandidatForm.Controls.Add(this.btnSelectionnerCandidat);
            this.panelCandidatForm.Controls.Add(this.btnModifierCandidat);
            this.panelCandidatForm.Controls.Add(this.btnSupprimerCandidat);
            this.panelCandidatForm.Controls.Add(this.btnAjouterCandidat);
            this.panelCandidatForm.Controls.Add(this.txtEmailCandidat);
            this.panelCandidatForm.Controls.Add(this.label4);
            this.panelCandidatForm.Controls.Add(this.txtTelephoneCandidat);
            this.panelCandidatForm.Controls.Add(this.txtPrenomCandidat);
            this.panelCandidatForm.Controls.Add(this.txtNomCandidat);
            this.panelCandidatForm.Controls.Add(this.label3);
            this.panelCandidatForm.Controls.Add(this.label2);
            this.panelCandidatForm.Controls.Add(this.label1);
            this.panelCandidatForm.Dock = System.Windows.Forms.DockStyle.Left;
            this.panelCandidatForm.Location = new System.Drawing.Point(3, 3);
            this.panelCandidatForm.Name = "panelCandidatForm";
            this.panelCandidatForm.Size = new System.Drawing.Size(385, 418);
            this.panelCandidatForm.TabIndex = 0;
            // 
            // txtMatriculeCandidat
            // 
            this.txtMatriculeCandidat.Location = new System.Drawing.Point(88, 232);
            this.txtMatriculeCandidat.Name = "txtMatriculeCandidat";
            this.txtMatriculeCandidat.Size = new System.Drawing.Size(233, 20);
            this.txtMatriculeCandidat.TabIndex = 35;
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(12, 239);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(50, 13);
            this.label5.TabIndex = 34;
            this.label5.Text = "Matricule";
            // 
            // btnSelectionnerCandidat
            // 
            this.btnSelectionnerCandidat.BackColor = System.Drawing.Color.DarkSeaGreen;
            this.btnSelectionnerCandidat.FlatAppearance.BorderSize = 0;
            this.btnSelectionnerCandidat.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btnSelectionnerCandidat.Location = new System.Drawing.Point(88, 268);
            this.btnSelectionnerCandidat.Name = "btnSelectionnerCandidat";
            this.btnSelectionnerCandidat.Size = new System.Drawing.Size(77, 34);
            this.btnSelectionnerCandidat.TabIndex = 33;
            this.btnSelectionnerCandidat.Text = "Selectionner";
            this.btnSelectionnerCandidat.UseVisualStyleBackColor = false;
            this.btnSelectionnerCandidat.Click += new System.EventHandler(this.btnSelectionnerCandidat_Click);
            // 
            // btnModifierCandidat
            // 
            this.btnModifierCandidat.BackColor = System.Drawing.Color.DarkOrange;
            this.btnModifierCandidat.FlatAppearance.BorderSize = 0;
            this.btnModifierCandidat.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btnModifierCandidat.Location = new System.Drawing.Point(171, 268);
            this.btnModifierCandidat.Name = "btnModifierCandidat";
            this.btnModifierCandidat.Size = new System.Drawing.Size(72, 34);
            this.btnModifierCandidat.TabIndex = 32;
            this.btnModifierCandidat.Text = "Modifier";
            this.btnModifierCandidat.UseVisualStyleBackColor = false;
            this.btnModifierCandidat.Click += new System.EventHandler(this.btnModifierCandidat_Click);
            // 
            // btnSupprimerCandidat
            // 
            this.btnSupprimerCandidat.BackColor = System.Drawing.Color.Red;
            this.btnSupprimerCandidat.FlatAppearance.BorderSize = 0;
            this.btnSupprimerCandidat.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btnSupprimerCandidat.Location = new System.Drawing.Point(249, 268);
            this.btnSupprimerCandidat.Name = "btnSupprimerCandidat";
            this.btnSupprimerCandidat.Size = new System.Drawing.Size(72, 34);
            this.btnSupprimerCandidat.TabIndex = 31;
            this.btnSupprimerCandidat.Text = "Supprimer";
            this.btnSupprimerCandidat.UseVisualStyleBackColor = false;
            this.btnSupprimerCandidat.Click += new System.EventHandler(this.btnSupprimerCandidat_Click);
            // 
            // btnAjouterCandidat
            // 
            this.btnAjouterCandidat.BackColor = System.Drawing.Color.GreenYellow;
            this.btnAjouterCandidat.FlatAppearance.BorderSize = 0;
            this.btnAjouterCandidat.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btnAjouterCandidat.Location = new System.Drawing.Point(15, 268);
            this.btnAjouterCandidat.Name = "btnAjouterCandidat";
            this.btnAjouterCandidat.Size = new System.Drawing.Size(67, 34);
            this.btnAjouterCandidat.TabIndex = 30;
            this.btnAjouterCandidat.Text = "Ajouter";
            this.btnAjouterCandidat.UseVisualStyleBackColor = false;
            this.btnAjouterCandidat.Click += new System.EventHandler(this.btnAjouterCandidat_Click);
            // 
            // txtEmailCandidat
            // 
            this.txtEmailCandidat.Location = new System.Drawing.Point(88, 185);
            this.txtEmailCandidat.Name = "txtEmailCandidat";
            this.txtEmailCandidat.Size = new System.Drawing.Size(233, 20);
            this.txtEmailCandidat.TabIndex = 29;
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(12, 192);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(32, 13);
            this.label4.TabIndex = 28;
            this.label4.Text = "Email";
            // 
            // txtTelephoneCandidat
            // 
            this.txtTelephoneCandidat.Location = new System.Drawing.Point(88, 137);
            this.txtTelephoneCandidat.Name = "txtTelephoneCandidat";
            this.txtTelephoneCandidat.Size = new System.Drawing.Size(233, 20);
            this.txtTelephoneCandidat.TabIndex = 27;
            // 
            // txtPrenomCandidat
            // 
            this.txtPrenomCandidat.Location = new System.Drawing.Point(88, 80);
            this.txtPrenomCandidat.Name = "txtPrenomCandidat";
            this.txtPrenomCandidat.Size = new System.Drawing.Size(233, 20);
            this.txtPrenomCandidat.TabIndex = 26;
            // 
            // txtNomCandidat
            // 
            this.txtNomCandidat.Location = new System.Drawing.Point(88, 25);
            this.txtNomCandidat.Name = "txtNomCandidat";
            this.txtNomCandidat.Size = new System.Drawing.Size(233, 20);
            this.txtNomCandidat.TabIndex = 25;
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(12, 87);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(43, 13);
            this.label3.TabIndex = 24;
            this.label3.Text = "Prénom";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(12, 144);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(58, 13);
            this.label2.TabIndex = 23;
            this.label2.Text = "Téléphone";
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(12, 32);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(29, 13);
            this.label1.TabIndex = 22;
            this.label1.Text = "Nom";
            // 
            // frmUtilisateur
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(776, 450);
            this.Controls.Add(this.tabUtilisateur);
            this.Name = "frmUtilisateur";
            this.Text = "frmUtilisateur";
            this.Load += new System.EventHandler(this.frmUtilisateur_Load);
            this.tabUtilisateur.ResumeLayout(false);
            this.tabChefDepartement.ResumeLayout(false);
            this.tabChefDepartement.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dgChefDepartement)).EndInit();
            this.tabProfesseur.ResumeLayout(false);
            this.tabProfesseur.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dgProfesseur)).EndInit();
            this.tabCandidat.ResumeLayout(false);
            this.panelCandidatGrid.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.dgCandidat)).EndInit();
            this.panelCandidatForm.ResumeLayout(false);
            this.panelCandidatForm.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.TabControl tabUtilisateur;
        private System.Windows.Forms.TabPage tabChefDepartement;
        private System.Windows.Forms.TabPage tabProfesseur;
        private System.Windows.Forms.TabPage tabCandidat;
        private System.Windows.Forms.Panel panelCandidatGrid;
        private System.Windows.Forms.Panel panelCandidatForm;
        private System.Windows.Forms.TextBox txtEmailCandidat;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.TextBox txtTelephoneCandidat;
        private System.Windows.Forms.TextBox txtPrenomCandidat;
        private System.Windows.Forms.TextBox txtNomCandidat;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.DataGridView dgCandidat;
        private System.Windows.Forms.Button btnSelectionnerCandidat;
        private System.Windows.Forms.Button btnModifierCandidat;
        private System.Windows.Forms.Button btnSupprimerCandidat;
        private System.Windows.Forms.Button btnAjouterCandidat;
        private System.Windows.Forms.TextBox txtMatriculeCandidat;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.TextBox txtProfEmail;
        private System.Windows.Forms.Label label6;
        private System.Windows.Forms.TextBox txtProfTelephone;
        private System.Windows.Forms.Label label7;
        private System.Windows.Forms.TextBox txtProfSpecialite;
        private System.Windows.Forms.TextBox txtProfPrenom;
        private System.Windows.Forms.TextBox txtProfNom;
        private System.Windows.Forms.Label label8;
        private System.Windows.Forms.Label label9;
        private System.Windows.Forms.Label label10;
        private System.Windows.Forms.DataGridView dgProfesseur;
        private System.Windows.Forms.Button btnProfSelectionner;
        private System.Windows.Forms.Button btnProfModifier;
        private System.Windows.Forms.Button btnProfSupprimer;
        private System.Windows.Forms.Button btnProfAjouter;
        private System.Windows.Forms.Button btnSelectionnerChef;
        private System.Windows.Forms.Button btnModifierChef;
        private System.Windows.Forms.Button btnSupprimerChef;
        private System.Windows.Forms.Button btnAjouterChef;
        private System.Windows.Forms.DataGridView dgChefDepartement;
        private System.Windows.Forms.TextBox txtChefEmail;
        private System.Windows.Forms.Label label11;
        private System.Windows.Forms.TextBox txtChefTelephone;
        private System.Windows.Forms.Label label12;
        private System.Windows.Forms.TextBox txtChefPrenom;
        private System.Windows.Forms.TextBox txtChefNom;
        private System.Windows.Forms.Label label13;
        private System.Windows.Forms.Label label15;
    }
}