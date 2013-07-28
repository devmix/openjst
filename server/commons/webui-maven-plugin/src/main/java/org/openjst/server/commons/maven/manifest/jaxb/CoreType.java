
package org.openjst.server.commons.maven.manifest.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for coreType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="coreType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="entry" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sources" type="{http://openjst.org/xml/ns/web-ui/manifest}sourcesType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="root" use="required" type="{http://openjst.org/xml/ns/web-ui/manifest}stringWithoutWhiteSpacesType" />
 *       &lt;attribute name="joinAs" type="{http://openjst.org/xml/ns/web-ui/manifest}stringWithoutWhiteSpacesType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "coreType", namespace = "http://openjst.org/xml/ns/web-ui/manifest", propOrder = {
    "entry",
    "sources"
})
public class CoreType {

    @XmlElement(namespace = "http://openjst.org/xml/ns/web-ui/manifest", required = true)
    protected String entry;
    @XmlElement(namespace = "http://openjst.org/xml/ns/web-ui/manifest")
    protected SourcesType sources;
    @XmlAttribute(name = "root", required = true)
    protected String root;
    @XmlAttribute(name = "joinAs")
    protected String joinAs;

    /**
     * Gets the value of the entry property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEntry() {
        return entry;
    }

    /**
     * Sets the value of the entry property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEntry(String value) {
        this.entry = value;
    }

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
