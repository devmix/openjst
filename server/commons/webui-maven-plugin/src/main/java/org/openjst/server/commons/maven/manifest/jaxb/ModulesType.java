
package org.openjst.server.commons.maven.manifest.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for modulesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="modulesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sources" type="{http://openjst.org/xml/ns/web-ui/manifest}sourcesWithRootType" minOccurs="0"/>
 *         &lt;element name="styles" type="{http://openjst.org/xml/ns/web-ui/manifest}stylesWithRootType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="namespace" use="required" type="{http://openjst.org/xml/ns/web-ui/manifest}namespaceNameType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "modulesType", namespace = "http://openjst.org/xml/ns/web-ui/manifest", propOrder = {
    "sources",
    "styles"
})
public class ModulesType {

    @XmlElement(namespace = "http://openjst.org/xml/ns/web-ui/manifest")
    protected SourcesWithRootType sources;
    @XmlElement(namespace = "http://openjst.org/xml/ns/web-ui/manifest")
    protected StylesWithRootType styles;
    @XmlAttribute(name = "namespace", required = true)
    protected String namespace;

    /**
     * Gets the value of the sources property.
     * 
     * @return
     *     possible object is
     *     {@link SourcesWithRootType }
     *     
     */
    public SourcesWithRootType getSources() {
        return sources;
    }

    /**
     * Sets the value of the sources property.
     * 
     * @param value
     *     allowed object is
     *     {@link SourcesWithRootType }
     *     
     */
    public void setSources(SourcesWithRootType value) {
        this.sources = value;
    }

    /**
     * Gets the value of the styles property.
     * 
     * @return
     *     possible object is
     *     {@link StylesWithRootType }
     *     
     */
    public StylesWithRootType getStyles() {
        return styles;
    }

    /**
     * Sets the value of the styles property.
     * 
     * @param value
     *     allowed object is
     *     {@link StylesWithRootType }
     *     
     */
    public void setStyles(StylesWithRootType value) {
        this.styles = value;
    }

    /**
     * Gets the value of the namespace property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Sets the value of the namespace property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNamespace(String value) {
        this.namespace = value;
    }

}
