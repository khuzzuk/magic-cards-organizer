package pl.khuzzuk.mtg.organizer.gui.form

import spock.lang.Specification
import spock.lang.Unroll

class BeanReflectionSpec extends Specification {
    private static final int INT_VALUE = 1
    private static final int ABSTRACT_INT_VALUE = 2
    private static final int ABSTRACT_SECOND_LEVEL_INT_VALUE = 3
    private static final long LONG_VALUE = 4L
    private static final String STRING_VALUE = 'string value'

    private TestBean testBean
    private TestElementBean testElementBean

    void setup() {
        testElementBean = new TestElementBean()
        testElementBean.rawLongField = LONG_VALUE
        testBean = new TestBean()
        testBean.rawIntField = INT_VALUE
        testBean.abstractField = ABSTRACT_INT_VALUE
        testBean.abstractSecondLevelField = ABSTRACT_SECOND_LEVEL_INT_VALUE
        testBean.stringField = STRING_VALUE
        testBean.testElementBean = testElementBean
    }

    @Unroll
    def 'check basic field'() {
        when:
        def getter = BeanReflection.propertyGetterFor(path, TestBean.class)
        def result = getter.apply(testBean)

        then:
        result == expected

        where:
        path                           | expected
        'rawIntField'                  | INT_VALUE
        'abstractField'                | ABSTRACT_INT_VALUE
        'abstractSecondLevelField'     | ABSTRACT_SECOND_LEVEL_INT_VALUE
        'stringField'                  | STRING_VALUE
        'testElementBean.rawLongField' | LONG_VALUE
    }

    @Unroll
    def 'type for path tests'() {
        when:
        Class<?> result = BeanReflection.getFieldTypeFor(path, beanType)

        then:
        result == expected

        where:
        path                           | beanType              | expected
        'rawIntField'                  | TestBean.class        | int.class
        'abstractField'                | TestBean.class        | int.class
        'abstractSecondLevelField'     | TestBean.class        | int.class
        'stringField'                  | TestBean.class        | String.class
        'testElementBean.rawLongField' | TestBean.class        | long.class
        'rawLongField'                 | TestElementBean.class | long.class
    }

    @Unroll
    def 'hasGetterForPath tests'() {
        when:
        def result = BeanReflection.hasGetterForPath(path, beanType)

        then:
        result == expected

        where:
        path                           | beanType              | expected
        'rawIntField'                  | TestBean.class        | true
        'abstractField'                | TestBean.class        | true
        'stringField'                  | TestBean.class        | true
        'testElementBean.rawLongField' | TestBean.class        | true
        'rawLongField'                 | TestElementBean.class | true
        'testElementBean.stringField'  | TestBean.class        | false

    }

    static class TestBean extends AbstractTestBean {
        int rawIntField
        String stringField
        TestElementBean testElementBean
    }

    static class AbstractTestBean extends AbstractSecondLevelBean {
        int abstractField
    }

    static class AbstractSecondLevelBean {
        int abstractSecondLevelField
    }

    static class TestElementBean {
        long rawLongField
    }
}
