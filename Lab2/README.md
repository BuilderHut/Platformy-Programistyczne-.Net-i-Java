# Laboratorium 2 - VIES API, SQLite i Entity Framework Core

Projekt desktopowy w C# przygotowany w Windows Forms. Aplikacja pobiera dane podatnika z publicznego API VIES, zapisuje je w lokalnej bazie SQLite i umożliwia podstawową obsługę zapisanych rekordów.

## Co robi aplikacja

- pobiera dane podatnika VAT z API VIES Komisji Europejskiej,
- deserializuje odpowiedź JSON,
- zapisuje dane w lokalnej bazie SQLite przez Entity Framework Core,
- tworzy bazę automatycznie przy starcie aplikacji przez `Database.EnsureCreated()`,
- pozwala dodać podatnika ręcznie,
- pozwala usuwać rekordy,
- pozwala filtrować aktywnych podatników VAT,
- pozwala sortować podatników po nazwie,
- zapisuje historię sprawdzeń VAT dla danego podatnika,
- nie pobiera danych drugi raz z API, jeśli podatnik istnieje już w bazie.

## Model danych

Projekt zawiera dwie encje:

| Encja | Opis |
| --- | --- |
| `Taxpayer` | podatnik VAT zapisany w lokalnej bazie |
| `VatCheck` | pojedyncze sprawdzenie podatnika |

Relacja:

```text
Taxpayer 1:N VatCheck
```

Dodatkowo na parze `CountryCode` i `VatNumber` ustawiony jest unikalny indeks, żeby nie zapisywać duplikatów tego samego podatnika.

## Technologie

- C#
- .NET 8
- Windows Forms
- Entity Framework Core
- SQLite
- HTTP Client
- JSON
- VIES REST API

## Uruchomienie

Projekt wymaga systemu Windows, ponieważ używa Windows Forms.

W katalogu `Lab2` uruchom:

```powershell
dotnet run --project .\ViesLab2\ViesLab2.csproj
```

Przykładowe dane testowe:

```text
Kraj: PL
VAT ID: 5260309174
```

Po wpisaniu danych kliknij przycisk:

```text
Pobierz z API / z bazy
```

## Budowanie projektu

```powershell
dotnet build .\ViesLab2\ViesLab2.csproj
```

## Baza danych

Aplikacja używa lokalnego pliku SQLite:

```text
vies_lab2.db
```

Plik bazy powstaje automatycznie przy uruchomieniu aplikacji. Można też użyć migracji Entity Framework Core:

```powershell
Add-Migration Init
Update-Database
```

## Struktura projektu

- `ViesLab2/ViesLab2.csproj` - konfiguracja projektu .NET,
- `ViesLab2/Program.cs` - punkt startowy aplikacji,
- `ViesLab2/MainForm.cs` - interfejs Windows Forms i obsługa akcji użytkownika,
- `ViesLab2/Data/AppDbContext.cs` - konfiguracja Entity Framework Core i SQLite,
- `ViesLab2/Models/Taxpayer.cs` - model podatnika,
- `ViesLab2/Models/VatCheck.cs` - model historii sprawdzeń,
- `ViesLab2/Services/ViesService.cs` - komunikacja z API VIES,
- `README.txt` - pierwotna notatka do projektu.
