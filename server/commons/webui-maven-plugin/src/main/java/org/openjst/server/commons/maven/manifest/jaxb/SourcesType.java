
package org.openjst.server.commons.maven.manifest.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for sourcesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="sourcesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="includes" type="{http://openjst.org/xml/ns/web-ui/manifest}includesType" minOccurs="0"/>
 *         &lt;element name="excludes" type="{http://openjst.org/xml/ns/web-ui/manifest}excludesType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sourcesType", namespace = "http://openjst.org/xml/ns/web-ui/manifest", propOrder = {
    "includes",
    "excludes"
})
@XmlSeeAlso({
    SourcesWithRootType.class
})
public class SourcesType {

    @XmlElement(namespace = "http://openjst.org/xml/ns/web-ui/manifest")
    protected IncludesType includes;
    @XmlElement(namespace = "http://openjst.org/xml/ns/web-ui/manifest")
    protected ExcludesType excludes;

    /**
     * Gets the value of the includes property.
     * 
     * @return
     *     possible object is
     *     {@link IncludesType }
     *     
     */
    public IncludesType getIncludes() {
        return includes;
    }

    /**
     * Sets the value of the includes property.
     * 
     * @param value
     *     allowed object is
     *     {@link IncludesType }
     *     
     */
    public void setIncludes(IncludesType value) {
        this.includes = value;
    }

    /**
     * Gets the value of the excludes property.
     * 
     * @return
     *     possible object is
     *     {@link ExcludesType }
     *     
     */
    public ExcludesType getExcludes() {
        return excludes;
    }

    /**
     * Sets the value of the excludes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExcludesType }
     *     
     */
    public void setExcludes(ExcludesType value) {
        this.excludes = value;
    }

}
