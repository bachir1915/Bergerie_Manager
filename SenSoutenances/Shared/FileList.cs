using SenSoutenance.Models;
using System.Collections.Generic;
using System.Linq;

public class FillerList
{
    BdSenSoutenanceContext db = new BdSenSoutenanceContext();

    // Remplir la liste des années académiques
    public List<ListItem> FillAnneAcademique()
    {
        List<ListItem> laListe = new List<ListItem>();
        var liste = db.anneeAcademiques.ToList();

        // Ajouter l'option par défaut
        laListe.Add(new ListItem
        {
            Value = string.Empty,
            Text = "Sélectionner"
        });

        // Ajouter les éléments de la base de données
        foreach (var t in liste)
        {
            var item = new ListItem
            {
                Value = t.IdAnneeAcademique.ToString(),
                Text = t.LibelleAnneeAcademique
            };
            laListe.Add(item);
        }

        return laListe;
    }
}