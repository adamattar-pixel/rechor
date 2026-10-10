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

public class MyProfileTest {
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
        List<Journey> js = JourneyExtractor.journeys(p, 7872);
        StringBuilder j = new StringBuilder();
        for (int i = 0; i < js.size(); i++) {
            System.out.println(JourneyIcalConverter.toIcalendar(js.get(i)));
            j.append(JourneyIcalConverter.toIcalendar(js.get(i)));
        }
        assertEquals(test2(), deleteRemove(j.toString()));
        String[] parts = JourneyIcalConverter.toIcalendar(js.get(32)).split("\n");
        String[] parts2 = (("BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:bbff399f-d276-4052-8798-9c2f227353de\r\n" +
                "DTSTAMP:20250325T125024\r\n" +
                "DTSTART:20250318T161300\r\n" +
                "DTEND:20250318T175700\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:16h13 Ecublens VD, EPFL → Renens VD, gare (arr. 16h19)\\ntrajet \r\n" +
                " à pied (3 min)\\n16h26 Renens VD (voie 4) → Lausanne (arr. 16h33 voie 5)\\nc\r\n" +
                " hangement (5 min)\\n16h40 Lausanne (voie 1) → Romont FR (arr. 17h13 voie 2)\r\n" +
                " \\nchangement (3 min)\\n17h22 Romont FR (voie 1) → Bulle (arr. 17h41 voie 2)\r\n" +
                " \\nchangement (3 min)\\n17h50 Bulle (voie 4) → Gruyères (arr. 17h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n").split("\n"));
        StringBuilder builder1 = new StringBuilder();
        StringBuilder builder2 = new StringBuilder();
        builder1.append(parts[0]).append("\n")
                .append(parts[1]).append("\n")
                .append(parts[2]).append("\n")
                .append(parts[3]).append("\n");
        builder2.append(parts[0]).append("\n")
                .append(parts[1]).append("\n")
                .append(parts[2]).append("\n")
                .append(parts[3]).append("\n");
        for (int i = 6; i < parts.length; i++) {
            builder1.append(parts[i]).append("\n");
            builder2.append(parts2[i]).append("\n");
        }
        assertEquals(builder2.toString(), builder1.toString());
    }

    public static String test2() throws IOException {
        return deleteRemove("BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:56ea0cb6-70a6-47fe-b971-704459193573\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T053300\r\n" +
                "DTEND:20250318T072800\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:5h33 Ecublens VD, EPFL → Renens VD, gare (arr. 5h39)\\ntrajet à \r\n" +
                " pied (3 min)\\n5h50 Renens VD (voie 5) → Palézieux (arr. 6h16 voie 3)\\nchan\r\n" +
                " gement (3 min)\\n6h19 Palézieux (voie 12) → Gruyères (arr. 7h28 voie 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:602cdef7-2078-43b0-ae23-6cfd58c50f90\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T055300\r\n" +
                "DTEND:20250318T075700\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:5h53 Ecublens VD, EPFL → Renens VD, gare (arr. 5h59)\\ntrajet à \r\n" +
                " pied (3 min)\\n6h08 Renens VD (voie 5) → Lausanne (arr. 6h14 voie 1)\\nchang\r\n" +
                " ement (5 min)\\n6h21 Lausanne (voie 70) → Palézieux (arr. 6h40 voie 2)\\ncha\r\n" +
                " ngement (3 min)\\n6h49 Palézieux (voie 12) → Gruyères (arr. 7h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:29c56e09-8e11-4fc4-aa1d-9308c02fc8f0\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T061300\r\n" +
                "DTEND:20250318T075700\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:6h13 Ecublens VD, EPFL → Renens VD, gare (arr. 6h19)\\ntrajet à \r\n" +
                " pied (3 min)\\n6h26 Renens VD (voie 4) → Lausanne (arr. 6h33 voie 5)\\nchang\r\n" +
                " ement (5 min)\\n6h40 Lausanne (voie 1) → Romont FR (arr. 7h13 voie 2)\\nchan\r\n" +
                " gement (3 min)\\n7h22 Romont FR (voie 1) → Bulle (arr. 7h41 voie 2)\\nchange\r\n" +
                " ment (3 min)\\n7h50 Bulle (voie 4) → Gruyères (arr. 7h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:80cd5478-c14c-436d-a291-42c0b5b31013\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T063300\r\n" +
                "DTEND:20250318T082800\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:6h33 Ecublens VD, EPFL → Renens VD, gare (arr. 6h39)\\ntrajet à \r\n" +
                " pied (3 min)\\n6h50 Renens VD (voie 5) → Palézieux (arr. 7h16 voie 3)\\nchan\r\n" +
                " gement (3 min)\\n7h19 Palézieux (voie 12) → Gruyères (arr. 8h28 voie 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:6303f004-30d3-48c9-be96-1260d48005be\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T065700\r\n" +
                "DTEND:20250318T085700\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:6h57 Ecublens VD, EPFL → Renens VD, gare (arr. 7h04)\\ntrajet à \r\n" +
                " pied (3 min)\\n7h08 Renens VD (voie 5) → Lausanne (arr. 7h14 voie 1)\\nchang\r\n" +
                " ement (5 min)\\n7h21 Lausanne (voie 70) → Palézieux (arr. 7h40 voie 2)\\ncha\r\n" +
                " ngement (3 min)\\n7h49 Palézieux (voie 11) → Gruyères (arr. 8h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:9768b07c-cabb-4702-ba39-d5f8cb2a6284\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T071300\r\n" +
                "DTEND:20250318T085700\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:7h13 Ecublens VD, EPFL → Renens VD, gare (arr. 7h19)\\ntrajet à \r\n" +
                " pied (3 min)\\n7h26 Renens VD (voie 4) → Lausanne (arr. 7h33 voie 5)\\nchang\r\n" +
                " ement (5 min)\\n7h40 Lausanne (voie 1) → Romont FR (arr. 8h13 voie 2)\\nchan\r\n" +
                " gement (3 min)\\n8h22 Romont FR (voie 1) → Bulle (arr. 8h41 voie 2)\\nchange\r\n" +
                " ment (3 min)\\n8h50 Bulle (voie 4) → Gruyères (arr. 8h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:aa2b4bed-8755-4331-b9eb-6bf617b4aed4\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T073800\r\n" +
                "DTEND:20250318T092800\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:7h38 Ecublens VD, EPFL → Renens VD, gare (arr. 7h44)\\ntrajet à \r\n" +
                " pied (3 min)\\n7h50 Renens VD (voie 5) → Palézieux (arr. 8h16 voie 3)\\nchan\r\n" +
                " gement (3 min)\\n8h19 Palézieux (voie 12) → Gruyères (arr. 9h28 voie 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:f7716244-36d2-40da-a801-3df9a45762c7\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T075800\r\n" +
                "DTEND:20250318T095700\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:7h58 Ecublens VD, EPFL → Renens VD, gare (arr. 8h04)\\ntrajet à \r\n" +
                " pied (3 min)\\n8h08 Renens VD (voie 5) → Lausanne (arr. 8h14 voie 1)\\nchang\r\n" +
                " ement (5 min)\\n8h21 Lausanne (voie 70) → Palézieux (arr. 8h40 voie 2)\\ncha\r\n" +
                " ngement (3 min)\\n8h49 Palézieux (voie 12) → Gruyères (arr. 9h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:b9dc35c4-64a3-4f16-ad5c-da8370fbff0a\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T081300\r\n" +
                "DTEND:20250318T095700\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:8h13 Ecublens VD, EPFL → Renens VD, gare (arr. 8h19)\\ntrajet à \r\n" +
                " pied (3 min)\\n8h26 Renens VD (voie 4) → Lausanne (arr. 8h33 voie 5)\\nchang\r\n" +
                " ement (5 min)\\n8h40 Lausanne (voie 1) → Romont FR (arr. 9h13 voie 2)\\nchan\r\n" +
                " gement (3 min)\\n9h22 Romont FR (voie 1) → Bulle (arr. 9h41 voie 2)\\nchange\r\n" +
                " ment (3 min)\\n9h50 Bulle (voie 4) → Gruyères (arr. 9h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:96b8ad8a-2d73-4eb5-ae98-c4fd0a754aa6\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T083800\r\n" +
                "DTEND:20250318T102800\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:8h38 Ecublens VD, EPFL → Renens VD, gare (arr. 8h44)\\ntrajet à \r\n" +
                " pied (3 min)\\n8h50 Renens VD (voie 5) → Palézieux (arr. 9h16 voie 3)\\nchan\r\n" +
                " gement (3 min)\\n9h19 Palézieux (voie 12) → Gruyères (arr. 10h28 voie 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:5b5f32c2-f35e-4266-837c-4d798a8a7ae0\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T085800\r\n" +
                "DTEND:20250318T105700\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:8h58 Ecublens VD, EPFL → Renens VD, gare (arr. 9h04)\\ntrajet à \r\n" +
                " pied (3 min)\\n9h08 Renens VD (voie 3) → Lausanne (arr. 9h14 voie 1)\\nchang\r\n" +
                " ement (5 min)\\n9h21 Lausanne (voie 70) → Palézieux (arr. 9h40 voie 2)\\ncha\r\n" +
                " ngement (3 min)\\n9h49 Palézieux (voie 12) → Gruyères (arr. 10h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:9f81d358-9db8-44f2-a73f-7f2b8044f9b2\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T091300\r\n" +
                "DTEND:20250318T105700\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:9h13 Ecublens VD, EPFL → Renens VD, gare (arr. 9h19)\\ntrajet à \r\n" +
                " pied (3 min)\\n9h26 Renens VD (voie 4) → Lausanne (arr. 9h33 voie 5)\\nchang\r\n" +
                " ement (5 min)\\n9h40 Lausanne (voie 1) → Romont FR (arr. 10h13 voie 2)\\ncha\r\n" +
                " ngement (3 min)\\n10h22 Romont FR (voie 1) → Bulle (arr. 10h41 voie 2)\\ncha\r\n" +
                " ngement (3 min)\\n10h50 Bulle (voie 4) → Gruyères (arr. 10h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:729cba03-92fa-491b-b793-aca8ffc1e17f\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T093800\r\n" +
                "DTEND:20250318T112800\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:9h38 Ecublens VD, EPFL → Renens VD, gare (arr. 9h44)\\ntrajet à \r\n" +
                " pied (3 min)\\n9h50 Renens VD (voie 5) → Palézieux (arr. 10h16 voie 3)\\ncha\r\n" +
                " ngement (3 min)\\n10h19 Palézieux (voie 12) → Gruyères (arr. 11h28 voie 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:281ccabd-0550-4cce-8b25-84ea437ae008\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T095800\r\n" +
                "DTEND:20250318T115700\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:9h58 Ecublens VD, EPFL → Renens VD, gare (arr. 10h04)\\ntrajet à\r\n" +
                "  pied (3 min)\\n10h08 Renens VD (voie 5) → Lausanne (arr. 10h14 voie 1)\\nch\r\n" +
                " angement (5 min)\\n10h21 Lausanne (voie 70) → Palézieux (arr. 10h40 voie 2)\r\n" +
                " \\nchangement (3 min)\\n10h49 Palézieux (voie 12) → Gruyères (arr. 11h57 voi\r\n" +
                " e 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:013bc814-edcf-4900-8940-cdf0ec422dbc\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T101300\r\n" +
                "DTEND:20250318T115700\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:10h13 Ecublens VD, EPFL → Renens VD, gare (arr. 10h19)\\ntrajet \r\n" +
                " à pied (3 min)\\n10h26 Renens VD (voie 4) → Lausanne (arr. 10h33 voie 5)\\nc\r\n" +
                " hangement (5 min)\\n10h40 Lausanne (voie 1) → Romont FR (arr. 11h13 voie 2)\r\n" +
                " \\nchangement (3 min)\\n11h22 Romont FR (voie 1) → Bulle (arr. 11h41 voie 2)\r\n" +
                " \\nchangement (3 min)\\n11h50 Bulle (voie 4) → Gruyères (arr. 11h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:72f1d377-9cb1-4c7e-81e8-477da8365050\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T103800\r\n" +
                "DTEND:20250318T122800\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:10h38 Ecublens VD, EPFL → Renens VD, gare (arr. 10h44)\\ntrajet \r\n" +
                " à pied (3 min)\\n10h50 Renens VD (voie 5) → Palézieux (arr. 11h16 voie 3)\\n\r\n" +
                " changement (3 min)\\n11h19 Palézieux (voie 12) → Gruyères (arr. 12h28 voie \r\n" +
                " 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:4b4797a6-e6dd-4289-a0d9-e943dc562e88\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T105600\r\n" +
                "DTEND:20250318T125100\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:10h56 Ecublens VD, EPFL → Renens VD, gare (arr. 11h02)\\ntrajet \r\n" +
                " à pied (3 min)\\n11h08 Renens VD (voie 3) → Lausanne (arr. 11h14 voie 1)\\nc\r\n" +
                " hangement (5 min)\\n11h21 Lausanne (voie 70) → Palézieux (arr. 11h40 voie 2\r\n" +
                " )\\nchangement (3 min)\\n11h49 Palézieux (voie 12) → Gruyères (arr. 12h51 vo\r\n" +
                " ie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:2c690572-96fb-4814-9027-32586e3f997a\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T111700\r\n" +
                "DTEND:20250318T125100\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:11h17 Ecublens VD, EPFL → Lausanne, Vigie (arr. 11h28)\\ntrajet \r\n" +
                " à pied (2 min)\\n11h30 Lausanne, Cécil → Lausanne, gare (arr. 11h35)\\ntraje\r\n" +
                " t à pied (5 min)\\n11h40 Lausanne (voie 1) → Romont FR (arr. 12h13 voie 2)\\\r\n" +
                " nchangement (3 min)\\n12h22 Romont FR (voie 1) → Bulle (arr. 12h41 voie 2)\\\r\n" +
                " nchangement (3 min)\\n12h44 Bulle (voie 4) → Gruyères (arr. 12h51 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:5c04f417-2b4f-4d15-b2bc-ad8f741d6241\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T114000\r\n" +
                "DTEND:20250318T132800\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:11h40 Ecublens VD, EPFL → Renens VD, gare (arr. 11h46)\\ntrajet \r\n" +
                " à pied (3 min)\\n11h50 Renens VD (voie 5) → Palézieux (arr. 12h16 voie 3)\\n\r\n" +
                " changement (3 min)\\n12h19 Palézieux (voie 12) → Gruyères (arr. 13h28 voie \r\n" +
                " 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:248e6662-2954-431d-b7a6-5df58b9b944e\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T115500\r\n" +
                "DTEND:20250318T135700\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:11h55 Ecublens VD, EPFL → Renens VD, gare (arr. 12h01)\\ntrajet \r\n" +
                " à pied (3 min)\\n12h08 Renens VD (voie 5) → Lausanne (arr. 12h14 voie 1)\\nc\r\n" +
                " hangement (5 min)\\n12h21 Lausanne (voie 70) → Palézieux (arr. 12h40 voie 2\r\n" +
                " )\\nchangement (3 min)\\n12h49 Palézieux (voie 12) → Gruyères (arr. 13h57 vo\r\n" +
                " ie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:96f35aab-dc80-40be-bdf8-d0b678b3cf53\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T121700\r\n" +
                "DTEND:20250318T135700\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:12h17 Ecublens VD, EPFL → Lausanne, Flon (arr. 12h30)\\nchangeme\r\n" +
                " nt (2 min)\\n12h33 Lausanne, Flon → Lausanne, gare (arr. 12h34)\\ntrajet à p\r\n" +
                " ied (5 min)\\n12h40 Lausanne (voie 1) → Romont FR (arr. 13h13 voie 2)\\nchan\r\n" +
                " gement (3 min)\\n13h22 Romont FR (voie 1) → Bulle (arr. 13h41 voie 2)\\nchan\r\n" +
                " gement (3 min)\\n13h50 Bulle (voie 4) → Gruyères (arr. 13h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:e0cef1dd-0d69-4b38-937f-096c6b8db444\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T124000\r\n" +
                "DTEND:20250318T142800\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:12h40 Ecublens VD, EPFL → Renens VD, gare (arr. 12h46)\\ntrajet \r\n" +
                " à pied (3 min)\\n12h50 Renens VD (voie 5) → Palézieux (arr. 13h16 voie 3)\\n\r\n" +
                " changement (3 min)\\n13h19 Palézieux (voie 12) → Gruyères (arr. 14h28 voie \r\n" +
                " 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:a438cc47-70d3-4c80-9565-9cfbc5ad7683\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T125500\r\n" +
                "DTEND:20250318T145700\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:12h55 Ecublens VD, EPFL → Renens VD, gare (arr. 13h01)\\ntrajet \r\n" +
                " à pied (3 min)\\n13h08 Renens VD (voie 5) → Lausanne (arr. 13h14 voie 1)\\nc\r\n" +
                " hangement (5 min)\\n13h21 Lausanne (voie 70) → Palézieux (arr. 13h40 voie 2\r\n" +
                " )\\nchangement (3 min)\\n13h49 Palézieux (voie 12) → Gruyères (arr. 14h57 vo\r\n" +
                " ie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:e5aadb74-7c66-447d-a65b-3415eb624fcb\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T131700\r\n" +
                "DTEND:20250318T145700\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:13h17 Ecublens VD, EPFL → Lausanne, Vigie (arr. 13h28)\\ntrajet \r\n" +
                " à pied (2 min)\\n13h30 Lausanne, Cécil → Lausanne, gare (arr. 13h35)\\ntraje\r\n" +
                " t à pied (5 min)\\n13h40 Lausanne (voie 1) → Romont FR (arr. 14h13 voie 2)\\\r\n" +
                " nchangement (3 min)\\n14h22 Romont FR (voie 1) → Bulle (arr. 14h41 voie 2)\\\r\n" +
                " nchangement (3 min)\\n14h50 Bulle (voie 4) → Gruyères (arr. 14h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:40ea4767-38f7-4358-a268-1eb174694a63\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T134000\r\n" +
                "DTEND:20250318T152800\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:13h40 Ecublens VD, EPFL → Renens VD, gare (arr. 13h46)\\ntrajet \r\n" +
                " à pied (3 min)\\n13h50 Renens VD (voie 5) → Palézieux (arr. 14h16 voie 3)\\n\r\n" +
                " changement (3 min)\\n14h19 Palézieux (voie 12) → Gruyères (arr. 15h28 voie \r\n" +
                " 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:24cfd4c5-df87-41bd-ae29-ee5ba8fe9479\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T135500\r\n" +
                "DTEND:20250318T155700\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:13h55 Ecublens VD, EPFL → Renens VD, gare (arr. 14h01)\\ntrajet \r\n" +
                " à pied (3 min)\\n14h08 Renens VD (voie 5) → Lausanne (arr. 14h14 voie 1)\\nc\r\n" +
                " hangement (5 min)\\n14h21 Lausanne (voie 70) → Palézieux (arr. 14h40 voie 2\r\n" +
                " )\\nchangement (3 min)\\n14h49 Palézieux (voie 12) → Gruyères (arr. 15h57 vo\r\n" +
                " ie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:3819fb03-f70b-403f-bfc8-ff73ddc54b31\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T141700\r\n" +
                "DTEND:20250318T155700\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:14h17 Ecublens VD, EPFL → Lausanne, Flon (arr. 14h30)\\nchangeme\r\n" +
                " nt (2 min)\\n14h34 Lausanne, Flon → Lausanne, gare (arr. 14h35)\\ntrajet à p\r\n" +
                " ied (5 min)\\n14h40 Lausanne (voie 1) → Romont FR (arr. 15h13 voie 2)\\nchan\r\n" +
                " gement (3 min)\\n15h22 Romont FR (voie 1) → Bulle (arr. 15h41 voie 2)\\nchan\r\n" +
                " gement (3 min)\\n15h50 Bulle (voie 4) → Gruyères (arr. 15h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:2cf6e3f9-6f35-4a57-963a-e55efff02dae\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T144000\r\n" +
                "DTEND:20250318T162800\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:14h40 Ecublens VD, EPFL → Renens VD, gare (arr. 14h46)\\ntrajet \r\n" +
                " à pied (3 min)\\n14h50 Renens VD (voie 5) → Palézieux (arr. 15h16 voie 3)\\n\r\n" +
                " changement (3 min)\\n15h19 Palézieux (voie 12) → Gruyères (arr. 16h28 voie \r\n" +
                " 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:de7cf689-4ed7-4e93-a4ab-ad6684668bc6\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T145500\r\n" +
                "DTEND:20250318T165100\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:14h55 Ecublens VD, EPFL → Renens VD, gare (arr. 15h01)\\ntrajet \r\n" +
                " à pied (3 min)\\n15h08 Renens VD (voie 3) → Lausanne (arr. 15h14 voie 1)\\nc\r\n" +
                " hangement (5 min)\\n15h21 Lausanne (voie 70) → Palézieux (arr. 15h40 voie 2\r\n" +
                " )\\nchangement (3 min)\\n15h49 Palézieux (voie 12) → Gruyères (arr. 16h51 vo\r\n" +
                " ie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:0b418175-b8f8-4585-8b27-13ccc5acd0a2\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T151700\r\n" +
                "DTEND:20250318T165100\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:15h17 Ecublens VD, EPFL → Lausanne, Flon (arr. 15h30)\\nchangeme\r\n" +
                " nt (2 min)\\n15h32 Lausanne, Flon → Lausanne, gare (arr. 15h34)\\ntrajet à p\r\n" +
                " ied (5 min)\\n15h40 Lausanne (voie 1) → Romont FR (arr. 16h13 voie 2)\\nchan\r\n" +
                " gement (3 min)\\n16h22 Romont FR (voie 1) → Bulle (arr. 16h41 voie 2)\\nchan\r\n" +
                " gement (3 min)\\n16h44 Bulle (voie 4) → Gruyères (arr. 16h51 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:cf5319bc-c538-461b-b4da-ff089a089d76\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T154000\r\n" +
                "DTEND:20250318T172800\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:15h40 Ecublens VD, EPFL → Renens VD, gare (arr. 15h46)\\ntrajet \r\n" +
                " à pied (3 min)\\n15h50 Renens VD (voie 5) → Palézieux (arr. 16h16 voie 3)\\n\r\n" +
                " changement (3 min)\\n16h19 Palézieux (voie 12) → Gruyères (arr. 17h28 voie \r\n" +
                " 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:e4355f9c-31da-45b1-b2dd-1bb983202abb\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T155500\r\n" +
                "DTEND:20250318T175700\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:15h55 Ecublens VD, EPFL → Renens VD, gare (arr. 16h01)\\ntrajet \r\n" +
                " à pied (3 min)\\n16h08 Renens VD (voie 5) → Lausanne (arr. 16h14 voie 1)\\nc\r\n" +
                " hangement (5 min)\\n16h21 Lausanne (voie 70) → Palézieux (arr. 16h40 voie 2\r\n" +
                " )\\nchangement (3 min)\\n16h49 Palézieux (voie 12) → Gruyères (arr. 17h57 vo\r\n" +
                " ie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:9c83eae7-e098-44b7-a624-991b15f57314\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T161300\r\n" +
                "DTEND:20250318T175700\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:16h13 Ecublens VD, EPFL → Renens VD, gare (arr. 16h19)\\ntrajet \r\n" +
                " à pied (3 min)\\n16h26 Renens VD (voie 4) → Lausanne (arr. 16h33 voie 5)\\nc\r\n" +
                " hangement (5 min)\\n16h40 Lausanne (voie 1) → Romont FR (arr. 17h13 voie 2)\r\n" +
                " \\nchangement (3 min)\\n17h22 Romont FR (voie 1) → Bulle (arr. 17h41 voie 2)\r\n" +
                " \\nchangement (3 min)\\n17h50 Bulle (voie 4) → Gruyères (arr. 17h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:0bcd1dfb-7449-41eb-9e9c-ef147839efe5\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T163800\r\n" +
                "DTEND:20250318T182100\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:16h38 Ecublens VD, EPFL → Renens VD, gare (arr. 16h44)\\ntrajet \r\n" +
                " à pied (3 min)\\n16h50 Renens VD (voie 5) → Palézieux (arr. 17h16 voie 3)\\n\r\n" +
                " changement (3 min)\\n17h19 Palézieux (voie 12) → Bulle (arr. 17h59 voie 4)\\\r\n" +
                " nchangement (3 min)\\n18h14 Bulle (voie 7) → Gruyères (arr. 18h21 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:ab4aa450-b4b9-4807-a9d0-92d5e0682c4b\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T163800\r\n" +
                "DTEND:20250318T182800\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:16h38 Ecublens VD, EPFL → Renens VD, gare (arr. 16h44)\\ntrajet \r\n" +
                " à pied (3 min)\\n16h50 Renens VD (voie 5) → Palézieux (arr. 17h16 voie 3)\\n\r\n" +
                " changement (3 min)\\n17h19 Palézieux (voie 12) → Gruyères (arr. 18h28 voie \r\n" +
                " 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:e65ee49b-d063-4af4-b722-743635dc540b\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T165800\r\n" +
                "DTEND:20250318T185100\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:16h58 Ecublens VD, EPFL → Lausanne, Flon (arr. 17h12)\\nchangeme\r\n" +
                " nt (2 min)\\n17h14 Lausanne, Flon → Lausanne, gare (arr. 17h16)\\ntrajet à p\r\n" +
                " ied (5 min)\\n17h21 Lausanne (voie 70) → Palézieux (arr. 17h40 voie 2)\\ncha\r\n" +
                " ngement (3 min)\\n17h49 Palézieux (voie 12) → Gruyères (arr. 18h51 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:9d3971d6-78e1-4834-9c46-26bd53663763\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T171800\r\n" +
                "DTEND:20250318T185100\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:17h18 Ecublens VD, EPFL → Lausanne, Flon (arr. 17h32)\\nchangeme\r\n" +
                " nt (2 min)\\n17h34 Lausanne, Flon → Lausanne, gare (arr. 17h35)\\ntrajet à p\r\n" +
                " ied (5 min)\\n17h40 Lausanne (voie 1) → Romont FR (arr. 18h13 voie 2)\\nchan\r\n" +
                " gement (3 min)\\n18h22 Romont FR (voie 1) → Bulle (arr. 18h41 voie 2)\\nchan\r\n" +
                " gement (3 min)\\n18h44 Bulle (voie 4) → Gruyères (arr. 18h51 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:039bf1df-043d-4dfd-a629-92155e8b4596\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T173800\r\n" +
                "DTEND:20250318T192800\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:17h38 Ecublens VD, EPFL → Renens VD, gare (arr. 17h44)\\ntrajet \r\n" +
                " à pied (3 min)\\n17h50 Renens VD (voie 5) → Palézieux (arr. 18h16 voie 3)\\n\r\n" +
                " changement (3 min)\\n18h19 Palézieux (voie 12) → Gruyères (arr. 19h28 voie \r\n" +
                " 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:182e4b3b-ccf0-448f-99cc-f43e5e050e8f\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T175800\r\n" +
                "DTEND:20250318T195700\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:17h58 Ecublens VD, EPFL → Renens VD, gare (arr. 18h04)\\ntrajet \r\n" +
                " à pied (3 min)\\n18h08 Renens VD (voie 5) → Lausanne (arr. 18h14 voie 1)\\nc\r\n" +
                " hangement (5 min)\\n18h21 Lausanne (voie 70) → Palézieux (arr. 18h40 voie 2\r\n" +
                " )\\nchangement (3 min)\\n18h49 Palézieux (voie 12) → Gruyères (arr. 19h57 vo\r\n" +
                " ie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:dbfa5cdb-99ab-40fe-a21d-03ee1aa33dc0\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T181300\r\n" +
                "DTEND:20250318T195700\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:18h13 Ecublens VD, EPFL → Renens VD, gare (arr. 18h19)\\ntrajet \r\n" +
                " à pied (3 min)\\n18h26 Renens VD (voie 4) → Lausanne (arr. 18h33 voie 5)\\nc\r\n" +
                " hangement (5 min)\\n18h40 Lausanne (voie 1) → Romont FR (arr. 19h13 voie 2)\r\n" +
                " \\nchangement (3 min)\\n19h22 Romont FR (voie 1) → Bulle (arr. 19h41 voie 2)\r\n" +
                " \\nchangement (3 min)\\n19h50 Bulle (voie 4) → Gruyères (arr. 19h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:52007632-b386-4dda-afda-50aa2c08ffca\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T183300\r\n" +
                "DTEND:20250318T202800\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:18h33 Ecublens VD, EPFL → Renens VD, gare (arr. 18h39)\\ntrajet \r\n" +
                " à pied (3 min)\\n18h50 Renens VD (voie 5) → Palézieux (arr. 19h16 voie 3)\\n\r\n" +
                " changement (3 min)\\n19h19 Palézieux (voie 12) → Gruyères (arr. 20h28 voie \r\n" +
                " 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:c642fcc3-3a2f-4c53-9060-2be24c9dc509\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T185800\r\n" +
                "DTEND:20250318T205700\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:18h58 Ecublens VD, EPFL → Renens VD, gare (arr. 19h04)\\ntrajet \r\n" +
                " à pied (3 min)\\n19h08 Renens VD (voie 5) → Lausanne (arr. 19h14 voie 1)\\nc\r\n" +
                " hangement (5 min)\\n19h21 Lausanne (voie 70) → Palézieux (arr. 19h40 voie 2\r\n" +
                " )\\nchangement (3 min)\\n19h49 Palézieux (voie 12) → Gruyères (arr. 20h57 vo\r\n" +
                " ie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:06a7ac19-4f8f-4a90-84be-af1ff02a2a6f\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T191300\r\n" +
                "DTEND:20250318T205700\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:19h13 Ecublens VD, EPFL → Renens VD, gare (arr. 19h19)\\ntrajet \r\n" +
                " à pied (3 min)\\n19h26 Renens VD (voie 4) → Lausanne (arr. 19h33 voie 5)\\nc\r\n" +
                " hangement (5 min)\\n19h40 Lausanne (voie 1) → Romont FR (arr. 20h13 voie 2)\r\n" +
                " \\nchangement (3 min)\\n20h22 Romont FR (voie 1) → Bulle (arr. 20h41 voie 2)\r\n" +
                " \\nchangement (3 min)\\n20h50 Bulle (voie 4) → Gruyères (arr. 20h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:857a948c-7b6f-4801-8e78-edc9b013ea94\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T193800\r\n" +
                "DTEND:20250318T212800\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:19h38 Ecublens VD, EPFL → Renens VD, gare (arr. 19h44)\\ntrajet \r\n" +
                " à pied (3 min)\\n19h50 Renens VD (voie 5) → Palézieux (arr. 20h16 voie 3)\\n\r\n" +
                " changement (3 min)\\n20h19 Palézieux (voie 12) → Gruyères (arr. 21h28 voie \r\n" +
                " 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:86c863ec-186f-423d-8b94-55898d7cdcdf\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T195600\r\n" +
                "DTEND:20250318T215700\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:19h56 Ecublens VD, EPFL → Renens VD, gare (arr. 20h02)\\ntrajet \r\n" +
                " à pied (3 min)\\n20h08 Renens VD (voie 5) → Lausanne (arr. 20h14 voie 1)\\nc\r\n" +
                " hangement (5 min)\\n20h21 Lausanne (voie 70) → Palézieux (arr. 20h40 voie 2\r\n" +
                " )\\nchangement (3 min)\\n20h49 Palézieux (voie 12) → Gruyères (arr. 21h57 vo\r\n" +
                " ie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:daf21b46-e9c9-46f4-9990-c512569c1bd0\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T201700\r\n" +
                "DTEND:20250318T215700\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:20h17 Ecublens VD, EPFL → Lausanne, Flon (arr. 20h30)\\nchangeme\r\n" +
                " nt (2 min)\\n20h32 Lausanne, Flon → Lausanne, gare (arr. 20h34)\\ntrajet à p\r\n" +
                " ied (5 min)\\n20h40 Lausanne (voie 1) → Romont FR (arr. 21h13 voie 2)\\nchan\r\n" +
                " gement (3 min)\\n21h22 Romont FR (voie 1) → Bulle (arr. 21h41 voie 2)\\nchan\r\n" +
                " gement (3 min)\\n21h50 Bulle (voie 4) → Gruyères (arr. 21h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:a9bc3255-e45f-461b-81c1-d055f2aeb995\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T204100\r\n" +
                "DTEND:20250318T222800\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:20h41 Ecublens VD, EPFL → Renens VD, gare (arr. 20h47)\\ntrajet \r\n" +
                " à pied (3 min)\\n20h50 Renens VD (voie 3) → Palézieux (arr. 21h16 voie 3)\\n\r\n" +
                " changement (3 min)\\n21h19 Palézieux (voie 12) → Gruyères (arr. 22h28 voie \r\n" +
                " 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:4f02feee-c8ba-4a85-999b-a6ce3ef19bfe\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T205600\r\n" +
                "DTEND:20250318T225700\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:20h56 Ecublens VD, EPFL → Renens VD, gare (arr. 21h02)\\ntrajet \r\n" +
                " à pied (3 min)\\n21h08 Renens VD (voie 5) → Lausanne (arr. 21h14 voie 1)\\nc\r\n" +
                " hangement (5 min)\\n21h21 Lausanne (voie 70) → Palézieux (arr. 21h40 voie 2\r\n" +
                " )\\nchangement (3 min)\\n21h49 Palézieux (voie 12) → Gruyères (arr. 22h57 vo\r\n" +
                " ie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:91f9a858-ed00-490b-8bda-1a9a4b3bd62c\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T211300\r\n" +
                "DTEND:20250318T225700\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:21h13 Ecublens VD, EPFL → Lausanne, Flon (arr. 21h26)\\nchangeme\r\n" +
                " nt (2 min)\\n21h31 Lausanne, Flon → Lausanne, gare (arr. 21h33)\\ntrajet à p\r\n" +
                " ied (5 min)\\n21h40 Lausanne (voie 6) → Romont FR (arr. 22h13 voie 2)\\nchan\r\n" +
                " gement (3 min)\\n22h22 Romont FR (voie 1) → Bulle (arr. 22h41 voie 2)\\nchan\r\n" +
                " gement (3 min)\\n22h50 Bulle (voie 4) → Gruyères (arr. 22h57 voie 2)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:6b6aa268-dbb2-41d8-85c5-0f64308ce1a0\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T213300\r\n" +
                "DTEND:20250318T232800\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:21h33 Ecublens VD, EPFL → Renens VD, gare (arr. 21h39)\\ntrajet \r\n" +
                " à pied (3 min)\\n21h50 Renens VD (voie 3) → Palézieux (arr. 22h16 voie 3)\\n\r\n" +
                " changement (3 min)\\n22h19 Palézieux (voie 12) → Gruyères (arr. 23h28 voie \r\n" +
                " 1)\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n" +
                "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:ReCHor\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:6850b32d-e2f5-4035-b44c-c344c90a4d24\r\n" +
                "DTSTAMP:20250326T123411\r\n" +
                "DTSTART:20250318T214300\r\n" +
                "DTEND:20250318T235800\r\n" +
                "SUMMARY:Ecublens VD, EPFL → Gruyères\r\n" +
                "DESCRIPTION:21h43 Ecublens VD, EPFL → Renens VD, gare (arr. 21h49)\\ntrajet \r\n" +
                " à pied (3 min)\\n22h01 Renens VD (voie 3) → Montreux (arr. 22h35 voie 3)\\nc\r\n" +
                " hangement (3 min)\\n22h43 Montreux (voie 7) → Montbovon (arr. 23h34 voie 2)\r\n" +
                " \\nchangement (2 min)\\n23h38 Montbovon (voie 3) → Gruyères (arr. 23h58 voie\r\n" +
                "  1)\r\n" +
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
