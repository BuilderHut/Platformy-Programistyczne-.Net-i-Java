# Laboratorium 7 - JHipster Car Rental App

Projekt wykonany w ramach zajęć laboratoryjnych z przedmiotu **Platformy Programistyczne .NET i Java**.

Temat laboratorium: **Generatory aplikacji Java**. Celem projektu było wygenerowanie i uruchomienie aplikacji bazodanowej z użyciem JHipster, a następnie rozszerzenie jej o własny model bazy danych.

W projekcie zostały wykonane:

- **Zadanie 1** - wygenerowanie i uruchomienie aplikacji monolitycznej JHipster wraz z testami oraz panelem administracyjnym.
- **Zadanie 2** - dodanie własnej struktury bazy danych dla aplikacji wypożyczalni samochodów.

Zadanie 3, czyli architektura mikroserwisowa uruchamiana w Dockerze, nie jest częścią tego projektu.

## Opis aplikacji

Aplikacja **Car Rental App** jest prostym systemem do obsługi wypożyczalni samochodów. Projekt pozwala zarządzać klientami, prawami jazdy, samochodami, wypożyczeniami oraz kategoriami pojazdów.

Aplikacja została wygenerowana jako monolit JHipster z backendem Spring Boot WebFlux, frontendem Angular oraz bazą danych MySQL. Dodatkowo wykorzystuje Elasticsearch do wyszukiwania danych.

## Wykonane zadanie 1

W ramach pierwszego zadania wykonano aplikację zgodnie z wymaganiami instrukcji laboratoryjnej:

- wygenerowano aplikację typu **Monolithic application**,
- ustawiono nazwę aplikacji `carrental`,
- wybrano Maven jako narzędzie budowania projektu,
- włączono Spring WebFlux,
- ustawiono port aplikacji na `8080`,
- skonfigurowano uwierzytelnianie JWT,
- wybrano bazę danych MySQL dla profilu developerskiego i produkcyjnego,
- dodano Elasticsearch,
- wybrano Angular jako framework frontendowy,
- włączono panel administracyjny JHipster,
- włączono internacjonalizację z językami angielskim i polskim,
- dodano testy Cypress wraz z obsługą code coverage i audytu.

Po uruchomieniu aplikacji dostępne są standardowe funkcje JHipster:

- logowanie użytkowników,
- rejestracja kont,
- panel administracyjny,
- podgląd metryk,
- konfiguracja logów,
- dokumentacja API,
- endpointy monitorujące pod `/management`.

Domyślne konta:

| Login | Hasło | Rola |
| --- | --- | --- |
| `admin` | `admin` | administrator |
| `user` | `user` | użytkownik |

## Wykonane zadanie 2

W drugim zadaniu do aplikacji dodano własny model danych opisujący wypożyczalnię samochodów. Model został zapisany w pliku `database.jdl` i zaimportowany do projektu przez JHipster.

Wymagania z instrukcji zostały spełnione w następujący sposób:

| Wymaganie | Realizacja w projekcie |
| --- | --- |
| Minimum trzy encje | utworzono pięć encji: `Customer`, `DrivingLicense`, `Car`, `Rental`, `Category` |
| Relacja OneToOne | `Customer` - `DrivingLicense` |
| Relacja OneToMany | `Customer` - `Rental` |
| Relacja ManyToMany | `Car` - `Category` |
| Różne typy pól | użyto m.in. `String`, `Integer`, `Float`, `LocalDate`, `Enum` |
| Paginacja tabel | wszystkie encje mają włączoną paginację |
| Warstwa serwisowa | encje wygenerowano z `serviceClass` |
| DTO | encje wykorzystują DTO generowane przez MapStruct |
| Wyszukiwanie | encje są indeksowane w Elasticsearch |

## Model bazy danych

Główne encje projektu:

| Encja | Opis |
| --- | --- |
| `Customer` | klient wypożyczalni |
| `DrivingLicense` | prawo jazdy klienta |
| `Car` | samochód dostępny w wypożyczalni |
| `Rental` | wypożyczenie samochodu przez klienta |
| `Category` | kategoria samochodu |

Pola encji:

| Encja | Pola |
| --- | --- |
| `Customer` | `firstName`, `lastName`, `email`, `phone` |
| `DrivingLicense` | `licenseNumber`, `issueDate`, `expirationDate` |
| `Car` | `brand`, `model`, `productionYear`, `dailyPrice`, `status` |
| `Rental` | `startDate`, `endDate`, `totalPrice`, `status` |
| `Category` | `name`, `description` |

Statusy samochodu:

- `AVAILABLE` - dostępny,
- `RENTED` - wypożyczony,
- `SERVICE` - w serwisie.

Statusy wypożyczenia:

- `PLANNED` - zaplanowane,
- `ACTIVE` - aktywne,
- `FINISHED` - zakończone,
- `CANCELLED` - anulowane.

Relacje:

- `Customer` 1:1 `DrivingLicense`,
- `Customer` 1:N `Rental`,
- `Rental` N:1 `Car`,
- `Car` N:M `Category`.

## Użyte technologie

