# Image Editor - Laboratorium 6

Aplikacja desktopowa JavaFX do prostej obrobki obrazow JPG. Program pozwala
wczytac obraz, podejrzec wersje oryginalna i wynikowa, wykonac operacje na
pikselach oraz zapisac rezultat jako plik JPG.

## Funkcje

- wczytywanie obrazow w formacie `.jpg`
- podglad obrazu oryginalnego i obrazu po zmianach
- generowanie negatywu
- progowanie obrazu dla wartosci z zakresu `0-255`
- konturowanie obrazu z uzyciem masek Sobela
- obrot obrazu w lewo i w prawo
- skalowanie obrazu do podanych wymiarow
- zapis wyniku do katalogu `Pictures` uzytkownika

## Wymagania

- JDK 21
- Maven 3.9 lub nowszy

## Uruchamianie

W katalogu projektu `image-editor` uruchom:

```powershell
mvn clean javafx:run
```

Jesli projekt jest otwierany w IDE, nalezy uruchomic klase:

```text
pl.pwr.Main
```

## Struktura projektu

- `src/main/java/pl/pwr/Main.java` - glowna klasa aplikacji JavaFX
- `src/main/resources` - zasoby graficzne uzywane w interfejsie
- `pom.xml` - konfiguracja Maven i zaleznosci JavaFX
