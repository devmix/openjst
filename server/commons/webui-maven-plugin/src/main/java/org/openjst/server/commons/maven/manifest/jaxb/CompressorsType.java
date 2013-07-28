
package org.openjst.server.commons.maven.manifest.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for compressorsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="compressorsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="compressor" type="{http://openjst.org/xml/ns/web-ui/manifest}compressorType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "compressorsType", namespace = "http://openjst.org/xml/ns/web-ui/manifest", propOrder = {
    "compressor"
})
public class CompressorsType {

    @XmlElement(namespace = "http://openjst.org/xml/ns/web-ui/manifest")
    protected List<CompressorType> compressor;

    /**
     * Gets the value of the compressor property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the compressor property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCompressor().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CompressorType }
     * 
     * 
     */
    public List<CompressorType> getCompressor() {
        if (compressor == null) {
            compressor = new ArrayList<CompressorType>();
        }
        return this.compressor;
    }

}
