package ua.klesaak.simpleconomy.utils;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class NumberUtils {
    public final ThreadLocalRandom LOCAL_RANDOM = ThreadLocalRandom.current();
    public final Pattern TIME_TO_MILLIS_PATTERN = Pattern.compile("(?i)" + "(\\d{1,3}(?=ns))?" + "(\\d{1,3}(?=mc))?" + "(\\d{1,3}(?=ms))?" + "(\\d{1,3}(?=s))?" + "(\\d{1,3}(?=m))?" + "(\\d{1,3}(?=h))?" + "(\\d{1,3}(?=d))?" + "(\\d{1,3}(?=w))?");

    /**
     * Добавить знак после каждой третьей цифры
     *
     * @param number - число
     * @param symbol - знак
     */
    public String spaced(int number, String symbol) {
        String integer = String.valueOf(number);
        StringBuilder builder = new StringBuilder();

        for (int a = 0; a < integer.length(); a++) {
            builder.append(integer.split("")[a]);

            if ((integer.length() - a + 2) % 3 != 0) {
                continue;
            }
            builder.append(symbol);
        }

        return builder.substring(0, builder.toString().length() - 1);
    }

    /**
     * Добавить запятую после каждой третьей цифры
     *
     * @param number - число
     */
    public String spaced(int number) {
        return spaced(number, ",");
    }

    /**
     * Получить процент текущего числа от максимального
     *
     * @param currentNumber - текущее число
     * @param maxNumber     - максимальное число
     */
    public int getIntPercent(int currentNumber, int maxNumber) {
        return (int) Math.round(PercentUtil.getPercent(currentNumber, maxNumber));
    }

    /**
     * Получить число от процента максимального числа
     *
     * @param currentPercent - текущий процент, который надо найти
     * @param maxNumber      - максимальное число, от которого ищем процент
     */
    public int getIntNumberByPercent(int currentPercent, int maxNumber) {
        return (int) Math.round(PercentUtil.getNumberByPercent(currentPercent, maxNumber));
    }

    /**
     * Возвести число в степень
     *
     * @param number - число
     * @param radian - степень
     */
    public int toRadians(int number, int radian) {
        for (int count = 0; count < radian; count++) {
            number *= number;
        }

        return number;
    }

    /**
     * Создать массив, который будет иметь в себе
     * множество значений между минимальным и
     * максимальным указанным индексом
     *
     * @param minIndex - минимальный индекс
     * @param maxIndex - максимальный индекс
     */
    public int[] toManyArray(int minIndex, int maxIndex) {
        int[] resultArray = new int[maxIndex - minIndex];

        int counter = 0;
        for (int i = minIndex; i < maxIndex; i++) {
            resultArray[counter] = i;

            counter++;
        }

        return resultArray;
    }

    /**
     * Получить рандомное число
     *
     * @param min - минимальное значение
     * @param max - максимальное значение
     */
    public int randomInt(int min, int max) {
        return min + LOCAL_RANDOM.nextInt(max - min);
    }

    /**
     * Получить рандомное число
     *
     * @param min - минимальное значение
     * @param max - максимальное значение
     */
    public double randomDouble(double min, double max) {
        return min + LOCAL_RANDOM.nextDouble(max - min);
    }

    /**
     * Преобразовать число в грамотно составленное
     * словосочетание
     *
     * @param number - число
     * @param single  - словосочетание, если число закаончивается на 1
     * @param second - словосочетание, если число закаончивается на 2
     * @param other  - словосочетание, если число закаончивается на 5
     */
    public String formatting(int number, String single, String second, String other) {
        if (number % 100 > 10 && number % 100 < 15) {
            return number + " " + other;
        }
        return switch (number % 10) {
            case 1 -> number + " " + single;
            case 2, 3, 4 -> number + " " + second;
            default -> number + " " + other;
        };
    }


    /**
     * Преобразовать число в грамотно составленное
     * словосочетание
     *
     * @param number - число
     * @param single  - словосочетание, если число закаончивается на 1
     * @param second - словосочетание, если число закаончивается на 2
     * @param other  - словосочетание, если число закаончивается на 5
     */
    public String formattingSpaced(int number, String single, String second, String other) {
        if (number % 100 > 10 && number % 100 < 15) {
            return spaced(number) + " " + other;
        }
        return switch (number % 10) {
            case 1 -> spaced(number) + " " + single;
            case 2, 3, 4 -> spaced(number) + " " + second;
            default -> spaced(number) + " " + other;
        };
    }

    /**
     * Преобразовать число в грамотно составленное
     * словосочетание
     *
     * @param number - число
     * @param unit   - словосочетание
     */
    public String formatting(int number, NumberTimeUnit unit) {
        return formatting(number, unit.getOne(), unit.getTwo(), unit.getOther());
    }

    /**
     * Получить грамотно составленное время из
     * количества секунд
     *
     * @param seconds - кол-во секунд
     */
    public String getTime(int seconds) {
        int minutes = 0, hours = 0, days = 0, weeks = 0, months = 0, years = 0;

        if (seconds >= 60) {
            int i = seconds / 60;
            seconds -= 60 * i;
            minutes += i;
        }

        if (minutes >= 60) {
            int i = minutes / 60;
            minutes -= 60 * i;
            hours += i;
        }

        if (hours >= 24) {
            int i = hours / 24;
            hours -= 24 * i;
            days += i;
        }

        if (days >= 7) {
            int i = days / 7;
            days -= 7 * i;
            weeks += i;
        }

        if (weeks >= 4) {
            int i = weeks / 4;
            weeks -= 4 * i;
            months += i;
        }

        if (months >= 12) {
            int i = months / 12;
            months -= 12 * i;
            years += i;
        }

        StringBuilder builder = new StringBuilder();

        if (years != 0) {
            builder.append(formatting(years, NumberTimeUnit.YEARS)).append(" ");
        }

        if (months != 0) {
            builder.append(formatting(months, NumberTimeUnit.MONTHS)).append(" ");
        }

        if (weeks != 0) {
            builder.append(formatting(weeks, NumberTimeUnit.WEEKS)).append(" ");
        }

        if (days != 0) {
            builder.append(formatting(days, NumberTimeUnit.DAYS)).append(" ");
        }

        if (hours != 0) {
            builder.append(formatting(hours, NumberTimeUnit.HOURS)).append(" ");
        }

        if (minutes != 0) {
            builder.append(formatting(minutes, NumberTimeUnit.MINUTES)).append(" ");
        }

        if (seconds != 0) {
            builder.append(formatting(seconds, NumberTimeUnit.SECONDS));
        }

        return builder.toString();
    }

    /**
     * Получить грамотно составленное время из
     * количества миллисекунд, переведенные в секунды
     *
     * @param millis - кол-во миллисекунд
     */
    public String getTime(long millis) {
        return getTime((int) millis / 1000);
    }

    /**
     * Парсит такие значения, как 5d, 3m, 50s и т.д.
     *
     * @param time   - значения, которое нужно парсить
     * @param unitTo - в какую единицу измерения времени парсить
     * @author GitCoder
     */
    public long parseTimeFromString(@NonNull String time, @NonNull TimeUnit unitTo) {
        if (time.startsWith("-a")) {
            return -1;
        }

        Matcher matcher = TIME_TO_MILLIS_PATTERN.matcher(time);
        TObjectIntMap<TimeUnit> values = new TObjectIntHashMap<>();

        while (matcher.find()) {
            for (int i = 1; i <= 7; i++) {
                String value = matcher.group(i);

                if (value == null || value.isEmpty()) {
                    continue;
                }

                TimeUnit unit = TimeUnit.values()[i - 1];
                int intValue = Integer.parseInt(value);

                values.adjustOrPutValue(unit, intValue, intValue);
                break;
            }
        }

        if (values.isEmpty()) {
            throw new IllegalArgumentException("Illegal Date");
        }

        AtomicLong total = new AtomicLong();

        values.forEachEntry((timeUnit, value) -> {
            total.addAndGet(unitTo.convert(value, timeUnit));

            return true;
        });

        if (total.get() <= 0) {
            throw new IllegalArgumentException("Illegal Date");
        }

        return total.get();
    }

    @RequiredArgsConstructor
    @Getter
    public enum NumberTimeUnit {
        SECONDS("секунда", "секунды", "секунд"),
        MINUTES("минута", "минуты", "минут"),
        HOURS("час", "часа", "часов"),
        DAYS("день", "дня", "дней"),
        WEEKS("неделя", "недели", "недель"),
        MONTHS("месяц", "месяца", "месяцев"),
        YEARS("год", "года", "лет");

        private final String one, two, other;
    }
}
