namespace ViesLab2.Models;

public class Taxpayer
{
    public int Id { get; set; }
    public required string CountryCode { get; set; }
    public required string VatNumber { get; set; }
    public string? Name { get; set; }
    public string? Address { get; set; }
    public bool Valid { get; set; }

    public List<VatCheck> VatChecks { get; set; } = new();

    public override string ToString()
    {
        return $"{Id}. {CountryCode}{VatNumber} | {Name} | Valid: {Valid}";
    }
}
