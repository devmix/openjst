
package org.openjst.server.commons.maven.manifest.jaxb;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openjst.server.commons.maven.manifest.jaxb package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Manifest_QNAME = new QName("http://openjst.org/xml/ns/web-ui/manifest", "manifest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openjst.server.commons.maven.manifest.jaxb
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ManifestType }
     * 
     */
    public ManifestType createManifestType() {
        return new ManifestType();
    }

    /**
     * Create an instance of {@link ParametersType }
     * 
     */
    public ParametersType createParametersType() {
        return new ParametersType();
    }

    /**
     * Create an instance of {@link FrameworkGroupNamespacesType }
     * 
     */
    public FrameworkGroupNamespacesType createFrameworkGroupNamespacesType() {
        return new FrameworkGroupNamespacesType();
    }

    /**
     * Create an instance of {@link SourcesType }
     * 
     */
    public SourcesType createSourcesType() {
        return new SourcesType();
    }

    /**
     * Create an instance of {@link LibrariesType }
     * 
     */
    public LibrariesType createLibrariesType() {
        return new LibrariesType();
    }

    /**
     * Create an instance of {@link FrameworkType }
     * 
     */
    public FrameworkType createFrameworkType() {
        return new FrameworkType();
    }

    /**
     * Create an instance of {@link IncludesType }
     * 
     */
    public IncludesType createIncludesType() {
        return new IncludesType();
    }

    /**
     * Create an instance of {@link CompressorsType }
     * 
     */
    public CompressorsType createCompressorsType() {
        return new CompressorsType();
    }

    /**
     * Create an instance of {@link FrameworkGroupsType }
     * 
     */
    public FrameworkGroupsType createFrameworkGroupsType() {
        return new FrameworkGroupsType();
    }

    /**
     * Create an instance of {@link ModulesType }
     * 
     */
    public ModulesType createModulesType() {
        return new ModulesType();
    }

    /**
     * Create an instance of {@link ExcludesType }
     * 
     */
    public ExcludesType createExcludesType() {
        return new ExcludesType();
    }

    /**
     * Create an instance of {@link OptionType }
     * 
     */
    public OptionType createOptionType() {
        return new OptionType();
    }

    /**
     * Create an instance of {@link FrameworkGroupType }
     * 
     */
    public FrameworkGroupType createFrameworkGroupType() {
        return new FrameworkGroupType();
    }

    /**
     * Create an instance of {@link CompressorType }
     * 
     */
    public CompressorType createCompressorType() {
        return new CompressorType();
    }

    /**
     * Create an instance of {@link ResourcesType }
     * 
     */
    public ResourcesType createResourcesType() {
        return new ResourcesType();
    }

    /**
     * Create an instance of {@link ValidatorsType }
     * 
     */
    public ValidatorsType createValidatorsType() {
        return new ValidatorsType();
    }

    /**
     * Create an instance of {@link LibraryType }
     * 
     */
    public LibraryType createLibraryType() {
        return new LibraryType();
    }

    /**
     * Create an instance of {@link StylesWithRootType }
     * 
     */
    public StylesWithRootType createStylesWithRootType() {
        return new StylesWithRootType();
    }

    /**
     * Create an instance of {@link ValidatorType }
     * 
     */
    public ValidatorType createValidatorType() {
        return new ValidatorType();
    }

    /**
     * Create an instance of {@link OptionsType }
     * 
     */
    public OptionsType createOptionsType() {
        return new OptionsType();
    }

    /**
     * Create an instance of {@link SourcesWithRootType }
     * 
     */
    public SourcesWithRootType createSourcesWithRootType() {
        return new SourcesWithRootType();
    }

    /**
     * Create an instance of {@link StylesType }
     * 
     */
    public StylesType createStylesType() {
        return new StylesType();
    }

    /**
     * Create an instance of {@link FrameworkCoreGroupType }
     * 
     */
    public FrameworkCoreGroupType createFrameworkCoreGroupType() {
        return new FrameworkCoreGroupType();
    }

    /**
     * Create an instance of {@link CoreType }
     * 
     */
    public CoreType createCoreType() {
        return new CoreType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ManifestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://openjst.org/xml/ns/web-ui/manifest", name = "manifest")
    public JAXBElement<ManifestType> createManifest(ManifestType value) {
        return new JAXBElement<ManifestType>(_Manifest_QNAME, ManifestType.class, null, value);
    }

}
