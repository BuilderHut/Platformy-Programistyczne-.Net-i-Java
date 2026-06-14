# Laboratorium 4 - Blazor i ML.NET

Aplikacja webowa Blazor Server przygotowana w ramach laboratorium z użyciem ML.NET. Projekt udostępnia prosty widok do analizy sentymentu tekstu.

## Co robi aplikacja

- uruchamia aplikację Blazor Server,
- udostępnia stronę `Sentiment` pod adresem `/sentiment`,
- pozwala wpisać krótki tekst lub tweet,
- przekazuje tekst do modelu ML.NET,
- wyświetla przewidzianą etykietę sentymentu oraz poziom pewności predykcji.

## Technologie

- C#
- .NET 8
- Blazor Server
- Razor Components
- ML.NET
- FastTree

## Model ML

W projekcie znajduje się wygenerowany model:

```text
Lab4Blazor/MLModel.mlnet
```

Kod obsługi modelu został wygenerowany przez ML.NET Model Builder:

- `MLModel.consumption.cs` - ładowanie modelu i predykcja,
- `MLModel.training.cs` - kod ponownego trenowania modelu,
- `MLModel.evaluate.cs` - pomocniczy kod ewaluacji.

Model klasyfikuje tekst na podstawie danych tweetów. W widoku aplikacji etykieta `0` jest interpretowana jako sentyment negatywny, a etykieta `4` jako sentyment pozytywny.

## Uruchomienie

W katalogu `Lab4` uruchom:

```powershell
dotnet run --project .\Lab4Blazor\Lab4Blazor.csproj
```

Po starcie aplikacji adres lokalny zostanie wypisany w konsoli. Strona z analizą sentymentu jest dostępna pod ścieżką:

```text
/sentiment
```

## Budowanie projektu

```powershell
dotnet build .\Lab4Blazor.sln
```

## Uwaga dotycząca trenowania

Plik `MLModel.training.cs` zawiera lokalną ścieżkę do zbioru treningowego użytego podczas tworzenia modelu. Do samego uruchomienia aplikacji wystarczy plik `MLModel.mlnet`, ale ponowne trenowanie modelu wymaga dostępu do oryginalnego pliku CSV albo podania nowej ścieżki do danych.

## Struktura projektu

- `Lab4Blazor.sln` - rozwiązanie Visual Studio,
- `Lab4Blazor/Program.cs` - konfiguracja aplikacji Blazor,
- `Lab4Blazor/Components/Pages/Sentiment.razor` - widok analizy sentymentu,
- `Lab4Blazor/MLModel.*.cs` - pliki wygenerowane przez ML.NET Model Builder,
- `Lab4Blazor/MLModel.mlnet` - zapisany model uczenia maszynowego.
