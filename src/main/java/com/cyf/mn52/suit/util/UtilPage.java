package com.cyf.mn52.suit.util;


import com.cyf.mn52.suit.request.Search;

public class UtilPage {

    public static String getPage(int page, int limit) {
        return String.format("%s,%s", (page - 1) * limit, limit);
    }

    public static String getPage(Search model) {
        return String.format("%s,%s", (model.page - 1) * model.limit, model.limit);
    }
}
