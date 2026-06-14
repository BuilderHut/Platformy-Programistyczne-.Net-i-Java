# Laboratorium 2 - Problem plecakowy

Projekt w C# realizujący zachłanne rozwiązanie problemu plecakowego. W laboratorium przygotowano wersję konsolową, projekt GUI w Windows Forms oraz projekt testów jednostkowych.

## Co robi aplikacja

- pobiera od użytkownika liczbę przedmiotów, ziarno generatora losowego oraz pojemność plecaka,
- generuje listę przedmiotów z losową wartością i wagą,
- sortuje przedmioty według stosunku `value / weight`,
- wybiera kolejne przedmioty tak długo, jak mieszczą się w plecaku,
- wypisuje wygenerowany problem oraz uzyskane rozwiązanie.

## Projekty w rozwiązaniu

| Projekt | Opis |
| --- | --- |
| `lab1-dot-net` | aplikacja konsolowa z logiką problemu plecakowego |
| `GUI` | interfejs graficzny Windows Forms |
| `TestProject` | testy jednostkowe MSTest |

## Technologie

- C#
- .NET 8
- Windows Forms
- MSTest

## Uruchomienie wersji konsolowej

W katalogu `Lab2` uruchom:

```powershell
dotnet run --project .\lab1-dot-net\lab1-dot-net.csproj
```

Program zapyta o:

- liczbę przedmiotów,
- seed generatora losowego,
- pojemność plecaka.

## Uruchomienie GUI

Projekt GUI wymaga systemu Windows, ponieważ korzysta z Windows Forms.

```powershell
dotnet run --project .\GUI\GUI.csproj
```

## Testy

Projekt `TestProject` zawiera testy jednostkowe sprawdzające m.in. generowanie przedmiotów, ograniczenie pojemności plecaka oraz obsługę niepoprawnych danych.

```powershell
dotnet test .\TestProject\TestProject.csproj
```

## Obecny stan techniczny

W aktualnym stanie repozytorium projekt konsolowy buduje się poprawnie, ale GUI i testy odwołują się do klasy `Problem`, podczas gdy główna logika problemu plecakowego znajduje się w klasie `Solution`. Przed uruchomieniem całego rozwiązania należy ujednolicić nazwy klas albo dostosować odwołania w `GUI` i `TestProject`.

## Struktura projektu

- `lab1-dot-net.sln` - rozwiązanie Visual Studio,
- `lab1-dot-net/` - aplikacja konsolowa i logika problemu,
- `GUI/` - aplikacja Windows Forms,
- `TestProject/` - testy jednostkowe.
