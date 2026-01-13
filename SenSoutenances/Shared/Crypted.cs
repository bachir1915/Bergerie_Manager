using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Threading.Tasks;

namespace SenSoutenance.Shared
{
    public class Md5Helper
    {
        // Fonction pour calculer le hash MD5 d'une chaîne et retourner le résultat en hexadécimal
        public static string GetMd5Hash(string input)
        {
            using (MD5 md5Hash = MD5.Create())
            {
                // Convertir la chaîne d'entrée en tableau de bytes et calculer le hash
                byte[] data = md5Hash.ComputeHash(Encoding.UTF8.GetBytes(input));

                // Créer un StringBuilder pour collecter les bytes et créer une chaîne
                StringBuilder sBuilder = new StringBuilder();

                // Parcourir chaque byte des données hachées et les formater en chaîne hexadécimale
                for (int i = 0; i < data.Length; i++)
                {
                    sBuilder.Append(data[i].ToString("x2"));
                }

                // Retourner la chaîne hexadécimale
                return sBuilder.ToString();
            }
        }

        // Fonction pour vérifier si un hash correspond à une chaîne d'entrée
        public static bool VerifyMd5Hash(string input, string hash)
        {
            // Hacher la chaîne d'entrée
            string hashOfInput = GetMd5Hash(input);

            // Comparer les hashs (insensible à la casse)
            StringComparer comparer = StringComparer.OrdinalIgnoreCase;

            return comparer.Compare(hashOfInput, hash) == 0;
        }
    }
}