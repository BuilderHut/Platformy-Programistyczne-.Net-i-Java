Laboratorium 2 - projekt na ocenę 4.0
Temat: VIES API + baza SQLite + Entity Framework Core

Co projekt spełnia:
1. Pobranie danych z zewnętrznego API VIES.
2. Deserializacja odpowiedzi JSON.
3. Zapis danych do bazy SQLite przez Entity Framework Core.
4. Obsługa bazy: dodawanie, usuwanie, filtrowanie, sortowanie.
5. Relacja w bazie danych:
   Taxpayer 1 : N VatCheck
6. Dane z API nie są pobierane drugi raz, jeśli rekord jest już w bazie.

Jak uruchomić:
1. Otwórz folder ViesLab2 w Visual Studio 2022.
2. Przy pierwszym uruchomieniu Visual Studio powinno pobrać paczki NuGet.
3. Uruchom projekt przyciskiem Start.
4. Domyślne dane testowe:
   Kraj: PL
   VAT ID: 5260309174
5. Kliknij „Pobierz z API / z bazy”.

Wymagane paczki NuGet:
- Microsoft.EntityFrameworkCore 8.0.3
- Microsoft.EntityFrameworkCore.Sqlite 8.0.3
- Microsoft.EntityFrameworkCore.Tools 8.0.3

Opcjonalnie migracje w Package Manager Console:
Add-Migration Init
Update-Database

Uwaga:
Projekt używa też Database.EnsureCreated(), więc baza SQLite może utworzyć się automatycznie przy starcie aplikacji.
