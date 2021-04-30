package com.abbvisor.abbvisor.util;

import com.google.android.gms.location.places.Place;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by natavit on 1/27/2017 AD.
 */

public enum PlaceFilters2 {

    TYPE_AMUSEMENT_PARK(Place.TYPE_AMUSEMENT_PARK),
    TYPE_AQUARIUM(Place.TYPE_AQUARIUM),
    TYPE_ART_GALLERY(Place.TYPE_ART_GALLERY),
    TYPE_BAKERY(Place.TYPE_BAKERY),
    TYPE_BAR(Place.TYPE_BAR),
    TYPE_BOOK_STORE(Place.TYPE_BOOK_STORE),
    TYPE_CAFE(Place.TYPE_CAFE),
    TYPE_CASINO(Place.TYPE_CASINO),
    TYPE_DEPARTMENT_STORE(Place.TYPE_DEPARTMENT_STORE),
    TYPE_FOOD(Place.TYPE_FOOD),
    TYPE_HOME_GOODS_STORE(Place.TYPE_HOME_GOODS_STORE),
    TYPE_JEWELRY_STORE(Place.TYPE_JEWELRY_STORE),
    TYPE_LIBRARY(Place.TYPE_LIBRARY),
    TYPE_MOVIE_THEATER(Place.TYPE_MOVIE_THEATER),
    TYPE_MUSEUM(Place.TYPE_MUSEUM),
    TYPE_NATURAL_FEATURE(Place.TYPE_NATURAL_FEATURE),
    TYPE_NIGHT_CLUB(Place.TYPE_NIGHT_CLUB),
    TYPE_PARK(Place.TYPE_PARK),
    TYPE_PET_STORE(Place.TYPE_PET_STORE),
    TYPE_PLACE_OF_WORSHIP(Place.TYPE_PLACE_OF_WORSHIP),
    TYPE_POINT_OF_INTEREST(Place.TYPE_POINT_OF_INTEREST),
    TYPE_RESTAURANT(Place.TYPE_RESTAURANT),
    TYPE_SHOPPING_MALL(Place.TYPE_SHOPPING_MALL),
    TYPE_SPA(Place.TYPE_SPA),
    TYPE_ZOO(Place.TYPE_ZOO);


    private static final List<Integer> VALUES;

    private final int type;

    static {
        VALUES = new ArrayList<>();
        for (PlaceFilters2 v : PlaceFilters2.values()) {
            VALUES.add(v.type);
        }
    }

    PlaceFilters2(int type) {
        this.type = type;
    }

    public static List<Integer> getValues() {
        return Collections.unmodifiableList(VALUES);
    }
}
