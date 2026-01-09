using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace SenSoutenance.Models
{
    public class ChefDepartement : Utilisateur
    {
        public int IdDepartement { get; set; }
    }
}