- Java 21
- JHipster 9.1.0
- Spring Boot 3.5.14
- Spring WebFlux
- Spring Security
- JWT
- Maven
- MySQL
- R2DBC
- Liquibase
- Elasticsearch
- MapStruct
- Angular 21
- Bootstrap 5
- Cypress
- Vitest
- Docker Compose

## Struktura projektu

```text
car-rental-app/
|-- .jhipster/                         konfiguracja encji JHipster
|-- database.jdl                       model bazy danych w JDL
|-- pom.xml                            konfiguracja Maven
|-- package.json                       skrypty npm i zależności frontendu
|-- src/main/java/com/mycompany/carrental
|   |-- config/                        konfiguracja Spring Boot
|   |-- domain/                        encje i typy wyliczeniowe
|   |-- repository/                    repozytoria bazodanowe i wyszukiwarki
|   |-- service/                       klasy serwisowe, DTO i mappery
|   |-- web/rest/                      kontrolery REST
|-- src/main/resources/config
|   |-- application.yml                konfiguracja główna aplikacji
|   |-- application-dev.yml            konfiguracja profilu developerskiego
|   |-- application-prod.yml           konfiguracja profilu produkcyjnego
|   |-- liquibase/                     migracje bazy danych
|-- src/main/webapp/app                aplikacja frontendowa Angular
|-- src/main/docker                    pliki Docker Compose
|-- src/test                           testy backendu i frontendu
```

## Uruchomienie aplikacji

Projekt był wykonywany pod systemem Windows, dlatego poniższe komendy podano w wersji dla PowerShell.

1. Uruchom Docker Desktop.

2. Przejdź do katalogu projektu:

```powershell
cd car-rental-app
```

3. Uruchom usługi wymagane przez aplikację:

```powershell
.\npmw.cmd run services:up
```

Komenda uruchamia kontenery MySQL i Elasticsearch zdefiniowane w `src/main/docker/services.yml`.

4. Uruchom backend:

```powershell
.\npmw.cmd run backend:start
```

Backend działa pod adresem:

```text
http://localhost:8080
```

5. W drugim terminalu uruchom frontend:

```powershell
.\npmw.cmd start
```

Frontend Angular działa pod adresem:

```text
http://localhost:4200
```

Można też uruchomić backend i frontend jednocześnie:

```powershell
.\npmw.cmd run watch
```

## Przydatne komendy

Instalacja zależności frontendu:

```powershell
.\npmw.cmd install
```

Uruchomienie testów backendu:

```powershell
.\mvnw.cmd verify
```

Uruchomienie testów frontendu:

```powershell
.\npmw.cmd test
```

Uruchomienie testów end-to-end Cypress:

```powershell
.\npmw.cmd run e2e
```

Sprawdzenie jakości kodu:

```powershell
.\npmw.cmd run lint
```

Zbudowanie wersji produkcyjnej:

```powershell
.\mvnw.cmd -Pprod clean verify
```

Uruchomienie zbudowanego pliku JAR:

```powershell
java -jar target\*.jar
```

Zatrzymanie kontenerów developerskich:

```powershell
docker compose -f src/main/docker/services.yml down
```

## Najważniejsze endpointy API

| Funkcja | Endpoint |
| --- | --- |
| Logowanie | `POST /api/authenticate` |
| Konto użytkownika | `/api/account` |
| Klienci | `/api/customers` |
| Prawa jazdy | `/api/driving-licenses` |
| Samochody | `/api/cars` |
| Wypożyczenia | `/api/rentals` |
| Kategorie | `/api/categories` |
| Wyszukiwanie encji | `/api/<encja>/_search` |
| Monitoring | `/management/*` |

## Migracje bazy danych

Migracje Liquibase znajdują się w katalogu:

```text
src/main/resources/config/liquibase/
```

Główny plik migracji:

```text
src/main/resources/config/liquibase/master.xml
```

W profilu developerskim aktywne są konteksty `dev, faker`, dlatego aplikacja może ładować dane przykładowe z katalogu:

```text
src/main/resources/config/liquibase/fake-data/
```

## Pliki istotne dla zadania laboratoryjnego

- `database.jdl` - definicja własnego modelu bazy danych z Zadania 2,
- `.jhipster/*.json` - konfiguracje encji wygenerowane przez JHipster,
- `src/main/java/com/mycompany/carrental/domain` - klasy encji,
- `src/main/java/com/mycompany/carrental/web/rest` - kontrolery REST,
- `src/main/webapp/app/entities` - widoki CRUD po stronie Angulara,
- `src/main/resources/config/liquibase` - migracje bazy danych,
- `src/main/docker` - konfiguracja kontenerów MySQL i Elasticsearch.

## Podsumowanie

Projekt realizuje dwa pierwsze zadania z instrukcji laboratoryjnej:

1. Wygenerowano i uruchomiono aplikację monolityczną JHipster z panelem administracyjnym, logowaniem JWT, testami oraz monitoringiem.
2. Rozszerzono aplikację o własny model bazy danych dla wypożyczalni samochodów, zawierający wymagane encje, relacje, typy pól i paginację.

Projekt może być dalej rozwijany przez dodanie reguł biznesowych, np. blokady wypożyczenia samochodu będącego w serwisie, automatycznego wyliczania ceny wypożyczenia lub zmiany statusu samochodu po utworzeniu wypożyczenia.
