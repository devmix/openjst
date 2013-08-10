package org.openjst.server.commons.maven.manifest.jaxb;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for compilerType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="compilerType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="options" type="{http://openjst.org/xml/ns/web-ui/manifest}optionsType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="type" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="less"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="skip" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "compilerType", namespace = "http://openjst.org/xml/ns/web-ui/manifest", propOrder = {
        "options"
})
public class CompilerType {

    @XmlElement(namespace = "http://openjst.org/xml/ns/web-ui/manifest")
    protected OptionsType options;
    @XmlAttribute(name = "type", required = true)
    protected String type;
    @XmlAttribute(name = "skip")
    protected Boolean skip;

    /**
     * Gets the value of the options property.
     *
     * @return possible object is
     *         {@link OptionsType }
     */
    public OptionsType getOptions() {
        return options;
    }

    /**
     * Sets the value of the options property.
     *
     * @param value allowed object is
     *              {@link OptionsType }
     */
    public void setOptions(OptionsType value) {
        this.options = value;
    }

    /**
     * Gets the value of the type property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the skip property.
     *
     * @return possible object is
     *         {@link Boolean }
     */
    public boolean isSkip() {
        if (skip == null) {
            return false;
        } else {
            return skip;
        }
    }

    /**
     * Sets the value of the skip property.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setSkip(Boolean value) {
        this.skip = value;
    }

}
