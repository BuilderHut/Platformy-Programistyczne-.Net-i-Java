# Laboratorium 3 - Równoległe mnożenie macierzy

Projekt konsolowy w C# porównujący dwa sposoby równoległego mnożenia macierzy: z użyciem `Parallel.For` oraz ręcznie tworzonych obiektów `Thread`.

## Co robi program

- generuje losowe macierze,
- mnoży macierze metodą opartą o `Parallel.For`,
- mnoży macierze metodą opartą o klasę `Thread`,
- sprawdza zgodność wyników obu metod,
- wykonuje prosty benchmark dla różnych rozmiarów macierzy i liczby wątków,
- wypisuje średni czas wykonania i przyspieszenie względem wariantu jednowątkowego.

## Technologie

- C#
- .NET 8
- `System.Threading`
- `System.Threading.Tasks`

## Zakres testowanych danych

W programie ustawiono:

- rozmiary macierzy: `100`, `200`, `400`, `800`,
- liczby wątków: `1`, `2`, `4`, `6`, `8`, `12`, `16`,
- liczbę prób dla konfiguracji: `5`.

Parametry można zmienić w pliku `ConsoleApp1/ConsoleApp1/Program.cs`.

## Uruchomienie

W katalogu `Lab3` uruchom:

```powershell
dotnet run --project .\ConsoleApp1\ConsoleApp1\ConsoleApp1.csproj
```

## Budowanie projektu

```powershell
dotnet build .\ConsoleApp1\ConsoleApp1.sln
```

## Struktura projektu

- `ConsoleApp1/ConsoleApp1.sln` - rozwiązanie Visual Studio,
- `ConsoleApp1/ConsoleApp1/Program.cs` - cała implementacja programu,
- `Matrix` - klasa reprezentująca macierz,
- `MatrixMultiplierParallel` - mnożenie macierzy przez `Parallel.For`,
- `MatrixMultiplierThread` - mnożenie macierzy przez ręczne zarządzanie wątkami,
- `BenchmarkRunner` - uruchamianie pomiarów czasu.
