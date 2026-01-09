using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace SenSoutenance.Models
{
    public class Professeur : Utilisateur
    {
   

        [required, MaxLength(100)]
        public string SpecialiteProfesseur { get; set; }
    }
}
