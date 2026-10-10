package ch.epfl.rechor.journey;

import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyProfileTest9 {
    Profile readProfile(TimeTable timeTable, LocalDate date,int arrStationId) throws IOException {
        Path path =
                Path.of("timetable/profile_" + date + "_" + arrStationId + ".txt");
        try (BufferedReader r = Files.newBufferedReader(path)) {
            Profile.Builder profileB =
                    new Profile.Builder(timeTable, date, arrStationId);
            int stationId = -1;
            String line;
            while ((line = r.readLine()) != null) {
                stationId += 1;
                if (line.isEmpty()) continue;
                ParetoFront.Builder frontB = new ParetoFront.Builder();
                for (String t : line.split(","))
                    frontB.add(Long.parseLong(t, 16));
                profileB.setForStation(stationId, frontB);
            }
            return profileB.build();
        }
    }

    @Test
    public void test() throws IOException {
        TimeTable t = FileTimeTable.in(Path.of("timetable"));
        LocalDate date = LocalDate.of(2025, Month.MARCH, 18);
        Profile p = readProfile(t, date, 11486);
        List<Journey> js = JourneyExtractor.journeys(p, 7800);
        StringBuilder j = new StringBuilder();
        for (int i = 0; i < js.size(); i++) {
            System.out.println(JourneyIcalConverter.toIcalendar(js.get(i)));
            j.append(JourneyIcalConverter.toIcalendar(js.get(i)));
        }
        assertEquals(test2(), deleteRemove(j.toString()));

    }

    public static String test2() throws IOException {
        return deleteRemove("BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:0d10478e-fd61-4a9c-bf68-7a17e98d5074\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T054500\r\n" +
                "DTEND:20250318T085700\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:5h45 Ebmatingen, Looren → Zürich, Klusplatz (arr. 6h02)\\nchange\r\n" +
                " ment (2 min)\\n6h13 Zürich, Klusplatz (quai A) → Zürich, Bahnhofplatz/HB (a\r\n" +
                " rr. 6h23)\\ntrajet à pied (7 min)\\n6h32 Zürich HB (voie 32) → Fribourg/Frei\r\n" +
                " burg (arr. 7h56 voie 2)\\nchangement (4 min)\\n8h04 Fribourg/Freiburg (voie \r\n" +
                " 2) → Bulle (arr. 8h41 voie 2)\\nchangement (3 min)\\n8h50 Bulle (voie 4) → G\r\n" +
                " ruyères (arr. 8h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:a559fa35-b7c0-4d80-98f8-ff31d40265e3\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T060600\r\n" +
                "DTEND:20250318T092800\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:6h06 Ebmatingen, Looren → Zollikon, Bahnhof (arr. 6h28)\\ntrajet\r\n" +
                "  à pied (3 min)\\n6h34 Zollikon (voie 3) → Zürich HB (arr. 6h44 voie 41/42)\r\n" +
                " \\nchangement (7 min)\\n7h02 Zürich HB (voie 31) → Bern (arr. 7h58 voie 7)\\n\r\n" +
                " changement (6 min)\\n8h09 Bern (quai E-H) → Bulle (arr. 9h11 voie 2)\\nchang\r\n" +
                " ement (3 min)\\n9h20 Bulle (voie 4) → Gruyères (arr. 9h28 voie 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:f41fce04-a587-47b8-91bb-9f099653991d\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T062100\r\n" +
                "DTEND:20250318T092800\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:6h21 Ebmatingen, Looren → Binz bei Maur, Gassacher (arr. 6h26)\\\r\n" +
                " nchangement (1 min)\\n6h27 Binz bei Maur, Gassacher → Zürich, Klusplatz (ar\r\n" +
                " r. 6h40 quai C)\\nchangement (2 min)\\n6h44 Zürich, Klusplatz (quai H) → Zür\r\n" +
                " ich, Bahnhofplatz/HB (arr. 6h54)\\ntrajet à pied (7 min)\\n7h02 Zürich HB (v\r\n" +
                " oie 31) → Bern (arr. 7h58 voie 7)\\nchangement (6 min)\\n8h09 Bern (quai E-H\r\n" +
                " ) → Bulle (arr. 9h11 voie 2)\\nchangement (3 min)\\n9h20 Bulle (voie 4) → Gr\r\n" +
                " uyères (arr. 9h28 voie 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:ae36e9ac-d7f1-4e90-be6d-9ca65574cec1\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T064800\r\n" +
                "DTEND:20250318T095700\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:6h48 Ebmatingen, Looren → Stettbach, Bahnhof (arr. 7h13 quai D)\r\n" +
                " \\ntrajet à pied (3 min)\\n7h17 Stettbach (voie 1) → Zürich HB (arr. 7h25 vo\r\n" +
                " ie 41/42)\\nchangement (7 min)\\n7h32 Zürich HB (voie 32) → Fribourg/Freibur\r\n" +
                " g (arr. 8h56 voie 2)\\nchangement (4 min)\\n9h04 Fribourg/Freiburg (voie 2) \r\n" +
                " → Bulle (arr. 9h41 voie 2)\\nchangement (3 min)\\n9h50 Bulle (voie 4) → Gruy\r\n" +
                " ères (arr. 9h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:80a078c8-6948-4823-82f1-7a0e7a960822\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T071800\r\n" +
                "DTEND:20250318T102800\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:7h18 Ebmatingen, Looren → Stettbach, Bahnhof (arr. 7h43 quai D)\r\n" +
                " \\ntrajet à pied (3 min)\\n7h47 Stettbach (voie 1) → Zürich HB (arr. 7h55 vo\r\n" +
                " ie 41/42)\\nchangement (7 min)\\n8h02 Zürich HB (voie 31) → Bern (arr. 8h58 \r\n" +
                " voie 8)\\nchangement (6 min)\\n9h09 Bern (quai E-H) → Bulle (arr. 10h11 voie\r\n" +
                "  2)\\nchangement (3 min)\\n10h20 Bulle (voie 4) → Gruyères (arr. 10h28 voie \r\n" +
                " 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:a2353bf3-6527-4187-9ef9-e689f3bce792\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T074800\r\n" +
                "DTEND:20250318T105700\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:7h48 Ebmatingen, Looren → Stettbach, Bahnhof (arr. 8h13 quai D)\r\n" +
                " \\ntrajet à pied (3 min)\\n8h17 Stettbach (voie 1) → Zürich HB (arr. 8h25 vo\r\n" +
                " ie 41/42)\\nchangement (7 min)\\n8h32 Zürich HB (voie 32) → Fribourg/Freibur\r\n" +
                " g (arr. 9h56 voie 2)\\nchangement (4 min)\\n10h04 Fribourg/Freiburg (voie 2)\r\n" +
                "  → Bulle (arr. 10h41 voie 2)\\nchangement (3 min)\\n10h50 Bulle (voie 4) → G\r\n" +
                " ruyères (arr. 10h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:310691f0-f11f-40f3-8a2e-916f05a34efc\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T081800\r\n" +
                "DTEND:20250318T112800\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:8h18 Ebmatingen, Looren → Stettbach, Bahnhof (arr. 8h43 quai D)\r\n" +
                " \\ntrajet à pied (3 min)\\n8h47 Stettbach (voie 1) → Zürich HB (arr. 8h55 vo\r\n" +
                " ie 41/42)\\nchangement (7 min)\\n9h02 Zürich HB (voie 31) → Bern (arr. 9h58 \r\n" +
                " voie 7)\\nchangement (6 min)\\n10h09 Bern (quai E-H) → Bulle (arr. 11h11 voi\r\n" +
                " e 2)\\nchangement (3 min)\\n11h20 Bulle (voie 4) → Gruyères (arr. 11h28 voie\r\n" +
                "  1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:d9cff702-2ead-41a1-bd65-972652516461\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T084800\r\n" +
                "DTEND:20250318T115700\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:8h48 Ebmatingen, Looren → Stettbach, Bahnhof (arr. 9h10 quai D)\r\n" +
                " \\ntrajet à pied (3 min)\\n9h17 Stettbach (voie 1) → Zürich HB (arr. 9h25 vo\r\n" +
                " ie 41/42)\\nchangement (7 min)\\n9h32 Zürich HB (voie 32) → Fribourg/Freibur\r\n" +
                " g (arr. 10h56 voie 2)\\nchangement (4 min)\\n11h04 Fribourg/Freiburg (voie 2\r\n" +
                " ) → Bulle (arr. 11h41 voie 2)\\nchangement (3 min)\\n11h50 Bulle (voie 4) → \r\n" +
                " Gruyères (arr. 11h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:b015d855-fdf6-4600-bbfe-2f5bdab027a7\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T090500\r\n" +
                "DTEND:20250318T122800\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:9h05 Ebmatingen, Looren → Zollikon, Bahnhof (arr. 9h26)\\ntrajet\r\n" +
                "  à pied (3 min)\\n9h34 Zollikon (voie 3) → Zürich HB (arr. 9h44 voie 41/42)\r\n" +
                " \\nchangement (7 min)\\n10h02 Zürich HB (voie 31) → Bern (arr. 10h58 voie 7)\r\n" +
                " \\nchangement (6 min)\\n11h09 Bern (quai E-H) → Bulle (arr. 12h11 voie 2)\\nc\r\n" +
                " hangement (3 min)\\n12h20 Bulle (voie 4) → Gruyères (arr. 12h28 voie 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:3bfc610f-996a-47fd-83ac-0680956fb786\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T090900\r\n" +
                "DTEND:20250318T122800\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:9h09 Ebmatingen, Looren → Maur, See (arr. 9h16)\\nchangement (1 \r\n" +
                " min)\\n9h25 Maur, See → Stettbach, Bahnhof (arr. 9h43 quai D)\\ntrajet à pie\r\n" +
                " d (3 min)\\n9h47 Stettbach (voie 1) → Zürich HB (arr. 9h55 voie 41/42)\\ncha\r\n" +
                " ngement (7 min)\\n10h02 Zürich HB (voie 31) → Bern (arr. 10h58 voie 7)\\ncha\r\n" +
                " ngement (6 min)\\n11h09 Bern (quai E-H) → Bulle (arr. 12h11 voie 2)\\nchange\r\n" +
                " ment (3 min)\\n12h20 Bulle (voie 4) → Gruyères (arr. 12h28 voie 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:7380854e-6d02-42f9-8ff7-62a66d7efa44\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T093500\r\n" +
                "DTEND:20250318T125100\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:9h35 Ebmatingen, Looren → Zollikon, Bahnhof (arr. 9h56)\\ntrajet\r\n" +
                "  à pied (3 min)\\n10h04 Zollikon (voie 3) → Zürich HB (arr. 10h14 voie 41/4\r\n" +
                " 2)\\nchangement (7 min)\\n10h32 Zürich HB (voie 32) → Fribourg/Freiburg (arr\r\n" +
                " . 11h56 voie 2)\\nchangement (4 min)\\n12h04 Fribourg/Freiburg (voie 2) → Bu\r\n" +
                " lle (arr. 12h41 voie 2)\\nchangement (3 min)\\n12h44 Bulle (voie 4) → Gruyèr\r\n" +
                " es (arr. 12h51 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:70e00934-eb4e-4f52-b2f9-89719e4c931d\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T100500\r\n" +
                "DTEND:20250318T132800\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:10h05 Ebmatingen, Looren → Zollikon, Bahnhof (arr. 10h26)\\ntraj\r\n" +
                " et à pied (3 min)\\n10h34 Zollikon (voie 3) → Zürich HB (arr. 10h44 voie 41\r\n" +
                " /42)\\nchangement (7 min)\\n11h02 Zürich HB (voie 31) → Bern (arr. 11h58 voi\r\n" +
                " e 8)\\nchangement (6 min)\\n12h09 Bern (quai E-H) → Bulle (arr. 13h11 voie 2\r\n" +
                " )\\nchangement (3 min)\\n13h20 Bulle (voie 4) → Gruyères (arr. 13h28 voie 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:126bd3d1-f965-4149-98c1-ff84303dd972\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T100900\r\n" +
                "DTEND:20250318T132800\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:10h09 Ebmatingen, Looren → Maur, See (arr. 10h16)\\nchangement (\r\n" +
                " 1 min)\\n10h25 Maur, See → Stettbach, Bahnhof (arr. 10h43 quai D)\\ntrajet à\r\n" +
                "  pied (3 min)\\n10h47 Stettbach (voie 1) → Zürich HB (arr. 10h55 voie 41/42\r\n" +
                " )\\nchangement (7 min)\\n11h02 Zürich HB (voie 31) → Bern (arr. 11h58 voie 8\r\n" +
                " )\\nchangement (6 min)\\n12h09 Bern (quai E-H) → Bulle (arr. 13h11 voie 2)\\n\r\n" +
                " changement (3 min)\\n13h20 Bulle (voie 4) → Gruyères (arr. 13h28 voie 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:dc5d588c-aa08-4da4-af6c-a9dd971751f6\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T103500\r\n" +
                "DTEND:20250318T135700\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:10h35 Ebmatingen, Looren → Zollikon, Bahnhof (arr. 10h56)\\ntraj\r\n" +
                " et à pied (3 min)\\n11h04 Zollikon (voie 3) → Zürich HB (arr. 11h14 voie 41\r\n" +
                " /42)\\nchangement (7 min)\\n11h32 Zürich HB (voie 32) → Fribourg/Freiburg (a\r\n" +
                " rr. 12h56 voie 2)\\nchangement (4 min)\\n13h04 Fribourg/Freiburg (voie 2) → \r\n" +
                " Bulle (arr. 13h41 voie 2)\\nchangement (3 min)\\n13h50 Bulle (voie 4) → Gruy\r\n" +
                " ères (arr. 13h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:dce615a6-3893-4344-82e0-2e100687f3e2\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T110500\r\n" +
                "DTEND:20250318T142800\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:11h05 Ebmatingen, Looren → Zollikon, Bahnhof (arr. 11h26)\\ntraj\r\n" +
                " et à pied (3 min)\\n11h34 Zollikon (voie 3) → Zürich HB (arr. 11h44 voie 41\r\n" +
                " /42)\\nchangement (7 min)\\n12h02 Zürich HB (voie 31) → Bern (arr. 12h58 voi\r\n" +
                " e 7)\\nchangement (6 min)\\n13h09 Bern (quai E-H) → Bulle (arr. 14h11 voie 2\r\n" +
                " )\\nchangement (3 min)\\n14h20 Bulle (voie 4) → Gruyères (arr. 14h28 voie 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:d7397396-6185-4afd-bcae-f162c613b0eb\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T110900\r\n" +
                "DTEND:20250318T142800\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:11h09 Ebmatingen, Looren → Maur, See (arr. 11h16)\\nchangement (\r\n" +
                " 1 min)\\n11h25 Maur, See → Stettbach, Bahnhof (arr. 11h43 quai D)\\ntrajet à\r\n" +
                "  pied (3 min)\\n11h47 Stettbach (voie 1) → Zürich HB (arr. 11h55 voie 41/42\r\n" +
                " )\\nchangement (7 min)\\n12h02 Zürich HB (voie 31) → Bern (arr. 12h58 voie 7\r\n" +
                " )\\nchangement (6 min)\\n13h09 Bern (quai E-H) → Bulle (arr. 14h11 voie 2)\\n\r\n" +
                " changement (3 min)\\n14h20 Bulle (voie 4) → Gruyères (arr. 14h28 voie 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:7b744862-d066-4ed6-843a-0c565d4c9ab5\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T113500\r\n" +
                "DTEND:20250318T145700\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:11h35 Ebmatingen, Looren → Zollikon, Bahnhof (arr. 11h56)\\ntraj\r\n" +
                " et à pied (3 min)\\n12h04 Zollikon (voie 3) → Zürich HB (arr. 12h14 voie 41\r\n" +
                " /42)\\nchangement (7 min)\\n12h32 Zürich HB (voie 32) → Fribourg/Freiburg (a\r\n" +
                " rr. 13h56 voie 2)\\nchangement (4 min)\\n14h04 Fribourg/Freiburg (voie 2) → \r\n" +
                " Bulle (arr. 14h41 voie 2)\\nchangement (3 min)\\n14h50 Bulle (voie 4) → Gruy\r\n" +
                " ères (arr. 14h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:841f9891-ec5c-4c7f-8951-285fdb55bcd0\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T121900\r\n" +
                "DTEND:20250318T152800\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:12h19 Ebmatingen, Looren → Stettbach, Bahnhof (arr. 12h41 quai \r\n" +
                " D)\\ntrajet à pied (3 min)\\n12h47 Stettbach (voie 1) → Zürich HB (arr. 12h5\r\n" +
                " 5 voie 41/42)\\nchangement (7 min)\\n13h02 Zürich HB (voie 31) → Bern (arr. \r\n" +
                " 13h58 voie 8)\\nchangement (6 min)\\n14h09 Bern (quai E-H) → Bulle (arr. 15h\r\n" +
                " 11 voie 2)\\nchangement (3 min)\\n15h20 Bulle (voie 4) → Gruyères (arr. 15h2\r\n" +
                " 8 voie 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:134c882d-860b-4f42-9d31-ce8eca93b184\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T124900\r\n" +
                "DTEND:20250318T155700\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:12h49 Ebmatingen, Looren → Stettbach, Bahnhof (arr. 13h11 quai \r\n" +
                " D)\\ntrajet à pied (3 min)\\n13h17 Stettbach (voie 1) → Zürich HB (arr. 13h2\r\n" +
                " 5 voie 41/42)\\nchangement (7 min)\\n13h32 Zürich HB (voie 32) → Fribourg/Fr\r\n" +
                " eiburg (arr. 14h56 voie 2)\\nchangement (4 min)\\n15h04 Fribourg/Freiburg (v\r\n" +
                " oie 2) → Bulle (arr. 15h41 voie 2)\\nchangement (3 min)\\n15h50 Bulle (voie \r\n" +
                " 4) → Gruyères (arr. 15h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:8150470c-625e-42e0-bf65-2a2eb87e3f16\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T131900\r\n" +
                "DTEND:20250318T162800\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:13h19 Ebmatingen, Looren → Stettbach, Bahnhof (arr. 13h41 quai \r\n" +
                " D)\\ntrajet à pied (3 min)\\n13h47 Stettbach (voie 1) → Zürich HB (arr. 13h5\r\n" +
                " 5 voie 41/42)\\nchangement (7 min)\\n14h02 Zürich HB (voie 31) → Bern (arr. \r\n" +
                " 14h58 voie 7)\\nchangement (6 min)\\n15h09 Bern (quai E-H) → Bulle (arr. 16h\r\n" +
                " 11 voie 2)\\nchangement (3 min)\\n16h20 Bulle (voie 4) → Gruyères (arr. 16h2\r\n" +
                " 8 voie 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:9322a6f3-e3b6-412e-af22-2848d659b01b\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T133500\r\n" +
                "DTEND:20250318T165100\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:13h35 Ebmatingen, Looren → Zollikon, Bahnhof (arr. 13h56)\\ntraj\r\n" +
                " et à pied (3 min)\\n14h04 Zollikon (voie 3) → Zürich HB (arr. 14h14 voie 41\r\n" +
                " /42)\\nchangement (7 min)\\n14h32 Zürich HB (voie 32) → Fribourg/Freiburg (a\r\n" +
                " rr. 15h56 voie 2)\\nchangement (4 min)\\n16h04 Fribourg/Freiburg (voie 2) → \r\n" +
                " Bulle (arr. 16h41 voie 2)\\nchangement (3 min)\\n16h44 Bulle (voie 4) → Gruy\r\n" +
                " ères (arr. 16h51 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:78f3e2c5-3293-49fa-bc9f-8375b2a3205e\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T133900\r\n" +
                "DTEND:20250318T165100\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:13h39 Ebmatingen, Looren → Maur, See (arr. 13h46)\\nchangement (\r\n" +
                " 1 min)\\n13h55 Maur, See → Stettbach, Bahnhof (arr. 14h13 quai D)\\ntrajet à\r\n" +
                "  pied (3 min)\\n14h17 Stettbach (voie 1) → Zürich HB (arr. 14h25 voie 41/42\r\n" +
                " )\\nchangement (7 min)\\n14h32 Zürich HB (voie 32) → Fribourg/Freiburg (arr.\r\n" +
                "  15h56 voie 2)\\nchangement (4 min)\\n16h04 Fribourg/Freiburg (voie 2) → Bul\r\n" +
                " le (arr. 16h41 voie 2)\\nchangement (3 min)\\n16h44 Bulle (voie 4) → Gruyère\r\n" +
                " s (arr. 16h51 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:55a91961-19bb-487f-b20c-2f322e87234e\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T140500\r\n" +
                "DTEND:20250318T172800\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:14h05 Ebmatingen, Looren → Zollikon, Bahnhof (arr. 14h26)\\ntraj\r\n" +
                " et à pied (3 min)\\n14h34 Zollikon (voie 3) → Zürich HB (arr. 14h44 voie 41\r\n" +
                " /42)\\nchangement (7 min)\\n15h02 Zürich HB (voie 31) → Bern (arr. 15h58 voi\r\n" +
                " e 8)\\nchangement (6 min)\\n16h09 Bern (quai E-H) → Bulle (arr. 17h11 voie 2\r\n" +
                " )\\nchangement (3 min)\\n17h20 Bulle (voie 4) → Gruyères (arr. 17h28 voie 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:38a66802-1a33-4a99-ae36-df667bfc44ef\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T140900\r\n" +
                "DTEND:20250318T172800\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:14h09 Ebmatingen, Looren → Maur, See (arr. 14h16)\\nchangement (\r\n" +
                " 1 min)\\n14h25 Maur, See → Stettbach, Bahnhof (arr. 14h43 quai D)\\ntrajet à\r\n" +
                "  pied (3 min)\\n14h47 Stettbach (voie 1) → Zürich HB (arr. 14h55 voie 41/42\r\n" +
                " )\\nchangement (7 min)\\n15h02 Zürich HB (voie 31) → Bern (arr. 15h58 voie 8\r\n" +
                " )\\nchangement (6 min)\\n16h09 Bern (quai E-H) → Bulle (arr. 17h11 voie 2)\\n\r\n" +
                " changement (3 min)\\n17h20 Bulle (voie 4) → Gruyères (arr. 17h28 voie 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:ec5b39a0-a383-4cda-9ad6-f757cdbf5f37\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T143500\r\n" +
                "DTEND:20250318T175700\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:14h35 Ebmatingen, Looren → Zollikon, Bahnhof (arr. 14h56)\\ntraj\r\n" +
                " et à pied (3 min)\\n15h04 Zollikon (voie 3) → Zürich HB (arr. 15h14 voie 41\r\n" +
                " /42)\\nchangement (7 min)\\n15h32 Zürich HB (voie 32) → Fribourg/Freiburg (a\r\n" +
                " rr. 16h56 voie 2)\\nchangement (4 min)\\n17h04 Fribourg/Freiburg (voie 2) → \r\n" +
                " Bulle (arr. 17h41 voie 2)\\nchangement (3 min)\\n17h50 Bulle (voie 4) → Gruy\r\n" +
                " ères (arr. 17h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:656f6435-af74-4d88-b525-f8304059e376\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T143900\r\n" +
                "DTEND:20250318T175700\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:14h39 Ebmatingen, Looren → Maur, See (arr. 14h46)\\nchangement (\r\n" +
                " 1 min)\\n14h55 Maur, See → Stettbach, Bahnhof (arr. 15h13 quai D)\\ntrajet à\r\n" +
                "  pied (3 min)\\n15h17 Stettbach (voie 1) → Zürich HB (arr. 15h25 voie 41/42\r\n" +
                " )\\nchangement (7 min)\\n15h32 Zürich HB (voie 32) → Fribourg/Freiburg (arr.\r\n" +
                "  16h56 voie 2)\\nchangement (4 min)\\n17h04 Fribourg/Freiburg (voie 2) → Bul\r\n" +
                " le (arr. 17h41 voie 2)\\nchangement (3 min)\\n17h50 Bulle (voie 4) → Gruyère\r\n" +
                " s (arr. 17h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:85b4ddaa-1047-4243-8c4c-048d96da8175\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T150500\r\n" +
                "DTEND:20250318T182100\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:15h05 Ebmatingen, Looren → Zollikon, Bahnhof (arr. 15h26)\\ntraj\r\n" +
                " et à pied (3 min)\\n15h34 Zollikon (voie 3) → Zürich HB (arr. 15h44 voie 41\r\n" +
                " /42)\\nchangement (7 min)\\n16h02 Zürich HB (voie 31) → Bern (arr. 16h58 voi\r\n" +
                " e 7)\\nchangement (6 min)\\n17h09 Bern (quai E-H) → Bulle (arr. 18h11 voie 2\r\n" +
                " )\\nchangement (3 min)\\n18h14 Bulle (voie 7) → Gruyères (arr. 18h21 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:7fe931be-b3b2-4522-b3d4-b0e7be9950cb\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T150900\r\n" +
                "DTEND:20250318T182100\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:15h09 Ebmatingen, Looren → Maur, See (arr. 15h16)\\nchangement (\r\n" +
                " 1 min)\\n15h25 Maur, See → Stettbach, Bahnhof (arr. 15h43 quai D)\\ntrajet à\r\n" +
                "  pied (3 min)\\n15h47 Stettbach (voie 1) → Zürich HB (arr. 15h55 voie 41/42\r\n" +
                " )\\nchangement (7 min)\\n16h02 Zürich HB (voie 31) → Bern (arr. 16h58 voie 7\r\n" +
                " )\\nchangement (6 min)\\n17h09 Bern (quai E-H) → Bulle (arr. 18h11 voie 2)\\n\r\n" +
                " changement (3 min)\\n18h14 Bulle (voie 7) → Gruyères (arr. 18h21 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:7072666a-5ade-4059-9b80-babc6e2d7b69\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T153500\r\n" +
                "DTEND:20250318T185100\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:15h35 Ebmatingen, Looren → Zollikon, Bahnhof (arr. 15h56)\\ntraj\r\n" +
                " et à pied (3 min)\\n16h04 Zollikon (voie 3) → Zürich HB (arr. 16h14 voie 41\r\n" +
                " /42)\\nchangement (7 min)\\n16h32 Zürich HB (voie 32) → Fribourg/Freiburg (a\r\n" +
                " rr. 17h56 voie 2)\\nchangement (4 min)\\n18h04 Fribourg/Freiburg (voie 2) → \r\n" +
                " Bulle (arr. 18h41 voie 2)\\nchangement (3 min)\\n18h44 Bulle (voie 4) → Gruy\r\n" +
                " ères (arr. 18h51 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:5ca0c329-5c49-4f8a-a12d-1e962834b737\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T153900\r\n" +
                "DTEND:20250318T185100\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:15h39 Ebmatingen, Looren → Maur, See (arr. 15h46)\\nchangement (\r\n" +
                " 1 min)\\n15h55 Maur, See → Stettbach, Bahnhof (arr. 16h13 quai D)\\ntrajet à\r\n" +
                "  pied (3 min)\\n16h17 Stettbach (voie 1) → Zürich HB (arr. 16h25 voie 41/42\r\n" +
                " )\\nchangement (7 min)\\n16h32 Zürich HB (voie 32) → Fribourg/Freiburg (arr.\r\n" +
                "  17h56 voie 2)\\nchangement (4 min)\\n18h04 Fribourg/Freiburg (voie 2) → Bul\r\n" +
                " le (arr. 18h41 voie 2)\\nchangement (3 min)\\n18h44 Bulle (voie 4) → Gruyère\r\n" +
                " s (arr. 18h51 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:512140cc-68d4-456e-a52c-e944de707fd9\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T161600\r\n" +
                "DTEND:20250318T192800\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:16h16 Ebmatingen, Looren → Zürich, Klusplatz (arr. 16h35 quai C\r\n" +
                " )\\nchangement (2 min)\\n16h40 Zürich, Klusplatz (quai A) → Zürich, Bahnhofp\r\n" +
                " latz/HB (arr. 16h51)\\ntrajet à pied (7 min)\\n17h02 Zürich HB (voie 31) → B\r\n" +
                " ern (arr. 17h58 voie 8)\\nchangement (6 min)\\n18h09 Bern (quai E-H) → Bulle\r\n" +
                "  (arr. 19h11 voie 2)\\nchangement (3 min)\\n19h20 Bulle (voie 4) → Gruyères \r\n" +
                " (arr. 19h28 voie 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:7e2f340b-6aee-4c55-bcb3-c0e28baf05a6\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T162000\r\n" +
                "DTEND:20250318T192800\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:16h20 Ebmatingen, Looren → Fällanden, Eggler (arr. 16h30)\\nchan\r\n" +
                " gement (1 min)\\n16h31 Fällanden, Eggler → Fällanden, Gemeindehaus (arr. 16\r\n" +
                " h32)\\nchangement (1 min)\\n16h33 Fällanden, Gemeindehaus → Stettbach, Bahnh\r\n" +
                " of (arr. 16h44 quai C)\\ntrajet à pied (3 min)\\n16h47 Stettbach (voie 1) → \r\n" +
                " Zürich HB (arr. 16h55 voie 41/42)\\nchangement (7 min)\\n17h02 Zürich HB (vo\r\n" +
                " ie 31) → Bern (arr. 17h58 voie 8)\\nchangement (6 min)\\n18h09 Bern (quai E-\r\n" +
                " H) → Bulle (arr. 19h11 voie 2)\\nchangement (3 min)\\n19h20 Bulle (voie 4) →\r\n" +
                "  Gruyères (arr. 19h28 voie 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:9ad67631-b434-4dc8-a8c8-87cbf0222cab\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T164600\r\n" +
                "DTEND:20250318T195700\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:16h46 Ebmatingen, Looren → Zürich, Klusplatz (arr. 17h05 quai C\r\n" +
                " )\\nchangement (2 min)\\n17h10 Zürich, Klusplatz (quai A) → Zürich, Bahnhofp\r\n" +
                " latz/HB (arr. 17h21)\\ntrajet à pied (7 min)\\n17h32 Zürich HB (voie 32) → F\r\n" +
                " ribourg/Freiburg (arr. 18h56 voie 2)\\nchangement (4 min)\\n19h04 Fribourg/F\r\n" +
                " reiburg (voie 2) → Bulle (arr. 19h41 voie 2)\\nchangement (3 min)\\n19h50 Bu\r\n" +
                " lle (voie 4) → Gruyères (arr. 19h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:5ef2807a-73fb-4fc6-94d6-dc39e6ed15fc\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T165000\r\n" +
                "DTEND:20250318T195700\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:16h50 Ebmatingen, Looren → Fällanden, Eggler (arr. 17h00)\\nchan\r\n" +
                " gement (1 min)\\n17h01 Fällanden, Eggler → Fällanden, Gemeindehaus (arr. 17\r\n" +
                " h02)\\nchangement (1 min)\\n17h03 Fällanden, Gemeindehaus → Stettbach, Bahnh\r\n" +
                " of (arr. 17h14 quai C)\\ntrajet à pied (3 min)\\n17h17 Stettbach (voie 1) → \r\n" +
                " Zürich HB (arr. 17h25 voie 41/42)\\nchangement (7 min)\\n17h32 Zürich HB (vo\r\n" +
                " ie 32) → Fribourg/Freiburg (arr. 18h56 voie 2)\\nchangement (4 min)\\n19h04 \r\n" +
                " Fribourg/Freiburg (voie 2) → Bulle (arr. 19h41 voie 2)\\nchangement (3 min)\r\n" +
                " \\n19h50 Bulle (voie 4) → Gruyères (arr. 19h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:1c203ff0-e6bf-4658-8f6d-b67e56e94392\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T171600\r\n" +
                "DTEND:20250318T202800\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:17h16 Ebmatingen, Looren → Zürich, Klusplatz (arr. 17h35 quai C\r\n" +
                " )\\nchangement (2 min)\\n17h40 Zürich, Klusplatz (quai A) → Zürich, Bahnhofp\r\n" +
                " latz/HB (arr. 17h51)\\ntrajet à pied (7 min)\\n18h02 Zürich HB (voie 31) → B\r\n" +
                " ern (arr. 18h58 voie 7)\\nchangement (6 min)\\n19h09 Bern (quai E-H) → Bulle\r\n" +
                "  (arr. 20h11 voie 2)\\nchangement (3 min)\\n20h20 Bulle (voie 4) → Gruyères \r\n" +
                " (arr. 20h28 voie 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:5c6f4bf9-8751-4524-b616-1ec7a34afb26\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T172000\r\n" +
                "DTEND:20250318T202800\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:17h20 Ebmatingen, Looren → Fällanden, Eggler (arr. 17h30)\\nchan\r\n" +
                " gement (1 min)\\n17h31 Fällanden, Eggler → Fällanden, Gemeindehaus (arr. 17\r\n" +
                " h32)\\nchangement (1 min)\\n17h33 Fällanden, Gemeindehaus → Stettbach, Bahnh\r\n" +
                " of (arr. 17h44 quai C)\\ntrajet à pied (3 min)\\n17h47 Stettbach (voie 1) → \r\n" +
                " Zürich HB (arr. 17h55 voie 41/42)\\nchangement (7 min)\\n18h02 Zürich HB (vo\r\n" +
                " ie 31) → Bern (arr. 18h58 voie 7)\\nchangement (6 min)\\n19h09 Bern (quai E-\r\n" +
                " H) → Bulle (arr. 20h11 voie 2)\\nchangement (3 min)\\n20h20 Bulle (voie 4) →\r\n" +
                "  Gruyères (arr. 20h28 voie 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:2293de26-9987-455e-bf9a-4e05631bbe1d\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T174600\r\n" +
                "DTEND:20250318T205700\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:17h46 Ebmatingen, Looren → Zürich, Klusplatz (arr. 18h05 quai C\r\n" +
                " )\\nchangement (2 min)\\n18h10 Zürich, Klusplatz (quai A) → Zürich, Bahnhofp\r\n" +
                " latz/HB (arr. 18h21)\\ntrajet à pied (7 min)\\n18h32 Zürich HB (voie 32) → F\r\n" +
                " ribourg/Freiburg (arr. 19h56 voie 2)\\nchangement (4 min)\\n20h04 Fribourg/F\r\n" +
                " reiburg (voie 2) → Bulle (arr. 20h41 voie 2)\\nchangement (3 min)\\n20h50 Bu\r\n" +
                " lle (voie 4) → Gruyères (arr. 20h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:d7c9287d-c14d-4eba-b185-983fa61ffba8\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T175000\r\n" +
                "DTEND:20250318T205700\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:17h50 Ebmatingen, Looren → Fällanden, Eggler (arr. 18h00)\\nchan\r\n" +
                " gement (1 min)\\n18h01 Fällanden, Eggler → Fällanden, Gemeindehaus (arr. 18\r\n" +
                " h02)\\nchangement (1 min)\\n18h03 Fällanden, Gemeindehaus → Stettbach, Bahnh\r\n" +
                " of (arr. 18h14 quai C)\\ntrajet à pied (3 min)\\n18h17 Stettbach (voie 1) → \r\n" +
                " Zürich HB (arr. 18h25 voie 41/42)\\nchangement (7 min)\\n18h32 Zürich HB (vo\r\n" +
                " ie 32) → Fribourg/Freiburg (arr. 19h56 voie 2)\\nchangement (4 min)\\n20h04 \r\n" +
                " Fribourg/Freiburg (voie 2) → Bulle (arr. 20h41 voie 2)\\nchangement (3 min)\r\n" +
                " \\n20h50 Bulle (voie 4) → Gruyères (arr. 20h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:01021144-656a-429f-aaf2-77544cf67b10\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T181600\r\n" +
                "DTEND:20250318T212800\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:18h16 Ebmatingen, Looren → Zürich, Klusplatz (arr. 18h35 quai C\r\n" +
                " )\\nchangement (2 min)\\n18h41 Zürich, Klusplatz (quai A) → Zürich, Bahnhofp\r\n" +
                " latz/HB (arr. 18h52)\\ntrajet à pied (7 min)\\n19h02 Zürich HB (voie 31) → B\r\n" +
                " ern (arr. 19h58 voie 8)\\nchangement (6 min)\\n20h09 Bern (quai E-H) → Bulle\r\n" +
                "  (arr. 21h11 voie 2)\\nchangement (3 min)\\n21h20 Bulle (voie 4) → Gruyères \r\n" +
                " (arr. 21h28 voie 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:4205ae4a-96bd-4dcb-b125-b69a27ed4c47\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T182000\r\n" +
                "DTEND:20250318T212800\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:18h20 Ebmatingen, Looren → Fällanden, Eggler (arr. 18h30)\\nchan\r\n" +
                " gement (1 min)\\n18h31 Fällanden, Eggler → Fällanden, Gemeindehaus (arr. 18\r\n" +
                " h32)\\nchangement (1 min)\\n18h33 Fällanden, Gemeindehaus → Stettbach, Bahnh\r\n" +
                " of (arr. 18h44 quai C)\\ntrajet à pied (3 min)\\n18h47 Stettbach (voie 1) → \r\n" +
                " Zürich HB (arr. 18h55 voie 41/42)\\nchangement (7 min)\\n19h02 Zürich HB (vo\r\n" +
                " ie 31) → Bern (arr. 19h58 voie 8)\\nchangement (6 min)\\n20h09 Bern (quai E-\r\n" +
                " H) → Bulle (arr. 21h11 voie 2)\\nchangement (3 min)\\n21h20 Bulle (voie 4) →\r\n" +
                "  Gruyères (arr. 21h28 voie 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:cf7d77da-4a3c-40b0-8edc-40e319826e30\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T183500\r\n" +
                "DTEND:20250318T215700\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:18h35 Ebmatingen, Looren → Zollikon, Bahnhof (arr. 18h58)\\ntraj\r\n" +
                " et à pied (3 min)\\n19h04 Zollikon (voie 3) → Zürich HB (arr. 19h14 voie 41\r\n" +
                " /42)\\nchangement (7 min)\\n19h32 Zürich HB (voie 32) → Fribourg/Freiburg (a\r\n" +
                " rr. 20h56 voie 2)\\nchangement (4 min)\\n21h04 Fribourg/Freiburg (voie 1) → \r\n" +
                " Bulle (arr. 21h41 voie 2)\\nchangement (3 min)\\n21h50 Bulle (voie 4) → Gruy\r\n" +
                " ères (arr. 21h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:49773fde-96b7-47a6-9426-72630d166ba3\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T184000\r\n" +
                "DTEND:20250318T215700\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:18h40 Ebmatingen, Looren → Maur, See (arr. 18h47)\\nchangement (\r\n" +
                " 1 min)\\n18h55 Maur, See → Stettbach, Bahnhof (arr. 19h14 quai C)\\ntrajet à\r\n" +
                "  pied (3 min)\\n19h17 Stettbach (voie 1) → Zürich HB (arr. 19h25 voie 41/42\r\n" +
                " )\\nchangement (7 min)\\n19h32 Zürich HB (voie 32) → Fribourg/Freiburg (arr.\r\n" +
                "  20h56 voie 2)\\nchangement (4 min)\\n21h04 Fribourg/Freiburg (voie 1) → Bul\r\n" +
                " le (arr. 21h41 voie 2)\\nchangement (3 min)\\n21h50 Bulle (voie 4) → Gruyère\r\n" +
                " s (arr. 21h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:d52ed88a-3f45-4169-a7ff-d4a8e9b489d0\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T185000\r\n" +
                "DTEND:20250318T215700\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:18h50 Ebmatingen, Looren → Fällanden, Eggler (arr. 19h00)\\nchan\r\n" +
                " gement (1 min)\\n19h01 Fällanden, Eggler → Fällanden, Gemeindehaus (arr. 19\r\n" +
                " h02)\\nchangement (1 min)\\n19h03 Fällanden, Gemeindehaus → Stettbach, Bahnh\r\n" +
                " of (arr. 19h14 quai C)\\ntrajet à pied (3 min)\\n19h17 Stettbach (voie 1) → \r\n" +
                " Zürich HB (arr. 19h25 voie 41/42)\\nchangement (7 min)\\n19h32 Zürich HB (vo\r\n" +
                " ie 32) → Fribourg/Freiburg (arr. 20h56 voie 2)\\nchangement (4 min)\\n21h04 \r\n" +
                " Fribourg/Freiburg (voie 1) → Bulle (arr. 21h41 voie 2)\\nchangement (3 min)\r\n" +
                " \\n21h50 Bulle (voie 4) → Gruyères (arr. 21h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:77a2e53a-c99f-41ec-a6e6-8f9f7fa11ea2\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T192000\r\n" +
                "DTEND:20250318T222800\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:19h20 Ebmatingen, Looren → Stettbach, Bahnhof (arr. 19h41 quai \r\n" +
                " D)\\ntrajet à pied (3 min)\\n19h47 Stettbach (voie 1) → Zürich HB (arr. 19h5\r\n" +
                " 5 voie 41/42)\\nchangement (7 min)\\n20h02 Zürich HB (voie 31) → Bern (arr. \r\n" +
                " 20h58 voie 8)\\nchangement (6 min)\\n21h04 Bern (voie 3) → Romont FR (arr. 2\r\n" +
                " 1h45 voie 1)\\nchangement (3 min)\\n21h52 Romont FR (voie 1) → Bulle (arr. 2\r\n" +
                " 2h11 voie 2)\\nchangement (3 min)\\n22h20 Bulle (voie 4) → Gruyères (arr. 22\r\n" +
                " h28 voie 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:ba3bfb27-e892-4022-9712-f8ef32356c91\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T194900\r\n" +
                "DTEND:20250318T225700\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:19h49 Ebmatingen, Looren → Zürich, Klusplatz (arr. 20h06 quai C\r\n" +
                " )\\nchangement (2 min)\\n20h13 Zürich, Klusplatz (quai A) → Zürich, Bahnhofp\r\n" +
                " latz/HB (arr. 20h23)\\ntrajet à pied (7 min)\\n20h32 Zürich HB (voie 32) → F\r\n" +
                " ribourg/Freiburg (arr. 21h56 voie 2)\\nchangement (4 min)\\n22h04 Fribourg/F\r\n" +
                " reiburg (voie 1) → Bulle (arr. 22h41 voie 2)\\nchangement (3 min)\\n22h50 Bu\r\n" +
                " lle (voie 4) → Gruyères (arr. 22h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:11c516b3-2169-45a8-9bd8-b3e2f35bab53\r\n" +
                "DTSTAMP:20250327T134609\r\n" +
                "DTSTART:20250318T200600\r\n" +
                "DTEND:20250318T232800\r\n" +
                "SUMMARY:Ebmatingen, Looren → Gruyères\r\n" +
                "DESCRIPTION:20h06 Ebmatingen, Looren → Zollikon, Bahnhof (arr. 20h26)\\ntraj\r\n" +
                " et à pied (3 min)\\n20h34 Zollikon (voie 3) → Zürich HB (arr. 20h44 voie 41\r\n" +
                " /42)\\nchangement (7 min)\\n21h02 Zürich HB (voie 31) → Bern (arr. 21h58 voi\r\n" +
                " e 8)\\nchangement (6 min)\\n22h04 Bern (voie 3) → Romont FR (arr. 22h44 voie\r\n" +
                "  1)\\nchangement (3 min)\\n22h52 Romont FR (voie 1) → Bulle (arr. 23h11 voie\r\n" +
                "  2)\\nchangement (3 min)\\n23h20 Bulle (voie 4) → Gruyères (arr. 23h28 voie \r\n" +
                " 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n");
    }

    static String deleteRemove(String s) {
        String[] t = s.split(":");
        for (int i = 5; i < t.length; i+=12) {
            t[i] = null;
            t[i+1] = null;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < t.length; i++) {
            if (t[i] != null) {
                builder.append(t[i]);
            }
        }
        return builder.toString();
    }
}
