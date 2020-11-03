package abcd.spc.l.streams.sr.common;

import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * https://www.baeldung.com/java-8-date-time-intro
 */
public class DatesTest {
    @Test
    void localDateTest() {
        LocalDate localDate = LocalDate.now();
        System.out.println(localDate.toString());
        LocalDate localDate_2020_11_31 = LocalDate.of(2020, 10, 31);
        System.out.println(localDate_2020_11_31.toString());
        System.out.println(localDate_2020_11_31.getDayOfMonth());
        System.out.println(localDate_2020_11_31.getDayOfWeek());
        System.out.println(localDate_2020_11_31.plusDays(1));
        System.out.println(localDate_2020_11_31.plusDays(1).getDayOfWeek());
    }

    @Test
    void getSunTest() {
        assertEquals(getSun(
                LocalDate.of(2020, 10, 31)),
                LocalDate.of(2020, 11, 1));
    }

    LocalDate getSun(LocalDate localDate) {
        if (localDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return localDate;
        }
        int k = 1;
        while (localDate.plusDays(k).getDayOfWeek() != DayOfWeek.SUNDAY) {
            k += 1;
        }
        return localDate.plusDays(k);
    }

    @Test
    void localTimeTest() {
        LocalTime now = LocalTime.now();
        System.out.println(now.toString());
    }

    @Test
    void localDateTimeTest() {
        LocalDateTime localDateTime = LocalDateTime.of(2015, Month.FEBRUARY, 20, 6, 30);
        System.out.println(localDateTime.toString());
    }
}
