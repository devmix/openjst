package org.openjst.server.commons.maven.manifest.jaxb;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for manifestType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="manifestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="parameters" type="{http://openjst.org/xml/ns/web-ui/manifest}parametersType"/>
 *         &lt;element name="compilers" type="{http://openjst.org/xml/ns/web-ui/manifest}compilersType" minOccurs="0"/>
 *         &lt;element name="compressors" type="{http://openjst.org/xml/ns/web-ui/manifest}compressorsType" minOccurs="0"/>
 *         &lt;element name="validators" type="{http://openjst.org/xml/ns/web-ui/manifest}validatorsType" minOccurs="0"/>
 *         &lt;element name="framework" type="{http://openjst.org/xml/ns/web-ui/manifest}frameworkType" minOccurs="0"/>
 *         &lt;element name="resources" type="{http://openjst.org/xml/ns/web-ui/manifest}resourcesType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="version" use="required" type="{http://openjst.org/xml/ns/web-ui/manifest}versionType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "manifestType", namespace = "http://openjst.org/xml/ns/web-ui/manifest", propOrder = {
        "parameters",
        "compilers",
        "compressors",
        "validators",
        "framework",
        "resources"
})
public class ManifestType {

    @XmlElement(namespace = "http://openjst.org/xml/ns/web-ui/manifest", required = true)
    protected ParametersType parameters;
    @XmlElement(namespace = "http://openjst.org/xml/ns/web-ui/manifest")
    protected CompilersType compilers;
    @XmlElement(namespace = "http://openjst.org/xml/ns/web-ui/manifest")
    protected CompressorsType compressors;
    @XmlElement(namespace = "http://openjst.org/xml/ns/web-ui/manifest")
    protected ValidatorsType validators;
    @XmlElement(namespace = "http://openjst.org/xml/ns/web-ui/manifest")
    protected FrameworkType framework;
    @XmlElement(namespace = "http://openjst.org/xml/ns/web-ui/manifest", required = true)
    protected ResourcesType resources;
    @XmlAttribute(name = "version", required = true)
    protected String version;

    /**
     * Gets the value of the parameters property.
     *
     * @return possible object is
     *         {@link ParametersType }
     */
    public ParametersType getParameters() {
        return parameters;
    }

    /**
     * Sets the value of the parameters property.
     *
     * @param value allowed object is
     *              {@link ParametersType }
     */
    public void setParameters(ParametersType value) {
        this.parameters = value;
    }

    /**
     * Gets the value of the compilers property.
     *
     * @return possible object is
     *         {@link CompilersType }
     */
    public CompilersType getCompilers() {
        return compilers;
    }

    /**
     * Sets the value of the compilers property.
     *
     * @param value allowed object is
     *              {@link CompilersType }
     */
    public void setCompilers(CompilersType value) {
        this.compilers = value;
    }

    /**
     * Gets the value of the compressors property.
     *
     * @return possible object is
     *         {@link CompressorsType }
     */
    public CompressorsType getCompressors() {
        return compressors;
    }

    /**
     * Sets the value of the compressors property.
     *
     * @param value allowed object is
     *              {@link CompressorsType }
     */
    public void setCompressors(CompressorsType value) {
        this.compressors = value;
    }

    /**
     * Gets the value of the validators property.
     *
     * @return possible object is
     *         {@link ValidatorsType }
     */
    public ValidatorsType getValidators() {
        return validators;
    }

    /**
     * Sets the value of the validators property.
     *
     * @param value allowed object is
     *              {@link ValidatorsType }
     */
    public void setValidators(ValidatorsType value) {
        this.validators = value;
    }

    /**
     * Gets the value of the framework property.
     *
     * @return possible object is
     *         {@link FrameworkType }
     */
    public FrameworkType getFramework() {
        return framework;
    }

    /**
     * Sets the value of the framework property.
     *
     * @param value allowed object is
     *              {@link FrameworkType }
     */
    public void setFramework(FrameworkType value) {
        this.framework = value;
    }

    /**
     * Gets the value of the resources property.
     *
     * @return possible object is
     *         {@link ResourcesType }
     */
    public ResourcesType getResources() {
        return resources;
    }

    /**
     * Sets the value of the resources property.
     *
     * @param value allowed object is
     *              {@link ResourcesType }
     */
    public void setResources(ResourcesType value) {
        this.resources = value;
    }

    /**
     * Gets the value of the version property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setVersion(String value) {
        this.version = value;
    }

}
