package pl.khuzzuk.mtg.organizer.extractor

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import pl.khuzzuk.messaging.Bus
import pl.khuzzuk.mtg.organizer.Event
import pl.khuzzuk.mtg.organizer.initialize.Loadable
import pl.khuzzuk.mtg.organizer.model.Card
import pl.khuzzuk.mtg.organizer.model.CreatureCard
import pl.khuzzuk.mtg.organizer.model.SpellCard
import pl.khuzzuk.mtg.organizer.model.type.BasicType
import pl.khuzzuk.mtg.organizer.model.type.Type

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
        def profile = doc.getElementById('main').getElementsByClass("card-profile").get(0)
        Type type = determineBasicTypeFrom(profile.getElementsByClass('card-text-type-line').text())

        Card card = createCreatureCard(profile)
        card.type = type
        bus.message(Event.CARD_DATA).withContent(card).send()
    }

    private static CreatureCard createCreatureCard(Element profile) {
        CreatureCard card = new CreatureCard()
        populateSpellCardFields(card, profile)
        def stats = profile.getElementsByClass('card-text-stats').get(0).text().split('/')
        card.attack = Integer.parseInt(stats[0])
        card.defence = Integer.parseInt(stats[1])
        card
    }

    private static <T extends SpellCard> T populateSpellCardFields(T card, Element profile) {
        populateBasicFields(card, profile)
        Element titleText = profile.getElementsByClass("card-text-title").get(0)
        Elements manaCostElements = titleText.children()
        if (manaCostElements.size() > 0) {
            card.manaCost = TextToMana.from(manaCostElements.text())
        }
        card
    }

    private static <T extends Card> T populateBasicFields(T card, Element profile) {
        Element titleText = profile.getElementsByClass("card-text-title").get(0)
        Elements manaCostElements = titleText.children()
        card.name = titleText.text().replace(manaCostElements.text(), '').trim()
        card.text = profile.getElementsByClass("card-text-flavor").get(0).child(0).text()
        card.front = new URL(profile.getElementsByClass('card-image-front').get(0)
                .getElementsByAttribute('src').get(0).attributes().get('src'))
        card
    }

    private static Type determineBasicTypeFrom(String text) {
        Type type = new Type()
        String[] lineElements = text.split(' — ')
        if (lineElements[0].contains(' ')) {
            def primaryTypes = lineElements[0].split(' ')
            type.basicType = BasicType.valueOf(primaryTypes[1])
            type.primaryTypes.add(primaryTypes[0])
        } else {
            type.basicType = BasicType.valueOf(lineElements[0])
        }
        if (lineElements.length > 1) {
            def secondaryTypes = lineElements[1].split(' ')
            for (String secondaryType : secondaryTypes) {
                type.secondaryTypes.add(secondaryType)
            }
        }
        type
    }
}
