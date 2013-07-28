
package org.openjst.server.commons.maven.manifest.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for resourcesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resourcesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="core" type="{http://openjst.org/xml/ns/web-ui/manifest}coreType" minOccurs="0"/>
 *         &lt;element name="modules" type="{http://openjst.org/xml/ns/web-ui/manifest}modulesType" minOccurs="0"/>
 *         &lt;element name="libraries" type="{http://openjst.org/xml/ns/web-ui/manifest}librariesType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resourcesType", namespace = "http://openjst.org/xml/ns/web-ui/manifest", propOrder = {
    "core",
    "modules",
    "libraries"
})
public class ResourcesType {

    @XmlElement(namespace = "http://openjst.org/xml/ns/web-ui/manifest")
    protected CoreType core;
    @XmlElement(namespace = "http://openjst.org/xml/ns/web-ui/manifest")
    protected ModulesType modules;
    @XmlElement(namespace = "http://openjst.org/xml/ns/web-ui/manifest")
    protected LibrariesType libraries;

    /**
     * Gets the value of the core property.
     * 
     * @return
     *     possible object is
     *     {@link CoreType }
     *     
     */
    public CoreType getCore() {
        return core;
    }

    /**
     * Sets the value of the core property.
     * 
     * @param value
     *     allowed object is
     *     {@link CoreType }
     *     
     */
    public void setCore(CoreType value) {
        this.core = value;
    }

    /**
     * Gets the value of the modules property.
     * 
     * @return
     *     possible object is
     *     {@link ModulesType }
     *     
     */
    public ModulesType getModules() {
        return modules;
    }

    /**
     * Sets the value of the modules property.
     * 
     * @param value
     *     allowed object is
     *     {@link ModulesType }
     *     
     */
    public void setModules(ModulesType value) {
        this.modules = value;
    }

    /**
     * Gets the value of the libraries property.
     * 
     * @return
     *     possible object is
     *     {@link LibrariesType }
     *     
     */
    public LibrariesType getLibraries() {
        return libraries;
    }

    /**
     * Sets the value of the libraries property.
     * 
     * @param value
     *     allowed object is
     *     {@link LibrariesType }
     *     
     */
    public void setLibraries(LibrariesType value) {
        this.libraries = value;
    }

}
