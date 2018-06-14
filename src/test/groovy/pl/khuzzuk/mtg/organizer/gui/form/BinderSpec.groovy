package pl.khuzzuk.mtg.organizer.gui.form

import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import pl.khuzzuk.mtg.organizer.JavaFxSpecification

class BinderSpec extends JavaFxSpecification {
    private Binder binder

    private Form form
    private Bean bean

    private static final String STRING_FIELD_VALUE = 'stringFieldValue'
    private static final URL URL_VALUE = new URL('file::url')
    private static final URL DEFAULT_URL = new URL('file::default')
    private static final int INT_FIELD_VALUE = 1
    private static final int ABSTRACT_INT_FIELD = 2
    private static Image imageFieldValue1
    private static Image imageFieldValue2

    void setup() {
        binder = new Binder()
        binder.load()
        binder.bind(Bean.class, Form.class)

        form = new Form()

        bean = new Bean()
        bean.field1 = STRING_FIELD_VALUE
        bean.field2 = URL_VALUE
        bean.abstractIntField = ABSTRACT_INT_FIELD
        InnerBean innerBean = new InnerBean()
        innerBean.intField = INT_FIELD_VALUE
        bean.innerBean = innerBean

        imageFieldValue1 = new Image('file::image1')
        imageFieldValue2 = new Image('file::image2')
    }

    def 'set form fields from bean'() {
        when:
        binder.fillForm(form, bean)

        then:
        form.field1.text == STRING_FIELD_VALUE
        form.field2.image.url == URL_VALUE.toString()
        form.field3.image == null
        form.intField.text == INT_FIELD_VALUE.toString()
        form.abstractIntField.text == ABSTRACT_INT_FIELD.toString()
        form.abstractIntField.visible
    }

    def 'clear form with default values'() {
        given:
        Form form = new Form()
        form.field1.text = STRING_FIELD_VALUE
        form.field2.image = imageFieldValue1
        form.field3.image = imageFieldValue2
        form.intField.text = INT_FIELD_VALUE.toString()
        form.hiddenCheckLabel.visible = true
        form.noHideLabel.visible = true

        when:
         binder.clearForm(form)

        then:
        form.field1.text == 'default'
        !form.field1.visible
        form.field2.image == null
        form.field2.visible
        form.field3.image.url == DEFAULT_URL.toString()
        !form.field3.visible
        !form.intField.visible
        form.intField.text == '0'
        !form.hiddenCheckLabel.visible
        form.noHideLabel.visible
        form.abstractIntField.text == null
        form.abstractIntField.visible
    }

    def 'field is visible after clear and fill'() {
        given:
        binder.fillForm(form, bean)

        when:
        binder.clearForm(form)

        then:
        form.field1.text == 'default'
        !form.field1.visible
        form.field2.image == null
        form.field2.visible
        form.field3.image.url == DEFAULT_URL.toString()
        !form.field3.visible
        !form.intField.visible
        form.intField.text == '0'
        !form.hiddenCheckLabel.visible
        form.noHideLabel.visible
        form.abstractIntField.text == null
        form.abstractIntField.visible

        and:
        binder.fillForm(form, bean)

        then:
        form.field1.text == STRING_FIELD_VALUE
        form.field1.visible
        form.field2.image.url == URL_VALUE.toString()
        form.field2.visible
        form.field3.image.url == DEFAULT_URL.toString()
        !form.field3.visible
        form.intField.text == INT_FIELD_VALUE.toString()
        form.intField.visible
        form.noHideLabel.visible
        form.abstractIntField.text == ABSTRACT_INT_FIELD.toString()
        form.abstractIntField.visible
    }

    static class Form extends Node {
        @FormProperty(defaultValue = 'default', hideAfterClear = true)
        private Label field1 = new Label()
        @FormProperty(hideAfterClear = false)
        private ImageView field2 = new ImageView()
        @FormProperty(defaultValue = 'file::default', hideAfterClear = true)
        private ImageView field3 = new ImageView()
        @FormProperty(defaultValue = '0', beanPath = 'innerBean.intField', hideAfterClear = true)
        private Label intField = new Label()
        @HideCheck('intField')
        private Label hiddenCheckLabel = new Label()
        @HideCheck('field2')
        private Label noHideLabel = new Label()
        @FormProperty
        private Label abstractIntField = new Label()
    }

    static class Bean extends AbstractBean {
        String field1
        URL field2
        URL field3
        InnerBean innerBean
    }

    static class AbstractBean {
        int abstractIntField
    }

    static class InnerBean {
        int intField
    }
}
