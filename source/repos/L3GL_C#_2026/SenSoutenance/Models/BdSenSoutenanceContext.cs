using MySql.Data.EntityFramework;
using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SenSoutenance.Models
{
    [DbConfigurationType(typeof(MySqlEFConfiguration))]
    public class BdSenSoutenanceContext: DbContext
    {
        public BdSenSoutenanceContext() :base("connBdSenSoutenance") { }
        public DbSet<AnneeAcademique> anneeAcademiques { get; set; }

        public DbSet<Session> sessions { get; set; }

        public DbSet<Memoire> memoires { get; set; }

        public DbSet<Soutenance> soutenance { get; set; }

        public DbSet<Professeur> professeurs { get; set; }

        public DbSet<Utilisateur> utilisateurs { get; set; }

        public DbSet<Admin> admins { get; set; }

        public DbSet<ChefDepartement> chefsDepartement { get; set; }

        public DbSet<Candidat> candidats { get; set; }
    }
}
