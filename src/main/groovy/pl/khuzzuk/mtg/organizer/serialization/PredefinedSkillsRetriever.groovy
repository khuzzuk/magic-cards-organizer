package pl.khuzzuk.mtg.organizer.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import pl.khuzzuk.mtg.organizer.model.skill.PredefinedSkill

class PredefinedSkillsRetriever {
    static void main(String[] args) {
        extractPredefinedSkillsFromGamepdia()
    }
    private static void extractPredefinedSkillsFromGamepdia() {
        List<PredefinedSkill> predefinedSkills = new ArrayList<>()

        def baseUrl = 'https://mtg.gamepedia.com/'
        def doc = Jsoup.connect(baseUrl + 'Keyword_ability').get()
        def keywords = doc.getElementsByAttributeValue('title', 'Keyword abilities').get(0)
        def ulWithKeywords = keywords.parent().child(2).children()
        def size = ulWithKeywords.size()
        for (int i = 1; i < size; i++) {
            PredefinedSkill predefinedSkill = new PredefinedSkill()
            predefinedSkills.add(predefinedSkill)

            predefinedSkill.text = ulWithKeywords.get(i).children().get(1).text()

            def skillUrl = baseUrl + predefinedSkill.text.replace(' ', '_').toLowerCase().capitalize()
            def skillDoc = Jsoup.connect(skillUrl).get()
            Element infobox = skillDoc.getElementsByClass('infobox').get(0)
            def reminderText = infobox.getElementsContainingOwnText("Reminder Text").next().text()
            predefinedSkill.info = reminderText
        }
        ObjectMapper mapper = new ObjectMapper()
        PredefinedSkillRepo.PredefinedSkillContainer container = new PredefinedSkillRepo.PredefinedSkillContainer()
        container.setPredefinedSkills(predefinedSkills)
        println mapper.writeValueAsString(container)
    }
}
