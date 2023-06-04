==============================
Pac-Man Hra
==============================

Popis:
--------------
Implementácia klasickej hry Pac-Man v jazyku Java, obsahuje základný herný engine, načítavanie vlastnej mapy a základnú umelej inteligencie pre pohyb duchov.

Autori:
--------------
Andrej Horáček (xhorac20)

Závislosti:
--------------
- Java SE Development Kit (JDK) verzia 11 alebo vyššia

Ako zostaviť a spustiť projekt:
--------------
1. Uistite sa, že máte nainštalovanú požadovanú verziu JDK na vašom zariadení.
2. Prejdite do priečinka projektu vo vašom termináli alebo príkazovom riadku.
3. Skompilujte projekt pomocou nasledujúceho príkazu:

   javac -d bin -sourcepath src src/App/PacManApp.java

   Tento príkaz skompiluje projekt a uloží skompilované .class súbory do priečinka "bin".

4. Spustite skompilovaný projekt pomocou nasledujúceho príkazu:

   java -classpath bin App.PacManApp

   Tento príkaz spustí hru Pac-Man.

Poznámky:
--------------
- Hra načíta vlastnú mapu zo súboru .txt (data/mapa01.txt). Ak chcete zmeniť mapu, upravte obsah tohto súboru alebo načítajte iný súbor s mapou v triede 'PacManApp'.
- Základ umelej inteligencie pre duchov je implementovaný, ale môže byť ďalej vylepšený.
- Hra obsahuje jednoduché užívateľské rozhranie a podporuje základný vstup užívateľa pre ovládanie postavy Pac-Man.
