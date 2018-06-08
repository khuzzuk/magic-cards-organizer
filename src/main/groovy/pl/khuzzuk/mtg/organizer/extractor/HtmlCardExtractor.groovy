package pl.khuzzuk.mtg.organizer.extractor

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import pl.khuzzuk.messaging.Bus
import pl.khuzzuk.mtg.organizer.Event
import pl.khuzzuk.mtg.organizer.initialize.Loadable
import pl.khuzzuk.mtg.organizer.model.Card

class HtmlCardExtractor implements Loadable {
    private Bus<Event> bus

    HtmlCardExtractor(Bus<Event> bus) {
        this.bus = bus
    }

    @Override
    void load() {
        bus.subscribingFor(Event.CARD_FROM_URL).accept({cardFrom(it as String)}).subscribe()
    }

    private void cardFrom(String url) {
        def doc = Jsoup.connect(url).get()
        def profile = doc.getElementById("main").getElementsByClass("card-profile").get(0)
        Card card = new Card()
        Element titleText = profile.getElementsByClass("card-text-title").get(0)
        Elements manaCostElements = titleText.children()
        card.name = titleText.text().replace(manaCostElements.text(), '').trim()
        card.text = profile.getElementsByClass("card-text-flavor").get(0).child(0).text()
        card.front = new URL(profile.getElementsByClass('card-image-front').get(0)
                .getElementsByAttribute('src').get(0).attributes().get('src'))
        if (manaCostElements.size() > 0) {
            card.manaCost = TextToMana.from(manaCostElements.text())
        }
        bus.message(Event.CARD_DATA).withContent(card).send()
    }
}
