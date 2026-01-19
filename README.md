# Minecraft-farming-bot (Fabric client mod) — 1.21.4

Opis:
Mod działający po stronie klienta (Fabric), który po wpisaniu w czacie "#farm" przejmuje kontrolę nad graczem i automatycznie zbiera i sadzi uprawy (wheat, carrots, potatoes, beetroot) oraz zbiera melony. Komunikacja z graczem: jeśli bot nie może wziąć seedów z ekwipunku (np. brak miejsca lub brak seedów) wysyła prywatną wiadomość:
  /msg <player> Throw away unnecessary items from your inventory.
Tylko ten gracz zobaczy tę wiadomość.

Instalacja:
1. Skompiluj projekt: ./gradlew build
2. Skopiuj jar z build/libs do folderu mods Twojego profilu Fabric 1.21.4
3. Uruchom Minecraft i zaloguj się na konto bota (klient).
4. W grze wpisz "#farm" aby uruchomić lub użyj komendy client-side "/farm". Użyj "#farmstop" lub "/farmstop" aby zatrzymać.

Uwaga:
- Ruch klienta i interakcje są implementowane w prosty sposób; docelowo poprawimy pathfinding/sterowanie, aby zachowanie było płynniejsze.
- Używaj na serwerach testowych lub tam, gdzie automatyczne boty są dozwolone.
- Po przetestowaniu zgłoś problemy — naniosę poprawki w PR.