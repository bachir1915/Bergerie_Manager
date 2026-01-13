using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SenSoutenance.Models
{
    public class AnneeAcademique
    {
        [Key] //cle primaire
        public int IdAnneeAcademique {  get; set; }

        [Required, MaxLength(10)] //obligatoire
        public String LibelleAnneeAcademique { get; set; }

        [Required] //obligatoire
        public int AnneeAcademiqueVal {  get; set; }
    }
}
