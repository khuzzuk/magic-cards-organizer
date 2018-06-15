package pl.khuzzuk.mtg.organizer;

public enum Event {
    //GUI
    SET_PRIMARY_STAGE,
    FX_THREAD_STARTED,
    WINDOW_TO_SHOW,

    //HTML extraction
    /** accepts {@link String} send CARD_DATA */
    CARD_FROM_URL,
    CARD_DATA,

    //CONTAINER
    LEFT_PANE_FILTER,
    MAIN_VIEW_SELECTOR,
    TABLE_SELECTOR,
    CARD_VIEWER,
    ORGANIZER_MENU,
    IMPORT_POPUP,
    BINDER,
    PREDEFINED_SKILLS,

    ERROR,
    CLOSE
}