#!/usr/bin/env bash
#
# Fetch the current GTFS data for Switzerland.
#
# Portable rewrite of the zsh/argc script attached by M. Schinz (see
# "To DO/Re_ Finir le project PPOO (Rechor).msg") so it runs under Git Bash
# on Windows without installing zsh or argc.
#
# Usage: scripts/fetch-gtfs.sh

set -euo pipefail

url="https://gtfs.geops.ch/dl/gtfs_complete.zip"

script_dir=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
cache_dir="${script_dir}/../data/gtfs-cache"
mkdir -p "${cache_dir}"

etag="${cache_dir}/etag.txt"
etag_new="${cache_dir}/etag-new.txt"
output_file="${cache_dir}/gtfs_complete_$(date -I).zip"

if [[ -f "${output_file}" ]]; then
    printf "Error: output file %s already exists!\n" "${output_file}"
    exit 1
fi

curl --silent \
     --output "${output_file}" \
     --etag-save "${etag_new}" \
     --etag-compare "${etag}" \
     "${url}"

if [[ -s "${etag_new}" ]]; then
    mv "${etag_new}" "${etag}"
    printf "Downloaded new version to %s.\n" "${output_file}"
else
    rm -f "${etag_new}"
    rm -f "${output_file}"
    printf "Remote file unchanged, nothing downloaded.\n"
fi
