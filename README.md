# ReCHor

Planificateur de trajets en transports publics suisses (projet PPOO, EPFL). Le
code de l'application se trouve dans [`ReCHor 2/`](<ReCHor 2>).

## Mise à jour des données d'horaire

L'application lit son horaire dans `ReCHor 2/timetable/`, sous forme de
fichiers binaires (`stations.bin`, `routes.bin`, ...) plus un sous-dossier par
jour (`timetable/<YYYY-MM-DD>/trips.bin`, `connections.bin`,
`connections-succ.bin`).

Un vrai mode temps réel n'est pas réalisable sans changements majeurs à
l'application (confirmé par l'enseignant). L'approche retenue est donc celle
des années précédentes : télécharger régulièrement les données GTFS
officielles puis les convertir au format de ReCHor.

1. **Télécharger les données GTFS** :
   ```
   scripts/fetch-gtfs.sh
   ```
   Télécharge (si une nouvelle version existe) le flux complet depuis
   <https://gtfs.geops.ch/#completefeed> dans `data/gtfs-cache/`.

2. **Convertir une semaine de données** :
   ```
   scripts/update-timetable.sh data/gtfs-cache/gtfs_complete_<date>.zip <premier_jour YYYY-MM-DD>
   ```
   Convertit les données GTFS pour `<premier_jour>` et les 6 jours suivants,
   et écrit directement le résultat dans `ReCHor 2/timetable/`.

Ces deux scripts (dans `scripts/`) sont une réécriture portable (bash
simple, sans `argc` ni zsh) des scripts fournis par l'enseignant. Le
convertisseur lui-même (`GtfsConverter`) est vendu dans
[`tools/gtfs-converter/`](tools/gtfs-converter) ; les scripts le compilent
automatiquement à la volée (nécessite un JDK — testé avec le JDK 23).

Les données générées (`ReCHor 2/timetable/`) et le cache GTFS
(`data/gtfs-cache/`) ne sont pas versionnés (voir `.gitignore`) : il faut
relancer les scripts ci-dessus après avoir cloné le dépôt pour obtenir un
horaire à jour.
