
package org.openjst.server.commons.maven.manifest.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for frameworkGroupType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="frameworkGroupType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://openjst.org/xml/ns/web-ui/manifest}frameworkCoreGroupType">
 *       &lt;sequence>
 *         &lt;element name="namespaces" type="{http://openjst.org/xml/ns/web-ui/manifest}frameworkGroupNamespacesType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://openjst.org/xml/ns/web-ui/manifest}stringWithoutWhiteSpacesType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "frameworkGroupType", namespace = "http://openjst.org/xml/ns/web-ui/manifest", propOrder = {
    "namespaces"
})
public class FrameworkGroupType
    extends FrameworkCoreGroupType
{

    @XmlElement(namespace = "http://openjst.org/xml/ns/web-ui/manifest", required = true)
    protected FrameworkGroupNamespacesType namespaces;
    @XmlAttribute(name = "name", required = true)
    protected String name;

    /**
     * Gets the value of the namespaces property.
     * 
     * @return
     *     possible object is
     *     {@link FrameworkGroupNamespacesType }
     *     
     */
    public FrameworkGroupNamespacesType getNamespaces() {
        return namespaces;
    }

    /**
     * Sets the value of the namespaces property.
     * 
     * @param value
     *     allowed object is
     *     {@link FrameworkGroupNamespacesType }
     *     
     */
    public void setNamespaces(FrameworkGroupNamespacesType value) {
        this.namespaces = value;
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

}
