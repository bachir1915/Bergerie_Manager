using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SenSoutenance.Models
{
    public class Candidat:Utilisateur
    {
        [Required,MaxLength(30)]
        public string MatriculeCandidat { get; set; }
    }
}
