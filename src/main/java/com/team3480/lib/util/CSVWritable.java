package com.team3480.lib.util;

/*
///LBCHECK3480
*/

/**
 * CSVWritable is an interface used create a string with specif format, using DecimalFormat java class.
 *  like "#0.000" that guarantees that the string always have 0.000 characters number.
 */

public interface CSVWritable {
    String toCSV();
    String toCSVHeader();
}
