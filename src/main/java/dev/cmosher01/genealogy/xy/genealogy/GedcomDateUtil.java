/*
 *     Copyright 2026, Christopher Alan Mosher, New York, New York, USA, <cmosher01@gmail.com>.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.cmosher01.genealogy.xy.genealogy;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nu.mine.mosher.gedcom.date.*;
import nu.mine.mosher.gedcom.date.parser.*;

import java.io.StringReader;
import java.util.*;

@Slf4j
public class GedcomDateUtil {
    public static long calcBirthForSort(final String birth) {
        val db = toDateStrict(birth);
        val d = db.getStartDate().getApproxDay().asDate();
        if (d.getTime() == 0) {
            return 0L;
        }

        val cal = Calendar.getInstance();
        cal.setTime(d);
        val year = cal.get(Calendar.YEAR);
        val month = cal.get(Calendar.MONTH);
        return 100L*year+month;
    }

    public static String getLifespan(final String birth, final String death) {
        val db = toDateLenient(birth);
        val dd = toDateLenient(death);
        if (db.equals("?") && dd.equals("?")) {
            return "";
        }
        return "("+db+"\u2013"+dd+")";
    }



    private static DatePeriod toDateStrict(final String sDate) {
        try {
            return new GedcomDateValueParser(new StringReader(sDate)).parse();
        } catch (final Exception e) {
            if (!sDate.isEmpty()) {
                log.warn("Error while parsing DATE={}", sDate, e);
            }
            return DatePeriod.UNKNOWN;
        }
    }

    private static String toDateLenient(final String s) {
        String ret = "?";
        if (!s.isBlank()) {
            try {
                val period = new GedcomDateValueParser(new StringReader(s)).parse();
                if (period.equals(DatePeriod.UNKNOWN)) {
                    ret = "?";
                } else {
                    ret = dateString(period);
                }
            } catch (final ParseException e) {
                // use the date string as-is (just make sure it's a reasonable size)
                if (s.isBlank()) {
                    ret = "?";
                } else if (s.length() <= 16) {
                    ret = s;
                } else {
                    ret = s.substring(0, 16);
                }
                log.info("Invalid date. Cannot interpret, but will display as: \"{}\"", ret, e);
            }
        }
        return ret;
    }

    private static String dateString(final DatePeriod period) {
        // We are only interested in BIRTH and DEATH dates, which are not time periods
        // (i.e., not "FROM start TO end"), so here we just choose the START date.
        // (Not to be confused with "BET x AND y" or "AFT x" or "BEF y", which are acceptable.)
        val range = period.getStartDate();
        val yearEarliest = range.getEarliest().getYear();
        val yearLatest = range.getLatest().getYear();
        val yearApprox = (yearEarliest+yearLatest)/2;
        final int year;
        if (range.getEarliest().equals(YMD.getMinimum())) {
            year = yearLatest;
        } else if (range.getLatest().equals(YMD.getMaximum())) {
            year = yearEarliest;
        } else {
            year = yearApprox;
        }

        final String circa;
        if (range.isExact()) {
            if (range.getEarliest().isCirca() || range.getLatest().isCirca()) {
                circa = "c";
            } else {
                circa = "";
            }
        } else {
            circa = "c";
        }

        final int absYear;
        final String bce;
        if (year < 0) {
            absYear = -year;
            bce = "bce";
        } else {
            absYear = year;
            bce = "";
        }

        return String.format("%s%d%s", circa, absYear, bce);
    }
}
