#!/usr/bin/env bash
#
# Convert GTFS data into ReCHor's binary timetable format and drop the
# result straight into "timetable", for a given week.
#
# Portable rewrite of the zsh/argc "pack-timetable.sh" script originally
# attached by M. Schinz. Differences from the original:
#   - no dependency on zsh, argc, or BSD `date -j` (runs under Git Bash)
#   - compiles GtfsConverter from source on demand instead of requiring a
#     pre-built jar
#   - writes directly into "timetable" instead of producing a zip archive,
#     since that is the directory the app reads from locally
#
# Usage: scripts/update-timetable.sh <gtfs_zip> <first_day YYYY-MM-DD>
#
# <gtfs_zip> is the "Complete feed" archive downloaded by fetch-gtfs.sh (or
# manually from https://gtfs.geops.ch/#completefeed). <first_day> is the
# Monday (or any start date) of the week to convert; the script converts
# that date plus the following 6 days.

set -euo pipefail

if [[ $# -lt 2 ]]; then
    printf "Usage: %s <gtfs_zip> <first_day YYYY-MM-DD>\n" "$0"
    exit 1
fi

gtfs_zip=$1
first_day=$2

script_dir=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
converter_dir="${script_dir}/../tools/gtfs-converter"
timetable_dir="${script_dir}/../timetable"
classes_dir="${converter_dir}/build/classes"
lib_jar="${converter_dir}/lib/fastcsv-3.4.0.jar"

# Convert to native Windows paths (when applicable) before handing them to
# java/javac: the JDK on Windows does not understand MSYS-style paths, and
# the shell's automatic conversion is unreliable once a path contains a
# space (as "ReCHor duo" does).
to_native_path() {
    if command -v cygpath >/dev/null 2>&1; then
        cygpath -m "$1"
    else
        printf '%s' "$1"
    fi
}
classes_dir_native=$(to_native_path "${classes_dir}")
lib_jar_native=$(to_native_path "${lib_jar}")
timetable_dir_native=$(to_native_path "${timetable_dir}")

last_day=$(date -I -d "${first_day} +7 days")

printf "Compiling GtfsConverter...\n"
mkdir -p "${classes_dir}"
java_sources=()
while IFS= read -r -d '' f; do
    java_sources+=("$(to_native_path "${f}")")
done < <(find "${converter_dir}/src" -name '*.java' -print0)
javac -cp "${lib_jar_native}" -d "${classes_dir_native}" "${java_sources[@]}"

tmp_dir=$(mktemp -d "${TMPDIR:-/tmp}/gtfs_XXXX")
tmp_dir_native=$(to_native_path "${tmp_dir}")
printf "Temporary directory: %s\n" "${tmp_dir}"
trap 'rm -rf "${tmp_dir}"' EXIT

printf "Extracting archive...\n"
unzip -q -d "${tmp_dir}/gtfs" "${gtfs_zip}"

printf "Converting data (%s to %s)\n" "${first_day}" "${last_day}"
mkdir -p "${timetable_dir}"
classpath_sep=':'
case "$(uname -s)" in
    MINGW*|MSYS*|CYGWIN*) classpath_sep=';' ;;
esac

java -cp "${classes_dir_native}${classpath_sep}${lib_jar_native}" ch.epfl.rechor.Main \
     "${tmp_dir_native}/gtfs" \
     "${timetable_dir_native}" \
     "${first_day}…${last_day}"

printf "Done. Timetable updated in %s\n" "${timetable_dir}"
