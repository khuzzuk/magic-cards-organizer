package pl.khuzzuk.mtg.organizer.events;

public enum Event {
    //GUI
    SET_PRIMARY_STAGE,
    FX_THREAD_STARTED,
    WINDOW_TO_SHOW,
    /** accepts {@link java.util.SortedSet}&lt{@link String}&gt*/
    REFRESH_CARD_SETS,

    //Card extraction
    /** accepts {@link String} send CARD_DATA */
    URL_TO_IMPORT,
    SET_FROM_URL,
    CARD_FROM_URL,
    CARD_DTO_JSON,
    CARD_DATA,
    CARD_INDEX,
    CARD_SHOW,
    CARD_LIST_SHOW,
    /** accepts {@link pl.khuzzuk.mtg.organizer.model.CardQuery}, produces {@link java.util.Set}&lt{@link pl.khuzzuk.mtg.organizer.model.card.Card}&gt*/
    CARD_FIND,
    /** produces {@link java.util.SortedSet}&lt{@link String}&gt*/
    CARD_SETS,
    REINDEX_REPO,

    /** accepts {@link java.net.URL}*/
    DOWNLOAD_IMAGE,

    //SETTINGS
    SET_REPO_LOCATION,

    ERROR,
    CLOSE
}