
package org.openjst.server.commons.maven.manifest.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for stylesWithRootType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="stylesWithRootType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://openjst.org/xml/ns/web-ui/manifest}stylesType">
 *       &lt;attribute name="root" use="required" type="{http://openjst.org/xml/ns/web-ui/manifest}stringWithoutWhiteSpacesType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "stylesWithRootType", namespace = "http://openjst.org/xml/ns/web-ui/manifest")
public class StylesWithRootType
    extends StylesType
{

    @XmlAttribute(name = "root", required = true)
    protected String root;

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

}
