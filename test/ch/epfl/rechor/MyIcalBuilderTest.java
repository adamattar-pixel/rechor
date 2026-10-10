package ch.epfl.rechor;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static ch.epfl.rechor.IcalBuilder.Component.VCALENDAR;
import static ch.epfl.rechor.IcalBuilder.Component.VEVENT;
import static ch.epfl.rechor.IcalBuilder.Name.*;
import static org.junit.jupiter.api.Assertions.*;

public class MyIcalBuilderTest {

    @Test
    void IcalAddValueWorks() {
        IcalBuilder test1 = new IcalBuilder();
        test1.add(DESCRIPTION, "123456789012345678901245678901234567890123456789012345678901234");
        assertEquals("DESCRIPTION:123456789012345678901245678901234567890123456789012345678901234\r\n", test1.build());

        IcalBuilder test2 = new IcalBuilder();
        test2.add(DESCRIPTION, "1234567890123456789012456789012345678901234567890123456789012345");
        assertEquals("DESCRIPTION:123456789012345678901245678901234567890123456789012345678901234\r\n 5\r\n", test2.build());

        IcalBuilder test3 = new IcalBuilder();
        test3.add(DESCRIPTION, "12345");
        assertEquals("DESCRIPTION:12345\r\n", test3.build());
    }

    @Test
    void IcalAddDateWorks() {
        IcalBuilder test1 = new IcalBuilder();
        LocalDateTime dateTime1 = LocalDateTime.of(2024, Month.JUNE, 10, 14, 30, 45);
        test1.add(PRODID, dateTime1);
        assertEquals("PRODID:20240610T143045\r\n", test1.build());

        IcalBuilder test2 = new IcalBuilder();
        LocalDateTime dateTime2 = LocalDateTime.of(0001, Month.JANUARY, 1, 0, 0, 0);
        test2.add(BEGIN, dateTime2);
        assertEquals("BEGIN:00010101T000000\r\n", test2.build());

        IcalBuilder test3 = new IcalBuilder();
        LocalDateTime dateTime3 = LocalDateTime.of(2024, Month.DECEMBER, 31, 23, 59, 59);
        LocalDateTime dateTime4 = LocalDateTime.of(2024, 6, 11, 12, 30, 30);
        test3.add(UID, dateTime3);
        test3.add(VERSION, dateTime4);
        assertEquals("UID:20241231T235959\r\nVERSION:20240611T123030\r\n", test3.build());
        LocalDateTime dateTime5 = LocalDateTime.of(2024, 6, 10, 10, 0, 0);
        test3.add(END, dateTime5);
        assertEquals("UID:20241231T235959\r\nVERSION:20240611T123030\r\nEND:20240610T100000\r\n", test3.build());
    }


    @Test
    void IcalEndWork() {
        assertThrows(IllegalArgumentException.class, () -> {
            IcalBuilder test = new IcalBuilder();
            test.begin(VCALENDAR);
            test.end();
            test.end();
        });
        assertThrows(IllegalArgumentException.class, () -> {
            IcalBuilder test = new IcalBuilder();
            test.end();
        });
        assertDoesNotThrow(() -> {
            IcalBuilder test = new IcalBuilder();
            test.begin(VCALENDAR);
            test.end();
        });
    }

    @Test
    void IcalBeginWork() {
        IcalBuilder test = new IcalBuilder();
        IcalBuilder test1 = new IcalBuilder();
        IcalBuilder test2 = new IcalBuilder();
        assertThrows(IllegalArgumentException.class, () -> {
            test.begin(VEVENT).build();
        });
        assertThrows(IllegalArgumentException.class, () -> {
            test1.begin(VCALENDAR).build();
        });
        assertEquals("BEGIN:VCALENDAR\r\nEND:VCALENDAR\r\n", test2.begin(VCALENDAR).end().build());

    }

}
