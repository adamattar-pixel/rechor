# ReCHor

**An offline Swiss public transport journey planner written in Java (JavaFX).**
Pick a departure stop, an arrival stop, a date and a time, and ReCHor lists the best connections, much like cff.ch or search.ch, but it runs entirely on your machine against a local copy of the official Swiss timetable.

Course project for **CS-108 (Practice of Object-Oriented Programming), EPFL, spring 2025**, built by [Adam Attar](https://github.com/adamattar-pixel) and Saad Amraoui.

## Features

- **Multi-criteria search.** Results are Pareto-optimal over departure time, arrival time and number of changes: no proposed journey is beaten by another on every criterion. You choose between leaving earlier with fewer changes, or later with more.
- **Fully offline.** The search runs on a pre-converted timetable stored on disk, with no network access needed at query time.
- **Fast stop search.** Accent-insensitive, case-insensitive matching of stop names and aliases (typing `lausanne gare` or `geneve` works).
- **JavaFX interface.** A query panel, a list of all journeys found for the day, and a detail view of the selected journey (legs, platforms, intermediate stops, walking transfers, vehicle icons).
- **Map export.** Opens the selected journey as a GeoJSON line on [uMap](https://umap.osm.ch).
- **Calendar export.** Saves the selected journey as an iCalendar (`.ics`) file, see [`examples/`](examples/).

## How it works

1. **Data.** The official GTFS feed is converted into compact binary files (stations, routes, trips, connections, transfers) that are memory-mapped by the application.
2. **Routing.** `Router` implements the **Connection Scan Algorithm (CSA)**. For a given date and arrival station, it scans all connections of the day in decreasing order of departure time and builds a `Profile`: for every station and every trip, a Pareto front of optimal (arrival time, changes) options.
3. **Compact criteria.** Each Pareto tuple (arrival time, departure time, number of changes, payload) is bit-packed into a single `long` (`PackedCriteria`), and fronts are stored in plain `long[]` arrays (`ParetoFront`). Index ranges and connection references are packed the same way (`PackedRange`, `Bits32_24_8`).
4. **Extraction.** `JourneyExtractor` walks the profile from the departure station and rebuilds full `Journey` objects (a sealed hierarchy of `Leg`: `Transport` and `Foot`), sorted by departure then arrival time.
5. **Presentation.** `StopIndex` powers stop autocompletion, the JavaFX UI renders the journeys, and `JourneyGeoJsonConverter` / `JourneyIcalConverter` handle the exports.

## Project structure

```
src/ch/epfl/rechor/
  journey/              Router (CSA), Profile, ParetoFront, PackedCriteria,
                        JourneyExtractor, Journey, GeoJSON and iCalendar converters
  gui/                  JavaFX app: Main, QueryUI, SummaryUI, DetailUI, StopField
  StopIndex.java        Fuzzy stop-name search
  IcalBuilder.java      iCalendar writer
  Json.java             Minimal JSON model
  FormatterFr.java      French date and duration formatting
  Bits32_24_8.java, PackedRange.java, Preconditions.java
test/                   JUnit tests
resources/              Vehicle icons and CSS
scripts/                GTFS download and conversion scripts
tools/gtfs-converter/   GTFS to ReCHor binary converter
examples/               Sample iCalendar exports
```

## Getting started

### Requirements

- JDK 23 (the version it was developed and tested with)
- A Unix-like shell (Git Bash works on Windows)

### Updating the timetable data

The application reads its timetable from `timetable/`, as binary files (`stations.bin`, `routes.bin`, ...) plus one sub-folder per day (`timetable/<YYYY-MM-DD>/trips.bin`, `connections.bin`, `connections-succ.bin`).

The generated data and the GTFS cache are not versioned (see `.gitignore`), so re-run the scripts after cloning:

1. **Download the GTFS data**

   ```
   scripts/fetch-gtfs.sh
   ```

   Downloads the full feed from [geOps](https://gtfs.geops.ch/#completefeed) into `data/gtfs-cache/` (only if a newer version exists).

2. **Convert one week of data**

   ```
   scripts/update-timetable.sh data/gtfs-cache/gtfs_complete_<date>.zip <first_day YYYY-MM-DD>
   ```

   Converts the given day and the 6 following days, and writes the result directly into `timetable/`.

The converter itself (`GtfsConverter`) is vendored in `tools/gtfs-converter/` and compiled on the fly by the scripts.

> Note: the `ch.epfl.rechor.timetable` package (readers for the binary timetable files) is not included in this repository, so the application cannot be built from this repo alone.

## Real-time data

Real-time updates are out of scope: they would require major changes to the architecture. Instead, re-run the two scripts above regularly to refresh the timetable.

## Credits

- Application code by Adam Attar and Saad Amraoui.
- The GTFS converter in `tools/gtfs-converter/` and the scripts' original versions were provided by the CS-108 teaching staff. The scripts here are a portable bash rewrite. The converter uses [FastCSV](https://github.com/osiegmar/FastCSV).
- Timetable data: official Swiss public transport timetable, via geOps.
- The course permits publishing this project.
