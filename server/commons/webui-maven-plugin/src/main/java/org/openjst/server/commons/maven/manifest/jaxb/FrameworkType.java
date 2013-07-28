
package org.openjst.server.commons.maven.manifest.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for frameworkType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="frameworkType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="core" type="{http://openjst.org/xml/ns/web-ui/manifest}frameworkCoreGroupType"/>
 *         &lt;element name="groups" type="{http://openjst.org/xml/ns/web-ui/manifest}frameworkGroupsType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="type" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="yui"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "frameworkType", namespace = "http://openjst.org/xml/ns/web-ui/manifest", propOrder = {
    "core",
    "groups"
})
public class FrameworkType {

    @XmlElement(namespace = "http://openjst.org/xml/ns/web-ui/manifest", required = true)
    protected FrameworkCoreGroupType core;
    @XmlElement(namespace = "http://openjst.org/xml/ns/web-ui/manifest")
    protected FrameworkGroupsType groups;
    @XmlAttribute(name = "type", required = true)
    protected String type;

    /**
     * Gets the value of the core property.
     * 
     * @return
     *     possible object is
     *     {@link FrameworkCoreGroupType }
     *     
     */
    public FrameworkCoreGroupType getCore() {
        return core;
    }

    /**
     * Sets the value of the core property.
     * 
     * @param value
     *     allowed object is
     *     {@link FrameworkCoreGroupType }
     *     
     */
    public void setCore(FrameworkCoreGroupType value) {
        this.core = value;
    }

    /**
     * Gets the value of the groups property.
     * 
     * @return
     *     possible object is
     *     {@link FrameworkGroupsType }
     *     
     */
    public FrameworkGroupsType getGroups() {
        return groups;
    }

    /**
     * Sets the value of the groups property.
     * 
     * @param value
     *     allowed object is
     *     {@link FrameworkGroupsType }
     *     
     */
    public void setGroups(FrameworkGroupsType value) {
        this.groups = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

}
