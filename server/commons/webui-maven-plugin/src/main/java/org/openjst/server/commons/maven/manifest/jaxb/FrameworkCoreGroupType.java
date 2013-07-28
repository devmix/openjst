
package org.openjst.server.commons.maven.manifest.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for frameworkCoreGroupType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="frameworkCoreGroupType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="debug" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="combine" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="comboServiceUrl" use="required" type="{http://openjst.org/xml/ns/web-ui/manifest}stringWithoutWhiteSpacesType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "frameworkCoreGroupType", namespace = "http://openjst.org/xml/ns/web-ui/manifest")
@XmlSeeAlso({
    FrameworkGroupType.class
})
public class FrameworkCoreGroupType {

    @XmlAttribute(name = "debug")
    protected Boolean debug;
    @XmlAttribute(name = "combine")
    protected Boolean combine;
    @XmlAttribute(name = "comboServiceUrl", required = true)
    protected String comboServiceUrl;

    /**
     * Gets the value of the debug property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isDebug() {
        if (debug == null) {
            return false;
        } else {
            return debug;
        }
    }

    /**
     * Sets the value of the debug property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDebug(Boolean value) {
        this.debug = value;
    }

    /**
     * Gets the value of the combine property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isCombine() {
        if (combine == null) {
            return true;
        } else {
            return combine;
        }
    }

    /**
     * Sets the value of the combine property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCombine(Boolean value) {
        this.combine = value;
    }

    /**
     * Gets the value of the comboServiceUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComboServiceUrl() {
        return comboServiceUrl;
    }

    /**
     * Sets the value of the comboServiceUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComboServiceUrl(String value) {
        this.comboServiceUrl = value;
    }

}
