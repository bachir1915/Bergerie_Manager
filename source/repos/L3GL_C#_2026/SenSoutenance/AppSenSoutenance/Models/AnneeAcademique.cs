using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace AppSenSoutenance.Models
{
    public class AnneeAcademique
    {
        [Key]
        public int IdAnneeAcademique { get; set; }

        [Required, MaxLength(9)]
        public string LibelleAnneeAcademique { get; set; }

        [Required]
        public int AnneeAcademiqueVal { get; set; }
    }
}
