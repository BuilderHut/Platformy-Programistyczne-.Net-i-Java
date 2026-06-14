namespace ViesLab2.Models;

public class VatCheck
{
    public int Id { get; set; }
    public DateTime CheckedAt { get; set; }

    public int TaxpayerId { get; set; }
    public Taxpayer? Taxpayer { get; set; }
}
