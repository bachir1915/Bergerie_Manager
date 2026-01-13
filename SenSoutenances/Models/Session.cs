using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SenSoutenance.Models
{
    public class Session
    {
        [Key]
        public int IdSession { get; set; }
        
        [Required, MaxLength(50)]
        public string LibelleSession { get; set; }

        public int? IdAnneeAcademique { get; set; }

        [ForeignKey("IdAnneeAcademique")]

        public virtual AnneeAcademique AnneeAcademique { get; set; }


    }
}
