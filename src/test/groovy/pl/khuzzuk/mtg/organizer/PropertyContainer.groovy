package pl.khuzzuk.mtg.organizer

class PropertyContainer<T> {
    private T propertyValue

    synchronized T get() {
        propertyValue
    }

    synchronized void put(T newValue) {
        propertyValue = newValue
    }

    synchronized boolean hasValue() {
        propertyValue != null
    }

    synchronized void clear() {
        propertyValue = null
    }
}
