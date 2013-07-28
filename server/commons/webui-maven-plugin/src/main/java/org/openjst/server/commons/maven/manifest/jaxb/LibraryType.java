
package org.openjst.server.commons.maven.manifest.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for libraryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="libraryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sources" type="{http://openjst.org/xml/ns/web-ui/manifest}sourcesType" minOccurs="0"/>
 *         &lt;element name="styles" type="{http://openjst.org/xml/ns/web-ui/manifest}stylesType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="root" use="required" type="{http://openjst.org/xml/ns/web-ui/manifest}stringWithoutWhiteSpacesType" />
 *       &lt;attribute name="name" use="required" type="{http://openjst.org/xml/ns/web-ui/manifest}namespaceNameType" />
 *       &lt;attribute name="skipValidate" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="joinAs" use="required" type="{http://openjst.org/xml/ns/web-ui/manifest}stringWithoutWhiteSpacesType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "libraryType", namespace = "http://openjst.org/xml/ns/web-ui/manifest", propOrder = {
    "sources",
    "styles"
})
public class LibraryType {

    @XmlElement(namespace = "http://openjst.org/xml/ns/web-ui/manifest")
    protected SourcesType sources;
    @XmlElement(namespace = "http://openjst.org/xml/ns/web-ui/manifest")
    protected StylesType styles;
    @XmlAttribute(name = "root", required = true)
    protected String root;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "skipValidate")
    protected Boolean skipValidate;
    @XmlAttribute(name = "joinAs", required = true)
    protected String joinAs;

    /**
     * Gets the value of the sources property.
     * 
     * @return
     *     possible object is
     *     {@link SourcesType }
     *     
     */
    public SourcesType getSources() {
        return sources;
    }

    /**
     * Sets the value of the sources property.
     * 
     * @param value
     *     allowed object is
     *     {@link SourcesType }
     *     
     */
    public void setSources(SourcesType value) {
        this.sources = value;
    }

    /**
     * Gets the value of the styles property.
     * 
     * @return
     *     possible object is
     *     {@link StylesType }
     *     
     */
    public StylesType getStyles() {
        return styles;
    }

    /**
     * Sets the value of the styles property.
     * 
     * @param value
     *     allowed object is
     *     {@link StylesType }
     *     
     */
    public void setStyles(StylesType value) {
        this.styles = value;
    }

    /**
     * Gets the value of the root property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRoot() {
        return root;
    }

    /**
     * Sets the value of the root property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoot(String value) {
        this.root = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the skipValidate property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isSkipValidate() {
        if (skipValidate == null) {
            return true;
        } else {
            return skipValidate;
        }
    }

    /**
     * Sets the value of the skipValidate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSkipValidate(Boolean value) {
        this.skipValidate = value;
    }

    /**
     * Gets the value of the joinAs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJoinAs() {
        return joinAs;
    }

    /**
     * Sets the value of the joinAs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJoinAs(String value) {
        this.joinAs = value;
    }

}
