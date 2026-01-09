using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SenSoutenance.Models
{
    public class Memoire
    {
        [Key]
        public int IdMemoire { get; set; }

        [MaxLength(500), Required]
        public string SujetMemoire { get; set; }

        public int? IdAnneeAcademique { get; set; }

        [ForeignKey("IdAnneeAcademique")]
        public virtual AnneeAcademique AnneeAcademique { get; set; }

        //public byte[] DocumentMemoire { get; set; }

        public int? IdSession { get; set; }

        [ForeignKey("IdSession")]
        public virtual Session Session { get; set; }
    }
}
