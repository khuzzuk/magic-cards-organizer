package pl.khuzzuk.mtg.organizer;

public enum Event {
    //GUI
    SET_PRIMARY_STAGE,
    FX_THREAD_STARTED,
    WINDOW_TO_SHOW,

    //HTML extraction
    /** accepts {@link String} send CARD_DATA */
    CARD_FROM_URL,
    CARD_DTO_JSON,
    CARD_DATA,
    CARD_INDEX,
    CARD_FIND,

    //SETTINGS
    SET_REPO_LOCATION,

    //CONTAINER
    LEFT_PANE_FILTER,
    MAIN_VIEW_SELECTOR,
    TABLE_SELECTOR,
    CARD_VIEWER,
    ORGANIZER_MENU,
    BINDER,
    PREDEFINED_SKILLS,
    SETTINGS_MANAGER,

    ERROR,
    CLOSE
}