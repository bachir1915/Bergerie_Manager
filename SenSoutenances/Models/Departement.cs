using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SenSoutenance.Models
{
    public class Departement
    {
        [Key]
        public int IdDepartement { get; set; }

        [Required,MaxLength(80)]
        public string LibelleDepartement { get; set; }
    }
}
