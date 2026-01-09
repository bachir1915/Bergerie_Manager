using MySql.Data.EntityFramework;
using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.Linq;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading.Tasks;

namespace AppSenSoutenance.Models
{
    [DbConfigurationType(typeof(MySqlEFConfiguration))]

    public class BdSensoutenanceContext : DbContext
    {
        public BdSensoutenanceContext() : base("ConnBDSebSoutenance")
        {
        }

        public DbSet<AnneeAcademique> AnneeAcademiques { get; set; }
    }
}