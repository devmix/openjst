
package org.openjst.server.commons.maven.manifest.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for librariesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="librariesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="library" type="{http://openjst.org/xml/ns/web-ui/manifest}libraryType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="root" use="required" type="{http://openjst.org/xml/ns/web-ui/manifest}stringWithoutWhiteSpacesType" />
 *       &lt;attribute name="namespace" use="required" type="{http://openjst.org/xml/ns/web-ui/manifest}namespaceNameType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "librariesType", namespace = "http://openjst.org/xml/ns/web-ui/manifest", propOrder = {
    "library"
})
public class LibrariesType {

    @XmlElement(namespace = "http://openjst.org/xml/ns/web-ui/manifest", required = true)
    protected List<LibraryType> library;
    @XmlAttribute(name = "root", required = true)
    protected String root;
    @XmlAttribute(name = "namespace", required = true)
    protected String namespace;

    /**
     * Gets the value of the library property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the library property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLibrary().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LibraryType }
     * 
     * 
     */
    public List<LibraryType> getLibrary() {
        if (library == null) {
            library = new ArrayList<LibraryType>();
        }
        return this.library;
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
