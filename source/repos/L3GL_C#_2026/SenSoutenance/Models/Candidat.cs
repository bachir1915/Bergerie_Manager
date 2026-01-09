using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace SenSoutenance.Models
{
    public class Candidat : Utilisateur
    {
        [Required, MaxLength(20)]
        public string MatriculeCandidat { get; set; }
    }
}
