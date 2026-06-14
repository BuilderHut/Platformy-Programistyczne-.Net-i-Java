# Laboratorium 0 - FizzBuzz

Prosty projekt konsolowy w C# przygotowany w ramach laboratorium 0. Aplikacja wypisuje liczby od 1 do 20 zgodnie z zasadami zadania FizzBuzz.

## Co robi program

- wypisuje kolejne liczby od `1` do `20`,
- dla liczb podzielnych przez `3` wypisuje `Fizz`,
- dla liczb podzielnych przez `5` wypisuje `Buzz`,
- dla liczb podzielnych jednocześnie przez `3` i `5` wypisuje `FizzBuzz`.

## Technologie

- C#
- .NET 8
- aplikacja konsolowa

## Uruchomienie

W katalogu `Lab0` uruchom:

```powershell
dotnet run --project .\zad0\zad0.csproj
```

## Budowanie projektu

```powershell
dotnet build .\zad0.sln
```

## Struktura projektu

- `zad0.sln` - plik rozwiązania Visual Studio,
- `zad0/zad0.csproj` - konfiguracja projektu .NET,
- `zad0/Program.cs` - implementacja programu i klasy `FizzBuzz`.
