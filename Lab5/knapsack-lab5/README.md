# Knapsack Lab 5

Prosty projekt Java pokazujący zachłanne rozwiązanie problemu plecakowego.

## Co robi aplikacja

- generuje losowe przedmioty na podstawie podanego ziarna (`seed`)
- porównuje przedmioty według stosunku `value / weight`
- wybiera przedmioty tak długo, jak mieszczą się w plecaku
- wypisuje wygenerowany problem i uzyskane rozwiązanie

## Wymagania

- Java 17
- JDK, nie samo `java`

Do uruchamiania testów dodany jest Maven Wrapper, więc nie musisz mieć globalnie zainstalowanego `mvn`.

## Uruchamianie testów

Na Windows:

```powershell
.\mvnw.cmd test
```

Na Linux / macOS:

```bash
./mvnw test
```

## Uruchamianie programu

Możesz uruchomić klasę `org.knapsack.Main` z IDE albo przez Maven:

```powershell
.\mvnw.cmd -q exec:java
```

Jeśli nie masz skonfigurowanego pluginu `exec`, najprościej uruchomić projekt bezpośrednio z IDE.

## Struktura projektu

- `src/main/java/org/knapsack` - kod aplikacji
- `src/test/java/org/knapsack` - testy automatyczne
- `pom.xml` - konfiguracja Maven
