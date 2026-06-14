using System.Text.Json;
using ViesLab2.Models;

namespace ViesLab2.Services;

public class ViesService
{
    private readonly HttpClient _client = new();

    public async Task<Taxpayer> GetTaxpayerAsync(string countryCode, string vatNumber)
    {
        countryCode = countryCode.Trim().ToUpperInvariant();
        vatNumber = vatNumber.Trim().Replace(" ", "");

        string url = $"https://ec.europa.eu/taxation_customs/vies/rest-api/ms/{countryCode}/vat/{vatNumber}";
        string json = await _client.GetStringAsync(url);

        using JsonDocument document = JsonDocument.Parse(json);
        JsonElement root = document.RootElement;

        bool valid = ReadBool(root, "valid") ?? ReadBool(root, "isValid") ?? false;
        string? name = ReadString(root, "name");
        string? address = ReadString(root, "address");
        string? responseCountry = ReadString(root, "countryCode") ?? countryCode;
        string? responseVat = ReadString(root, "vatNumber") ?? vatNumber;

        return new Taxpayer
        {
            CountryCode = responseCountry,
            VatNumber = responseVat,
            Name = string.IsNullOrWhiteSpace(name) ? "Brak nazwy w odpowiedzi API" : name,
            Address = string.IsNullOrWhiteSpace(address) ? "Brak adresu w odpowiedzi API" : address,
            Valid = valid
        };
    }

    private static string? ReadString(JsonElement root, string propertyName)
    {
        if (!root.TryGetProperty(propertyName, out JsonElement value))
            return null;

        return value.ValueKind == JsonValueKind.String ? value.GetString() : value.ToString();
    }

    private static bool? ReadBool(JsonElement root, string propertyName)
    {
        if (!root.TryGetProperty(propertyName, out JsonElement value))
            return null;

        if (value.ValueKind == JsonValueKind.True) return true;
        if (value.ValueKind == JsonValueKind.False) return false;

        if (value.ValueKind == JsonValueKind.String && bool.TryParse(value.GetString(), out bool result))
            return result;

        return null;
    }
}
