package edu.ucsb.cs56.mapache_search;

/** 
 * Literals for testing whether Bootstrap is loaded correctly
 */
public class BootstrapLiterals {

    public static final String bootstrapCSSXpath = "//head/link[@rel='stylesheet' and @href='https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css']";

    public static final String [] bootstrapJSurls = {
        "https://code.jquery.com/jquery-3.3.1.slim.min.js", 
        "https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js", 
        "https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js",
    };

}
