package pl.khuzzuk.mtg.organizer.extractor

import pl.khuzzuk.mtg.organizer.extractor.rest.CardDTO
import pl.khuzzuk.mtg.organizer.extractor.rest.CardFaceDTO
import pl.khuzzuk.mtg.organizer.model.ManaType
import pl.khuzzuk.mtg.organizer.model.type.BasicType
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class TypeExtractorSpec extends Specification {
    def "extract type"() {
        given:
        def cardDto = createCardDTO(line?.toString(), faceLine?.toString(), faceLine?.toString())

        when:
        def type = TypeExtractor.from(cardDto)

        then:
        type.basicType == expectedBasicType
        type.primaryTypes.containsAll(expectedPrimaryTypes)
        type.secondaryTypes.containsAll(expectedSecondaryTypes)
        ManaType.WHITE in type.colors

        where:
        line                                                       | faceLine                     | expectedBasicType               | expectedPrimaryTypes            | expectedSecondaryTypes
        'Basic Land — Forest'                                      | null                         | BasicType.Land                  | ['Basic'] as List<String>       | ['Forest'] as List<String>
        'Sorcery'                                                  | null                         | BasicType.Sorcery               | [] as List<String>              | [] as List<String>
        'Enchantment — Aura'                                       | null                         | BasicType.Enchantment           | [] as List<String>              | ['Aura'] as List<String>
        'Artifact — Equipment'                                     | null                         | BasicType.Artifact              | [] as List<String>              | ['Equipment'] as List<String>
        'Artifact'                                                 | null                         | BasicType.Artifact              | [] as List<String>              | [] as List<String>
        'Legendary Creature — Angel'                               | null                         | BasicType.Creature              | ['Legendary'] as List<String>   | ['Angel'] as List<String>
        'Legendary Creature — Angel // Legendary Creature — Angel' | 'Legendary Creature — Angel' | BasicType.TransformableCreature | ['Legendary'] as List<String>   | ['Angel'] as List<String>



    }

    private static CardDTO createCardDTO(String line, String face1Line, String face2Line) {
        CardDTO cardDTO = new CardDTO()
        cardDTO.typeLine = line
        cardDTO.colorIdentity = ['W'] as String[]
        cardDTO.cardFaces = new ArrayList<>()
        if (face1Line != null) {
            def cardFace = new CardFaceDTO()
            cardFace.typeLine = face1Line
            cardFace.colors = ['W'] as String[]
            cardDTO.cardFaces.add(cardFace)
        }
        if (face2Line != null) {
            def cardFace = new CardFaceDTO()
            cardFace.typeLine = face1Line
            cardFace.colors = ['W'] as String[]
            cardDTO.cardFaces.add(cardFace)
        }
        cardDTO
    }
}
