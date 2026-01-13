using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;

namespace SenSoutenance.Models
{
    public class Professeur:Utilisateur
    {
        [Required,MaxLength(80)]
        public string SpecialiteProfesseur { get; set; }
    }
}
