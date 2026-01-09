using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SenSoutenance.Models
{
    public class Soutenance
    {
        [Key]
        public int IdSoutenance { get; set; }

        public DateTime DatwSoutenance { get; set; } = DateTime.Now;

        [MaxLength(2000)]
        public string LieuSoutenance { get; set; }

        [MaxLength(50)]
        public String ResultatSoutenance { get; set; }

        [MaxLength(100)]
        public String MentionSoutenance { get; set; }

        [MaxLength(5000)]
        public String ObservationSoutenance { get; set; }

        public int? IdMemoire { get; set; }

        [ForeignKey("IdMemoire")]
        public virtual Memoire Memoire { get; set; }

    }
}
