package ch.epfl.rechor.guiTempTest;

import ch.epfl.rechor.journey.Profile;
import ch.epfl.rechor.journey.Router;
import ch.epfl.rechor.timetable.TimeTable;
import ch.epfl.rechor.timetable.mapped.FileTimeTable;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class testProFinal {
    void profileCreator(Profile p) throws IOException {
        LocalDate d = p.date();
        Writer s = new FileWriter("timetable/profile_" + d +"_"+p.arrStationId()+".txt");
        p.stationFront().forEach(
                pf -> {
                    AtomicBoolean check = new AtomicBoolean(false);
                    AtomicInteger count = new AtomicInteger();
                    pf.forEach(
                            tuple -> {
                                try {
                                    s.write(Long.toHexString(tuple)+ ((count.get() < pf.size()-1) ? "," : ""));
                                    count.getAndIncrement();
                                    check.set(true);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    );
                    try {
                        s.write("\n");
                        check.set(true);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        s.close();
    }

    @Test
    public void testComprehension() throws IOException {
        TimeTable t = FileTimeTable.in(Path.of("timetable"));
        LocalDate date = LocalDate.of(2025, Month.MARCH, 18);
        Router router = new Router(t);
        profileCreator(router.profile(date, 11486));
        router.profile(date, 11486);
    }
}
