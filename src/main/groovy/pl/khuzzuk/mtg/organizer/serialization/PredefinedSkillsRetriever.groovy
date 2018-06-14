package pl.khuzzuk.mtg.organizer.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import pl.khuzzuk.mtg.organizer.model.skill.PredefinedSkill

class PredefinedSkillsRetriever {
    static void main(String[] args) {
        List<PredefinedSkill> predefinedSkills = new ArrayList<>()

        def baseUrl = 'https://mtg.gamepedia.com/'
        def doc = Jsoup.connect(baseUrl + 'Keyword_ability').get()
        def keywords = doc.getElementsByAttributeValue('title', 'Keyword abilities').get(0)
        def ulWithKeywords = keywords.parent().child(2).children()
        def size = ulWithKeywords.size()
        for (int i = 1; i < size; i++) {
            PredefinedSkill predefinedSkill = new PredefinedSkill()
            predefinedSkill.text = ulWithKeywords.get(i).children().get(1).text()

            try {
                def skillUrl = baseUrl + predefinedSkill.text.replace(' ', '_').toLowerCase().capitalize()
                def skillDoc = Jsoup.connect(skillUrl).get()
                Element infobox = skillDoc.getElementsByClass('infobox').get(0)
                predefinedSkill.info = retrieveDescription(infobox)
                def remainderText = skillDoc.getElementsByClass('infobox').get(0)
                        .children().get(0).children().get(6).children().get(1).children().get(0).text()
                predefinedSkill.info = remainderText.replace('(', '').replace(')', '')

                predefinedSkills.add(predefinedSkill)
            } catch (Exception ignore) {
                ignore.printStackTrace()
            }
        }
        ObjectMapper mapper = new ObjectMapper()
        println mapper.writeValueAsString(predefinedSkills)
        println predefinedSkills.size()
    }

    private static String retrieveDescription(Element infobox) {
        def children = infobox.children().get(0).children()
        for (int i = 1; i < children.size(); i++) {
            def elementsOfElements = children.get(i).children()
            if (elementsOfElements.size() < 2) continue
            def desc = elementsOfElements.get(1).children().get(0).text()
            if (desc.startsWith('(')) return desc.replace('(', '').replace(')', '')
        }
        ''
    }
}
