package pl.khuzzuk.mtg.organizer

class PropertyContainer<V> {
    private V propertyValue

    synchronized V get() {
        propertyValue
    }

    synchronized void put(V newValue) {
        propertyValue = newValue
    }

    synchronized boolean hasValue() {
        propertyValue != null
    }

    synchronized void clear() {
        propertyValue = null
    }
}
