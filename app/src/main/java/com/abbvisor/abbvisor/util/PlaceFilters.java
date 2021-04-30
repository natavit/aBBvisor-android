package com.abbvisor.abbvisor.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by natavit on 1/27/2017 AD.
 */

public enum PlaceFilters {

    TYPE_1("amusement_park"), TYPE_2("aquarium"), TYPE_3("art_gallery"), TYPE_4("bakery"),
    TYPE_5("bar"), TYPE_6("book_store"), TYPE_7("bowling_alley"), TYPE_8("cafe"),
    TYPE_9("campground"), TYPE_10("casino"), TYPE_11("city_hall"), TYPE_12("clothing_store"),
    TYPE_13("convenience_store"), TYPE_14("department_store"), TYPE_15("florist"), TYPE_16("gym"),
    TYPE_17("jewelry_store"), TYPE_18("library"), TYPE_19("movie_theater"), TYPE_20("moving_company"),
    TYPE_21("museum"), TYPE_22("night_club"), TYPE_23("park"), TYPE_24("restaurant"),
    TYPE_25("shoe_store"), TYPE_26("shopping_mall"), TYPE_27("spa"), TYPE_28("stadium"),
    TYPE_29("store"), TYPE_30("travel_agency"), TYPE_31("zoo"),

    // Type 2
    TYPE_32("food"), TYPE_33("point_of_interest");
//    TYPE_34("establishment");


    private static final List<String> VALUES;

    private final String type;

    static {
        VALUES = new ArrayList<>();
        for (PlaceFilters v : PlaceFilters.values()) {
            VALUES.add(v.type);
        }
    }

    PlaceFilters(String type) {
        this.type = type;
    }

    public static List<String> getValues() {
        return Collections.unmodifiableList(VALUES);
    }
}
